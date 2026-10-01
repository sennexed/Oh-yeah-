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
fun SmishingAnalyzerScreen(
    viewModel: AegisViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    val scrollState = rememberScrollState()

    var lureInput by remember {
        mutableStateOf(
            "URGENT: Your bank account access has been suspended due to unauthorized login attempts. Verify your identity immediately at https://secure-bank-login-auth.cc/verify to restore access."
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
            text = "MOBILE THREAT INTELLIGENCE & SMISHING",
            color = AegisPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "Dissect incoming SMS lures, WhatsApp/Telegram redirection chains, and credential harvesting URLs.",
            color = AegisTextSecondary,
            fontSize = 14.sp
        )

        OutlinedTextField(
            value = lureInput,
            onValueChange = { lureInput = it },
            label = { Text("Suspicious SMS / Lure / Phishing URL") },
            modifier = Modifier
                .fillMaxWidth()
                .height(150.dp),
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
                        prompt = "Analyze this suspicious message/URL for smishing, banking trojan lures, and credential harvesting:\n$lureInput",
                        category = "Smishing Scan"
                    )
                    isLoading = false
                }
            },
            modifier = Modifier.fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(containerColor = AegisPrimary)
        ) {
            Icon(Icons.Default.PlayArrow, contentDescription = null, tint = AegisDarkBackground)
            Spacer(modifier = Modifier.width(8.dp))
            Text("ANALYZE THREAT VECTOR", color = AegisDarkBackground, fontWeight = FontWeight.Bold)
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
                            text = "THREAT INTELLIGENCE REPORT",
                            color = AegisPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        IconButton(
                            onClick = {
                                coroutineScope.launch {
                                    viewModel.saveAuditLog(
                                        AuditLogEntity(
                                            title = "Smishing Scan: Bank Lure",
                                            category = "Smishing Scan",
                                            threatLevel = "MALICIOUS",
                                            cvssScore = "8.2",
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
