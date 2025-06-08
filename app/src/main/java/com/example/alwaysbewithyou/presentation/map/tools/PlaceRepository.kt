package com.example.alwaysbewithyou.presentation.map.tools

import com.example.alwaysbewithyou.presentation.map.tools.GooglePlacesApiService
import com.example.alwaysbewithyou.presentation.map.tools.GooglePlacesApiService.PlaceResult
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber

class PlaceRepository(
    private val apiService: GooglePlacesApiService,
    private val apiKey: String
) {
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
        radius: Int // MapViewModel에서 50000으로 넘겨주므로 추가
    ): List<PlaceResult> {
        return withContext(Dispatchers.IO) {
            try {
                val location = "$latitude,$longitude"
                val response = apiService.nearbySearch(
                    location = location,
                    radius = radius, // 반경 파라미터 사용
                    keyword = query, // 검색 키워드가 있다면 사용
                    apiKey = apiKey
                    // type 등 추가 파라미터 필요시 여기에 추가
                )

                if (response.status == "OK") {
                    response.results ?: emptyList()
                } else if (response.status == "ZERO_RESULTS") {
                    emptyList()
                } else {
                    Timber.e("Google Places API Nearby Search Error: ${response.status} - ${response.error_message}")
                    emptyList() // API 오류 시 빈 리스트 반환
                }
            } catch (e: Exception) {
                Timber.e(e, "PlaceRepository 주변 검색 오류: ${e.localizedMessage}")
                emptyList() // 예외 발생 시 빈 리스트 반환
            }
        }
    }
}