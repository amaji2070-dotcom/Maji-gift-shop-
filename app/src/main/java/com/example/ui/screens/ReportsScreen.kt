package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.QueryStats
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Order
import com.example.data.model.PaymentMethod
import com.example.data.model.PaymentStatus
import com.example.data.model.Product
import com.example.ui.components.StatCard
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.RoyalPlum
import java.util.Locale

@Composable
fun ReportsScreen(
    orders: List<Order>,
    products: List<Product>,
    onShowToast: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current

    val paidOrders = orders.filter { it.paymentStatus == PaymentStatus.PAID }
    val totalRevenue = paidOrders.sumOf { it.totalAmount }
    val codPendingRevenue = orders.filter { it.paymentMethod == PaymentMethod.COD && it.paymentStatus == PaymentStatus.UNPAID }.sumOf { it.totalAmount }

    // Aggregate category revenue from ordered items
    val allOrderedItems = orders.flatMap { it.items }
    val totalUnitsSold = allOrderedItems.sumOf { it.quantity }

    // Payment methods breakdown
    val codCount = orders.count { it.paymentMethod == PaymentMethod.COD }
    val cardCount = orders.count { it.paymentMethod == PaymentMethod.CREDIT_CARD }
    val mobileCount = orders.count { it.paymentMethod == PaymentMethod.MOBILE_PAY }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column {
                    Text(
                        text = "Financial & Sales Reports",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold
                    )
                    Text(
                        text = "Consolidated revenue analytics & gift sales metrics",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Button(
                    onClick = {
                        val reportSummary = "MAJI GIFT SHOP - SALES REPORT SUMMARY\n" +
                                "Total Collected Revenue: $${String.format(Locale.US, "%.2f", totalRevenue)}\n" +
                                "Pending COD Collections: $${String.format(Locale.US, "%.2f", codPendingRevenue)}\n" +
                                "Total Orders Placed: ${orders.size}\n" +
                                "Total Gift Items Sold: $totalUnitsSold pcs\n" +
                                "Payment Breakdown: Card ($cardCount), COD ($codCount), Mobile Money ($mobileCount)"

                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Maji Sales Report", reportSummary))
                        onShowToast("Sales report copied to clipboard!")
                    },
                    shape = RoundedCornerShape(12.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPlum),
                    contentPadding = PaddingValues(horizontal = 12.dp, vertical = 6.dp)
                ) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp), tint = GoldenAmber)
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Export", color = GoldenAmber, fontSize = 12.sp)
                }
            }
        }

        // Revenue KPI Cards
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = "Collected Revenue",
                    value = "$${String.format(Locale.US, "%,.2f", totalRevenue)}",
                    subtitle = "${paidOrders.size} paid orders",
                    icon = Icons.Default.AttachMoney,
                    iconTint = Color(0xFF2E7D32),
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = "Pending COD",
                    value = "$${String.format(Locale.US, "%,.2f", codPendingRevenue)}",
                    subtitle = "Cash to collect upon delivery",
                    icon = Icons.Default.Payments,
                    iconTint = Color(0xFFE65100),
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Payment Methods Breakdown
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Payment Methods Breakdown",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    val totalOrd = orders.size.coerceAtLeast(1)

                    PaymentMethodRow(
                        name = "Credit & Debit Cards",
                        count = cardCount,
                        total = totalOrd,
                        color = Color(0xFF1976D2)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PaymentMethodRow(
                        name = "Cash on Delivery (COD)",
                        count = codCount,
                        total = totalOrd,
                        color = Color(0xFFE65100)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    PaymentMethodRow(
                        name = "Mobile Money (M-Pesa / UPI)",
                        count = mobileCount,
                        total = totalOrd,
                        color = Color(0xFF388E3C)
                    )
                }
            }
        }

        // Top Gift Products Sold
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "Top Selling Gift Products",
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    products.take(4).forEachIndexed { index, prod ->
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 6.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(26.dp)
                                        .background(RoyalPlum, CircleShape),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = "#${index + 1}",
                                        color = GoldenAmber,
                                        fontSize = 11.sp,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(prod.title, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
                                    Text(prod.categoryName, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }

                            Text(
                                text = "$${String.format(Locale.US, "%.2f", prod.salePrice)}",
                                fontWeight = FontWeight.Bold,
                                color = RoyalPlum
                            )
                        }
                        if (index < 3) Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }
}

@Composable
fun PaymentMethodRow(name: String, count: Int, total: Int, color: Color) {
    val pct = (count.toFloat() / total * 100).toInt()
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(name, fontSize = 12.sp, fontWeight = FontWeight.Medium)
            Text("$count orders ($pct%)", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = color)
        }
        Spacer(modifier = Modifier.height(4.dp))
        LinearProgressIndicator(
            progress = { count.toFloat() / total },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = color,
            trackColor = MaterialTheme.colorScheme.surfaceVariant
        )
    }
}
