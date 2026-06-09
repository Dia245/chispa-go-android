package com.example.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface ChispaDao {
    @Query("SELECT * FROM users")
    fun getAllUsersFlow(): Flow<List<UserEntity>>

    @Query("SELECT * FROM users WHERE code = :code LIMIT 1")
    fun getUserByCodeFlow(code: String): Flow<UserEntity?>

    @Query("SELECT * FROM users WHERE code = :code LIMIT 1")
    suspend fun getUserByCode(code: String): UserEntity?

    @Query("SELECT * FROM quotas WHERE userCode = :userCode")
    fun getQuotasForUserFlow(userCode: String): Flow<List<QuotaEntity>>

    @Query("SELECT * FROM quotas WHERE userCode = :userCode")
    suspend fun getQuotasForUser(userCode: String): List<QuotaEntity>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUsers(users: List<UserEntity>)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertQuotas(quotas: List<QuotaEntity>)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Update
    suspend fun updateQuota(quota: QuotaEntity)

    @Query("SELECT COUNT(*) FROM users")
    suspend fun getUsersCount(): Int
}
