package com.example.dbtest.data

import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit

enum class FontSize(val navBarSize: TextUnit, val buttonSize: TextUnit) {
    SMALL(15.sp, 13.sp),
    MEDIUM(20.sp, 18.sp),
    LARGE(25.sp, 23.sp),
    EXTRA_LARGE(30.sp, 28.sp);

    companion object {
        fun fromString(value: String): FontSize = when (value.lowercase()) {
            "small" -> SMALL
            "medium" -> MEDIUM
            "large" -> LARGE
            "extra_large" -> EXTRA_LARGE
            else -> MEDIUM // 기본값
        }
    }
}
