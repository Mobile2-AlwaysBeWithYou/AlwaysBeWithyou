package com.example.dbtest.data

import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp

enum class FontSize(val navBarSize: TextUnit, val buttonSize: TextUnit) {
    SMALL(10.sp, 12.sp),
    MEDIUM(13.sp, 15.sp),
    LARGE(17.sp, 18.sp),
    EXTRA_LARGE(20.sp, 21.sp);

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
