package com.example.alwaysbewithyou.presentation.map

import android.Manifest
import android.content.Context
import android.util.Log
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
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.alwaysbewithyou.presentation.map.api.GooglePlacesApiService.PlaceResult
import com.example.alwaysbewithyou.presentation.map.viewmodel.MapViewModel
import com.example.alwaysbewithyou.presentation.map.viewmodel.SearchState
import com.example.alwaysbewithyou.presentation.navigation.Route
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberMultiplePermissionsState
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import kotlin.collections.sortedByDescending
import com.google.android.gms.maps.model.CameraPosition
import com.google.android.gms.maps.model.LatLng
import com.google.android.libraries.places.api.Places
import com.google.android.libraries.places.api.model.Place
import com.google.android.libraries.places.api.net.FetchPlaceRequest
import com.google.maps.android.compose.GoogleMap
import com.google.maps.android.compose.MapProperties as GoogleMapProperties
import com.google.maps.android.compose.MapUiSettings as GoogleMapUiSettings
import com.google.maps.android.compose.rememberCameraPositionState as rememberGoogleCameraPositionState
import com.google.maps.android.compose.CameraPositionState
import com.google.maps.android.compose.Marker
import com.google.maps.android.compose.rememberMarkerState
import android.location.Location as AndroidLocation // 충돌 방지를 위해 alias 사용

fun fetchPlaceDetails(
    context: Context,
    placeId: String,
    onSuccess: (Place) -> Unit,
    onError: (Exception) -> Unit
) {
    val placesClient = Places.createClient(context)
    val placeFields = listOf(
        Place.Field.ID,
        Place.Field.ADDRESS
    )
    val request = FetchPlaceRequest.newInstance(placeId, placeFields)

    placesClient.fetchPlace(request)
        .addOnSuccessListener { response -> onSuccess(response.place) }
        .addOnFailureListener { exception -> onError(exception) }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun MapListScreen(
    viewModel: MapViewModel,
    onNavigateBack: () -> Unit,
    navController: NavHostController,
    currentLocation: LatLng?
) {
    var sortType by remember { mutableStateOf("거리순") }
    val context = LocalContext.current

    val permissionState = rememberMultiplePermissionsState(
        listOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION
        )
    )

    val granted = permissionState.permissions.any { it.status.isGranted }

    val defaultLocation = LatLng(37.5665, 126.9770)
    val cameraPositionState: CameraPositionState = rememberGoogleCameraPositionState {
        position = CameraPosition.fromLatLngZoom(currentLocation ?: defaultLocation, 15f)
    }

    val searchResultsState by viewModel.searchResults.collectAsState()

    val places = (searchResultsState as? SearchState.Success)?.results

    LaunchedEffect(places) {
        if (!places.isNullOrEmpty()) {
            val avgLat = places.mapNotNull { it.geometry?.location?.lat }.average()
            val avgLng = places.mapNotNull { it.geometry?.location?.lng }.average()
            val center = LatLng(avgLat, avgLng)
            cameraPositionState.animate(
                update = CameraUpdateFactory.newLatLngZoom(center, 14f),
                durationMs = 1000
            )
        }
    }
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
    ) {
        // 지도
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
                            snippet = place.formattedAddress ?: "주소 없음",
                            icon = BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_BLUE)
                        )
                        Log.d("MapDebug", "Address: ${place.formattedAddress}")
                    }
                }
                currentLocation?.let {
                    Marker(
                        state = rememberMarkerState(position = it),
                        title = "현재 위치",
                        snippet = "내 위치"
                    )
                }
            }
        }

        // 정렬 토글 버튼
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.End
        ) {
            SortToggle(sortType = sortType, onSortChange = { sortType = it })
        }

        // 장소 리스트
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
                        "거리순" -> {
                            currentLocation?.let { currentLoc ->
                                (places ?: emptyList()).sortedBy { place ->
                                    val placeLat = place.geometry?.location?.lat
                                    val placeLng = place.geometry?.location?.lng
                                    if (placeLat != null && placeLng != null) {
                                        val results = FloatArray(1)
                                        AndroidLocation.distanceBetween(
                                            currentLoc.latitude, currentLoc.longitude,
                                            placeLat, placeLng,
                                            results
                                        )
                                        results[0]
                                    } else {
                                        Float.MAX_VALUE
                                    }
                                }
                            } ?: (places ?: emptyList())
                        }
                        else -> places ?: emptyList()
                    }

                    if (sortedPlaces.isEmpty()) {
                        Text("검색 결과가 없습니다.", modifier = Modifier.padding(16.dp))
                    } else {
                        LazyColumn(modifier = Modifier.fillMaxSize()) {
                            items(sortedPlaces) { place ->
                                PlaceListItem(place = place) { selectedPlace ->
                                    selectedPlace.placeId?.let { placeId ->
                                        navController.navigate(
                                            Route.MapDetail.createRouteWithStart(
                                                placeId,
                                                currentLocation?.latitude?.toFloat() ?: 37.5408f,
                                                currentLocation?.longitude?.toFloat() ?: 127.0793f
                                            )
                                        )
                                    }
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
fun PlaceListItem(
    place: PlaceResult,
    onClick: (PlaceResult) -> Unit
) {
    val context = LocalContext.current
    var detailedAddress by remember { mutableStateOf<String?>(null) }

    LaunchedEffect(place.placeId) {
        place.placeId?.let { placeId ->
            fetchPlaceDetails(
                context = context,
                placeId = placeId,
                onSuccess = { detailedPlace ->
                    detailedAddress = detailedPlace.address
                },
                onError = { error ->
                    Log.e("PlaceListItem", "주소 가져오기 실패: ${error.message}")
                }
            )
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp)
            .clickable { onClick(place) }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Text(
                text = place.name ?: "",
                fontWeight = FontWeight.Bold,
                fontSize = 18.sp
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = detailedAddress ?: "주소 불러오는 중...",
                fontSize = 14.sp,
                color = Color.Gray
            )
            place.rating?.let { rating ->
                Spacer(modifier = Modifier.height(4.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Filled.Star,
                        contentDescription = "평점 별",
                        tint = Color.Red,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "$rating", fontSize = 14.sp, color = Color.Gray)
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
