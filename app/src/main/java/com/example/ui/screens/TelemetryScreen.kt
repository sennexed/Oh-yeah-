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
fun TelemetryScreen(
    viewModel: AegisViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var telemetryLog by remember {
        mutableStateOf(
            """
            [LOGCAT_DUMP] 2026-09-27 02:15:10.412 [PID: 1042] AegisTelemetryEngine: Scanning runtime environment...
            [ROOT_CHECK] Found binary: /system/xbin/su (Perms: rwsr-xr-x) -> ROOT ESCALATION DETECTED
            [HOOK_DETECTION] Port 27042 active: /data/local/tmp/re.frida.server -> FRIDA INJECTION HOOK DETECTED
            [OVERLAY_ABUSE] Package 'com.unauth.overlay' requested SYSTEM_ALERT_WINDOW overdraw permission.
            [NETSTAT] ESTABLISHED tcp4 0 0 192.168.1.15:45210 185.220.101.5:443 (Plain-text C2 Beaconing)
            """.trimIndent()
        )
    }
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
            text = "RUNTIME TELEMETRY & BEHAVIOR ANALYSIS",
            color = AegisPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Inspect Logcat dumps, root binary checks, Frida/Xposed hooks, and Netstat network telemetry.",
            color = AegisTextSecondary,
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = telemetryLog,
            onValueChange = { telemetryLog = it },
            label = { Text("Logcat / Telemetry Stream") },
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
                        prompt = "Analyze this Android runtime telemetry and logcat dump for root escalation, hooking frameworks, and network exfiltration:\n$telemetryLog",
                        category = "Telemetry Analysis"
                    )
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AegisPrimary)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AegisDarkBackground)
            Spacer(modifier = Modifier.width(8.dp))
            Text("ANALYZE TELEMETRY STREAM", color = AegisDarkBackground, fontWeight = FontWeight.Bold)
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
                            text = "TELEMETRY THREAT REPORT",
                            color = AegisPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.saveAuditLog(
                                        AuditLogEntity(
                                            title = "Telemetry Analysis: Root/Frida",
                                            category = "Telemetry Analysis",
                                            threatLevel = "CRITICAL",
                                            cvssScore = "9.1",
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
