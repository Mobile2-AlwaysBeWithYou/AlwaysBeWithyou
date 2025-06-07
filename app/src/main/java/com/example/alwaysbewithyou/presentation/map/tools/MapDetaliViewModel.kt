package com.example.alwaysbewithyou.presentation.map.tools

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alwaysbewithyou.presentation.map.tools.GooglePlacesApiService.PlaceResult
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

sealed class PlaceDetailState {
    object Idle : PlaceDetailState()
    object Loading : PlaceDetailState()
    data class Success(val place: PlaceResult) : PlaceDetailState()
    data class Error(val message: String) : PlaceDetailState()
}

class MapDetailViewModel(
    private val placeRepository: PlaceRepository // PlaceRepository 주입
) : ViewModel() {

    private val _placeDetail = MutableStateFlow<PlaceDetailState>(PlaceDetailState.Idle)
    val placeDetail: StateFlow<PlaceDetailState> = _placeDetail.asStateFlow()

    // Place ID를 받아서 상세 정보를 로드하는 함수
    fun loadPlaceDetails(placeId: String) {
        // 이미 로딩 중이거나 성공적으로 로드된 상태면 다시 로드하지 않도록 방지 (선택 사항)
        if (_placeDetail.value is PlaceDetailState.Loading ||
            (_placeDetail.value is PlaceDetailState.Success && (placeDetail.value as PlaceDetailState.Success).place.placeId == placeId)
        ) {
            return
        }

        viewModelScope.launch {
            _placeDetail.value = PlaceDetailState.Loading
            try {
                // Repository를 통해 실제 데이터 가져오기
                val detail = placeRepository.getPlaceDetails(placeId)
                if (detail != null) {
                    _placeDetail.value = PlaceDetailState.Success(detail)
                } else {
                    _placeDetail.value = PlaceDetailState.Error("장소 상세 정보를 찾을 수 없습니다. (ID: $placeId)")
                }
            } catch (e: Exception) {
                _placeDetail.value = PlaceDetailState.Error("장소 상세 정보 로딩 실패: ${e.message}")
            }
        }
    }
}