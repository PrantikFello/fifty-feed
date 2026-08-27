package com.bitgranules.androidproject.CommonSystemConfig

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitgranules.androidproject.viewmodel.QuoteModelView

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AboutButton(viewModel: QuoteModelView) {

    // ***************
    val appName = "Fifty Feed"
    val version = "0.1.0"

    // *********

    val triggerSnackbar = LocalSnackbarTrigger.current
    var showAboutDialog by remember { mutableStateOf(false) }
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(appName)

        Button(
            onClick = { showAboutDialog = true }, colors = ButtonDefaults.buttonColors(
                containerColor = MaterialTheme.colorScheme.primaryContainer,
                contentColor = MaterialTheme.colorScheme.onSecondaryContainer
            )
        ) {
            Text("About(i)")
        }
    }
    if (showAboutDialog) {
        AlertDialog(
            onDismissRequest = { showAboutDialog = false },
            modifier = Modifier.padding(10.dp),
            title = {
                Text(
                    text = "$appName v$version",
                    fontSize = 17.sp,

                    )
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text("Fetch quotes from api and import images from device. Export vertical renderded Images.")
                    Text("This App is built using Jetpack Compose.")
                }
            },
            dismissButton = {
                Button(
                    onClick = { showAboutDialog = false }, colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.secondaryContainer
                    )
                ) {
                    Text(text = "Close")

                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        showAboutDialog = false
                        triggerSnackbar("Currently in Develowpment.")
                    },
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.onPrimary
                    )
                ) {
                    Text(text = "Github")
                }
            })
    }
}
