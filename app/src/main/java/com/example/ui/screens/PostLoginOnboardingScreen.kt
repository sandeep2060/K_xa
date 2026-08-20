package com.example.ui.screens

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CameraAlt
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.EmojiEmotions
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.PlayCircleOutline
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.Friend
import com.example.model.User
import com.example.ui.components.KXaButton
import com.example.ui.components.KXaOutlinedButton
import com.example.ui.components.KXaTextField
import com.example.ui.components.UserAvatar
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.KxaRadius
import com.example.ui.theme.KxaSpacing
import com.example.ui.theme.KxaTheme
import com.example.ui.theme.OnlineGreen
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary
import kotlinx.coroutines.launch

private enum class PostLoginStep {
    WELCOME,
    PROFILE_PHOTO,
    SUGGESTED_FRIENDS,
    READY
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun PostLoginOnboardingScreen(
    currentUser: User,
    suggestedFriends: List<Friend>,
    onUploadAvatar: suspend (bytes: ByteArray, mimeType: String) -> Result<String>,
    onUpdateStatus: (String) -> Unit,
    onAddFriend: (User) -> Unit,
    onComplete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var currentStep by remember { mutableStateOf(PostLoginStep.WELCOME) }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var customAvatarUrl by remember { mutableStateOf<String?>(currentUser.avatarUrl) }
    var statusBio by remember { mutableStateOf(currentUser.statusMessage.ifBlank { "Ready to watch & vibe 🍿" }) }
    val addedFriends = remember { mutableStateListOf<String>() }

    var isUploadingAvatar by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    val galleryLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            selectedImageUri = uri
            scope.launch {
                isUploadingAvatar = true
                try {
                    val inputStream = context.contentResolver.openInputStream(uri)
                    val bytes = inputStream?.readBytes()
                    inputStream?.close()
                    if (bytes != null) {
                        val result = onUploadAvatar(bytes, context.contentResolver.getType(uri) ?: "image/jpeg")
                        result.onSuccess { url ->
                            customAvatarUrl = url
                        }
                    }
                } catch (e: Exception) {
                    // Fallback to local URI display
                } finally {
                    isUploadingAvatar = false
                }
            }
        }
    }

    // Avatar Presets (Color schemes)
    val avatarPresets = listOf(
        0xFF8B5CF6, // Purple
        0xFF06B6D4, // Cyan
        0xFFEC4899, // Pink
        0xFF10B981, // Green
        0xFFF59E0B, // Amber
        0xFF6366F1, // Indigo
        0xFFEF4444, // Red
        0xFF14B8A6  // Teal
    )
    var selectedPresetColor by remember { mutableStateOf(currentUser.avatarColorHex) }

    val infiniteTransition = rememberInfiniteTransition(label = "welcome_glow")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.85f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "glow_pulse"
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(KxaTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = KxaSpacing.standard, vertical = KxaSpacing.md)
            .testTag("screen_post_login_onboarding"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Step Indicator Progress Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            PostLoginStep.entries.forEachIndexed { index, step ->
                val isDoneOrCurrent = currentStep.ordinal >= step.ordinal
                val isCurrent = currentStep == step
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .height(4.dp)
                        .clip(RoundedCornerShape(KxaRadius.pill))
                        .background(
                            if (isCurrent) CyanAccent else if (isDoneOrCurrent) PurplePrimary else KxaTheme.colors.borderSubtle
                        )
                )
            }
        }

        // Animated Body
        AnimatedContent(
            targetState = currentStep,
            transitionSpec = {
                if (targetState.ordinal > initialState.ordinal) {
                    (slideInHorizontally { width -> width / 3 } + fadeIn()) togetherWith
                            (slideOutHorizontally { width -> -width / 3 } + fadeOut())
                } else {
                    (slideInHorizontally { width -> -width / 3 } + fadeIn()) togetherWith
                            (slideOutHorizontally { width -> width / 3 } + fadeOut())
                }
            },
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            label = "step_content"
        ) { step ->
            when (step) {
                PostLoginStep.WELCOME -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = KxaSpacing.md),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        // Glowing Welcome Icon
                        Box(
                            modifier = Modifier
                                .size(110.dp)
                                .scale(pulseGlow)
                                .background(PurplePrimary.copy(alpha = 0.2f), CircleShape)
                                .border(1.5.dp, CyanAccent.copy(alpha = 0.7f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.RocketLaunch,
                                contentDescription = null,
                                tint = CyanAccent,
                                modifier = Modifier.size(54.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(28.dp))

                        Text(
                            text = "Welcome to K Xa,",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 24.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = KxaTheme.colors.textSecondary
                        )

                        Text(
                            text = currentUser.name.ifBlank { currentUser.username },
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontSize = 32.sp,
                                fontWeight = FontWeight.ExtraBold
                            ),
                            color = PurpleLight
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "Watch synchronized videos, chat in real-time, and make memories with friends.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            color = KxaTheme.colors.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 16.dp)
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // 3 Quick Highlights
                        Column(
                            verticalArrangement = Arrangement.spacedBy(10.dp),
                            modifier = Modifier.fillMaxWidth(0.92f)
                        ) {
                            OnboardingFeatureRow(
                                icon = Icons.Default.PlayCircleOutline,
                                iconTint = CyanAccent,
                                title = "Synchronized Playback",
                                subtitle = "Watch YouTube & streams together in exact sync"
                            )
                            OnboardingFeatureRow(
                                icon = Icons.Default.EmojiEmotions,
                                iconTint = PurpleLight,
                                title = "Live Reactions & Chat",
                                subtitle = "React in real-time with floating emojis and voice"
                            )
                            OnboardingFeatureRow(
                                icon = Icons.Default.GroupAdd,
                                iconTint = OnlineGreen,
                                title = "Private & Public Rooms",
                                subtitle = "Easily host and invite your friends in one tap"
                            )
                        }
                    }
                }

                PostLoginStep.PROFILE_PHOTO -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = KxaSpacing.standard),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Set Up Your Profile",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = KxaTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Add a photo or pick your favorite color vibe (Optional)",
                            style = MaterialTheme.typography.bodySmall,
                            color = KxaTheme.colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(24.dp))

                        // Large Avatar Preview with upload button
                        Box(
                            modifier = Modifier.size(116.dp),
                            contentAlignment = Alignment.BottomEnd
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(116.dp)
                                    .clip(CircleShape)
                                    .background(
                                        Brush.linearGradient(
                                            listOf(
                                                Color(selectedPresetColor),
                                                Color(selectedPresetColor).copy(alpha = 0.6f)
                                            )
                                        ),
                                        CircleShape
                                    )
                                    .border(2.5.dp, PurpleLight, CircleShape),
                                contentAlignment = Alignment.Center
                            ) {
                                if (selectedImageUri != null) {
                                    AsyncImage(
                                        model = selectedImageUri,
                                        contentDescription = "Selected Avatar",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                } else if (!customAvatarUrl.isNullOrBlank()) {
                                    AsyncImage(
                                        model = customAvatarUrl,
                                        contentDescription = "Avatar",
                                        modifier = Modifier
                                            .fillMaxSize()
                                            .clip(CircleShape)
                                    )
                                } else {
                                    Text(
                                        text = currentUser.avatarInitial,
                                        fontSize = 44.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color.White
                                    )
                                }
                            }

                            // Camera / Upload badge button
                            Box(
                                modifier = Modifier
                                    .size(36.dp)
                                    .clip(CircleShape)
                                    .background(PurplePrimary)
                                    .border(2.dp, KxaTheme.colors.background, CircleShape)
                                    .clickable { galleryLauncher.launch("image/*") },
                                contentAlignment = Alignment.Center
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = "Change photo",
                                    tint = Color.White,
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Or pick an avatar color theme",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = KxaTheme.colors.textSecondary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        // Color preset palettes
                        Row(
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier.padding(horizontal = 8.dp)
                        ) {
                            avatarPresets.forEach { hex ->
                                val isSelected = selectedPresetColor == hex
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .clip(CircleShape)
                                        .background(Color(hex))
                                        .border(
                                            if (isSelected) 2.5.dp else 1.dp,
                                            if (isSelected) Color.White else Color.Transparent,
                                            CircleShape
                                        )
                                        .clickable {
                                            selectedPresetColor = hex
                                            selectedImageUri = null
                                        },
                                    contentAlignment = Alignment.Center
                                ) {
                                    if (isSelected) {
                                        Icon(
                                            imageVector = Icons.Default.Check,
                                            contentDescription = null,
                                            tint = Color.White,
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(20.dp))

                        // Bio / Status input
                        KXaTextField(
                            value = statusBio,
                            onValueChange = { statusBio = it },
                            label = "Status / Vibe",
                            placeholder = "What's on your watch list?",
                            leadingIcon = Icons.Default.EmojiEmotions,
                            singleLine = true,
                            testTag = "input_onboarding_status"
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Quick Vibe Chips
                        Text(
                            text = "Quick ideas:",
                            style = MaterialTheme.typography.labelSmall,
                            color = KxaTheme.colors.textMuted
                        )
                        Spacer(modifier = Modifier.height(6.dp))

                        FlowRow(
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            val ideas = listOf(
                                "Anime nights 🍿",
                                "Gaming streamer 🎮",
                                "Movie marathon 🎬",
                                "Chill vibes 🎧",
                                "Music lover 🎵"
                            )
                            ideas.forEach { idea ->
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(KxaRadius.pill))
                                        .background(KxaTheme.colors.surfaceVariant)
                                        .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.pill))
                                        .clickable { statusBio = idea }
                                        .padding(horizontal = 10.dp, vertical = 5.dp)
                                ) {
                                    Text(
                                        text = idea,
                                        style = MaterialTheme.typography.bodySmall.copy(fontSize = 12.sp),
                                        color = KxaTheme.colors.textSecondary
                                    )
                                }
                            }
                        }
                    }
                }

                PostLoginStep.SUGGESTED_FRIENDS -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = KxaSpacing.standard),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = "Find Friends",
                            style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                            color = KxaTheme.colors.textPrimary
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Connect with members to easily invite them to watch rooms",
                            style = MaterialTheme.typography.bodySmall,
                            color = KxaTheme.colors.textSecondary,
                            textAlign = TextAlign.Center
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        if (suggestedFriends.isEmpty()) {
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = "No other users online yet. You can invite friends later!",
                                    style = MaterialTheme.typography.bodyMedium,
                                    color = KxaTheme.colors.textMuted,
                                    textAlign = TextAlign.Center
                                )
                            }
                        } else {
                            LazyColumn(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .weight(1f),
                                verticalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                items(suggestedFriends) { friend ->
                                    val isAdded = addedFriends.contains(friend.user.id)
                                    SuggestedFriendRow(
                                        friend = friend,
                                        isAdded = isAdded,
                                        onToggleAdd = {
                                            if (isAdded) {
                                                addedFriends.remove(friend.user.id)
                                            } else {
                                                addedFriends.add(friend.user.id)
                                                onAddFriend(friend.user)
                                            }
                                        }
                                    )
                                }
                            }
                        }
                    }
                }

                PostLoginStep.READY -> {
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(vertical = KxaSpacing.standard),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(96.dp)
                                .background(OnlineGreen.copy(alpha = 0.15f), CircleShape)
                                .border(2.dp, OnlineGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Check,
                                contentDescription = "Ready",
                                tint = OnlineGreen,
                                modifier = Modifier.size(48.dp)
                            )
                        }

                        Spacer(modifier = Modifier.height(24.dp))

                        Text(
                            text = "You're All Set! 🎉",
                            style = MaterialTheme.typography.displayMedium.copy(
                                fontSize = 26.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            color = KxaTheme.colors.textPrimary
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = "Your K Xa account is customized and ready. Discover live watch parties or host your own!",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontSize = 14.sp,
                                lineHeight = 20.sp
                            ),
                            color = KxaTheme.colors.textSecondary,
                            textAlign = TextAlign.Center,
                            modifier = Modifier.padding(horizontal = 24.dp)
                        )
                    }
                }
            }
        }

        // Bottom Navigation / Action Controls
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            when (currentStep) {
                PostLoginStep.WELCOME -> {
                    KXaButton(
                        text = "Let's Get Started",
                        onClick = { currentStep = PostLoginStep.PROFILE_PHOTO },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_welcome_continue"
                    )
                }

                PostLoginStep.PROFILE_PHOTO -> {
                    KXaButton(
                        text = "Save & Continue",
                        onClick = {
                            if (statusBio.isNotBlank()) {
                                onUpdateStatus(statusBio.trim())
                            }
                            currentStep = PostLoginStep.SUGGESTED_FRIENDS
                        },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_profile_save_continue"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { currentStep = PostLoginStep.SUGGESTED_FRIENDS }
                    ) {
                        Text(
                            text = "Skip for now",
                            style = MaterialTheme.typography.labelLarge,
                            color = KxaTheme.colors.textSecondary
                        )
                    }
                }

                PostLoginStep.SUGGESTED_FRIENDS -> {
                    KXaButton(
                        text = if (addedFriends.isNotEmpty()) "Continue with ${addedFriends.size} Friend${if (addedFriends.size > 1) "s" else ""}" else "Continue",
                        onClick = { currentStep = PostLoginStep.READY },
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_friends_continue"
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    TextButton(
                        onClick = { currentStep = PostLoginStep.READY }
                    ) {
                        Text(
                            text = "Skip for now",
                            style = MaterialTheme.typography.labelLarge,
                            color = KxaTheme.colors.textSecondary
                        )
                    }
                }

                PostLoginStep.READY -> {
                    KXaButton(
                        text = "Enter K Xa Home",
                        onClick = onComplete,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_enter_home"
                    )
                }
            }
        }
    }
}

