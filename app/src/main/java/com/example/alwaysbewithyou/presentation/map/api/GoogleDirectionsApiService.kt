package com.example.alwaysbewithyou.presentation.map.api

import com.example.alwaysbewithyou.presentation.map.api.GoogleDirectionsResponse
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Query

interface GoogleDirectionsApiService {
    @GET("directions/json")
    suspend fun getDirections(
        @Query("origin") origin: String,
        @Query("destination") destination: String,
        @Query("mode") mode: String,
        @Query("key") key: String,
        @Query("departure_time") departure_time: String,
        @Query("alternatives") alternatives: Boolean = false
    ): Response<GoogleDirectionsResponse>
}