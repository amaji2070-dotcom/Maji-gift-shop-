package com.example.data.repository

import com.example.data.local.AppDatabase
import com.example.data.model.AdminPermission
import com.example.data.model.AdminRole
import com.example.data.model.AdminUser
import com.example.data.model.Category
import com.example.data.model.Coupon
import com.example.data.model.Customer
import com.example.data.model.CustomerTier
import com.example.data.model.DiscountType
import com.example.data.model.NotificationAudience
import com.example.data.model.Order
import com.example.data.model.OrderItem
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.model.Product
import com.example.data.model.PromoBanner
import com.example.data.model.PushNotificationLog
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first

class MajiAdminRepository(private val db: AppDatabase) {

    private val productDao = db.productDao()
    private val categoryDao = db.categoryDao()
    private val orderDao = db.orderDao()
    private val couponDao = db.couponDao()
    private val promoBannerDao = db.promoBannerDao()
    private val customerDao = db.customerDao()
    private val adminDao = db.adminDao()
    private val pushNotificationDao = db.pushNotificationDao()

    // Current logged-in admin session
    private val _currentAdmin = MutableStateFlow<AdminUser?>(null)
    val currentAdmin = _currentAdmin.asStateFlow()

    // Streams
    val allProducts: Flow<List<Product>> = productDao.getAllProducts()
    val lowStockProducts: Flow<List<Product>> = productDao.getLowStockProducts()
    val discountedProducts: Flow<List<Product>> = productDao.getDiscountedProducts()
    val allCategories: Flow<List<Category>> = categoryDao.getAllCategories()
    val allOrders: Flow<List<Order>> = orderDao.getAllOrders()
    val allCoupons: Flow<List<Coupon>> = couponDao.getAllCoupons()
    val allBanners: Flow<List<PromoBanner>> = promoBannerDao.getAllBanners()
    val allCustomers: Flow<List<Customer>> = customerDao.getAllCustomers()
    val allAdmins: Flow<List<AdminUser>> = adminDao.getAllAdmins()
    val allPushLogs: Flow<List<PushNotificationLog>> = pushNotificationDao.getAllLogs()

