package com.example.alwaysbewithyou.presentation.map.api.tmap_directions

data class TmapDrivingRouteResponse(
    val features: List<Feature>
) {
    data class Feature(
        val type: String,
        val geometry: Geometry,
        val properties: Properties
    )

    data class Geometry(
        val type: String,
        val coordinates: Any
    )

    data class Properties(
        val totalDistance: Int?,
        val totalTime: Int?,
        val totalFare: Int?,
        val taxiFare: Int?,
        val tollFare: Int?,

        val pointType: String?,
        val turnType: Int?,
        val description: String?,
        val roadName: String?,
        val nextRoadName: String?,
        val time: Int?,
        val distance: Int?,
        val management: String?,
        val trafficSpeed: Double?,
        val trafficAndOption: String?
    )
}