package com.example.alwaysbewithyou.presentation.map.tools

import com.google.gson.annotations.SerializedName
import retrofit2.Response
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Query

interface GooglePlacesApiService {

    @GET("place/textsearch/json")
    suspend fun searchTextPlaces(
        @Query("query") query: String,
        @Query("language") language: String = "ko", // 결과 언어 (한국어)
        @Query("key") apiKey: String,
        @Query("region") region: String = "kr", // 지역(한국)
        @Query("fields") fields: String = "place_id,name,formatted_address,geometry,rating,user_ratings_total,types,photos"
    ): PlaceSearchResponse

    data class PlaceSearchResponse(
        val results: List<PlaceResult>?,
        val status: String,
        @SerializedName("next_page_token")
        val nextPageToken: String?
    )

    data class PlaceResult(
        val geometry: Geometry?,
        val name: String?, // 장소 이름
        @SerializedName("place_id")
        val placeId: String?, // 장소 고유 ID
        @SerializedName("formatted_address")
        val formattedAddress: String?,
        val rating: Double?, // 평점
        @SerializedName("user_ratings_total")
        val userRatingsTotal: Int?, // 총 리뷰 수
        val types: List<String>?,
        val photos: List<Photo>?,
    )

    data class Geometry(
        val location: Location?,
        val viewport: Viewport?
    )

    data class Location(
        val lat: Double?,
        val lng: Double?
    )

    data class Viewport(
        val northeast: Location?,
        val southwest: Location?
    )

    data class Photo(
        @SerializedName("height")
        val height: Int?,
        @SerializedName("html_attributions")
        val htmlAttributions: List<String>?,
        @SerializedName("photo_reference")
        val photoReference: String?,
        @SerializedName("width")
        val width: Int?
    )
}