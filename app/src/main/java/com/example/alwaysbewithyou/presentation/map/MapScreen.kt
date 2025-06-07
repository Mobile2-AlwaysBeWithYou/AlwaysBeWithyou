package com.example.alwaysbewithyou.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel // ViewModel 사용을 위한 import
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties as GoogleMapProperties
import com.google.maps.android.compose.MapUiSettings as GoogleMapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.CameraPositionState
import androidx.compose.runtime.collectAsState // StateFlow를 Compose 상태로 변환
import com.example.alwaysbewithyou.presentation.map.tools.MapViewModel
import com.example.alwaysbewithyou.presentation.map.tools.SearchState
import com.naver.maps.map.compose.rememberFusedLocationSource

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    modifier: Modifier = Modifier,
    onNavigateToMapList: () -> Unit,
    viewModel: MapViewModel = viewModel()
) {
    // 위치 권한 요청
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )
    LaunchedEffect(Unit) { permissionState.launchMultiplePermissionRequest() }
    val granted = permissionState.permissions.any { it.status.isGranted }

    // 카메라 상태
    val seoulCityHall = LatLng(37.5665, 126.9770) // 서울 시청 위경도
    val cameraPositionState: CameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(seoulCityHall, 15f) // 초기 줌 레벨 15
    }

    // ViewModel에서 검색어와 검색 결과 상태를 가져옴
    val searchText by viewModel.searchQuery.collectAsState()
    val searchResultsState by viewModel.searchResults.collectAsState()

    LaunchedEffect(searchResultsState) {
        if (searchResultsState is SearchState.Success || searchResultsState is SearchState.NoResults) {
            onNavigateToMapList()
        }
    }

    Column(
        modifier = modifier.fillMaxSize()
    ) {
        // 검색바
        OutlinedTextField(
            value = searchText,
            onValueChange = { viewModel.updateSearchQuery(it) }, // 검색어 변경 시 ViewModel 업데이트
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            placeholder = { Text("장소 검색") },
            singleLine = true,
            trailingIcon = {
                IconButton(onClick = { viewModel.searchPlaces(searchText) }) {
                    Icon(Icons.Default.Search, contentDescription = "검색")
                }
            },
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.searchPlaces(searchText)
            })
        )

        // 지도
        Box(
            modifier = Modifier
                .weight(1f) // 남은 공간 모두 차지
                .fillMaxWidth()
        ) {
            if (granted) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = GoogleMapProperties(
                        isMyLocationEnabled = true // 내 위치 표시 활성화

                    ),
                    uiSettings = GoogleMapUiSettings(
                        myLocationButtonEnabled = true, // 내 위치 버튼
                        zoomControlsEnabled = false
                    )
                ) {

                }
            } else {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                )
                Text(
                    text = "위치 권한이 필요합니다.",
                    color = Color.Red,
                    modifier = Modifier
                        .align(Alignment.Center)
                        .background(Color.White.copy(alpha = 0.8f))
                        .padding(8.dp)
                )
            }
        }
    }
}