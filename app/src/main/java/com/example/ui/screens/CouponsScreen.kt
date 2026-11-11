package com.example.ui.screens

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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Discount
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Coupon
import com.example.data.model.DiscountType
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.RoyalPlum
import java.util.Locale

@Composable
fun CouponsScreen(
    coupons: List<Coupon>,
    onSaveCoupon: (Coupon) -> Unit,
    onDeleteCoupon: (Coupon) -> Unit,
    onToggleActive: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingCoupon by remember { mutableStateOf<Coupon?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var couponToDelete by remember { mutableStateOf<Coupon?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingCoupon = null
                    showDialog = true
                },
                containerColor = RoyalPlum,
                contentColor = GoldenAmber
            ) {
                Icon(Icons.Default.Add, contentDescription = "Create Coupon")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                Text(
                    text = "Promotional Coupons & Vouchers (${coupons.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Create customer discount codes, configure minimum spend and usage limits.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(coupons, key = { it.id }) { coupon ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
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
                            Box(
                                modifier = Modifier
                                    .background(RoyalPlum, RoundedCornerShape(8.dp))
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = coupon.code,
                                    fontWeight = FontWeight.Black,
                                    fontSize = 15.sp,
                                    color = GoldenAmber,
                                    letterSpacing = 1.sp
                                )
                            }

                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (coupon.isActive) "Active" else "Disabled",
                                    fontSize = 12.sp,
                                    color = if (coupon.isActive) Color(0xFF2E7D32) else Color(0xFFC62828),
                                    fontWeight = FontWeight.Bold
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = coupon.isActive,
                                    onCheckedChange = { onToggleActive(coupon.id, it) }
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        val discountLabel = if (coupon.discountType == DiscountType.PERCENTAGE) {
                            "${coupon.discountValue.toInt()}% OFF"
                        } else {
                            "$${String.format(Locale.US, "%.0f", coupon.discountValue)} OFF"
                        }

                        Text(
                            text = "$discountLabel on orders over $${coupon.minOrderAmount.toInt()}",
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Usage Progress
                        val progress = if (coupon.usageLimit > 0) (coupon.timesUsed.toFloat() / coupon.usageLimit).coerceIn(0f, 1f) else 0f
                        LinearProgressIndicator(
                            progress = { progress },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp),
                            color = GoldenAmber,
                            trackColor = MaterialTheme.colorScheme.surfaceVariant
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text(
                                text = "Used: ${coupon.timesUsed} / ${coupon.usageLimit}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Row {
                                IconButton(onClick = {
                                    editingCoupon = coupon
                                    showDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = RoyalPlum, modifier = Modifier.size(20.dp))
                                }
                                IconButton(onClick = { couponToDelete = coupon }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F), modifier = Modifier.size(20.dp))
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Add / Edit Dialog
    if (showDialog) {
        var code by remember { mutableStateOf(editingCoupon?.code ?: "") }
        var discountType by remember { mutableStateOf(editingCoupon?.discountType ?: DiscountType.PERCENTAGE) }
        var discountValStr by remember { mutableStateOf(editingCoupon?.discountValue?.toString() ?: "20") }
        var minSpendStr by remember { mutableStateOf(editingCoupon?.minOrderAmount?.toString() ?: "50") }
        var usageLimitStr by remember { mutableStateOf(editingCoupon?.usageLimit?.toString() ?: "100") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (editingCoupon == null) "Create Coupon Code" else "Edit Coupon") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = code,
                            onValueChange = { code = it.uppercase() },
                            label = { Text("Coupon Code *") },
                            singleLine = true,
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedButton(
                            onClick = {
                                val randomNum = (10..99).random()
                                code = "GIFT$randomNum"
                            },
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier.align(Alignment.CenterVertically)
                        ) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Gen", fontSize = 11.sp)
                        }
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        FilterChip(
                            selected = discountType == DiscountType.PERCENTAGE,
                            onClick = { discountType = DiscountType.PERCENTAGE },
                            label = { Text("Percentage (%)") }
                        )
                        FilterChip(
                            selected = discountType == DiscountType.FIXED_AMOUNT,
                            onClick = { discountType = DiscountType.FIXED_AMOUNT },
                            label = { Text("Fixed Amount ($)") }
                        )
                    }

                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                        OutlinedTextField(
                            value = discountValStr,
                            onValueChange = { discountValStr = it },
                            label = { Text(if (discountType == DiscountType.PERCENTAGE) "Discount %" else "Discount $") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                        OutlinedTextField(
                            value = minSpendStr,
                            onValueChange = { minSpendStr = it },
                            label = { Text("Min Spend ($)") },
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                            modifier = Modifier.weight(1f)
                        )
                    }

                    OutlinedTextField(
                        value = usageLimitStr,
                        onValueChange = { usageLimitStr = it },
                        label = { Text("Usage Limit (Max Redeems)") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val coupon = Coupon(
                            id = editingCoupon?.id ?: 0L,
                            code = code.ifBlank { "MAJIGIFT10" },
                            discountType = discountType,
                            discountValue = discountValStr.toDoubleOrNull() ?: 10.0,
                            minOrderAmount = minSpendStr.toDoubleOrNull() ?: 0.0,
                            usageLimit = usageLimitStr.toIntOrNull() ?: 100
                        )
                        onSaveCoupon(coupon)
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPlum)
                ) {
                    Text("Save Coupon", color = GoldenAmber)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (couponToDelete != null) {
        val c = couponToDelete!!
        AlertDialog(
            onDismissRequest = { couponToDelete = null },
            title = { Text("Delete Coupon") },
            text = { Text("Are you sure you want to delete coupon code '${c.code}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteCoupon(c)
                        couponToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { couponToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
