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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.data.model.PromoBanner
import com.example.ui.theme.GoldenAmber
import com.example.ui.theme.RoyalPlum

@Composable
fun BannersScreen(
    banners: List<PromoBanner>,
    onSaveBanner: (PromoBanner) -> Unit,
    onDeleteBanner: (PromoBanner) -> Unit,
    onToggleActive: (Long, Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    var editingBanner by remember { mutableStateOf<PromoBanner?>(null) }
    var showDialog by remember { mutableStateOf(false) }
    var bannerToDelete by remember { mutableStateOf<PromoBanner?>(null) }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    editingBanner = null
                    showDialog = true
                },
                containerColor = RoyalPlum,
                contentColor = GoldenAmber
            ) {
                Icon(Icons.Default.Add, contentDescription = "New Banner")
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(horizontal = 16.dp),
            contentPadding = PaddingValues(vertical = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Text(
                    text = "Promotional Banners & Carousels (${banners.size})",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = "Hero advertising campaigns displayed at the top of the customer Android app.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }

            items(banners, key = { it.id }) { banner ->
                val bgCol = try {
                    Color(android.graphics.Color.parseColor(banner.backgroundColorHex))
                } catch (_: Exception) {
                    RoyalPlum
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(20.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
                ) {
                    Column {
                        // Live Customer Mockup Banner
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(140.dp)
                                .background(bgCol)
                        ) {
                            if (banner.imageUrl.isNotBlank()) {
                                AsyncImage(
                                    model = banner.imageUrl,
                                    contentDescription = banner.title,
                                    contentScale = ContentScale.Crop,
                                    modifier = Modifier
                                        .fillMaxSize()
                                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)),
                                    alpha = 0.35f
                                )
                            }

                            Column(
                                modifier = Modifier
                                    .fillMaxSize()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.SpaceBetween
                            ) {
                                Column {
                                    Text(
                                        text = banner.title,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 16.sp,
                                        color = Color.White
                                    )
                                    Spacer(modifier = Modifier.height(4.dp))
                                    Text(
                                        text = banner.subtitle,
                                        fontSize = 12.sp,
                                        color = Color.White.copy(alpha = 0.85f),
                                        maxLines = 2
                                    )
                                }

                                Box(
                                    modifier = Modifier
                                        .background(GoldenAmber, RoundedCornerShape(8.dp))
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                ) {
                                    Text(
                                        text = banner.buttonText,
                                        color = RoyalPlum,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp
                                    )
                                }
                            }
                        }

                        // Banner Controls
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Text(
                                    text = if (banner.isActive) "Active on Store" else "Inactive",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (banner.isActive) Color(0xFF2E7D32) else Color(0xFFC62828)
                                )
                                Spacer(modifier = Modifier.width(6.dp))
                                Switch(
                                    checked = banner.isActive,
                                    onCheckedChange = { onToggleActive(banner.id, it) }
                                )
                            }

                            Row {
                                IconButton(onClick = {
                                    editingBanner = banner
                                    showDialog = true
                                }) {
                                    Icon(Icons.Default.Edit, contentDescription = "Edit", tint = RoyalPlum)
                                }
                                IconButton(onClick = { bannerToDelete = banner }) {
                                    Icon(Icons.Default.Delete, contentDescription = "Delete", tint = Color(0xFFD32F2F))
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
        var title by remember { mutableStateOf(editingBanner?.title ?: "") }
        var subtitle by remember { mutableStateOf(editingBanner?.subtitle ?: "") }
        var imageUrl by remember {
            mutableStateOf(editingBanner?.imageUrl ?: "https://images.unsplash.com/photo-1549465220-1a8b9238cd48?w=1200&q=80")
        }
        var buttonText by remember { mutableStateOf(editingBanner?.buttonText ?: "Shop Now") }
        var bgHex by remember { mutableStateOf(editingBanner?.backgroundColorHex ?: "#4A148C") }

        AlertDialog(
            onDismissRequest = { showDialog = false },
            title = { Text(if (editingBanner == null) "Create Promo Banner" else "Edit Promo Banner") },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                    OutlinedTextField(
                        value = title,
                        onValueChange = { title = it },
                        label = { Text("Banner Headline *") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = subtitle,
                        onValueChange = { subtitle = it },
                        label = { Text("Subtext / Offer Details") },
                        maxLines = 2,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = imageUrl,
                        onValueChange = { imageUrl = it },
                        label = { Text("Image URL") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = buttonText,
                        onValueChange = { buttonText = it },
                        label = { Text("CTA Button Text") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = bgHex,
                        onValueChange = { bgHex = it },
                        label = { Text("Background Hex (e.g. #4A148C)") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                Button(
                    onClick = {
                        val banner = PromoBanner(
                            id = editingBanner?.id ?: 0L,
                            title = title.ifBlank { "Maji Gift Promotion" },
                            subtitle = subtitle,
                            imageUrl = imageUrl,
                            buttonText = buttonText,
                            backgroundColorHex = bgHex
                        )
                        onSaveBanner(banner)
                        showDialog = false
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = RoyalPlum)
                ) {
                    Text("Save Banner", color = GoldenAmber)
                }
            },
            dismissButton = {
                TextButton(onClick = { showDialog = false }) {
                    Text("Cancel")
                }
            }
        )
    }

    if (bannerToDelete != null) {
        val b = bannerToDelete!!
        AlertDialog(
            onDismissRequest = { bannerToDelete = null },
            title = { Text("Delete Banner") },
            text = { Text("Are you sure you want to remove promotional banner '${b.title}'?") },
            confirmButton = {
                Button(
                    onClick = {
                        onDeleteBanner(b)
                        bannerToDelete = null
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(onClick = { bannerToDelete = null }) {
                    Text("Cancel")
                }
            }
        )
    }
}
