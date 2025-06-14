package com.example.alwaysbewithyou.presentation.navigation

import android.util.Log
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
import com.example.alwaysbewithyou.BuildConfig
import com.example.alwaysbewithyou.LoginScreen
import com.example.alwaysbewithyou.data.viewmodel.DatabaseViewModel
import com.example.alwaysbewithyou.presentation.call.CallScreen
import com.example.alwaysbewithyou.presentation.call.ResultScreen
import com.example.alwaysbewithyou.presentation.call.ReviewScreen
import com.example.alwaysbewithyou.presentation.guardian.GuardianAddScreen
import com.example.alwaysbewithyou.presentation.call.ScheduleScreen
import com.example.alwaysbewithyou.presentation.guardian.GuardianScreen
import com.example.alwaysbewithyou.presentation.home.HomeScreen
import com.example.alwaysbewithyou.presentation.map.MapDetailScreen
import com.example.alwaysbewithyou.presentation.map.MapListScreen
import com.example.alwaysbewithyou.presentation.map.MapRouteScreen
import com.example.alwaysbewithyou.presentation.map.MapScreen
import com.example.alwaysbewithyou.presentation.map.api.GoogleDirectionsApiService
import com.example.alwaysbewithyou.presentation.map.api.GooglePlacesApiService
import com.example.alwaysbewithyou.presentation.map.viewmodel.MapDetailViewModel
import com.example.alwaysbewithyou.presentation.map.viewmodel.MapViewModel
import com.example.alwaysbewithyou.presentation.map.tools.PlaceRepository
import com.example.alwaysbewithyou.presentation.map.viewmodel.MapRouteViewModel
import com.example.alwaysbewithyou.presentation.onboarding.SignUpScreen
import com.example.alwaysbewithyou.presentation.onboarding.SplashScreen
import com.example.alwaysbewithyou.presentation.setting.AnnouncementScreen
import com.example.alwaysbewithyou.presentation.setting.FontSettingScreen
import com.example.alwaysbewithyou.presentation.setting.InformationUpdateScreen
import com.example.alwaysbewithyou.presentation.setting.MyPageScreen
import com.google.android.gms.maps.model.LatLng
import com.example.alwaysbewithyou.presentation.setting.NotificationSettingScreen
import com.google.firebase.auth.FirebaseAuth
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import com.example.alwaysbewithyou.presentation.map.tools.DirectionRepository
import com.example.alwaysbewithyou.presentation.map.TmapNetworkModule
import com.example.alwaysbewithyou.presentation.map.TransportType


