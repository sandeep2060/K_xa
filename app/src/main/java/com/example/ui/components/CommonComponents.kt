package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.Groups
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.MeetingRoom
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.MeetingRoom
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.model.ChatMessage
import com.example.model.FlyingReaction
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
import com.example.viewmodel.AppScreen
import kotlin.math.roundToInt

// =============================================================================
// 1. BUTTONS (KXaButton, KXaOutlinedButton, KXaIconButton)
// =============================================================================

@Composable
fun KXaButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    leadingIcon: ImageVector? = null,
    icon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    shape: Shape = RoundedCornerShape(KxaRadius.md),
    testTag: String = "kxa_button"
) {
    val effectiveLeadingIcon = leadingIcon ?: icon

    Surface(
        onClick = onClick,
        enabled = enabled && !isLoading,
        shape = shape,
        color = Color.Transparent,
        modifier = modifier
            .testTag(testTag)
            .height(52.dp)
            .clip(shape)
            .background(
                if (enabled) {
                    KxaTheme.colors.brandGradient
                } else {
                    Brush.linearGradient(
                        listOf(
                            KxaTheme.colors.surfaceVariant,
                            KxaTheme.colors.surfaceVariant
                        )
                    )
                }
            )
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(22.dp),
                    color = Color.White,
                    strokeWidth = 2.5.dp
                )
            } else {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.padding(horizontal = KxaSpacing.standard)
                ) {
                    if (effectiveLeadingIcon != null) {
                        Icon(
                            imageVector = effectiveLeadingIcon,
                            contentDescription = null,
                            tint = if (enabled) Color.White else KxaTheme.colors.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(KxaSpacing.sm))
                    }

                    Text(
                        text = text,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        ),
                        color = if (enabled) Color.White else KxaTheme.colors.textMuted
                    )

                    if (trailingIcon != null) {
                        Spacer(modifier = Modifier.width(KxaSpacing.sm))
                        Icon(
                            imageVector = trailingIcon,
                            contentDescription = null,
                            tint = if (enabled) Color.White else KxaTheme.colors.textMuted,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }
    }
}

