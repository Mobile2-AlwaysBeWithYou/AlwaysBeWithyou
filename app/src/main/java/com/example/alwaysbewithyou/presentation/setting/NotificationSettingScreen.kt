package com.example.alwaysbewithyou.presentation.setting

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.IconButton
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import com.example.alwaysbewithyou.R
import com.example.alwaysbewithyou.data.viewmodel.DatabaseViewModel
import com.example.dbtest.data.Settings
import com.google.firebase.auth.FirebaseAuth

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationSettingScreen(
        modifier: Modifier = Modifier,
        navController: NavHostController,
        databaseViewModel: DatabaseViewModel
    ) {

    val currentSettings by databaseViewModel.settings.collectAsState()
    val currentUserId = FirebaseAuth.getInstance().currentUser?.uid ?: ""

    // 로컬 상태 - 설정이 로드되면 업데이트
    var timeNotification by remember { mutableStateOf(false) }
    var pillNotification by remember { mutableStateOf(false) }
    var announcementNotification by remember { mutableStateOf(false) }
    var notificationPermission by remember { mutableStateOf(true) }

    LaunchedEffect(currentUserId) {
        if (currentUserId.isNotEmpty()) {
            databaseViewModel.loadSettings(currentUserId)
        }
    }

    LaunchedEffect(currentSettings) {
        currentSettings?.let { settings ->
            timeNotification = settings.time_notification
            pillNotification = settings.pill_notification
            announcementNotification = settings.announcement_notification
            notificationPermission = settings.notification_permission_granted
        }
    }

    fun updateSettings() {
        if (currentUserId.isNotEmpty()) {
            val updatedSettings = Settings(
                font_size = currentSettings?.font_size ?: "medium", // 기존 폰트 크기 유지
                notifications_enabled = currentSettings?.notifications_enabled ?: true, // 기존 전체 알림 설정 유지
                notification_permission_granted = notificationPermission,
                time_notification = timeNotification,
                pill_notification = pillNotification,
                announcement_notification = announcementNotification
            )
            databaseViewModel.saveSettings(currentUserId, updatedSettings)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        TopAppBar(
            title = {
                Text(
                    text = "알림",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    color = Color.Black
                )
            },
            navigationIcon = {
                IconButton(onClick = {navController.popBackStack()}) {
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

        Spacer(modifier = Modifier.height(16.dp))

        Column {
            NotificationToggleItem(
                title = "시간마다 알림",
                isChecked = timeNotification,
                onToggle = { timeNotification = it }
            )

            NotificationToggleItem(
                title = "약 복용 알림",
                description = "약물 복용 시간에 알림을 받습니다",
                isChecked = pillNotification,
                onToggle = {
                    pillNotification = it
                    updateSettings()
                }
            )

            NotificationToggleItem(
                title = "공지 사항 알림",
                description = "새로운 공지사항이 있을 때 알림을 받습니다",
                isChecked = announcementNotification,
                onToggle = {
                    announcementNotification = it
                    updateSettings()
                }
            )

            NotificationToggleItem(
                title = "알림 권한 허용",
                description = "앱에서 알림을 보낼 수 있도록 허용합니다",
                isChecked = notificationPermission,
                onToggle = {
                    notificationPermission = it
                    updateSettings()
                },
                isImportant = true
            )
        }

    }
}

@Composable
fun NotificationToggleItem(
    title: String,
    description: String? = null,
    isChecked: Boolean,
    onToggle: (Boolean) -> Unit,
    isImportant: Boolean = false
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 2.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 16.sp,
                    color = Color.Black,
                    fontWeight = if (isImportant) FontWeight.Medium else FontWeight.Normal
                )

                description?.let {
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = it,
                        fontSize = 12.sp,
                        color = Color.Gray,
                        fontWeight = FontWeight.Normal
                    )
                }
            }

            Switch(
                checked = isChecked,
                onCheckedChange = onToggle,
                colors = SwitchDefaults.colors(
                    checkedThumbColor = Color.White,
                    checkedTrackColor = Color(0xFF4CAF50),
                    uncheckedThumbColor = Color.White,
                    uncheckedTrackColor = Color(0xFFE0E0E0)
                )
            )
        }
    }
}