package com.bitgranules.androidproject.CommonSystemConfig

import androidx.compose.runtime.staticCompositionLocalOf

val LocalSnackbarTrigger = staticCompositionLocalOf<(String) -> Unit> {
    { _ -> }
}