    suspend fun initializeSeedDataIfNeeded() {
        if (adminDao.getCount() == 0) {
            val defaultAdmins = listOf(
                AdminUser(
                    username = "superadmin",
                    fullName = "Amara Maji (Store Founder)",
                    email = "amaji2070@gmail.com",
                    role = AdminRole.SUPER_ADMIN,
                    pinCode = "1234"
                ),
                AdminUser(
                    username = "inventory",
                    fullName = "David Chen (Stock Lead)",
                    email = "david@majigift.com",
                    role = AdminRole.INVENTORY_MANAGER,
                    pinCode = "2233"
                ),
                AdminUser(
                    username = "logistics",
                    fullName = "Elena Gomez (Dispatch & COD)",
                    email = "elena@majigift.com",
                    role = AdminRole.ORDER_DISPATCHER,
                    pinCode = "4455"
                ),
                AdminUser(
                    username = "marketing",
                    fullName = "Kofi Mensah (Growth Lead)",
                    email = "kofi@majigift.com",
                    role = AdminRole.MARKETING_MANAGER,
                    pinCode = "7788"
                )
            )
            adminDao.insertAll(defaultAdmins)
            _currentAdmin.value = defaultAdmins.first()
        } else if (_currentAdmin.value == null) {
            _currentAdmin.value = adminDao.getAllAdmins().first().firstOrNull()
        }

        if (categoryDao.getCount() == 0) {
            val categories = listOf(
                Category(id = 1, name = "Luxury Hampers", description = "Curated celebration baskets & festive hampers", iconName = "CardGiftcard", bannerColorHex = "#6A1B4D", displayOrder = 1),
                Category(id = 2, name = "Fine Jewelry", description = "Solid gold, diamonds, and sterling silver pieces", iconName = "Diamond", bannerColorHex = "#D4AF37", displayOrder = 2),
                Category(id = 3, name = "Artisanal Perfumes", description = "Rare oud, amber, and French floral extracts", iconName = "Spa", bannerColorHex = "#1E88E5", displayOrder = 3),
                Category(id = 4, name = "Gourmet Confections", description = "Handmade Belgian truffles & luxury sweets", iconName = "Cake", bannerColorHex = "#8D6E63", displayOrder = 4),
                Category(id = 5, name = "Personalized Keepsakes", description = "Laser-engraved watches, pens & wooden gift boxes", iconName = "Edit", bannerColorHex = "#43A047", displayOrder = 5),
                Category(id = 6, name = "Premium Leather Goods", description = "Full-grain Italian wallets, journals & accessories", iconName = "Luggage", bannerColorHex = "#5D4037", displayOrder = 6)
            )
            categoryDao.insertAll(categories)
        }

        if (productDao.getCount() == 0) {
            val products = listOf(
                Product(
                    title = "Royal Velvet Grandeur Hamper",
                    sku = "MJ-HMP-001",
                    description = "A magnificent celebration hamper packed with vintage sparkling cider, imported Belgian truffles, gold-dusted almonds, scented wax candle, and a handcrafted keepsake ribbon.",
                    categoryId = 1,
                    categoryName = "Luxury Hampers",
                    originalPrice = 149.99,
                    salePrice = 129.99,
                    isOnSale = true,
                    stockQuantity = 18,
                    lowStockThreshold = 5,
                    imageUrls = listOf(
                        "https://images.unsplash.com/photo-1549465220-1a8b9238cd48?w=800&q=80",
                        "https://images.unsplash.com/photo-1513885535751-8b9238bd345a?w=800&q=80"
                    ),
                    tags = listOf("Bestseller", "Holiday", "Luxury"),
                    isFeatured = true,
                    rating = 4.9,
                    reviewsCount = 48
                ),
                Product(
                    title = "18K Gold Solitaire Pendant",
                    sku = "MJ-JWL-102",
                    description = "Dazzling handcrafted 18K yellow gold necklace with a brilliant cut zirconia solitaire. Includes velvet presentation box and certificate of authenticity.",
                    categoryId = 2,
                    categoryName = "Fine Jewelry",
                    originalPrice = 280.00,
                    salePrice = 249.00,
                    isOnSale = true,
                    stockQuantity = 4,
                    lowStockThreshold = 5,
                    imageUrls = listOf(
                        "https://images.unsplash.com/photo-1599643478518-a784e5dc4c8f?w=800&q=80",
                        "https://images.unsplash.com/photo-1515562141207-7a88fb7ce338?w=800&q=80"
                    ),
                    tags = listOf("Anniversary", "Gold", "Romantic"),
                    isFeatured = true,
                    rating = 5.0,
                    reviewsCount = 32
                ),
                Product(
                    title = "Midnight Imperial Oud 100ml",
                    sku = "MJ-PRF-203",
                    description = "Exquisite niche eau de parfum with aged Cambodian agarwood, Damascus rose, and warm bourbon vanilla in a frosted midnight blue crystal flacon.",
                    categoryId = 3,
                    categoryName = "Artisanal Perfumes",
                    originalPrice = 110.00,
                    salePrice = 110.00,
                    isOnSale = false,
                    stockQuantity = 22,
                    lowStockThreshold = 6,
                    imageUrls = listOf(
                        "https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=800&q=80",
                        "https://images.unsplash.com/photo-1523293182086-7651a899d37f?w=800&q=80"
                    ),
                    tags = listOf("Signature", "Fragrance", "Unisex"),
                    isFeatured = true,
                    rating = 4.8,
                    reviewsCount = 29
                ),
                Product(
                    title = "Belgian Artisan Truffle Prestige Box",
                    sku = "MJ-GOU-304",
                    description = "24-piece assorted gourmet praline selection filled with dark ganache, sea salt caramel, roasted hazelnut crunch, and champagne cream.",
                    categoryId = 4,
                    categoryName = "Gourmet Confections",
                    originalPrice = 45.00,
                    salePrice = 38.00,
                    isOnSale = true,
                    stockQuantity = 2, // Low stock demo!
                    lowStockThreshold = 8,
                    imageUrls = listOf(
                        "https://images.unsplash.com/photo-1549007994-cb92caebd54b?w=800&q=80"
                    ),
                    tags = listOf("Chocolates", "Treats", "Sweet"),
                    isFeatured = false,
                    rating = 4.9,
                    reviewsCount = 64
                ),
                Product(
                    title = "Engraved Walnut Chronograph Watch",
                    sku = "MJ-KPS-405",
                    description = "Custom-engraved wooden dial with stainless steel casing and Japanese quartz movement. Packaged in a solid walnut engraved box.",
                    categoryId = 5,
                    categoryName = "Personalized Keepsakes",
                    originalPrice = 125.00,
                    salePrice = 125.00,
                    isOnSale = false,
                    stockQuantity = 11,
                    lowStockThreshold = 4,
                    imageUrls = listOf(
                        "https://images.unsplash.com/photo-1524805444758-089113d48a6d?w=800&q=80"
                    ),
                    tags = listOf("Personalized", "GiftForHim", "Engraved"),
                    isFeatured = false,
                    rating = 4.7,
                    reviewsCount = 19
                ),
                Product(
                    title = "Handstitched Saffiano Leather Wallet",
                    sku = "MJ-LTH-506",
                    description = "Slim bifold wallet made from Italian calf leather with RFID protection, embossed monogram option, and satin inner lining.",
                    categoryId = 6,
                    categoryName = "Premium Leather Goods",
                    originalPrice = 65.00,
                    salePrice = 52.00,
                    isOnSale = true,
                    stockQuantity = 0, // Out of stock demo!
                    lowStockThreshold = 5,
                    imageUrls = listOf(
                        "https://images.unsplash.com/photo-1627123424574-724758594e93?w=800&q=80"
                    ),
                    tags = listOf("Leather", "Accessories", "Minimalist"),
                    isFeatured = false,
                    rating = 4.6,
                    reviewsCount = 14
                )
            )
            productDao.insertAll(products)
            categoryDao.refreshItemCounts()
        }

        if (customerDao.getCount() == 0) {
            val customers = listOf(
                Customer(
                    name = "Fatima Al-Mansoor",
                    email = "fatima.m@example.com",
                    phone = "+971 50 123 4567",
                    address = "Palm Jumeirah Villa 42, Dubai",
                    totalOrders = 7,
                    totalSpend = 1420.50,
                    tier = CustomerTier.PLATINUM,
                    adminNotes = "Prefers midnight delivery and gold embossed gift tags."
                ),
                Customer(
                    name = "Marcus Vance",
                    email = "marcus.v@example.com",
                    phone = "+1 415 889 0122",
                    address = "742 Montgomery St, San Francisco, CA",
                    totalOrders = 4,
                    totalSpend = 680.00,
                    tier = CustomerTier.GOLD,
                    adminNotes = "Regular corporate client buying holiday hampers."
                ),
                Customer(
                    name = "Amina Mwangi",
                    email = "amina.mwangi@example.com",
                    phone = "+254 712 345 678",
                    address = "Riverside Drive Apt 5B, Nairobi",
                    totalOrders = 3,
                    totalSpend = 310.00,
                    tier = CustomerTier.SILVER,
                    adminNotes = "Prompt payments via Mobile Money."
                ),
                Customer(
                    name = "Oliver Schmidt",
                    email = "oliver.s@example.de",
                    phone = "+49 30 9988 7766",
                    address = "Kurfürstendamm 112, Berlin",
                    totalOrders = 1,
                    totalSpend = 129.99,
                    tier = CustomerTier.STANDARD
                )
            )
            customerDao.insertAll(customers)
        }

        if (orderDao.getCount() == 0) {
            val orders = listOf(
                Order(
                    orderNumber = "MGS-92410",
                    customerId = 1,
                    customerName = "Fatima Al-Mansoor",
                    customerEmail = "fatima.m@example.com",
                    customerPhone = "+971 50 123 4567",
                    shippingAddress = "Palm Jumeirah Villa 42, Dubai",
                    items = listOf(
                        OrderItem(
                            productId = 1,
                            productTitle = "Royal Velvet Grandeur Hamper",
                            quantity = 2,
                            unitPrice = 129.99,
                            giftWrapSelected = true,
                            giftMessage = "Happy Silver Jubilee Anniversary! With love."
                        ),
                        OrderItem(
                            productId = 2,
                            productTitle = "18K Gold Solitaire Pendant",
                            quantity = 1,
                            unitPrice = 249.00,
                            giftWrapSelected = true
                        )
                    ),
                    subtotal = 508.98,
                    discountAmount = 50.00,
                    giftWrapFee = 15.00,
                    deliveryFee = 0.00,
                    totalAmount = 473.98,
                    appliedCouponCode = "MAJIGIFT20",
                    orderStatus = OrderStatus.PROCESSING,
                    paymentStatus = PaymentStatus.PAID,
                    paymentMethod = PaymentMethod.CREDIT_CARD,
                    courierName = "DHL Express Worldwide",
                    trackingNumber = "DHL-883920193",
                    adminNotes = "VIP Customer. Prioritize packing with satin royal ribbon."
                ),
                Order(
                    orderNumber = "MGS-92411",
                    customerId = 2,
                    customerName = "Marcus Vance",
                    customerEmail = "marcus.v@example.com",
                    customerPhone = "+1 415 889 0122",
                    shippingAddress = "742 Montgomery St, San Francisco, CA",
                    items = listOf(
                        OrderItem(
                            productId = 3,
                            productTitle = "Midnight Imperial Oud 100ml",
                            quantity = 2,
                            unitPrice = 110.00,
                            giftWrapSelected = true,
                            giftMessage = "Welcome to the new executive leadership team!"
                        )
                    ),
                    subtotal = 220.00,
                    discountAmount = 0.00,
                    giftWrapFee = 10.00,
                    deliveryFee = 12.00,
                    totalAmount = 242.00,
                    orderStatus = OrderStatus.PENDING,
                    paymentStatus = PaymentStatus.UNPAID,
                    paymentMethod = PaymentMethod.COD,
                    courierName = "Maji Express Couriers",
                    adminNotes = "Cash on Delivery. Confirm phone call before dispatch."
                ),
                Order(
                    orderNumber = "MGS-92412",
                    customerId = 3,
                    customerName = "Amina Mwangi",
                    customerEmail = "amina.mwangi@example.com",
                    customerPhone = "+254 712 345 678",
                    shippingAddress = "Riverside Drive Apt 5B, Nairobi",
                    items = listOf(
                        OrderItem(
                            productId = 4,
                            productTitle = "Belgian Artisan Truffle Prestige Box",
                            quantity = 3,
                            unitPrice = 38.00,
                            giftWrapSelected = true
                        )
                    ),
                    subtotal = 114.00,
                    discountAmount = 11.40,
                    giftWrapFee = 8.00,
                    deliveryFee = 5.00,
                    totalAmount = 115.60,
                    appliedCouponCode = "WELCOME10",
                    orderStatus = OrderStatus.SHIPPED,
                    paymentStatus = PaymentStatus.PAID,
                    paymentMethod = PaymentMethod.MOBILE_PAY,
                    courierName = "Fargo Courier",
                    trackingNumber = "FRG-771928"
                ),
                Order(
                    orderNumber = "MGS-92413",
                    customerId = 4,
                    customerName = "Oliver Schmidt",
                    customerEmail = "oliver.s@example.de",
                    customerPhone = "+49 30 9988 7766",
                    shippingAddress = "Kurfürstendamm 112, Berlin",
                    items = listOf(
                        OrderItem(
                            productId = 5,
                            productTitle = "Engraved Walnut Chronograph Watch",
                            quantity = 1,
                            unitPrice = 125.00,
                            giftWrapSelected = true,
                            giftMessage = "Zum Geburtstag viel Glück!"
                        )
                    ),
                    subtotal = 125.00,
                    discountAmount = 0.00,
                    giftWrapFee = 5.00,
                    deliveryFee = 15.00,
                    totalAmount = 145.00,
                    orderStatus = OrderStatus.DELIVERED,
                    paymentStatus = PaymentStatus.PAID,
                    paymentMethod = PaymentMethod.CREDIT_CARD,
                    courierName = "FedEx Priority",
                    trackingNumber = "FDX-99410294"
                )
            )
            orderDao.insertAll(orders)
        }

        if (couponDao.getCount() == 0) {
            val coupons = listOf(
                Coupon(code = "WELCOME10", discountType = DiscountType.PERCENTAGE, discountValue = 10.0, minOrderAmount = 50.0, usageLimit = 500, timesUsed = 84),
                Coupon(code = "MAJIGIFT20", discountType = DiscountType.PERCENTAGE, discountValue = 20.0, minOrderAmount = 100.0, maxDiscountAmount = 60.0, usageLimit = 200, timesUsed = 39),
                Coupon(code = "FESTIVE50", discountType = DiscountType.FIXED_AMOUNT, discountValue = 50.0, minOrderAmount = 250.0, usageLimit = 50, timesUsed = 12),
                Coupon(code = "VIPFLASH", discountType = DiscountType.PERCENTAGE, discountValue = 25.0, minOrderAmount = 150.0, usageLimit = 100, timesUsed = 62)
            )
            couponDao.insertAll(coupons)
        }

        if (promoBannerDao.getCount() == 0) {
            val banners = listOf(
                PromoBanner(
                    title = "Grand Celebration Hampers 2026",
                    subtitle = "Handcrafted with luxury treats, champagne & gold truffles. Get 20% off with MAJIGIFT20",
                    imageUrl = "https://images.unsplash.com/photo-1549465220-1a8b9238cd48?w=1200&q=80",
                    targetType = "CATEGORY",
                    targetId = "1",
                    buttonText = "Explore Hampers",
                    backgroundColorHex = "#4A148C",
                    displayPriority = 1
                ),
                PromoBanner(
                    title = "Royal Imperial Perfumes Launch",
                    subtitle = "Niche perfumes blended with rare oud and Damascus roses.",
                    imageUrl = "https://images.unsplash.com/photo-1592945403244-b3fbafd7f539?w=1200&q=80",
                    targetType = "CATEGORY",
                    targetId = "3",
                    buttonText = "Discover Scents",
                    backgroundColorHex = "#0D47A1",
                    displayPriority = 2
                )
            )
            promoBannerDao.insertAll(banners)
        }

        if (pushNotificationDao.getCount() == 0) {
            val pushLogs = listOf(
                PushNotificationLog(
                    title = "✨ New Festive Gift Hampers Just Arrived!",
                    message = "Surprise your loved ones with our exclusive handcrafted hampers. Limited edition boxes available now.",
                    audience = NotificationAudience.ALL_CUSTOMERS,
                    deepLinkUrl = "majigift://shop/category/1",
                    sentByAdmin = "Amara Maji (Super Admin)",
                    recipientsCount = 2450,
                    deliveredCount = 2418,
                    clickedCount = 412
                ),
                PushNotificationLog(
                    title = "💎 VIP Private Flash Sale: 25% Off Fine Jewelry",
                    message = "Exclusive perk for VIP Platinum & Gold members. Use coupon code VIPFLASH at checkout today.",
                    audience = NotificationAudience.VIP_PLATINUM,
                    deepLinkUrl = "majigift://shop/deals/vip",
                    sentByAdmin = "Kofi Mensah (Growth Lead)",
                    recipientsCount = 380,
                    deliveredCount = 377,
                    clickedCount = 194
                )
            )
            for (log in pushLogs) {
                pushNotificationDao.insertLog(log)
            }
        }
    }

