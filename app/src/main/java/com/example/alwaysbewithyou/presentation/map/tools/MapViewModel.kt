package com.example.alwaysbewithyou.presentation.map.tools

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alwaysbewithyou.BuildConfig
import com.example.alwaysbewithyou.presentation.map.tools.RetrofitClient
import com.example.alwaysbewithyou.presentation.map.tools.GooglePlacesApiService.PlaceResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val results: List<PlaceResult>) : SearchState()
    object NoResults : SearchState()
    data class Error(val message: String) : SearchState()
}

class MapViewModel : ViewModel() {

    private val API_KEY = BuildConfig.API_KEY

    private val _searchResults = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchResults: StateFlow<SearchState> = _searchResults.asStateFlow()

    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    fun searchPlaces(query: String) {
        if (query.isBlank()) {
            _searchResults.value = SearchState.Idle
            return
        }

        _searchResults.value = SearchState.Loading // 검색 시작 시 로딩 상태로 변경

        viewModelScope.launch {
            try {
                val response = RetrofitClient.googlePlacesApiService.searchTextPlaces(
                    query = query,
                    apiKey = API_KEY // API 키 전달
                )

                // API 응답 상태 확인
                if (response.status == "OK" && !response.results.isNullOrEmpty()) {
                    _searchResults.value = SearchState.Success(response.results)
                } else if (response.status == "ZERO_RESULTS") {
                    _searchResults.value = SearchState.NoResults // 결과 없음
                } else {
                    _searchResults.value = SearchState.Error("API 오류: ${response.status}")
                }
            } catch (e: Exception) {
                _searchResults.value = SearchState.Error("검색 중 오류 발생: ${e.localizedMessage}")
                e.printStackTrace()
            }
        }
    }
}