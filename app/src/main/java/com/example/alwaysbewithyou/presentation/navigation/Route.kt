package com.example.alwaysbewithyou.presentation.navigation

sealed class Route(
    val route: String
) {
    data object Splash: Route(route = "splash")

    data object SignUp: Route(route = "signup")

    data object Login: Route(route = "login")

    data object Home: Route(route = "home")

    data object HomeDetail: Route(route = "homeDetail")

    data object Map: Route(route = "map")

    data object MapList: Route(route = "mapList")

    data object Call: Route(route = "call")

    data object Guardian: Route(route = "guardian")

    data object Setting: Route(route = "setting")

    data object NotificationSetting: Route(route = "notificationSetting")
}