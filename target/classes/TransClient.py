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

    nowtraffic = [0.1, 0.2, 0.3, 0.4]
    time = 0.15
    data = NowIntervalTrafficData(timeOfHour=time, nowIntervalTraffic=nowtraffic)

    predictedData = client.getPredictedData(nowIntervalTrafficData=data)
    print(predictedData.migration)

except Thrift.TException as ex:
    print("%s" % (ex.message))