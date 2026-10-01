package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "audit_logs")
data class AuditLogEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val title: String,
    val category: String, // Manifest Audit, Telemetry, Smishing Scan, AI Copilot
    val threatLevel: String, // CLEAN, LOW, SUSPICIOUS, MALICIOUS, CRITICAL
    val cvssScore: String,
    val reportContent: String,
    val timestamp: Long = System.currentTimeMillis()
)
