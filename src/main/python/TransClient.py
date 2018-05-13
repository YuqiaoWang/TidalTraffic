import sys
import thrift
sys.path.append('gen-py')

from TrafficDataTrans import TrafficDescription.TrafficDataService
from TrafficDataTrans.ttypes import *

from thrift import Thrift
from thrift.transport import TSocket
from thrift.transport import TTransport
from thrift.protocol import TBinaryProtocol

try:
    transport = TSocket.TSocket('localhost', 9095)
    transport = TTransport.TBufferedTransport(transport)
    protocol = TBinaryProtocol.TBinaryProtocol(transport)
    client = TrafficDescription.TrafficDataService.Client(protocol)
    transport.open()

    #nowtraffic = [0.1, 0.2, 0.3, 0.4]
    #time = 0.15
    time = 0.0
    #nowtraffic = [0.481, 0.497, 0.493, 0.504, 0.518, 0.535, 0.527, 0.481, 0.475, 0.491, 0.493, 0.495, 0.510, 0.508, 0.525, 0.522, 0.512, 0.518, 0.522, 0.525, 0.508, 0.512, 0.456, 0.447, 0.443, 0.443, 0.462, 0.477, 0.472, 0.485]
    nowtraffic = [0, 0.004166667, 0.008333333, 0.0125, 0.029166667, 0.03125, 0.04375, 0.060416667, 0.06875, 0.06875, 0.079166667, 0.079166667, 0.079166667, 0.0875,	0.1, 0.104166667, 0.120833333, 0.141666667, 0.147916667, 0.15, 0.175, 0.183333333, 0.233333333, 0.233333333, 0.235416667, 0.245833333, 0.25, 0.304166667, 0.310416667, 0.314583333]
    data = TrafficDescription.NowIntervalTrafficData(areaId ="1", timeOfHour=time, nowIntervalTraffic=nowtraffic)

    predictedData = client.getPredictedData(nowIntervalTrafficData=data)
    print(predictedData.migration)

except Thrift.TException as ex:
    print("%s" % (ex.message))