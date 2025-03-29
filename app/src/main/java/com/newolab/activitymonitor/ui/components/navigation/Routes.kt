package com.newolab.activitymonitor.ui.components.navigation

object Routes {
    const val SPLASH = "splash"
    const val SIGN_IN = "sign_in"
    const val SIGN_UP = "sign_up"
    const val HOME = "home"

    // New Route for Equipment Detail with equipmentId as a path parameter
    const val EQUIPMENT_DETAIL = "equipment_detail/{equipmentId}"

    // Helper function to create a route with a specific equipmentId
    fun equipmentDetailRoute(equipmentId: String) = "equipment_detail/$equipmentId"
}
