package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "promo_banners")
data class PromoBanner(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val subtitle: String,
    val imageUrl: String,
    val targetType: String = "CATEGORY", // "CATEGORY", "PRODUCT", "DEALS"
    val targetId: String = "1",
    val buttonText: String = "Shop Now",
    val backgroundColorHex: String = "#311B92",
    val isActive: Boolean = true,
    val displayPriority: Int = 1,
    val createdAt: Long = System.currentTimeMillis()
)
