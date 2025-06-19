package com.example.alwaysbewithyou.presentation.call

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
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
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.YearMonth
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ScheduleScreen(
    navController: NavHostController,
    databaseViewModel: DatabaseViewModel
) {
    // 현재 사용자 정보
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""
    val consultations by databaseViewModel.consultations.collectAsState()

    // UI 상태
    var selectedDate by remember { mutableStateOf<LocalDate?>(null) }
    var currentYearMonth by remember { mutableStateOf(YearMonth.now()) }
    var consultationText by remember { mutableStateOf("") }
    var isSubmitting by remember { mutableStateOf(false) }

    // Snackbar 상태
    val snackbarHostState = remember { SnackbarHostState() }
    val coroutineScope = rememberCoroutineScope()

    // 상담 신청 데이터 로드
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            databaseViewModel.loadConsultations(currentUserId)
        }
    }

    // 상담 신청 함수
    fun submitConsultation() {
        if (selectedDate == null) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("날짜를 선택해주세요")
            }
            return
        }

        if (consultationText.isBlank()) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("상담 내용을 입력해주세요")
            }
            return
        }

        if (currentUserId.isEmpty()) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar("로그인이 필요합니다")
            }
            return
        }

        isSubmitting = true

        val consultation = Consultation(
            date = selectedDate!!.format(DateTimeFormatter.ISO_LOCAL_DATE),
            content = consultationText.trim(),
            status = "pending", // 대기중
            requested_at = com.google.firebase.Timestamp.now()
        )

        databaseViewModel.requestConsultation(currentUserId, consultation)

        // 성공 메시지 표시 후 결과 화면으로 이동
        coroutineScope.launch {
            snackbarHostState.showSnackbar("상담 신청이 완료되었습니다")
            navController.navigate("result")

            // 폼 초기화
            selectedDate = null
            consultationText = ""
            isSubmitting = false
        }
    }

    MaterialTheme {
        Box(modifier = Modifier.fillMaxSize()) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF8F9FA))
            ) {

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
                    Text("       상담/전화", fontSize = 20.sp,fontWeight = FontWeight.Medium)
                }


                // Main Content
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.White)
                        .padding(16.dp)
                ) {
                    // Title with Calendar Icon
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(bottom = 16.dp)
                    ) {
                        Image(
                            painter = painterResource(R.drawable.calendar_today),
                            contentDescription = "calendar icon"
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "상담 신청",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.Black
                        )
                    }

                    // Year/Month Navigation
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "${currentYearMonth.year}년",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black
                        )

                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            IconButton(
                                onClick = { currentYearMonth = currentYearMonth.minusMonths(1) }
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.keyboard_arrow_left),
                                    contentDescription = "arrow left"
                                )
                            }

                            Text(
                                text = "${currentYearMonth.monthValue}월",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.Medium,
                                color = Color.Black,
                                modifier = Modifier.padding(horizontal = 16.dp)
                            )

                            IconButton(
                                onClick = { currentYearMonth = currentYearMonth.plusMonths(1) }
                            ) {
                                Image(
                                    painter = painterResource(R.drawable.keyboard_arrow_right),
                                    contentDescription = "arrow right"
                                )
                            }
                        }
                    }

                    // Week Headers
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(bottom = 8.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        listOf("월", "화", "수", "목", "금", "토", "일").forEach { day ->
                            Text(
                                text = day,
                                fontSize = 14.sp,
                                color = Color.Gray,
                                textAlign = TextAlign.Center,
                                modifier = Modifier.weight(1f)
                            )
                        }
                    }

                    // Calendar Grid
                    val daysInMonth = generateCalendarDays(currentYearMonth)
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(7),
                        modifier = Modifier.padding(bottom = 24.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp),
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        items(daysInMonth.size) { index ->
                            val date = daysInMonth[index]
                            CalendarDay(
                                date = date,
                                isSelected = date == selectedDate,
                                onClick = { if (date != null && !date.isBefore(LocalDate.now())) selectedDate = date }
                            )
                        }
                    }

                    // Consultation Input Section
                    Text(
                        text = "상담 개요",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )

                    OutlinedTextField(
                        value = consultationText,
                        onValueChange = { consultationText = it },
                        placeholder = {
                            Text(
                                text = "상담 받고 싶은 내용을 자세히 적어주세요...",
                                color = Color.Gray
                            )
                        },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedContainerColor = Color(0xFFF5F5F5),
                            unfocusedContainerColor = Color(0xFFF5F5F5),
                            focusedBorderColor = Color(0xFF4FC3F7),
                            unfocusedBorderColor = Color.Transparent
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Submit Button
                    Button(
                        onClick = { submitConsultation() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp),
                        enabled = !isSubmitting && selectedDate != null && consultationText.isNotBlank(),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = Color(0xFF4FC3F7),
                            disabledContainerColor = Color(0xFFE0E0E0)
                        ),
                        shape = RoundedCornerShape(24.dp)
                    ) {
                        Text(
                            text = if (isSubmitting) "신청 중..." else "상담 신청",
                            fontSize = 16.sp,
                            color = Color.White,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            // Snackbar
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.align(Alignment.BottomCenter)
            )
        }
    }
}

@Composable
fun CalendarDay(
    date: LocalDate?,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    val today = LocalDate.now()
    val isToday = date == today
    val isPastDate = date?.isBefore(today) ?: false
    val dayText = date?.dayOfMonth?.toString() ?: ""

    Box(
        modifier = Modifier
            .size(40.dp)
            .clickable(enabled = date != null && !isPastDate) { onClick() }
            .background(
                color = when {
                    isSelected && date != null -> Color(0xFF4FC3F7)
//                    isToday && date != null -> Color(0xFF2196F3)
                    isPastDate -> Color(0xFFF5F5F5)
                    else -> Color.Transparent
                },
                shape = RoundedCornerShape(20.dp)
            ),
        contentAlignment = Alignment.Center
    ) {
        if (date != null) {
            Text(
                text = dayText,
                fontSize = 14.sp,
                color = when {
                    isSelected -> Color.White
//                    isToday -> Color.White
                    isPastDate -> Color(0xFFBDBDBD)
                    else -> Color.Black
                },
                fontWeight = when {
                    isSelected || isToday -> FontWeight.Medium
                    else -> FontWeight.Normal
                }
            )
        }
    }
}

fun generateCalendarDays(yearMonth: YearMonth): List<LocalDate?> {
    val firstDayOfMonth = yearMonth.atDay(1)
    val lastDayOfMonth = yearMonth.atEndOfMonth()
    val firstDayOfWeek = (firstDayOfMonth.dayOfWeek.value - 1) % 7

    val days = mutableListOf<LocalDate?>()

    // Add empty cells for days before the first day of the month
    repeat(firstDayOfWeek) {
        days.add(null)
    }

    // Add all days of the month
    for (day in 1..lastDayOfMonth.dayOfMonth) {
        days.add(yearMonth.atDay(day))
    }

    // Fill remaining cells to complete the grid (最多添加到42个格子，6行7列)
    while (days.size < 42) {
        days.add(null)
    }

    return days
}