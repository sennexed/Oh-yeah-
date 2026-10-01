package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.*

@Composable
fun HardeningScreen() {
    val context = LocalContext.current
    val scrollState = rememberScrollState()

    val hardeningItems = listOf(
        HardeningItem(
            title = "1. Revoke Dangerous SMS Permissions",
            description = "Prevent unauthorized background message reading and OTP interception.",
            command = "adb shell pm revoke <package_name> android.permission.READ_SMS"
        ),
        HardeningItem(
            title = "2. Disable Unused Exported Receivers",
            description = "Block unauthorized intent broadcast invocation.",
            command = "adb shell pm disable-user --user 0 <package_name>/<receiver_component>"
        ),
        HardeningItem(
            title = "3. Force Cleartext Traffic Block",
            description = "Enforce encrypted TLS communication across all network layers.",
            command = "adb shell dumpsys package <package_name> | grep usesCleartextTraffic"
        ),
        HardeningItem(
            title = "4. Inspect App Ops Overdraw Privileges",
            description = "Verify SYSTEM_ALERT_WINDOW overlay permissions to prevent tap-jacking.",
            command = "adb shell appops set <package_name> SYSTEM_ALERT_WINDOW ignore"
        )
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(AegisDarkBackground)
            .verticalScroll(scrollState)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "DEFENSIVE REMEDIATION & HARDENING",
            color = AegisPrimary,
            fontSize = 18.sp,
            fontWeight = FontWeight.Bold
        )
        Text(
            text = "OWASP MASVS & NIST mobile security hardening profiles and concrete ADB command-line playbooks.",
            color = AegisTextSecondary,
            fontSize = 14.sp
        )

        hardeningItems.forEach { item ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = AegisSurface),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = item.title,
                        color = AegisTextPrimary,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = item.description,
                        color = AegisTextSecondary,
                        fontSize = 13.sp
                    )
                    Surface(
                        color = AegisSurfaceVariant,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = androidx.compose.ui.Alignment.CenterVertically
                        ) {
                            Text(
                                text = item.command,
                                color = AegisPrimary,
                                fontFamily = FontFamily.Monospace,
                                fontSize = 12.sp,
                                modifier = Modifier.weight(1f)
                            )
                            IconButton(
                                onClick = {
                                    val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                                    val clip = ClipData.newPlainText("ADB Command", item.command)
                                    clipboard.setPrimaryClip(clip)
                                    Toast.makeText(context, "Command copied to clipboard", Toast.LENGTH_SHORT).show()
                                }
                            ) {
                                Icon(Icons.Default.ContentCopy, contentDescription = "Copy", tint = AegisPrimary)
                            }
                        }
                    }
                }
            }
        }
    }
}

data class HardeningItem(
    val title: String,
    val description: String,
    val command: String
)
