package com.example.ui.screens

import androidx.compose.foundation.Image
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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Tv
import androidx.compose.material.icons.filled.Whatshot
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.MockData
import com.example.model.Friend
import com.example.model.User
import com.example.model.VideoItem
import com.example.model.WatchRoom
import com.example.ui.components.KXaChip
import com.example.ui.components.KXaEmptyState
import com.example.ui.components.KXaLiveBadge
import com.example.ui.components.KXaRoomFeaturedCard
import com.example.ui.components.KXaRoomListCard
import com.example.ui.components.UserAvatar
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.KxaRadius
import com.example.ui.theme.KxaSpacing
import com.example.ui.theme.KxaTheme
import com.example.ui.theme.LiveRed
import com.example.ui.theme.OnlineGreen
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary

@Composable
fun HomeScreen(
    currentUser: User,
    rooms: List<WatchRoom>,
    friends: List<Friend>,
    onSelectRoom: (WatchRoom) -> Unit,
    onCreateRoomClick: () -> Unit,
    onJoinRoomClick: () -> Unit,
    onFriendsClick: () -> Unit,
    onChatFriendClick: (Friend) -> Unit
) {
    var selectedCategory by remember { mutableStateOf("All") }
    val categories = listOf("All", "YouTube", "Gaming", "Music", "Live Stream", "Private")

    val filteredRooms = remember(rooms, selectedCategory) {
        if (selectedCategory == "All") rooms
        else rooms.filter {
            it.category.contains(selectedCategory, ignoreCase = true) ||
            it.videoSource.contains(selectedCategory, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(KxaTheme.colors.background)
            .testTag("screen_home"),
        contentPadding = PaddingValues(bottom = 100.dp)
    ) {
        // Top Header
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KxaSpacing.standard, vertical = KxaSpacing.md),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedCornerShape(KxaRadius.sm))
                            .background(KxaTheme.colors.surfaceVariant)
                            .border(1.dp, PurplePrimary, RoundedCornerShape(KxaRadius.sm))
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.img_app_icon),
                            contentDescription = "Logo",
                            modifier = Modifier.size(32.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column {
                        Text(
                            text = "K Xa",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Black,
                                letterSpacing = 0.5.sp
                            ),
                            color = KxaTheme.colors.textPrimary
                        )
                        Text(
                            text = "Welcome back, ${currentUser.name.substringBefore(" ")} 👋",
                            style = MaterialTheme.typography.bodySmall,
                            color = KxaTheme.colors.textSecondary
                        )
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(
                        onClick = {},
                        modifier = Modifier
                            .size(40.dp)
                            .background(KxaTheme.colors.surfaceVariant, CircleShape)
                            .border(1.dp, KxaTheme.colors.borderSubtle, CircleShape)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Notifications,
                            contentDescription = "Notifications",
                            tint = KxaTheme.colors.textPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        // Hero CTA Banner: "Host a Watch Party"
        item {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KxaSpacing.standard, vertical = 4.dp)
                    .clip(RoundedCornerShape(KxaRadius.xl))
                    .background(
                        Brush.linearGradient(
                            colors = if (KxaTheme.colors.isDark) {
                                listOf(
                                    Color(0xFF3B1E6D),
                                    Color(0xFF132D4C),
                                    KxaTheme.colors.surfaceElevated
                                )
                            } else {
                                listOf(
                                    Color(0xFF6366F1),
                                    Color(0xFF3B82F6),
                                    Color(0xFF8B5CF6)
                                )
                            }
                        )
                    )
                    .border(
                        1.5.dp,
                        Brush.linearGradient(listOf(PurplePrimary, CyanAccent)),
                        RoundedCornerShape(KxaRadius.xl)
                    )
                    .clickable { onCreateRoomClick() }
                    .padding(18.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(KxaRadius.xs))
                                    .background(Color.White.copy(alpha = 0.25f))
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "SYNCHRONIZED STREAM",
                                    color = Color.White,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Start a Watch Party",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.ExtraBold,
                                fontSize = 19.sp
                            ),
                            color = Color.White
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "Stream YouTube, Twitch or web videos with friends in real-time.",
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.85f),
                            maxLines = 2
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Box(
                        modifier = Modifier
                            .size(50.dp)
                            .background(Color.White.copy(alpha = 0.2f), CircleShape)
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(30.dp)
                        )
                    }
                }
            }
        }

        // Quick Action Tiles
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = KxaSpacing.standard, vertical = KxaSpacing.md),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                QuickActionChip(
                    icon = Icons.Default.Add,
                    title = "New Room",
                    subtitle = "Create & invite",
                    accentColor = PurplePrimary,
                    modifier = Modifier.weight(1f),
                    onClick = onCreateRoomClick,
                    testTag = "btn_home_create_room"
                )

                QuickActionChip(
                    icon = Icons.Default.Key,
                    title = "Join Code",
                    subtitle = "Enter PIN/ID",
                    accentColor = CyanAccent,
                    modifier = Modifier.weight(1f),
                    onClick = onJoinRoomClick,
                    testTag = "btn_home_join_code"
                )
            }
        }

        // Friends Online Story Row
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = KxaSpacing.standard, vertical = KxaSpacing.xs),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Box(
                            modifier = Modifier
                                .size(8.dp)
                                .background(OnlineGreen, CircleShape)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = "Friends Online",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = KxaTheme.colors.textPrimary
                        )
                    }

                    Text(
                        text = "View All (${friends.count { it.user.isOnline }})",
                        style = MaterialTheme.typography.bodySmall,
                        color = PurpleLight,
                        fontWeight = FontWeight.SemiBold,
                        modifier = Modifier.clickable { onFriendsClick() }
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = KxaSpacing.standard),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(friends) { friend ->
                        FriendStoryItem(
                            friend = friend,
                            onChatClick = { onChatFriendClick(friend) },
                            onJoinRoomClick = {
                                friend.activeRoom?.let { onSelectRoom(it) }
                            }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(18.dp))
        }

        // Featured Parties Carousel
        item {
            Column(modifier = Modifier.fillMaxWidth()) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = KxaSpacing.standard),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Whatshot,
                            contentDescription = null,
                            tint = LiveRed,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = "Featured Watch Parties",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = KxaTheme.colors.textPrimary
                        )
                    }

                    Text(
                        text = "${rooms.size} Active",
                        style = MaterialTheme.typography.bodySmall,
                        color = KxaTheme.colors.textSecondary
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                LazyRow(
                    contentPadding = PaddingValues(horizontal = KxaSpacing.standard),
                    horizontalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    items(rooms) { room ->
                        KXaRoomFeaturedCard(
                            room = room,
                            onClick = { onSelectRoom(room) }
                        )
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }

        // Category Filter Chips
        item {
            LazyRow(
                contentPadding = PaddingValues(horizontal = KxaSpacing.standard),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier.padding(bottom = 12.dp)
            ) {
                items(categories) { category ->
                    KXaChip(
                        text = category,
                        selected = selectedCategory == category,
                        onClick = { selectedCategory = category }
                    )
                }
            }
        }

        // All Rooms List
        if (filteredRooms.isEmpty()) {
            item {
                KXaEmptyState(
                    title = "No Rooms Found",
                    description = "Be the first to launch a watch party in this category!",
                    actionButtonText = "Create Room",
                    onActionClick = onCreateRoomClick
                )
            }
        } else {
            items(filteredRooms) { room ->
                Box(modifier = Modifier.padding(horizontal = KxaSpacing.standard, vertical = 5.dp)) {
                    KXaRoomListCard(
                        room = room,
                        onClick = { onSelectRoom(room) }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(16.dp))
        }

        // Trending Video Catalog
        item {
            Column(modifier = Modifier.fillMaxWidth().padding(horizontal = KxaSpacing.standard)) {
                Text(
                    text = "Trending Video Feeds",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = KxaTheme.colors.textPrimary
                )
                Spacer(modifier = Modifier.height(10.dp))

                MockData.sampleVideos.forEach { video ->
                    TrendingVideoRow(
                        video = video,
                        onWatchClick = {
                            val targetRoom = rooms.firstOrNull() ?: MockData.activeRooms[0]
                            onSelectRoom(targetRoom)
                        }
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun QuickActionChip(
    icon: ImageVector,
    title: String,
    subtitle: String,
    accentColor: Color,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
    testTag: String
) {
    Box(
        modifier = modifier
            .testTag(testTag)
            .clip(RoundedCornerShape(KxaRadius.lg))
            .background(KxaTheme.colors.surface)
            .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.lg))
            .clickable { onClick() }
            .padding(14.dp)
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(40.dp)
                    .background(accentColor.copy(alpha = 0.15f), RoundedCornerShape(KxaRadius.sm)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = accentColor,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(fontSize = 14.sp),
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
    }
}

@Composable
private fun FriendStoryItem(
    friend: Friend,
    onChatClick: () -> Unit,
    onJoinRoomClick: () -> Unit
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .width(68.dp)
            .clickable {
                if (friend.activeRoom != null) {
                    onJoinRoomClick()
                } else {
                    onChatClick()
                }
            }
    ) {
        Box(contentAlignment = Alignment.Center) {
            UserAvatar(
                user = friend.user,
                size = 52.dp,
                showSpeakingHalo = friend.user.isSpeaking
            )

            if (friend.activeRoom != null) {
                Box(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .clip(RoundedCornerShape(4.dp))
                        .background(LiveRed)
                        .padding(horizontal = 4.dp, vertical = 1.dp)
                ) {
                    Text(
                        text = "LIVE",
                        color = Color.White,
                        fontSize = 8.sp,
                        fontWeight = FontWeight.Black
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = friend.user.name.substringBefore(" "),
            style = MaterialTheme.typography.bodySmall,
            fontWeight = FontWeight.Medium,
            color = KxaTheme.colors.textPrimary,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis
        )
    }
}

@Composable
private fun TrendingVideoRow(
    video: VideoItem,
    onWatchClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(KxaRadius.md))
            .background(KxaTheme.colors.surface)
            .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.md))
            .clickable { onWatchClick() }
            .padding(10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(width = 72.dp, height = 48.dp)
                .clip(RoundedCornerShape(8.dp))
                .background(KxaTheme.colors.surfaceElevated),
            contentAlignment = Alignment.Center
        ) {
            Image(
                painter = painterResource(id = R.drawable.img_watch_preview),
                contentDescription = null,
                contentScale = ContentScale.Crop,
                modifier = Modifier.fillMaxSize()
            )
            Box(
                modifier = Modifier
                    .size(22.dp)
                    .background(Color.Black.copy(alpha = 0.6f), CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.PlayArrow,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(14.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column(modifier = Modifier.weight(1f)) {
            Text(
                text = video.title,
                style = MaterialTheme.typography.titleMedium.copy(fontSize = 13.sp),
                color = KxaTheme.colors.textPrimary,
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = "${video.channel} • ${video.views} views",
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = KxaTheme.colors.textMuted
            )
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(8.dp))
                .background(PurplePrimary.copy(alpha = 0.15f))
                .padding(horizontal = 10.dp, vertical = 5.dp)
        ) {
            Text(
                text = "Stream",
                color = PurpleLight,
                fontSize = 11.sp,
                fontWeight = FontWeight.Bold
            )
        }
    }
}
