package com.example.alwaysbewithyou.presentation.navigation

import com.example.alwaysbewithyou.presentation.map.TransportType

sealed class Route(
    val route: String
) {
    data object Splash: Route(route = "splash")

    data object SignUp: Route(route = "signup")

    data object Login: Route(route = "login")

    data object Home: Route(route = "home")

    data object HomeDetail: Route(route = "homeDetail")

    data object Map: Route(route = "map")

    data object MapList : Route("mapList?currentLat={currentLat}&currentLng={currentLng}") {
        fun createRoute(currentLat: Float?, currentLng: Float?) =
            "mapList?currentLat=${currentLat ?: "null"}&currentLng=${currentLng ?: "null"}"
    }

    data object MapDetail : Route("mapDetail/{placeId}?startLat={startLat}&startLng={startLng}") {
        fun createRoute(placeId: String) = "mapDetail/$placeId"
        fun createRouteWithStart(placeId: String, startLat: Float, startLng: Float) =
            "mapDetail/$placeId?startLat=$startLat&startLng=$startLng"
    }

    data object MapRoute: Route(route = "mapRoute/{startLat}/{startLng}/{endLat}/{endLng}/{transportType}") {
        fun createRoute(
            startLat: Float,
            startLng: Float,
            endLat: Float,
            endLng: Float,
            transportType: TransportType
        ): String {
            return "mapRoute/$startLat/$startLng/$endLat/$endLng/${transportType.name}"
        }
    }

    data object Call: Route(route = "call")

    data object Guardian: Route(route = "guardian")

    data object Setting: Route(route = "setting")

    data object NotificationSetting: Route(route = "notificationSetting")

    data object FontSetting: Route(route = "fontSetting")

    data object Announcement: Route(route = "announcement")
}