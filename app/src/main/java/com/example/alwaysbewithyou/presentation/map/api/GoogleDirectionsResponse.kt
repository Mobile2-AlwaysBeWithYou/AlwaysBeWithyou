package com.example.alwaysbewithyou.presentation.map.api

import com.google.gson.annotations.SerializedName

data class GoogleDirectionsResponse(
    val routes: List<Route>?,
    val status: String
) {
    data class Route(
        val legs: List<Leg>?,
        @SerializedName("overview_polyline")
        val overviewPolyline: PolylineInfo?
    )

    data class Leg(
        val distance: ValueText?,
        val duration: ValueText?,
        val end_address: String?,
        val end_location: LatLngLiteral?,
        val start_address: String?,
        val start_location: LatLngLiteral?,
        val steps: List<Step>?,
        val traffic_speed_entry: List<Any>?,
        val via_waypoint: List<Any>?
    )

    data class PolylineInfo(
        val points: String?
    )

    data class ValueText(
        val text: String?,
        val value: Int?
    )

    data class LatLngLiteral(
        val lat: Double,
        val lng: Double
    )

    data class Step(
        val distance: ValueText?,
        val duration: ValueText?,
        val end_location: LatLngLiteral?,
        val html_instructions: String?,
        val polyline: PolylineInfo?,
        val start_location: LatLngLiteral?,
        val travel_mode: String?,
        val maneuver: String?
    )
}
