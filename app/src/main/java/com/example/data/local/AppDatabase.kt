package com.example.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.data.model.AdminUser
import com.example.data.model.Category
import com.example.data.model.Coupon
import com.example.data.model.Customer
import com.example.data.model.Order
import com.example.data.model.Product
import com.example.data.model.PromoBanner
import com.example.data.model.PushNotificationLog

@Database(
    entities = [
        Product::class,
        Category::class,
        Order::class,
        Coupon::class,
        PromoBanner::class,
        Customer::class,
        AdminUser::class,
        PushNotificationLog::class
    ],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class AppDatabase : RoomDatabase() {

    abstract fun productDao(): ProductDao
    abstract fun categoryDao(): CategoryDao
    abstract fun orderDao(): OrderDao
    abstract fun couponDao(): CouponDao
    abstract fun promoBannerDao(): PromoBannerDao
    abstract fun customerDao(): CustomerDao
    abstract fun adminDao(): AdminDao
    abstract fun pushNotificationDao(): PushNotificationDao

    companion object {
        @Volatile
        private var INSTANCE: AppDatabase? = null

        fun getDatabase(context: Context): AppDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "maji_gift_admin.db"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
