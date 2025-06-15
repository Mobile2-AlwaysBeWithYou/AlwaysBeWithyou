package com.example.alwaysbewithyou.presentation.main

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.alwaysbewithyou.R
import com.example.alwaysbewithyou.presentation.main.component.MainBottomBar
import com.example.alwaysbewithyou.presentation.navigation.BottomNavItem
import com.example.alwaysbewithyou.presentation.navigation.NavGraph
import com.example.alwaysbewithyou.presentation.navigation.Route

@Composable
fun MainScreen(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val currentBackStackEntry by navController.currentBackStackEntryAsState()
    var currentRoute = currentBackStackEntry?.destination?.route

    val bottomNavItems = listOf(
        BottomNavItem("홈", Route.Home.route, R.drawable.home_selected, R.drawable.home),
        BottomNavItem("지도", Route.Map.route, R.drawable.map_selected, R.drawable.map),
        BottomNavItem("상담·전화", Route.Call.route, R.drawable.call_selected, R.drawable.call),
        BottomNavItem("보호자", Route.Guardian.route, R.drawable.heart_selected, R.drawable.heart),
        BottomNavItem("설정", Route.Setting.route, R.drawable.setting_selected, R.drawable.setting)
    )

    Scaffold(
        bottomBar = {
            if (currentRoute in bottomNavItems.map { it.route }) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(Color(0xFFFFF1E6))
                        .height(100.dp),
                    contentAlignment = Alignment.BottomCenter
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent,
                        modifier = Modifier.height(100.dp),
                        tonalElevation = 0.dp
                    ) {
                        bottomNavItems.forEach { item ->
                            val isSelected = currentRoute == item.route

                            val scale = animateFloatAsState(
                                targetValue = if (isSelected) 1.1f else 1f,
                                animationSpec = tween(250)
                            ).value

                            MainBottomBar(
                                selected = isSelected,
                                onClick = {
                                    navController.navigate(item.route) {
                                        popUpTo(route = Route.Home.route) {
                                            inclusive = false
                                            saveState = true
                                        }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                icon = {
                                    Box(
                                        modifier = Modifier
                                            .scale(scale)
                                            .clip(RoundedCornerShape(10))
                                            .width(70.dp)
                                            .height(70.dp)
                                            .background(
                                                if (isSelected) Color(0xFFFFD7BA) else Color.Transparent
                                            ),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                            Icon(
                                                painter = painterResource(
                                                    if (isSelected) item.selectedIcon else item.unselectedIcon
                                                ),
                                                contentDescription = item.label,
                                                tint = Color.Unspecified,
                                                modifier = Modifier
                                                    .padding(bottom = 2.dp)
                                                    .align(Alignment.CenterHorizontally)
                                            )

                                            Text(
                                                text = item.label,
                                                color = Color.Black,
                                                fontSize = 12.sp
                                            )
                                        }
                                    }
                                },
                                label = null,
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent
                                )
                            )
                        }

                    }
                }
            }
        }
    ) { innerPadding ->
        NavGraph(
            navController = navController,
            modifier = Modifier.padding(innerPadding)
        )
    }
}
