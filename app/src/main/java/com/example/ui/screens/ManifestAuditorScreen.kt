package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.AuditLogEntity
import com.example.ui.theme.*
import com.example.ui.viewmodel.AegisViewModel
import kotlinx.coroutines.launch

@Composable
fun ManifestAuditorScreen(
    viewModel: AegisViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    val sampleMaliciousManifest = """
        <manifest xmlns:android="http://schemas.android.com/apk/res/android"
            package="com.spyware.tracker">
            <uses-permission android:name="android.permission.READ_SMS"/>
            <uses-permission android:name="android.permission.RECEIVE_BOOT_COMPLETED"/>
            <uses-permission android:name="android.permission.SYSTEM_ALERT_WINDOW"/>
            <uses-permission android:name="android.permission.BIND_ACCESSIBILITY_SERVICE"/>
            <application
                android:debuggable="true"
                android:allowBackup="true"
                android:usesCleartextTraffic="true">
                <receiver android:name=".BootReceiver" android:exported="true">
                    <intent-filter>
                        <action android:name="android.intent.action.BOOT_COMPLETED"/>
                    </intent-filter>
                </receiver>
            </application>
        </manifest>
    """.trimIndent()

    val sampleCleanManifest = """
        <manifest xmlns:android="http://schemas.android.com/apk/res/android"
            package="com.example.securenote">
            <uses-permission android:name="android.permission.INTERNET"/>
            <application
                android:debuggable="false"
                android:allowBackup="false"
                android:usesCleartextTraffic="false">
            </application>
        </manifest>
    """.trimIndent()

    var manifestInput by remember { mutableStateOf(sampleMaliciousManifest) }
    var auditReport by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    var saveStatus by remember { mutableStateOf("") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AegisDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "STATIC ARTIFACT AUDITING",
            color = AegisPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Paste an AndroidManifest.xml or APK component configuration below to inspect dangerous permissions, debug flags, and exported receivers.",
            color = AegisTextSecondary,
            fontSize = 14.sp
        )

        Row(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Button(
                onClick = { manifestInput = sampleMaliciousManifest },
                colors = ButtonDefaults.buttonColors(containerColor = AegisSurfaceVariant)
            ) {
                Text("Load Malicious Sample", color = AegisCritical, fontSize = 12.sp)
            }
            Button(
                onClick = { manifestInput = sampleCleanManifest },
                colors = ButtonDefaults.buttonColors(containerColor = AegisSurfaceVariant)
            ) {
                Text("Load Clean Sample", color = AegisSuccess, fontSize = 12.sp)
            }
        }

        OutlinedTextField(
            value = manifestInput,
            onValueChange = { manifestInput = it },
            label = { Text("AndroidManifest.xml Source") },
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = AegisPrimary,
                unfocusedBorderColor = AegisBorder,
                focusedLabelColor = AegisPrimary,
                focusedTextColor = AegisTextPrimary,
                unfocusedTextColor = AegisTextPrimary
            )
        )

        Button(
            onClick = {
                coroutineScope.launch {
                    isLoading = true
                    saveStatus = ""
                    auditReport = viewModel.analyzeWithGemini(
                        prompt = "Audit this Android Manifest for security vulnerabilities, permission abuse, and insecure flags:\n$manifestInput",
                        category = "Manifest Audit"
                    )
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AegisPrimary)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AegisDarkBackground)
            Spacer(modifier = Modifier.width(8.dp))
            Text("RUN STATIC ARTIFACT AUDIT", color = AegisDarkBackground, fontWeight = FontWeight.Bold)
        }

        if (isLoading) {
            Box(modifier = Modifier.fillMaxWidth().padding(24.dp), contentAlignment = androidx.compose.ui.Alignment.Center) {
                CircularProgressIndicator(color = AegisPrimary)
            }
        }

        if (auditReport.isNotBlank()) {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AegisSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "STRUCTURED THREAT REPORT",
                            color = AegisPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.saveAuditLog(
                                        AuditLogEntity(
                                            title = "Manifest Audit: com.spyware",
                                            category = "Manifest Audit",
                                            threatLevel = if (auditReport.contains("MALICIOUS")) "MALICIOUS" else "CLEAN",
                                            cvssScore = "8.8",
                                            reportContent = auditReport
                                        )
                                    )
                                    saveStatus = "Report saved to Room DB!"
                                }
                            }
                        ) {
                            Icon(Icons.Default.Save, contentDescription = "Save Report", tint = AegisSuccess)
                        }
                    }

                    if (saveStatus.isNotBlank()) {
                        Text(text = saveStatus, color = AegisSuccess, fontSize = 12.sp)
                    }

                    Text(
                        text = auditReport,
                        color = AegisTextPrimary,
                        fontFamily = FontFamily.Monospace,
                        fontSize = 13.sp
                    )
                }
            }
        }
    }
}
