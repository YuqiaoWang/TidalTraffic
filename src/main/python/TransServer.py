import thrift
import numpy as np
import tensorflow as tf
import datetime
import socket
import platform
import json
import os
from TrafficDataTrans import TrafficDataService
from TrafficDataTrans.ttypes import *
import xlwt
import edge_model_restore as emr
import model_restore as mr
from thrift.server import TServer
from thrift.protocol import TBinaryProtocol
from thrift.transport import TTransport
from thrift.transport import TSocket
from thrift import Thrift
import sys
import importlib
sys.path.append('gen-py')
importlib.reload(sys)


class TrafficDataServiceHandler:
    def __init__(self, model_areas, model_edge):
        predictedArea1Data = []
        predictedArea3Data = []
        self.PredictedAreaTrafficData = []
        self.PredictedAreaTrafficData.append(predictedArea1Data)
        self.PredictedAreaTrafficData.append(predictedArea3Data)
        self.model_areas = model_areas
        self.model_edge = model_edge
        self.workbook_area = xlwt.Workbook()
        self.sheet_area1 = self.workbook_area.add_sheet('area1')
        self.sheet_area3 = self.workbook_area.add_sheet('area3')
        self.row_1 = 0
        self.row_3 = 0

    def ping(self):
        print('ping()')

    def getPredictedData(self, nowAreaTrafficData):
        print('收到area预测请求')
        #listTraffic = [0.3, 0.4, 0.5, 0.6, 0.5, 0.4, 0.5, 0.1, 0.2, 0.3, 0.3, 0.4, 0.5, 0.3, 0.2]
        #migration = 0.6
        areaId = nowAreaTrafficData.areaId
        print('area' + areaId)
        time = nowAreaTrafficData.timeOfHour
        now_traffic = nowAreaTrafficData.nowAreaTraffic
        input_data = []
        input_data.append(time)
        for i in range(len(now_traffic)):
            input_data.append(now_traffic[i])
        # TODO : 将来要针对不同area调用不同的模型
        if areaId is "1":
            model_param = self.model_areas[0]
        if areaId is "3":
            model_param = self.model_areas[1]
        # TODO: 将来mr.predict()方法入参0改为model_param_area1
        predicted_data = mr.predict(model_param, input_data)
        out_data = predicted_data[0]
        # print(out_data)
        # print(type(out_data[0]))
        migration = out_data[0]

        listTraffic = predicted_data[1:]
        '''
        if areaId is '1':
            row = self.sheet_area1.row(self.row_1)
            data_lenth = len(listTraffic)
            for i in range(0, data_lenth):
                row.write(i, listTraffic[i])
            self.row_1 += 1
        if areaId is '3':
            row = self.sheet_area1.row(self.row_3)
            data_lenth = len(listTraffic)
            for i in range(0, data_lenth):
                row.write(i, listTraffic[i])
            self.row_3 += 1
        '''
        #self.workbook_area.save('data/100erlang/area_predict_data.xls')
        #data = PredictedIntervalTrafficData(migration=migration, predictedIntervalTraffic=listTraffic)
        print('到此为止正常,listTraffic如下：')
        print(listTraffic)
        data = PredictedAreaTrafficData(
            migration=migration, predictedAreaTraffic=listTraffic)
        return data

    def getEdgePredictedData(self, nowEdgeTrafficData):
        print('收到link预测请求')
        nodeSequence = nowEdgeTrafficData.nodeSequence
        time = nowEdgeTrafficData.timeOfHour
        now_edge_traffic = nowEdgeTrafficData.nowEdgeTraffic
        input_data = []
        for i in range(0, len(nodeSequence)):
            input_data.append(nodeSequence[i])
        input_data.append(time)
        for i in range(0, len(now_edge_traffic)):
            input_data.append(now_edge_traffic[i])
        predicted_edge_data = emr.predict(self.model_edge, input_data)
        #out_data = predicted_edge_data[0]
        listTraffic = predicted_edge_data
        listTraffic = listTraffic[0]
        #data = TrafficDescription.PredictedEdgeTrafficData(predictedEdgeTraffic=listTraffic)
        return listTraffic


# 0. 获取系统和配置信息
now_time = datetime.datetime.now()
date_form = now_time.strftime('%Y%m%d')
system_name = platform.system()
# 模型配置的json文件（model_train路径下）
json_file_path = os.path.abspath(os.path.join(sys.path[0], 'model_train'))
json_file_name = os.path.abspath(
    os.path.join(json_file_path, 'model_config.json'))
json_file = open(json_file_name, encoding='utf-8')
config_content = json.load(json_file)
# 用与区别模型是area还是link
model_type = config_content['model']['type']
area_name = config_content['model']['area_name']                # 区域名称
service_ratio = config_content['model']['service_ratio']        # 业务量
input_num = config_content['param']['input']                    # 输入层神经元个数
output_num = config_content['param']['output']                  # 输出层神经元个数
hidden_layer = config_content['param']['hidden']['layer']       # 隐藏层层数
hidden_neurons = config_content['param']['hidden']['neuron']    # 隐藏层各层神经元个数
step = config_content['param']['step']  # 训练迭代次数


# 1.重建模型
# TODO:针对不同area要加载不同的模型（TODO：需要对每个area数据进行训练）
model_param_area1 = mr.parameter(areaId="1", hidden_layer=hidden_layer,
                                 hidden_neuron=hidden_neurons, service_ratio=service_ratio)
#model_param_area3 = mr.parameter("3", hidden_layer)
model_area1 = model_param_area1.restore()
#model_area3 = model_param_area3.restore()
models = []
models.append(model_area1)
# models.append(model_area3)
# 应该是个list
# models = []
# models.append(model_param_area1)
# TODO： Handler构造器入参将来应该改为models
print('重建area模型成功')

edge_json_name = os.path.abspath(
    os.path.join(json_file_path, 'edge_model_config.json'))
edge_json_file = open(edge_json_name, encoding='utf-8')
edge_config_content = json.load(edge_json_file)
#model_type = config_content['model']['type']
#service_ratio = config_content['model']['service_ratio']        # 业务量
#edge = config_content['edge']                                   # 边集
#input_num = config_content['param']['input']                    # 输入层神经元个数
#output_num = config_content['param']['output']                  # 输出层神经元个数
edge_hidden_layer = config_content['param']['hidden']['layer']       # 隐藏层层数
edge_hidden_neurons = config_content['param']['hidden']['neuron']    # 隐藏层各层神经元个数
#step = config_content['param']['step']                          # 训练迭代次数

edge_model_param = emr.edge_parameter(hidden_layer=edge_hidden_layer, hidden_neuron=edge_hidden_neurons, service_ratio = service_ratio)
model_edge = edge_model_param.restore()
print('重建edge模型成功')

# 2.创建服务端
handler = TrafficDataServiceHandler(models, model_edge)
processor = TrafficDataService.Processor(handler)
# 2.1监听端口
transport = TSocket.TServerSocket(port=9095)
# 2.2选择传输层
tfactory = TTransport.TBufferedTransportFactory()
# 2.3选择传输协议
pfactory = TBinaryProtocol.TBinaryProtocolFactory()
# 2.4创建服务端
server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)
print('创建服务端完成')
print('Starting the server......')


server.serve()
print('done')
