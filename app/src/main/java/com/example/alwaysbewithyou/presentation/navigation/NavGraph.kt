package com.example.alwaysbewithyou.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.alwaysbewithyou.LoginScreen
import com.example.alwaysbewithyou.presentation.call.CallScreen
import com.example.alwaysbewithyou.presentation.guardian.GuardianScreen
import com.example.alwaysbewithyou.presentation.home.HomeScreen
import com.example.alwaysbewithyou.presentation.map.MapListScreen
import com.example.alwaysbewithyou.presentation.map.MapScreen
import com.example.alwaysbewithyou.presentation.map.tools.MapViewModel
import com.example.alwaysbewithyou.presentation.onboarding.SignUpScreen
import com.example.alwaysbewithyou.presentation.onboarding.SplashScreen
import com.example.alwaysbewithyou.presentation.setting.MyPageScreen

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val mapViewModel: MapViewModel = viewModel()
    NavHost(
        navController = navController,
        startDestination = Route.Splash.route
    ) {
        composable(route = Route.Splash.route) {
            SplashScreen(
                onNavigateToLogin = {
                    navController.navigate(Route.Login.route)
                }
            )
        }

        composable(route = Route.SignUp.route) {
            SignUpScreen(
                onNavigateToLogin = {
                    navController.navigate(Route.Login.route)
                }
            )
        }

        composable(route = Route.Login.route) {
            LoginScreen(
                onNavigateToHome={
                    navController.navigate(Route.Home.route)
                },
                onNavigateToSignUp={
                    navController.navigate(Route.SignUp.route)
                }
            )
        }

        composable(route = Route.Home.route) {
            HomeScreen(
                onNavigateToHomeDetail = {
                    navController.navigate(Route.HomeDetail.route)
                }
            )
        }

        composable(route = Route.Map.route) {
            MapScreen(
                onNavigateToMapList = {
                    navController.navigate(Route.MapList.route)
                },
                viewModel = mapViewModel
            )
        }

        composable(route = Route.MapList.route) {
            MapListScreen(
                viewModel = mapViewModel,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable(route = Route.Call.route) {
            CallScreen()
        }

        composable(route = Route.Guardian.route) {
            GuardianScreen()
        }

        composable(route = Route.Setting.route) {
            MyPageScreen(
                onLogout = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(0)  // 백스택 제거
                    }
                }
            )
        }

        composable(route = Route.HomeDetail.route) {
            //HomeDetailScreen()
        }
    }
}