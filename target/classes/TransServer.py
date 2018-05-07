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

class TrafficDataServiceHandler:
    def __init__(self):
        self.PredictedIntervalTrafficData = []

    def ping(self):
        print('ping()')
    
    def getPredictedData(self, nowIntervalTrafficData):
        print('收到来自client的请求')
        listTraffic = [0.3, 0.4, 0.5, 0.6, 0.5, 0.4, 0.5, 0.1, 0.2, 0.3, 0.3, 0.4, 0.5, 0.3, 0.2]
        migration = 0.6
        data = PredictedIntervalTrafficData(migration=migration, predictedIntervalTraffic=listTraffic)
        return data

    
#创建服务端
handler = TrafficDataServiceHandler()
processor = TrafficDataService.Processor(handler)
#监听端口
transport = TSocket.TServerSocket(port=9095)
#选择传输层
tfactory = TTransport.TBufferedTransportFactory()
#选择传输协议
pfactory = TBinaryProtocol.TBinaryProtocolFactory()
#创建服务端
server = TServer.TSimpleServer(processor, transport, tfactory, pfactory)
print('Starting the server......')
server.serve()
print('done')
    
        
