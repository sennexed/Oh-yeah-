package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.runtime.*
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.ui.components.AegisScaffold
import com.example.ui.screens.*
import com.example.ui.theme.AegisAndroidTheme
import com.example.ui.viewmodel.AegisViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: AegisViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            AegisAndroidTheme {
                val navController = rememberNavController()
                var currentRoute by remember { mutableStateOf("dashboard") }

                AegisScaffold(
                    currentRoute = currentRoute,
                    onNavigate = { route ->
                        currentRoute = route
                        navController.navigate(route) {
                            popUpTo(navController.graph.startDestinationId) { saveState = true }
                            launchSingleTop = true
                            restoreState = true
                        }
                    }
                ) { modifier, openApkDownloader ->
                    NavHost(
                        navController = navController,
                        startDestination = "dashboard",
                        modifier = modifier
                    ) {
                        composable("dashboard") {
                            DashboardScreen(
                                onNavigate = { route ->
                                    currentRoute = route
                                    navController.navigate(route) {
                                        popUpTo(navController.graph.startDestinationId) { saveState = true }
                                        launchSingleTop = true
                                        restoreState = true
                                    }
                                },
                                onOpenApkDownloader = openApkDownloader
                            )
                        }
                        composable("manifest") {
                            ManifestAuditorScreen(viewModel = viewModel)
                        }
                        composable("telemetry") {
                            TelemetryScreen(viewModel = viewModel)
                        }
                        composable("smishing") {
                            SmishingAnalyzerScreen(viewModel = viewModel)
                        }
                        composable("hardening") {
                            HardeningScreen()
                        }
                        composable("copilot") {
                            AiCopilotScreen(viewModel = viewModel)
                        }
                        composable("history") {
                            AuditHistoryScreen(viewModel = viewModel)
                        }
                    }
                }
            }
        }
    }
}
