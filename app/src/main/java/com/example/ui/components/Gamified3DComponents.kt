package com.example.ui.components

import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.state.Screen
import com.example.ui.theme.GamifyBlue
import com.example.ui.theme.GamifyBlueLight
import com.example.ui.theme.GamifyBlueShadow
import com.example.ui.theme.GamifyGreen
import com.example.ui.theme.GamifyGreenDark
import com.example.ui.theme.GamifyGreenShadow
import com.example.ui.theme.GamifyRed
import com.example.ui.theme.GamifyYellow
import com.example.ui.theme.NeutralBorder
import com.example.ui.theme.NeutralCard
import com.example.ui.theme.NeutralDisabled
import com.example.ui.theme.NeutralDisabledText
import com.example.ui.theme.NeutralSubtext
import com.example.ui.theme.NeutralText
import com.example.ui.theme.NeutralWhite
import com.example.ui.theme.RoundedFull
import com.example.ui.theme.RoundedLg
import com.example.ui.theme.RoundedMd

/**
 * Duolingo-style 3D Tactile Button
 * Features a thick 4dp bottom border in a darker shade that presses down on click.
 */
@Composable
fun Gamified3DButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    backgroundColor: Color = GamifyGreen,
    shadowColor: Color = GamifyGreenShadow,
    textColor: Color = Color.White,
    icon: ImageVector? = null,
    enabled: Boolean = true,
    shape: RoundedCornerShape = RoundedLg,
    height: Dp = 50.dp,
    testTag: String = ""
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val currentBg = if (enabled) backgroundColor else NeutralDisabled
    val currentShadow = if (enabled) shadowColor else Color(0xFFD0D0D0)
    val currentTextColor = if (enabled) textColor else NeutralDisabledText

    val pressOffset by animateDpAsState(
        targetValue = if (isPressed && enabled) 3.dp else 0.dp,
        animationSpec = tween(durationMillis = 60),
        label = "btn_press"
    )

    val shadowHeight = 4.dp

    Box(
        modifier = modifier
            .height(height + shadowHeight)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
    ) {
        // Bottom 3D shadow layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = shadowHeight)
                .clip(shape)
                .background(currentShadow)
        )

        // Top interactive face layer
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(height)
                .offset(y = pressOffset)
                .clip(shape)
                .background(currentBg),
            contentAlignment = Alignment.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center,
                modifier = Modifier.padding(horizontal = 16.dp)
            ) {
                if (icon != null) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = currentTextColor,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                }
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = currentTextColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp
                    )
                )
            }
        }
    }
}

/**
 * 3D Gamified Card with optional selection state
 */
@Composable
fun Gamified3DCard(
    modifier: Modifier = Modifier,
    isSelected: Boolean = false,
    onClick: (() -> Unit)? = null,
    backgroundColor: Color = if (isSelected) GamifyBlueLight else NeutralWhite,
    borderColor: Color = if (isSelected) GamifyBlue else NeutralBorder,
    borderWidth: Dp = if (isSelected) 2.5.dp else 2.dp,
    shape: RoundedCornerShape = RoundedLg,
    testTag: String = "",
    content: @Composable () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val cardOffset by animateDpAsState(
        targetValue = if (isPressed && onClick != null) 2.dp else 0.dp,
        animationSpec = tween(durationMillis = 60),
        label = "card_press"
    )

    val shadowHeight = 3.dp
    val shadowColor = if (isSelected) GamifyBlueShadow.copy(alpha = 0.5f) else Color(0xFFD6D6D6)

    Box(
        modifier = modifier
            .offset(y = cardOffset)
            .then(
                if (onClick != null) {
                    Modifier.clickable(
                        interactionSource = interactionSource,
                        indication = null,
                        onClick = onClick
                    )
                } else Modifier
            )
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier)
    ) {
        // Shadow base
        Box(
            modifier = Modifier
                .matchParentSize()
                .offset(y = shadowHeight)
                .clip(shape)
                .background(shadowColor)
        )

        // Main card body
        Box(
            modifier = Modifier
                .clip(shape)
                .background(backgroundColor)
                .border(borderWidth, borderColor, shape)
        ) {
            content()
        }
    }
}

/**
 * Sticky Top Header showing HP Hearts and Gem/Score count
 */
