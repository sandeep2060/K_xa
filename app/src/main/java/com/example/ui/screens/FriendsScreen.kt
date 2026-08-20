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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.Friend
import com.example.model.WatchRoom
import com.example.ui.components.AppHeader
import com.example.ui.components.KXaChip
import com.example.ui.components.KXaEmptyState
import com.example.ui.components.KXaFriendCard
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.KxaRadius
import com.example.ui.theme.KxaSpacing
import com.example.ui.theme.KxaTheme
import com.example.ui.theme.LiveRed
import com.example.ui.theme.OnlineGreen
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary

@Composable
fun FriendsScreen(
    friends: List<Friend>,
    onOpenChat: (Friend) -> Unit,
    onJoinRoom: (WatchRoom) -> Unit,
    onBack: () -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var selectedTab by remember { mutableIntStateOf(0) } // 0: All, 1: Watching Now, 2: Online, 3: Requests

    val filteredFriends = remember(friends, searchQuery, selectedTab) {
        friends.filter { friend ->
            val matchesQuery = friend.user.name.contains(searchQuery, ignoreCase = true) ||
                    friend.user.username.contains(searchQuery, ignoreCase = true)
            val matchesTab = when (selectedTab) {
                1 -> friend.activeRoom != null
                2 -> friend.user.isOnline
                3 -> false
                else -> true
            }
            matchesQuery && matchesTab
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KxaTheme.colors.background)
            .testTag("screen_friends")
    ) {
        AppHeader(
            title = "Friends & Social",
            subtitle = "${friends.count { it.user.isOnline }} friends online now",
            showBack = false,
            trailingContent = {
                IconButton(
                    onClick = {},
                    modifier = Modifier
                        .size(40.dp)
                        .background(KxaTheme.colors.surfaceVariant, CircleShape)
                        .border(1.dp, KxaTheme.colors.borderSubtle, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.PersonAdd,
                        contentDescription = "Add Friend",
                        tint = CyanAccent,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        )

        // Search Input
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            placeholder = { Text("Search friends by name or @handle...", color = KxaTheme.colors.textMuted) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = null,
                    tint = KxaTheme.colors.textMuted
                )
            },
            singleLine = true,
            shape = RoundedCornerShape(KxaRadius.md),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurplePrimary,
                unfocusedBorderColor = KxaTheme.colors.border,
                focusedContainerColor = KxaTheme.colors.surfaceVariant,
                unfocusedContainerColor = KxaTheme.colors.surface,
                focusedTextColor = KxaTheme.colors.textPrimary,
                unfocusedTextColor = KxaTheme.colors.textPrimary
            ),
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KxaSpacing.standard, vertical = KxaSpacing.xs)
        )

        // Filter Tabs
        LazyRow(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = KxaSpacing.standard, vertical = KxaSpacing.sm),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            item {
                KXaChip(
                    text = "All Friends (${friends.size})",
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0 }
                )
            }
            item {
                KXaChip(
                    text = "Watching Now (${friends.count { it.activeRoom != null }})",
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1 }
                )
            }
            item {
                KXaChip(
                    text = "Online (${friends.count { it.user.isOnline }})",
                    selected = selectedTab == 2,
                    onClick = { selectedTab = 2 }
                )
            }
            item {
                KXaChip(
                    text = "Pending (2)",
                    selected = selectedTab == 3,
                    onClick = { selectedTab = 3 }
                )
            }
        }

        // Friends List or Request View
        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = KxaSpacing.standard, end = KxaSpacing.standard, top = 6.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (selectedTab == 3) {
                item {
                    PendingFriendRequestItem(
                        name = "Lucas Thorne",
                        username = "lucast",
                        mutuals = 6,
                        avatarInitial = "L",
                        avatarColor = 0xFF7C3AED
                    )
                }
                item {
                    PendingFriendRequestItem(
                        name = "Elena Rostova",
                        username = "elenar",
                        mutuals = 14,
                        avatarInitial = "E",
                        avatarColor = 0xFF06B6D4
                    )
                }
            } else if (filteredFriends.isEmpty()) {
                item {
                    KXaEmptyState(
                        title = "No Friends Found",
                        description = "Try searching for a different handle or invite friends to join K Xa."
                    )
                }
            } else {
                items(filteredFriends) { friend ->
                    KXaFriendCard(
                        friend = friend,
                        onOpenChat = { onOpenChat(friend) },
                        onJoinRoom = onJoinRoom
                    )
                }
            }
        }
    }
}

@Composable
private fun PendingFriendRequestItem(
    name: String,
    username: String,
    mutuals: Int,
    avatarInitial: String,
    avatarColor: Long
) {
    var isHandled by remember { mutableStateOf(false) }

    if (!isHandled) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(KxaRadius.lg))
                .background(KxaTheme.colors.surface)
                .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.lg))
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.weight(1f)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .background(Color(avatarColor), CircleShape)
                        .border(1.5.dp, KxaTheme.colors.border, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = avatarInitial,
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                Spacer(modifier = Modifier.width(12.dp))

                Column {
                    Text(
                        text = name,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = KxaTheme.colors.textPrimary
                    )
                    Text(
                        text = "@$username • $mutuals mutuals",
                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                        color = KxaTheme.colors.textMuted
                    )
                }
            }

            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(KxaRadius.md))
                        .background(KxaTheme.colors.brandGradient)
                        .clickable { isHandled = true }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "Accept",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedCornerShape(KxaRadius.md))
                        .background(KxaTheme.colors.surfaceVariant)
                        .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.md))
                        .clickable { isHandled = true }
                        .padding(horizontal = 10.dp, vertical = 7.dp)
                ) {
                    Text(
                        text = "Decline",
                        color = KxaTheme.colors.textSecondary,
                        fontSize = 12.sp
                    )
                }
            }
        }
    }
}
