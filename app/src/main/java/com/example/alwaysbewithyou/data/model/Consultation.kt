package com.example.dbtest.data

// Consultation.kt
data class Consultation(
    val date: String ="",
    val content: String = "",
    val status: String = "",
    val requested_at: com.google.firebase.Timestamp? = null
)
