package com.example.alwaysbewithyou.presentation.main

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.rememberNavController
import com.example.alwaysbewithyou.R
import com.example.alwaysbewithyou.presentation.navigation.Route
import com.example.alwaysbewithyou.ui.theme.AlwaysBeWithYouTheme
import com.google.android.libraries.places.api.Places
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    //권한 요청 및 변수 정의
    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>
    private lateinit var notificationHandler: Handler
    private lateinit var notificationRunnable: Runnable

    private var notificationCount = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val apiKey = try {
            val appInfo = packageManager.getApplicationInfo(packageName, android.content.pm.PackageManager.GET_META_DATA)
            appInfo.metaData.getString("com.google.android.geo.API_KEY")
        } catch (e: Exception) {
            null
        }

        apiKey?.let {
            if (!Places.isInitialized()) {
                Places.initialize(applicationContext, it)
            }
        }

        //알림 권한 요청
        requestPermissionLauncher = registerForActivityResult(
            ActivityResultContracts.RequestPermission()
        ) { isGranted ->
            if (isGranted) {
                println("알림 권한이 부여")
            } else {
                println("알림 권한이 거부")
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (checkSelfPermission(android.Manifest.permission.POST_NOTIFICATIONS)
                != PackageManager.PERMISSION_GRANTED
            ) {
                requestPermissionLauncher.launch(android.Manifest.permission.POST_NOTIFICATIONS)
            }
        }

        //알림 채널 만들기
        createNotificationChannel()

        //알림 클릭 인지 확인
        val fromNotification = intent?.getBooleanExtra("fromNotification", false) == true

        //UI 콘텐츠 설정
        setContent {
            AlwaysBeWithYouTheme {
                val navController = rememberNavController()
                var showConfirmation by remember { mutableStateOf(fromNotification) }

                LaunchedEffect(fromNotification) {
                    //알림 클릭 로직
                    if (fromNotification) {
                        NotificationManagerCompat.from(this@MainActivity).cancelAll()
                    }
                }

                // 확인 화면 표시 여부에 따라 UI 선택
                if (showConfirmation) {
                    NotificationConfirmationScreen(
                        onConfirm = {
                            showConfirmation = false
                            // 통지 카운터 리셋 및 알림 타이머 재설정
                            notificationCount = 0
                            resetNotificationTimer()
                        }
                    )
                } else {
                    MainScreen(navController = navController)
                }
            }
        }

        //앱 실행 시 알림 예약
        if (!fromNotification) {
            scheduleCustomNotification(delayMillis = 10_000L)
        }
    }

    //알림 논리 재설정
    private fun resetNotificationTimer() {
        if (::notificationHandler.isInitialized && ::notificationRunnable.isInitialized) {
            notificationHandler.removeCallbacks(notificationRunnable)
        }
        scheduleCustomNotification(delayMillis = 10_000L)
    }

    //알림 채널 생성
    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                "alarm_channel",
                "알림 채널",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "시간 알림 및 미응답 체크"
            }
            val manager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    //알림 시간 설정
    private fun scheduleCustomNotification(delayMillis: Long) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("fromNotification", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        //타이머 로직 설정
        notificationHandler = Handler(Looper.getMainLooper())

        notificationRunnable = object : Runnable {
            override fun run() {
                notificationCount++

                val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

                val (title, message) = when {
                    notificationCount == 1 -> Pair(
                        "늘곁에  $currentTime",
                        "10초동안 응답하지 않으셨어요!\n무슨 일 있으신가요?\n곧 보호자에게 자동으로 연락이 가요.\n앱에 접속하시려면 탭해주세요."
                    )
                    else -> Pair(
                        "미확인 알림 (${notificationCount} 번째)",
                        "아직 확인하지 않으셨습니다!\n괜찮으신가요?\n앱에 접속해주세요."
                    )
                }

                //알림 전송
                val customView = RemoteViews(packageName, R.layout.custom_notification).apply {
                    setTextViewText(R.id.title, title)
                    setTextViewText(R.id.message, message)
                    setImageViewResource(R.id.icon_left, R.drawable.alarm_icon)
                }

                val notification = NotificationCompat.Builder(this@MainActivity, "alarm_channel")
                    .setSmallIcon(android.R.drawable.stat_sys_warning)
                    .setCustomContentView(customView)
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .setContentIntent(pendingIntent)
                    .setAutoCancel(true)
                    .setPriority(NotificationCompat.PRIORITY_HIGH)
                    .build()

                if (NotificationManagerCompat.from(this@MainActivity).areNotificationsEnabled()) {
                    NotificationManagerCompat.from(this@MainActivity).notify(1, notification)
                }

                //알림 재발동 보장
                notificationHandler.postDelayed(this, 10_000L)
            }
        }

        //예약된 작업 시작
        notificationHandler.postDelayed(notificationRunnable, delayMillis)
    }

    //종료 시 알림 작업 취소
    override fun onDestroy() {
        super.onDestroy()
        if (::notificationHandler.isInitialized && ::notificationRunnable.isInitialized) {
            notificationHandler.removeCallbacks(notificationRunnable)
        }
    }
}

@Composable
fun NotificationConfirmationScreen(
    onConfirm: () -> Unit
) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White),
        contentAlignment = Alignment.Center
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(32.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color.White),
            elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(32.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 웃는 얼굴 이모지
                Text(
                    text = "😊",
                    fontSize = 48.sp,
                    modifier = Modifier.padding(bottom = 8.dp)
                )

                // 메인 제목
                Text(
                    text = "접속이 확인되었습니다.",
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color.Black,
                    textAlign = TextAlign.Center
                )

                // 부제목
                Text(
                    text = "4시간후에 다시 알림 예정",
                    fontSize = 14.sp,
                    color = Color.Gray,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(8.dp))

                // 확인 버튼
                Button(
                    onClick = onConfirm,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0xFFFF9800) // 오렌지 색상
                    ),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = "확인",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Medium,
                        color = Color.White
                    )
                }
            }
        }
    }
}