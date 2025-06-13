package com.example.alwaysbewithyou.presentation.call

import android.R.attr.label
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
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
import java.util.UUID

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ResultScreen(
    navController: NavHostController,
    databaseViewModel: DatabaseViewModel
) {
    // 현재 사용자 정보
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val consultations by databaseViewModel.consultations.collectAsState()

    // 최신 상담 신청 찾기
    val latestConsultation = consultations.maxByOrNull { it.requested_at?.seconds ?: 0 }

    // 아직랜덤 생성
    val reservationNumber = remember { "NO.${String.format("%03d", (1..999).random())}" }

    // 상담 데이터 로드
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            databaseViewModel.loadConsultations(currentUserId)
        }
    }

    MaterialTheme {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF8F9FA))
        ) {
            TopAppBar(
                title = {
                    Text(
                        text = "상담 신청",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Image(
                            painter = painterResource(R.drawable.arrow_back),
                            contentDescription = "arrow back"
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFFE8E8E8)
                )
            )

            // Main Content
            if (latestConsultation != null) {
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    Spacer(modifier = Modifier.height(40.dp))

                    // Success Message Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFF0F8FF)
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 2.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(24.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // 성공 아이콘
                            Text(
                                text = "✓",
                                fontSize = 32.sp,
                                color = Color(0xFF4CAF50),
                                fontWeight = FontWeight.Bold
                            )

                            Spacer(modifier = Modifier.height(12.dp))

                            Text(
                                text = "상담 신청을 완료했습니다!",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color.Black,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = "24시간 내에 연락드리겠습니다!",
                                fontSize = 14.sp,
                                color = Color(0xFF666666),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    // Details Section
                    Text(
                        text = "자세한 정보",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )

                    // Reservation Details Card
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 32.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(
                            containerColor = Color(0xFFFAFAFA)
                        ),
                        elevation = CardDefaults.cardElevation(
                            defaultElevation = 1.dp
                        )
                    ) {
                        Column(
                            modifier = Modifier.padding(20.dp)
                        ) {
                            DetailRow(
                                label = "예약번호:",
                                value = reservationNumber
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            DetailRow(
                                label = "상담날짜:",
                                value = try {
                                    LocalDate.parse(latestConsultation.date).format(
                                        DateTimeFormatter.ofPattern("yyyy년 MM월 dd일")
                                    )
                                } catch (e: Exception) {
                                    latestConsultation.date
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            DetailRow(
                                label = "신청상태:",
                                value = when(latestConsultation.status) {
                                    "pending" -> "접수 완료"
                                    "approved" -> "승인됨"
                                    "completed" -> "완료"
                                    "cancelled" -> "취소됨"
                                    else -> latestConsultation.status
                                },
                                valueColor = when(latestConsultation.status) {
                                    "pending" -> Color(0xFF4FC3F7)
                                    "approved" -> Color(0xFF4CAF50)
                                    "completed" -> Color(0xFF2196F3)
                                    "cancelled" -> Color(0xFFF44336)
                                    else -> Color.Black
                                }
                            )

                            Spacer(modifier = Modifier.height(16.dp))

                            Column {
                                Text(
                                    text = "상담개요:",
                                    fontSize = 14.sp,
                                    color = Color(0xFF666666),
                                    modifier = Modifier.padding(bottom = 8.dp)
                                )

                                Text(
                                    text = latestConsultation.content.ifEmpty { "내용 없음" },
                                    fontSize = 14.sp,
                                    color = Color.Black,
                                    lineHeight = 20.sp
                                )
                            }
                        }
                    }

                    // Action Buttons
                    ActionButton(
                        text = "상담 기록 조회",
                        onClick = {
                            // 상담 기록 조회 페이지로 이동
                            navController.navigate("review")
                        }
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val context = LocalContext.current
                    ActionButton(
                        text = "전화 요청 가기",
                        onClick = {
                            val intent = Intent(Intent.ACTION_VIEW, Uri.parse("https://www.1661-2129.or.kr/"))
                            context.startActivity(intent)
                        }
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // 홈으로 가기 버튼
                    Button(
                        onClick = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4FC3F7)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = "홈으로 돌아가기",
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            } else {
                // 로딩 상태 또는 데이터 없음
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(16.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    if (consultations.isEmpty()) {
                        CircularProgressIndicator(
                            color = Color(0xFF4FC3F7),
                            modifier = Modifier.size(48.dp)
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Text(
                            text = "상담 신청 정보를 불러오는 중...",
                            fontSize = 16.sp,
                            color = Color.Gray
                        )
                    } else {
                        Text(
                            text = "상담 신청 정보를 찾을 수 없습니다.",
                            fontSize = 16.sp,
                            color = Color.Gray,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(16.dp))
                        Button(
                            onClick = { navController.popBackStack() },
                            colors = ButtonDefaults.buttonColors(
                                containerColor = Color(0xFF4FC3F7)
                            )
                        ) {
                            Text("돌아가기", color = Color.White)
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DetailRow(
    label: String,
    value: String,
    valueColor: Color = Color.Black
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            color = Color(0xFF666666),
            modifier = Modifier.width(80.dp)
        )

        Text(
            text = value,
            fontSize = 14.sp,
            color = valueColor,
            fontWeight = if (valueColor != Color.Black) FontWeight.Medium else FontWeight.Normal,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth(),
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color(0xFFF5F5F5)
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = text,
                fontSize = 16.sp,
                color = Color.Black,
                fontWeight = FontWeight.Medium
            )

            Button(
                onClick = onClick,
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF4FC3F7)
                ),
                shape = RoundedCornerShape(20.dp),
                modifier = Modifier
                    .width(60.dp)
                    .height(32.dp),
                contentPadding = PaddingValues(0.dp)
            ) {
                Text(
                    text = "Go",
                    fontSize = 12.sp,
                    color = Color.White,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}