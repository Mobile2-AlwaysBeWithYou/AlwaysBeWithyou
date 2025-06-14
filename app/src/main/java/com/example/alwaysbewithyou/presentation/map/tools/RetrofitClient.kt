package com.example.alwaysbewithyou.presentation.map.tools

import com.example.alwaysbewithyou.presentation.map.api.GooglePlacesApiService
import com.example.alwaysbewithyou.presentation.map.api.TmapDirectionsApiService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.concurrent.TimeUnit
import kotlin.jvm.java

object RetrofitClient {

    private const val GOOGLE_BASE_URL = "https://maps.googleapis.com/maps/api/"
    private const val TMAP_BASE_URL = "https://apis.openapi.sk.com/"

    val googlePlacesApiService: GooglePlacesApiService by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        Retrofit.Builder()
            .baseUrl(GOOGLE_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(GooglePlacesApiService::class.java)
    }

    val tmapDirectionsApiService: TmapDirectionsApiService by lazy {
        val okHttpClient = OkHttpClient.Builder()
            .connectTimeout(30, TimeUnit.SECONDS)
            .readTimeout(30, TimeUnit.SECONDS)
            .writeTimeout(30, TimeUnit.SECONDS)
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()

        Retrofit.Builder()
            .baseUrl(TMAP_BASE_URL)
            .client(okHttpClient)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(TmapDirectionsApiService::class.java)
    }
}
