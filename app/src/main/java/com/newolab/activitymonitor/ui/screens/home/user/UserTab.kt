package com.newolab.activitymonitor.ui.screens.home.user

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.newolab.activitymonitor.BuildConfig
import com.newolab.activitymonitor.R
import com.newolab.activitymonitor.model.Me
import com.newolab.activitymonitor.ui.screens.home.HomeViewModel
import com.newolab.activitymonitor.ui.theme.PrimaryColor
import com.newolab.activitymonitor.ui.theme.PrimaryColorTransparent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserTab(
    homeViewModel: HomeViewModel,
    navController: NavController
) {
    // Trigger fetch on view start
    LaunchedEffect(Unit) {
        homeViewModel.fetchCurrentUser()
    }

    val user by homeViewModel.currentMe.collectAsState()
    val errorMessage by homeViewModel.errorMessage.collectAsState()
    val currentLanguage by homeViewModel.currentLanguage.collectAsState()

    // Update the context based on the current language
    val baseContext = LocalContext.current
    val localizedContext = remember(currentLanguage) {
        homeViewModel.updateLocale(baseContext, currentLanguage)
    }

    // State for showing the language selection dialog
    var showLanguageDialog by remember { mutableStateOf(false) }

    CompositionLocalProvider(LocalContext provides localizedContext) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        Text(
                            text = stringResource(R.string.user_title),
                            fontWeight = FontWeight.Bold,
                            fontSize = 17.sp
                        )
                    },
                    actions = {
                        IconButton(onClick = {
                            homeViewModel.fetchCurrentUser()
                        }) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = stringResource(R.string.refresh),
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
                if (user != null) {
                    LazyColumn(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                        contentPadding = PaddingValues(vertical = 16.dp)
                    ) {
                        // Profile Section
                        item {
                            ProfileSection(user = user!!)
                        }

                        // User Details Section
                        item {
                            UserDetailsList(
                                user = user!!,
                                currentLanguage = currentLanguage,
                                onLanguageClick = { showLanguageDialog = true }
                            )
                        }

                        // Logout Button
                        item {
                            Spacer(modifier = Modifier.height(24.dp))
                            LogoutButton {
                                homeViewModel.signOut(navController)
                            }
                        }
                    }
                } else {
                    // Loading or Error State
                    Box(
                        modifier = Modifier
                            .padding(paddingValues)
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        if (errorMessage != null) {
                            Text(
                                text = errorMessage ?: stringResource(R.string.failed_to_load_user_data),
                                color = MaterialTheme.colorScheme.error,
                                style = MaterialTheme.typography.bodyLarge
                            )
                        } else {
                            CircularProgressIndicator()
                        }
                    }
                }
            }
        )

        if (showLanguageDialog) {
            LanguageSelectionDialog(
                currentLanguage = currentLanguage,
                onDismiss = { showLanguageDialog = false },
                onConfirm = { selectedLang ->
                    homeViewModel.saveLanguage(selectedLang)
                    showLanguageDialog = false
                }
            )
        }
    }
}

@Composable
fun ProfileSection(user: Me) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Image(
            painter = painterResource(id = R.drawable.ic_profile_placeholder),
            contentDescription = stringResource(R.string.profile_picture),
            modifier = Modifier
                .size(100.dp)
                .clip(CircleShape),
            contentScale = ContentScale.Crop
        )

        Spacer(modifier = Modifier.height(12.dp))

        Text(
            text = "${user.firstName ?: "Seymur"} ${user.lastName ?: "Mammadli"}",
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 24.sp, fontWeight = FontWeight.Bold),
            color = PrimaryColor
        )
    }
}

