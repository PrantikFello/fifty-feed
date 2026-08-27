package com.bitgranules.androidproject.screens

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.bitgranules.androidproject.compositions.QuoteRenderDisplayLayer
import com.bitgranules.androidproject.viewmodel.QuoteModelView

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun MainScreen(viewModel: QuoteModelView) {
    val selectedBGIndex by viewModel.selectedBgIndex.collectAsState()
    val bgList by viewModel.bgImages.collectAsState()

    // ✅ Reactive Safety Net: Automatically clamps index down if list shrinks from underneath it
    LaunchedEffect(bgList.size) {
        if (bgList.isNotEmpty() && selectedBGIndex >= bgList.size) {
            viewModel.setSelectedBgIndex(bgList.lastIndex)
        }
    }

    // Handle early loading safely to prevent IndexOutOfBoundsException
    if (bgList.isEmpty()) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background),
            contentAlignment = Alignment.Center
        ) {
            QuoteRenderDisplayLayer(
                viewModel = viewModel,
                bgIsFirst = true,
                bgIsLast = true
            )
        }
        return // Fix: Ensure return is structured outside the component block context
    }
// Explicitly check bounds to protect your layout
    val safeIndex = selectedBGIndex.coerceIn(0, bgList.lastIndex)


    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
    ) {
        Box(modifier = Modifier.fillMaxSize()) {
            AsyncImage(
                model = bgList[safeIndex],
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(15.dp)
            ) {
                QuoteRenderDisplayLayer(
                    viewModel = viewModel,
                    bgIsFirst = safeIndex == 0,
                    bgIsLast = safeIndex == bgList.lastIndex
                )
            }
        }
    }
}