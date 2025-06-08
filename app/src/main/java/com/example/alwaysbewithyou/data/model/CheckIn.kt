package com.example.dbtest.data

// CheckIn.kt
data class CheckIn(
    val check_time: com.google.firebase.Timestamp? = null,
    val responded: Boolean = false
)
