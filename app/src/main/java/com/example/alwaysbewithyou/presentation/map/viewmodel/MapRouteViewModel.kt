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
import kotlinx.coroutines.flow.asStateFlow

sealed class RouteState {
    object Idle : RouteState()
    object Loading : RouteState()
    data class Success(val routes: List<RouteInfo>) : RouteState()
    object NoResults : RouteState()
    data class Error(val message: String) : RouteState()
}

class MapRouteViewModel(
    private val directionRepository: DirectionRepository
) : ViewModel() {

    private val _routes = MutableStateFlow<List<RouteInfo>>(emptyList())
    val routes: StateFlow<List<RouteInfo>> = _routes.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _error = MutableStateFlow<String?>(null)
    val error: StateFlow<String?> = _error.asStateFlow()

    private val _routeState = MutableStateFlow<RouteState>(RouteState.Idle)
    val routeState: StateFlow<RouteState> = _routeState.asStateFlow()

    fun findRoutes(start: LatLng, end: LatLng, transportType: TransportType) {
        _isLoading.value = true
        _error.value = null
        _routes.value = emptyList()

        _routeState.value = RouteState.Loading

        viewModelScope.launch {
            when (val result = directionRepository.getRoutes(start, end, transportType)) {
                is Result.Success<List<RouteInfo>> -> {
                    if (result.data.isNotEmpty()) {
                        _routes.value = result.data
                        _routeState.value = RouteState.Success(result.data)
                    } else {
                        _routes.value = emptyList()
                        _routeState.value = RouteState.NoResults
                    }
                }
                is Result.Failure -> {
                    _error.value = result.exception.message
                    _routeState.value = RouteState.Error(result.exception.message ?: "알 수 없는 오류 발생")
                }
            }
            _isLoading.value = false
        }
    }
}

