package com.newolab.activitymonitor.ui.screens.splash

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import kotlinx.coroutines.delay
import androidx.navigation.NavController
import com.newolab.activitymonitor.R
import com.newolab.activitymonitor.ui.components.navigation.Routes
import com.newolab.activitymonitor.ui.theme.PrimaryColor

@Composable
fun SplashScreen(
    navController: NavController,
    onNavigateToSignIn: () -> Unit,
    onNavigateToHome: () -> Unit,
    splashViewModel: SplashViewModel = hiltViewModel()
) {
    // Duration of the splash screen in milliseconds
    val splashDuration = 2000L

    // Observe authentication state
    val isLoggedIn by remember { splashViewModel.isLoggedIn }

    // Start a coroutine to delay navigation
    LaunchedEffect(key1 = true) {
        delay(splashDuration)
        if (isLoggedIn) {
            navController.navigate(Routes.HOME) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        } else {
            navController.navigate(Routes.SIGN_IN) {
                popUpTo(Routes.SPLASH) { inclusive = true }
            }
        }
    }

    // Splash screen UI
    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(PrimaryColor),
        contentAlignment = Alignment.Center
    ) {
        Image(
            painter = painterResource(id = R.drawable.logo_white),
            contentDescription = "App Logo",
            modifier = Modifier.size(128.dp)
        )
    }
}