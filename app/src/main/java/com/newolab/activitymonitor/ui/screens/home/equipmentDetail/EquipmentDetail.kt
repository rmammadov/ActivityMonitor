package com.newolab.activitymonitor.ui.screens.home.equipmentDetail

import CNCStatusButton
import EquipmentStatusIndicator
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.newolab.activitymonitor.ui.screens.home.HomeViewModel
import androidx.navigation.NavController
import kotlinx.coroutines.launch
import com.newolab.activitymonitor.ui.screens.home.equipments.EquipmentLoading
import com.newolab.activitymonitor.ui.theme.PrimaryColor
import com.newolab.activitymonitor.ui.theme.PrimaryColorTransparent
import java.text.SimpleDateFormat
import java.util.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.compose.foundation.Image
import com.newolab.activitymonitor.R

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EquipmentDetail(
    equipmentId: String?,
    navController: NavController,
    homeViewModel: HomeViewModel = hiltViewModel()
) {
    val coroutineScope = rememberCoroutineScope()
    val equipment by homeViewModel.getEquipmentById(equipmentId).collectAsState(initial = null)
    val updateStatusState by homeViewModel.updateStatusState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }

    var showWorkDialog by remember { mutableStateOf(false) }

    val successLabel = stringResource(R.string.status_updated_successfully)
    val fallbackError = stringResource(R.string.failed_to_update_status)
    val dismissLabel = stringResource(R.string.dismiss)

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.equipment_details),
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                navigationIcon = {
                    IconButton(onClick = { navController.popBackStack() }) {
                        Icon(
                            imageVector = Icons.Default.ArrowBack,
                            contentDescription = stringResource(R.string.back),
                            modifier = Modifier.size(24.dp),
                            tint = Color.White
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
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color.White
    ) { paddingValues ->
        equipment?.let { equip ->
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .padding(16.dp)
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.Top,
                horizontalAlignment = Alignment.Start
            ) {
                CNCStatusButton(
                    isActive = equip.status,
                    onToggle = {
                        if (!equip.status) {
                            showWorkDialog = true
                        } else {
                            coroutineScope.launch {
                                homeViewModel.updateEquipmentStatus(equip.id, !equip.status)
                            }
                        }
                    },
                    modifier = Modifier
                        .size(196.dp)
                        .align(Alignment.CenterHorizontally)
                )

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryColorTransparent,
                    tonalElevation = 1.dp
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp)
                    ) {
                        equip.name?.let {
                            SettingItem(label = stringResource(R.string.equipment_name), value = it)
                            Divider(
                                modifier = Modifier.padding(start = 16.dp),
                                color = Color.LightGray,
                                thickness = 0.5.dp
                            )
                        }

                        equip.description?.let {
                            SettingItem(label = stringResource(R.string.description), value = it)
                            Divider(
                                modifier = Modifier.padding(start = 16.dp),
                                color = Color.LightGray,
                                thickness = 0.5.dp
                            )
                        }

                        equip.model?.let { model ->
                            model.name?.let {
                                SettingItem(label = stringResource(R.string.model_name), value = it)
                                Divider(
                                    modifier = Modifier.padding(start = 16.dp),
                                    color = Color.LightGray,
                                    thickness = 0.5.dp
                                )
                            }

                            SettingItem(label = stringResource(R.string.maintenance_period), value = "${model.maintenancePeriod} days")
                            Divider(
                                modifier = Modifier.padding(start = 16.dp),
                                color = Color.LightGray,
                                thickness = 0.5.dp
                            )
                        }

                        equip.owner?.let {
                            SettingItem(label = stringResource(R.string.owner), value = it)
                            Divider(
                                modifier = Modifier.padding(start = 16.dp),
                                color = Color.LightGray,
                                thickness = 0.5.dp
                            )
                        }

                        equip.created?.let {
                            SettingItem(label = stringResource(R.string.created), value = formatDate(it))
                            Divider(
                                modifier = Modifier.padding(start = 16.dp),
                                color = Color.LightGray,
                                thickness = 0.5.dp
                            )
                        }

                        equip.updated?.let {
                            SettingItem(label = stringResource(R.string.last_updated), value = formatDate(it))
                            Divider(
                                modifier = Modifier.padding(start = 16.dp),
                                color = Color.LightGray,
                                thickness = 0.5.dp
                            )
                        }

                        equip.utilization?.let { utilization ->
                            val utilizationPercentage = (utilization * 100).toInt()
                            SettingItem(label = stringResource(R.string.utilization), value = "${utilizationPercentage}%")
                            LinearProgressIndicator(
                                progress = utilization.toFloat(),
                                color = PrimaryColor,
                                trackColor = PrimaryColorTransparent,
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 16.dp, vertical = 8.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                Surface(
                    modifier = Modifier
                        .fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    color = PrimaryColorTransparent,
                    tonalElevation = 1.dp
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 12.dp, horizontal = 16.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = stringResource(R.string.status),
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Normal,
                            modifier = Modifier.weight(1f),
                            color = PrimaryColor
                        )
                        EquipmentStatusIndicator(status = equip.status.toString())
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))

                when (updateStatusState) {
                    is HomeViewModel.UpdateStatusState.Loading -> {
                        CircularProgressIndicator(
                            modifier = Modifier.align(Alignment.CenterHorizontally)
                        )
                    }
                    is HomeViewModel.UpdateStatusState.Success -> {
                        LaunchedEffect(Unit) {
                            snackbarHostState.showSnackbar(successLabel)
                        }
                    }
                    is HomeViewModel.UpdateStatusState.Error -> {
                        LaunchedEffect(Unit) {
                            snackbarHostState.showSnackbar(
                                message = (updateStatusState as HomeViewModel.UpdateStatusState.Error).message
                                    ?: fallbackError,
                                actionLabel = dismissLabel
                            )
                        }
                    }
                    else -> { /* No Action Needed */ }
                }
            }

            if (showWorkDialog) {
                SelectWorkDialog(
                    onDismiss = { showWorkDialog = false },
                    onConfirm = { selectedOption ->
                        coroutineScope.launch {
                            homeViewModel.updateEquipmentStatus(equip.id, true)
                        }
                        showWorkDialog = false
                    }
                )
            }
        } ?: EquipmentLoading()
    }
}

