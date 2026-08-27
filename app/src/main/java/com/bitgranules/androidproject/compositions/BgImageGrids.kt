package com.bitgranules.androidproject.compositions

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.SentimentNeutral
import androidx.compose.material.icons.filled.SentimentSatisfiedAlt
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import com.bitgranules.androidproject.viewmodel.QuoteModelView


@Composable
fun BgImageGridLayout(viewModel: QuoteModelView) {

    val imageList by viewModel.bgImages.collectAsStateWithLifecycle()
    val selectedBgIndex by viewModel.selectedBgIndex.collectAsStateWithLifecycle()
    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(5.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = imageList, key = { uri -> uri }) { uri ->
            ImageGridItem(
                viewModel = viewModel,
                imageUri = uri,
                isSelectedBg = uri == selectedBgIndex.let { imageList[it] })
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ImageGridItem(
    viewModel: QuoteModelView,
    imageUri: String,
    isSelectedBg: Boolean,
) {
    var showDeleteDialog by remember { mutableStateOf(false) }
    Box(modifier = Modifier.fillMaxSize()) {
        Card(
            border = if (isSelectedBg) {
                BorderStroke(width = 1.dp, color = MaterialTheme.colorScheme.primary)
            } else {
                null
            }
        ) {
            Box(modifier = Modifier.fillMaxSize()) {
                AsyncImage(
                    model = imageUri,
                    contentDescription = "selected image from Memory",
                    modifier = Modifier
                        .fillMaxSize()
                        .aspectRatio(1f),
                    contentScale = ContentScale.Fit
                )
                val controlPanelShape = CircleShape
                Column(modifier = Modifier.padding(10.dp)) {
                    //Delete
                    IconButton(
                        onClick = {
                            showDeleteDialog = true
                        },
                        Modifier
                            .clip(controlPanelShape)
                            .size(30.dp)
                            .background(color = MaterialTheme.colorScheme.background)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Delete,
                            contentDescription = "Delete this image uri string",
                            modifier = Modifier.size(25.dp),
                            tint = if (!showDeleteDialog) LocalContentColor.current else Color.Red,
                        )
                    }
                    Spacer(modifier = Modifier.height(15.dp))
                    //setBG
                    IconButton(
                        onClick = { viewModel.setSelectedBgByString(imageUri) },
                        Modifier
                            .clip(controlPanelShape)
                            .size(30.dp)
                            .background(color = MaterialTheme.colorScheme.background)

                    ) {
                        Icon(
                            imageVector = if (isSelectedBg) Icons.Filled.SentimentSatisfiedAlt else Icons.Filled.SentimentNeutral,
                            contentDescription = "bg image selection",
                            modifier = Modifier.size(25.dp),
                            tint = if (isSelectedBg) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.secondary
                        )
                    }
                }
            }
        }
        if (showDeleteDialog) {
            AlertDialog(
                onDismissRequest = { showDeleteDialog = false },
                title = { Text("Delete this Image path?") },
                confirmButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                        viewModel.deleteBgImageByString(imageUri)
                    }) { Text("Delete") }
                },
                modifier = Modifier.fillMaxWidth(),
                dismissButton = {
                    TextButton(onClick = {
                        showDeleteDialog = false
                    }) { Text("Cancel") }
                }

            )
        }
    }
}