package com.example.ui.screens

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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.LocalShipping
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.ui.components.OrderStatusBadge
import com.example.ui.components.PaymentStatusBadge
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.RoyalPlum
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun OrdersScreen(
    orders: List<Order>,
    onUpdateOrderStatus: (Long, OrderStatus) -> Unit,
    onUpdatePaymentStatus: (Long, PaymentStatus) -> Unit,
    onUpdateShipping: (Long, String, String, OrderStatus) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedStatusFilter by remember { mutableStateOf<OrderStatus?>(null) } // null = All
    var selectedOrderForDetail by remember { mutableStateOf<Order?>(null) }

    val filteredOrders = orders.filter { order ->
        val matchesStatus = selectedStatusFilter == null || order.orderStatus == selectedStatusFilter
        val matchesSearch = searchQuery.isBlank() ||
                order.orderNumber.contains(searchQuery, ignoreCase = true) ||
                order.customerName.contains(searchQuery, ignoreCase = true) ||
                order.customerPhone.contains(searchQuery, ignoreCase = true)
        matchesStatus && matchesSearch
    }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Search by Order #, Customer, or Phone...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(14.dp)
        )

        // Status Tabs / Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = selectedStatusFilter == null,
                    onClick = { selectedStatusFilter = null },
                    label = { Text("All Orders (${orders.size})") }
                )
            }
            OrderStatus.values().forEach { st ->
                val count = orders.count { it.orderStatus == st }
                item {
                    FilterChip(
                        selected = selectedStatusFilter == st,
                        onClick = { selectedStatusFilter = st },
                        label = { Text("${st.label} ($count)") }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Orders List
        if (filteredOrders.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ShoppingBag,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                        modifier = Modifier.size(56.dp)
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = "No orders found for selected filter",
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                items(filteredOrders, key = { it.id }) { order ->
                    OrderCard(
                        order = order,
                        onClick = { selectedOrderForDetail = order }
                    )
                }
            }
        }
    }

    // Order Details & Pipeline Dialog
    if (selectedOrderForDetail != null) {
        val currentOrder = orders.find { it.id == selectedOrderForDetail!!.id } ?: selectedOrderForDetail!!
        OrderDetailDialog(
            order = currentOrder,
            onDismiss = { selectedOrderForDetail = null },
            onUpdateStatus = { newSt -> onUpdateOrderStatus(currentOrder.id, newSt) },
            onUpdatePayment = { newPay -> onUpdatePaymentStatus(currentOrder.id, newPay) },
            onUpdateShipping = { tracking, courier, st -> onUpdateShipping(currentOrder.id, tracking, courier, st) }
        )
    }
}

@Composable
fun OrderCard(
    order: Order,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val dateStr = SimpleDateFormat("MMM dd, yyyy • HH:mm", Locale.getDefault()).format(Date(order.createdAt))

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = order.orderNumber,
                        fontWeight = FontWeight.Bold,
                        fontSize = 16.sp
                    )
                    Text(
                        text = dateStr,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
                OrderStatusBadge(status = order.orderStatus)
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = "${order.customerName} • ${order.customerPhone}",
                fontWeight = FontWeight.Medium,
                fontSize = 13.sp
            )

            Text(
                text = "${order.items.size} gift items • ${order.shippingAddress}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(12.dp))
            Divider()
            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    PaymentStatusBadge(status = order.paymentStatus)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = order.paymentMethod.label,
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Text(
                    text = "$${String.format(Locale.US, "%.2f", order.totalAmount)}",
                    fontWeight = FontWeight.Bold,
                    fontSize = 16.sp,
                    color = RoyalPlum
                )
            }
        }
    }
}