    // Admin Auth
    suspend fun loginWithPin(pin: String): AdminUser? {
        val admins = adminDao.getAllAdmins().first()
        val match = admins.find { it.pinCode == pin && it.isActive }
        if (match != null) {
            adminDao.updateLastLogin(match.id)
            _currentAdmin.value = match
        }
        return match
    }

    suspend fun switchAdminRole(adminUser: AdminUser) {
        _currentAdmin.value = adminUser
    }

    fun logout() {
        _currentAdmin.value = null
    }

    // Product actions
    suspend fun saveProduct(product: Product): Long {
        val id = if (product.id == 0L) {
            productDao.insertProduct(product)
        } else {
            productDao.updateProduct(product.copy(updatedAt = System.currentTimeMillis()))
            product.id
        }
        categoryDao.refreshItemCounts()
        return id
    }

    suspend fun deleteProduct(product: Product) {
        productDao.deleteProduct(product)
        categoryDao.refreshItemCounts()
    }

    suspend fun adjustStock(productId: Long, delta: Int) {
        productDao.updateStock(productId, delta)
    }

    suspend fun updatePriceDiscount(productId: Long, original: Double, sale: Double, onSale: Boolean) {
        productDao.updatePriceAndDiscount(productId, original, sale, onSale)
    }

    // Category actions
    suspend fun saveCategory(category: Category): Long {
        return if (category.id == 0L) {
            categoryDao.insertCategory(category)
        } else {
            categoryDao.updateCategory(category)
            category.id
        }
    }

