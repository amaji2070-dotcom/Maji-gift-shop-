package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.model.Product
import kotlinx.coroutines.flow.Flow

@Dao
interface ProductDao {
    @Query("SELECT * FROM products ORDER BY id DESC")
    fun getAllProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE id = :id")
    suspend fun getProductById(id: Long): Product?

    @Query("SELECT * FROM products WHERE categoryId = :catId ORDER BY id DESC")
    fun getProductsByCategory(catId: Long): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE stockQuantity <= lowStockThreshold ORDER BY stockQuantity ASC")
    fun getLowStockProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE isOnSale = 1 ORDER BY id DESC")
    fun getDiscountedProducts(): Flow<List<Product>>

    @Query("SELECT * FROM products WHERE title LIKE '%' || :query || '%' OR sku LIKE '%' || :query || '%'")
    fun searchProducts(query: String): Flow<List<Product>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProduct(product: Product): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(products: List<Product>)

    @Update
    suspend fun updateProduct(product: Product)

    @Delete
    suspend fun deleteProduct(product: Product)

    @Query("UPDATE products SET stockQuantity = stockQuantity + :adjustment, updatedAt = :timestamp WHERE id = :id")
    suspend fun updateStock(id: Long, adjustment: Int, timestamp: Long = System.currentTimeMillis())

    @Query("UPDATE products SET originalPrice = :original, salePrice = :sale, isOnSale = :onSale, updatedAt = :timestamp WHERE id = :id")
    suspend fun updatePriceAndDiscount(id: Long, original: Double, sale: Double, onSale: Boolean, timestamp: Long = System.currentTimeMillis())

    @Query("SELECT COUNT(*) FROM products")
    suspend fun getCount(): Int
}
