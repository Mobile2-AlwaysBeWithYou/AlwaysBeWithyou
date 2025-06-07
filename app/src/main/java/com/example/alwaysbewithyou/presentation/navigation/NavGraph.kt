package com.example.alwaysbewithyou.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.alwaysbewithyou.LoginScreen
import com.example.alwaysbewithyou.data.viewmodel.DatabaseViewModel
import com.example.alwaysbewithyou.presentation.call.CallScreen
import com.example.alwaysbewithyou.presentation.guardian.GuardianAddScreen
import com.example.alwaysbewithyou.presentation.guardian.GuardianScreen
import com.example.alwaysbewithyou.presentation.home.HomeScreen
import com.example.alwaysbewithyou.presentation.map.MapDetailScreen
import com.example.alwaysbewithyou.presentation.map.MapListScreen
import com.example.alwaysbewithyou.presentation.map.MapScreen
import com.example.alwaysbewithyou.presentation.map.tools.GooglePlacesApiService
import com.example.alwaysbewithyou.presentation.map.tools.MapDetailViewModel
import com.example.alwaysbewithyou.presentation.map.tools.MapViewModel
import com.example.alwaysbewithyou.presentation.map.tools.PlaceRepository
import com.example.alwaysbewithyou.presentation.onboarding.SignUpScreen
import com.example.alwaysbewithyou.presentation.onboarding.SplashScreen
import com.example.alwaysbewithyou.presentation.setting.MyPageScreen
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    val databaseViewModel : DatabaseViewModel = viewModel()

    val retrofit = remember {
        Retrofit.Builder()
            .baseUrl("https://maps.googleapis.com/maps/api/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
    }

    val googlePlacesApiService = remember { retrofit.create(GooglePlacesApiService::class.java) }
    val placeRepository = remember { PlaceRepository(googlePlacesApiService, context) }

    val mapViewModel: MapViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MapViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MapViewModel(placeRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

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
                },
                viewModel = databaseViewModel
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
                onPlaceClick = { placeId ->
                    navController.navigate(Route.MapDetail.createRoute(placeId))
                },
                viewModel = mapViewModel
            )
        }

        composable(Route.MapList.route) {
            MapListScreen(
                viewModel = mapViewModel,
                onNavigateBack = { navController.popBackStack() },
                navController = navController
            )
        }

        composable(
            route = Route.MapDetail.route,
            arguments = listOf(navArgument("placeId") { type = NavType.StringType })
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString("placeId")

            // MapDetailViewModel 인스턴스 생성 및 PlaceRepository 주입
            val mapDetailViewModel: MapDetailViewModel = viewModel(
                factory = object : ViewModelProvider.Factory {
                    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                        if (modelClass.isAssignableFrom(MapDetailViewModel::class.java)) {
                            @Suppress("UNCHECKED_CAST")
                            return MapDetailViewModel(placeRepository) as T
                        }
                        throw IllegalArgumentException("Unknown ViewModel class")
                    }
                }
            )

            MapDetailScreen(
                placeId = placeId, // placeId 전달
                mapDetailViewModel = mapDetailViewModel, // ViewModel 전달
                onNavigateBack = { navController.popBackStack() },
                onFindRouteClick = { /* TODO: 길찾기 기능 구현 */ }
            )
        }

        composable(route = Route.Call.route) {
            CallScreen(navController = navController)
        }

        composable(route = Route.Guardian.route) {
            val currentUserId = FirebaseAuth.getInstance().currentUser?.uid

            currentUserId?.let { userId ->
                GuardianScreen(
                    onNavigateToAddPage = { navController.navigate(Route.GuardianAdd.route) },
                    viewModel = databaseViewModel,
                    userId = userId
                )
            }
        }

        composable(route = Route.GuardianAdd.route) {
            GuardianAddScreen(
                onNavigateToGuardian = {
                    navController.navigate(Route.Guardian.route)
                },
                viewModel = databaseViewModel
            )
        }

        composable(route = Route.Setting.route) {
            MyPageScreen(
                navController = navController,
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