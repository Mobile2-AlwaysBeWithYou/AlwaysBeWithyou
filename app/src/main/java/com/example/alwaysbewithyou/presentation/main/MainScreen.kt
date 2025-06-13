package com.example.alwaysbewithyou.presentation.main

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import com.example.alwaysbewithyou.R
import com.example.alwaysbewithyou.presentation.home.HomeScreen
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
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    NavigationBar(
                        containerColor = Color.Transparent
                    ) {
                        bottomNavItems.forEach { item ->
                            MainBottomBar(
                                selected = currentRoute == item.route,
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
                                    Icon(
                                        painter = painterResource(
                                            if (item.route == currentRoute) {
                                                item.selectedIcon
                                            } else item.unselectedIcon
                                        ),
                                        contentDescription = item.label,
                                        tint = Color.Unspecified
                                    )
                                },
                                label = {
                                    Text(
                                        text = item.label
                                    )
                                },
                                colors = NavigationBarItemDefaults.colors(
                                    indicatorColor = Color.Transparent,
                                    //selectedTextColor = Green500,
                                    //unselectedTextColor = Gray100
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