@Composable
fun KXaOutlinedButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    leadingIcon: ImageVector? = null,
    icon: ImageVector? = null,
    trailingIcon: ImageVector? = null,
    borderColor: Color = KxaTheme.colors.border,
    textColor: Color = KxaTheme.colors.textPrimary,
    shape: Shape = RoundedCornerShape(KxaRadius.md),
    testTag: String = "kxa_outlined_button"
) {
    val effectiveLeadingIcon = leadingIcon ?: icon

    Surface(
        onClick = onClick,
        enabled = enabled,
        shape = shape,
        color = KxaTheme.colors.surface,
        border = androidx.compose.foundation.BorderStroke(1.dp, borderColor),
        modifier = modifier
            .testTag(testTag)
            .height(52.dp)
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier.padding(horizontal = KxaSpacing.standard)
        ) {
            if (effectiveLeadingIcon != null) {
                Icon(
                    imageVector = effectiveLeadingIcon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(KxaSpacing.sm))
            }

            Text(
                text = text,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 15.sp,
                    fontWeight = FontWeight.SemiBold
                ),
                color = textColor
            )

            if (trailingIcon != null) {
                Spacer(modifier = Modifier.width(KxaSpacing.sm))
                Icon(
                    imageVector = trailingIcon,
                    contentDescription = null,
                    tint = textColor,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

@Composable
fun KXaIconButton(
    icon: ImageVector,
    contentDescription: String?,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    tint: Color = KxaTheme.colors.textPrimary,
    backgroundColor: Color = KxaTheme.colors.surfaceVariant,
    size: Dp = 42.dp,
    badgeCount: Int = 0,
    testTag: String = "kxa_icon_button"
) {
    Box(
        contentAlignment = Alignment.TopEnd,
        modifier = modifier.testTag(testTag)
    ) {
        IconButton(
            onClick = onClick,
            modifier = Modifier
                .size(size)
                .background(backgroundColor, CircleShape)
                .border(1.dp, KxaTheme.colors.borderSubtle, CircleShape)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = contentDescription,
                tint = tint,
                modifier = Modifier.size(20.dp)
            )
        }

        if (badgeCount > 0) {
            Box(
                modifier = Modifier
                    .offset(x = 2.dp, y = (-2).dp)
                    .size(16.dp)
                    .background(LiveRed, CircleShape)
                    .border(1.5.dp, KxaTheme.colors.surface, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (badgeCount > 99) "99+" else badgeCount.toString(),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}

// =============================================================================
// 2. TEXT FIELDS (KXaTextField)
// =============================================================================

@Composable
fun KXaTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String = "",
    modifier: Modifier = Modifier,
    placeholder: String = "",
    leadingIcon: ImageVector? = null,
    trailingIcon: @Composable (() -> Unit)? = null,
    isError: Boolean = false,
    errorMessage: String? = null,
    singleLine: Boolean = true,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions.Default,
    keyboardActions: KeyboardActions = KeyboardActions.Default,
    testTag: String = "kxa_text_field"
) {
    Column(modifier = modifier.fillMaxWidth()) {
        if (label.isNotBlank()) {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 12.sp
                ),
                color = if (isError) LiveRed else KxaTheme.colors.textSecondary
            )
            Spacer(modifier = Modifier.height(6.dp))
        }

        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            placeholder = {
                if (placeholder.isNotBlank()) {
                    Text(
                        text = placeholder,
                        style = MaterialTheme.typography.bodyMedium,
                        color = KxaTheme.colors.textMuted
                    )
                }
            },
            leadingIcon = if (leadingIcon != null) {
                {
                    Icon(
                        imageVector = leadingIcon,
                        contentDescription = null,
                        tint = if (isError) LiveRed else KxaTheme.colors.textSecondary,
                        modifier = Modifier.size(20.dp)
                    )
                }
            } else null,
            trailingIcon = trailingIcon,
            singleLine = singleLine,
            isError = isError,
            visualTransformation = visualTransformation,
            keyboardOptions = keyboardOptions,
            keyboardActions = keyboardActions,
            shape = RoundedCornerShape(KxaRadius.md),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = PurplePrimary,
                unfocusedBorderColor = KxaTheme.colors.border,
                errorBorderColor = LiveRed,
                focusedContainerColor = KxaTheme.colors.surfaceVariant,
                unfocusedContainerColor = KxaTheme.colors.surface,
                errorContainerColor = KxaTheme.colors.surface,
                focusedTextColor = KxaTheme.colors.textPrimary,
                unfocusedTextColor = KxaTheme.colors.textPrimary,
                cursorColor = PurplePrimary
            ),
            modifier = Modifier
                .testTag(testTag)
                .fillMaxWidth()
        )

        if (isError && !errorMessage.isNullOrBlank()) {
            Spacer(modifier = Modifier.height(4.dp))
            Row(verticalAlignment = Alignment.CenterVertically) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = LiveRed,
                    modifier = Modifier.size(13.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = errorMessage,
                    style = MaterialTheme.typography.bodySmall.copy(fontSize = 11.sp),
                    color = LiveRed
                )
            }
        }
    }
}

// =============================================================================
// 3. CARDS & SURFACES (KXaCard, KXaGlassCard)
// =============================================================================

@Composable
fun KXaCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(KxaRadius.lg),
    borderColor: Color = KxaTheme.colors.border,
    backgroundColor: Color = KxaTheme.colors.surface,
    elevation: Dp = 2.dp,
    onClick: (() -> Unit)? = null,
    content: @Composable () -> Unit
) {
    val baseModifier = modifier
        .shadow(elevation, shape, clip = false)
        .clip(shape)
        .background(backgroundColor)
        .border(1.dp, borderColor, shape)

    if (onClick != null) {
        Box(
            modifier = baseModifier.clickable(onClick = onClick)
        ) {
            content()
        }
    } else {
        Box(modifier = baseModifier) {
            content()
        }
    }
}

