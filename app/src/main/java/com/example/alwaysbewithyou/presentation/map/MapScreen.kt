package com.example.alwaysbewithyou.presentation.map

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.rememberCameraPositionState
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import kotlinx.coroutines.tasks.await
import timber.log.Timber
import com.example.alwaysbewithyou.presentation.map.viewmodel.MapViewModel
import com.example.alwaysbewithyou.presentation.map.viewmodel.SearchState
import androidx.compose.runtime.collectAsState
import com.google.maps.android.compose.MapProperties
import com.google.maps.android.compose.MapUiSettings
import androidx.core.content.ContextCompat

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapScreen(
    viewModel: MapViewModel,
    onNavigateToMapList: (Float, Float) -> Unit,
    onPlaceClick: (String, LatLng?) -> Unit
) {
    val searchState by viewModel.searchResults.collectAsState()
    val searchText by viewModel.searchQuery.collectAsState()

    val context = LocalContext.current
    val fusedLocationClient: FusedLocationProviderClient = remember {
        LocationServices.getFusedLocationProviderClient(context)
    }

    // 현재 위치 상태
    var currentLocation by remember { mutableStateOf<LatLng?>(null) }

    // 위치 권한 상태
    val locationPermissionsState = rememberMultiplePermissionsState(
        permissions = listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val requestPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        if (permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
            permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        ) {
            Timber.d("위치 권한 승인됨")
            if (ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED ||
                ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                fusedLocationClient.lastLocation.addOnSuccessListener { location: Location? ->
                    location?.let {
                        currentLocation = LatLng(it.latitude, it.longitude)
                        Timber.d("초기 위치 업데이트: ${it.latitude}, ${it.longitude}")
                    }
                }
            }
        } else {
            Timber.d("위치 권한 거부됨")
        }
    }

    LaunchedEffect(Unit) {
        if (locationPermissionsState.allPermissionsGranted) {
            try {
                if (ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_FINE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED ||
                    ContextCompat.checkSelfPermission(
                        context,
                        Manifest.permission.ACCESS_COARSE_LOCATION
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    val locationResult = fusedLocationClient.lastLocation.await()
                    locationResult?.let {
                        currentLocation = LatLng(it.latitude, it.longitude)
                        Timber.d("초기 위치 업데이트: ${it.latitude}, ${it.longitude}")
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "초기 위치 정보 가져오기 실패: ${e.localizedMessage}")
            }
        } else {
            requestPermissionLauncher.launch(
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                )
            )
        }
    }

    val singapore = LatLng(1.35, 103.87) // 위치 기본값
    val cameraPositionState = rememberCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation ?: singapore, 10f)
    }

    LaunchedEffect(currentLocation) {
        currentLocation?.let {
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(it, 15f),
                durationMs = 1000
            )
        }
    }

    Column(
        modifier = Modifier.fillMaxSize()
    ) {
        // 검색 입력 필드
        OutlinedTextField(
            value = searchText,
            onValueChange = { viewModel.updateSearchQuery(it) },
            label = { Text("장소 검색") },
            trailingIcon = {
                IconButton(onClick = {
                    viewModel.searchPlaces(searchText)
                    currentLocation?.let {
                        onNavigateToMapList(it.latitude.toFloat(), it.longitude.toFloat())
                    }
                }) {
                    Icon(Icons.Filled.Search, contentDescription = "검색")
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
            keyboardActions = KeyboardActions(onSearch = {
                viewModel.searchPlaces(searchText)
                currentLocation?.let {
                    onNavigateToMapList(it.latitude.toFloat(), it.longitude.toFloat())
                }
            })
        )

        // 지도 UI
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            if (locationPermissionsState.allPermissionsGranted) {
                GoogleMap(
                    modifier = Modifier.fillMaxSize(),
                    cameraPositionState = cameraPositionState,
                    properties = MapProperties(
                        isMyLocationEnabled = true
                    ),
                    uiSettings = MapUiSettings(
                        myLocationButtonEnabled = true,
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

            if (searchState is SearchState.Loading) {
                CircularProgressIndicator(modifier = Modifier.align(Alignment.Center))
            }
        }
    }
}