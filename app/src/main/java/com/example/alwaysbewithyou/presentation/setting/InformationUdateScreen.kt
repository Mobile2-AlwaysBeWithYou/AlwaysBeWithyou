package com.example.alwaysbewithyou.presentation.setting

import android.app.ProgressDialog.show
import android.util.Log
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.alwaysbewithyou.R
import com.example.alwaysbewithyou.data.viewmodel.DatabaseViewModel
import com.example.dbtest.data.PillAlarm
import com.example.dbtest.data.User
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InformationUpdateScreen(
    modifier: Modifier = Modifier,
    navController: NavHostController,
    databaseViewModel: DatabaseViewModel
) {
    // 현재 사용자 정보 가져오기
    val currentUser by databaseViewModel.user.collectAsState()
    val pillAlarms by databaseViewModel.pillAlarms.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // 입력 필드 상태
    var name by remember { mutableStateOf("") }
    var age by remember { mutableStateOf("") }
    var gender by remember { mutableStateOf("") }
    var phone by remember { mutableStateOf("") }
    var isGenderDropdownExpanded by remember { mutableStateOf(false) }
    var isPillAlarmEnabled by remember { mutableStateOf(true) }
    var showAddAlarmDialog by remember { mutableStateOf(false) }

    // 사용자 정보가 로드되면 입력 필드에 설정
    LaunchedEffect(currentUser) {
        currentUser?.let { user ->
            name = user.name
            age = user.age?.toString() ?: ""
            gender = user.gender ?: "여성"
            phone = user.phone ?: ""
        }
    }

    // 사용자 정보 로드
    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            databaseViewModel.loadUser(currentUserId)
            databaseViewModel.loadPillAlarms(currentUserId)
        }
    }

    Column(
        modifier = modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "내 정보",
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
                containerColor = Color.White
            )
        )

        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = Color.White
            ),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(24.dp)
            ) {
                // 이름 입력
                Text(
                    text = "이름",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("이름을 입력하세요") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4FC3F7),
                        unfocusedBorderColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 나이 입력
                Text(
                    text = "나이",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = age,
                    onValueChange = { age = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("나이를 입력하세요") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4FC3F7),
                        unfocusedBorderColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(20.dp))

                // 성별 선택
                Text(
                    text = "성별",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Box {
                    OutlinedTextField(
                        value = gender,
                        onValueChange = { },
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { isGenderDropdownExpanded = true },
                        placeholder = { Text("성별을 선택하세요") },
                        readOnly = true,
                        trailingIcon = {
                            Image(
                                painter = painterResource(id = R.drawable.keyboard_arrow_right),
                                contentDescription = "dropdown arrow",
                                modifier = Modifier.clickable { isGenderDropdownExpanded = true }
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF4FC3F7),
                            unfocusedBorderColor = Color.Gray
                        ),
                        shape = RoundedCornerShape(8.dp)
                    )

                    DropdownMenu(
                        expanded = isGenderDropdownExpanded,
                        onDismissRequest = { isGenderDropdownExpanded = false }
                    ) {
                        DropdownMenuItem(
                            text = { Text("남성") },
                            onClick = {
                                gender = "남성"
                                isGenderDropdownExpanded = false
                            }
                        )
                        DropdownMenuItem(
                            text = { Text("여성") },
                            onClick = {
                                gender = "여성"
                                isGenderDropdownExpanded = false
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // 전화번호 입력
                Text(
                    text = "전화번호",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                OutlinedTextField(
                    value = phone,
                    onValueChange = { phone = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("전화번호를 입력하세요") },
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Phone),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4FC3F7),
                        unfocusedBorderColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 복용 약물 알림 설정 카드
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            shape = RoundedCornerShape(12.dp),
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
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "약물 복용 여부",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.Black
                    )

                    Switch(
                        checked = isPillAlarmEnabled,
                        onCheckedChange = { isPillAlarmEnabled = it },
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = Color(0xFF4CAF50),
                            uncheckedThumbColor = Color.White,
                            uncheckedTrackColor = Color.Gray
                        )
                    )

                }

                Spacer(modifier = Modifier.height(16.dp))

                // 복용 약물 목록 헤더
                if (isPillAlarmEnabled) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "약명",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "복용시간",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.weight(1f)
                        )
                        Text(
                            text = "알림",
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium,
                            color = Color.Black,
                            modifier = Modifier.width(40.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    // 실제 약물 목록 표시
                    pillAlarms.forEach { alarm ->
                        PillAlarmItem(
                            pillName = alarm.label,
                            time = alarm.time,
                            isEnabled = alarm.enabled,
                            onToggleEnabled = {
                                if (alarm.id.isNotEmpty()) {
                                    databaseViewModel.updatePillAlarmEnabled(currentUserId, alarm.id, !alarm.enabled)
                                }else{
                                    databaseViewModel.loadPillAlarms(currentUserId)
                                    if (alarm.id.isNotEmpty()){
                                        databaseViewModel.updatePillAlarmEnabled(currentUserId, alarm.id, !alarm.enabled)
                                    }
                                }
                            }
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // 추가 버튼
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.End
                    ) {
                        Box(
                            modifier = Modifier
                                .size(30.dp)
                                .background(
                                    Color.Black,
                                    CircleShape
                                )
                                .clickable {
                                    showAddAlarmDialog = true
                                },
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = "+",
                                color = Color.White,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // 정보 수정 버튼
        Button(
            onClick = {
                if (name.isNotBlank() && age.isNotBlank() && currentUserId.isNotEmpty()) {
                    val updatedUser = User(
                        id = currentUserId,
                        name = name,
                        age = age.toIntOrNull(),
                        gender = gender,
                        phone = phone
                    )
                    databaseViewModel.createUser(updatedUser)
                    navController.popBackStack()
                }
            },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .height(50.dp),
            colors = ButtonDefaults.buttonColors(
                containerColor = Color(0xFF4FC3F7)
            ),
            shape = RoundedCornerShape(25.dp)
        ) {
            Text(
                text = "정보 수정",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium,
                color = Color.White
            )
        }

        Spacer(modifier = Modifier.height(16.dp))
    }

    // 새 약물 알림 추가 다이얼로그
    if (showAddAlarmDialog) {
        AddPillAlarmDialog(
            onDismiss = { showAddAlarmDialog = false },
            onConfirm = { pillName, time, repeatDay ->
                val newAlarm = PillAlarm(
                    time = time,
                    label = pillName,
                    repeat_day = repeatDay,
                    enabled = true,
                    created_at = com.google.firebase.Timestamp.now()
                )
                databaseViewModel.addPillAlarm(currentUserId, newAlarm)
                showAddAlarmDialog = false
            }
        )
    }

}

@Composable
fun PillAlarmItem(
    pillName: String,
    time: String,
    isEnabled: Boolean,
    onToggleEnabled: () -> Unit = {}
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = pillName,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )
        Text(
            text = time,
            fontSize = 14.sp,
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        // 알림 아이콘
        Image(
            painter = painterResource(
                id = if (isEnabled) R.drawable.ic_notification_on else R.drawable.ic_notification_off
            ),
            contentDescription = if (isEnabled) "알림 켜짐" else "알림 꺼짐",
            modifier = Modifier
                .size(20.dp)
                .clickable { onToggleEnabled() }
        )
    }
}

@Composable
fun AddPillAlarmDialog(
    onDismiss: () -> Unit,
    onConfirm: (String, String, String) -> Unit
) {
    var pillName by remember { mutableStateOf("") }
    var selectedHour by remember { mutableStateOf(8) }
    var selectedMinute by remember { mutableStateOf(0) }
    var selectedPeriod by remember { mutableStateOf("오전") }
    var repeatDay by remember { mutableStateOf("매일") }
    var isTimeExpanded by remember { mutableStateOf(false) }
    var isRepeatExpanded by remember { mutableStateOf(false) }

    val timeString = "${selectedPeriod} ${selectedHour}:${String.format("%02d", selectedMinute)}"

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(
                text = "새 약물 알림 추가",
                fontSize = 18.sp,
                fontWeight = FontWeight.Medium
            )
        },
        text = {
            Column(
                modifier = Modifier.fillMaxWidth()
            ) {
                // 약물명 입력
                Text(
                    text = "약물명",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                OutlinedTextField(
                    value = pillName,
                    onValueChange = { pillName = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("약물명을 입력하세요") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color(0xFF4FC3F7),
                        unfocusedBorderColor = Color.Gray
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // 복용 시간 선택
                Text(
                    text = "복용 시간",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 시간 표시
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isTimeExpanded = !isTimeExpanded },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = timeString,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Text("▼", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                // 시간 선택 옵션들
                if (isTimeExpanded) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp)
                        ) {
                            // 오전/오후 선택
                            Text("오전/오후 선택:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                listOf("오전", "오후").forEach { period ->
                                    Button(
                                        onClick = { selectedPeriod = period },
                                        colors = ButtonDefaults.buttonColors(
                                            containerColor = if (selectedPeriod == period)
                                                Color(0xFF4FC3F7) else Color.White,
                                            contentColor = if (selectedPeriod == period)
                                                Color.White else Color.Black
                                        ),
                                        modifier = Modifier.weight(1f)
                                    ) {
                                        Text(period)
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 시간 선택
                            Text("시간 선택:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                (1..12).chunked(6).forEach { hourGroup ->
                                    Column(modifier = Modifier.weight(1f)) {
                                        hourGroup.forEach { hour ->
                                            Button(
                                                onClick = { selectedHour = hour },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (selectedHour == hour)
                                                        Color(0xFF4FC3F7) else Color.White,
                                                    contentColor = if (selectedHour == hour)
                                                        Color.White else Color.Black
                                                ),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(36.dp)
                                                    .padding(vertical = 2.dp)
                                            ) {
                                                Text(hour.toString(), fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            // 분 선택
                            Text("분 선택:", fontSize = 14.sp, fontWeight = FontWeight.Medium)
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(4.dp)
                            ) {
                                (0..55 step 5).chunked(6).forEach { minuteGroup ->
                                    Column(modifier = Modifier.weight(1f)) {
                                        minuteGroup.forEach { minute ->
                                            Button(
                                                onClick = { selectedMinute = minute },
                                                colors = ButtonDefaults.buttonColors(
                                                    containerColor = if (selectedMinute == minute)
                                                        Color(0xFF4FC3F7) else Color.White,
                                                    contentColor = if (selectedMinute == minute)
                                                        Color.White else Color.Black
                                                ),
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .height(36.dp)
                                                    .padding(vertical = 2.dp)
                                            ) {
                                                Text(String.format("%02d", minute), fontSize = 12.sp)
                                            }
                                        }
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(12.dp))

                            Button(
                                onClick = { isTimeExpanded = false },
                                modifier = Modifier.fillMaxWidth(),
                                colors = ButtonDefaults.buttonColors(
                                    containerColor = Color(0xFF4FC3F7)
                                )
                            ) {
                                Text("확인", color = Color.White)
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // 반복 요일 선택
                Text(
                    text = "반복",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { isRepeatExpanded = !isRepeatExpanded },
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = repeatDay,
                            fontSize = 16.sp,
                            color = Color.Black
                        )
                        Text("▼", fontSize = 12.sp, color = Color.Gray)
                    }
                }

                // 반복 선택 옵션들
                if (isRepeatExpanded) {
                    Card(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 4.dp),
                        colors = CardDefaults.cardColors(containerColor = Color(0xFFF5F5F5)),
                        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Column(
                            modifier = Modifier.padding(16.dp),
                            verticalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            listOf("매일", "평일", "주말", "월요일", "화요일", "수요일", "목요일", "금요일", "토요일", "일요일").forEach { day ->
                                Button(
                                    onClick = {
                                        repeatDay = day
                                        isRepeatExpanded = false
                                    },
                                    colors = ButtonDefaults.buttonColors(
                                        containerColor = if (repeatDay == day)
                                            Color(0xFF4FC3F7) else Color.White,
                                        contentColor = if (repeatDay == day)
                                            Color.White else Color.Black
                                    ),
                                    modifier = Modifier.fillMaxWidth()
                                ) {
                                    Text(day)
                                }
                            }
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    if (pillName.isNotBlank()) {
                        onConfirm(pillName, timeString, repeatDay)
                    }
                },
                enabled = pillName.isNotBlank()
            ) {
                Text("추가", color = Color(0xFF4FC3F7))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("취소", color = Color.Gray)
            }
        }
    )
}