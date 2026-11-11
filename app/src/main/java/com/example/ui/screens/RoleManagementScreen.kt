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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VerifiedUser
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
import com.example.data.model.AdminPermission
import com.example.data.model.AdminRole
import com.example.data.model.AdminUser
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.RoyalPlum

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RoleManagementScreen(
    currentAdmin: AdminUser?,
    adminList: List<AdminUser>,
    onSwitchAdmin: (AdminUser) -> Unit,
    onUpdateRole: (Long, AdminRole) -> Unit,
    modifier: Modifier = Modifier
) {
    var adminToEditRole by remember { mutableStateOf<AdminUser?>(null) }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Security Notice Card
        item {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(18.dp),
                colors = CardDefaults.cardColors(containerColor = RoyalPlum)
            ) {
                Row(
                    modifier = Modifier.padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .background(GoldenAmber, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(Icons.Default.Security, contentDescription = null, tint = RoyalPlum)
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text("Role-Based Access Control (RBAC)", fontWeight = FontWeight.Bold, color = GoldenAmber, fontSize = 14.sp)
                        Text(
                            "Privileged functions (product deletion, financial reports, push broadcasts) are strictly gated per role.",
                            color = Color.White.copy(alpha = 0.85f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Active Admin Profile Info
        item {
            Text(
                text = "Currently Active Admin Session",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column {
                        Text(
                            text = currentAdmin?.fullName ?: "Administrator",
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                        Text(
                            text = currentAdmin?.role?.roleName ?: "Super Admin",
                            fontSize = 13.sp,
                            color = RoyalPlum,
                            fontWeight = FontWeight.SemiBold
                        )
                        Text(
                            text = currentAdmin?.email ?: "",
                            fontSize = 11.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }

                    Box(
                        modifier = Modifier
                            .background(Color(0xFFE8F5E9), RoundedCornerShape(8.dp))
                            .padding(horizontal = 8.dp, vertical = 4.dp)
                    ) {
                        Text("Active Now", color = Color(0xFF2E7D32), fontWeight = FontWeight.Bold, fontSize = 11.sp)
                    }
                }
            }
        }

        // Team Accounts List
        item {
            Text(
                text = "Admin Staff Accounts (${adminList.size})",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        items(adminList, key = { it.id }) { admin ->
            Card(
                modifier = Modifier.fillMaxWidth(),
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
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = admin.fullName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 15.sp
                            )
                            Text(
                                text = admin.role.roleName,
                                fontSize = 12.sp,
                                color = RoyalPlum,
                                fontWeight = FontWeight.SemiBold
                            )
                            Text(
                                text = "${admin.email} • PIN: ${admin.pinCode}",
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            if (admin.id != currentAdmin?.id) {
                                Button(
                                    onClick = { onSwitchAdmin(admin) },
                                    shape = RoundedCornerShape(8.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPlum),
                                    contentPadding = PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                    modifier = Modifier.height(32.dp)
                                ) {
                                    Text("Switch", fontSize = 11.sp, color = GoldenAmber)
                                }
                            }
                            IconButton(onClick = { adminToEditRole = admin }) {
                                Icon(Icons.Default.Edit, contentDescription = "Edit Role", tint = RoyalPlum)
                            }
                        }
                    }
                }
            }
        }

        // Permissions Matrix Overview
        item {
            Text(
                text = "Role Permissions Breakdown Matrix",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f))
            ) {
                Column(modifier = Modifier.padding(14.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    AdminRole.values().forEach { role ->
                        Column {
                            Text(
                                text = role.roleName,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                color = RoyalPlum
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = role.permissions.joinToString(", ") { it.title },
                                fontSize = 11.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                        Divider(modifier = Modifier.padding(vertical = 4.dp))
                    }
                }
            }
        }
    }

    // Role editing dialog
    if (adminToEditRole != null) {
        val target = adminToEditRole!!
        var selectedRole by remember { mutableStateOf(target.role) }
        var dropdownExpanded by remember { mutableStateOf(false) }

        AlertDialog(
            onDismissRequest = { adminToEditRole = null },
            title = { Text("Update Role: ${target.fullName}") },
            text = {
                Column {
                    Text("Select new administrative role with granted capabilities:")
                    Spacer(modifier = Modifier.height(8.dp))

                    ExposedDropdownMenuBox(
                        expanded = dropdownExpanded,
                        onExpandedChange = { dropdownExpanded = !dropdownExpanded }
                    ) {
                        OutlinedTextField(
                            value = selectedRole.roleName,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Assigned Role") },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = dropdownExpanded) },
                            modifier = Modifier
                                .fillMaxWidth()
                                .menuAnchor()
                        )
                        ExposedDropdownMenu(
                            expanded = dropdownExpanded,
                            onDismissRequest = { dropdownExpanded = false }
                        ) {
                            AdminRole.values().forEach { r ->
                                DropdownMenuItem(
                                    text = { Text(r.roleName) },
                                    onClick = {
                                        selectedRole = r
                                        dropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        onUpdateRole(target.id, selectedRole)
                        adminToEditRole = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPlum)
                ) {
                    Text("Save Role", color = GoldenAmber)
                }
            },
            dismissButton = {
                TextButton(onClick = { adminToEditRole = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
