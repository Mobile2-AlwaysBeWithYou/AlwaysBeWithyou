package com.example.dbtest.data
import com.google.firebase.Timestamp

data class GuardianWard(
    val guardian_id: String = "",           // 보호자 사용자 ID
    val ward_user_id: String = "",          // 피보호자 사용자 ID (연결 대상)
    val connected_since: Timestamp = Timestamp.now(), // 연결된 시점
    //추가 정보들
    val guardian_name: String = "",                  // 보호자 이름
    val phone: String = "",                 // 전화번호
    val relation: String = "",          // 관계 (ex: 아들, 딸, 손자 등)
    val note: String = ""                   // 메모 (자유입력)
)
