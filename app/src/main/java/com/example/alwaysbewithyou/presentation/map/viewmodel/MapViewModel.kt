package com.example.alwaysbewithyou.presentation.map.viewmodel

import android.location.Location
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alwaysbewithyou.presentation.map.api.GooglePlacesApiService
import com.example.alwaysbewithyou.presentation.map.tools.PlaceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val results: List<GooglePlacesApiService.PlaceResult>) : SearchState()
    object NoResults : SearchState()
    data class Error(val message: String) : SearchState()
}

private const val DEFAULT_LATITUDE = 37.5408
private const val DEFAULT_LONGITUDE = 127.0793

fun createLocationFromLatLng(latitude: Double, longitude: Double): Location {
    val loc = Location("default_provider")
    loc.latitude = latitude
    loc.longitude = longitude
    return loc
}

class MapViewModel(
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _searchResults = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchResults: StateFlow<SearchState> = _searchResults.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    private val _currentLocation = MutableStateFlow<Location>(
        createLocationFromLatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
    )
    val currentLocation: StateFlow<Location> = _currentLocation.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun updateCurrentLocation(location: Location?) {
        _currentLocation.value = location ?: createLocationFromLatLng(DEFAULT_LATITUDE, DEFAULT_LONGITUDE)
        Timber.d("MapViewModel: currentLocation 업데이트됨: ${_currentLocation.value.latitude}, ${_currentLocation.value.longitude}")
    }

    fun searchPlaces(query: String) {
        val currentEffectiveLocation = _currentLocation.value

        Timber.d("MapViewModel: searchPlaces 호출 시 사용될 위치: (${currentEffectiveLocation.latitude}, ${currentEffectiveLocation.longitude})")

        if (query.isBlank() && currentEffectiveLocation == null) {
            _searchResults.value = SearchState.Idle
            Timber.d("MapViewModel: 검색 쿼리가 비어 있고 유효한 위치가 없어 검색하지 않습니다. (Idle 상태)")
            return
        }

        _searchResults.value = SearchState.Loading
        Timber.d("MapViewModel: 검색 시작: 쿼리='$query'")

        viewModelScope.launch {
            try {
                Timber.d("현재 위치 (${currentEffectiveLocation.latitude}, ${currentEffectiveLocation.longitude}) 기반 주변 검색 시작: $query, 반경=10000m")
                val results: List<GooglePlacesApiService.PlaceResult> =
                    placeRepository.searchPlacesNearby(
                        currentEffectiveLocation.latitude,
                        currentEffectiveLocation.longitude,
                        query.ifBlank { null },
                        10000
                    )

                if (results.isNotEmpty()) {
                    _searchResults.value = SearchState.Success(results)
                    Timber.d("검색 성공: ${results.size}개 결과")
                } else {
                    _searchResults.value = SearchState.NoResults
                    Timber.d("검색 결과 없음")
                }
            } catch (e: Exception) {
                _searchResults.value = SearchState.Error("검색 중 오류 발생: ${e.localizedMessage}")
                Timber.e(e, "검색 오류: ${e.localizedMessage}")
            }
        }
    }
}
