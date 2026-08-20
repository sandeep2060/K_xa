package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Headphones
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.SettingsBrightness
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.User
import com.example.ui.components.AppHeader
import com.example.ui.components.KXaGlassCard
import com.example.ui.components.KXaOutlinedButton
import com.example.ui.components.UserAvatar
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.KxaRadius
import com.example.ui.theme.KxaSpacing
import com.example.ui.theme.KxaTheme
import com.example.ui.theme.LiveRed
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary
import com.example.ui.theme.ThemeMode

@Composable
fun ProfileScreen(
    user: User,
    themeMode: ThemeMode = ThemeMode.DARK,
    onSetThemeMode: (ThemeMode) -> Unit = {},
    onNavigateToAdmin: () -> Unit = {},
    onUpdateStatus: (String) -> Unit,
    onLogout: () -> Unit
) {
    var showEditStatusDialog by remember { mutableStateOf(false) }
    var editedStatus by remember { mutableStateOf(user.statusMessage) }

    var lowLatencySync by remember { mutableStateOf(true) }
    var friendPartyNotifications by remember { mutableStateOf(true) }
    var spatialAudio by remember { mutableStateOf(true) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KxaTheme.colors.background)
            .testTag("screen_profile")
    ) {
        AppHeader(
            title = "Profile & Settings",
            subtitle = "Manage account & preferences",
            showBack = false
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = KxaSpacing.standard, end = KxaSpacing.standard, top = 4.dp, bottom = 100.dp)
        ) {
            // User Bio Profile Card
            item {
                KXaGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = PurplePrimary.copy(alpha = 0.5f)
                ) {
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Box(contentAlignment = Alignment.BottomEnd) {
                            UserAvatar(
                                user = user,
                                size = 78.dp,
                                showOnlineDot = true
                            )
                            Box(
                                modifier = Modifier
                                    .size(28.dp)
                                    .background(PurplePrimary, CircleShape)
                                    .border(2.dp, KxaTheme.colors.surface, CircleShape)
                                    .clickable { showEditStatusDialog = true },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Edit,
                                    contentDescription = "Edit Status",
                                    tint = Color.White,
                                    modifier = Modifier.size(14.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = user.name,
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = KxaTheme.colors.textPrimary
                        )

                        Text(
                            text = "@${user.username}",
                            style = MaterialTheme.typography.bodyMedium,
                            color = PurpleLight
                        )

                        if (user.age != null || !user.dateOfBirth.isNullOrBlank()) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(4.dp),
                                modifier = Modifier
                                    .clip(RoundedCornerShape(KxaRadius.pill))
                                    .background(CyanAccent.copy(alpha = 0.12f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "🎂 Age: ${user.age ?: 16}+ (${user.dobCalendar ?: "AD"})",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = CyanAccent
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        // Status bubble
                        Box(
                            modifier = Modifier
                                .clip(RoundedCornerShape(KxaRadius.pill))
                                .background(KxaTheme.colors.surfaceVariant)
                                .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.pill))
                                .clickable { showEditStatusDialog = true }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "💬 \"${user.statusMessage}\"",
                                style = MaterialTheme.typography.bodySmall,
                                color = KxaTheme.colors.textSecondary
                            )
                        }

                        Spacer(modifier = Modifier.height(18.dp))

                        // Stats Grid Row
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedCornerShape(KxaRadius.md))
                                .background(KxaTheme.colors.surface)
                                .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.md))
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            ProfileStatItem(
                                number = "${user.partiesHosted}",
                                label = "Hosted",
                                accent = PurpleLight
                            )
                            ProfileStatItem(
                                number = "${user.hoursWatched}h",
                                label = "Watched",
                                accent = CyanAccent
                            )
                            ProfileStatItem(
                                number = "${user.friendsCount}",
                                label = "Friends",
                                accent = Color(0xFF10B981)
                            )
                        }
                    }
                }
            }

            // Theme Mode Selector
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "APPEARANCE THEME",
                    style = MaterialTheme.typography.labelMedium,
                    color = PurpleLight,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    ThemeOptionButton(
                        icon = Icons.Default.DarkMode,
                        label = "Dark",
                        isSelected = themeMode == ThemeMode.DARK,
                        modifier = Modifier.weight(1f),
                        onClick = { onSetThemeMode(ThemeMode.DARK) }
                    )
                    ThemeOptionButton(
                        icon = Icons.Default.LightMode,
                        label = "Light",
                        isSelected = themeMode == ThemeMode.LIGHT,
                        modifier = Modifier.weight(1f),
                        onClick = { onSetThemeMode(ThemeMode.LIGHT) }
                    )
                    ThemeOptionButton(
                        icon = Icons.Default.SettingsBrightness,
                        label = "System",
                        isSelected = themeMode == ThemeMode.SYSTEM,
                        modifier = Modifier.weight(1f),
                        onClick = { onSetThemeMode(ThemeMode.SYSTEM) }
                    )
                }
            }

            // Streaming Settings
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "SYNC & PLAYBACK SETTINGS",
                    style = MaterialTheme.typography.labelMedium,
                    color = PurpleLight,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                SettingsToggleRow(
                    icon = Icons.Default.Sync,
                    title = "Ultra-Low Latency Sync",
                    subtitle = "Maintains sub-100ms video synchronization",
                    checked = lowLatencySync,
                    onCheckedChange = { lowLatencySync = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SettingsToggleRow(
                    icon = Icons.Default.Headphones,
                    title = "Spatial Voice Audio",
                    subtitle = "Position voice chat based on avatar layouts",
                    checked = spatialAudio,
                    onCheckedChange = { spatialAudio = it }
                )

                Spacer(modifier = Modifier.height(8.dp))

                SettingsToggleRow(
                    icon = Icons.Default.Notifications,
                    title = "Watch Party Invites",
                    subtitle = "Notify when close friends start streaming",
                    checked = friendPartyNotifications,
                    onCheckedChange = { friendPartyNotifications = it }
                )
            }

            // App & Security
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "PRIVACY & SECURITY",
                    style = MaterialTheme.typography.labelMedium,
                    color = PurpleLight,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                SettingsActionRow(
                    icon = Icons.Default.Security,
                    title = "PIN Locks & Safe Chat",
                    value = "Active"
                )
            }

            // Admin & Moderation Console Access (Always accessible for Admins / Staff, or Demo access)
            item {
                Spacer(modifier = Modifier.height(20.dp))
                Text(
                    text = "STAFF & SECURITY",
                    style = MaterialTheme.typography.labelMedium,
                    color = PurpleLight,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                KXaGlassCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onNavigateToAdmin() }
                        .testTag("btn_open_admin_console"),
                    borderColor = PurplePrimary.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(12.dp),
                            modifier = Modifier.weight(1f)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(Brush.linearGradient(listOf(PurplePrimary, CyanAccent))),
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.AdminPanelSettings,
                                    contentDescription = "Admin Console",
                                    tint = Color.White,
                                    modifier = Modifier.size(22.dp)
                                )
                            }

                            Column {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                                ) {
                                    Text(
                                        text = "Admin & Moderation Console",
                                        style = MaterialTheme.typography.bodyMedium.copy(fontWeight = FontWeight.Bold),
                                        color = KxaTheme.colors.textPrimary
                                    )
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedCornerShape(KxaRadius.pill))
                                            .background(CyanAccent.copy(alpha = 0.2f))
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = user.role.uppercase(),
                                            fontSize = 9.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = CyanAccent
                                        )
                                    }
                                }
                                Text(
                                    text = "User search, suspensions, reports, audit logs & rooms",
                                    style = MaterialTheme.typography.bodySmall,
                                    color = KxaTheme.colors.textSecondary,
                                    fontSize = 11.sp
                                )
                            }
                        }

                        Icon(
                            imageVector = Icons.Default.Security,
                            contentDescription = "Open",
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Logout Action Button
            item {
                Spacer(modifier = Modifier.height(24.dp))

                KXaOutlinedButton(
                    text = "Sign Out of K Xa",
                    onClick = onLogout,
                    icon = Icons.AutoMirrored.Filled.Logout,
                    borderColor = LiveRed.copy(alpha = 0.6f),
                    textColor = LiveRed,
                    modifier = Modifier
                        .testTag("btn_logout")
                        .fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(16.dp))

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "K Xa • Watch. Chat. Connect.",
                        style = MaterialTheme.typography.bodySmall,
                        color = KxaTheme.colors.textMuted
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "App developed by Dreamer Sandeep Gaire",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        ),
                        color = PurpleLight
                    )
                }
            }
        }

        // Edit Status Dialog
        if (showEditStatusDialog) {
            AlertDialog(
                onDismissRequest = { showEditStatusDialog = false },
                containerColor = KxaTheme.colors.surfaceElevated,
                title = {
                    Text(
                        text = "Update Status Bio",
                        color = KxaTheme.colors.textPrimary,
                        fontWeight = FontWeight.Bold
                    )
                },
                text = {
                    Column {
                        Text(
                            text = "Let friends know what you're in the mood to watch:",
                            style = MaterialTheme.typography.bodySmall,
                            color = KxaTheme.colors.textSecondary
                        )
                        Spacer(modifier = Modifier.height(10.dp))
                        OutlinedTextField(
                            value = editedStatus,
                            onValueChange = { editedStatus = it },
                            singleLine = false,
                            maxLines = 3,
                            shape = RoundedCornerShape(KxaRadius.md),
                            colors = OutlinedTextFieldDefaults.colors(
                                focusedBorderColor = PurplePrimary,
                                unfocusedBorderColor = KxaTheme.colors.border,
                                focusedContainerColor = KxaTheme.colors.surfaceVariant,
                                unfocusedContainerColor = KxaTheme.colors.surface,
                                focusedTextColor = KxaTheme.colors.textPrimary,
                                unfocusedTextColor = KxaTheme.colors.textPrimary
                            ),
                            modifier = Modifier.fillMaxWidth()
                        )
                    }
                },
                confirmButton = {
                    Button(
                        onClick = {
                            onUpdateStatus(editedStatus)
                            showEditStatusDialog = false
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                    ) {
                        Text("Save Status", color = Color.White)
                    }
                },
                dismissButton = {
                    OutlinedButton(
                        onClick = { showEditStatusDialog = false }
                    ) {
                        Text("Cancel", color = KxaTheme.colors.textSecondary)
                    }
                }
            )
        }
    }
}

