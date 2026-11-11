package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.Product
import com.example.ui.DashboardMetrics
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.PaymentStatusBadge
import com.example.ui.components.StatCard
import com.example.ui.navigation.AdminScreen
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.RoyalPlum
import java.util.Locale

@Composable
fun DashboardScreen(
    metrics: DashboardMetrics,
    recentOrders: List<Order>,
    lowStockProducts: List<Product>,
    onNavigate: (AdminScreen) -> Unit,
    onQuickOrderStatusUpdate: (Long, OrderStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Welcome Banner
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = RoyalPlum)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = "Gift Boutique Operations",
                            color = GoldenAmber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp,
                            letterSpacing = 1.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Live Store Overview",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 20.sp
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Manage inventory, dispatch customer orders, track COD payments & broadcast promo campaigns.",
                            color = Color.White.copy(alpha = 0.8f),
                            fontSize = 12.sp
                        )
                    }
                }
            }
        }

        // Low stock warning alert banner if any
        if (lowStockProducts.isNotEmpty()) {
            item {
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigate(AdminScreen.INVENTORY) },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = Color(0xFFFFF3E0))
                ) {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .background(Color(0xFFE65100), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = Color.White,
                                modifier = Modifier.size(20.dp)
                            )
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Attention: ${lowStockProducts.size} Gift Items Low in Stock",
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFFE65100),
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Tap here to review inventory levels and restock items immediately.",
                                color = Color(0xFF8D3B00),
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Primary 4 Stat Cards in 2x2 grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Gross Revenue",
                        value = "$${String.format(Locale.US, "%.2f", metrics.totalRevenue)}",
                        subtitle = "+18.4% vs last week",
                        icon = Icons.Default.AttachMoney,
                        iconTint = Color(0xFF2E7D32),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AdminScreen.REPORTS) }
                    )
                    StatCard(
                        title = "Total Orders",
                        value = "${metrics.totalOrdersCount}",
                        subtitle = "${metrics.pendingOrdersCount} pending action",
                        icon = Icons.Default.ShoppingBag,
                        iconTint = RoyalPlum,
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AdminScreen.ORDERS) }
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    StatCard(
                        title = "Active Catalog",
                        value = "${metrics.totalProductsCount} gifts",
                        subtitle = "${metrics.lowStockItemsCount} low/out-of-stock",
                        icon = Icons.Default.Inventory2,
                        iconTint = Color(0xFFE65100),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AdminScreen.PRODUCTS) }
                    )
                    StatCard(
                        title = "Avg Order Value",
                        value = "$${String.format(Locale.US, "%.1f", metrics.avgOrderValue)}",
                        subtitle = "${metrics.activeCouponsCount} active coupons",
                        icon = Icons.Default.LocalShipping,
                        iconTint = Color(0xFF0288D1),
                        modifier = Modifier.weight(1f),
                        onClick = { onNavigate(AdminScreen.REPORTS) }
                    )
                }
            }
        }

        // Quick Actions Row
        item {
            Text(
                text = "Quick Administrative Actions",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                OutlinedButton(
                    onClick = { onNavigate(AdminScreen.PRODUCTS) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Add Gift", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { onNavigate(AdminScreen.ORDERS) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.LocalShipping, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Orders", fontSize = 12.sp)
                }

                OutlinedButton(
                    onClick = { onNavigate(AdminScreen.NOTIFICATIONS) },
                    modifier = Modifier.weight(1f),
                    shape = RoundedCornerShape(12.dp),
                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 8.dp)
                ) {
                    Icon(imageVector = Icons.Default.Campaign, contentDescription = null, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(text = "Broadcast", fontSize = 12.sp)
                }
            }
        }

        // Weekly Revenue Visual Graph
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Weekly Revenue Trend ($)",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )
                        Text(
                            text = "Last 7 Days",
                            style = MaterialTheme.typography.labelSmall,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    val days = listOf("Mon", "Tue", "Wed", "Thu", "Fri", "Sat", "Sun")
                    val values = listOf(140f, 280f, 195f, 420f, 510f, 680f, 473f)
                    val maxVal = values.maxOrNull() ?: 1f

                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                    ) {
                        Canvas(modifier = Modifier.fillMaxSize()) {
                            val barWidth = size.width / (days.size * 2)
                            val spacing = size.width / days.size

                            values.forEachIndexed { index, v ->
                                val barHeight = (v / maxVal) * (size.height - 24.dp.toPx())
                                val x = index * spacing + (spacing - barWidth) / 2
                                val y = size.height - barHeight - 20.dp.toPx()

                                drawRoundRect(
                                    color = if (index == values.size - 1) Color(0xFFD4AF37) else Color(0xFF6A1B4D),
                                    topLeft = Offset(x, y),
                                    size = Size(barWidth, barHeight),
                                    cornerRadius = CornerRadius(8.dp.toPx(), 8.dp.toPx())
                                )
                            }
                        }

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.BottomCenter),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            days.forEach { day ->
                                Text(
                                    text = day,
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                        }
                    }
                }
            }
        }

        // Recent Orders Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Recent Orders & Shipments",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "View All (${recentOrders.size})",
                    style = MaterialTheme.typography.labelMedium,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.clickable { onNavigate(AdminScreen.ORDERS) }
                )
            }
        }

        // Order list cards
        items(recentOrders.take(3)) { order ->
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onNavigate(AdminScreen.ORDERS) },
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = order.orderNumber,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        OrderStatusBadge(status = order.orderStatus)
                    }

                    Spacer(modifier = Modifier.height(6.dp))

                    Text(
                        text = "${order.customerName} • ${order.items.size} gift item(s)",
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "$${String.format(Locale.US, "%.2f", order.totalAmount)}",
                                fontWeight = FontWeight.Bold,
                                color = RoyalPlum,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            PaymentStatusBadge(status = order.paymentStatus)
                        }

                        // Quick advance order status button
                        if (order.orderStatus != OrderStatus.DELIVERED && order.orderStatus != OrderStatus.CANCELLED) {
                            val nextStatus = when (order.orderStatus) {
                                OrderStatus.PENDING -> OrderStatus.PROCESSING
                                OrderStatus.PROCESSING -> OrderStatus.SHIPPED
                                OrderStatus.PACKED -> OrderStatus.SHIPPED
                                OrderStatus.SHIPPED -> OrderStatus.OUT_FOR_DELIVERY
                                OrderStatus.OUT_FOR_DELIVERY -> OrderStatus.DELIVERED
                                else -> OrderStatus.DELIVERED
                            }
                            Button(
                                onClick = { onQuickOrderStatusUpdate(order.id, nextStatus) },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = RoyalPlum),
                                contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(32.dp)
                            ) {
                                Text(
                                    text = "Mark ${nextStatus.label}",
                                    fontSize = 11.sp,
                                    color = GoldenAmber
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
