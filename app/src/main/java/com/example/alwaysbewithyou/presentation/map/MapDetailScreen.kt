package com.example.alwaysbewithyou.presentation.map

import android.content.Context
import android.content.pm.PackageManager
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.alwaysbewithyou.data.viewmodel.DatabaseViewModel
import com.example.alwaysbewithyou.presentation.map.viewmodel.MapDetailViewModel
import com.example.alwaysbewithyou.presentation.map.viewmodel.PlaceDetailState
import com.google.android.gms.maps.model.LatLng

@Composable
fun MapDetailScreen(
    placeId: String?,
    modifier: Modifier = Modifier,
    mapDetailViewModel: MapDetailViewModel,
    onNavigateBack: () -> Unit,
    onFindRouteClick: (LatLng) -> Unit,
    databaseViewModel: DatabaseViewModel
) {
    val placeDetailState by mapDetailViewModel.placeDetail.collectAsState()

    val fontSize by databaseViewModel.fontSizeEnum.collectAsState()

    LaunchedEffect(placeId) {
        if (placeId != null) {
            mapDetailViewModel.loadPlaceDetails(placeId)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color.White)
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
                fontSize = fontSize.buttonSize,
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 장소 이름
        when (placeDetailState) {
            is PlaceDetailState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
            is PlaceDetailState.Error -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("오류: ${(placeDetailState as PlaceDetailState.Error).message}", color = Color.Red)
                }
            }
            is PlaceDetailState.Success -> {
                val place = (placeDetailState as PlaceDetailState.Success).place

                val photoRef = place.photos?.firstOrNull()?.photoReference
                if (photoRef != null) {
                    val imageUrl = mapDetailViewModel.getPhotoUrl(photoRef)
                    Log.d("MapDetailScreen", "Image URL: $imageUrl")

                    AsyncImage(
                        model = imageUrl,
                        contentDescription = "장소 사진",
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(8.dp)),
                        contentScale = ContentScale.Crop,
                        onError = {
                            Log.e("MapDetailScreen", "이미지 로딩 실패: ${it.result.throwable}")
                        }
                    )
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(200.dp)
                            .padding(horizontal = 16.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(Color(0xFFE0E0E0)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("사진 없음", color = Color.Gray)
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 장소 이름
                Text(
                    text = place.name ?: "이름 없음",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                // 평점
                place.rating?.let { rating ->
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 16.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = "평점 별 아이콘",
                            tint = Color(0xFFFFD700),
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "$rating (${place.userRatingsTotal ?: 0}명)",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // 상세 주소
                Text(
                    text = place.formattedAddress ?: "주소 정보 없음",
                    fontSize = 16.sp,
                    color = Color.DarkGray,
                    modifier = Modifier.padding(horizontal = 16.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 상세 정보 (전화번호, 웹사이트, 운영 시간 등)
                Column(modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .padding(horizontal = 16.dp)) {

                    place.internationalPhoneNumber?.let { phone ->
                        Text(text = "전화번호: $phone", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    place.website?.let { website ->
                        Text(text = "웹사이트: $website", fontSize = 14.sp)
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                    place.openingHours?.let { hours ->
                        Text(text = "영업 시간:", fontSize = 14.sp, fontWeight = FontWeight.SemiBold)
                        hours.forEach { hour ->
                            Text(text = hour, fontSize = 14.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                    }
                }

                // 길찾기 버튼
                Button(
                    onClick = {
                        val destinationLatLng = place.geometry?.location?.let { loc ->
                            LatLng(loc.lat ?: 0.0, loc.lng ?: 0.0)
                        } ?: LatLng(0.0, 0.0)

                        Log.d("MapDetailScreen", "Destination LatLng to pass: $destinationLatLng")

                        onFindRouteClick(destinationLatLng)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(70.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Text(text = "길찾기", fontSize = fontSize.buttonSize)
                }
            }
            is PlaceDetailState.Error -> {
                Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text("오류: ${(placeDetailState as PlaceDetailState.Error).message}")
                }
            }
            PlaceDetailState.Idle -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text("장소 상세 정보를 로드 중...")
                }
            }
        }
    }
}