package com.bitgranules.androidproject

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.android.volley.BuildConfig
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.RequestConfiguration
import com.bitgranules.androidproject.CommonSystemConfig.AdsBannerView
import com.bitgranules.androidproject.CommonSystemConfig.LocalSnackbarTrigger
import com.bitgranules.androidproject.data.SettingsRepository
import com.bitgranules.androidproject.navigation.BottomNavBar
import com.bitgranules.androidproject.screens.MainScreen
import com.bitgranules.androidproject.screens.ManagerScreen
import com.bitgranules.androidproject.screens.SettingsScreen
import com.bitgranules.androidproject.ui.theme.AndroidProjectTheme
import com.bitgranules.androidproject.viewmodel.QuoteModelView
import com.bitgranules.androidproject.viewmodel.QuoteModelViewFactory

class MainActivity : ComponentActivity() {
    val repository: SettingsRepository by lazy { SettingsRepository(application) }
    private val viewModel: QuoteModelView by viewModels { QuoteModelViewFactory(repository) }

    override fun onCreate(savedInstanceState: Bundle?) {
        val splashScreen = installSplashScreen()
        super.onCreate(savedInstanceState)

        // 1. Initialize Mobile Ads SDK immediately on startup
//        MobileAds.initialize(this) { initializationStatus ->
//            // SDK is ready. Safe to execute network calls.
//        }

        // 2. Safely configure local test devices programmatically
        if (BuildConfig.DEBUG) {
            // Replace the string below with the MD5 ID found in your Logcat output
            val testDeviceIds =
                listOf("04C78541426D9D98A07E05AFA1EF1BED") // cite: val testDeviceIds = listOf("TEST_DEVICE_ID")
            val configuration = RequestConfiguration.Builder()
                .setTestDeviceIds(testDeviceIds) // cite: val configuration = RequestConfiguration.Builder().setTestDeviceIds(testDeviceIds).build()
                .build()
            MobileAds.setRequestConfiguration(configuration) // cite: MobileAds.setRequestConfiguration(configuration)
        }

        enableEdgeToEdge()
        hideSystemBars(this)

        setContent {
            val isDarkThemeActive by viewModel.isDarkMode.collectAsState()
            val cachedQuotes = viewModel.cachedQuoteList.collectAsState().value.isEmpty()

            LaunchedEffect(cachedQuotes) {
                if (cachedQuotes) {
                    viewModel.fetchFreshQuoteBatch()
                }
            }
            AndroidProjectTheme(darkTheme = isDarkThemeActive) {
                MainApp(viewModel = viewModel)
            }
        }
    }


    private fun hideSystemBars(activity: ComponentActivity) {
        val windowInsetsController =
            WindowCompat.getInsetsController(activity.window, activity.window.decorView)
        windowInsetsController.systemBarsBehavior =
            WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
    }
}

@Composable
fun MainApp(viewModel: QuoteModelView) {
    val navController = rememberNavController()
    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(viewModel.snackbarEvents) {
        viewModel.snackbarEvents.collect { mssg -> snackbarHostState.showSnackbar(mssg) }
    }

    CompositionLocalProvider(
        LocalSnackbarTrigger provides { mssg: String ->
            viewModel.showTransientMessage(mssg)
        }) {
        Scaffold(
            snackbarHost = { SnackbarHost(hostState = snackbarHostState) },
            modifier = Modifier,
            topBar = { AdsBannerView() },
            bottomBar = {
                Column {

                    BottomNavBar(navController)
                }
            }) { innerPadding ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
//                AdsBannerView()
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    NavHost(
                        navController = navController, startDestination = "feed"
                    ) {
                        composable("feed") { MainScreen(viewModel) }
                        composable("settings") { SettingsScreen(viewModel) }
                        composable("manager") { ManagerScreen(viewModel) }
                    }
                }
            }
        }
    }
}