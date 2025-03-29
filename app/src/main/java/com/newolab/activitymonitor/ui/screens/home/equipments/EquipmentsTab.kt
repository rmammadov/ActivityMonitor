package com.newolab.activitymonitor.ui.screens.home.equipments

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.newolab.activitymonitor.R
import com.newolab.activitymonitor.model.Equipment
import com.newolab.activitymonitor.model.EquipmentsResponse
import com.newolab.activitymonitor.ui.components.navigation.Routes
import com.newolab.activitymonitor.ui.screens.home.HomeViewModel
import com.newolab.activitymonitor.ui.screens.home.add.AddNewEquipment
import com.newolab.activitymonitor.ui.theme.PrimaryColor
import com.newolab.activitymonitor.ui.theme.PrimaryColorTransparent
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentsTab(
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    // Trigger fetch on view start
    LaunchedEffect(Unit) {
        homeViewModel.fetchEquipments()
    }

    val equipmentResponse by homeViewModel.equipmentsResponse.collectAsState()
    val errorMessage by homeViewModel.errorMessage.collectAsState()

    // Retrieve the localized "All" string
    val allString = stringResource(R.string.all)
    var filter by remember { mutableStateOf(allString) }
    var showDialog by remember { mutableStateOf(false) }

    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.equipments),
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                actions = {
                    IconButton(onClick = {
                        homeViewModel.fetchEquipments()
                    }) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = stringResource(R.string.refresh),
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    IconButton(onClick = { showDialog = true }) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = stringResource(R.string.add),
                            tint = Color.White,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = PrimaryColor,
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        },
        content = { paddingValues ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .fillMaxSize()
            ) {
                FilterSegmentedControl(filter = filter, onFilterChange = { filter = it })

                when (val response = equipmentResponse) {
                    is EquipmentsResponse.Success -> {
                        val active = stringResource(R.string.active)
                        val passive = stringResource(R.string.passive)

                        val filteredEquipments = when (filter) {
                            active -> response.equipmentList.filter { it.status }
                            passive -> response.equipmentList.filter { !it.status }
                            else -> response.equipmentList
                        }

                        EquipmentList(
                            equipment = filteredEquipments,
                            onItemClick = { equipmentId ->
                                navController.currentBackStackEntry?.savedStateHandle?.set("shouldRefresh", false)
                                navController.navigate(Routes.equipmentDetailRoute(equipmentId))
                            }
                        )
                    }
                    is EquipmentsResponse.Error -> {
                        EquipmentError(message = errorMessage)
                    }
                    null -> {
                        EquipmentLoading()
                    }
                }
            }
        }
    )

    if (showDialog) {
        AddNewEquipment(
            onDismiss = { showDialog = false },
            onConfirm = { title, description ->
                showDialog = false
                coroutineScope.launch {
                    homeViewModel.addEquipment(title, description)
                    homeViewModel.fetchEquipments()
                }
            }
        )
    }
}

@Composable
fun FilterSegmentedControl(
    filter: String,
    onFilterChange: (String) -> Unit
) {
    val all = stringResource(R.string.all)
    val active = stringResource(R.string.active)
    val passive = stringResource(R.string.passive)

    val options = listOf(all, active, passive)

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
            .background(
                color = PrimaryColorTransparent,
                shape = RoundedCornerShape(16.dp)
            )
            .padding(4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        options.forEach { option ->
            val isSelected = filter == option
            Text(
                text = option,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onFilterChange(option) }
                    .padding(vertical = 0.dp)
                    .background(
                        color = if (isSelected) PrimaryColor else Color.Transparent,
                        shape = RoundedCornerShape(12.dp)
                    )
                    .padding(vertical = 8.dp),
                color = if (isSelected) Color.White else PrimaryColor,
                style = MaterialTheme.typography.bodyMedium.copy(fontSize = 18.sp),
                textAlign = androidx.compose.ui.text.style.TextAlign.Center
            )
        }
    }
}

@Composable
fun EquipmentList(
    equipment: List<Equipment>,
    onItemClick: (String) -> Unit
) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier
            .fillMaxSize()
            .padding(8.dp),
        contentPadding = PaddingValues(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(equipment) { equip ->
            EquipmentItem(equipment = equip, onItemClick = onItemClick)
        }
    }
}

@Composable
fun EquipmentError(message: String?) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = message ?: stringResource(R.string.unknown_error),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}

@Composable
fun EquipmentLoading() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}
