package com.example.dbtest.data

// Notification.kt
data class Notification(
    val type: String = "",
    val message: String = "",
    val sent_time: com.google.firebase.Timestamp? = null
)
