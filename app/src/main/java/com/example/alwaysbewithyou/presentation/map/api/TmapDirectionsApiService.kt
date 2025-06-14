package com.example.alwaysbewithyou.presentation.map.api

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query
import com.example.alwaysbewithyou.presentation.map.api.tmap_directions.*

interface TmapDirectionsApiService {

    @GET("tmap/routes")
    suspend fun getDrivingDirections(
        @Header("appKey") appKey: String,
        @Query("startX") startX: Double,
        @Query("startY") startY: Double,
        @Query("endX") endX: Double,
        @Query("endY") endY: Double,
        @Query("reqDolls") reqDolls: String = "Y",
        @Query("reqMin") reqMin: String = "Y",
        @Query("detailPosFlag") detailPosFlag: String = "2",
        @Query("roadInfo") roadInfo: String = "Y",
        @Query("trafficInfo") trafficInfo: String = "Y"
    ): Response<TmapDrivingRouteResponse>

    @GET("tmap/routes/pedestrian")
    suspend fun getPedestrianDirections(
        @Header("appKey") appKey: String,
        @Query("startX") startX: Double,
        @Query("startY") startY: Double,
        @Query("endX") endX: Double,
        @Query("endY") endY: Double,
        @Query("reqCoordType") reqCoordType: String = "WGS84GEO",
        @Query("resCoordType") resCoordType: String = "WGS84GEO",
        @Query("startName") startName: String = "출발지",
        @Query("endName") endName: String = "도착지"
    ): Response<TmapPedestrianRouteResponse>
}
