package com.bitgranules.androidproject.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@Composable
fun BottomNavBar(navController: NavController) {

    val items = listOf("manager", "feed", "settings")
    val currentRoute = navController.currentBackStackEntryAsState().value?.destination?.route

    NavigationBar {
        items.forEach { route ->
            NavigationBarItem(
                selected = currentRoute == route,
                onClick = {
                    navController.navigate(route) {
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                        launchSingleTop = true
                        restoreState = true
                    }
                },
                label = { Text(route.replaceFirstChar { it.uppercase() }) },
                icon = {
                    Icon(
                        imageVector = when (route) {
                            "feed" -> {
                                Icons.Default.Home
                            }

                            "settings" -> {
                                Icons.Default.Settings
                            }

                            "manager" -> {
                                Icons.AutoMirrored.Filled.List
                            }

                            else -> {
                                Icons.Default.Home
                            }
                        },
                        contentDescription = route,
                        modifier = Modifier,
                        tint = MaterialTheme.colorScheme.tertiary
                    )

                }
            )
        }
    }
}
