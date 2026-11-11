package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class AdminPermission(val title: String) {
    MANAGE_PRODUCTS("Create, Edit & Delete Products"),
    MANAGE_CATEGORIES("Manage Product Categories"),
    MANAGE_INVENTORY("Stock In / Out & Restock Adjustments"),
    MANAGE_ORDERS("View & Update Order Delivery Status"),
    MANAGE_PAYMENTS("Verify Online & COD Payments"),
    MANAGE_MARKETING("Create Coupons & Promo Banners"),
    MANAGE_CUSTOMERS("View Customers & Suspend Accounts"),
    VIEW_FINANCIAL_REPORTS("View Sales & Financial Analytics"),
    SEND_PUSH_NOTIFICATIONS("Broadcast Push Notifications"),
    MANAGE_ROLES("Admin Role & Security Configuration")
}

enum class AdminRole(val roleName: String, val permissions: Set<AdminPermission>) {
    SUPER_ADMIN(
        "Super Administrator",
        AdminPermission.values().toSet()
    ),
    INVENTORY_MANAGER(
        "Inventory & Catalog Manager",
        setOf(
            AdminPermission.MANAGE_PRODUCTS,
            AdminPermission.MANAGE_CATEGORIES,
            AdminPermission.MANAGE_INVENTORY
        )
    ),
    ORDER_DISPATCHER(
        "Order & Logistics Dispatcher",
        setOf(
            AdminPermission.MANAGE_ORDERS,
            AdminPermission.MANAGE_PAYMENTS
        )
    ),
    MARKETING_MANAGER(
        "Marketing & Growth Specialist",
        setOf(
            AdminPermission.MANAGE_MARKETING,
            AdminPermission.SEND_PUSH_NOTIFICATIONS,
            AdminPermission.VIEW_FINANCIAL_REPORTS
        )
    )
}

@Entity(tableName = "admin_users")
data class AdminUser(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val username: String,
    val fullName: String,
    val email: String,
    val role: AdminRole = AdminRole.SUPER_ADMIN,
    val pinCode: String = "1234",
    val isActive: Boolean = true,
    val lastLoginAt: Long = System.currentTimeMillis()
) {
    fun hasPermission(permission: AdminPermission): Boolean {
        return role.permissions.contains(permission)
    }
}
