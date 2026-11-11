package com.example.ui.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.PointOfSale
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.ui.graphics.vector.ImageVector
import com.example.data.model.AdminPermission

enum class AdminScreen(
    val title: String,
    val icon: ImageVector,
    val requiredPermission: AdminPermission? = null
) {
    DASHBOARD("Dashboard", Icons.Default.Dashboard),
    PRODUCTS("Products", Icons.Default.CardGiftcard, AdminPermission.MANAGE_PRODUCTS),
    ORDERS("Orders & COD", Icons.Default.ShoppingBag, AdminPermission.MANAGE_ORDERS),
    INVENTORY("Inventory & Stock", Icons.Default.Inventory2, AdminPermission.MANAGE_INVENTORY),
    CATEGORIES("Categories", Icons.Default.Category, AdminPermission.MANAGE_CATEGORIES),
    COUPONS("Coupon Codes", Icons.Default.Discount, AdminPermission.MANAGE_MARKETING),
    BANNERS("Promo Banners", Icons.Default.ViewCarousel, AdminPermission.MANAGE_MARKETING),
    CUSTOMERS("Customers", Icons.Default.People, AdminPermission.MANAGE_CUSTOMERS),
    REPORTS("Sales Reports", Icons.Default.QueryStats, AdminPermission.VIEW_FINANCIAL_REPORTS),
    NOTIFICATIONS("Push Broadcast", Icons.Default.Campaign, AdminPermission.SEND_PUSH_NOTIFICATIONS),
    ROLES("Admin Roles", Icons.Default.Security, AdminPermission.MANAGE_ROLES)
}
