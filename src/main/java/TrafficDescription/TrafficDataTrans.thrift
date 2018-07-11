struct NowAreaTrafficData {
    1: string areaId
    2: double timeOfHour
    3: list<double> nowAreaTraffic
}

struct PredictedAreaTrafficData {
    1: double migration
    2: list<double> predictedAreaTraffic
}

struct NowEdgeTrafficData {
    1: list<double>  nodeSequence
    2: double timeOfHour
    3: list<double> nowEdgeTraffic
}

struct PredictedEdgeTrafficData {
    1: list<double> predictedEdgeTraffic
}

service TrafficDataService {
    PredictedAreaTrafficData getPredictedData(1:NowAreaTrafficData nowAreaTrafficData)
    list<double> getEdgePredictedData(1:NowEdgeTrafficData nowEdgeTrafficData)
}