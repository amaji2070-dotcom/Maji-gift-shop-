package com.example

import com.example.data.model.AdminPermission
import com.example.data.model.AdminRole
import com.example.data.model.AdminUser
import com.example.data.model.Coupon
import com.example.data.model.DiscountType
import com.example.data.model.Product
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class MajiAdminUnitTest {

    @Test
    fun productDiscountCalculation_isCorrect() {
        val product = Product(
            title = "Gold Pendant",
            sku = "SKU-1",
            description = "18k gold",
            categoryId = 1,
            categoryName = "Fine Jewelry",
            originalPrice = 200.0,
            salePrice = 150.0,
            isOnSale = true,
            stockQuantity = 5
        )
        // (200 - 150) / 200 = 25%
        assertEquals(25, product.discountPercent)
        assertTrue(product.isLowStock)
        assertFalse(product.isOutOfStock)
    }

    @Test
    fun couponAvailability_isCorrect() {
        val coupon = Coupon(
            code = "GIFT20",
            discountType = DiscountType.PERCENTAGE,
            discountValue = 20.0,
            usageLimit = 50,
            timesUsed = 10,
            validUntil = System.currentTimeMillis() + 100000L,
            isActive = true
        )
        assertTrue(coupon.isAvailable)
        assertFalse(coupon.isExpired)
    }

    @Test
    fun adminPermissions_enforceRoleHierarchy() {
        val superAdmin = AdminUser(
            username = "admin1",
            fullName = "Admin Lead",
            email = "admin@example.com",
            role = AdminRole.SUPER_ADMIN,
            pinCode = "1234"
        )
        assertTrue(superAdmin.hasPermission(AdminPermission.MANAGE_PRODUCTS))
        assertTrue(superAdmin.hasPermission(AdminPermission.MANAGE_ROLES))
        assertTrue(superAdmin.hasPermission(AdminPermission.VIEW_FINANCIAL_REPORTS))

        val dispatcher = AdminUser(
            username = "dispatch1",
            fullName = "Logistics Lead",
            email = "logistics@example.com",
            role = AdminRole.ORDER_DISPATCHER,
            pinCode = "4455"
        )
        assertTrue(dispatcher.hasPermission(AdminPermission.MANAGE_ORDERS))
        assertTrue(dispatcher.hasPermission(AdminPermission.MANAGE_PAYMENTS))
        assertFalse(dispatcher.hasPermission(AdminPermission.MANAGE_ROLES))
        assertFalse(dispatcher.hasPermission(AdminPermission.MANAGE_PRODUCTS))
    }
}
