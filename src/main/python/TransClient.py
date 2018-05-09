import sys
import thrift
sys.path.append('gen-py')

from TrafficDataTrans import TrafficDataService
from TrafficDataTrans.ttypes import *

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol

try:
    transport = TSocket.TSocket('localhost', 9095)
    transport = TTransport.TBufferedTransport(transport)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    client = TrafficDataService.Client(protocol)
    transport.open()

    #nowtraffic = [0.1, 0.2, 0.3, 0.4]
    #time = 0.15
    time = 0.416
    nowtraffic = [0.481, 0.497, 0.493, 0.504, 0.518, 0.535, 0.527, 0.481, 0.475, 0.491, 0.493, 0.495, 0.510, 0.508, 0.525, 0.522, 0.512, 0.518, 0.522, 0.525, 0.508, 0.512, 0.456, 0.447, 0.443, 0.443, 0.462, 0.477, 0.472, 0.485]
    data = NowIntervalTrafficData(timeOfHour=time, nowIntervalTraffic=nowtraffic)

    predictedData = client.getPredictedData(nowIntervalTrafficData=data)
    print(predictedData.migration)

except Thrift.TException as ex:
    print("%s" % (ex.message))