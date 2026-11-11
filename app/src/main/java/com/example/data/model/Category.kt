package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "categories")
data class Category(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String,
    val iconName: String = "CardGiftcard", // Material Icon identifier
    val bannerColorHex: String = "#7B1FA2",
    val itemCount: Int = 0,
    val isActive: Boolean = true,
    val displayOrder: Int = 0
)