    suspend fun deleteCategory(category: Category) {
        categoryDao.deleteCategory(category)
    }

    // Order actions
    suspend fun updateOrderStatus(orderId: Long, newStatus: OrderStatus) {
        orderDao.updateOrderStatus(orderId, newStatus)
    }

    suspend fun updatePaymentStatus(orderId: Long, newStatus: PaymentStatus) {
        orderDao.updatePaymentStatus(orderId, newStatus)
    }

    suspend fun updateShipping(orderId: Long, tracking: String, courier: String, status: OrderStatus) {
        orderDao.updateShippingInfo(orderId, tracking, courier, status)
    }

    // Coupon actions
    suspend fun saveCoupon(coupon: Coupon): Long {
        return if (coupon.id == 0L) couponDao.insertCoupon(coupon)
        else {
            couponDao.updateCoupon(coupon)
            coupon.id
        }
    }

    suspend fun deleteCoupon(coupon: Coupon) = couponDao.deleteCoupon(coupon)
    suspend fun toggleCoupon(id: Long, active: Boolean) = couponDao.toggleActive(id, active)

    // Banner actions
    suspend fun saveBanner(banner: PromoBanner): Long {
        return if (banner.id == 0L) promoBannerDao.insertBanner(banner)
        else {
            promoBannerDao.updateBanner(banner)
            banner.id
        }
    }

