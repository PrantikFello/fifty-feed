package com.bitgranules.androidproject.compositions

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.bitgranules.androidproject.viewmodel.QuoteModelView

@Composable
fun BgTraverseButtons(viewModel: QuoteModelView, isFirst: Boolean, isLast: Boolean) {
    Column {
        // PREVIOUS BACKGROUND BUTTON
        IconButton(
            onClick = { if (!isFirst) viewModel.navigateToPreviousBg() },
            modifier = Modifier
                .clip(CircleShape)
                .size(50.dp)
                .background(Color.Black)
                .border(color = Color.Transparent, shape = CircleShape, width = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowUp,
                contentDescription = "Previous Background",
                modifier = Modifier.size(40.dp),
                tint = if (isFirst) Color.Gray else Color.White // White when navigation is possible
            )
        }

        Spacer(Modifier.height(15.dp))

        // NEXT BACKGROUND BUTTON
        IconButton(
            onClick = { if (!isLast) viewModel.navigateToNextBg() },
            modifier = Modifier
                .clip(CircleShape)
                .size(50.dp)
                .background(Color.Black)
                .border(color = Color.Transparent, shape = CircleShape, width = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.KeyboardArrowDown,
                contentDescription = "Next Background",
                modifier = Modifier.size(40.dp),
                tint = if (isLast) Color.Gray else Color.White // White when navigation is possible
            )
        }
    }
}