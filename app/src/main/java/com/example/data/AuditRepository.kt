package com.example.data

import kotlinx.coroutines.flow.Flow

class AuditRepository(private val dao: AuditLogDao) {
    val allLogs: Flow<List<AuditLogEntity>> = dao.getAllLogs()

    suspend fun insert(log: AuditLogEntity) {
        dao.insertLog(log)
    }

    suspend fun delete(id: Long) {
        dao.deleteLogById(id)
    }

    suspend fun clear() {
        dao.clearAll()
    }
}
