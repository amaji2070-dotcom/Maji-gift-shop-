package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class CustomerTier(val label: String, val badgeColorHex: String) {
    STANDARD("Standard", "#757575"),
    SILVER("Silver", "#0288D1"),
    GOLD("Gold", "#FBC02D"),
    PLATINUM("VIP Platinum", "#7B1FA2")
}

@Entity(tableName = "customers")
data class Customer(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val phone: String,
    val address: String,
    val totalOrders: Int = 0,
    val totalSpend: Double = 0.0,
    val tier: CustomerTier = CustomerTier.STANDARD,
    val isSuspended: Boolean = false,
    val adminNotes: String = "",
    val registeredAt: Long = System.currentTimeMillis(),
    val lastOrderAt: Long? = null
)
