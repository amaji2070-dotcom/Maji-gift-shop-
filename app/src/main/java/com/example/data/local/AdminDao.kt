package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.AdminRole
import com.example.data.model.AdminUser
import kotlinx.coroutines.flow.Flow

@Dao
interface AdminDao {
    @Query("SELECT * FROM admin_users ORDER BY id ASC")
    fun getAllAdmins(): Flow<List<AdminUser>>

    @Query("SELECT * FROM admin_users WHERE username = :username LIMIT 1")
    suspend fun getByUsername(username: String): AdminUser?

    @Query("SELECT * FROM admin_users WHERE id = :id")
    suspend fun getById(id: Long): AdminUser?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAdmin(admin: AdminUser): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(admins: List<AdminUser>)

    @Update
    suspend fun updateAdmin(admin: AdminUser)

    @Delete
    suspend fun deleteAdmin(admin: AdminUser)

    @Query("UPDATE admin_users SET role = :newRole WHERE id = :id")
    suspend fun updateRole(id: Long, newRole: AdminRole)

    @Query("UPDATE admin_users SET pinCode = :newPin WHERE id = :id")
    suspend fun updatePin(id: Long, newPin: String)

    @Query("UPDATE admin_users SET lastLoginAt = :timestamp WHERE id = :id")
    suspend fun updateLastLogin(id: Long, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM admin_users")
    suspend fun getCount(): Int
}
