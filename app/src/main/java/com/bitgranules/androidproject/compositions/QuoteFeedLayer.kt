package com.bitgranules.androidproject.compositions

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.bitgranules.androidproject.viewmodel.QuoteModelView
import kotlinx.coroutines.flow.distinctUntilChanged

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun QuoteRenderDisplayLayer(viewModel: QuoteModelView, bgIsFirst: Boolean, bgIsLast: Boolean) {
    val quotesList by viewModel.cachedQuoteList.collectAsStateWithLifecycle()
    val currentIndex by viewModel.currentQuoteIndex.collectAsStateWithLifecycle()

    val pagerState = rememberPagerState(pageCount = { quotesList.size })
    // Track current active drag gesture orientations dynamically


    LaunchedEffect(pagerState) {
        snapshotFlow { pagerState.settledPage }.distinctUntilChanged().collect { page ->
            if (quotesList.isNotEmpty() && page >= 0 && page != currentIndex) {
                viewModel.setCurrentQuoteIndex(page)

            }
        }
    }

    LaunchedEffect(currentIndex) {
        if (quotesList.isNotEmpty() && !pagerState.isScrollInProgress && pagerState.currentPage != currentIndex) {
            pagerState.animateScrollToPage(currentIndex)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        if (quotesList.isEmpty()) {

            Box(
                Modifier
                    .fillMaxWidth()
                    .wrapContentHeight()
                    .wrapContentHeight(),
                contentAlignment = Alignment.Center
            ) {
                QuoteUI(
                    viewModel,
                    viewModel.defaultQuote.collectAsState().value,
                    bgIsFirst,
                    bgIsLast
                )
            }
        } else {
            HorizontalPager(
                state = pagerState, modifier = Modifier.fillMaxSize()
            ) { pageIndex ->
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    QuoteUI(
                        viewModel = viewModel,
                        currentQuote = quotesList[pageIndex],
                        bgIsFirst, bgIsLast
                    )
                }
            }

        }

    }
}
