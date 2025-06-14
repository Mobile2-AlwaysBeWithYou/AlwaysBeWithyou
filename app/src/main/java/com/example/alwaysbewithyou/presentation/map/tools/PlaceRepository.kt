package com.example.alwaysbewithyou.presentation.map.tools

import android.content.Context
import android.content.pm.PackageManager
import com.example.alwaysbewithyou.presentation.map.api.GooglePlacesApiService
import com.example.alwaysbewithyou.presentation.map.api.GooglePlacesApiService.PlaceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class PlaceRepository(
    private val apiService: GooglePlacesApiService,
    private val context: Context
) {
    private val apiKey: String by lazy {
        val applicationInfo = context.packageManager.getApplicationInfo(
            context.packageName,
            PackageManager.GET_META_DATA
        )
        applicationInfo.metaData.getString("com.google.android.geo.API_KEY")
            ?: throw IllegalStateException("API_KEY not found in AndroidManifest.xml")
    }

    fun getPhotoUrl(photoReference: String): String {
        return "https://maps.googleapis.com/maps/api/place/photo" +
                "?maxwidth=600" +
                "&photoreference=$photoReference" +
                "&key=$apiKey"
    }

    suspend fun getPlaceDetails(placeId: String): PlaceResult? {
        return withContext(Dispatchers.IO) {
            try {
                Timber.d("API 호출: Place Details for ID: $placeId")
                val response = apiService.getPlaceDetails(placeId, apiKey, language = "ko")
                if (response.status == "OK") {
                    Timber.i("Place Details API 응답 성공, status: ${response.status}")
                    response.result
                } else {
                    Timber.w("Place Details API 응답 실패, status: ${response.status}, message: ${response.status}")
                    null
                }
            } catch (e: Exception) {
                Timber.e(e, "Place Details API 호출 중 예외 발생: ${e.message}")
                null
            }
        }
    }

    suspend fun searchTextPlaces(query: String): List<PlaceResult> {
        return withContext(Dispatchers.IO) {
            try {
                Timber.d("API 호출: Text Search for Query: $query")
                val response = apiService.searchTextPlaces(query, "ko", apiKey, "kr", "place_id,name,formatted_address,geometry,rating,user_ratings_total,types,photos")
                if (response.status == "OK" && !response.results.isNullOrEmpty()) {
                    Timber.i("Text Search API 응답 성공, ${response.results.size}개 결과")
                    response.results
                } else if (response.status == "ZERO_RESULTS") {
                    Timber.i("Text Search API 응답: 결과 없음")
                    emptyList()
                } else {
                    Timber.w("Text Search API 응답 실패, status: ${response.status}")
                    emptyList()
                }
            } catch (e: Exception) {
                Timber.e(e, "Text Search API 호출 중 예외 발생: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun searchPlacesNearby(
        latitude: Double,
        longitude: Double,
        query: String?,
        radius: Int
    ): List<PlaceResult> {
        return withContext(Dispatchers.IO) {
            try {
                val location = "$latitude,$longitude"
                val response = apiService.nearbySearch(
                    location = location,
                    radius = radius,
                    keyword = query,
                    apiKey = apiKey
                )

                if (response.status == "OK") {
                    response.results ?: emptyList()
                } else if (response.status == "ZERO_RESULTS") {
                    emptyList()
                } else {
                    Timber.e("Google Places API Nearby Search Error: ${response.status} - ${response.error_message}")
                    emptyList()
                }
            } catch (e: Exception) {
                Timber.e(e, "PlaceRepository 주변 검색 오류: ${e.localizedMessage}")
                emptyList()
            }
        }
    }
}