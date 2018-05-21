struct TrafficDescription.NowAreaTrafficData {
    1: string areaId
    2: double timeOfHour
    3: list<double> nowAreaTraffic
}

struct TrafficDescription.PredictedAreaTrafficData {
    1: double migration
    2: list<double> predictedAreaTraffic
}

struct TrafficDescription.NowEdgeTrafficData {
    1: string edgeId
    2: double timeOfHour
    3: list<double> NowEdgeTraffic
}

struct TrafficDescription.PredictedEdgeTrafficData {
    1: list<double> predictedEdgeTraffic
}

service TrafficDescription.TrafficDataService {
    TrafficDescription.PredictedAreaTrafficData getPredictedData(1:TrafficDescription.NowAreaTrafficData nowAreaTrafficData)
    TrafficDescription.PredictedEdgeTrafficData getEdgePredictedData(1:TrafficDescription.NowEdgeTrafficData nowEdgeTrafficData)
}