package com.newolab.activitymonitor.ui.screens.signin

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import com.newolab.activitymonitor.R
import com.newolab.activitymonitor.ui.theme.PrimaryColor

@Composable
fun SignInForm(
    username: String,
    onUsernameChange: (String) -> Unit,
    password: String,
    onPasswordChange: (String) -> Unit,
    onSubmit: (String, String) -> Unit,
    showError: Boolean = false
) {
    // State variable to keep track of password visibility
    var passwordVisible by remember { mutableStateOf(false) }

    val usernameLabel = stringResource(R.string.username_label)
    val passwordLabel = stringResource(R.string.password_label)
    val signInText = stringResource(R.string.sign_in)
    val showPasswordText = stringResource(R.string.show_password)
    val hidePasswordText = stringResource(R.string.hide_password)
    val appLogoDescription = stringResource(R.string.app_logo_description)

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Logo Image at the Top
        Image(
            painter = painterResource(id = R.drawable.logo_dark),
            contentDescription = appLogoDescription,
            modifier = Modifier.size(128.dp)
        )

        Spacer(modifier = Modifier.height(96.dp))

        // Username TextField
        TextField(
            value = username,
            onValueChange = onUsernameChange,
            label = { Text(usernameLabel) },
            modifier = Modifier.fillMaxWidth(),
            isError = showError // Highlight in red when there's an error
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Password TextField with Show/Hide Password Button
        TextField(
            value = password,
            onValueChange = onPasswordChange,
            label = { Text(passwordLabel) },
            modifier = Modifier.fillMaxWidth(),
            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
            trailingIcon = {
                val image = if (passwordVisible) Icons.Filled.Visibility else Icons.Filled.VisibilityOff
                val description = if (passwordVisible) hidePasswordText else showPasswordText

                IconButton(onClick = { passwordVisible = !passwordVisible }) {
                    Icon(imageVector = image, contentDescription = description)
                }
            },
            isError = showError // Highlight in red when there's an error
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Sign In Button
        Button(
            onClick = { onSubmit(username, password) },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                containerColor = PrimaryColor,
                contentColor = Color.White
            )
        ) {
            Text(signInText)
        }
    }
}
