package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ModalNavigationDrawer
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.local.AppDatabase
import com.example.data.repository.MajiAdminRepository
import com.example.ui.MajiAdminViewModel
import com.example.ui.MajiAdminViewModelFactory
import com.example.ui.components.AdminDrawerContent
import com.example.ui.components.AdminTopBar
import com.example.ui.navigation.AdminScreen
import com.example.ui.screens.BannersScreen
import com.example.ui.screens.CategoriesScreen
import com.example.ui.screens.CouponsScreen
import com.example.ui.screens.CustomersScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.InventoryScreen
import com.example.ui.screens.LoginScreen
import com.example.ui.screens.NotificationsScreen
import com.example.ui.screens.OrdersScreen
import com.example.ui.screens.ProductsScreen
import com.example.ui.screens.ReportsScreen
import com.example.ui.screens.RoleManagementScreen
import com.example.ui.theme.MajiAdminTheme
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val db = AppDatabase.getDatabase(this)
        val repository = MajiAdminRepository(db)
        val factory = MajiAdminViewModelFactory(repository)
        val viewModel = ViewModelProvider(this, factory)[MajiAdminViewModel::class.java]

        setContent {
            MajiAdminTheme {
                MajiAdminApp(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MajiAdminApp(viewModel: MajiAdminViewModel) {
    val currentAdmin by viewModel.currentAdmin.collectAsStateWithLifecycle()
    val currentScreen by viewModel.currentScreen.collectAsStateWithLifecycle()
    val adminList by viewModel.adminUsers.collectAsStateWithLifecycle()
    val metrics by viewModel.dashboardMetrics.collectAsStateWithLifecycle()
    val products by viewModel.products.collectAsStateWithLifecycle()
    val categories by viewModel.categories.collectAsStateWithLifecycle()
    val orders by viewModel.orders.collectAsStateWithLifecycle()
    val coupons by viewModel.coupons.collectAsStateWithLifecycle()
    val banners by viewModel.banners.collectAsStateWithLifecycle()
    val customers by viewModel.customers.collectAsStateWithLifecycle()
    val pushLogs by viewModel.pushLogs.collectAsStateWithLifecycle()

    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    // Listen for snackbar notifications
    LaunchedEffect(viewModel) {
        viewModel.snackbarMessage.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    // Handle back button: from any sub-screen, navigate back to Dashboard
    BackHandler(enabled = currentScreen != AdminScreen.DASHBOARD && currentAdmin != null) {
        if (drawerState.isOpen) {
            scope.launch { drawerState.close() }
        } else {
            viewModel.navigateTo(AdminScreen.DASHBOARD)
        }
    }

    if (currentAdmin == null) {
        // Secure Login / PIN Screen
        LoginScreen(
            adminList = adminList,
            onLoginSuccess = { pin ->
                viewModel.login(pin) { success ->
                    // handled inside ViewModel
                }
            }
        )
    } else {
        ModalNavigationDrawer(
            drawerState = drawerState,
            drawerContent = {
                AdminDrawerContent(
                    currentScreen = currentScreen,
                    currentAdmin = currentAdmin,
                    onScreenSelected = { screen ->
                        viewModel.navigateTo(screen)
                        scope.launch { drawerState.close() }
                    },
                    onLogout = {
                        scope.launch { drawerState.close() }
                        viewModel.logout()
                    }
                )
            }
        ) {
            Scaffold(
                modifier = Modifier.fillMaxSize(),
                snackbarHost = { SnackbarHost(snackbarHostState) },
                topBar = {
                    AdminTopBar(
                        title = currentScreen.title,
                        currentAdmin = currentAdmin,
                        adminList = adminList,
                        onSwitchAdmin = { viewModel.switchRole(it) },
                        onOpenDrawer = {
                            scope.launch { drawerState.open() }
                        }
                    )
                }
            ) { padding ->
                val contentModifier = Modifier
                    .fillMaxSize()
                    .padding(padding)

                when (currentScreen) {
                    AdminScreen.DASHBOARD -> {
                        DashboardScreen(
                            metrics = metrics,
                            recentOrders = orders,
                            lowStockProducts = products.filter { it.isLowStock || it.isOutOfStock },
                            onNavigate = { viewModel.navigateTo(it) },
                            onQuickOrderStatusUpdate = { id, st -> viewModel.updateOrderStatus(id, st) },
                            modifier = contentModifier
                        )
                    }
                    AdminScreen.PRODUCTS -> {
                        ProductsScreen(
                            products = products,
                            categories = categories,
                            onSaveProduct = { viewModel.saveProduct(it) },
                            onDeleteProduct = { viewModel.deleteProduct(it) },
                            modifier = contentModifier
                        )
                    }
                    AdminScreen.ORDERS -> {
                        OrdersScreen(
                            orders = orders,
                            onUpdateOrderStatus = { id, st -> viewModel.updateOrderStatus(id, st) },
                            onUpdatePaymentStatus = { id, pay -> viewModel.updatePaymentStatus(id, pay) },
                            onUpdateShipping = { id, track, courier, st -> viewModel.updateShipping(id, track, courier, st) },
                            modifier = contentModifier
                        )
                    }
                    AdminScreen.INVENTORY -> {
                        InventoryScreen(
                            products = products,
                            onAdjustStock = { id, delta -> viewModel.adjustStock(id, delta) },
                            modifier = contentModifier
                        )
                    }
                    AdminScreen.CATEGORIES -> {
                        CategoriesScreen(
                            categories = categories,
                            onSaveCategory = { viewModel.saveCategory(it) },
                            onDeleteCategory = { viewModel.deleteCategory(it) },
                            modifier = contentModifier
                        )
                    }
                    AdminScreen.COUPONS -> {
                        CouponsScreen(
                            coupons = coupons,
                            onSaveCoupon = { viewModel.saveCoupon(it) },
                            onDeleteCoupon = { viewModel.deleteCoupon(it) },
                            onToggleActive = { id, active -> viewModel.toggleCoupon(id, active) },
                            modifier = contentModifier
                        )
                    }
                    AdminScreen.BANNERS -> {
                        BannersScreen(
                            banners = banners,
                            onSaveBanner = { viewModel.saveBanner(it) },
                            onDeleteBanner = { viewModel.deleteBanner(it) },
                            onToggleActive = { id, active -> viewModel.toggleBanner(id, active) },
                            modifier = contentModifier
                        )
                    }
                    AdminScreen.CUSTOMERS -> {
                        CustomersScreen(
                            customers = customers,
                            onToggleSuspension = { viewModel.toggleCustomerSuspension(it) },
                            onUpdateTier = { id, tier -> viewModel.updateCustomerTier(id, tier) },
                            onUpdateNotes = { id, notes -> viewModel.updateCustomerNotes(id, notes) },
                            modifier = contentModifier
                        )
                    }
                    AdminScreen.REPORTS -> {
                        ReportsScreen(
                            orders = orders,
                            products = products,
                            onShowToast = { viewModel.postMessage(it) },
                            modifier = contentModifier
                        )
                    }
                    AdminScreen.NOTIFICATIONS -> {
                        NotificationsScreen(
                            pushLogs = pushLogs,
                            onSendBroadcast = { title, msg, aud, link ->
                                viewModel.sendPushBroadcast(title, msg, aud, link)
                            },
                            modifier = contentModifier
                        )
                    }
                    AdminScreen.ROLES -> {
                        RoleManagementScreen(
                            currentAdmin = currentAdmin,
                            adminList = adminList,
                            onSwitchAdmin = { viewModel.switchRole(it) },
                            onUpdateRole = { id, role -> viewModel.updateAdminRole(id, role) },
                            modifier = contentModifier
                        )
                    }
                }
            }
        }
    }
}
