package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "quotas")
data class QuotaEntity(
    @PrimaryKey val quotaId: String,
    val userCode: String,
    val dueDate: String,
    val amount: Double,
    val status: String // "PAGADA", "PENDIENTE_SEMANAL", "VENCIDA_MORA"
)
