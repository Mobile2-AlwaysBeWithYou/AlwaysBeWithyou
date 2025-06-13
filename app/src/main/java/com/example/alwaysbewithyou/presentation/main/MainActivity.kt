package com.example.alwaysbewithyou.presentation.main

import android.app.AlertDialog
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.icu.text.SimpleDateFormat
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.RemoteViews
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.ActivityResultLauncher
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.navigation.compose.rememberNavController
import com.example.alwaysbewithyou.R
import com.example.alwaysbewithyou.ui.theme.AlwaysBeWithYouTheme
import com.google.android.libraries.places.api.Places
import timber.log.Timber
import java.util.Date
import java.util.Locale

class MainActivity : ComponentActivity() {

    private lateinit var requestPermissionLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

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
        setContent {
            AlwaysBeWithYouTheme {
                val navController = rememberNavController()
                MainScreen(navController = navController)
            }
        }

        // 처음 알림 시간 설정 20초
        scheduleCustomNotification(delayMillis = 20_000L)
    }

    // 알림 채널 생성 함수
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

    //알림 발송 함수
    private fun scheduleCustomNotification(delayMillis: Long) {
        val intent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            putExtra("fromNotification", true)
        }

        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        // 현재 시간
        val currentTime = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())

        // 맞춤 알림 보기(시스템 알림 화면 변동 불가능하기 때문에)
        val customView = RemoteViews(packageName, R.layout.custom_notification).apply {
            setTextViewText(R.id.title, "늘곁에  $currentTime")
            setTextViewText(
                R.id.message,
                "4시간동안 응답하지 않으셨어요!\n무슨 일 있으신가요?\n곧 보호자에게 자동으로 연락이 가요.\n앱에 접속하시려면 탭해주세요."
            )
            setImageViewResource(R.id.icon_left, R.drawable.alarm_icon)
        }

        val notification = NotificationCompat.Builder(this, "alarm_channel")
            .setSmallIcon(android.R.drawable.stat_sys_warning)
            .setCustomContentView(customView)
            .setStyle(NotificationCompat.DecoratedCustomViewStyle())
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        // 초음 발송
        Handler(Looper.getMainLooper()).postDelayed({
            if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                NotificationManagerCompat.from(this).notify(1, notification)
            }

            // 응답하지 않으면 다시 알림 20초후
            Handler(Looper.getMainLooper()).postDelayed({
                val againCustomView = RemoteViews(packageName, R.layout.custom_notification).apply {
                    setTextViewText(R.id.title, "미확인 알림")
                    setTextViewText(R.id.message, "확인을 아직 하지 않으셨습니다!")
                    setImageViewResource(R.id.icon_left, R.drawable.alarm_icon)
                }

                val againNotification = NotificationCompat.Builder(this, "alarm_channel")
                    .setSmallIcon(android.R.drawable.stat_sys_warning)
                    .setCustomContentView(againCustomView)
                    .setStyle(NotificationCompat.DecoratedCustomViewStyle())
                    .build()

                if (NotificationManagerCompat.from(this).areNotificationsEnabled()) {
                    NotificationManagerCompat.from(this).notify(2, againNotification)
                }
            }, 20_000L)

        }, delayMillis)
    }

    // 알림 이동할 때 확인용 (test용)
    private fun showConfirmationDialog() {
        AlertDialog.Builder(this)
            .setTitle("확인 페이지")
            .setMessage("알림을 확인하셨나요?")
            .setPositiveButton("네") { _, _ ->
                scheduleCustomNotification(10_000L)
            }
            .setNegativeButton("다시 설정") { _, _ ->
                scheduleCustomNotification(15_000L)
            }
            .show()
    }
}