@Composable
fun KXaGlassCard(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(KxaRadius.lg),
    borderColor: Color? = null,
    content: @Composable () -> Unit
) {
    val borderBrush = if (borderColor != null) {
        Brush.linearGradient(listOf(borderColor, borderColor.copy(alpha = 0.4f)))
    } else {
        Brush.linearGradient(
            listOf(
                PurplePrimary.copy(alpha = 0.35f),
                CyanAccent.copy(alpha = 0.2f)
            )
        )
    }

    Box(
        modifier = modifier
            .clip(shape)
            .background(
                if (KxaTheme.colors.isDark) {
                    KxaTheme.colors.surfaceElevated.copy(alpha = 0.7f)
                } else {
                    KxaTheme.colors.surface.copy(alpha = 0.85f)
                }
            )
            .border(1.dp, borderBrush, shape)
    ) {
        content()
    }
}

// =============================================================================
// 4. TOP BAR & APP HEADER
// =============================================================================

@Composable
fun AppHeader(
    title: String,
    subtitle: String? = null,
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    trailingContent: @Composable (() -> Unit)? = null
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = KxaSpacing.standard, vertical = KxaSpacing.md),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.weight(1f, fill = false)
        ) {
            if (showBack) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier
                        .testTag("btn_back")
                        .size(38.dp)
                        .background(KxaTheme.colors.surfaceVariant, CircleShape)
                        .border(1.dp, KxaTheme.colors.borderSubtle, CircleShape)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = KxaTheme.colors.textPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                }
                Spacer(modifier = Modifier.width(KxaSpacing.md))
            }

            Column {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                    color = KxaTheme.colors.textPrimary
                )
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall,
                        color = KxaTheme.colors.textSecondary
                    )
                }
            }
        }

        if (trailingContent != null) {
            trailingContent()
        }
    }
}

// =============================================================================
// 5. BOTTOM NAVIGATION BAR
// =============================================================================

@Composable
fun AppBottomNav(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding(),
        color = KxaTheme.colors.surface.copy(alpha = 0.96f),
        tonalElevation = 8.dp,
        border = androidx.compose.foundation.BorderStroke(1.dp, KxaTheme.colors.borderSubtle)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp, horizontal = 12.dp),
            horizontalArrangement = Arrangement.SpaceAround,
            verticalAlignment = Alignment.CenterVertically
        ) {
            NavTabItem(
                label = "Home",
                selectedIcon = Icons.Filled.Home,
                unselectedIcon = Icons.Outlined.Home,
                isSelected = currentScreen == AppScreen.HOME,
                onClick = { onNavigate(AppScreen.HOME) },
                testTag = "nav_home"
            )
            NavTabItem(
                label = "Friends",
                selectedIcon = Icons.Filled.Groups,
                unselectedIcon = Icons.Outlined.Groups,
                isSelected = currentScreen == AppScreen.FRIENDS,
                onClick = { onNavigate(AppScreen.FRIENDS) },
                badgeCount = 3,
                testTag = "nav_friends"
            )
            NavTabItem(
                label = "Rooms",
                selectedIcon = Icons.Filled.MeetingRoom,
                unselectedIcon = Icons.Outlined.MeetingRoom,
                isSelected = currentScreen == AppScreen.JOIN_ROOM || currentScreen == AppScreen.CREATE_ROOM,
                onClick = { onNavigate(AppScreen.JOIN_ROOM) },
                testTag = "nav_rooms"
            )
            NavTabItem(
                label = "Chats",
                selectedIcon = Icons.Filled.ChatBubble,
                unselectedIcon = Icons.Outlined.ChatBubbleOutline,
                isSelected = currentScreen == AppScreen.PRIVATE_CHAT,
                onClick = { onNavigate(AppScreen.PRIVATE_CHAT) },
                testTag = "nav_chats"
            )
            NavTabItem(
                label = "Profile",
                selectedIcon = Icons.Filled.Person,
                unselectedIcon = Icons.Outlined.Person,
                isSelected = currentScreen == AppScreen.PROFILE,
                onClick = { onNavigate(AppScreen.PROFILE) },
                testTag = "nav_profile"
            )
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    isSelected: Boolean,
    onClick: () -> Unit,
    badgeCount: Int = 0,
    testTag: String
) {
    val interactionSource = remember { MutableInteractionSource() }
    val animatedColor by animateColorAsState(
        targetValue = if (isSelected) PurplePrimary else KxaTheme.colors.textMuted,
        animationSpec = tween(180),
        label = "nav_color"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .testTag(testTag)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Box(contentAlignment = Alignment.TopEnd) {
            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isSelected) PurplePrimary.copy(alpha = 0.15f) else Color.Transparent
                    )
                    .padding(horizontal = 14.dp, vertical = 6.dp)
            ) {
                Icon(
                    imageVector = if (isSelected) selectedIcon else unselectedIcon,
                    contentDescription = label,
                    tint = animatedColor,
                    modifier = Modifier.size(22.dp)
                )
            }

            if (badgeCount > 0 && !isSelected) {
                Box(
                    modifier = Modifier
                        .offset(x = 2.dp, y = (-2).dp)
                        .size(14.dp)
                        .background(LiveRed, CircleShape)
                        .border(1.5.dp, KxaTheme.colors.surface, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = badgeCount.toString(),
                        color = Color.White,
                        fontSize = 9.sp,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            fontSize = 11.sp,
            fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
            color = animatedColor
        )
    }
}

