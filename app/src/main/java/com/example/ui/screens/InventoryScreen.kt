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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Inventory2
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Warning
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
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Product
import com.example.ui.components.StockBadge
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.RoyalPlum
import java.util.Locale

@Composable
fun InventoryScreen(
    products: List<Product>,
    onAdjustStock: (Long, Int) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var filterMode by remember { mutableStateOf("ALL") } // ALL, LOW, OUT

    var productForCustomRestock by remember { mutableStateOf<Product?>(null) }
    var customDeltaText by remember { mutableStateOf("10") }

    val filteredProducts = products.filter { p ->
        val matchesSearch = searchQuery.isBlank() ||
                p.title.contains(searchQuery, ignoreCase = true) ||
                p.sku.contains(searchQuery, ignoreCase = true)
        val matchesFilter = when (filterMode) {
            "LOW" -> p.isLowStock
            "OUT" -> p.isOutOfStock
            else -> true
        }
        matchesSearch && matchesFilter
    }

    val totalStockUnits = products.sumOf { it.stockQuantity }
    val totalInventoryValue = products.sumOf { it.stockQuantity * it.salePrice }
    val lowStockCount = products.count { it.isLowStock }
    val outOfStockCount = products.count { it.isOutOfStock }

    Column(
        modifier = modifier
            .fillMaxSize()
    ) {
        // Summary Header Card
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Inventory & Warehouse Overview",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp,
                    color = RoyalPlum
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text("Total Stock Units", style = MaterialTheme.typography.labelSmall)
                        Text("$totalStockUnits pcs", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                    Column {
                        Text("Estimated Value", style = MaterialTheme.typography.labelSmall)
                        Text("$${String.format(Locale.US, "%,.2f", totalInventoryValue)}", fontWeight = FontWeight.Bold, fontSize = 16.sp, color = Color(0xFF2E7D32))
                    }
                    Column {
                        Text("Alerts", style = MaterialTheme.typography.labelSmall)
                        Text("$lowStockCount low • $outOfStockCount out", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Color(0xFFD32F2F))
                    }
                }
            }
        }

        // Search Box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp),
            placeholder = { Text("Filter items by name or SKU...") },
            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
            trailingIcon = {
                if (searchQuery.isNotBlank()) {
                    IconButton(onClick = { searchQuery = "" }) {
                        Icon(Icons.Default.Clear, contentDescription = "Clear")
                    }
                }
            },
            singleLine = true,
            shape = RoundedCornerShape(12.dp)
        )

        Spacer(modifier = Modifier.height(8.dp))

        // Stock Filter Chips
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(horizontal = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                FilterChip(
                    selected = filterMode == "ALL",
                    onClick = { filterMode = "ALL" },
                    label = { Text("All Items (${products.size})") }
                )
            }
            item {
                FilterChip(
                    selected = filterMode == "LOW",
                    onClick = { filterMode = "LOW" },
                    label = { Text("Low Stock ($lowStockCount)") }
                )
            }
            item {
                FilterChip(
                    selected = filterMode == "OUT",
                    onClick = { filterMode = "OUT" },
                    label = { Text("Out of Stock ($outOfStockCount)") }
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Inventory Items List
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(filteredProducts, key = { it.id }) { product ->
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(14.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = product.title,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 14.sp
                                )
                                Text(
                                    text = "SKU: ${product.sku} • Category: ${product.categoryName}",
                                    fontSize = 11.sp,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            }
                            StockBadge(quantity = product.stockQuantity, threshold = product.lowStockThreshold)
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "Current: ${product.stockQuantity} (Alert at ≤${product.lowStockThreshold})",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Medium
                            )

                            // Quick Restock buttons
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                OutlinedButton(
                                    onClick = { onAdjustStock(product.id, 5) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("+5", fontSize = 11.sp)
                                }
                                OutlinedButton(
                                    onClick = { onAdjustStock(product.id, 20) },
                                    shape = RoundedCornerShape(8.dp),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("+20", fontSize = 11.sp)
                                }
                                Button(
                                    onClick = {
                                        productForCustomRestock = product
                                        customDeltaText = "10"
                                    },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPlum),
                                    contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                                    modifier = Modifier.height(30.dp)
                                ) {
                                    Text("Custom", fontSize = 11.sp, color = GoldenAmber)
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    // Custom restock dialog
    if (productForCustomRestock != null) {
        val prod = productForCustomRestock!!
        AlertDialog(
            onDismissRequest = { productForCustomRestock = null },
            title = { Text("Restock: ${prod.title}") },
            text = {
                Column {
                    Text("Enter quantity adjustment (positive to add, negative to reduce):")
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = customDeltaText,
                        onValueChange = { customDeltaText = it },
                        label = { Text("Quantity Adjustment") },
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val delta = customDeltaText.toIntOrNull() ?: 0
                        onAdjustStock(prod.id, delta)
                        productForCustomRestock = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPlum)
                ) {
                    Text("Apply Stock", color = GoldenAmber)
                }
            },
            dismissButton = {
                TextButton(onClick = { productForCustomRestock = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
