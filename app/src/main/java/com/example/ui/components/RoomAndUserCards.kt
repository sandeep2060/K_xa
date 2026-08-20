package com.example.ui.components

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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ChatMessage
import com.example.model.Friend
import com.example.model.User
import com.example.model.WatchRoom
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.KxaRadius
import com.example.ui.theme.KxaSpacing
import com.example.ui.theme.KxaTheme
import com.example.ui.theme.LiveRed
import com.example.ui.theme.OnlineGreen
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary

// =============================================================================
// ROOM CARDS
// =============================================================================

@Composable
fun KXaRoomFeaturedCard(
    room: WatchRoom,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .testTag("room_card_${room.id}")
            .width(280.dp)
            .height(180.dp)
            .shadow(4.dp, RoundedCornerShape(KxaRadius.lg))
            .clip(RoundedCornerShape(KxaRadius.lg))
            .background(KxaTheme.colors.surface)
            .border(
                1.dp,
                Brush.linearGradient(
                    listOf(
                        PurplePrimary.copy(alpha = 0.5f),
                        CyanAccent.copy(alpha = 0.25f)
                    )
                ),
                RoundedCornerShape(KxaRadius.lg)
            )
            .clickable(onClick = onClick)
    ) {
        // Thumbnail & Cinematic Background Gradient
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(
                    Brush.verticalGradient(
                        colors = listOf(
                            Color(0xFF2A1B4E).copy(alpha = if (KxaTheme.colors.isDark) 0.8f else 0.4f),
                            KxaTheme.colors.surfaceElevated.copy(alpha = 0.95f)
                        )
                    )
                )
        )

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(KxaSpacing.standard),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Row: Badges
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    KXaLiveBadge()
                    if (room.isPrivate) {
                        Spacer(modifier = Modifier.width(6.dp))
                        Box(
                            modifier = Modifier
                                .clip(CircleShape)
                                .background(KxaTheme.colors.surfaceVariant)
                                .padding(4.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Lock,
                                contentDescription = "Private Room",
                                tint = PurpleLight,
                                modifier = Modifier.size(12.dp)
                            )
                        }
                    }
                }

                // Viewer Count Pill
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(KxaRadius.pill))
                        .background(Color.Black.copy(alpha = 0.45f))
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Visibility,
                        contentDescription = null,
                        tint = Color.White,
                        modifier = Modifier.size(12.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${room.participants.size}",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = Color.White
                    )
                }
            }

            // Bottom Section: Title, Video Provider, Host info
            Column {
                Text(
                    text = room.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp
                    ),
                    color = KxaTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Spacer(modifier = Modifier.height(2.dp))

                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Tv,
                        contentDescription = null,
                        tint = CyanAccent,
                        modifier = Modifier.size(13.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = room.videoTitle,
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = KxaTheme.colors.textSecondary,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Host row
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    UserAvatar(
                        user = room.host,
                        size = 22.dp,
                        showOnlineDot = false
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(
                        text = "Hosted by ${room.host.name}",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 11.sp),
                        color = KxaTheme.colors.textMuted
                    )

                    Spacer(modifier = Modifier.weight(1f))

                    // Code Tag
                    Text(
                        text = room.code,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        ),
                        color = PurpleLight
                    )
                }
            }
        }
    }
}

@Composable
fun KXaRoomCard(
    room: WatchRoom,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) = KXaRoomFeaturedCard(room, onClick, modifier)

@Composable
fun KXaUserCard(
    user: User,
    onClick: (() -> Unit)? = null,
    trailingAction: @Composable (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(KxaRadius.lg))
            .background(KxaTheme.colors.surface)
            .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.lg))
            .then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(user = user, size = 44.dp)
        Spacer(modifier = Modifier.width(12.dp))
        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = user.name,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold, fontSize = 14.sp),
                color = KxaTheme.colors.textPrimary
            )
            Text(
                text = "@${user.username}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = PurpleLight
            )
        }
        if (trailingAction != null) {
            trailingAction()
        }
    }
}

@Composable
fun KXaRoomListCard(
    room: WatchRoom,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .testTag("room_item_${room.id}")
            .fillMaxWidth()
            .clip(RoundedCornerShape(KxaRadius.lg))
            .background(KxaTheme.colors.surface)
            .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.lg))
            .clickable(onClick = onClick)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Thumbnail box with play icon
        Box(
            modifier = Modifier
                .size(54.dp)
                .clip(RoundedCornerShape(KxaRadius.md))
                .background(
                    Brush.linearGradient(
                        listOf(PurplePrimary.copy(alpha = 0.8f), CyanAccent.copy(alpha = 0.8f))
                    )
                ),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(28.dp)
            )
        }

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = room.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 14.sp
                    ),
                    color = KxaTheme.colors.textPrimary,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                if (room.isPlaying) {
                    KXaBadge(
                        text = "${room.participants.size} watching",
                        backgroundColor = PurplePrimary.copy(alpha = 0.15f),
                        textColor = PurpleLight,
                        leadingDotColor = LiveRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = "${room.videoSource} • ${room.videoTitle}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                color = KxaTheme.colors.textSecondary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Host: @${room.host.username} • Code: ${room.code}",
                style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                color = KxaTheme.colors.textMuted
            )
        }
    }
}

