package com.newolab.activitymonitor.ui.screens.home

import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavHostController
import com.newolab.activitymonitor.ui.components.navigation.bottom.BottomNavItem
import com.newolab.activitymonitor.ui.components.navigation.bottom.BottomNavigationBar
import com.newolab.activitymonitor.ui.components.navigation.bottom.bottomNavItems
import com.newolab.activitymonitor.ui.screens.home.equipments.EquipmentsTab
import com.newolab.activitymonitor.ui.screens.home.user.UserTab

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    navController: NavHostController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val bottomNavItemsList = bottomNavItems
    var selectedItem by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            BottomNavigationBar(
                items = bottomNavItemsList,
                currentIndex = selectedItem,
                onItemSelected = { index ->
                    selectedItem = index
                }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .padding(paddingValues)
                .fillMaxSize()
        ) {
            when (val current = bottomNavItemsList.getOrNull(selectedItem)) {
                is BottomNavItem.Equipments -> {
                    EquipmentsTab(
                        homeViewModel = homeViewModel,
                        navController = navController
                    )
                }
                is BottomNavItem.User -> {
                    UserTab(
                        homeViewModel = homeViewModel,
                        navController = navController
                    )
                }
                else -> {
                    // Default content or error
                    Text("Invalid selection", modifier = Modifier.align(Alignment.Center))
                }
            }
        }
    }
}
