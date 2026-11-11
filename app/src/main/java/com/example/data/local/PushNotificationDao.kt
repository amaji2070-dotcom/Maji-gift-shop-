package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.data.model.PushNotificationLog
import kotlinx.coroutines.flow.Flow

@Dao
interface PushNotificationDao {
    @Query("SELECT * FROM push_notifications ORDER BY sentAt DESC")
    fun getAllLogs(): Flow<List<PushNotificationLog>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertLog(log: PushNotificationLog): Long

    @Delete
    suspend fun deleteLog(log: PushNotificationLog)

    @Query("SELECT COUNT(*) FROM push_notifications")
    suspend fun getCount(): Int
}
