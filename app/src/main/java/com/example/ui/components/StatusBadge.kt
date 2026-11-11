package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.CustomerTier
import com.example.data.model.OrderStatus
import com.example.data.model.PaymentStatus

@Composable
fun OrderStatusBadge(status: OrderStatus, modifier: Modifier = Modifier) {
    val (bg, fg) = when (status) {
        OrderStatus.PENDING -> Color(0xFFFFF3E0) to Color(0xFFE65100)
        OrderStatus.PROCESSING -> Color(0xFFE3F2FD) to Color(0xFF1565C0)
        OrderStatus.PACKED -> Color(0xFFF3E5F5) to Color(0xFF6A1B9A)
        OrderStatus.SHIPPED -> Color(0xFFE0F7FA) to Color(0xFF00838F)
        OrderStatus.OUT_FOR_DELIVERY -> Color(0xFFE8F5E9) to Color(0xFF2E7D32)
        OrderStatus.DELIVERED -> Color(0xFFE8F5E9) to Color(0xFF1B5E20)
        OrderStatus.CANCELLED -> Color(0xFFFFEBEE) to Color(0xFFC62828)
    }

    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.label,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun PaymentStatusBadge(status: PaymentStatus, modifier: Modifier = Modifier) {
    val (bg, fg) = when (status) {
        PaymentStatus.PAID -> Color(0xFFE8F5E9) to Color(0xFF1B5E20)
        PaymentStatus.UNPAID -> Color(0xFFFFEBEE) to Color(0xFFC62828)
        PaymentStatus.REFUNDED -> Color(0xFFEEEEEE) to Color(0xFF616161)
    }

    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = status.label,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun StockBadge(quantity: Int, threshold: Int = 5, modifier: Modifier = Modifier) {
    val (bg, fg, label) = when {
        quantity <= 0 -> Triple(Color(0xFFFFEBEE), Color(0xFFC62828), "Out of Stock")
        quantity <= threshold -> Triple(Color(0xFFFFF3E0), Color(0xFFE65100), "Low Stock ($quantity)")
        else -> Triple(Color(0xFFE8F5E9), Color(0xFF2E7D32), "$quantity in stock")
    }

    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = label,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
fun CustomerTierBadge(tier: CustomerTier, modifier: Modifier = Modifier) {
    val (bg, fg) = when (tier) {
        CustomerTier.STANDARD -> Color(0xFFECEFF1) to Color(0xFF455A64)
        CustomerTier.SILVER -> Color(0xFFE1F5FE) to Color(0xFF0277BD)
        CustomerTier.GOLD -> Color(0xFFFFF8E1) to Color(0xFFF57F17)
        CustomerTier.PLATINUM -> Color(0xFFF3E5F5) to Color(0xFF7B1FA2)
    }

    Box(
        modifier = modifier
            .background(bg, RoundedCornerShape(12.dp))
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = tier.label,
            color = fg,
            fontSize = 11.sp,
            fontWeight = FontWeight.Bold
        )
    }
}
