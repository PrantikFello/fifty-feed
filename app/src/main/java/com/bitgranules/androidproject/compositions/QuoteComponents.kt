package com.bitgranules.androidproject.compositions

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.text.TextAutoSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.bitgranules.androidproject.data.QuoteStruct
import com.bitgranules.androidproject.viewmodel.QuoteModelView

@Composable
fun QuoteUI(viewModel: QuoteModelView, currentQuote: QuoteStruct,isFirst: Boolean,isLast: Boolean) {
    Column(modifier = Modifier.fillMaxSize()) {
        val boxMod = Modifier
            .weight(1f).fillMaxWidth()
        Box(modifier = boxMod) { FetchButton(viewModel) }
        Box(modifier = boxMod, contentAlignment = Alignment.Center) {
            QuoteRenderComposition(currentQuote)
        }
        Box(modifier = boxMod) { BgTraverseButtons(viewModel, isFirst, isLast) }
    }
}

@Composable
fun QuoteRenderComposition(currentQuote: QuoteStruct) {
    Column(
        modifier = Modifier
            .fillMaxWidth() // FIXED: Bound parameters ensure cleaner layout calculations
            .wrapContentHeight()
            .padding(24.dp)
            .border(
                shape = RectangleShape, width = 1.dp, color = MaterialTheme.colorScheme.secondary
            ), verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = currentQuote.content, color = Color.White, autoSize = TextAutoSize.StepBased(
                minFontSize = 12.sp, maxFontSize = 24.sp, stepSize = 1.sp
            ), textAlign = TextAlign.Center, modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp)
        )
        Text(
            text = "— ${currentQuote.author}",
            color = Color.White.copy(alpha = 0.8f),
            textAlign = TextAlign.Center,
            autoSize = TextAutoSize.StepBased(
                minFontSize = 10.sp, maxFontSize = 16.sp, stepSize = 1.sp
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 12.dp)
        )
    }
}