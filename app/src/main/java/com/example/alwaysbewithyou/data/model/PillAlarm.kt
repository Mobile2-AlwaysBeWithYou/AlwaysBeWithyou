package com.example.dbtest.data

// PillAlarm.kt
data class PillAlarm(
    val time: String = "",
    val label: String = "",
    val repeat_day: String = "",
    val enabled: Boolean = true,
    val created_at: com.google.firebase.Timestamp? = null
)
