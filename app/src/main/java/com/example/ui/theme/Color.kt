package com.example.ui.theme

import androidx.compose.runtime.Immutable
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

// =============================================================================
// BRAND ACCENT SYSTEM
// =============================================================================
val PurplePrimary = Color(0xFF8B5CF6)
val PurpleLight = Color(0xFFA78BFA)
val PurpleDark = Color(0xFF6D28D9)
val PurpleDeep = Color(0xFF4C1D95)

val CyanAccent = Color(0xFF06B6D4)
val CyanLight = Color(0xFF38BDF8)
val CyanDark = Color(0xFF0891B2)
val CyanGlow = Color(0xFF22D3EE)

val NeonPink = Color(0xFFF43F5E)
val OnlineGreen = Color(0xFF10B981)
val LiveRed = Color(0xFFEF4444)
val AmberWarning = Color(0xFFF59E0B)

// =============================================================================
// DARK MODE PALETTE (Obsidian Dark / Deep Charcoal)
// =============================================================================
val DarkBackground = Color(0xFF0A0B10)
val DarkSurface = Color(0xFF12141F)
val DarkSurfaceVariant = Color(0xFF191C2C)
val DarkSurfaceElevated = Color(0xFF222638)
val DarkBorder = Color(0xFF2E334D)
val DarkBorderSubtle = Color(0xFF1C2033)

val DarkTextPrimary = Color(0xFFF8FAFC)
val DarkTextSecondary = Color(0xFF94A3B8)
val DarkTextMuted = Color(0xFF64748B)

// =============================================================================
// LIGHT MODE PALETTE (Clean Crisp Off-White)
// =============================================================================
val LightBackground = Color(0xFFF8F9FD)
val LightSurface = Color(0xFFFFFFFF)
val LightSurfaceVariant = Color(0xFFF1F3F9)
val LightSurfaceElevated = Color(0xFFFFFFFF)
val LightBorder = Color(0xFFE2E8F0)
val LightBorderSubtle = Color(0xFFEEF2F6)

val LightTextPrimary = Color(0xFF0F172A)
val LightTextSecondary = Color(0xFF475569)
val LightTextMuted = Color(0xFF94A3B8)

// Backward compatibility alias references
val TextPrimary = DarkTextPrimary
val TextSecondary = DarkTextSecondary
val TextMuted = DarkTextMuted

// =============================================================================
// BRAND GRADIENTS
// =============================================================================
val BrandGradientDark = Brush.linearGradient(
    listOf(PurplePrimary, CyanAccent)
)

val BrandGradientLight = Brush.linearGradient(
    listOf(PurpleDark, CyanDark)
)

val CardGlowDark = Brush.verticalGradient(
    listOf(PurplePrimary.copy(alpha = 0.12f), Color.Transparent)
)

val CardGlowLight = Brush.verticalGradient(
    listOf(PurplePrimary.copy(alpha = 0.06f), Color.Transparent)
)

@Immutable
data class KxaCustomColors(
    val background: Color,
    val surface: Color,
    val surfaceVariant: Color,
    val surfaceElevated: Color,
    val border: Color,
    val borderSubtle: Color,
    val textPrimary: Color,
    val textSecondary: Color,
    val textMuted: Color,
    val primary: Color,
    val secondary: Color,
    val liveRed: Color,
    val onlineGreen: Color,
    val brandGradient: Brush,
    val cardGlow: Brush,
    val isDark: Boolean
)

val LocalKxaColors = staticCompositionLocalOf {
    KxaCustomColors(
        background = DarkBackground,
        surface = DarkSurface,
        surfaceVariant = DarkSurfaceVariant,
        surfaceElevated = DarkSurfaceElevated,
        border = DarkBorder,
        borderSubtle = DarkBorderSubtle,
        textPrimary = DarkTextPrimary,
        textSecondary = DarkTextSecondary,
        textMuted = DarkTextMuted,
        primary = PurplePrimary,
        secondary = CyanAccent,
        liveRed = LiveRed,
        onlineGreen = OnlineGreen,
        brandGradient = BrandGradientDark,
        cardGlow = CardGlowDark,
        isDark = true
    )
}
