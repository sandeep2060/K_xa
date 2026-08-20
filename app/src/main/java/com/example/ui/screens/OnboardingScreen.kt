package com.example.ui.screens

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.Chat
import androidx.compose.material.icons.filled.GroupAdd
import androidx.compose.material.icons.filled.Groups
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.KXaButton
import com.example.ui.components.KXaOutlinedButton
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.CyanGlow
import com.example.ui.theme.KxaRadius
import com.example.ui.theme.KxaSpacing
import com.example.ui.theme.KxaTheme
import com.example.ui.theme.PurpleLight
import com.example.ui.theme.PurplePrimary
import kotlinx.coroutines.launch

data class OnboardingPageData(
    val title: String,
    val subtitle: String,
    val description: String,
    val icon: ImageVector,
    val iconColor: Color,
    val imageRes: Int,
    val badgeText: String,
    val gradient: List<Color>
)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun OnboardingScreen(
    onGetStarted: () -> Unit,
    onSignIn: () -> Unit
) {
    val pages = listOf(
        OnboardingPageData(
            title = "Watch Together",
            subtitle = "Turn watching into a shared experience",
            description = "Stream YouTube, Twitch, anime and movies in perfect millisecond synchronization with friends worldwide.",
            icon = Icons.Default.PlayCircleFilled,
            iconColor = CyanAccent,
            imageRes = R.drawable.img_onboarding_watch,
            badgeText = "⚡ SYNCED 1080P STREAM",
            gradient = listOf(PurplePrimary, CyanAccent)
        ),
        OnboardingPageData(
            title = "Chat & Connect",
            subtitle = "Talk, react and vibe while you stream",
            description = "Drop real-time reactions, audio voice commentary, and interactive chat streams as epic moments happen.",
            icon = Icons.AutoMirrored.Filled.Chat,
            iconColor = PurpleLight,
            imageRes = R.drawable.img_onboarding_chat,
            badgeText = "💬 LIVE CHAT & AUDIO REACTIONS",
            gradient = listOf(Color(0xFFEC4899), PurplePrimary)
        ),
        OnboardingPageData(
            title = "Create Your Partner",
            subtitle = "Match party buddies & host rooms",
            description = "Find your stream partner, invite your squad, and launch instant private watch rooms with custom PIN security.",
            icon = Icons.Default.GroupAdd,
            iconColor = Color(0xFF10B981),
            imageRes = R.drawable.img_onboarding_partner,
            badgeText = "✨ HOST & MATCH PARTY",
            gradient = listOf(Color(0xFF10B981), CyanAccent)
        )
    )

    val pagerState = rememberPagerState(pageCount = { pages.size })
    val scope = rememberCoroutineScope()

    // Ambient floating glow animation
    val infiniteTransition = rememberInfiniteTransition(label = "ambient_glow")
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.6f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(2400, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    val isLastPage = pagerState.currentPage == pages.size - 1

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(KxaTheme.colors.background)
            .statusBarsPadding()
            .navigationBarsPadding()
            .padding(horizontal = KxaSpacing.standard, vertical = KxaSpacing.md)
            .testTag("screen_onboarding"),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        // Top Bar: Logo, App Title & Skip Button
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 4.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(KxaTheme.colors.surfaceVariant)
                        .border(
                            1.2.dp,
                            Brush.linearGradient(pages[pagerState.currentPage].gradient),
                            RoundedCornerShape(10.dp)
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Image(
                        painter = painterResource(id = R.drawable.img_app_icon),
                        contentDescription = "K Xa Logo",
                        modifier = Modifier.size(26.dp)
                    )
                }
                Spacer(modifier = Modifier.width(10.dp))
                Column {
                    Text(
                        text = "K Xa",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 18.sp
                        ),
                        color = KxaTheme.colors.textPrimary
                    )
                    Text(
                        text = "Synchronized Watch Parties",
                        style = MaterialTheme.typography.labelSmall.copy(fontSize = 10.sp),
                        color = KxaTheme.colors.textMuted
                    )
                }
            }

            AnimatedVisibility(
                visible = !isLastPage,
                enter = fadeIn(),
                exit = fadeOut()
            ) {
                TextButton(
                    onClick = onSignIn,
                    modifier = Modifier.testTag("btn_skip_onboarding")
                ) {
                    Text(
                        text = "Skip",
                        style = MaterialTheme.typography.labelLarge.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        ),
                        color = PurpleLight
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Sliding Section Pager (with synchronized sliding image & rich details)
        HorizontalPager(
            state = pagerState,
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .testTag("pager_onboarding")
        ) { pageIndex ->
            val page = pages[pageIndex]

            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.Center,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 4.dp)
            ) {
                // Animated Card with Distinct Sliding Image
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(230.dp)
                        .clip(RoundedCornerShape(22.dp))
                        .background(KxaTheme.colors.surface)
                        .border(
                            1.5.dp,
                            Brush.linearGradient(page.gradient),
                            RoundedCornerShape(22.dp)
                        )
                ) {
                    // Image for current slide
                    Image(
                        painter = painterResource(id = page.imageRes),
                        contentDescription = page.title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Ambient Dark Gradient Overlay
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.35f),
                                        KxaTheme.colors.background.copy(alpha = 0.92f)
                                    )
                                )
                            )
                    )

                    // Top Floating Badge Chip
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(12.dp)
                            .clip(RoundedCornerShape(KxaRadius.pill))
                            .background(Color.Black.copy(alpha = 0.75f))
                            .border(1.dp, page.iconColor.copy(alpha = 0.6f), RoundedCornerShape(KxaRadius.pill))
                            .padding(horizontal = 10.dp, vertical = 5.dp)
                    ) {
                        Text(
                            text = page.badgeText,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                fontSize = 10.sp,
                                letterSpacing = 0.5.sp
                            ),
                            color = page.iconColor
                        )
                    }

                    // Floating Icon Center-Bottom Accent
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .offset(y = 18.dp)
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(KxaTheme.colors.surface)
                            .border(2.dp, Brush.linearGradient(page.gradient), CircleShape)
                            .padding(4.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(page.iconColor.copy(alpha = 0.2f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = page.icon,
                                contentDescription = null,
                                tint = page.iconColor,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(28.dp))

                // Title
                Text(
                    text = page.title,
                    style = MaterialTheme.typography.headlineMedium.copy(
                        fontSize = 24.sp,
                        fontWeight = FontWeight.ExtraBold
                    ),
                    color = KxaTheme.colors.textPrimary,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(4.dp))

                // Subtitle
                Text(
                    text = page.subtitle,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontSize = 14.sp,
                        fontWeight = FontWeight.SemiBold
                    ),
                    color = page.iconColor,
                    textAlign = TextAlign.Center
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Description
                Text(
                    text = page.description,
                    style = MaterialTheme.typography.bodyMedium.copy(
                        fontSize = 13.sp,
                        lineHeight = 20.sp
                    ),
                    color = KxaTheme.colors.textSecondary,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth(0.92f)
                )
            }
        }

        // Animated Page Indicator Dots & Navigation Indicator (with 48dp touch target)
        Row(
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(vertical = 10.dp)
                .testTag("onboarding_page_indicators")
        ) {
            repeat(pages.size) { index ->
                val isSelected = pagerState.currentPage == index
                val width by animateFloatAsState(
                    targetValue = if (isSelected) 28f else 8f,
                    animationSpec = tween(300),
                    label = "dot_width"
                )

                Box(
                    modifier = Modifier
                        .padding(horizontal = 4.dp)
                        .clip(RoundedCornerShape(KxaRadius.pill))
                        .clickable {
                            scope.launch {
                                pagerState.animateScrollToPage(index)
                            }
                        }
                        .padding(vertical = 8.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Box(
                        modifier = Modifier
                            .height(8.dp)
                            .width(width.dp)
                            .clip(RoundedCornerShape(KxaRadius.pill))
                            .background(
                                if (isSelected) {
                                    Brush.linearGradient(pages[index].gradient)
                                } else {
                                    Brush.linearGradient(listOf(KxaTheme.colors.border, KxaTheme.colors.border))
                                }
                            )
                    )
                }
            }
        }

        // Bottom CTA Buttons (Dynamic: "Sign Up ➔" on Last Section / "Continue ➔" on other slides)
        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            KXaButton(
                text = if (isLastPage) "Sign Up ➔" else "Continue ➔",
                onClick = {
                    if (isLastPage) {
                        onGetStarted()
                    } else {
                        scope.launch {
                            pagerState.animateScrollToPage(pagerState.currentPage + 1)
                        }
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                testTag = if (isLastPage) "btn_sign_up_cta" else "btn_continue_onboarding"
            )

            Spacer(modifier = Modifier.height(10.dp))

            KXaOutlinedButton(
                text = "Already have an account? Sign In",
                onClick = onSignIn,
                modifier = Modifier.fillMaxWidth(),
                testTag = "btn_sign_in_nav"
            )
        }
    }
}

