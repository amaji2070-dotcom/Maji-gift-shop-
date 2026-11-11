package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "products")
data class Product(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val sku: String,
    val description: String,
    val categoryId: Long,
    val categoryName: String,
    val originalPrice: Double,
    val salePrice: Double, // discounted price if on sale
    val isOnSale: Boolean = false,
    val stockQuantity: Int,
    val lowStockThreshold: Int = 5,
    val imageUrls: List<String> = emptyList(), // primary + secondary photos
    val isGiftWrappable: Boolean = true,
    val tags: List<String> = emptyList(),
    val isFeatured: Boolean = false,
    val isActive: Boolean = true,
    val rating: Double = 4.8,
    val reviewsCount: Int = 12,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val discountPercent: Int
        get() = if (originalPrice > salePrice && originalPrice > 0) {
            (((originalPrice - salePrice) / originalPrice) * 100).toInt()
        } else 0

    val isLowStock: Boolean
        get() = stockQuantity in 1..lowStockThreshold

    val isOutOfStock: Boolean
        get() = stockQuantity <= 0
}
