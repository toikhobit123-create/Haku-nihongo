package com.example.ui.theme

import androidx.compose.ui.graphics.Color

/**
 * Duolingo-style Bright Gamification Design System Palette
 * - Primary Green: #58CC02, shadow/border: #58A700
 * - Secondary Blue: #1CB0F6, shadow/border: #1899D6
 * - Danger Red: #FF4B4B, shadow/border: #EA1515
 * - Neutral: Page #FFFFFF, text #4B4B4B, subtext #777777, border #E5E5E5, light card #F7F7F7
 */

// Gamification Tokens
val GamifyGreen = Color(0xFF58CC02)
val GamifyGreenShadow = Color(0xFF58A700)
val GamifyGreenLight = Color(0xFFD7FFB8)
val GamifyGreenDark = Color(0xFF459A00)

val GamifyBlue = Color(0xFF1CB0F6)
val GamifyBlueShadow = Color(0xFF1899D6)
val GamifyBlueLight = Color(0xFFEAF6FE)
val GamifyBlueDark = Color(0xFF147BAA)

val GamifyRed = Color(0xFFFF4B4B)
val GamifyRedShadow = Color(0xFFEA1515)
val GamifyRedLight = Color(0xFFFFDFE0)
val GamifyRedDark = Color(0xFFEA2B2B)

val GamifyYellow = Color(0xFFFFC800)
val GamifyYellowShadow = Color(0xFFE5A500)
val GamifyYellowLight = Color(0xFFFFF7D6)

// Neutral Tokens
val NeutralWhite = Color(0xFFFFFFFF)
val NeutralText = Color(0xFF4B4B4B)
val NeutralSubtext = Color(0xFF777777)
val NeutralBorder = Color(0xFFE5E5E5)
val NeutralCard = Color(0xFFF7F7F7)
val NeutralDisabled = Color(0xFFE5E5E5)
val NeutralDisabledText = Color(0xFFAFAFAF)

// Compatibility Aliases for older code references
val Color01 = NeutralText            // #4B4B4B
val Color02 = NeutralSubtext         // #777777
val Color03 = NeutralBorder          // #E5E5E5
val Color04 = NeutralCard            // #F7F7F7
val Color05 = GamifyBlueLight        // #EAF6FE

val AppBackground = NeutralWhite
val SurfacePureWhite = NeutralWhite
val SurfaceSubtle = NeutralCard

// Semantic helpers
val HealthBarRed = GamifyRed
val BossHealthBar = Color(0xFF2B3240)
val AccentSuccess = GamifyGreen
