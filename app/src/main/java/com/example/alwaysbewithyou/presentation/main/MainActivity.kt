package com.example.alwaysbewithyou.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.alwaysbewithyou.BuildConfig
import com.example.alwaysbewithyou.ui.theme.AlwaysBeWithYouTheme
import com.google.android.libraries.places.api.Places
import java.util.Locale

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
//        enableEdgeToEdge()
        if (!Places.isInitialized()) {
            Places.initialize(applicationContext, BuildConfig.API_KEY , Locale.KOREA)
        }

        setContent {
            AlwaysBeWithYouTheme {
                val navController = rememberNavController()
                MainScreen(
                    navController = navController
                )
            }
        }
    }
}