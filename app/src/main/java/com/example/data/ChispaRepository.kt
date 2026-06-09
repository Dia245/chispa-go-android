package com.example.data

import android.content.Context
import android.util.Log
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf

class ChispaRepository(private val context: Context) {
    private val database: AppDatabase by lazy { AppDatabase.getDatabase(context) }
    private val dao: ChispaDao by lazy { database.chispaDao() }

    val allUsers: Flow<List<UserEntity>> = try {
        dao.getAllUsersFlow()
    } catch (e: Exception) {
        Log.e("ChispaRepository", "Error loading all users flow", e)
        flowOf(emptyList())
    }

    fun getUserByCodeFlow(code: String): Flow<UserEntity?> = try {
        dao.getUserByCodeFlow(code)
    } catch (e: Exception) {
        Log.e("ChispaRepository", "Error loading user model $code", e)
        flowOf(null)
    }

    fun getQuotasForUserFlow(userCode: String): Flow<List<QuotaEntity>> = try {
        dao.getQuotasForUserFlow(userCode)
    } catch (e: Exception) {
        Log.e("ChispaRepository", "Error loading quotas flow for $userCode", e)
        flowOf(emptyList())
    }

    suspend fun getQuotasForUser(userCode: String): List<QuotaEntity> = try {
        dao.getQuotasForUser(userCode)
    } catch (e: Exception) {
        Log.e("ChispaRepository", "Error fetching quotas for $userCode", e)
        emptyList()
    }

    suspend fun initializeDataIfEmpty() {
        try {
            val count = dao.getUsersCount()
            if (count == 0) {
                Log.d("ChispaRepository", "Initializing pre-populated fintech mock database accounts...")
                
                // 1. Create 3 Profiles
                val defaultUsers = listOf(
                    UserEntity(code = "GG-777", name = "José Quispe Santos", recordWeeks = 2, hasEmergencyComodin = false),
                    UserEntity(code = "GG-911", name = "Carlos Mendoza", recordWeeks = 4, hasEmergencyComodin = false),
                    UserEntity(code = "GG-312", name = "Alex Alva G.", recordWeeks = 12, hasEmergencyComodin = true)
                )
                dao.insertUsers(defaultUsers)

                // 2. Insert Quotas for user GG-777 (José Quispe Santos - Overdue/Mora)
                val quotasJose = listOf(
                    QuotaEntity("Q-777-1", "GG-777", "Semana 1 - 05 May", 100.0, "PAGADA"),
                    QuotaEntity("Q-777-2", "GG-777", "Semana 2 - 12 May", 100.0, "PAGADA"),
                    QuotaEntity("Q-777-3", "GG-777", "Semana 3 - 19 May", 100.0, "VENCIDA_MORA"),
                    QuotaEntity("Q-777-4", "GG-777", "Semana 4 - Hoy", 100.0, "VENCIDA_MORA")
                )

                // Carlos Mendoza (GG-911) -> Status: Clean ("Al Día")
                val quotasCarlos = listOf(
                    QuotaEntity("Q-911-1", "GG-911", "Semana 1 - 05 May", 100.0, "PAGADA"),
                    QuotaEntity("Q-911-2", "GG-911", "Semana 2 - 12 May", 100.0, "PAGADA"),
                    QuotaEntity("Q-911-3", "GG-911", "Semana 3 - 19 May", 100.0, "PAGADA"),
                    QuotaEntity("Q-911-4", "GG-911", "Semana 4 - Hoy", 100.0, "PENDIENTE_SEMANAL")
                )

                // Alex Alva G. (GG-312) -> Level 4 "Historial Impecable" (12 weeks). All quotas paid clean.
                val quotasAlex = listOf(
                    QuotaEntity("Q-312-1", "GG-312", "Semana 9 - 05 May", 100.0, "PAGADA"),
                    QuotaEntity("Q-312-2", "GG-312", "Semana 10 - 12 May", 100.0, "PAGADA"),
                    QuotaEntity("Q-312-3", "GG-312", "Semana 11 - 19 May", 100.0, "PAGADA"),
                    QuotaEntity("Q-312-4", "GG-312", "Semana 12 - Hoy", 100.0, "PAGADA")
                )

                dao.insertQuotas(quotasJose + quotasCarlos + quotasAlex)
                Log.d("ChispaRepository", "Fintech Database populated successfully!")
            }
        } catch (e: Exception) {
            Log.e("ChispaRepository", "Fatal exception pre-populating database", e)
        }
    }

    suspend fun payActiveQuota(quotaId: String) {
        try {
            val db = AppDatabase.getDatabase(context)
            val daoObj = db.chispaDao()
            
            // Get quota
            // To make Room transaction robust we can update directly
            // Find user quotas and find the specific one
            val allQuotas = daoObj.getAllUsersFlow() // trigger read just in case
            Log.d("ChispaRepository", "Processing payment of quota $quotaId")
        } catch (e: Exception) {
            Log.e("ChispaRepository", "Error in payActiveQuota stub", e)
        }
    }

    suspend fun updateQuota(quota: QuotaEntity) {
        try {
            dao.updateQuota(quota)
        } catch (e: Exception) {
            Log.e("ChispaRepository", "Error updating quota ${quota.quotaId}", e)
        }
    }

    suspend fun updateUser(user: UserEntity) {
        try {
            dao.updateUser(user)
        } catch (e: Exception) {
            Log.e("ChispaRepository", "Error updating user ${user.code}", e)
        }
    }

    suspend fun createNewUserProgressive(user: UserEntity, quotas: List<QuotaEntity>) {
        try {
            dao.insertUsers(listOf(user))
            dao.insertQuotas(quotas)
        } catch (e: Exception) {
            Log.e("ChispaRepository", "Error creating dynamic user", e)
        }
    }
}
