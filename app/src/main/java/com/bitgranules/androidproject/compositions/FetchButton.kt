package com.bitgranules.androidproject.compositions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Api
import androidx.compose.material.icons.filled.Downloading
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bitgranules.androidproject.viewmodel.QuoteModelView

@Composable
fun FetchButton(viewModel: QuoteModelView) {
    val isFetching = viewModel.isFetching.collectAsState().value
    Row {
        IconButton(
            onClick = { viewModel.fetchFreshQuoteBatch() },
            Modifier
                .padding(15.dp)
                .clip(CircleShape)
                .size(65.dp)
                .background(
                    Color.Black
                )
                .border(color = Color.White, shape = CircleShape, width = 2.dp)
        ) {
            Icon(
                imageVector = if (!isFetching) Icons.Default.Api else Icons.Default.Downloading,
                contentDescription = "Fetch Quote",
                modifier = Modifier.size(35.dp),
                tint = if (!isFetching) Color.White else Color.Red
            )
        }

    }
}