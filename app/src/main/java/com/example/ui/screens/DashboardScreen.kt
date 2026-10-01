package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Assessment
import androidx.compose.material.icons.filled.Chat
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Phishing
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

import androidx.compose.foundation.border
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Share
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import com.example.util.ApkManager

@Composable
fun DashboardScreen(
    onNavigate: (String) -> Unit,
    onOpenApkDownloader: () -> Unit = {}
) {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AegisDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Status Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AegisSurface),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(
                modifier = Modifier.padding(20.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "SYSTEM POSTURE",
                        color = AegisTextSecondary,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Surface(
                        color = AegisSuccess.copy(alpha = 0.2f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "ACTIVE",
                            color = AegisSuccess,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                        )
                    }
                }
                Text(
                    text = "AEGIS Security Shield Online",
                    color = AegisTextPrimary,
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Autonomous mobile threat telemetry, static artifact auditing, and hardening engine operating normally.",
                    color = AegisTextSecondary,
                    fontSize = 14.sp
                )
            }
        }

        // APK Downloader Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = AegisSurfaceVariant),
            modifier = Modifier
                .fillMaxWidth()
                .border(1.dp, AegisPrimary.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                .testTag("preview_apk_downloader_card")
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = AegisPrimary,
                            modifier = Modifier.size(24.dp)
                        )
                        Text(
                            text = "APK DOWNLOAD & EXPORT",
                            color = AegisPrimary,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }

                    Surface(
                        color = AegisPrimary.copy(alpha = 0.15f),
                        shape = RoundedCornerShape(4.dp)
                    ) {
                        Text(
                            text = "PREVIEW READY",
                            color = AegisPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }

                Text(
                    text = "Download the compiled AEGIS Android package (.apk) to device storage, or share the installer directly.",
                    color = AegisTextPrimary,
                    fontSize = 13.sp
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = onOpenApkDownloader,
                        colors = ButtonDefaults.buttonColors(containerColor = AegisPrimary),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("download_apk_hero_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Download,
                            contentDescription = null,
                            tint = AegisDarkBackground,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Download APK",
                            color = AegisDarkBackground,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    }

                    OutlinedButton(
                        onClick = {
                            ApkManager.shareApk(context)
                        },
                        border = ButtonDefaults.outlinedButtonBorder.copy(
                            brush = androidx.compose.ui.graphics.SolidColor(AegisPrimary)
                        ),
                        modifier = Modifier
                            .weight(1f)
                            .testTag("share_apk_hero_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = null,
                            tint = AegisPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Export / Share",
                            color = AegisPrimary,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Quick Modules Grid
        Text(
            text = "SECURITY MODULES",
            color = AegisPrimary,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModuleCard(
                title = "Manifest Auditor",
                description = "Inspect APK & Manifest for permission abuse",
                icon = Icons.Default.Code,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("manifest") }
            )
            ModuleCard(
                title = "Telemetry Inspector",
                description = "Monitor Logcat, Root & Frida hooks",
                icon = Icons.Default.Speed,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("telemetry") }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModuleCard(
                title = "Smishing Analyzer",
                description = "Dissect SMS lures & phishing payloads",
                icon = Icons.Default.Phishing,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("smishing") }
            )
            ModuleCard(
                title = "Hardening Playbook",
                description = "OWASP MASVS & ADB commands",
                icon = Icons.Default.Assessment,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("hardening") }
            )
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            ModuleCard(
                title = "AI Copilot",
                description = "Gemini Security Analyst Assistant",
                icon = Icons.Default.Chat,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("copilot") }
            )
            ModuleCard(
                title = "Audit History",
                description = "Review saved threat intelligence reports",
                icon = Icons.Default.Security,
                modifier = Modifier.weight(1f),
                onClick = { onNavigate("history") }
            )
        }
    }
}

@Composable
fun ModuleCard(
    title: String,
    description: String,
    icon: ImageVector,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = AegisSurface),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = AegisPrimary,
                modifier = Modifier.size(28.dp)
            )
            Text(
                text = title,
                color = AegisTextPrimary,
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )
            Text(
                text = description,
                color = AegisTextSecondary,
                fontSize = 12.sp,
                maxLines = 2
            )
        }
    }
}
