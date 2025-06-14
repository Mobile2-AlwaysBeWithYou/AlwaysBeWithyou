package com.example.alwaysbewithyou.presentation.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.alwaysbewithyou.presentation.map.api.GooglePlacesApiService.PlaceResult
import com.example.alwaysbewithyou.presentation.map.tools.PlaceRepository
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
    private val placeRepository: PlaceRepository
) : ViewModel() {

    private val _placeDetail = MutableStateFlow<PlaceDetailState>(PlaceDetailState.Idle)
    val placeDetail: StateFlow<PlaceDetailState> = _placeDetail.asStateFlow()

    fun getPhotoUrl(photoReference: String): String {
        return placeRepository.getPhotoUrl(photoReference)
    }

    fun loadPlaceDetails(placeId: String) {
        if (_placeDetail.value is PlaceDetailState.Loading ||
            (_placeDetail.value is PlaceDetailState.Success && (placeDetail.value as PlaceDetailState.Success).place.placeId == placeId)
        ) {
            return
        }

        viewModelScope.launch {
            _placeDetail.value = PlaceDetailState.Loading
            try {
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