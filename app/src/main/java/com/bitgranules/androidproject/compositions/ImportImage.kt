package com.bitgranules.androidproject.compositions

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.result.contract.ActivityResultContracts.PickVisualMedia
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Image
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults.buttonColors
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.sp
import com.bitgranules.androidproject.viewmodel.QuoteModelView

@Composable
fun ImportButton(
    viewModel: QuoteModelView
) {
    val context = LocalContext.current

    val multiplePhotoPickerLauncher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickMultipleVisualMedia(),
            onResult = { uris: List<Uri> ->
                if (uris.isNotEmpty()) {
                    val flag = Intent.FLAG_GRANT_READ_URI_PERMISSION
                    val uriStringList = mutableListOf<String>()

                    uris.forEach { uri ->
                        context.contentResolver.takePersistableUriPermission(uri, flag)
                        uriStringList.add(uri.toString())
                    }
                    viewModel.addMultipleBgImages(uriStringList)
                }
            })


    Button(
        onClick = {
            multiplePhotoPickerLauncher.launch(
                input = PickVisualMediaRequest(
                    PickVisualMedia.ImageOnly
                )
            )
        },
        colors = buttonColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer,
            contentColor = MaterialTheme.colorScheme.surfaceContainerLowest
        )
    ) {
        Row(horizontalArrangement = Arrangement.SpaceEvenly) {
            Icon(imageVector = Icons.Default.Image, contentDescription = null)
            Text(
                text = "Import From Memory",
                fontSize = 13.sp,

                )
        }
    }
}