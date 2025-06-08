package com.example.dbtest.data
import com.google.firebase.Timestamp

data class GuardianWard(
    val guardian_id: String = "",
    val ward_user_id: String = "",
    val connected_since: Timestamp = Timestamp.now()
)
