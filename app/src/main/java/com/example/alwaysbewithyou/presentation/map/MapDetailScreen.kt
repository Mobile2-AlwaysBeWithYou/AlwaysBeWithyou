package com.example.alwaysbewithyou.presentation.map

import android.R.attr.rating
import android.R.attr.text
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alwaysbewithyou.R
import com.example.alwaysbewithyou.presentation.map.tools.MapDetailViewModel
import com.example.alwaysbewithyou.presentation.map.tools.PlaceDetailState

@Composable
fun MapDetailScreen(
    placeId: String?,
    modifier: Modifier = Modifier,
    mapDetailViewModel: MapDetailViewModel,
    onNavigateBack: () -> Unit,
    onFindRouteClick: () -> Unit
) {

    val placeDetailState by mapDetailViewModel.placeDetail.collectAsState()

    LaunchedEffect(placeId) {
        if (placeId != null) {
            mapDetailViewModel.loadPlaceDetails(placeId)
        }
    }
    val placeName = "선택된 장소 이름 (${placeId ?: "ID 없음"})"
    val rating = 4.5
    val address = "선택된 장소의 상세 주소 (데이터 로드 예정)"
    val details = "이곳은 ${placeId ?: "ID 없음"}에 대한 상세 정보입니다. 더 많은 정보가 여기에 표시될 수 있습니다. (TODO: 실제 데이터 로드)"

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
                style = MaterialTheme.typography.titleLarge,
                modifier = Modifier.padding(start = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(16.dp))

        // 장소 사진 (현재 보류)
//        Box(
//            modifier = Modifier
//                .fillMaxWidth()
//                .height(200.dp)
//                .padding(horizontal = 16.dp)
//                .clip(RoundedCornerShape(8.dp))
//                .background(Color(0xFFE0E0E0)),
//            contentAlignment = Alignment.Center
//        ) {
//            Icon(
//                painter = painterResource(id = R.drawable.ic_image_placeholder),
//                contentDescription = "장소 사진",
//                modifier = Modifier.size(64.dp),
//                tint = Color(0xFFAAAAAA)
//            )
//        }

        Spacer(modifier = Modifier.height(16.dp))

        // 장소 이름
        when (placeDetailState) {
            is PlaceDetailState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator() // 로딩 인디케이터
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

                // 장소 사진 Placeholder (현재 보류)
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .padding(horizontal = 16.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(Color(0xFFE0E0E0)),
                    contentAlignment = Alignment.Center
                ) {
//                    Icon(
//                        painter = painterResource(id = R.drawable.ic_image_placeholder),
//                        contentDescription = "장소 사진",
//                        modifier = Modifier.size(64.dp),
//                        tint = Color(0xFFAAAAAA)
//                    )
                    // TODO: 실제 장소 사진 로드 로직 (Coil, Glide 등 라이브러리 사용)
                    // if (place.photos?.isNotEmpty() == true) {
                    //    AsyncImage(
                    //        model = "https://maps.googleapis.com/maps/api/place/photo?maxwidth=400&photoreference=${place.photos[0].photoReference}&key=YOUR_API_KEY",
                    //        contentDescription = "장소 사진",
                    //        modifier = Modifier.fillMaxSize(),
                    //        contentScale = ContentScale.Crop
                    //    )
                    // }
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
                    // TODO: 더 많은 상세 정보 (리뷰 등) 추가
                }

                // 길찾기 버튼
                Button(
                    onClick = onFindRouteClick,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(56.dp)
                        .padding(horizontal = 16.dp, vertical = 8.dp)
                        .clip(RoundedCornerShape(8.dp))
                ) {
                    Text(text = "길찾기", fontSize = 18.sp)
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