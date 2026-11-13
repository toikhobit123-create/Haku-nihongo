package com.example.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// Gamification shapes: Rounded 12dp, 16dp, 20dp, and Full
val RoundedSm = RoundedCornerShape(8.dp)
val RoundedMd = RoundedCornerShape(12.dp)
val RoundedLg = RoundedCornerShape(16.dp)
val RoundedXl = RoundedCornerShape(20.dp)
val RoundedFull = RoundedCornerShape(999.dp)

val Shapes = Shapes(
    small = RoundedSm,
    medium = RoundedLg,
    large = RoundedXl
)
