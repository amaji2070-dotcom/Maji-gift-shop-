package com.example.data.local

import androidx.room.TypeConverter
import com.example.data.model.AdminRole
import com.example.data.model.CustomerTier
import com.example.data.model.DiscountType
import com.example.data.model.NotificationAudience
import com.example.data.model.OrderItem
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import org.json.JSONArray
import org.json.JSONObject

class Converters {

    @TypeConverter
    fun fromStringList(value: List<String>?): String {
        if (value == null) return "[]"
        val array = JSONArray()
        value.forEach { array.put(it) }
        return array.toString()
    }

    @TypeConverter
    fun toStringList(value: String?): List<String> {
        if (value.isNullOrBlank()) return emptyList()
        val list = mutableListOf<String>()
        try {
            val array = JSONArray(value)
            for (i in 0 until array.length()) {
                list.add(array.getString(i))
            }
        } catch (_: Exception) {}
        return list
    }

    @TypeConverter
    fun fromOrderItemList(items: List<OrderItem>?): String {
        if (items == null) return "[]"
        val array = JSONArray()
        for (item in items) {
            val obj = JSONObject()
            obj.put("productId", item.productId)
            obj.put("productTitle", item.productTitle)
            obj.put("quantity", item.quantity)
            obj.put("unitPrice", item.unitPrice)
            obj.put("imageUrl", item.imageUrl)
            obj.put("giftWrapSelected", item.giftWrapSelected)
            obj.put("giftMessage", item.giftMessage)
            array.put(obj)
        }
        return array.toString()
    }

    @TypeConverter
    fun toOrderItemList(value: String?): List<OrderItem> {
        if (value.isNullOrBlank()) return emptyList()
        val list = mutableListOf<OrderItem>()
        try {
            val array = JSONArray(value)
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                list.add(
                    OrderItem(
                        productId = obj.optLong("productId", 0L),
                        productTitle = obj.optString("productTitle", ""),
                        quantity = obj.optInt("quantity", 1),
                        unitPrice = obj.optDouble("unitPrice", 0.0),
                        imageUrl = obj.optString("imageUrl", ""),
                        giftWrapSelected = obj.optBoolean("giftWrapSelected", false),
                        giftMessage = obj.optString("giftMessage", "")
                    )
                )
            }
        } catch (_: Exception) {}
        return list
    }

    @TypeConverter
    fun fromOrderStatus(value: OrderStatus): String = value.name

    @TypeConverter
    fun toOrderStatus(value: String): OrderStatus = try {
        OrderStatus.valueOf(value)
    } catch (_: Exception) {
        OrderStatus.PENDING
    }

    @TypeConverter
    fun fromPaymentStatus(value: PaymentStatus): String = value.name

    @TypeConverter
    fun toPaymentStatus(value: String): PaymentStatus = try {
        PaymentStatus.valueOf(value)
    } catch (_: Exception) {
        PaymentStatus.UNPAID
    }

    @TypeConverter
    fun fromPaymentMethod(value: PaymentMethod): String = value.name

    @TypeConverter
    fun toPaymentMethod(value: String): PaymentMethod = try {
        PaymentMethod.valueOf(value)
    } catch (_: Exception) {
        PaymentMethod.COD
    }

    @TypeConverter
    fun fromDiscountType(value: DiscountType): String = value.name

    @TypeConverter
    fun toDiscountType(value: String): DiscountType = try {
        DiscountType.valueOf(value)
    } catch (_: Exception) {
        DiscountType.PERCENTAGE
    }

    @TypeConverter
    fun fromCustomerTier(value: CustomerTier): String = value.name

    @TypeConverter
    fun toCustomerTier(value: String): CustomerTier = try {
        CustomerTier.valueOf(value)
    } catch (_: Exception) {
        CustomerTier.STANDARD
    }

    @TypeConverter
    fun fromAdminRole(value: AdminRole): String = value.name

    @TypeConverter
    fun toAdminRole(value: String): AdminRole = try {
        AdminRole.valueOf(value)
    } catch (_: Exception) {
        AdminRole.SUPER_ADMIN
    }

    @TypeConverter
    fun fromNotificationAudience(value: NotificationAudience): String = value.name

    @TypeConverter
    fun toNotificationAudience(value: String): NotificationAudience = try {
        NotificationAudience.valueOf(value)
    } catch (_: Exception) {
        NotificationAudience.ALL_CUSTOMERS
    }
}
