data class RoutingApiResponse(
    val routes: List<Route> = emptyList()
)

data class Route(
    val summary: RouteSummary,
    val legs: List<Leg>,
    val sections: List<Section>
)

data class RouteSummary(
    val lengthInMeters: Int,
    val travelTimeInSeconds: Int,
    val trafficDelayInSeconds: Int,
    val departureTime: String,
    val arrivalTime: String
)

data class Leg(
    val summary: LegSummary,
    val points: List<Point>
)

data class LegSummary(
    val lengthInMeters: Int,
    val travelTimeInSeconds: Int,
    val trafficDelayInSeconds: Int,
    val departureTime: String,
    val arrivalTime: String
)

data class Point(
    val latitude: Double,
    val longitude: Double
)

data class Section(
    val startPointIndex: Int,
    val endPointIndex: Int,
    val sectionType: String,
    val travelMode: String
)
