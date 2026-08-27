package com.bitgranules.androidproject.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.bitgranules.androidproject.CommonSystemConfig.AboutButton
import com.bitgranules.androidproject.CommonSystemConfig.DarkThemeButton
import com.bitgranules.androidproject.viewmodel.QuoteModelView

@Composable
fun SettingsScreen(viewModel: QuoteModelView) {
    Column(
        modifier = Modifier.fillMaxSize().padding(horizontal = 15.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
        horizontalAlignment = Alignment.Start
    ) {
        AboutButton(viewModel)
        DarkThemeButton(viewModel)
    }
}