package com.newolab.activitymonitor.ui.components

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp

/**
 * A composable function that displays a toggle button for equipment status.
 *
 * @param currentStatus The current status of the equipment ("true" or "false").
 * @param onStatusChange Lambda function invoked when the status is toggled.
 * @param isEnabled Determines whether the toggle is interactive. Defaults to true.
 */
@Composable
fun StatusToggleButton(
    currentStatus: String?,
    onStatusChange: (String) -> Unit,
    isEnabled: Boolean = true
) {
    // Convert the status string to a boolean
    val isActive = currentStatus?.toBoolean() ?: false

    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
        modifier = Modifier.fillMaxWidth()
    ) {
        // "Passive" Label
        Text(
            text = "Passive",
            style = MaterialTheme.typography.bodyMedium,
            color = if (!isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )

        Spacer(modifier = Modifier.width(8.dp))

        // Toggle Switch
        Switch(
            checked = isActive,
            onCheckedChange = { isChecked ->
                val newStatus = if (isChecked) "true" else "false"
                onStatusChange(newStatus)
            },
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color(0xFF4CAF50), // Green for Active
                uncheckedThumbColor = Color(0xFFF44336) // Red for Passive
            ),
            enabled = isEnabled
        )

        Spacer(modifier = Modifier.width(8.dp))

        // "Active" Label
        Text(
            text = "Active",
            style = MaterialTheme.typography.bodyMedium,
            color = if (isActive) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
        )
    }
}