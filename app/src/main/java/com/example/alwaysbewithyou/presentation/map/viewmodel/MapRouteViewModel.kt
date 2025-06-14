package com.example.alwaysbewithyou.presentation.map.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.google.android.gms.maps.model.LatLng
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch
import com.example.alwaysbewithyou.presentation.map.RouteInfo
import com.example.alwaysbewithyou.presentation.map.TransportType
import com.example.alwaysbewithyou.presentation.map.tools.DirectionRepository
import com.example.alwaysbewithyou.presentation.map.tools.Result

class MapRouteViewModel(
    private val directionRepository: DirectionRepository
) : ViewModel() {

    private val _routes = MutableStateFlow<List<RouteInfo>>(emptyList())
    val routes: StateFlow<List<RouteInfo>> = _routes

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error

    fun findRoutes(start: LatLng, end: LatLng, transportType: TransportType) {
        _isLoading.value = true
        _error.value = null
        _routes.value = emptyList()

        viewModelScope.launch {
            when (val result = directionRepository.getRoutes(start, end, transportType)) {
                is Result.Success<List<RouteInfo>> -> {
                    _routes.value = result.data
                }
                is Result.Failure -> {
                    _error.value = result.exception.message
                }
            }
            _isLoading.value = false
        }
    }
}