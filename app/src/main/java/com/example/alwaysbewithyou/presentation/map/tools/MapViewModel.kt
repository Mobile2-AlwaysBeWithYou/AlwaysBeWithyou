package com.example.alwaysbewithyou.presentation.map.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alwaysbewithyou.presentation.map.tools.RetrofitClient
import com.example.alwaysbewithyou.presentation.map.tools.PlaceItem
import com.example.alwaysbewithyou.presentation.map.tools.NaverSearchResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import android.util.Log
import com.example.alwaysbewithyou.BuildConfig

// 검색 상태를 나타내는 sealed class
sealed class SearchState {
    object Idle : SearchState()
    object Loading : SearchState()
    data class Success(val results: List<PlaceItem>) : SearchState()
    data class Error(val message: String) : SearchState()
    object NoResults : SearchState() // 검색 결과가 없을 때 추가
}

class MapViewModel : ViewModel() {

    private val TAG = "MapViewModel"

    private val NAVER_SEARCH_CLIENT_ID = BuildConfig.NAVER_SEARCH_CLIENT_ID
    private val NAVER_SEARCH_CLIENT_SECRET = BuildConfig.NAVER_SEARCH_CLIENT_SECRET

    // 검색어 상태
    private val _searchQuery = MutableStateFlow("")
    val searchQuery: StateFlow<String> = _searchQuery.asStateFlow()

    // 검색 결과 상태 (UI에 반영될 데이터)
    private val _searchResults = MutableStateFlow<SearchState>(SearchState.Idle)
    val searchResults: StateFlow<SearchState> = _searchResults.asStateFlow()

    fun updateSearchQuery(query: String) {
        _searchQuery.value = query
    }

    /**
     * 네이버 지역 검색 API를 호출하고 결과를 _searchResults에 업데이트합니다.
     * @param query 검색어
     */
    fun searchLocalPlaces(query: String) {
        if (query.isBlank()) {
            _searchResults.value = SearchState.Idle // 빈 검색어면 초기 상태로
            return
        }

        _searchResults.value = SearchState.Loading // 검색 시작 시 로딩 상태로 변경

        viewModelScope.launch {
            try {
                val response = RetrofitClient.naverApiService.searchPlaces(
                    clientId = NAVER_SEARCH_CLIENT_ID,
                    clientSecret = NAVER_SEARCH_CLIENT_SECRET,
                    query = query,
                    display = 10, // 원하는 결과 개수
                    start = 1,
                    sort = "random"
                )

                if (response.isSuccessful) {
                    val naverSearchResponse = response.body()
                    val items = naverSearchResponse?.items ?: emptyList()
                    if (items.isEmpty()) {
                        _searchResults.value = SearchState.NoResults // 결과 없을 때
                    } else {
                        _searchResults.value = SearchState.Success(items)
                        Log.d(TAG, "검색 성공. 총 ${naverSearchResponse?.total}개 결과.")
                        items.forEach { item ->
                            Log.d(TAG, "  - ${item.title?.replace("<b>", "")?.replace("</b>", "")}, ${item.address}")
                        }
                    }
                } else {
                    val errorBody = response.errorBody()?.string()
                    val errorMessage = "API 호출 실패: HTTP ${response.code()}\n$errorBody"
                    _searchResults.value = SearchState.Error(errorMessage)
                    Log.e(TAG, errorMessage)
                }
            } catch (e: HttpException) {
                val errorMessage = "HTTP 예외 발생: ${e.message()}, 코드: ${e.code()}"
                _searchResults.value = SearchState.Error(errorMessage)
                Log.e(TAG, errorMessage)
            } catch (e: Exception) {
                val errorMessage = "네트워크 또는 기타 예외 발생: ${e.localizedMessage}"
                _searchResults.value = SearchState.Error(errorMessage)
                Log.e(TAG, errorMessage, e)
            }
        }
    }
}