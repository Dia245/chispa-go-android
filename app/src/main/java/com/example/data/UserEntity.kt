package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey val code: String,
    val name: String,
    val recordWeeks: Int,
    val hasEmergencyComodin: Boolean = false
)