@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier,
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
    val googleDirectionsApiService = remember { retrofit.create(GoogleDirectionsApiService::class.java) }
    val tmapDirectionsApiService = remember { TmapNetworkModule.tmapDirectionsApiService }

    val directionRepository = remember {
        DirectionRepository(
            tmapDirectionsApiService,
            googleDirectionsApiService,
            context
        )
    }

    val mapRouteViewModelFactory = remember {
        object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MapRouteViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MapRouteViewModel(directionRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    }

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

    val mapRouteViewModel: MapRouteViewModel = viewModel(
        factory = object : ViewModelProvider.Factory {
            override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
                if (modelClass.isAssignableFrom(MapRouteViewModel::class.java)) {
                    @Suppress("UNCHECKED_CAST")
                    return MapRouteViewModel(directionRepository) as T
                }
                throw IllegalArgumentException("Unknown ViewModel class")
            }
        }
    )

    NavHost(
        navController = navController,
        startDestination = Route.Splash.route,
        modifier = modifier
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
                onNavigateToHome = {
                    navController.navigate(Route.Home.route)
                },
                onNavigateToSignUp = {
                    navController.navigate(Route.SignUp.route)
                }
            )
        }

        composable(route = Route.Home.route) {
            HomeScreen()
        }

        composable(route = Route.Map.route) {
            MapScreen(
                onNavigateToMapList = { lat, lng ->
                    navController.navigate(Route.MapList.createRoute(lat, lng))
                },
                onPlaceClick = { placeId, currentGpsLocation ->
                    val startLat = currentGpsLocation?.latitude?.toFloat() ?: 37.5408f
                    val startLng = currentGpsLocation?.longitude?.toFloat() ?: 127.0793f

                    navController.navigate(
                        Route.MapDetail.createRouteWithStart(
                            placeId,
                            startLat,
                            startLng
                        )
                    )
                },
                viewModel = mapViewModel,
            )
        }

        composable(
            route = Route.MapList.route,
            arguments = listOf(
                navArgument("currentLat") {
                    type = NavType.FloatType
                    defaultValue = 0.0f
                },
                navArgument("currentLng") {
                    type = NavType.FloatType
                    defaultValue = 0.0f
                }
            )
        ) { backStackEntry ->
            val currentLat = backStackEntry.arguments?.getFloat("currentLat")?.toDouble()
            val currentLng = backStackEntry.arguments?.getFloat("currentLng")?.toDouble()
            val passedCurrentLocation = if (currentLat != null && currentLng != null) {
                LatLng(currentLat, currentLng)
            } else {
                null
            }

            MapListScreen(
                viewModel = mapViewModel,
                onNavigateBack = { navController.popBackStack() },
                navController = navController,
                currentLocation = passedCurrentLocation
            )
        }

        composable(
            route = Route.MapDetail.route,
            arguments = listOf(
                navArgument("placeId") { type = NavType.StringType },
                navArgument("startLat") { type = NavType.FloatType },
                navArgument("startLng") { type = NavType.FloatType }
            )
        ) { backStackEntry ->
            val placeId = backStackEntry.arguments?.getString("placeId")
            val startLat = backStackEntry.arguments?.getFloat("startLat")?.toDouble() ?: 0.0
            val startLng = backStackEntry.arguments?.getFloat("startLng")?.toDouble() ?: 0.0
            val initialStartLocation = LatLng(startLat, startLng)

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
                placeId = placeId,
                mapDetailViewModel = mapDetailViewModel,
                onNavigateBack = { navController.popBackStack() },
                onFindRouteClick = { destinationLatLng ->
                    navController.navigate(
                        Route.MapRoute.createRoute(
                            initialStartLocation.latitude.toFloat(),
                            initialStartLocation.longitude.toFloat(),
                            destinationLatLng.latitude.toFloat(),
                            destinationLatLng.longitude.toFloat(),
                            TransportType.TRANSIT
                        )
                    )
                }
            )
        }

        composable(
            route = Route.MapRoute.route,
            arguments = listOf(
                navArgument("startLat") { type = NavType.FloatType },
                navArgument("startLng") { type = NavType.FloatType },
                navArgument("endLat") { type = NavType.FloatType },
                navArgument("endLng") { type = NavType.FloatType },
                navArgument("transportType") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val startLat = backStackEntry.arguments?.getFloat("startLat")?.toDouble() ?: 0.0
            val startLng = backStackEntry.arguments?.getFloat("startLng")?.toDouble() ?: 0.0
            val endLat = backStackEntry.arguments?.getFloat("endLat")?.toDouble() ?: 0.0
            val endLng = backStackEntry.arguments?.getFloat("endLng")?.toDouble() ?: 0.0
            val transportTypeString = backStackEntry.arguments?.getString("transportType") ?: TransportType.DRIVING.name // 기본값 DRIVING

            val initialTransportType = try {
                TransportType.valueOf(transportTypeString)
            } catch (e: IllegalArgumentException) {
                Log.e("NavGraph", "Invalid TransportType received: $transportTypeString, defaulting to DRIVING")
                TransportType.DRIVING
            }

            Log.d("NavGraph", "MapRouteScreen received: start=($startLat, $startLng), end=($endLat, $endLng), type=$initialTransportType")

            val mapRouteViewModel: MapRouteViewModel = viewModel(factory = mapRouteViewModelFactory)

            MapRouteScreen(
                viewModel = mapRouteViewModel,
                startLocation = LatLng(startLat, startLng),
                endLocation = LatLng(endLat, endLng),
                initialTransportType = initialTransportType,
                onNavigateBack = { navController.popBackStack() }
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
                databaseViewModel = databaseViewModel,

                onLogout = {
                    navController.navigate(Route.Login.route) {
                        popUpTo(0)  // 백스택 제거
                    }
                }
            )
        }

        composable(route = Route.InformationUpdate.route) {
            InformationUpdateScreen(
                navController = navController,
                databaseViewModel = databaseViewModel
            )
        }

        composable(route = Route.NotificationSetting.route) {
            NotificationSettingScreen(
                navController = navController,
                databaseViewModel = databaseViewModel
            )
        }

        composable(route = Route.FontSetting.route) {
            FontSettingScreen(
                navController = navController,
                databaseViewModel = databaseViewModel
            )
        }

        composable(route = Route.Announcement.route) {
            AnnouncementScreen(
                navController = navController

            )
        }

        composable(route = Route.HomeDetail.route) {
            //HomeDetailScreen()
        }

        composable(route = Route.Schedule.route) {
            ScheduleScreen(
                navController = navController,
                databaseViewModel = databaseViewModel
            )
        }

        composable(route = Route.Result.route) {
            ResultScreen(
                navController = navController,
                databaseViewModel = databaseViewModel
            )
        }

        composable(route = Route.Review.route) {
            ReviewScreen(
                navController = navController,
                databaseViewModel = databaseViewModel
            )
        }
    }
}