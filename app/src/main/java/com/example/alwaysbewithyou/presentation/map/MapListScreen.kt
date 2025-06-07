package com.example.alwaysbewithyou.presentation.map

import android.Manifest
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items // items 임포트 확인
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.example.alwaysbewithyou.presentation.map.tools.GooglePlacesApiService.PlaceResult // PlaceResult 임포트 확인
import com.example.alwaysbewithyou.presentation.map.tools.MapViewModel
import com.example.alwaysbewithyou.presentation.map.tools.SearchState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlin.collections.sortedByDescending
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties as GoogleMapProperties
import com.google.maps.android.compose.MapUiSettings as GoogleMapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState as rememberGoogleCameraPositionState
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapListScreen(
    viewModel: MapViewModel,
    onNavigateBack: () -> Unit
) {
    var sortType by remember { mutableStateOf("거리순") }

    // 권한
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val granted = permissionState.permissions.any { it.status.isGranted }

    // 지도 상태
    val seoulCityHall = LatLng(37.5665, 126.9770)
    val cameraPositionState: CameraPositionState = rememberGoogleCameraPositionState {
        position = CameraPosition.fromLatLngZoom(seoulCityHall, 15f)
    }

    val searchResultsState by viewModel.searchResults.collectAsState()

    val places = (searchResultsState as? SearchState.Success)?.results

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 1. 지도 영역
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f) // 전체 높이의 50%
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = GoogleMapProperties(
                    isMyLocationEnabled = granted
                ),
                uiSettings = GoogleMapUiSettings(
                    myLocationButtonEnabled = granted,
                    zoomControlsEnabled = false
                )
            ) {
                places?.forEach { place ->
                    val lat = place.geometry?.location?.lat
                    val lng = place.geometry?.location?.lng

                    if (lat != null && lng != null) {
                        val position = LatLng(lat, lng)
                        Marker(
                            state = rememberMarkerState(position = position),
                            title = place.name ?: "이름 없음",
                            snippet = place.formattedAddress ?: "주소 없음"
                        )
                    }
                }
            }
        }

        // 2. 정렬 토글 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            SortToggle(sortType = sortType, onSortChange = { sortType = it })
        }

        // 3. 장소 리스트
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f) // 나머지 50% 공간 차지
                .padding(horizontal = 16.dp)
                .background(
                    Color.White,
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .border(
                    1.dp,
                    Color(0xFFE0E0E0),
                    RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)
                )
                .padding(vertical = 8.dp)
        ) {
            when (searchResultsState) {
                is SearchState.Idle -> {
                    Text("장소를 검색해주세요.", modifier = Modifier.padding(16.dp))
                }

                is SearchState.Loading -> {
                    Text("검색 중...", modifier = Modifier.padding(16.dp))
                }

                is SearchState.Success -> {
                    val sortedPlaces = when (sortType) {
                        "평점순" -> (places ?: emptyList()).sortedByDescending { it.rating ?: 0.0 }
                        "거리순" -> places ?: emptyList() // TODO: 실제 거리순 정렬 로직 구현 필요
                        else -> places ?: emptyList()
                    }

                    if (sortedPlaces.isEmpty()) {
                        Text("검색 결과가 없습니다.", modifier = Modifier.padding(16.dp))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(sortedPlaces) { place ->
                                PlaceListItem(place = place) {
                                    // 장소 클릭 시 동작 (예: 상세 화면으로 이동)
                                }
                            }
                        }
                    }
                }

                is SearchState.NoResults -> {
                    Text("검색 결과가 없습니다.", modifier = Modifier.padding(16.dp))
                }

                is SearchState.Error -> {
                    Text(
                        "오류: ${(searchResultsState as SearchState.Error).message}",
                        modifier = Modifier.padding(16.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun PlaceListItem(place: PlaceResult, onClick: (PlaceResult) -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(place) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(text = place.name ?: "이름 없음")
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = place.formattedAddress ?: "주소 없음")
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Text(text = "평점: ${place.rating ?: "N/A"}", modifier = Modifier.weight(1f))
                Text(text = "리뷰: ${place.userRatingsTotal ?: 0}개")
            }
        }
    }
}

// SortToggle 컴포저블
@Composable
fun SortToggle(sortType: String, onSortChange: (String) -> Unit) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5F5F5))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
    ) {
        val types = listOf("거리순", "평점순")
        types.forEach { type ->
            val isSelected = sortType == type
            TextButton(
                onClick = { onSortChange(type) },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = if (isSelected) Color(0xFFFFF5E3) else Color.Transparent,
                    contentColor = Color.Black
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .defaultMinSize(minWidth = 0.dp, minHeight = 0.dp)
                    .padding(horizontal = 4.dp)
            ) {
                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = null,
                        tint = Color(0xFF222222),
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(type)
            }
        }
    }
}