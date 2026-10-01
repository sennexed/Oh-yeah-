package com.example.ui.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AegisDarkBackground
import com.example.ui.theme.AegisPrimary
import com.example.ui.theme.AegisSurface
import com.example.ui.theme.AegisTextPrimary

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Dashboard : Screen("dashboard", "Dashboard", Icons.Default.Security)
    object ManifestAuditor : Screen("manifest", "Auditor", Icons.Default.Code)
    object Telemetry : Screen("telemetry", "Telemetry", Icons.Default.Speed)
    object Smishing : Screen("smishing", "Threats", Icons.Default.Phishing)
    object Hardening : Screen("hardening", "Hardening", Icons.Default.Assessment)
    object Copilot : Screen("copilot", "AI Copilot", Icons.Default.Chat)
    object History : Screen("history", "History", Icons.Default.History)
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AegisScaffold(
    currentRoute: String,
    onNavigate: (String) -> Unit,
    content: @Composable (Modifier, () -> Unit) -> Unit
) {
    var showApkDialog by remember { mutableStateOf(false) }

    val items = listOf(
        Screen.Dashboard,
        Screen.ManifestAuditor,
        Screen.Telemetry,
        Screen.Smishing,
        Screen.Hardening,
        Screen.Copilot,
        Screen.History
    )

    if (showApkDialog) {
        ApkDownloaderDialog(onDismissRequest = { showApkDialog = false })
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "AEGIS-ANDROID",
                        color = AegisPrimary,
                        fontWeight = FontWeight.Bold,
                        fontSize = 17.sp
                    )
                },
                actions = {
                    FilledTonalButton(
                        onClick = { showApkDialog = true },
                        colors = ButtonDefaults.filledTonalButtonColors(
                            containerColor = AegisPrimary.copy(alpha = 0.25f),
                            contentColor = AegisPrimary
                        ),
                        shape = RoundedCornerShape(8.dp),
                        contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                        modifier = Modifier
                            .padding(end = 8.dp)
                            .testTag("apk_downloader_preview_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = "Download APK",
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "Download APK",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = AegisSurface,
                    titleContentColor = AegisTextPrimary
                )
            )
        },
        bottomBar = {
            NavigationBar(
                containerColor = AegisSurface,
                contentColor = AegisPrimary
            ) {
                items.forEach { screen ->
                    NavigationBarItem(
                        icon = { Icon(screen.icon, contentDescription = screen.title) },
                        label = { Text(screen.title, maxLines = 1) },
                        selected = currentRoute == screen.route,
                        onClick = { onNavigate(screen.route) }
                    )
                }
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            content(Modifier.fillMaxSize()) {
                showApkDialog = true
            }
        }
    }
}