@Composable
private fun OnboardingFeatureRow(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(KxaRadius.md))
            .background(KxaTheme.colors.surfaceVariant)
            .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.md))
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(38.dp)
                .clip(CircleShape)
                .background(iconTint.copy(alpha = 0.15f)),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = iconTint,
                modifier = Modifier.size(20.dp)
            )
        }

        Spacer(modifier = Modifier.width(12.dp))

        Column {
            Text(
                text = title,
                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                color = KxaTheme.colors.textPrimary
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                color = KxaTheme.colors.textSecondary
            )
        }
    }
}

@Composable
private fun SuggestedFriendRow(
    friend: Friend,
    isAdded: Boolean,
    onToggleAdd: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(KxaRadius.md))
            .background(KxaTheme.colors.surfaceVariant)
            .border(1.dp, KxaTheme.colors.borderSubtle, RoundedCornerShape(KxaRadius.md))
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f)
        ) {
            UserAvatar(user = friend.user, size = 42.dp)
            Spacer(modifier = Modifier.width(12.dp))
            Column {
                Text(
                    text = friend.user.name,
                    style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold),
                    color = KxaTheme.colors.textPrimary
                )
                Text(
                    text = "@${friend.user.username} • ${friend.mutualFriends} mutual friends",
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = KxaTheme.colors.textSecondary
                )
            }
        }

        Box(
            modifier = Modifier
                .clip(RoundedCornerShape(KxaRadius.pill))
                .background(if (isAdded) OnlineGreen.copy(alpha = 0.2f) else PurplePrimary)
                .border(
                    1.dp,
                    if (isAdded) OnlineGreen else Color.Transparent,
                    RoundedCornerShape(KxaRadius.pill)
                )
                .clickable(onClick = onToggleAdd)
                .padding(horizontal = 12.dp, vertical = 6.dp),
            contentAlignment = Alignment.Center
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = if (isAdded) Icons.Default.Check else Icons.Default.Add,
                    contentDescription = null,
                    tint = if (isAdded) OnlineGreen else Color.White,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isAdded) "Added" else "Add",
                    style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                    color = if (isAdded) OnlineGreen else Color.White
                )
            }
        }
    }
}
