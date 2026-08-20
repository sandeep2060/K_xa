package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.MockData
import com.example.model.AuthState
import com.example.ui.components.AppBottomNav
import com.example.ui.theme.KxaTheme
import com.example.ui.theme.PurplePrimary
import com.example.viewmodel.AppScreen
import com.example.viewmodel.AppViewModel

@Composable
fun MainScaffold(
    viewModel: AppViewModel
) {
    val currentScreen by viewModel.currentScreen.collectAsState()
    val authState by viewModel.authState.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val rooms by viewModel.rooms.collectAsState()
    val friends by viewModel.friends.collectAsState()
    val activeRoom by viewModel.activeRoom.collectAsState()
    val roomChat by viewModel.roomChat.collectAsState()
    val selectedFriend by viewModel.selectedFriendForChat.collectAsState()
    val privateChat by viewModel.privateChat.collectAsState()
    val isPlaying by viewModel.isPlayingVideo.collectAsState()
    val videoPosition by viewModel.videoPositionSeconds.collectAsState()
    val isMicMuted by viewModel.isMicMuted.collectAsState()
    val isCameraOn by viewModel.isCameraOn.collectAsState()
    val isSynced by viewModel.isSyncedWithHost.collectAsState()
    val flyingReactions by viewModel.flyingReactions.collectAsState()
    val themeMode by viewModel.themeMode.collectAsState()
    val allUsers by viewModel.allUsers.collectAsState()
    val reports by viewModel.reports.collectAsState()
    val auditLogs by viewModel.auditLogs.collectAsState()
    val adminMessage by viewModel.adminMessage.collectAsState()
    val isSignUpMode by viewModel.isSignUpMode.collectAsState()

    // Handle system back navigation
    BackHandler(
        enabled = currentScreen != AppScreen.SPLASH && currentScreen != AppScreen.ONBOARDING && currentScreen != AppScreen.HOME
    ) {
        when (currentScreen) {
            AppScreen.WATCH_ROOM, AppScreen.CREATE_ROOM -> viewModel.navigateTo(AppScreen.HOME)
            AppScreen.PRIVATE_CHAT -> viewModel.navigateTo(AppScreen.FRIENDS)
            AppScreen.LOGIN -> viewModel.navigateTo(AppScreen.ONBOARDING)
            AppScreen.ADMIN_PANEL -> viewModel.navigateTo(AppScreen.PROFILE)
            AppScreen.FRIENDS, AppScreen.JOIN_ROOM, AppScreen.PROFILE -> viewModel.navigateTo(AppScreen.HOME)
            else -> viewModel.navigateTo(AppScreen.HOME)
        }
    }

    val showBottomNav = currentScreen in listOf(
        AppScreen.HOME,
        AppScreen.FRIENDS,
        AppScreen.JOIN_ROOM,
        AppScreen.PRIVATE_CHAT,
        AppScreen.PROFILE
    )

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        containerColor = KxaTheme.colors.background,
        bottomBar = {
            if (showBottomNav) {
                AppBottomNav(
                    currentScreen = currentScreen,
                    onNavigate = { target ->
                        viewModel.navigateTo(target)
                    }
                )
            }
        }
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(KxaTheme.colors.background)
                .padding(bottom = if (showBottomNav) innerPadding.calculateBottomPadding() else 0.dp)
        ) {
            AnimatedContent(
                targetState = currentScreen,
                transitionSpec = {
                    if (targetState == AppScreen.SPLASH || initialState == AppScreen.SPLASH) {
                        fadeIn() togetherWith fadeOut()
                    } else {
                        (slideInHorizontally { width -> width / 4 } + fadeIn()) togetherWith
                                (slideOutHorizontally { width -> -width / 4 } + fadeOut())
                    }
                },
                label = "screen_transition"
            ) { targetScreen ->
                when (targetScreen) {
                    AppScreen.SPLASH -> {
                        SplashScreen(
                            onFinished = { viewModel.completeSplash() }
                        )
                    }

                    AppScreen.ONBOARDING -> {
                        OnboardingScreen(
                            onGetStarted = { viewModel.navigateTo(AppScreen.LOGIN, isSignUp = true) },
                            onSignIn = { viewModel.navigateTo(AppScreen.LOGIN, isSignUp = false) }
                        )
                    }

                    AppScreen.LOGIN -> {
                        LoginScreen(
                            onSignIn = { email, password ->
                                viewModel.signIn(email, password)
                            },
                            onSignUp = { email, password, username, fullName, dob, cal, age ->
                                viewModel.signUp(email, password, username, fullName, dob, cal, age)
                            },
                            onBackToOnboarding = { viewModel.navigateTo(AppScreen.ONBOARDING) },
                            initialIsSignUp = isSignUpMode
                        )
                    }

                    AppScreen.POST_LOGIN_SETUP -> {
                        PostLoginOnboardingScreen(
                            currentUser = currentUser,
                            suggestedFriends = friends,
                            onUploadAvatar = { bytes, mime ->
                                viewModel.uploadAvatar(bytes, mime)
                            },
                            onUpdateStatus = { status ->
                                viewModel.updateProfileStatus(status)
                            },
                            onAddFriend = { user ->
                                viewModel.addSuggestedFriend(user)
                            },
                            onComplete = {
                                viewModel.completePostLoginOnboarding()
                            }
                        )
                    }

                    AppScreen.HOME -> {
                        HomeScreen(
                            currentUser = currentUser,
                            rooms = rooms,
                            friends = friends,
                            onSelectRoom = { room -> viewModel.openWatchRoom(room) },
                            onCreateRoomClick = { viewModel.navigateTo(AppScreen.CREATE_ROOM) },
                            onJoinRoomClick = { viewModel.navigateTo(AppScreen.JOIN_ROOM) },
                            onFriendsClick = { viewModel.navigateTo(AppScreen.FRIENDS) },
                            onChatFriendClick = { friend -> viewModel.openPrivateChat(friend) }
                        )
                    }

                    AppScreen.FRIENDS -> {
                        FriendsScreen(
                            friends = friends,
                            onOpenChat = { friend -> viewModel.openPrivateChat(friend) },
                            onJoinRoom = { room -> viewModel.openWatchRoom(room) },
                            onBack = { viewModel.navigateTo(AppScreen.HOME) }
                        )
                    }

                    AppScreen.CREATE_ROOM -> {
                        CreateRoomScreen(
                            onCreateRoom = { title, videoUrl, source, category, isPrivate, pin ->
                                viewModel.createNewRoom(title, videoUrl, source, category, isPrivate, pin)
                            },
                            onBack = { viewModel.navigateTo(AppScreen.HOME) }
                        )
                    }

                    AppScreen.JOIN_ROOM -> {
                        JoinRoomScreen(
                            rooms = rooms,
                            onJoinByCode = { code -> viewModel.joinRoomByCode(code) },
                            onSelectRoom = { room -> viewModel.openWatchRoom(room) },
                            onCreateRoomClick = { viewModel.navigateTo(AppScreen.CREATE_ROOM) },
                            onBack = { viewModel.navigateTo(AppScreen.HOME) }
                        )
                    }

                    AppScreen.WATCH_ROOM -> {
                        val room = activeRoom ?: (rooms.firstOrNull() ?: MockData.activeRooms[0])
                        WatchRoomScreen(
                            room = room,
                            currentUser = currentUser,
                            isPlaying = isPlaying,
                            videoPositionSeconds = videoPosition,
                            isSynced = isSynced,
                            isMicMuted = isMicMuted,
                            isCameraOn = isCameraOn,
                            chatMessages = roomChat,
                            flyingReactions = flyingReactions,
                            onTogglePlayPause = { viewModel.togglePlayPause() },
                            onSeek = { sec -> viewModel.seekTo(sec) },
                            onSyncClick = { viewModel.syncPlayback() },
                            onToggleMic = { viewModel.toggleMic() },
                            onToggleCamera = { viewModel.toggleCamera() },
                            onSendMessage = { txt -> viewModel.sendRoomMessage(txt) },
                            onSendReaction = { emoji -> viewModel.triggerReaction(emoji) },
                            onLeaveRoom = { viewModel.navigateTo(AppScreen.HOME) }
                        )
                    }

                    AppScreen.PRIVATE_CHAT -> {
                        PrivateChatScreen(
                            friend = selectedFriend,
                            currentUser = currentUser,
                            messages = privateChat,
                            onSendMessage = { txt -> viewModel.sendPrivateMessage(txt) },
                            onJoinRoom = { room -> viewModel.openWatchRoom(room) },
                            onBack = { viewModel.navigateTo(AppScreen.FRIENDS) }
                        )
                    }

                    AppScreen.PROFILE -> {
                        ProfileScreen(
                            user = currentUser,
                            themeMode = themeMode,
                            onSetThemeMode = { mode -> viewModel.setThemeMode(mode) },
                            onNavigateToAdmin = { viewModel.navigateTo(AppScreen.ADMIN_PANEL) },
                            onUpdateStatus = { st -> viewModel.updateProfileStatus(st) },
                            onLogout = { viewModel.logout() }
                        )
                    }

                    AppScreen.ADMIN_PANEL -> {
                        AdminPanelScreen(
                            currentUser = currentUser,
                            allUsers = allUsers,
                            reports = reports,
                            rooms = rooms,
                            auditLogs = auditLogs,
                            adminMessage = adminMessage,
                            onClearAdminMessage = { viewModel.clearAdminMessage() },
                            onRefreshData = { viewModel.loadAdminData() },
                            onSuspendUser = { target, reason, type, until ->
                                viewModel.suspendUser(target, reason, type, until)
                            },
                            onUnsuspendUser = { target, reason ->
                                viewModel.unsuspendUser(target, reason)
                            },
                            onEditUserProfile = { target, name, handle, phone, gender, role, status, reason ->
                                viewModel.editUserProfile(target, name, handle, phone, gender, role, status, reason)
                            },
                            onForcePasswordReset = { target, email, disableSignIn, reason ->
                                viewModel.forcePasswordReset(target, email, disableSignIn, reason)
                            },
                            onResolveReport = { reportId, status, action, notes ->
                                viewModel.resolveReport(reportId, status, action, notes)
                            },
                            onCloseRoom = { roomId, roomTitle, reason ->
                                viewModel.closeAdminRoom(roomId, roomTitle, reason)
                            },
                            onBack = { viewModel.navigateTo(AppScreen.PROFILE) }
                        )
                    }
                }
            }

            // Suspended Account Alert Dialog
            if (authState is AuthState.Suspended) {
                val reason = (authState as AuthState.Suspended).reason
                AlertDialog(
                    onDismissRequest = { },
                    containerColor = KxaTheme.colors.surfaceElevated,
                    title = {
                        Text(
                            text = "Account Suspended",
                            color = Color(0xFFFF6B6B),
                            fontWeight = FontWeight.Bold,
                            fontSize = 18.sp
                        )
                    },
                    text = {
                        Text(
                            text = reason.ifBlank { "This account has been temporarily or permanently suspended by community moderation for violating the terms of service." },
                            color = KxaTheme.colors.textSecondary,
                            fontSize = 14.sp
                        )
                    },
                    confirmButton = {
                        Button(
                            onClick = { viewModel.logout() },
                            colors = ButtonDefaults.buttonColors(containerColor = PurplePrimary)
                        ) {
                            Text("Sign Out", color = Color.White)
                        }
                    }
                )
            }
        }
    }
}
