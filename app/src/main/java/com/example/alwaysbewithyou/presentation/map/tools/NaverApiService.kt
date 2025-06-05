package com.example.alwaysbewithyou.presentation.map.tools

import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface NaverApiService {

    @GET("v1/search/local.json")
    suspend fun searchPlaces(
        @Header("X-Naver-Client-Id") clientId: String,
        @Header("X-Naver-Client-Secret") clientSecret: String,
        @Query("query") query: String, // 검색어
        @Query("display") display: Int? = null, // 한 번에 표시할 검색 결과 개수 (기본값: 10, 최대: 5)
        @Query("start") start: Int? = null, // 검색 시작 위치 (기본값: 1, 최대: 1)
        @Query("sort") sort: String? = null // 정렬 옵션: random (유사도순), comment (카페/블로그 리뷰 개수 순)
    ): Response<NaverSearchResponse> // NaverSearchResponse는 API 응답을 담을 데이터 클래스
}

// API 응답을 담을 데이터 클래스 (필드는 네이버 API 문서 참고)
// 예시이므로, 실제 필드명과 타입은 정확히 맞춰야 합니다.
data class NaverSearchResponse(
    val lastBuildDate: String?,
    val total: Int?,
    val start: Int?,
    val display: Int?,
    val items: List<PlaceItem>?
)

data class PlaceItem(
    val title: String?, // 업체, 기관 명칭
    val link: String?, // 업체, 기관의 상세 정보 URI
    val category: String?,
    val description: String?,
    val telephone: String?,
    val address: String?, // 주소
    val roadAddress: String?, // 도로명 주소
    val mapx: String?, // 업체, 기관이 위치한 장소의 x 좌표 (KATEC 좌표계)
    val mapy: String?  // 업체, 기관이 위치한 장소의 y 좌표 (KATEC 좌표계)
)