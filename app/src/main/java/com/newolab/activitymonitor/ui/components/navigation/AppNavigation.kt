package com.newolab.activitymonitor.ui.components.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.*
import androidx.navigation.navArgument
import com.newolab.activitymonitor.ui.components.base.ConnectivityAwareContent
import com.newolab.activitymonitor.ui.screens.home.HomeScreen
import com.newolab.activitymonitor.ui.screens.home.HomeViewModel
import com.newolab.activitymonitor.ui.screens.home.equipmentDetail.EquipmentDetail
import com.newolab.activitymonitor.ui.screens.signin.SignInScreen
import com.newolab.activitymonitor.ui.screens.signup.SignUpScreen
import com.newolab.activitymonitor.ui.screens.splash.SplashScreen
import com.newolab.activitymonitor.util.NetworkConnectivityObserver

@Composable
fun AppNavigation(
    navController: NavHostController,
    networkConnectivityObserver: NetworkConnectivityObserver,
    modifier: Modifier = Modifier
) {
    // Provide HomeViewModel at this level
    val homeViewModel: HomeViewModel = viewModel()

    ConnectivityAwareContent(
        networkConnectivityObserver = networkConnectivityObserver,
        content = {
            NavHost(
                navController = navController,
                startDestination = Routes.SPLASH,
                modifier = modifier
            ) {
                composable(Routes.SPLASH) {
                    SplashScreen(
                        navController = navController,
                        onNavigateToSignIn = { navController.navigate(Routes.SIGN_IN) },
                        onNavigateToHome = { navController.navigate(Routes.HOME) }
                    )
                }
                composable(Routes.SIGN_IN) {
                    SignInScreen(
                        navController = navController
                    )
                }
                composable(Routes.SIGN_UP) {
                    SignUpScreen(
                        navController = navController,
                        onSignUpSuccess = { navController.navigate(Routes.HOME) }
                    )
                }
                composable(Routes.HOME) {
                    // Pass homeViewModel to HomeScreen
                    HomeScreen(
                        navController = navController,
                        homeViewModel = homeViewModel
                    )
                }

                // Composable for EquipmentDetailScreen
                composable(
                    route = Routes.EQUIPMENT_DETAIL,
                    arguments = listOf(navArgument("equipmentId") { type = NavType.StringType })
                ) { backStackEntry ->
                    val equipmentId = backStackEntry.arguments?.getString("equipmentId")

                    // Ensure equipmentId is not null
                    if (equipmentId != null) {
                        // Pass homeViewModel to EquipmentDetail
                        EquipmentDetail(
                            equipmentId = equipmentId,
                            navController = navController,
                            homeViewModel = homeViewModel
                        )
                    } else {
                        // Handle the case where equipmentId is null
                        // You can navigate back or show an error screen
                        navController.popBackStack()
                    }
                }
            }
        }
    )
}
