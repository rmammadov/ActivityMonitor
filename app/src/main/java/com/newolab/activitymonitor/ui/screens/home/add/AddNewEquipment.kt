package com.newolab.activitymonitor.ui.screens.home.add

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.newolab.activitymonitor.R

@Composable
fun AddNewEquipment(
    onDismiss: () -> Unit,
    onConfirm: (String, String) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    val isAddEnabled = title.isNotBlank() && description.isNotBlank()

    val newEquipmentText = stringResource(R.string.new_equipment)
    val titleLabel = stringResource(R.string.title)
    val descriptionLabel = stringResource(R.string.description)
    val addText = stringResource(R.string.add)
    val cancelText = stringResource(R.string.cancel)

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(text = newEquipmentText) },
        text = {
            Column {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text(titleLabel) },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = { Text(descriptionLabel) },
                    singleLine = false,
                    maxLines = 4,
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(
                onClick = {
                    onConfirm(title.trim(), description.trim())
                },
                enabled = isAddEnabled
            ) {
                Text(addText)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text(cancelText)
            }
        }
    )
}
