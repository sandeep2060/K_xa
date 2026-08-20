package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Videocam
import androidx.compose.material.icons.filled.VideocamOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.FlyingReaction
import com.example.model.User
import com.example.model.WatchRoom
import com.example.ui.components.FlyingReactionsOverlay
import com.example.ui.components.KXaBadge
import com.example.ui.components.KXaLiveBadge
import com.example.ui.components.KXaMessageBubble
import com.example.ui.components.UserAvatar
import com.example.ui.components.VideoPlayerMock
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.KxaRadius
import com.example.ui.theme.KxaSpacing
import com.example.ui.theme.KxaTheme
import com.example.ui.theme.LiveRed
import com.example.ui.theme.OnlineGreen
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary

@Composable
fun WatchRoomScreen(
    room: WatchRoom,
    currentUser: User,
    isPlaying: Boolean,
    videoPositionSeconds: Int,
    isSynced: Boolean,
    isMicMuted: Boolean,
    isCameraOn: Boolean,
    chatMessages: List<ChatMessage>,
    flyingReactions: List<FlyingReaction>,
    onTogglePlayPause: () -> Unit,
    onSeek: (Int) -> Unit,
    onSyncClick: () -> Unit,
    onToggleMic: () -> Unit,
    onToggleCamera: () -> Unit,
    onSendMessage: (String) -> Unit,
    onSendReaction: (String) -> Unit,
    onLeaveRoom: () -> Unit
) {
    var messageInput by remember { mutableStateOf("") }
    val chatListState = rememberLazyListState()

    // Auto-scroll chat to latest message
    LaunchedEffect(chatMessages.size) {
        if (chatMessages.isNotEmpty()) {
            chatListState.animateScrollToItem(chatMessages.size - 1)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(KxaTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .imePadding()
            .testTag("screen_watch_room")
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Room Top Header
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = onLeaveRoom,
                        modifier = Modifier
                            .testTag("btn_leave_room")
                            .size(38.dp)
                            .background(KxaTheme.colors.surfaceVariant, CircleShape)
                            .border(1.dp, KxaTheme.colors.borderSubtle, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Leave Room",
                            tint = KxaTheme.colors.textPrimary,
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(10.dp))

                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = room.title,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontSize = 15.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                color = KxaTheme.colors.textPrimary,
                                maxLines = 1,
                                overflow = TextOverflow.Ellipsis
                            )
                        }

                        Row(verticalAlignment = Alignment.CenterVertically) {
                            KXaBadge(
                                text = room.code,
                                backgroundColor = PurplePrimary.copy(alpha = 0.2f),
                                textColor = PurpleLight
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = "Host: ${room.host.name.substringBefore(" ")}",
                                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                                color = KxaTheme.colors.textSecondary
                            )
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(38.dp)
                            .background(KxaTheme.colors.surfaceVariant, CircleShape)
                            .border(1.dp, KxaTheme.colors.borderSubtle, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Share,
                            contentDescription = "Share Code",
                            tint = CyanAccent,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }

            // Interactive Video Player Box
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp)
            ) {
                VideoPlayerMock(
                    room = room,
                    isPlaying = isPlaying,
                    currentPositionSeconds = videoPositionSeconds,
                    isSynced = isSynced,
                    onTogglePlayPause = onTogglePlayPause,
                    onSeek = onSeek,
                    onSyncClick = onSyncClick
                )
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Connected Members Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "MEMBERS (${room.participants.size})",
                    style = MaterialTheme.typography.labelMedium.copy(fontSize = 11.sp),
                    color = PurpleLight,
                    fontWeight = FontWeight.Bold
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(6.dp)
                            .background(OnlineGreen, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Voice Connected",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                        color = OnlineGreen
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                items(room.participants) { participant ->
                    ParticipantAvatarPill(
                        user = participant,
                        isHost = participant.id == room.host.id
                    )
                }

                item {
                    // Invite Pill
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(KxaRadius.pill))
                            .background(KxaTheme.colors.surfaceVariant)
                            .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.pill))
                            .clickable { }
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.PersonAdd,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(5.dp))
                            Text(
                                text = "Invite",
                                color = KxaTheme.colors.textPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Audio & Video Call Control Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .clip(RoundedCornerShape(KxaRadius.md))
                    .background(KxaTheme.colors.surface)
                    .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.md))
                    .padding(horizontal = 12.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceAround,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic Toggle
                RoomControlButton(
                    icon = if (isMicMuted) Icons.Default.MicOff else Icons.Default.Mic,
                    label = if (isMicMuted) "Muted" else "Mic On",
                    isActive = !isMicMuted,
                    activeColor = OnlineGreen,
                    inactiveColor = LiveRed,
                    onClick = onToggleMic,
                    testTag = "btn_toggle_mic"
                )

                // Camera Toggle
                RoomControlButton(
                    icon = if (isCameraOn) Icons.Default.Videocam else Icons.Default.VideocamOff,
                    label = if (isCameraOn) "Cam On" else "Cam Off",
                    isActive = isCameraOn,
                    activeColor = CyanAccent,
                    inactiveColor = KxaTheme.colors.textMuted,
                    onClick = onToggleCamera,
                    testTag = "btn_toggle_cam"
                )

                // Reaction Picker
                Row(
                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    listOf("🔥", "❤️", "😂", "🍿").forEach { emoji ->
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(KxaTheme.colors.surfaceVariant)
                                .clickable { onSendReaction(emoji) }
                                .padding(2.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = emoji, fontSize = 16.sp)
                        }
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Live Synchronized Chat Box
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .clip(RoundedCornerShape(KxaRadius.lg))
                    .background(KxaTheme.colors.surfaceElevated.copy(alpha = 0.5f))
                    .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.lg))
            ) {
                LazyColumn(
                    state = chatListState,
                    contentPadding = PaddingValues(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp),
                    modifier = Modifier.fillMaxSize()
                ) {
                    items(chatMessages) { msg ->
                        KXaMessageBubble(
                            message = msg,
                            isOutgoing = msg.sender.id == currentUser.id
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Quick Preset Reply Chips
            LazyRow(
                contentPadding = PaddingValues(horizontal = 14.dp),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                val presets = listOf("hype!! 🔥", "omg this part 🍿", "audio is so crisp 🎧", "LMAO 😂", "wait rewind 10s")
                items(presets) { preset ->
                    Box(
                        modifier = Modifier
                            .clip(RoundedCornerShape(KxaRadius.pill))
                            .background(KxaTheme.colors.surfaceVariant)
                            .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.pill))
                            .clickable { onSendMessage(preset) }
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = preset,
                            fontSize = 11.sp,
                            color = PurpleLight,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Chat Input Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 6.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                OutlinedTextField(
                    value = messageInput,
                    onValueChange = { messageInput = it },
                    placeholder = { Text("Chat in real-time...", color = KxaTheme.colors.textMuted, fontSize = 13.sp) },
                    singleLine = true,
                    shape = RoundedCornerShape(20.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = PurplePrimary,
                        unfocusedBorderColor = KxaTheme.colors.border,
                        focusedContainerColor = KxaTheme.colors.surfaceVariant,
                        unfocusedContainerColor = KxaTheme.colors.surface,
                        focusedTextColor = KxaTheme.colors.textPrimary,
                        unfocusedTextColor = KxaTheme.colors.textPrimary
                    ),
                    modifier = Modifier
                        .testTag("input_room_chat_message")
                        .weight(1f)
                )

                Spacer(modifier = Modifier.width(8.dp))

                IconButton(
                    onClick = {
                        if (messageInput.isNotBlank()) {
                            onSendMessage(messageInput)
                            messageInput = ""
                        }
                    },
                    modifier = Modifier
                        .testTag("btn_send_room_message")
                        .size(46.dp)
                        .background(
                            KxaTheme.colors.brandGradient,
                            CircleShape
                        )
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }

        // Flying Emoji Particles Layer
        FlyingReactionsOverlay(
            reactions = flyingReactions,
            modifier = Modifier.fillMaxSize()
        )
    }
}

@Composable
private fun ParticipantAvatarPill(
    user: User,
    isHost: Boolean
) {
    Row(
        modifier = Modifier
            .clip(RoundedCornerShape(KxaRadius.pill))
            .background(KxaTheme.colors.surface)
            .border(1.dp, if (isHost) PurplePrimary else KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.pill))
            .padding(horizontal = 8.dp, vertical = 4.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(
            user = user,
            size = 24.dp,
            showOnlineDot = false,
            showSpeakingHalo = user.isSpeaking
        )
        Spacer(modifier = Modifier.width(6.dp))
        Text(
            text = user.name.substringBefore(" "),
            style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
            color = KxaTheme.colors.textPrimary,
            fontWeight = FontWeight.SemiBold
        )
        if (isHost) {
            Spacer(modifier = Modifier.width(4.dp))
            Text(text = "👑", fontSize = 10.sp)
        }
    }
}

@Composable
private fun RoomControlButton(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    activeColor: Color,
    inactiveColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Row(
        modifier = Modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(KxaRadius.sm))
            .background(KxaTheme.colors.surfaceVariant)
            .clickable { onClick() }
            .padding(horizontal = 10.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) activeColor else inactiveColor,
            modifier = Modifier.size(16.dp)
        )
        Spacer(modifier = Modifier.width(4.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = FontWeight.SemiBold,
            color = if (isActive) KxaTheme.colors.textPrimary else KxaTheme.colors.textMuted
        )
    }
}
