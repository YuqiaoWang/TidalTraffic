#
# Autogenerated by Thrift Compiler (0.10.0)
#
# DO NOT EDIT UNLESS YOU ARE SURE THAT YOU KNOW WHAT YOU ARE DOING
#
#  options string: py
#

from thrift.Thrift import TType, TMessageType, TFrozenDict, TException, TApplicationException
from thrift.protocol.TProtocol import TProtocolException
import sys

from thrift.transport import TTransport


class NowIntervalTrafficData(object):
    """
    Attributes:
     - timeOfHour
     - nowIntervalTraffic
    """

    thrift_spec = (
        None,  # 0
        (1, TType.DOUBLE, 'timeOfHour', None, None, ),  # 1
        (2, TType.LIST, 'nowIntervalTraffic', (TType.DOUBLE, None, False), None, ),  # 2
    )

    def __init__(self, timeOfHour=None, nowIntervalTraffic=None,):
        self.timeOfHour = timeOfHour
        self.nowIntervalTraffic = nowIntervalTraffic

    def read(self, iprot):
        if iprot._fast_decode is not None and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None:
            iprot._fast_decode(self, iprot, (self.__class__, self.thrift_spec))
            return
        iprot.readStructBegin()
        while True:
            (fname, ftype, fid) = iprot.readFieldBegin()
            if ftype == TType.STOP:
                break
            if fid == 1:
                if ftype == TType.DOUBLE:
                    self.timeOfHour = iprot.readDouble()
                else:
                    iprot.skip(ftype)
            elif fid == 2:
                if ftype == TType.LIST:
                    self.nowIntervalTraffic = []
                    (_etype3, _size0) = iprot.readListBegin()
                    for _i4 in range(_size0):
                        _elem5 = iprot.readDouble()
                        self.nowIntervalTraffic.append(_elem5)
                    iprot.readListEnd()
                else:
                    iprot.skip(ftype)
            else:
                iprot.skip(ftype)
            iprot.readFieldEnd()
        iprot.readStructEnd()

    def write(self, oprot):
        if oprot._fast_encode is not None and self.thrift_spec is not None:
            oprot.trans.write(oprot._fast_encode(self, (self.__class__, self.thrift_spec)))
            return
        oprot.writeStructBegin('NowIntervalTrafficData')
        if self.timeOfHour is not None:
            oprot.writeFieldBegin('timeOfHour', TType.DOUBLE, 1)
            oprot.writeDouble(self.timeOfHour)
            oprot.writeFieldEnd()
        if self.nowIntervalTraffic is not None:
            oprot.writeFieldBegin('nowIntervalTraffic', TType.LIST, 2)
            oprot.writeListBegin(TType.DOUBLE, len(self.nowIntervalTraffic))
            for iter6 in self.nowIntervalTraffic:
                oprot.writeDouble(iter6)
            oprot.writeListEnd()
            oprot.writeFieldEnd()
        oprot.writeFieldStop()
        oprot.writeStructEnd()

    def validate(self):
        return

    def __repr__(self):
        L = ['%s=%r' % (key, value)
             for key, value in self.__dict__.items()]
        return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

    def __eq__(self, other):
        return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

    def __ne__(self, other):
        return not (self == other)


class PredictedIntervalTrafficData(object):
    """
    Attributes:
     - migration
     - predictedIntervalTraffic
    """

    thrift_spec = (
        None,  # 0
        (1, TType.DOUBLE, 'migration', None, None, ),  # 1
        (2, TType.LIST, 'predictedIntervalTraffic', (TType.DOUBLE, None, False), None, ),  # 2
    )

    def __init__(self, migration=None, predictedIntervalTraffic=None,):
        self.migration = migration
        self.predictedIntervalTraffic = predictedIntervalTraffic

    def read(self, iprot):
        if iprot._fast_decode is not None and isinstance(iprot.trans, TTransport.CReadableTransport) and self.thrift_spec is not None:
            iprot._fast_decode(self, iprot, (self.__class__, self.thrift_spec))
            return
        iprot.readStructBegin()
        while True:
            (fname, ftype, fid) = iprot.readFieldBegin()
            if ftype == TType.STOP:
                break
            if fid == 1:
                if ftype == TType.DOUBLE:
                    self.migration = iprot.readDouble()
                else:
                    iprot.skip(ftype)
            elif fid == 2:
                if ftype == TType.LIST:
                    self.predictedIntervalTraffic = []
                    (_etype10, _size7) = iprot.readListBegin()
                    for _i11 in range(_size7):
                        _elem12 = iprot.readDouble()
                        self.predictedIntervalTraffic.append(_elem12)
                    iprot.readListEnd()
                else:
                    iprot.skip(ftype)
            else:
                iprot.skip(ftype)
            iprot.readFieldEnd()
        iprot.readStructEnd()

    def write(self, oprot):
        if oprot._fast_encode is not None and self.thrift_spec is not None:
            oprot.trans.write(oprot._fast_encode(self, (self.__class__, self.thrift_spec)))
            return
        oprot.writeStructBegin('PredictedIntervalTrafficData')
        if self.migration is not None:
            oprot.writeFieldBegin('migration', TType.DOUBLE, 1)
            oprot.writeDouble(self.migration)
            oprot.writeFieldEnd()
        if self.predictedIntervalTraffic is not None:
            oprot.writeFieldBegin('predictedIntervalTraffic', TType.LIST, 2)
            oprot.writeListBegin(TType.DOUBLE, len(self.predictedIntervalTraffic))
            for iter13 in self.predictedIntervalTraffic:
                oprot.writeDouble(iter13)
            oprot.writeListEnd()
            oprot.writeFieldEnd()
        oprot.writeFieldStop()
        oprot.writeStructEnd()

    def validate(self):
        return

    def __repr__(self):
        L = ['%s=%r' % (key, value)
             for key, value in self.__dict__.items()]
        return '%s(%s)' % (self.__class__.__name__, ', '.join(L))

    def __eq__(self, other):
        return isinstance(other, self.__class__) and self.__dict__ == other.__dict__

    def __ne__(self, other):
        return not (self == other)