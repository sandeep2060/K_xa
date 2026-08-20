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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.QrCodeScanner
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.WatchRoom
import com.example.ui.components.AppHeader
import com.example.ui.components.KXaButton
import com.example.ui.components.KXaEmptyState
import com.example.ui.components.KXaGlassCard
import com.example.ui.components.KXaRoomListCard
import com.example.ui.components.KXaTextField
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.KxaRadius
import com.example.ui.theme.KxaSpacing
import com.example.ui.theme.KxaTheme
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary

@Composable
fun JoinRoomScreen(
    rooms: List<WatchRoom>,
    onJoinByCode: (String) -> Unit,
    onSelectRoom: (WatchRoom) -> Unit,
    onCreateRoomClick: () -> Unit,
    onBack: () -> Unit
) {
    var roomCodeInput by remember { mutableStateOf("KX-8492") }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KxaTheme.colors.background)
            .testTag("screen_join_room")
    ) {
        AppHeader(
            title = "Rooms Lobby",
            subtitle = "Join with a code or explore rooms",
            showBack = false,
            trailingContent = {
                IconButton(
                    onClick = onCreateRoomClick,
                    modifier = Modifier
                        .testTag("btn_lobby_create_room")
                        .size(40.dp)
                        .background(KxaTheme.colors.brandGradient, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.Default.Add,
                        contentDescription = "Create Room",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        )

        LazyColumn(
            modifier = Modifier.fillMaxWidth(),
            contentPadding = PaddingValues(start = KxaSpacing.standard, end = KxaSpacing.standard, top = 4.dp, bottom = 100.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Join with Code Card
            item {
                KXaGlassCard(
                    modifier = Modifier.fillMaxWidth(),
                    borderColor = PurplePrimary.copy(alpha = 0.5f)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "ENTER ROOM CODE",
                            style = MaterialTheme.typography.labelMedium,
                            color = PurpleLight,
                            fontWeight = FontWeight.Bold
                        )

                        IconButton(
                            onClick = {},
                            modifier = Modifier.size(32.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.QrCodeScanner,
                                contentDescription = "Scan QR",
                                tint = CyanAccent,
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    KXaTextField(
                        value = roomCodeInput,
                        onValueChange = { roomCodeInput = it.uppercase() },
                        placeholder = "e.g. KX-8492",
                        leadingIcon = Icons.Default.Key,
                        modifier = Modifier
                            .testTag("input_join_room_code")
                            .fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    KXaButton(
                        text = "Join Party Now ➔",
                        onClick = { onJoinByCode(roomCodeInput) },
                        modifier = Modifier
                            .testTag("btn_submit_join_code")
                            .fillMaxWidth()
                    )
                }
            }

            // Public Rooms Title
            item {
                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Active Public Rooms",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = KxaTheme.colors.textPrimary
                    )
                    Text(
                        text = "${rooms.size} Available",
                        style = MaterialTheme.typography.bodySmall,
                        color = KxaTheme.colors.textSecondary
                    )
                }
            }

            if (rooms.isEmpty()) {
                item {
                    KXaEmptyState(
                        title = "No Public Rooms",
                        description = "Create a room and invite your friends to start watching!",
                        actionButtonText = "Create Room",
                        onActionClick = onCreateRoomClick
                    )
                }
            } else {
                items(rooms) { room ->
                    KXaRoomListCard(
                        room = room,
                        onClick = { onSelectRoom(room) }
                    )
                }
            }
        }
    }
}
