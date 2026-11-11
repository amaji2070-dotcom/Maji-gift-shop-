package com.example.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class OrderStatus(val label: String, val colorHex: String) {
    PENDING("Pending", "#F57C00"),
    PROCESSING("Processing", "#1976D2"),
    PACKED("Packed", "#7B1FA2"),
    SHIPPED("Shipped", "#0288D1"),
    OUT_FOR_DELIVERY("Out for Delivery", "#388E3C"),
    DELIVERED("Delivered", "#2E7D32"),
    CANCELLED("Cancelled", "#D32F2F")
}

enum class PaymentStatus(val label: String, val colorHex: String) {
    UNPAID("Unpaid", "#D32F2F"),
    PAID("Paid", "#2E7D32"),
    REFUNDED("Refunded", "#757575")
}

enum class PaymentMethod(val label: String) {
    COD("Cash on Delivery"),
    CREDIT_CARD("Credit/Debit Card"),
    MOBILE_PAY("Mobile Money (M-Pesa / UPI)"),
    BANK_TRANSFER("Direct Bank Transfer")
}

data class OrderItem(
    val productId: Long,
    val productTitle: String,
    val quantity: Int,
    val unitPrice: Double,
    val imageUrl: String = "",
    val giftWrapSelected: Boolean = false,
    val giftMessage: String = ""
) {
    val totalPrice: Double get() = quantity * unitPrice
}

@Entity(tableName = "orders")
data class Order(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val orderNumber: String, // e.g. "MGS-84920"
    val customerId: Long,
    val customerName: String,
    val customerEmail: String,
    val customerPhone: String,
    val shippingAddress: String,
    val items: List<OrderItem>,
    val subtotal: Double,
    val discountAmount: Double = 0.0,
    val giftWrapFee: Double = 0.0,
    val deliveryFee: Double = 0.0,
    val totalAmount: Double,
    val appliedCouponCode: String? = null,
    val orderStatus: OrderStatus = OrderStatus.PENDING,
    val paymentStatus: PaymentStatus = PaymentStatus.UNPAID,
    val paymentMethod: PaymentMethod = PaymentMethod.COD,
    val courierName: String = "Maji Express Couriers",
    val trackingNumber: String? = null,
    val adminNotes: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
