package com.example.alwaysbewithyou.presentation.call

import android.R.attr.contentDescription
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.alwaysbewithyou.R
import java.time.LocalDate
import java.time.format.DateTimeFormatter


data class ConsultationRequest(
    val selectedDate: LocalDate?,
    val consultationText: String,
    val reservationNumber: String = "NO.01"
)

class ConsultationResultActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Intent에서 데이터 받기
        val selectedDateString = intent.getStringExtra("selectedDate")
        val consultationText = intent.getStringExtra("consultationText") ?: ""
        val selectedDate = selectedDateString?.let { LocalDate.parse(it) }

        val consultationRequest = ConsultationRequest(
            selectedDate = selectedDate,
            consultationText = consultationText
        )

        setContent {
            ConsultationResultScreen(consultationRequest = consultationRequest)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConsultationResultScreen(
    consultationRequest: ConsultationRequest = ConsultationRequest(
        selectedDate = LocalDate.of(2025, 5, 10),
        consultationText = "상담 상담을 하고 싶습니다."
    )
) {
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
                    IconButton(onClick = { /* Handle back navigation */ }) {
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

                // Reservation Details
                Column(
                    modifier = Modifier.padding(bottom = 32.dp)
                ) {
                    DetailRow(
                        label = "예약번호:",
                        value = consultationRequest.reservationNumber
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow(
                        label = "상담시간:",
                        value = consultationRequest.selectedDate?.format(
                            DateTimeFormatter.ofPattern("yyyy.MM.dd")
                        ) ?: "날짜 미선택"
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    DetailRow(
                        label = "상담개요:",
                        value = consultationRequest.consultationText.ifEmpty { "내용 없음" }
                    )
                }

                // Action Buttons
                ActionButton(
                    text = "상담 기록 조회",
                    onClick = { /* Handle consultation history */ }
                )

                Spacer(modifier = Modifier.height(12.dp))

                ActionButton(
                    text = "전화 요청 가기",
                    onClick = { /* Handle phone request */ }
                )

                Spacer(modifier = Modifier.weight(1f))
            }
        }
    }
}

@Composable
fun DetailRow(label: String, value: String) {
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
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
fun ActionButton(text: String, onClick: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(
                color = Color(0xFFF5F5F5),
                shape = RoundedCornerShape(8.dp)
            )
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
                containerColor = Color(0xFF333333)
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
                color = Color.White
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun ConsultationResultScreenPreview() {
    ConsultationResultScreen()
}