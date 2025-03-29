package com.newolab.activitymonitor.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.newolab.activitymonitor.ui.theme.ActivityMonitorTheme
import com.newolab.activitymonitor.ui.components.navigation.AppNavigation
import com.newolab.activitymonitor.util.NetworkConnectivityObserver
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    @Inject
    lateinit var networkConnectivityObserver: NetworkConnectivityObserver

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ActivityMonitorTheme {
                val navController = rememberNavController()
                AppNavigation(
                    navController = navController,
                    networkConnectivityObserver = networkConnectivityObserver
                )
            }
        }
    }
}