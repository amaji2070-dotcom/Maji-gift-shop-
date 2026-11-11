package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.PromoBanner
import kotlinx.coroutines.flow.Flow

@Dao
interface PromoBannerDao {
    @Query("SELECT * FROM promo_banners ORDER BY displayPriority ASC, id DESC")
    fun getAllBanners(): Flow<List<PromoBanner>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBanner(banner: PromoBanner): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(banners: List<PromoBanner>)

    @Update
    suspend fun updateBanner(banner: PromoBanner)

    @Delete
    suspend fun deleteBanner(banner: PromoBanner)

    @Query("UPDATE promo_banners SET isActive = :active WHERE id = :id")
    suspend fun toggleActive(id: Long, active: Boolean)

    @Query("SELECT COUNT(*) FROM promo_banners")
    suspend fun getCount(): Int
}
