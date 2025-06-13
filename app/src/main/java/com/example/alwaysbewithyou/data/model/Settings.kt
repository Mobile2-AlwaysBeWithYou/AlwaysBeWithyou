package com.example.dbtest.data

// Settings.kt
data class Settings(
    val font_size: String = "medium",
    val notifications_enabled: Boolean = true,
    val notification_permission_granted: Boolean = true,
    val time_notification: Boolean = false,
    val pill_notification: Boolean = false,
    val announcement_notification: Boolean = false
)
