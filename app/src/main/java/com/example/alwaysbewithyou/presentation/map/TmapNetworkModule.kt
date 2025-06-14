package com.example.alwaysbewithyou.presentation.map

import com.example.alwaysbewithyou.presentation.map.api.TmapDirectionsApiService
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

private const val TMAP_BASE_URL = "https://apis.openapi.sk.com/"

object TmapNetworkModule {

    val tmapRetrofit: Retrofit by lazy {
        Retrofit.Builder()
            .baseUrl(TMAP_BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val tmapDirectionsApiService: TmapDirectionsApiService by lazy {
        tmapRetrofit.create(TmapDirectionsApiService::class.java)
    }
}