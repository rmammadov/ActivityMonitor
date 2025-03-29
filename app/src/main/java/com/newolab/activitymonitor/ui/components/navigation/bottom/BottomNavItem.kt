package com.newolab.activitymonitor.ui.components.navigation.bottom

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import com.newolab.activitymonitor.R

sealed class BottomNavItem(
    val route: String,
    @StringRes val labelRes: Int,
    @DrawableRes val icon: Int
) {
    object Equipments : BottomNavItem(
        route = "equipments",
        labelRes = R.string.bottom_nav_equipments,
        icon = R.drawable.ic_equipments
    )

    object User : BottomNavItem(
        route = "me",
        labelRes = R.string.bottom_nav_me,
        icon = R.drawable.ic_user
    )
}