@Composable
fun GamifiedTopHeader(
    userHp: Int,
    maxUserHp: Int,
    score: Int,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(NeutralWhite)
            .border(width = 1.dp, color = NeutralBorder)
            .statusBarsPadding()
            .padding(horizontal = 14.dp, vertical = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // App Title / Level badge
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(6.dp),
            modifier = Modifier.weight(1f, fill = false)
        ) {
            Box(
                modifier = Modifier
                    .clip(RoundedMd)
                    .background(GamifyGreen)
                    .padding(horizontal = 6.dp, vertical = 3.dp)
            ) {
                Text(
                    text = "JLPT N3",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    softWrap = false
                )
            }
            Text(
                text = "NIHONGO QUEST",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NeutralText,
                    fontSize = 14.sp
                ),
                maxLines = 1,
                softWrap = false,
                overflow = TextOverflow.Ellipsis
            )
        }

        // Stats: HP & Gems
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // HP Heart Counter
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = "User HP",
                    tint = GamifyRed,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "$userHp",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = GamifyRed,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    maxLines = 1,
                    softWrap = false
                )
            }

            // Score Gems Counter
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(3.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Diamond,
                    contentDescription = "Score Gems",
                    tint = GamifyBlue,
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "$score",
                    style = MaterialTheme.typography.labelLarge.copy(
                        color = GamifyBlue,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    ),
                    maxLines = 1,
                    softWrap = false
                )
            }
        }
    }
}

/**
 * Fixed Bottom Navigation Bar with exactly 3 Gamified destinations:
 * 1. Lộ trình (Home / Pathway)
 * 2. Ôn tập SRS (Daily Review SM-2)
 * 3. Tiến độ (Profile / Weakness Stats)
 */
@Composable
fun GamifiedBottomNav(
    currentScreen: Screen,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .background(NeutralWhite)
            .border(width = 1.dp, color = NeutralBorder)
            .navigationBarsPadding()
            .padding(horizontal = 8.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        val isHomePathway = currentScreen == Screen.HOME ||
                currentScreen == Screen.LESSON_DETAIL ||
                currentScreen == Screen.STUDY_HUB ||
                currentScreen == Screen.INDUCTIVE_LEARNING ||
                currentScreen == Screen.CARD_BATTLE ||
                currentScreen == Screen.AI_CHAT

        BottomNavItem(
            title = "Lộ trình",
            icon = Icons.Default.School,
            isSelected = isHomePathway,
            activeColor = GamifyGreen,
            testTag = "nav_pathway",
            onClick = { onNavigate(Screen.HOME) }
        )

        BottomNavItem(
            title = "Ôn tập SRS",
            icon = Icons.Default.Psychology,
            isSelected = currentScreen == Screen.SRS_REVIEW,
            activeColor = GamifyBlue,
            testTag = "nav_srs_review",
            onClick = { onNavigate(Screen.SRS_REVIEW) }
        )

        BottomNavItem(
            title = "Tiến độ",
            icon = Icons.Default.BarChart,
            isSelected = currentScreen == Screen.PROFILE_STATS,
            activeColor = GamifyRed,
            testTag = "nav_profile_stats",
            onClick = { onNavigate(Screen.PROFILE_STATS) }
        )
    }
}

@Composable
private fun RowScope.BottomNavItem(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    testTag: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier
            .weight(1f)
            .clip(RoundedMd)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(vertical = 4.dp, horizontal = 2.dp)
            .testTag(testTag)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(RoundedFull)
                .background(if (isSelected) activeColor.copy(alpha = 0.15f) else Color.Transparent),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) activeColor else NeutralSubtext,
                modifier = Modifier.size(22.dp)
            )
        }
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                color = if (isSelected) activeColor else NeutralSubtext,
                fontSize = 10.sp
            ),
            maxLines = 1,
            softWrap = false,
            overflow = TextOverflow.Ellipsis
        )
    }
}

/**
 * Rounded-full Progress Bar with 3D gloss line
 */
@Composable
fun GamifiedProgressBar(
    progress: Float,
    modifier: Modifier = Modifier,
    fillColor: Color = GamifyGreen,
    trackColor: Color = NeutralBorder,
    height: Dp = 14.dp
) {
    val clampedProgress = progress.coerceIn(0f, 1f)

    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(height)
            .clip(RoundedFull)
            .background(trackColor)
    ) {
        if (clampedProgress > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxWidth(clampedProgress)
                    .height(height)
                    .clip(RoundedFull)
                    .background(fillColor)
            ) {
                // Subtle top highlight stripe for 3D gloss
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(height / 3)
                        .background(Color.White.copy(alpha = 0.35f))
                )
            }
        }
    }
}
