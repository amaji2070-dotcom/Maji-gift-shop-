package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Block
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Customer
import com.example.data.model.CustomerTier
import com.example.ui.components.CustomerTierBadge
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.RoyalPlum
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CustomersScreen(
    customers: List<Customer>,
    onToggleSuspension: (Customer) -> Unit,
    onUpdateTier: (Long, CustomerTier) -> Unit,
    onUpdateNotes: (Long, String) -> Unit,
    modifier: Modifier = Modifier
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedCustomerForDetail by remember { mutableStateOf<Customer?>(null) }

    val filteredCustomers = customers.filter { c ->
        searchQuery.isBlank() ||
                c.name.contains(searchQuery, ignoreCase = true) ||
                c.email.contains(searchQuery, ignoreCase = true) ||
                c.phone.contains(searchQuery, ignoreCase = true)
    }

    Column(modifier = modifier.fillMaxSize()) {
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 8.dp),
            placeholder = { Text("Search customers by name, email, or phone...") },
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

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            item {
                Text(
                    text = "Customer Directory (${customers.size} registered shoppers)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            items(filteredCustomers, key = { it.id }) { customer ->
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { selectedCustomerForDetail = customer },
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = customer.name,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 15.sp
                                )
                                if (customer.isSuspended) {
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Icon(
                                        imageVector = Icons.Default.Block,
                                        contentDescription = "Suspended",
                                        tint = Color(0xFFC62828),
                                        modifier = Modifier.size(16.dp)
                                    )
                                }
                            }
                            CustomerTierBadge(tier = customer.tier)
                        }

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${customer.email} • ${customer.phone}",
                            fontSize = 12.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )

                        Spacer(modifier = Modifier.height(8.dp))
                        Divider()
                        Spacer(modifier = Modifier.height(8.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = "${customer.totalOrders} total orders",
                                fontSize = 12.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )

                            Text(
                                text = "Lifetime Spend: $${String.format(Locale.US, "%,.2f", customer.totalSpend)}",
                                fontWeight = FontWeight.Bold,
                                color = RoyalPlum,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            }
        }
    }

    // Customer Detail Dialog
    if (selectedCustomerForDetail != null) {
        val cust = customers.find { it.id == selectedCustomerForDetail!!.id } ?: selectedCustomerForDetail!!
        var notesText by remember { mutableStateOf(cust.adminNotes) }
        var tierExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { selectedCustomerForDetail = null },
            title = {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(cust.name, fontWeight = FontWeight.Bold)
                    CustomerTierBadge(tier = cust.tier)
                }
            },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    Text("Contact & Shipping:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    Text("Email: ${cust.email}", fontSize = 12.sp)
                    Text("Phone: ${cust.phone}", fontSize = 12.sp)
                    Text("Address: ${cust.address}", fontSize = 12.sp)

                    Divider()

                    // Tier Selector
                    Text("VIP Customer Tier:", fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    ExposedDropdownMenuBox(
                        expanded = tierExpanded,
                        onExpandedChange = { tierExpanded = !tierExpanded }
                    ) {
                        OutlinedTextField(
                            value = cust.tier.label,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Customer Tier") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = tierExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = tierExpanded,
                            onDismissRequest = { tierExpanded = false }
                        ) {
                            CustomerTier.values().forEach { t ->
                                DropdownMenuItem(
                                    text = { Text(t.label) },
                                    onClick = {
                                        onUpdateTier(cust.id, t)
                                        tierExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    // Suspension Toggle
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column {
                            Text(
                                text = "Account Status",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = if (cust.isSuspended) "Suspended (Cannot order)" else "Active Shopper",
                                fontSize = 11.sp,
                                color = if (cust.isSuspended) Color(0xFFC62828) else Color(0xFF2E7D32)
                            )
                        }
                        Switch(
                            checked = !cust.isSuspended,
                            onCheckedChange = { onToggleSuspension(cust) }
                        )
                    }

                    // Admin Notes
                    OutlinedTextField(
                        value = notesText,
                        onValueChange = { notesText = it },
                        label = { Text("Admin Private Notes") },
                        placeholder = { Text("e.g. VIP preference, corporate delivery requirements...") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateNotes(cust.id, notesText)
                        selectedCustomerForDetail = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPlum)
                ) {
                    Text("Save Notes & Close", color = GoldenAmber)
                }
            },
            dismissButton = {
                TextButton(onClick = { selectedCustomerForDetail = null }) {
                    Text("Close")
                }
            }
        )
    }
}
