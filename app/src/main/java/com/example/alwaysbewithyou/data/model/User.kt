package com.example.dbtest.data

// User.kt
data class User(
    val id: String = "",
    val name: String = "",
    val phone: String = "",
    val password: String ="",
    val is_guardian: Boolean = false,
    val guardian_id: String? = null,
    val last_login: com.google.firebase.Timestamp? = null,
    val auto_login: Boolean = false
)
