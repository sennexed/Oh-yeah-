package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Send
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

data class ChatMessage(val sender: String, val text: String)

@Composable
fun AiCopilotScreen(
    viewModel: AegisViewModel
) {
    val coroutineScope = rememberCoroutineScope()
    var inputMessage by remember { mutableStateOf("") }
    var messages by remember {
        mutableStateOf(
            listOf(
                ChatMessage(
                    "AEGIS",
                    "AEGIS Security Copilot online. Ask any security question or paste a code snippet, manifest, or telemetry dump for autonomous threat assessment."
                )
            )
        )
    }
    var isLoading by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AegisDarkBackground)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "AEGIS AI SECURITY COPILOT",
            color = AegisPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )

        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            items(messages) { msg ->
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(
                        containerColor = if (msg.sender == "AEGIS") AegisSurface else AegisSurfaceVariant
                    ),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(
                        modifier = Modifier.padding(12.dp),
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(
                            text = msg.sender,
                            color = if (msg.sender == "AEGIS") AegisPrimary else AegisSuccess,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = msg.text,
                            color = AegisTextPrimary,
                            fontFamily = FontFamily.Monospace,
                            fontSize = 13.sp
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = inputMessage,
                onValueChange = { inputMessage = it },
                label = { Text("Ask security copilot...") },
                modifier = Modifier.weight(1f),
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
                    if (inputMessage.isNotBlank()) {
                        val query = inputMessage
                        inputMessage = ""
                        messages = messages + ChatMessage("USER", query)
                        coroutineScope.launch {
                            isLoading = true
                            val reply = viewModel.analyzeWithGemini(query, "AI Copilot")
                            messages = messages + ChatMessage("AEGIS", reply)
                            // Save to Room DB automatically
                            viewModel.saveAuditLog(
                                AuditLogEntity(
                                    title = "AI Copilot Query: ${query.take(25)}...",
                                    category = "AI Copilot",
                                    threatLevel = if (reply.contains("MALICIOUS")) "MALICIOUS" else "CLEAN",
                                    cvssScore = "7.5",
                                    reportContent = reply
                                )
                            )
                            isLoading = false
                        }
                    }
                },
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(containerColor = AegisPrimary),
                modifier = Modifier.height(56.dp)
            ) {
                if (isLoading) {
                    CircularProgressIndicator(modifier = Modifier.size(24.dp), color = AegisDarkBackground)
                } else {
                    Icon(Icons.Default.Send, contentDescription = "Send", tint = AegisDarkBackground)
                }
            }
        }
    }
}
