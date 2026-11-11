package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class DiscountType {
    PERCENTAGE,
    FIXED_AMOUNT
}

@Entity(tableName = "coupons")
data class Coupon(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val code: String, // e.g. "MAJIGIFT20"
    val discountType: DiscountType = DiscountType.PERCENTAGE,
    val discountValue: Double, // e.g. 20% or $15
    val minOrderAmount: Double = 0.0,
    val maxDiscountAmount: Double? = null,
    val usageLimit: Int = 100,
    val timesUsed: Int = 0,
    val validUntil: Long = System.currentTimeMillis() + 30L * 24 * 60 * 60 * 1000,
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
) {
    val isExpired: Boolean get() = System.currentTimeMillis() > validUntil
    val isAvailable: Boolean get() = isActive && !isExpired && timesUsed < usageLimit
}