@Composable
fun SettingItem(label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {}
            .padding(vertical = 12.dp, horizontal = 16.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = label,
            fontSize = 17.sp,
            fontWeight = FontWeight.Normal,
            modifier = Modifier.weight(1f),
            color = PrimaryColor
        )
        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Normal,
            color = PrimaryColor,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

fun formatDate(dateString: String?): String {
    return if (!dateString.isNullOrEmpty()) {
        try {
            val parser = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.getDefault())
            val formatter = SimpleDateFormat("dd MMM yyyy, HH:mm", Locale.getDefault())
            val date = parser.parse(dateString)
            date?.let { formatter.format(it) } ?: "N/A"
        } catch (e: Exception) {
            // Use string resource if desired: stringResource(R.string.invalid_date)
            "Invalid Date"
        }
    } else {
        "N/A"
    }
}

@Composable
fun SelectWorkDialog(
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedWorkOption by remember { mutableStateOf<String?>(null) }

    val newWork = stringResource(R.string.new_work)
    val completeExisting = stringResource(R.string.complete_existing)
    val fixTheIssue = stringResource(R.string.fix_the_issue)
    val other = stringResource(R.string.other)

    val workOptions = listOf(newWork, completeExisting, fixTheIssue, other)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = stringResource(R.string.select_the_work)) },
        text = {
            Column {
                workOptions.forEach { option ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedWorkOption = option }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedWorkOption == option,
                            onClick = { selectedWorkOption = option }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = option)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    selectedWorkOption?.let { onConfirm(it) }
                }
            ) {
                Text(stringResource(R.string.confirm))
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.cancel))
            }
        }
    )
}
