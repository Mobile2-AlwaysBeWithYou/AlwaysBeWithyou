package com.example.alwaysbewithyou.presentation.call

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.alwaysbewithyou.R
import com.example.alwaysbewithyou.data.viewmodel.DatabaseViewModel
import com.example.dbtest.data.Consultation
import com.google.firebase.auth.FirebaseAuth
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ReviewScreen(
    navController: NavHostController,
    databaseViewModel: DatabaseViewModel
) {
    // 현재 사용자 정보
    val currentUser by databaseViewModel.user.collectAsState()
    val consultations by databaseViewModel.consultations.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // 데이터 로드
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            databaseViewModel.loadUser(currentUserId)
            databaseViewModel.loadConsultations(currentUserId)
        }
    }

    // 날짜순으로 정렬 (최신순)
    val sortedConsultations = consultations.sortedByDescending {
        it.requested_at?.seconds ?: 0
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFF8F9FA))
    ) {
        // Top App Bar

        Box(
            modifier = Modifier
                .height(70.dp)
                .fillMaxWidth()
                .background(Color(0xFFE8E8E8)),
            contentAlignment = Alignment.CenterStart
        ){
            IconButton(onClick = { navController.popBackStack() }) {
                Image(
                    painter = painterResource(R.drawable.arrow_back),
                    contentDescription = "arrow back"
                )
            }
            Text("       신청기록", fontSize = 20.sp,fontWeight = FontWeight.Medium)
        }

        // Main Content
        if (consultations.isEmpty()) {
            // 데이터가 없을 때
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center
            ) {
                Image(
                    painter = painterResource(R.drawable.calendar_today),
                    contentDescription = "empty state",
                    modifier = Modifier.size(64.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "아직 신청 기록이 없습니다",
                    fontSize = 16.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                Text(
                    text = "상담을 신청해보세요",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(24.dp))

                Button(
                    onClick = { navController.navigate("schedule") },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4FC3F7)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "상담 신청하기",
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        } else {
            // 데이터가 있을 때
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White)
                    .padding(16.dp)
            ) {
                // 사용자 정보 카드
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color(0xFFF0F8FF)
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // 프로필 이미지
                        Box(
                            modifier = Modifier
                                .size(60.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF4FC3F7))
                        ) {
                            Image(
                                painter = painterResource(R.drawable.profile_image),
                                contentDescription = "profile image",
                                modifier = Modifier.fillMaxSize(),
                                contentScale = ContentScale.Crop
                            )
                        }

                        Spacer(modifier = Modifier.width(16.dp))

                        Column {
                            Text(
                                text = "신청기록 조회",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black
                            )

                            Spacer(modifier = Modifier.height(4.dp))

                            Row {
                                Text(
                                    text = "사용자: ",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = currentUser?.name ?: "**",
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            }

                            Row {
                                Text(
                                    text = "성별: ",
                                    fontSize = 14.sp,
                                    color = Color.Gray
                                )
                                Text(
                                    text = currentUser?.gender ?: "**",
                                    fontSize = 14.sp,
                                    color = Color.Black
                                )
                            }
                        }
                    }
                }

                // 신청 기록 목록
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = Color.White
                    ),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp)
                    ) {
                        // 헤더
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(bottom = 16.dp),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "NO.",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = "날짜",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.weight(2f),
                                textAlign = TextAlign.Center
                            )
                            Text(
                                text = "상태",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.weight(1f),
                                textAlign = TextAlign.End
                            )
                        }

                        // 구분선
                        Divider(
                            color = Color(0xFFE0E0E0),
                            thickness = 1.dp,
                            modifier = Modifier.padding(bottom = 12.dp)
                        )

                        // 신청 기록 리스트
                        LazyColumn(
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            itemsIndexed(sortedConsultations) { index, consultation ->
                                ConsultationItem(
                                    number = String.format("%02d", index + 1),
                                    consultation = consultation,
                                    onClick = {

                                    }
                                )
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                // 새 신청 버튼
                Button(
                    onClick = { navController.navigate("schedule") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFF4FC3F7)
                    ),
                    shape = RoundedCornerShape(24.dp)
                ) {
                    Text(
                        text = "새 상담 신청하기",
                        fontSize = 16.sp,
                        color = Color.White,
                        fontWeight = FontWeight.Medium
                    )
                }
            }
        }
    }
}

@Composable
fun ConsultationItem(
    number: String,
    consultation: Consultation,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = number,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        Text(
            text = try {
                LocalDate.parse(consultation.date).format(
                    DateTimeFormatter.ofPattern("yyyy. MM. dd")
                )
            } catch (e: Exception) {
                consultation.date
            },
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.weight(2f),
            textAlign = TextAlign.Center
        )

        // 상태 표시
        Box(
            modifier = Modifier
                .weight(1f),
            contentAlignment = Alignment.CenterEnd
        ) {
            Box(
                modifier = Modifier
                    .background(
                        color = when(consultation.status) {
                            "pending" -> Color(0xFFFFF3E0)
                            "approved" -> Color(0xFFE8F5E8)
                            "completed" -> Color(0xFFE3F2FD)
                            "cancelled" -> Color(0xFFFFEBEE)
                            else -> Color(0xFFF5F5F5)
                        },
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(horizontal = 8.dp, vertical = 4.dp)
            ) {
                Text(
                    text = when(consultation.status) {
                        "pending" -> "대기"
                        "approved" -> "승인"
                        "completed" -> "완료"
                        "cancelled" -> "취소"
                        else -> consultation.status
                    },
                    fontSize = 12.sp,
                    color = when(consultation.status) {
                        "pending" -> Color(0xFFFF8F00)
                        "approved" -> Color(0xFF4CAF50)
                        "completed" -> Color(0xFF2196F3)
                        "cancelled" -> Color(0xFFF44336)
                        else -> Color.Gray
                    },
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}