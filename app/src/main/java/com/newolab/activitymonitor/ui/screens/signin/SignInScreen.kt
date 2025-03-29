package com.newolab.activitymonitor.ui.screens.signin

import androidx.compose.runtime.*
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.newolab.activitymonitor.ui.components.LoadingIndicator
import com.newolab.activitymonitor.ui.components.navigation.Routes
import androidx.compose.material3.*
import androidx.compose.ui.res.stringResource
import com.newolab.activitymonitor.R

@Composable
fun SignInScreen(
    navController: NavController,
    signInViewModel: SignInViewModel = hiltViewModel()
) {
    var username by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    val signInState by signInViewModel.signInState.collectAsState()
    var showErrorDialog by remember { mutableStateOf(false) }

    // Observe signInState changes
    LaunchedEffect(signInState) {
        when (signInState) {
            is SignInViewModel.SignInState.Success -> {
                // Navigate to HomeScreen
                navController.navigate(Routes.HOME) {
                    popUpTo(Routes.SIGN_IN) { inclusive = true }
                }
            }
            is SignInViewModel.SignInState.Error -> {
                showErrorDialog = true
            }
            else -> { /* No action needed */ }
        }
    }

    if (showErrorDialog && signInState is SignInViewModel.SignInState.Error) {
        val errorTitle = stringResource(R.string.sign_in_error_title)
        val errorMessage = (signInState as SignInViewModel.SignInState.Error).message
            ?: stringResource(R.string.sign_in_error_message)

        AlertDialog(
            onDismissRequest = { showErrorDialog = false },
            title = { Text(errorTitle) },
            text = { Text(errorMessage) },
            confirmButton = {
                TextButton(onClick = { showErrorDialog = false }) {
                    Text(stringResource(R.string.confirm))
                }
            }
        )
    }

    val showErrorHighlight = signInState is SignInViewModel.SignInState.Error

    when (signInState) {
        is SignInViewModel.SignInState.Loading -> {
            LoadingIndicator()
        }
        else -> {
            SignInForm(
                username = username,
                onUsernameChange = {
                    username = it
                    if (signInState is SignInViewModel.SignInState.Error) {
                        signInViewModel.resetState()
                    }
                },
                password = password,
                onPasswordChange = {
                    password = it
                    if (signInState is SignInViewModel.SignInState.Error) {
                        signInViewModel.resetState()
                    }
                },
                onSubmit = { u, p -> signInViewModel.signIn(u, p) },
                showError = showErrorHighlight
            )
        }
    }
}
