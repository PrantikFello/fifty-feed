package com.bitgranules.androidproject.CommonSystemConfig

import android.util.Log
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.AdSize
import com.google.android.gms.ads.AdView
import com.google.android.gms.ads.LoadAdError

@Composable
fun AdsBannerView() {
    val context = LocalContext.current

    AndroidView(
        modifier = Modifier.fillMaxWidth().heightIn(30.dp, 50.dp),
        factory = { ctx ->
            AdView(ctx).apply {
                adUnitId = "ca-app-pub-3940256099942544/9214589741"

                val displayMetrics = ctx.resources.displayMetrics
                val widthPixels = displayMetrics.widthPixels
                val density = displayMetrics.density
                val adWidth = (widthPixels / density).toInt()

                setAdSize(AdSize.BANNER)

                // --- ADDED THIS DIAGNOSTIC LISTENER ---
                adListener = object : AdListener() {
                    override fun onAdLoaded() {
                        Log.d("AdMobDebug", "✅ Ad loaded completely and is visible!")
                    }

                    override fun onAdFailedToLoad(error: LoadAdError) {
                        // This prints the exact domain code and system error message to Logcat
                        Log.e("AdMobDebug", "❌ Ad failed to load. Error Code: ${error.code} | Message: ${error.message}")
                    }
                }
                // --------------------------------------

                val adRequest = AdRequest.Builder().build()
                loadAd(adRequest)
            }
        },
        update = { _ -> }
    )
}