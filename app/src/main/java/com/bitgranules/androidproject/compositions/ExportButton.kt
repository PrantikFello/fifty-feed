package com.bitgranules.androidproject.compositions

//@Composable
//fun ExportImageButton(viewModel: QuoteModelView) {
//    val context = LocalContext.current
//    val scope = rememberCoroutineScope()
//
//    val activeQuote by viewModel.activeQuote.collectAsStateWithLifecycle()
//    val selectedBgUri by viewModel.selectedBg.collectAsStateWithLifecycle()
//
//    Row(modifier = Modifier) {
//        Button(
//            onClick = {
//                scope.launch {
//                    try {
//                        val backgroundBitmap: Bitmap =
//                            if (!selectedBgUri.isNullOrEmpty()) {
//                                val loader = context.imageLoader
//                                val request =
//                                    ImageRequest.Builder(context).data(selectedBgUri)
//                                        .allowHardware(false)
//                                        .build()
//                                val result = withContext(Dispatchers.IO) {
//                                    loader.execute(request)
//                                }
//                                if (result is SuccessResult) {
//                                    (result.drawable as BitmapDrawable).bitmap
//                                } else {
//                                    Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
//                                }
//                            } else {
//                                Bitmap.createBitmap(1080, 1920, Bitmap.Config.ARGB_8888)
//                            }
//                        withContext(Dispatchers.IO) {
//                            val combinedBitmap =
//                                generateFinalImage(
//                                    context, backgroundBitmap, activeQuote,
//                                    fontResId = TODO()
//                                )
//                            val savedUri = saveBitmapToGallery(
//                                context = context,
//                                bitmap = combinedBitmap,
//                                fileName = "Quote_${System.currentTimeMillis()}"
//                            )
//                            withContext(Dispatchers.Main) {
//                                if (savedUri != null) {
//                                    viewModel.showTransientMessage("Wallpaper exported successfully!")
//                                } else {
//                                    viewModel.showTransientMessage("Failed to combile image export.")
//                                }
//                            }
//                        }
//                    } catch (e: Exception) {
//                        e.printStackTrace()
//                        viewModel.showTransientMessage("Export crash: ${e.localizedMessage}")
//                    }
//                }
//            },
//            modifier = Modifier.background(color = Color.Black).border(1.dp, Color.White)
//        ) {
//            Icon(
//                imageVector = Icons.Default.Download,
//                contentDescription = "export image",
//                modifier = Modifier.size(35.dp),
//                tint = MaterialTheme.colorScheme.primaryContainer
//            )
//            Text(text = "Export")
//        }
//    }
//
//}