// =============================================================================
// 6. AVATARS (KXaAvatar)
// =============================================================================

@Composable
fun UserAvatar(
    user: User,
    size: Dp = 44.dp,
    showOnlineDot: Boolean = true,
    showSpeakingHalo: Boolean = false,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "halo")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = 1.18f,
        animationSpec = infiniteRepeatable(
            animation = tween(900, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse"
    )

    Box(
        contentAlignment = Alignment.Center,
        modifier = modifier.size(size)
    ) {
        // Speaking ring pulse animation
        if (showSpeakingHalo || user.isSpeaking) {
            Box(
                modifier = Modifier
                    .size(size * 1.25f)
                    .scale(pulseScale)
                    .background(CyanGlow.copy(alpha = 0.25f), CircleShape)
                    .border(1.5.dp, CyanAccent.copy(alpha = 0.6f), CircleShape)
            )
        }

        // Avatar Core Circle
        Box(
            modifier = Modifier
                .size(size)
                .clip(CircleShape)
                .background(
                    Brush.linearGradient(
                        colors = listOf(
                            Color(user.avatarColorHex),
                            Color(user.avatarColorHex).copy(alpha = 0.65f)
                        )
                    ),
                    CircleShape
                )
                .border(1.5.dp, KxaTheme.colors.border, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            if (!user.avatarUrl.isNullOrBlank()) {
                AsyncImage(
                    model = user.avatarUrl,
                    contentDescription = user.name,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(CircleShape)
                )
            } else {
                Text(
                    text = user.avatarInitial,
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = (size.value * 0.42f).sp
                )
            }
        }

        // Online Status Dot
        if (showOnlineDot) {
            Box(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .size((size.value * 0.3f).coerceAtLeast(10f).dp)
                    .background(if (user.isOnline) OnlineGreen else KxaTheme.colors.textMuted, CircleShape)
                    .border(2.dp, KxaTheme.colors.surface, CircleShape)
            )
        }
    }
}

// =============================================================================
// 7. BADGES & CHIPS (KXaBadge, KXaChip)
// =============================================================================

@Composable
fun KXaBadge(
    text: String,
    modifier: Modifier = Modifier,
    backgroundColor: Color = PurplePrimary.copy(alpha = 0.15f),
    textColor: Color = PurpleLight,
    leadingDotColor: Color? = null
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(KxaRadius.pill))
            .background(backgroundColor)
            .border(1.dp, backgroundColor.copy(alpha = 0.4f), RoundedCornerShape(KxaRadius.pill))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        if (leadingDotColor != null) {
            Box(
                modifier = Modifier
                    .size(6.dp)
                    .background(leadingDotColor, CircleShape)
            )
            Spacer(modifier = Modifier.width(5.dp))
        }

        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                fontSize = 10.sp
            ),
            color = textColor
        )
    }
}

