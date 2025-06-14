package com.example.alwaysbewithyou.presentation.map

import androidx.compose.foundation.background
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
            cameraPositionState.move(CameraUpdateFactory.newCameraPosition(
                CameraPosition.fromLatLngZoom(startLocation, 14.0f)
            ))
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("경로 안내") },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "뒤로가기"
                        )
                    }
                }
            )
        },contentWindowInsets = WindowInsets(0.dp)
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize(1f)
                .padding(innerPadding)
        ) {
            // 지도 부분
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
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
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                TransportType.entries.forEach { type ->
                    Button(
                        onClick = {
                            selectedTransportType = type
                            selectedRouteIndex = 0
                        },
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (selectedTransportType == type) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.surfaceVariant,
                            contentColor = if (selectedTransportType == type) MaterialTheme.colorScheme.onPrimary else MaterialTheme.colorScheme.onSurfaceVariant
                        ),
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(type.getDisplayName())
                    }
                }
            }

            // 경로 목록
            LazyColumn(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(0.5f)
                    .clip(RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
                    .background(MaterialTheme.colorScheme.surface)
            ) {
                val sortedRoutes = routes.sortedBy { it.durationValue }

                if (sortedRoutes.isEmpty() && !isLoading && error == null) {
                    item {
                        Text(
                            text = "경로를 찾을 수 없습니다.",
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(16.dp),
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                        )
                    }
                } else {
                    items(sortedRoutes.withIndex().toList()) { (index, route) ->
                        val isSelected = index == selectedRouteIndex
                        RouteItem(
                            route = route,
                            index = index + 1,
                            isSelected = isSelected,
                            onClick = {
                                selectedRouteIndex = index
                            }
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                    }
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
            .clickable(onClick = onClick),
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (isSelected) MaterialTheme.colorScheme.primary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "$index 번 경로",
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