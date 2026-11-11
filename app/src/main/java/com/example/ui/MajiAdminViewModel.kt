package com.example.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.example.data.model.AdminPermission
import com.example.data.model.AdminRole
import com.example.data.model.AdminUser
import com.example.data.model.Category
import com.example.data.model.Coupon
import com.example.data.model.Customer
import com.example.data.model.CustomerTier
import com.example.data.model.NotificationAudience
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentStatus
import com.example.data.model.Product
import com.example.data.model.PromoBanner
import com.example.data.model.PushNotificationLog
import com.example.data.repository.MajiAdminRepository
import com.example.ui.navigation.AdminScreen
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class DashboardMetrics(
    val totalRevenue: Double = 0.0,
    val totalOrdersCount: Int = 0,
    val pendingOrdersCount: Int = 0,
    val lowStockItemsCount: Int = 0,
    val activeCouponsCount: Int = 0,
    val totalCustomersCount: Int = 0,
    val avgOrderValue: Double = 0.0,
    val totalProductsCount: Int = 0
)

class MajiAdminViewModel(
    private val repository: MajiAdminRepository
) : ViewModel() {

    val currentAdmin: StateFlow<AdminUser?> = repository.currentAdmin

    private val _currentScreen = MutableStateFlow(AdminScreen.DASHBOARD)
    val currentScreen = _currentScreen.asStateFlow()

    private val _snackbarMessage = MutableSharedFlow<String>()
    val snackbarMessage = _snackbarMessage.asSharedFlow()

    // Data streams
    val products = repository.allProducts
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val categories = repository.allCategories
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val orders = repository.allOrders
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val coupons = repository.allCoupons
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val banners = repository.allBanners
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val customers = repository.allCustomers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminUsers = repository.allAdmins
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val pushLogs = repository.allPushLogs
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    // Derived Dashboard Metrics
    val dashboardMetrics: StateFlow<DashboardMetrics> = combine(
        products, orders, coupons, customers
    ) { prodList, orderList, couponList, custList ->
        val rev = orderList.filter { it.paymentStatus == PaymentStatus.PAID }.sumOf { it.totalAmount }
        val pending = orderList.count { it.orderStatus == OrderStatus.PENDING || it.orderStatus == OrderStatus.PROCESSING }
        val lowStock = prodList.count { it.isLowStock || it.isOutOfStock }
        val activeCoupons = couponList.count { it.isAvailable }
        val avgOrder = if (orderList.isNotEmpty()) rev / orderList.size else 0.0

        DashboardMetrics(
            totalRevenue = rev,
            totalOrdersCount = orderList.size,
            pendingOrdersCount = pending,
            lowStockItemsCount = lowStock,
            activeCouponsCount = activeCoupons,
            totalCustomersCount = custList.size,
            avgOrderValue = avgOrder,
            totalProductsCount = prodList.size
        )
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), DashboardMetrics())

    init {
        viewModelScope.launch {
            repository.initializeSeedDataIfNeeded()
        }
    }

    fun navigateTo(screen: AdminScreen) {
        val admin = currentAdmin.value
        if (screen.requiredPermission != null && admin != null && !admin.hasPermission(screen.requiredPermission)) {
            viewModelScope.launch {
                _snackbarMessage.emit("Access Denied: Your role (${admin.role.roleName}) lacks permission for ${screen.title}")
            }
            return
        }
        _currentScreen.value = screen
    }

    // Login & Profile switcher
    fun login(pin: String, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            val user = repository.loginWithPin(pin)
            if (user != null) {
                _snackbarMessage.emit("Welcome back, ${user.fullName} (${user.role.roleName})")
                onResult(true)
            } else {
                _snackbarMessage.emit("Invalid PIN code. Try default 1234, 2233, 4455, or 7788.")
                onResult(false)
            }
        }
    }

    fun switchRole(adminUser: AdminUser) {
        viewModelScope.launch {
            repository.switchAdminRole(adminUser)
            _snackbarMessage.emit("Switched active profile to ${adminUser.fullName} (${adminUser.role.roleName})")
            if (currentScreen.value.requiredPermission != null && !adminUser.hasPermission(currentScreen.value.requiredPermission!!)) {
                _currentScreen.value = AdminScreen.DASHBOARD
            }
        }
    }

    fun logout() {
        repository.logout()
        _currentScreen.value = AdminScreen.DASHBOARD
    }

    // Product CRUD
    fun saveProduct(product: Product) {
        viewModelScope.launch {
            repository.saveProduct(product)
            _snackbarMessage.emit("Product '${product.title}' saved successfully")
        }
    }

    fun deleteProduct(product: Product) {
        viewModelScope.launch {
            repository.deleteProduct(product)
            _snackbarMessage.emit("Product '${product.title}' deleted")
        }
    }

    fun adjustStock(productId: Long, delta: Int) {
        viewModelScope.launch {
            repository.adjustStock(productId, delta)
            _snackbarMessage.emit("Stock adjusted by ${if (delta > 0) "+$delta" else "$delta"}")
        }
    }

    fun updatePriceAndDiscount(productId: Long, original: Double, sale: Double, onSale: Boolean) {
        viewModelScope.launch {
            repository.updatePriceDiscount(productId, original, sale, onSale)
            _snackbarMessage.emit("Price and discount updated")
        }
    }

    // Category CRUD
    fun saveCategory(category: Category) {
        viewModelScope.launch {
            repository.saveCategory(category)
            _snackbarMessage.emit("Category '${category.name}' saved")
        }
    }

    fun deleteCategory(category: Category) {
        viewModelScope.launch {
            repository.deleteCategory(category)
            _snackbarMessage.emit("Category '${category.name}' deleted")
        }
    }

    // Order CRUD
    fun updateOrderStatus(orderId: Long, newStatus: OrderStatus) {
        viewModelScope.launch {
            repository.updateOrderStatus(orderId, newStatus)
            _snackbarMessage.emit("Order status updated to ${newStatus.label}")
        }
    }

    fun updatePaymentStatus(orderId: Long, newStatus: PaymentStatus) {
        viewModelScope.launch {
            repository.updatePaymentStatus(orderId, newStatus)
            _snackbarMessage.emit("Payment status updated to ${newStatus.label}")
        }
    }

    fun updateShipping(orderId: Long, tracking: String, courier: String, status: OrderStatus) {
        viewModelScope.launch {
            repository.updateShipping(orderId, tracking, courier, status)
            _snackbarMessage.emit("Shipping information updated: $courier ($tracking)")
        }
    }

    // Coupon CRUD
    fun saveCoupon(coupon: Coupon) {
        viewModelScope.launch {
            repository.saveCoupon(coupon)
            _snackbarMessage.emit("Coupon '${coupon.code}' saved")
        }
    }

    fun deleteCoupon(coupon: Coupon) {
        viewModelScope.launch {
            repository.deleteCoupon(coupon)
            _snackbarMessage.emit("Coupon '${coupon.code}' deleted")
        }
    }

    fun toggleCoupon(id: Long, active: Boolean) {
        viewModelScope.launch {
            repository.toggleCoupon(id, active)
        }
    }

    // Banner CRUD
    fun saveBanner(banner: PromoBanner) {
        viewModelScope.launch {
            repository.saveBanner(banner)
            _snackbarMessage.emit("Promotional banner saved")
        }
    }

    fun deleteBanner(banner: PromoBanner) {
        viewModelScope.launch {
            repository.deleteBanner(banner)
            _snackbarMessage.emit("Banner deleted")
        }
    }

    fun toggleBanner(id: Long, active: Boolean) {
        viewModelScope.launch {
            repository.toggleBanner(id, active)
        }
    }

    // Customer CRUD
    fun toggleCustomerSuspension(customer: Customer) {
        viewModelScope.launch {
            val newStatus = !customer.isSuspended
            repository.toggleCustomerSuspension(customer.id, newStatus)
            _snackbarMessage.emit("Customer ${customer.name} ${if (newStatus) "suspended" else "activated"}")
        }
    }

    fun updateCustomerTier(customerId: Long, newTier: CustomerTier) {
        viewModelScope.launch {
            repository.updateCustomerTier(customerId, newTier)
            _snackbarMessage.emit("Customer tier changed to ${newTier.label}")
        }
    }

    fun updateCustomerNotes(customerId: Long, notes: String) {
        viewModelScope.launch {
            repository.updateCustomerNotes(customerId, notes)
            _snackbarMessage.emit("Customer admin notes updated")
        }
    }

    // Push Notifications
    fun sendPushBroadcast(title: String, message: String, audience: NotificationAudience, deepLink: String) {
        viewModelScope.launch {
            val log = repository.broadcastNotification(title, message, audience, deepLink)
            _snackbarMessage.emit("Broadcast sent to ${log.recipientsCount} customers!")
        }
    }

    // Role management
    fun updateAdminRole(adminId: Long, newRole: AdminRole) {
        viewModelScope.launch {
            repository.updateAdminRole(adminId, newRole)
            _snackbarMessage.emit("Admin user role updated to ${newRole.roleName}")
        }
    }

    fun postMessage(msg: String) {
        viewModelScope.launch {
            _snackbarMessage.emit(msg)
        }
    }
}

class MajiAdminViewModelFactory(
    private val repository: MajiAdminRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MajiAdminViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return MajiAdminViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
