package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Coupon
import kotlinx.coroutines.flow.Flow

@Dao
interface CouponDao {
    @Query("SELECT * FROM coupons ORDER BY id DESC")
    fun getAllCoupons(): Flow<List<Coupon>>

    @Query("SELECT * FROM coupons WHERE code = :code LIMIT 1")
    suspend fun getCouponByCode(code: String): Coupon?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCoupon(coupon: Coupon): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(coupons: List<Coupon>)

    @Update
    suspend fun updateCoupon(coupon: Coupon)

    @Delete
    suspend fun deleteCoupon(coupon: Coupon)

    @Query("UPDATE coupons SET isActive = :active WHERE id = :id")
    suspend fun toggleActive(id: Long, active: Boolean)

    @Query("UPDATE coupons SET timesUsed = timesUsed + 1 WHERE id = :id")
    suspend fun incrementUsage(id: Long)

    @Query("SELECT COUNT(*) FROM coupons")
    suspend fun getCount(): Int
}
