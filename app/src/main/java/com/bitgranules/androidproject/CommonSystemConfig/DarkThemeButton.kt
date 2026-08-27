package com.bitgranules.androidproject.CommonSystemConfig

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitgranules.androidproject.viewmodel.QuoteModelView

@Composable
fun DarkThemeButton(viewmodel: QuoteModelView) {
    val isDarkMode by viewmodel.isDarkMode.collectAsStateWithLifecycle()
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text("Dark Mode : ${if (isDarkMode) "ON" else "OFF"} ", modifier = Modifier)
        IconButton(
            onClick = {
                viewmodel.toggleDarkMode()
            }, modifier = Modifier.padding(horizontal = 20.dp)
        ) {
            Icon(
                imageVector = if (isDarkMode) Icons.Default.DarkMode else Icons.Default.LightMode,
                contentDescription = "dark mode indicator",
                tint = MaterialTheme.colorScheme.primary,
                )
        }

    }
}