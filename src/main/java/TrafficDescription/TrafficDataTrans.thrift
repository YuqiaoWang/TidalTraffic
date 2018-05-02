struct NowIntervalTrafficData {
    1: double timeOfHour
    2: list<double> nowIntervalTraffic
}

struct PredictedIntervalTrafficData {
    1: double migration
    2: list<double> predictedIntervalTraffic
}

service TrafficDataService {
    PredictedIntervalTrafficData getPredictedData(1:NowIntervalTrafficData nowIntervalTrafficData)
}