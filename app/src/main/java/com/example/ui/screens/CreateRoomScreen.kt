package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.PlayCircle
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppHeader
import com.example.ui.components.KXaButton
import com.example.ui.components.KXaChip
import com.example.ui.components.KXaTextField
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.KxaRadius
import com.example.ui.theme.KxaSpacing
import com.example.ui.theme.KxaTheme
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary

@Composable
fun CreateRoomScreen(
    onCreateRoom: (title: String, videoUrl: String, source: String, category: String, isPrivate: Boolean, pin: String) -> Unit,
    onBack: () -> Unit
) {
    var roomTitle by remember { mutableStateOf("Weekend Watch Party 🍿") }
    var selectedSource by remember { mutableStateOf("YouTube") }
    var videoUrl by remember { mutableStateOf("https://youtube.com/watch?v=kxa_stream_99") }
    var selectedCategory by remember { mutableStateOf("Anime & Sci-Fi") }
    var privacyMode by remember { mutableIntStateOf(0) } // 0: Public, 1: Friends Only, 2: Private PIN
    var pinCode by remember { mutableStateOf("4821") }
    var enableVoiceChat by remember { mutableStateOf(true) }

    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KxaTheme.colors.background)
            .navigationBarsPadding()
            .testTag("screen_create_room")
    ) {
        AppHeader(
            title = "Create Room",
            subtitle = "Set up synchronized stream",
            showBack = true,
            onBackClick = onBack
        )

        Column(
            modifier = Modifier
                .weight(1f)
                .verticalScroll(scrollState)
                .padding(horizontal = KxaSpacing.standard, vertical = KxaSpacing.xs)
        ) {
            // Room Name
            Text(
                text = "PARTY TITLE",
                style = MaterialTheme.typography.labelMedium,
                color = PurpleLight,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            KXaTextField(
                value = roomTitle,
                onValueChange = { roomTitle = it },
                placeholder = "e.g. Movie Night with Friends",
                modifier = Modifier
                    .testTag("input_room_title")
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Video Source Platform
            Text(
                text = "STREAM PLATFORM",
                style = MaterialTheme.typography.labelMedium,
                color = PurpleLight,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("YouTube", "Twitch", "Vimeo", "Custom Link").forEach { platform ->
                    val isSelected = selectedSource == platform
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedCornerShape(KxaRadius.md))
                            .background(if (isSelected) PurplePrimary else KxaTheme.colors.surface)
                            .border(
                                1.dp,
                                if (isSelected) PurpleLight else KxaTheme.colors.borderSubtle,
                                RoundedCornerShape(KxaRadius.md)
                            )
                            .clickable { selectedSource = platform }
                            .padding(vertical = 10.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = platform,
                            fontSize = 12.sp,
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else KxaTheme.colors.textSecondary
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Video URL Input
            Text(
                text = "VIDEO / STREAM URL",
                style = MaterialTheme.typography.labelMedium,
                color = PurpleLight,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            KXaTextField(
                value = videoUrl,
                onValueChange = { videoUrl = it },
                placeholder = "https://...",
                leadingIcon = Icons.Default.PlayCircle,
                modifier = Modifier
                    .testTag("input_video_url")
                    .fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(18.dp))

            // Category Chips
            Text(
                text = "CATEGORY",
                style = MaterialTheme.typography.labelMedium,
                color = PurpleLight,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("Anime & Sci-Fi", "Gaming", "Music", "Cinema").forEach { cat ->
                    KXaChip(
                        text = cat,
                        selected = selectedCategory == cat,
                        onClick = { selectedCategory = cat }
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Privacy Option Cards
            Text(
                text = "ROOM PRIVACY",
                style = MaterialTheme.typography.labelMedium,
                color = PurpleLight,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(8.dp))

            PrivacyOptionRow(
                icon = Icons.Default.Public,
                title = "Public Watch Party",
                subtitle = "Anyone can find and join from the discovery lobby",
                isSelected = privacyMode == 0,
                onClick = { privacyMode = 0 }
            )

            Spacer(modifier = Modifier.height(8.dp))

            PrivacyOptionRow(
                icon = Icons.Default.VideoLibrary,
                title = "Friends Only",
                subtitle = "Only accepted friends on your list can enter",
                isSelected = privacyMode == 1,
                onClick = { privacyMode = 1 }
            )

            Spacer(modifier = Modifier.height(8.dp))

            PrivacyOptionRow(
                icon = Icons.Default.Lock,
                title = "Private Room with PIN",
                subtitle = "Only guests with your 4-digit code can enter",
                isSelected = privacyMode == 2,
                onClick = { privacyMode = 2 }
            )

            if (privacyMode == 2) {
                Spacer(modifier = Modifier.height(10.dp))
                KXaTextField(
                    value = pinCode,
                    onValueChange = { if (it.length <= 4) pinCode = it },
                    placeholder = "4-Digit Access PIN",
                    modifier = Modifier.fillMaxWidth()
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Voice Chat Switch
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(KxaRadius.lg))
                    .background(KxaTheme.colors.surface)
                    .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.lg))
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .background(CyanAccent.copy(alpha = 0.15f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column {
                        Text(
                            text = "Enable Voice Chat",
                            style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
                            fontWeight = FontWeight.Bold,
                            color = KxaTheme.colors.textPrimary
                        )
                        Text(
                            text = "Allow participants to talk in real-time",
                            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                            color = KxaTheme.colors.textSecondary
                        )
                    }
                }

                Switch(
                    checked = enableVoiceChat,
                    onCheckedChange = { enableVoiceChat = it },
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = PurplePrimary,
                        uncheckedTrackColor = KxaTheme.colors.borderSubtle
                    )
                )
            }
        }

        // Bottom CTA Button
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(KxaSpacing.standard)
        ) {
            KXaButton(
                text = "Launch Watch Party ➔",
                onClick = {
                    onCreateRoom(
                        roomTitle,
                        videoUrl,
                        selectedSource,
                        selectedCategory,
                        privacyMode == 2,
                        pinCode
                    )
                },
                icon = Icons.Default.Movie,
                modifier = Modifier
                    .testTag("btn_launch_party")
                    .fillMaxWidth()
            )
        }
    }
}

@Composable
private fun PrivacyOptionRow(
    icon: ImageVector,
    title: String,
    subtitle: String,
    isSelected: Boolean,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(KxaRadius.md))
            .background(if (isSelected) KxaTheme.colors.surfaceVariant else KxaTheme.colors.surface)
            .border(
                1.dp,
                if (isSelected) PurplePrimary else KxaTheme.colors.borderSubtle,
                RoundedCornerShape(KxaRadius.md)
            )
            .clickable { onClick() }
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .background(
                    if (isSelected) PurplePrimary.copy(alpha = 0.2f) else KxaTheme.colors.surfaceElevated,
                    CircleShape
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) PurpleLight else KxaTheme.colors.textMuted,
                modifier = Modifier.size(18.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                fontWeight = FontWeight.Bold,
                color = if (isSelected) KxaTheme.colors.textPrimary else KxaTheme.colors.textSecondary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = KxaTheme.colors.textMuted
            )
        }
    }
}
