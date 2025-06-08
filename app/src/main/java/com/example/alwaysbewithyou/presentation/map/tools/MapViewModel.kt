// MapViewModel.kt 파일

package com.example.alwaysbewithyou.presentation.map.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alwaysbewithyou.BuildConfig
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import timber.log.Timber

sealed class SearchState {
    object Idle : SearchState() // 초기 상태 또는 검색 없음
    object Loading : SearchState() // 검색 진행 중
    data class Success(val results: List<GooglePlacesApiService.PlaceResult>) : SearchState() // 검색 성공 및 결과 포함
    object NoResults : SearchState() // 검색 결과 없음
    data class Error(val message: String) : SearchState() // 검색 중 오류 발생
}

class MapViewModel(
    private val placeRepository: PlaceRepository // PlaceRepository 주입
) : ViewModel() {

    // 기본 생성자
    constructor() : this(
        PlaceRepository(
            RetrofitClient.googlePlacesApiService,
            BuildConfig.API_KEY
        )
    )

    private val _searchResults = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchResults: StateFlow<SearchState> = _searchResults.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * 텍스트 쿼리를 기반으로 장소를 검색합니다 (검색바 입력용).
     * @param query 검색어
     * @param currentLocation 현재 위치 (선택 사항). 제공되면 주변 검색을 수행합니다.
     */
    fun searchPlaces(query: String, currentLocation: LatLng? = null) {
        if (query.isBlank() && currentLocation == null) { // 검색어 없고 위치도 없으면 Idle
            _searchResults.value = SearchState.Idle
            return
        }

        _searchResults.value = SearchState.Loading // 검색 시작 시 로딩 상태로 변경

        viewModelScope.launch {
            try {
                val results: List<GooglePlacesApiService.PlaceResult> = if (currentLocation != null) {
                    // 현재 위치가 있으면 주변 검색 + 키워드 필터링
                    Timber.d("현재 위치 (${currentLocation.latitude}, ${currentLocation.longitude}) 기반 검색 시작: $query")
                    placeRepository.searchPlacesNearby(currentLocation.latitude, currentLocation.longitude, query.ifBlank { null }, 50000) // 50km 반경 예시
                } else {
                    // 현재 위치가 없으면 일반 텍스트 검색 (MapListScreen에서 직접 사용될 경우 대비)
                    Timber.d("일반 텍스트 검색 시작: $query")
                    placeRepository.searchTextPlaces(query)
                }

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


    fun searchPlacesNearby(latitude: Double, longitude: Double, query: String? = null) {

    }
}