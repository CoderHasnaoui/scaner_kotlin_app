package com.example.mimi_projet_zentech.ui.theme.ui.deniedScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun ManualEntryDialog(
    show: Boolean,
    onDismiss: () -> Unit,
    onNext: (String) -> Unit
) {
    if (!show) return

    // Use rememberSaveable so text doesn't disappear on rotate
    var passId by rememberSaveable { mutableStateOf("") }

    Dialog(
        onDismissRequest = onDismiss ,
        properties = DialogProperties(
            dismissOnBackPress = true,
            dismissOnClickOutside = false,
            // UsePlatformDefaultWidth = false allows the card to be wider if needed
            usePlatformDefaultWidth = true
        )
    ) {
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp), // Add some outer padding
            shape = RoundedCornerShape(20.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.background)
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Header
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        color = MaterialTheme.colorScheme.onBackground,
                        text = "Trouble scanning?Enter manually", // Added newline for better fit
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f) // Give text room
                    )

                    IconButton(onClick = {
                        passId = "" // Clear text on close
                        onDismiss()
                    }) {
                        Icon(Icons.Default.Close, contentDescription = "Close")
                    }
                }

                // Input
                OutlinedTextField(
                    value = passId,
                    onValueChange = { passId = it },
                    modifier = Modifier.fillMaxWidth(),
                    placeholder = { Text("Enter Pass ID ") },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp)
                )

                // Button
                Button(
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color(0XFF1851C5),
                        disabledContainerColor = Color.Gray.copy(alpha = 0.5f) // Style for disabled
                    ),
                    onClick = {
                        if (passId.isNotBlank()) {
                            onNext(passId)
                        }
                    },
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(50.dp),
                    // 🔹 Only enable if user typed something
                    enabled = passId.isNotBlank()
                ) {
                    Text(
                        "Next",
                        color = Color.White,
                        modifier = Modifier.padding(vertical = 4.dp)
                    )
                }
            }
        }
    }
}
