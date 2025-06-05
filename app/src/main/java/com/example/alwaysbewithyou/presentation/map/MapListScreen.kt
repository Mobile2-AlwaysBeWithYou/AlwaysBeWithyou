package com.example.alwaysbewithyou.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.naver.maps.map.compose.ExperimentalNaverMapApi
import com.naver.maps.map.compose.LocationTrackingMode
import com.naver.maps.map.compose.MapProperties
import com.naver.maps.map.compose.MapUiSettings
import com.naver.maps.map.compose.NaverMap
import com.naver.maps.map.compose.rememberCameraPositionState
import com.naver.maps.map.compose.rememberFusedLocationSource
import androidx.compose.runtime.collectAsState
import com.example.alwaysbewithyou.presentation.map.tools.MapViewModel
import com.example.alwaysbewithyou.presentation.map.tools.SearchState
import com.example.alwaysbewithyou.presentation.map.tools.PlaceItem

@OptIn(ExperimentalNaverMapApi::class, ExperimentalPermissionsApi::class)
@Composable
fun MapListScreen(
    viewModel: MapViewModel,
    onNavigateBack: () -> Unit
) {
    var sortType by remember { mutableStateOf("거리순") }

    // 권한
    val permissionState = rememberMultiplePermissionsState(
        listOf(
            android.Manifest.permission.ACCESS_FINE_LOCATION,
            android.Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val granted = permissionState.permissions.any { it.status.isGranted }

    // 지도 상태
    val cameraPositionState = rememberCameraPositionState()
    val locationSource = rememberFusedLocationSource()

    val searchResultsState by viewModel.searchResults.collectAsState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 지도 (상단 50% 정도의 공간 차지)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f) // 전체 높이의 50%
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            if (granted) {
                NaverMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    locationSource = locationSource,
                    properties = MapProperties(
                        locationTrackingMode = LocationTrackingMode.Face
                    ),
                    uiSettings = MapUiSettings(
                        isLocationButtonEnabled = true
                    )
                )
            } else {
                NaverMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState
                )
            }
        }

        // 정렬 토글 버튼 (지도 아래 바로 배치)
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            SortToggle(sortType = sortType, onSortChange = { sortType = it })
        }

        // 장소 리스트 (하단 나머지 공간 차지)
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f) // 나머지 50% 공간 차지
                .padding(horizontal = 16.dp)
                .background(Color.White, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp)) // 상단만 둥글게
                .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                .padding(vertical = 8.dp)
        ) {
            when (searchResultsState) {
                is SearchState.Success -> {
                    val results = (searchResultsState as SearchState.Success).results
                    if (results.isNotEmpty()) {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(results) { placeItem ->
                                Column(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(vertical = 8.dp)
                                ) {
                                    Text(
                                        text = placeItem.title?.replace("<b>", "")?.replace("</b>", "") ?: "제목 없음",
                                        color = Color.Blue
                                    )
                                    Text(text = "${placeItem.address ?: "주소 없음"}", color = Color.DarkGray)
                                    // 더 많은 정보 표시 가능
                                }
                            }
                        }
                    } else {
                        Text(text = "검색 결과가 없습니다.", modifier = Modifier.padding(8.dp))
                    }
                }
                SearchState.NoResults -> {
                    Text(text = "검색 결과가 없습니다.", modifier = Modifier.padding(8.dp))
                }
                SearchState.Loading -> {
                    Text(text = "검색 결과를 불러오는 중...", modifier = Modifier.padding(8.dp))
                }
                is SearchState.Error -> {
                    Text(text = "오류: ${(searchResultsState as SearchState.Error).message}", color = Color.Red, modifier = Modifier.padding(8.dp))
                }
                SearchState.Idle -> {
                    Text(text = "장소를 검색해 주세요.", modifier = Modifier.padding(8.dp))
                }
            }
        }
    }
}


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
