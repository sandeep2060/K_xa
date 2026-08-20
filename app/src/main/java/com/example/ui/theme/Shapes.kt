package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

val Shapes = Shapes(
    extraSmall = RoundedCornerShape(KxaRadius.xs),
    small = RoundedCornerShape(KxaRadius.sm),
    medium = RoundedCornerShape(KxaRadius.md),
    large = RoundedCornerShape(KxaRadius.lg),
    extraLarge = RoundedCornerShape(KxaRadius.xl)
)

object KxaShapes {
    val buttonShape = RoundedCornerShape(KxaRadius.md)
    val cardShape = RoundedCornerShape(KxaRadius.lg)
    val inputShape = RoundedCornerShape(KxaRadius.md)
    val dialogShape = RoundedCornerShape(KxaRadius.xl)
    val bottomSheetShape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp)
    val chipShape = RoundedCornerShape(KxaRadius.pill)
}