@Composable
fun OrderDetailDialog(
    order: Order,
    onDismiss: () -> Unit,
    onUpdateStatus: (OrderStatus) -> Unit,
    onUpdatePayment: (PaymentStatus) -> Unit,
    onUpdateShipping: (String, String, OrderStatus) -> Unit
) {
    var trackingText by remember { mutableStateOf(order.trackingNumber ?: "") }
    var courierText by remember { mutableStateOf(order.courierName) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(text = "Order ${order.orderNumber}", fontWeight = FontWeight.Bold)
                OrderStatusBadge(status = order.orderStatus)
            }
        },
        text = {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .verticalScroll(rememberScrollState()),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                // Customer Information Box
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(order.customerName, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Phone, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(order.customerPhone, fontSize = 12.sp)
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(order.shippingAddress, fontSize = 12.sp)
                        }
                    }
                }

                // Delivery Status Pipeline
                Text("Update Delivery Pipeline Status:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    items(OrderStatus.values()) { status ->
                        FilterChip(
                            selected = order.orderStatus == status,
                            onClick = { onUpdateStatus(status) },
                            label = { Text(status.label, fontSize = 11.sp) }
                        )
                    }
                }

                // Payment Status & COD Actions
                Text("Payment Management:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Button(
                        onClick = { onUpdatePayment(PaymentStatus.PAID) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (order.paymentStatus == PaymentStatus.PAID) Color(0xFF2E7D32) else Color(0xFFE8F5E9)
                        )
                    ) {
                        Text(
                            text = "Mark Paid (COD/Online)",
                            fontSize = 11.sp,
                            color = if (order.paymentStatus == PaymentStatus.PAID) Color.White else Color(0xFF2E7D32)
                        )
                    }

                    Button(
                        onClick = { onUpdatePayment(PaymentStatus.UNPAID) },
                        modifier = Modifier.weight(1f),
                        colors = ButtonDefaults.buttonColors(
                            containerColor = if (order.paymentStatus == PaymentStatus.UNPAID) Color(0xFFC62828) else Color(0xFFFFEBEE)
                        )
                    ) {
                        Text(
                            text = "Mark Unpaid",
                            fontSize = 11.sp,
                            color = if (order.paymentStatus == PaymentStatus.UNPAID) Color.White else Color(0xFFC62828)
                        )
                    }
                }

                // Courier & Tracking Editor
                Text("Courier & Tracking Info:", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                OutlinedTextField(
                    value = courierText,
                    onValueChange = { courierText = it },
                    label = { Text("Courier Service") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = trackingText,
                    onValueChange = { trackingText = it },
                    label = { Text("Tracking Number") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                Button(
                    onClick = {
                        onUpdateShipping(trackingText, courierText, order.orderStatus)
                    },
                    modifier = Modifier.fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPlum)
                ) {
                    Text("Save Tracking Details", color = GoldenAmber)
                }

                Divider()

                // Items list
                Text("Purchased Gifts (${order.items.size}):", fontWeight = FontWeight.Bold, fontSize = 13.sp)
                order.items.forEach { item ->
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item.productTitle, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                Text("$${String.format(Locale.US, "%.2f", item.totalPrice)}", fontWeight = FontWeight.Bold)
                            }
                            Text("Qty: ${item.quantity} × $${item.unitPrice}", fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)

                            if (item.giftWrapSelected) {
                                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(top = 4.dp)) {
                                    Icon(Icons.Default.CardGiftcard, contentDescription = null, tint = GoldenAmber, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("Gift Wrap Requested", fontSize = 11.sp, color = GoldenAmber, fontWeight = FontWeight.Bold)
                                }
                            }
                            if (item.giftMessage.isNotBlank()) {
                                Text("Card Message: \"${item.giftMessage}\"", fontSize = 11.sp, fontStyle = androidx.compose.ui.text.font.FontStyle.Italic)
                            }
                        }
                    }
                }

                // Order Summary Totals
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                ) {
                    Column(modifier = Modifier.padding(12.dp)) {
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Subtotal", fontSize = 12.sp)
                            Text("$${String.format(Locale.US, "%.2f", order.subtotal)}", fontSize = 12.sp)
                        }
                        if (order.discountAmount > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Coupon Discount (${order.appliedCouponCode ?: ""})", fontSize = 12.sp, color = Color(0xFF2E7D32))
                                Text("-$${String.format(Locale.US, "%.2f", order.discountAmount)}", fontSize = 12.sp, color = Color(0xFF2E7D32))
                            }
                        }
                        if (order.giftWrapFee > 0) {
                            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Gift Wrapping Fee", fontSize = 12.sp)
                                Text("$${String.format(Locale.US, "%.2f", order.giftWrapFee)}", fontSize = 12.sp)
                            }
                        }
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Delivery Fee", fontSize = 12.sp)
                            Text("$${String.format(Locale.US, "%.2f", order.deliveryFee)}", fontSize = 12.sp)
                        }
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Amount", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            Text("$${String.format(Locale.US, "%.2f", order.totalAmount)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = RoyalPlum)
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Close")
            }
        }
    )
}