@Composable
private fun ThemeOptionButton(
    icon: ImageVector,
    label: String,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedCornerShape(KxaRadius.md))
            .background(if (isSelected) PurplePrimary else KxaTheme.colors.surface)
            .border(
                1.dp,
                if (isSelected) PurpleLight else KxaTheme.colors.borderSubtle,
                RoundedCornerShape(KxaRadius.md)
            )
            .clickable { onClick() }
            .padding(vertical = 12.dp),
        contentAlignment = Alignment.Center
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) Color.White else KxaTheme.colors.textSecondary,
                modifier = Modifier.size(16.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
            Text(
                text = label,
                fontSize = 12.sp,
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) Color.White else KxaTheme.colors.textSecondary
            )
        }
    }
}

@Composable
private fun ProfileStatItem(
    number: String,
    label: String,
    accent: Color
) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        Text(
            text = number,
            style = MaterialTheme.typography.titleLarge.copy(fontSize = 18.sp),
            fontWeight = FontWeight.Black,
            color = accent
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = KxaTheme.colors.textMuted
        )
    }
}

@Composable
private fun SettingsToggleRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(KxaRadius.md))
            .background(KxaTheme.colors.surface)
            .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.md))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(PurplePrimary.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = PurpleLight,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                    fontWeight = FontWeight.Bold,
                    color = KxaTheme.colors.textPrimary
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = KxaTheme.colors.textMuted
                )
            }
        }

        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange,
            colors = SwitchDefaults.colors(
                checkedThumbColor = Color.White,
                checkedTrackColor = PurplePrimary,
                uncheckedTrackColor = KxaTheme.colors.borderSubtle
            )
        )
    }
}

@Composable
private fun SettingsActionRow(
    icon: ImageVector,
    title: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(KxaRadius.md))
            .background(KxaTheme.colors.surface)
            .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.md))
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .background(CyanAccent.copy(alpha = 0.15f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = CyanAccent,
                    modifier = Modifier.size(18.dp)
                )
            }
            Spacer(modifier = Modifier.width(12.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                fontWeight = FontWeight.Bold,
                color = KxaTheme.colors.textPrimary
            )
        }

        Text(
            text = value,
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = CyanAccent
        )
    }
}
