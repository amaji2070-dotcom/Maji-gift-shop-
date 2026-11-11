package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudOff
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.SwitchAccount
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import com.example.data.model.AdminUser
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.RoyalPlum

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminTopBar(
    title: String,
    currentAdmin: AdminUser?,
    adminList: List<AdminUser>,
    onSwitchAdmin: (AdminUser) -> Unit,
    onOpenDrawer: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showRoleMenu by remember { mutableStateOf(false) }
    var showSyncDialog by remember { mutableStateOf(false) }

    TopAppBar(
        modifier = modifier,
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        navigationIcon = {
            IconButton(onClick = onOpenDrawer) {
                Icon(
                    imageVector = Icons.Default.Menu,
                    contentDescription = "Open Navigation Menu",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = "MAJI GIFT SHOP",
                        fontWeight = FontWeight.Black,
                        fontSize = 15.sp,
                        color = GoldenAmber,
                        letterSpacing = 1.sp
                    )
                }
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    fontSize = 17.sp
                )
            }
        },
        actions = {
            // Cloud / Local Status Pill
            Box(
                modifier = Modifier
                    .background(Color(0xFFE8F5E9), RoundedCornerShape(12.dp))
                    .clickable { showSyncDialog = true }
                    .padding(horizontal = 8.dp, vertical = 4.dp),
                contentAlignment = Alignment.Center
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "Sync Status",
                        tint = Color(0xFF2E7D32),
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Local DB Active",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF2E7D32)
                    )
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Admin Role Avatar & Quick Switcher
            Box {
                IconButton(onClick = { showRoleMenu = true }) {
                    Box(
                        modifier = Modifier
                            .size(34.dp)
                            .background(RoyalPlum, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Admin Profile",
                            tint = Color.White,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }

                DropdownMenu(
                    expanded = showRoleMenu,
                    onDismissRequest = { showRoleMenu = false }
                ) {
                    Text(
                        text = "Active Admin Profile",
                        style = MaterialTheme.typography.labelSmall,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                        color = MaterialTheme.colorScheme.primary
                    )
                    adminList.forEach { admin ->
                        DropdownMenuItem(
                            text = {
                                Column {
                                    Text(
                                        text = admin.fullName,
                                        fontWeight = if (admin.id == currentAdmin?.id) FontWeight.Bold else FontWeight.Normal
                                    )
                                    Text(
                                        text = admin.role.roleName,
                                        fontSize = 11.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.SwitchAccount,
                                    contentDescription = null,
                                    tint = if (admin.id == currentAdmin?.id) GoldenAmber else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            },
                            onClick = {
                                onSwitchAdmin(admin)
                                showRoleMenu = false
                            }
                        )
                    }
                }
            }
        }
    )

    if (showSyncDialog) {
        AlertDialog(
            onDismissRequest = { showSyncDialog = false },
            title = { Text("Database & Sync Architecture") },
            text = {
                Column {
                    Text(
                        text = "Maji Gift Shop Admin is operating with Local Room SQLite database engine.",
                        fontWeight = FontWeight.SemiBold
                    )
                    Spacer(modifier = Modifier.padding(4.dp))
                    Text(
                        text = "• All products, categories, orders, coupons, inventory, customer accounts, and logs are fully stored and synchronized locally on device.\n" +
                               "• Because Firebase setup was declined in the dialog, cloud sync to remote Firebase Firestore is currently in standby.\n" +
                               "• To connect live Firebase, simply accept terms when prompted in AI Studio."
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = { showSyncDialog = false }) {
                    Text("Got It")
                }
            }
        )
    }
}
