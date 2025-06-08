package com.example.dbtest.data

// Consultation.kt
data class Consultation(
    val type: String = "",
    val status: String = "",
    val requested_at: com.google.firebase.Timestamp? = null
)
