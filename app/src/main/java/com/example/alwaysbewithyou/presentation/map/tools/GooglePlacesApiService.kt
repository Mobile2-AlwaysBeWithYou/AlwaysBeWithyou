package com.example.alwaysbewithyou.presentation.map.tools

import com.google.android.gms.maps.model.LatLng
import com.google.gson.annotations.SerializedName
import retrofit2.http.GET
import retrofit2.http.Query

interface GooglePlacesApiService {
    @GET("place/nearbysearch/json")
    suspend fun nearbySearch(
        @Query("location") location: String,
        @Query("radius") radius: Int,
        @Query("type") type: String? = null,
        @Query("keyword") keyword: String? = null,
        @Query("language") language: String = "ko",
        @Query("key") apiKey: String
    ): NearbySearchResponse

    data class NearbySearchResponse(
        val results: List<PlaceResult>?,
        val status: String,
        val error_message: String?
    )

    @GET("place/autocomplete/json")
    suspend fun autocomplete(
        @Query("input") input: String,
        @Query("key") apiKey: String,
        @Query("sessiontoken") sessionToken: String? = null
    ): AutocompleteResponse

    @GET("place/details/json")
    suspend fun getPlaceDetails(
        @Query("place_id") placeId: String,
        @Query("key") apiKey: String,
        @Query("fields") fields: String = "name,formatted_address,geometry,rating,user_ratings_total,international_phone_number,website,opening_hours/weekday_text,photo,review", // 필요한 필드 지정
        @Query("sessiontoken") sessionToken: String? = null,
        @Query("language") language: String = "ko"
    ): PlaceDetailsResponse

    @GET("place/textsearch/json")
    suspend fun searchTextPlaces(
        @Query("query") query: String,
        @Query("language") language: String = "ko", // 결과 언어 (한국어)
        @Query("key") apiKey: String,
        @Query("region") region: String = "kr", // 지역(한국)
        @Query("fields") fields: String = "place_id,name,formatted_address,geometry,rating,user_ratings_total,types,photos"
    ): PlaceSearchResponse

    data class PlaceDetailsResponse(
        @SerializedName("result")
        val result: PlaceResult?,
        val status: String,
        @SerializedName("html_attributions")
        val htmlAttributions: List<String>? = null
    )

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
        val latLng: LatLng?,
        @SerializedName("user_ratings_total")
        val userRatingsTotal: Int?, // 총 리뷰 수
        val types: List<String>?,
        val photos: List<Photo>?,
        val openingHours: List<String>? = null,
        val website: String? = null,
        val internationalPhoneNumber: String? = null,
    )

    data class AutocompleteResponse(
        val predictions: List<Prediction>?,
        val status: String
    )

    data class Prediction(
        @SerializedName("place_id") val placeId: String,
        val description: String
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