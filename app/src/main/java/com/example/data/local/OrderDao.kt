package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface OrderDao {
    @Query("SELECT * FROM orders ORDER BY createdAt DESC")
    fun getAllOrders(): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE id = :id")
    suspend fun getOrderById(id: Long): Order?

    @Query("SELECT * FROM orders WHERE orderStatus = :status ORDER BY createdAt DESC")
    fun getOrdersByStatus(status: OrderStatus): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE paymentStatus = :status ORDER BY createdAt DESC")
    fun getOrdersByPaymentStatus(status: PaymentStatus): Flow<List<Order>>

    @Query("SELECT * FROM orders WHERE orderNumber LIKE '%' || :query || '%' OR customerName LIKE '%' || :query || '%' OR customerPhone LIKE '%' || :query || '%'")
    fun searchOrders(query: String): Flow<List<Order>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOrder(order: Order): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(orders: List<Order>)

    @Update
    suspend fun updateOrder(order: Order)

    @Delete
    suspend fun deleteOrder(order: Order)

    @Query("UPDATE orders SET orderStatus = :newStatus, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateOrderStatus(id: Long, newStatus: OrderStatus, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET paymentStatus = :newStatus, updatedAt = :timestamp WHERE id = :id")
    suspend fun updatePaymentStatus(id: Long, newStatus: PaymentStatus, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE orders SET trackingNumber = :tracking, courierName = :courier, orderStatus = :status, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateShippingInfo(id: Long, tracking: String, courier: String, status: OrderStatus, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT SUM(totalAmount) FROM orders WHERE paymentStatus = 'PAID'")
    fun getTotalRevenue(): Flow<Double?>

    @Query("SELECT COUNT(*) FROM orders")
    suspend fun getCount(): Int
}