    suspend fun deleteBanner(banner: PromoBanner) = promoBannerDao.deleteBanner(banner)
    suspend fun toggleBanner(id: Long, active: Boolean) = promoBannerDao.toggleActive(id, active)

    // Customer actions
    suspend fun toggleCustomerSuspension(id: Long, suspended: Boolean) = customerDao.toggleSuspension(id, suspended)
    suspend fun updateCustomerTier(id: Long, tier: CustomerTier) = customerDao.updateTier(id, tier)
    suspend fun updateCustomerNotes(id: Long, notes: String) = customerDao.updateNotes(id, notes)

    // Push Notification
    suspend fun broadcastNotification(
        title: String,
        message: String,
        audience: NotificationAudience,
        deepLink: String
    ): PushNotificationLog {
        val recipients = when (audience) {
            NotificationAudience.ALL_CUSTOMERS -> 2450
            NotificationAudience.VIP_PLATINUM -> 380
            NotificationAudience.INACTIVE_30_DAYS -> 612
            NotificationAudience.CART_ABANDONED -> 145
        }
        val delivered = (recipients * 0.985).toInt()
        val clicked = (delivered * 0.16).toInt()

        val log = PushNotificationLog(
            title = title,
            message = message,
            audience = audience,
            deepLinkUrl = deepLink,
            sentByAdmin = _currentAdmin.value?.fullName ?: "Store Admin",
            sentAt = System.currentTimeMillis(),
            recipientsCount = recipients,
            deliveredCount = delivered,
            clickedCount = clicked
        )
        val id = pushNotificationDao.insertLog(log)
        return log.copy(id = id)
    }

    // Admin Team actions
    suspend fun updateAdminRole(adminId: Long, newRole: AdminRole) {
        adminDao.updateRole(adminId, newRole)
    }
}