// =============================================================================
// FRIEND CARDS
// =============================================================================

@Composable
fun KXaFriendCard(
    friend: Friend,
    onOpenChat: () -> Unit,
    onJoinRoom: ((WatchRoom) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .testTag("friend_card_${friend.user.id}")
            .fillMaxWidth()
            .clip(RoundedCornerShape(KxaRadius.lg))
            .background(KxaTheme.colors.surface)
            .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.lg))
            .clickable(onClick = onOpenChat)
            .padding(14.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        UserAvatar(
            user = friend.user,
            size = 46.dp,
            showOnlineDot = true
        )

        Spacer(modifier = Modifier.width(14.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = friend.user.name,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    fontSize = 14.sp
                ),
                color = KxaTheme.colors.textPrimary
            )

            Text(
                text = "@${friend.user.username}",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = PurpleLight
            )

            if (friend.activeRoom != null) {
                Spacer(modifier = Modifier.height(4.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier
                        .clip(RoundedCornerShape(KxaRadius.pill))
                        .background(PurplePrimary.copy(alpha = 0.12f))
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(5.dp)
                            .background(LiveRed, CircleShape)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Watching: ${friend.activeRoom.title}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            fontWeight = FontWeight.SemiBold
                        ),
                        color = PurpleLight,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            } else {
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = friend.user.statusMessage,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = KxaTheme.colors.textMuted,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Spacer(modifier = Modifier.width(8.dp))

        // Action Buttons
        if (friend.activeRoom != null && onJoinRoom != null) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(KxaRadius.md))
                    .background(KxaTheme.colors.brandGradient)
                    .clickable { onJoinRoom(friend.activeRoom) }
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = "Join",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                )
            }
        } else {
            KXaIconButton(
                icon = Icons.Default.ChatBubble,
                contentDescription = "Message",
                onClick = onOpenChat,
                size = 38.dp,
                tint = PurpleLight
            )
        }
    }
}

// =============================================================================
// MESSAGE BUBBLES
// =============================================================================

@Composable
fun KXaMessageBubble(
    message: ChatMessage,
    isOutgoing: Boolean,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        horizontalAlignment = if (isOutgoing) Alignment.End else Alignment.Start
    ) {
        if (!isOutgoing) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.padding(start = 4.dp, bottom = 2.dp)
            ) {
                Text(
                    text = message.sender.name,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    color = PurpleLight
                )
                if (message.isHost) {
                    Spacer(modifier = Modifier.width(4.dp))
                    KXaBadge(text = "HOST", backgroundColor = LiveRed.copy(alpha = 0.2f), textColor = LiveRed)
                }
            }
        }

        Box(
            modifier = Modifier
                .clip(
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isOutgoing) 16.dp else 4.dp,
                        bottomEnd = if (isOutgoing) 4.dp else 16.dp
                    )
                )
                .background(
                    if (isOutgoing) {
                        Brush.linearGradient(listOf(PurplePrimary, Color(0xFF7C3AED)))
                    } else {
                        Brush.linearGradient(
                            listOf(
                                KxaTheme.colors.surfaceElevated,
                                KxaTheme.colors.surfaceVariant
                            )
                        )
                    }
                )
                .border(
                    1.dp,
                    if (isOutgoing) Color.Transparent else KxaTheme.colors.borderSubtle,
                    RoundedCornerShape(
                        topStart = 16.dp,
                        topEnd = 16.dp,
                        bottomStart = if (isOutgoing) 16.dp else 4.dp,
                        bottomEnd = if (isOutgoing) 4.dp else 16.dp
                    )
                )
                .padding(horizontal = 14.dp, vertical = 9.dp)
        ) {
            Column {
                Text(
                    text = message.text,
                    style = MaterialTheme.typography.bodyMedium.copy(fontSize = 14.sp),
                    color = if (isOutgoing) Color.White else KxaTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = message.timestamp,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 10.sp),
                    color = if (isOutgoing) Color.White.copy(alpha = 0.7f) else KxaTheme.colors.textMuted,
                    modifier = Modifier.align(Alignment.End)
                )
            }
        }
    }
}
