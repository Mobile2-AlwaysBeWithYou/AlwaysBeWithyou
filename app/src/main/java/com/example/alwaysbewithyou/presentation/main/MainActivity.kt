package com.example.alwaysbewithyou.presentation.main

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.navigation.compose.rememberNavController
import com.example.alwaysbewithyou.ui.theme.AlwaysBeWithYouTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
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