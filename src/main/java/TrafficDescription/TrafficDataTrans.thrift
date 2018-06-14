struct NowAreaTrafficData {
    1: string areaId
    2: double timeOfHour
    3: list<double> nowAreaTraffic
}

struct PredictedAreaTrafficData {
    1: double migration
    2: list<double> predictedAreaTraffic
}

struct TrafficDescription.NowEdgeTrafficData {
    1: list<double>  nodeSequence
    2: double timeOfHour
    3: list<double> nowEdgeTraffic
}

struct TrafficDescription.PredictedEdgeTrafficData {
    1: list<double> predictedEdgeTraffic
}

service TrafficDescription.TrafficDataService {
    PredictedAreaTrafficData getPredictedData(1:NowAreaTrafficData nowAreaTrafficData)
    list<double> getEdgePredictedData(1:TrafficDescription.NowEdgeTrafficData nowEdgeTrafficData)
}