@Composable
fun KXaLiveBadge(modifier: Modifier = Modifier) {
    val infiniteTransition = rememberInfiniteTransition(label = "live_pulse")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(700, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(KxaRadius.pill))
            .background(LiveRed.copy(alpha = 0.2f))
            .border(1.dp, LiveRed.copy(alpha = 0.6f), RoundedCornerShape(KxaRadius.pill))
            .padding(horizontal = 8.dp, vertical = 3.dp)
    ) {
        Box(
            modifier = Modifier
                .size(6.dp)
                .alpha(dotAlpha)
                .background(LiveRed, CircleShape)
        )
        Spacer(modifier = Modifier.width(5.dp))
        Text(
            text = "LIVE",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Black,
                fontSize = 10.sp
            ),
            color = LiveRed
        )
    }
}

@Composable
fun KXaChip(
    text: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    icon: ImageVector? = null
) {
    val bgColor by animateColorAsState(
        targetValue = if (selected) PurplePrimary else KxaTheme.colors.surfaceVariant,
        label = "chip_bg"
    )
    val textColor by animateColorAsState(
        targetValue = if (selected) Color.White else KxaTheme.colors.textSecondary,
        label = "chip_text"
    )

    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clip(RoundedCornerShape(KxaRadius.pill))
            .background(bgColor)
            .border(
                1.dp,
                if (selected) PurplePrimary else KxaTheme.colors.borderSubtle,
                RoundedCornerShape(KxaRadius.pill)
            )
            .clickable(onClick = onClick)
            .padding(horizontal = 14.dp, vertical = 7.dp)
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = textColor,
                modifier = Modifier.size(14.dp)
            )
            Spacer(modifier = Modifier.width(6.dp))
        }

        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium.copy(
                fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                fontSize = 12.sp
            ),
            color = textColor
        )
    }
}

// =============================================================================
// 8. LOADING & SKELETON SHIMMER (KXaSkeleton)
// =============================================================================

@Composable
fun KXaSkeleton(
    modifier: Modifier = Modifier,
    shape: Shape = RoundedCornerShape(KxaRadius.sm)
) {
    val transition = rememberInfiniteTransition(label = "shimmer")
    val translateAnim by transition.animateFloat(
        initialValue = 0f,
        targetValue = 1000f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "shimmer_trans"
    )

    val shimmerColors = if (KxaTheme.colors.isDark) {
        listOf(
            Color(0xFF1E2235),
            Color(0xFF2E334D),
            Color(0xFF1E2235)
        )
    } else {
        listOf(
            Color(0xFFE2E8F0),
            Color(0xFFF1F5F9),
            Color(0xFFE2E8F0)
        )
    }

    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(x = translateAnim - 300f, y = translateAnim - 300f),
        end = Offset(x = translateAnim, y = translateAnim)
    )

    Box(
        modifier = modifier
            .clip(shape)
            .background(brush)
    )
}

// =============================================================================
// 9. EMPTY & ERROR STATES
// =============================================================================

@Composable
fun KXaEmptyState(
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    icon: ImageVector = Icons.Default.Info,
    actionButtonText: String? = null,
    onActionClick: (() -> Unit)? = null
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(KxaSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(64.dp)
                .background(PurplePrimary.copy(alpha = 0.12f), CircleShape)
                .border(1.dp, PurplePrimary.copy(alpha = 0.3f), CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = PurpleLight,
                modifier = Modifier.size(30.dp)
            )
        }

        Spacer(modifier = Modifier.height(KxaSpacing.standard))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = KxaTheme.colors.textPrimary,
            textAlign = TextAlign.Center
        )

        Spacer(modifier = Modifier.height(KxaSpacing.xs))

        Text(
            text = description,
            style = MaterialTheme.typography.bodyMedium,
            color = KxaTheme.colors.textMuted,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth(0.85f)
        )

        if (actionButtonText != null && onActionClick != null) {
            Spacer(modifier = Modifier.height(KxaSpacing.standard))
            KXaButton(
                text = actionButtonText,
                onClick = onActionClick,
                modifier = Modifier.width(180.dp)
            )
        }
    }
}

