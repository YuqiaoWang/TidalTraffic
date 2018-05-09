import sys
import numpy as np
#import tensorflow as tf
import thrift
sys.path.append('gen-py')

import socket

from TrafficDataTrans import TrafficDataService
from TrafficDataTrans.ttypes import *

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol
from thrift.server import TServer

import model_restore as mr

class TrafficDataServiceHandler:
    def __init__(self, model_param):
        self.PredictedIntervalTrafficData = []
        self.model_parameter = model_param

    def ping(self):
        print('ping()')
    
    def getPredictedData(self, nowIntervalTrafficData):
        print('收到来自client的请求')
        #listTraffic = [0.3, 0.4, 0.5, 0.6, 0.5, 0.4, 0.5, 0.1, 0.2, 0.3, 0.3, 0.4, 0.5, 0.3, 0.2]
        #migration = 0.6
        areaId = nowIntervalTrafficData.areaId
        time = nowIntervalTrafficData.timeOfHour
        now_traffic = nowIntervalTrafficData.nowIntervalTraffic
        input_data = []
        input_data.append(time)
        for i in range(0, len(now_traffic)):
            input_data.append(now_traffic[i])
        # TODO : 将来要针对不同area调用不同的模型
        # model_param_area1 = self.model_param[0]
        # TODO: 将来mr.predict()方法入参0改为model_param_area1
        predicted_data = mr.predict(self.model_parameter, input_data)
        out_data = predicted_data[0]
        #print(out_data)
        #print(type(out_data[0]))
        migration = out_data[0]

        listTraffic = predicted_data[1:]
        data = PredictedIntervalTrafficData(migration=migration, predictedIntervalTraffic=listTraffic)
        return data


#1.重建模型
# TODO:针对不用area要加载不同的模型（TOTODO：需要对每个area数据进行训练）
model_param_area1 = mr.restore()
# 应该是个list
# models = []
# models.append(model_param_area1)
# TODO： Handler构造器入参将来应该改为models
print('重建模型成功')

#2.创建服务端
handler = TrafficDataServiceHandler(model_param_area1)
processor = TrafficDataService.Processor(handler)
#2.1监听端口
transport = TSocket.TServerSocket(port=9095)
#2.2选择传输层
tfactory = TTransport.TBufferedTransportFactory()
#2.3选择传输协议
pfactory = TBinaryProtocol.TBinaryProtocolFactory()
#2.4创建服务端
server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)
print('创建服务端完成')
print('Starting the server......')


server.serve()
print('done')
    
        
