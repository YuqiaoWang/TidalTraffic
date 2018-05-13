struct NowIntervalTrafficData {
    1: string areaId
    2: double timeOfHour
    3: list<double> nowIntervalTraffic
}

struct PredictedIntervalTrafficData {
    1: double migration
    2: list<double> predictedIntervalTraffic
}

struct NowEdgeTrafficData {
    1: string edgeId
    2: double timeOfHour
    3: list<double> NowEdgeTraffic
}

struct PredictedEdgeTrafficData {
    1: double migration
    2: list<double> predictedEdgeTraffic
}

service TrafficDataService {
    PredictedIntervalTrafficData getPredictedData(1:NowIntervalTrafficData nowIntervalTrafficData)
    PredictedEdgeTrafficData getEdgePredictedData(1:NowEdgeTrafficData nowEdgeTrafficData)
}