@Composable
fun KXaErrorState(
    message: String,
    onRetry: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .padding(KxaSpacing.xl),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = Icons.Default.Warning,
            contentDescription = null,
            tint = LiveRed,
            modifier = Modifier.size(40.dp)
        )
        Spacer(modifier = Modifier.height(KxaSpacing.sm))
        Text(
            text = "Something went wrong",
            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
            color = KxaTheme.colors.textPrimary
        )
        Spacer(modifier = Modifier.height(KxaSpacing.xs))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall,
            color = KxaTheme.colors.textSecondary,
            textAlign = TextAlign.Center
        )
        Spacer(modifier = Modifier.height(KxaSpacing.standard))
        KXaOutlinedButton(
            text = "Try Again",
            onClick = onRetry,
            modifier = Modifier.width(140.dp)
        )
    }
}

// =============================================================================
// 10. FLYING REACTIONS OVERLAY & REACTION BUTTON
// =============================================================================

@Composable
fun FlyingReactionsOverlay(
    reactions: List<FlyingReaction>,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier.fillMaxSize()
    ) {
        reactions.forEach { item ->
            SingleFlyingEmoji(item = item)
        }
    }
}

@Composable
private fun SingleFlyingEmoji(item: FlyingReaction) {
    var offsetY by remember { mutableFloatStateOf(0f) }
    var alpha by remember { mutableFloatStateOf(1f) }

    LaunchedEffect(item.id) {
        val duration = 2000
        val startTime = System.currentTimeMillis()
        while (true) {
            val elapsed = System.currentTimeMillis() - startTime
            val fraction = (elapsed.toFloat() / duration).coerceIn(0f, 1f)
            offsetY = -fraction * 350f
            alpha = (1f - fraction).coerceIn(0f, 1f)
            if (fraction >= 1f) break
            kotlinx.coroutines.delay(16)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .offset {
                IntOffset(
                    x = (item.startXRatio * 700).roundToInt() - 350,
                    y = (offsetY).roundToInt()
                )
            }
            .alpha(alpha)
            .scale(item.scale),
        contentAlignment = Alignment.BottomCenter
    ) {
        Text(
            text = item.emoji,
            fontSize = 32.sp
        )
    }
}

@Composable
fun KXaReactionButton(
    emoji: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        onClick = onClick,
        shape = CircleShape,
        color = KxaTheme.colors.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, KxaTheme.colors.borderSubtle),
        modifier = modifier.size(44.dp)
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(text = emoji, fontSize = 20.sp)
        }
    }
}

@Composable
fun KXaOnlineIndicator(
    isOnline: Boolean,
    modifier: Modifier = Modifier,
    size: Dp = 10.dp
) {
    Box(
        modifier = modifier
            .size(size)
            .background(if (isOnline) OnlineGreen else KxaTheme.colors.textMuted, CircleShape)
            .border(1.5.dp, KxaTheme.colors.surface, CircleShape)
    )
}

@Composable
fun KXaSuccessMessage(
    message: String,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(KxaRadius.md))
            .background(OnlineGreen.copy(alpha = 0.12f))
            .border(1.dp, OnlineGreen.copy(alpha = 0.4f), RoundedCornerShape(KxaRadius.md))
            .padding(KxaSpacing.md),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = OnlineGreen,
            modifier = Modifier.size(18.dp)
        )
        Spacer(modifier = Modifier.width(KxaSpacing.sm))
        Text(
            text = message,
            style = MaterialTheme.typography.bodySmall.copy(fontWeight = FontWeight.Medium),
            color = OnlineGreen
        )
    }
}

// Alias composables for clean nomenclature
@Composable
fun KXaAvatar(
    user: User,
    size: Dp = 44.dp,
    showOnlineDot: Boolean = true,
    showSpeakingHalo: Boolean = false,
    modifier: Modifier = Modifier
) = UserAvatar(user, size, showOnlineDot, showSpeakingHalo, modifier)

@Composable
fun KXaTopBar(
    title: String,
    subtitle: String? = null,
    showBack: Boolean = false,
    onBackClick: () -> Unit = {},
    trailingContent: @Composable (() -> Unit)? = null
) = AppHeader(title, subtitle, showBack, onBackClick, trailingContent)

@Composable
fun KXaBottomNavigation(
    currentScreen: AppScreen,
    onNavigate: (AppScreen) -> Unit,
    modifier: Modifier = Modifier
) = AppBottomNav(currentScreen, onNavigate, modifier)

