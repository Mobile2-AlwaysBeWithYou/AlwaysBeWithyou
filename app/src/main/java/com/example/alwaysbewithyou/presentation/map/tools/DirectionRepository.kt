package com.example.alwaysbewithyou.presentation.map.tools

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import com.google.android.gms.maps.model.LatLng
import com.example.alwaysbewithyou.presentation.map.RouteInfo
import com.example.alwaysbewithyou.presentation.map.TransportType
import com.example.alwaysbewithyou.presentation.map.api.GoogleDirectionsApiService
import com.example.alwaysbewithyou.presentation.map.api.TmapDirectionsApiService
import com.google.gson.Gson
import com.google.maps.android.PolyUtil


class DirectionRepository(
    private val tmapDirectionsApiService: TmapDirectionsApiService,
    private val googleDirectionsApiService: GoogleDirectionsApiService,
    private val context: Context
) {
    companion object {
        private const val TAG = "DirectionRepository"
    }

    private val tmapAppKey: String by lazy {
        val applicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        val key = applicationInfo.metaData.getString("com.skt.Tmap.OPENAPIKEY")
            ?: throw IllegalStateException("tmapAppKey not found in AndroidManifest.xml. Check com.skt.Tmap.OPENAPIKEY meta-data.")
        Log.d(TAG, "Loaded Tmap App Key: $key")
        key
    }

    private val googleApiKey: String by lazy {
        val applicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        val key = applicationInfo.metaData.getString("com.google.android.geo.API_KEY")
            ?: throw IllegalStateException("Google API Key not found in AndroidManifest.xml. Check com.google.android.geo.API_KEY meta-data.")
        Log.d(TAG, "Loaded Google API Key: $key")
        key
    }

    suspend fun getRoutes(
        start: LatLng,
        end: LatLng,
        transportType: TransportType
    ): Result<List<RouteInfo>> {
        return try {
            val mode = when (transportType) {
                TransportType.WALKING -> "walking"
                TransportType.TRANSIT -> "transit"
                TransportType.DRIVING -> "driving"
            }

            Log.d(
                TAG,
                "Fetching routes for ${transportType.name} from ${start.latitude},${start.longitude} to ${end.latitude},${end.longitude} using Tmap API"
            )

            val routesFound = mutableListOf<RouteInfo>()

            when (transportType) {
                TransportType.WALKING -> {
                    val response = tmapDirectionsApiService.getPedestrianDirections(
                        appKey = tmapAppKey,
                        startX = start.longitude,
                        startY = start.latitude,
                        endX = end.longitude,
                        endY = end.latitude
                    )

                    if (response.isSuccessful) {
                        val tmapResponse = response.body()
                        Log.d(TAG, "Tmap Walking Response Body: ${Gson().toJson(tmapResponse)}")

                        val firstFeatureProperties =
                            tmapResponse?.features?.firstOrNull()?.properties
                        val totalDistance = firstFeatureProperties?.totalDistance
                        val totalTime = firstFeatureProperties?.totalTime

                        val polylinePoints = mutableListOf<LatLng>()
                        tmapResponse?.features?.forEach { feature ->
                            if (feature.geometry.type == "LineString") {
                                (feature.geometry.coordinates as? List<List<Double>>)?.let { coords ->
                                    coords.forEach { coordPair ->
                                        polylinePoints.add(LatLng(coordPair[1], coordPair[0]))
                                    }
                                }
                            }
                        }

                        if (totalTime != null && totalDistance != null) {
                            val durationText = formatDuration(totalTime.toLong())
                            routesFound.add(
                                RouteInfo(
                                    id = "tmap_walking_route_0",
                                    durationText = durationText,
                                    durationValue = totalTime.toLong(),
                                    polylinePoints = polylinePoints,
                                    transportType = transportType
                                )
                            )
                        } else {
                            Log.w(
                                TAG,
                                "Tmap Walking response missing totalTime or totalDistance in first feature properties."
                            )
                        }
                    } else {
                        handleApiError(
                            response.code(),
                            response.message(),
                            response.errorBody()?.string(),
                            transportType
                        )
                        return Result.Failure(Exception("보행자 길찾기 실패: ${response.code()}"))
                    }
                }

                TransportType.DRIVING -> {
                    val response = tmapDirectionsApiService.getDrivingDirections(
                        appKey = tmapAppKey,
                        startX = start.longitude,
                        startY = start.latitude,
                        endX = end.longitude,
                        endY = end.latitude
                    )

                    if (response.isSuccessful) {
                        val tmapResponse = response.body()
                        Log.d(TAG, "Tmap Driving Response Body: ${Gson().toJson(tmapResponse)}")

                        val firstFeatureProperties =
                            tmapResponse?.features?.firstOrNull()?.properties
                        val totalDistance = firstFeatureProperties?.totalDistance
                        val totalTime = firstFeatureProperties?.totalTime

                        val polylinePoints = mutableListOf<LatLng>()
                        tmapResponse?.features?.forEach { feature ->
                            if (feature.geometry.type == "LineString") {
                                (feature.geometry.coordinates as? List<List<Double>>)?.let { coords ->
                                    coords.forEach { coordPair ->
                                        polylinePoints.add(LatLng(coordPair[1], coordPair[0]))
                                    }
                                }
                            }
                        }

                        if (totalTime != null && totalDistance != null) {
                            val durationText = formatDuration(totalTime.toLong())
                            routesFound.add(
                                RouteInfo(
                                    id = "tmap_driving_route_0",
                                    durationText = durationText,
                                    durationValue = totalTime.toLong(),
                                    polylinePoints = polylinePoints,
                                    transportType = transportType
                                )
                            )
                        } else {
                            Log.w(
                                TAG,
                                "Tmap Driving response missing totalTime or totalDistance in first feature properties."
                            )
                        }
                    } else {
                        handleApiError(
                            response.code(),
                            response.message(),
                            response.errorBody()?.string(),
                            transportType
                        )
                        return Result.Failure(Exception("자동차 길찾기 실패: ${response.code()}"))
                    }
                }

                TransportType.TRANSIT -> {
                    Log.d(TAG, "Fetching routes for TRANSIT from ${start.latitude},${start.longitude} to ${end.latitude},${end.longitude} using Google Directions API")

                    val departureTime = System.currentTimeMillis() / 1000

                    val response = googleDirectionsApiService.getDirections(
                        origin = "${start.latitude},${start.longitude}",
                        destination = "${end.latitude},${end.longitude}",
                        mode = "transit",
                        departure_time = departureTime.toString(),
                        key = googleApiKey,
                        alternatives = true
                    )

                    if (response.isSuccessful) {
                        val googleResponse = response.body()
                        Log.d(TAG, "Google Transit Response Body: ${Gson().toJson(googleResponse)}")

                        googleResponse?.routes?.forEachIndexed { index, route ->
                            val totalDuration = route.legs?.firstOrNull()?.duration?.value
                            val polylinePoints = mutableListOf<LatLng>()

                            route.overviewPolyline?.points?.let { encodedPolyline ->
                                polylinePoints.addAll(PolyUtil.decode(encodedPolyline))
                            }

                            if (totalDuration != null) {
                                val durationText = formatDuration(totalDuration.toLong())
                                routesFound.add(
                                    RouteInfo(
                                        id = "google_transit_route_$index",
                                        durationText = durationText,
                                        durationValue = totalDuration.toLong(),
                                        polylinePoints = polylinePoints,
                                        transportType = transportType
                                    )
                                )
                            } else {
                                Log.w(TAG, "Google Transit response missing duration for route $index.")
                            }
                        }

                        if (routesFound.isEmpty()) {
                            Log.d(TAG, "Google Transit Response did not contain any valid routes.")
                        }

                    } else {
                        handleApiError(
                            response.code(),
                            response.message(),
                            response.errorBody().toString(),
                            transportType
                        )
                        return Result.Failure(Exception("Google 대중교통 길찾기 실패: ${response.code()}"))
                    }
                }
            }

            Log.d(
                TAG,
                "Received ${routesFound.size} routes from API for ${transportType.name}"
            )
            Result.Success(routesFound)

        } catch (e: Exception) {
            Log.e(TAG, "Error fetching routes for ${transportType.name}: ${e.localizedMessage}", e)
            Result.Failure(Exception("오류 발생: ${e.localizedMessage}"))
        }
    }

    private fun formatDuration(seconds: Long): String {
        val minutes = seconds / 60
        val hours = minutes / 60
        val remainingMinutes = minutes % 60
        return if (hours > 0) {
            "${hours}시간 ${remainingMinutes}분"
        } else {
            "${minutes}분"
        }
    }

    private fun handleApiError(
        statusCode: Int,
        message: String?,
        errorBody: String?,
        transportType: TransportType
    ) {
        Log.e(
            TAG,
            "API call failed for ${transportType.name}: $statusCode - $message. Error body: $errorBody"
        )
    }
}
