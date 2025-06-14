package com.example.alwaysbewithyou.presentation.map

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.alwaysbewithyou.presentation.map.viewmodel.MapRouteViewModel
import androidx.compose.material.icons.filled.Check
import androidx.compose.ui.unit.sp
import com.example.alwaysbewithyou.presentation.map.viewmodel.RouteState
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import com.google.maps.android.compose.Polyline
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.LatLngBounds
import com.google.android.gms.maps.model.CameraPosition

data class RouteInfo(
    val id: String,
    val durationText: String,
    val durationValue: Long,
    val polylinePoints: List<LatLng>,
    val transportType: TransportType
)

enum class TransportType {
    WALKING,
    TRANSIT,
    DRIVING;

    fun getDisplayName(): String {
        return when (this) {
            WALKING -> "도보"
            TRANSIT -> "대중교통"
            DRIVING -> "자가용"
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapRouteScreen(
    viewModel: MapRouteViewModel,
    startLocation: LatLng,
    endLocation: LatLng,
    initialTransportType: TransportType,
    onNavigateBack: () -> Unit
) {
    val context = LocalContext.current

    var selectedTransportType by remember { mutableStateOf(initialTransportType) }

    var selectedRouteIndex by remember { mutableStateOf(0) }

    val routes by viewModel.routes.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val error by viewModel.error.collectAsState()

    val cameraPositionState = rememberCameraPositionState()

    LaunchedEffect(startLocation, endLocation, selectedTransportType) {
        viewModel.findRoutes(startLocation, endLocation, selectedTransportType)
    }

    LaunchedEffect(routes, selectedRouteIndex) {
        if (routes.isNotEmpty() && selectedRouteIndex < routes.size) {
            val selectedRoute = routes[selectedRouteIndex]
            val polylinePoints = selectedRoute.polylinePoints

            if (polylinePoints.isNotEmpty()) {
                val boundsBuilder = LatLngBounds.Builder()
                boundsBuilder.include(startLocation)
                boundsBuilder.include(endLocation)
                polylinePoints.forEach { point ->
                    boundsBuilder.include(point)
                }
                val bounds = boundsBuilder.build()

                cameraPositionState.move(CameraUpdateFactory.newLatLngBounds(bounds, 100))
            }
        } else if (!isLoading && routes.isEmpty()) {
            cameraPositionState.move(
                CameraUpdateFactory.newCameraPosition(
                    CameraPosition.fromLatLngZoom(startLocation, 14.0f)
                )
            )
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize(1f)
    ) {
        // 뒤로가기 버튼 및 텍스트
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 16.dp, start = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = onNavigateBack) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "뒤로가기"
                )
            }
            Text(
                text = "뒤로가기",
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 4.dp)
            )
        }
        // 지도 부분
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
                .padding(horizontal = 12.dp, vertical = 8.dp)
                .clip(RoundedCornerShape(16.dp))
                .background(Color(0xFFF5F5F5))
        ) {
            GoogleMap(
                modifier = Modifier.fillMaxSize(),
                cameraPositionState = cameraPositionState,
                properties = MapProperties(
                    isMyLocationEnabled = false
                ),
                uiSettings = MapUiSettings(
                    myLocationButtonEnabled = false,
                    zoomControlsEnabled = false
                )
            ) {
                Marker(
                    state = rememberMarkerState(position = startLocation),
                    title = "출발"
                )
                Marker(
                    state = rememberMarkerState(position = endLocation),
                    title = "도착"
                )

                val currentSelectedRoute = routes.getOrNull(selectedRouteIndex)
                if (currentSelectedRoute != null && currentSelectedRoute.polylinePoints.isNotEmpty()) {
                    Polyline(
                        points = currentSelectedRoute.polylinePoints,
                        color = Color.Blue,
                        width = 10f
                    )
                }
            }


            if (isLoading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }

            error?.let {
                Text(
                    text = "오류: $it",
                    color = Color.Red,
                    modifier = Modifier.align(Alignment.Center)
                )
            }
        }

        // 이동 수단 탭
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            TransportTypeSelector(
                selectedTransportType = selectedTransportType,
                onTransportTypeSelected = { newType ->
                    selectedTransportType = newType
                    selectedRouteIndex = 0
                }
            )
        }

        // 경로 목록
        val routeState by viewModel.routeState.collectAsState()

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.5f)
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
            when (routeState) {
                is RouteState.Idle -> {
                    Text("경로를 검색해주세요.", modifier = Modifier.padding(16.dp))
                }

                is RouteState.Loading -> {
                    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        CircularProgressIndicator()
                    }
                }

                is RouteState.Success -> {
                    val currentRoutes = (routeState as RouteState.Success).routes
                    val sortedRoutes = currentRoutes.sortedBy { it.durationValue }

                    if (sortedRoutes.isEmpty()) {
                        Text("경로를 찾을 수 없습니다.", modifier = Modifier.padding(16.dp))
                    } else {
                        LazyColumn(
                            modifier = Modifier.fillMaxSize(),
                            contentPadding = PaddingValues(horizontal = 8.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            items(sortedRoutes.size) { index ->
                                val route = sortedRoutes[index]
                                val isSelected = index == selectedRouteIndex
                                RouteItem(
                                    route = route,
                                    index = index + 1,
                                    isSelected = isSelected,
                                    onClick = {
                                        selectedRouteIndex = index
                                    }
                                )
                            }
                        }
                    }
                }

                is RouteState.NoResults -> {
                    Text("경로를 찾을 수 없습니다.", modifier = Modifier.padding(16.dp))
                }

                is RouteState.Error -> {
                    Text(
                        "오류: ${(routeState as RouteState.Error).message}",
                        modifier = Modifier.padding(16.dp),
                        color = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}


@Composable
fun RouteItem(route: RouteInfo, index: Int, isSelected: Boolean, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .border(
                width = if (isSelected) 1.dp else 0.dp,
                color = if (isSelected) Color(0xFF2B5EBD) else Color.Transparent,
                shape = RoundedCornerShape(8.dp)
            ),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "$index 번 경로",
                fontSize = 18.sp,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "예상 소요 시간: ${route.durationText}",
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
fun TransportTypeSelector(
    selectedTransportType: TransportType,
    onTransportTypeSelected: (TransportType) -> Unit
) {
    val selectedButtonBgColor = Color(0xFFD1D1D6)
    val unselectedButtonBgColor = Color.Transparent
    val contentColor = Color.Black
    val iconTint = Color(0xFF222222)

    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(16.dp))
            .background(Color(0xFFF5F5F5))
            .border(1.dp, Color(0xFFE0E0E0), RoundedCornerShape(16.dp))
    ) {
        TransportType.entries.forEach { type ->
            val isSelected = selectedTransportType == type
            TextButton(
                onClick = { onTransportTypeSelected(type) },
                colors = ButtonDefaults.textButtonColors(
                    containerColor = if (isSelected) selectedButtonBgColor else unselectedButtonBgColor,
                    contentColor = contentColor
                ),
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier
                    .defaultMinSize(minWidth = 0.dp, minHeight = 0.dp)
                    .padding(horizontal = 4.dp)
            ) {

                if (isSelected) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "선택됨",
                        tint = iconTint,
                        modifier = Modifier.size(16.dp)
                    )
                }
                Text(
                    text = type.getDisplayName(),
                    style = MaterialTheme.typography.bodyLarge
                )

            }
        }
    }
}