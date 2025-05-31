package com.example.alwaysbewithyou.util.context

import android.content.Context
import android.widget.Toast

// 토스트 메세지 띄우기
fun Context.toast(message: String) {
    Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
}