@Composable
fun UserDetailsList(user: Me, currentLanguage: String, onLanguageClick: () -> Unit) {
    val emailLabel = stringResource(R.string.email)
    val registrationLabel = stringResource(R.string.registration)
    val roleLabel = stringResource(R.string.role)
    val administratorLabel = stringResource(R.string.administrator)
    val userRoleLabel = stringResource(R.string.user_role)
    val positionLabel = stringResource(R.string.position)
    val factoryLabel = stringResource(R.string.factory)
    val languageLabel = stringResource(R.string.language)
    val appVersionLabel = stringResource(R.string.app_version)

    val roleValue = if (user.isAdmin) administratorLabel else userRoleLabel

    val appVersion = "v${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})"

    val items = listOf(
        emailLabel to user.email,
        registrationLabel to (user.registration ?: "N/A"),
        roleLabel to roleValue,
        positionLabel to (user.position ?: "N/A"),
        factoryLabel to (user.factory ?: "N/A"),
        languageLabel to currentLanguage,
        appVersionLabel to appVersion
    )

    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = PrimaryColorTransparent,
        tonalElevation = 1.dp
    ) {
        Column {
            items.forEachIndexed { index, item ->
                val isLanguageRow = item.first == languageLabel
                SettingsListItem(
                    title = item.first,
                    subtitle = item.second,
                    isFirstItem = index == 0,
                    isLastItem = index == items.size - 1,
                    onClick = {
                        if (isLanguageRow) {
                            onLanguageClick()
                        }
                    }
                )
            }
        }
    }
}

@Composable
fun SettingsListItem(
    title: String,
    subtitle: String,
    isFirstItem: Boolean,
    isLastItem: Boolean,
    onClick: () -> Unit = {}
) {
    val shape = when {
        isFirstItem && isLastItem -> RoundedCornerShape(12.dp)
        isFirstItem -> RoundedCornerShape(topStart = 12.dp, topEnd = 12.dp)
        isLastItem -> RoundedCornerShape(bottomStart = 12.dp, bottomEnd = 12.dp)
        else -> RoundedCornerShape(0.dp)
    }

    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = shape,
        color = Color.Transparent
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onClick() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.bodyLarge.copy(fontSize = 17.sp),
                    color = PrimaryColor
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 15.sp),
                    color = PrimaryColor
                )
            }
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = PrimaryColor
            )
        }
    }
    if (!isLastItem) {
        Divider(
            modifier = Modifier.padding(start = 16.dp),
            color = Color.LightGray,
            thickness = 0.5.dp
        )
    }
}

@Composable
fun LogoutButton(onLogout: () -> Unit) {
    Surface(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp),
        shape = RoundedCornerShape(12.dp),
        color = PrimaryColorTransparent,
        tonalElevation = 1.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onLogout() }
                .padding(horizontal = 16.dp, vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = stringResource(R.string.logout),
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontSize = 17.sp,
                    color = MaterialTheme.colorScheme.error
                )
            )
            Spacer(modifier = Modifier.weight(1f))
            Icon(
                imageVector = Icons.Default.ChevronRight,
                contentDescription = null,
                tint = Color.Red
            )
        }
    }
}

@Composable
fun LanguageSelectionDialog(
    currentLanguage: String,
    onDismiss: () -> Unit,
    onConfirm: (String) -> Unit
) {
    var selectedLanguage by remember { mutableStateOf(currentLanguage) }
    val languageOptions = listOf("az", "en")
    val selectLanguageTitle = stringResource(R.string.select_language)
    val confirmText = stringResource(R.string.confirm)
    val cancelText = stringResource(R.string.cancel)
    val azName = stringResource(R.string.language_az)
    val enName = stringResource(R.string.language_en)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = selectLanguageTitle) },
        text = {
            Column {
                languageOptions.forEach { lang ->
                    val langName = when (lang) {
                        "az" -> azName
                        "en" -> enName
                        else -> lang
                    }
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { selectedLanguage = lang }
                            .padding(8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = selectedLanguage == lang,
                            onClick = { selectedLanguage = lang }
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(text = langName)
                    }
                }
            }
        },
        confirmButton = {
            TextButton(
                onClick = { onConfirm(selectedLanguage) }
            ) {
                Text(confirmText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelText)
            }
        }
    )
}
