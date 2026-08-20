package com.example.ui.theme

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.core.tween

object KxaAnimation {
    val quick = tween<Float>(durationMillis = 180, easing = FastOutSlowInEasing)
    val standard = tween<Float>(durationMillis = 280, easing = FastOutSlowInEasing)
    val smooth = tween<Float>(durationMillis = 400, easing = LinearOutSlowInEasing)

    fun <T> bouncySpring() = spring<T>(
        dampingRatio = Spring.DampingRatioMediumBouncy,
        stiffness = Spring.StiffnessLow
    )

    fun <T> responsiveSpring() = spring<T>(
        dampingRatio = Spring.DampingRatioNoBouncy,
        stiffness = Spring.StiffnessMedium
    )
}
