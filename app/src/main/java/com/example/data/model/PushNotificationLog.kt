package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class NotificationAudience(val label: String) {
    ALL_CUSTOMERS("All Customers (2,450 users)"),
    VIP_PLATINUM("VIP Platinum & Gold Tier (380 users)"),
    INACTIVE_30_DAYS("Inactive Last 30 Days (612 users)"),
    CART_ABANDONED("Pending Cart Checkouts (145 users)")
}

@Entity(tableName = "push_notifications")
data class PushNotificationLog(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val message: String,
    val audience: NotificationAudience = NotificationAudience.ALL_CUSTOMERS,
    val deepLinkUrl: String = "majigift://shop/featured",
    val sentByAdmin: String = "Super Admin",
    val sentAt: Long = System.currentTimeMillis(),
    val recipientsCount: Int = 2450,
    val deliveredCount: Int = 2418,
    val clickedCount: Int = 384
)
