package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.layout.defaultMinSize
import com.example.data.model.MatchPairItem
import com.example.data.model.VocabularyItem
import com.example.ui.components.Gamified3DButton
import com.example.ui.components.Gamified3DCard
import com.example.ui.components.GamifiedProgressBar
import com.example.ui.state.NihongoUiState
import com.example.ui.state.NihongoViewModel
import com.example.ui.theme.GamifyBlue
import com.example.ui.theme.GamifyBlueDark
import com.example.ui.theme.GamifyBlueLight
import com.example.ui.theme.GamifyBlueShadow
import com.example.ui.theme.GamifyGreen
import com.example.ui.theme.GamifyGreenDark
import com.example.ui.theme.GamifyGreenLight
import com.example.ui.theme.GamifyGreenShadow
import com.example.ui.theme.GamifyRed
import com.example.ui.theme.GamifyRedDark
import com.example.ui.theme.GamifyRedLight
import com.example.ui.theme.GamifyRedShadow
import com.example.ui.theme.GamifyYellow
import com.example.ui.theme.GamifyYellowLight
import com.example.ui.theme.LoraFontFamily
import com.example.ui.theme.NeutralBorder
import com.example.ui.theme.NeutralCard
import com.example.ui.theme.NeutralSubtext
import com.example.ui.theme.NeutralText
import com.example.ui.theme.NeutralWhite
import com.example.ui.theme.RoundedFull
import com.example.ui.theme.RoundedLg
import com.example.ui.theme.RoundedMd
import com.example.ui.theme.RoundedSm
import com.example.util.JapaneseAudioSpeakerButton
import com.example.util.LocalJapaneseTts

@Composable
fun CardBattleScreen(
    uiState: NihongoUiState,
    viewModel: NihongoViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.goBack()
    }

    val tts = LocalJapaneseTts.current
    val scrollState = rememberScrollState()
    val currentQuestion = uiState.activePveQuestion
    var aiExplanationText by remember { mutableStateOf<String?>(null) }
    var isLoadingAi by remember { mutableStateOf(false) }

    val isMatchMode = uiState.matchRounds.isNotEmpty()
    var prevMatchedCount by remember { mutableIntStateOf(0) }

    LaunchedEffect(uiState.matchedPairIds.size, uiState.currentMatchRoundIndex) {
        if (isMatchMode && uiState.matchedPairIds.size > prevMatchedCount) {
            tts.playHitSound()
        }
        prevMatchedCount = uiState.matchedPairIds.size
    }

    LaunchedEffect(uiState.isUserHit) {
        if (isMatchMode && uiState.isUserHit) {
            tts.playErrorSound()
        }
    }

    // Flash effect alpha animation: khi khớp từ, nháy màn hình nhẹ
    val flashAlpha by animateFloatAsState(
        targetValue = if (uiState.isScreenFlashing) 0.45f else 0f,
        animationSpec = tween(durationMillis = 180),
        label = "screen_flash"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralWhite)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Top App Bar
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    IconButton(
                        onClick = { viewModel.goBack() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedMd)
                            .background(NeutralCard)
                            .border(1.5.dp, NeutralBorder, RoundedMd)
                            .testTag("btn_back_home_battle")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = NeutralText
                        )
                    }
                    Column(modifier = Modifier.padding(start = 10.dp)) {
                        Text(
                            text = if (isMatchMode) "GHÉP THẺ NHẬT ⇄ VIỆT" else "CARD BATTLE (PvE)",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeutralText,
                                fontSize = 16.sp
                            )
                        )
                        if (isMatchMode) {
                            Text(
                                text = "Vòng ${uiState.currentMatchRoundIndex + 1}/${uiState.matchRounds.size} • Combo x${uiState.matchCombo}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GamifyBlueDark,
                                    fontWeight = FontWeight.SemiBold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    if (!isMatchMode) {
                        IconButton(
                            onClick = {
                                isLoadingAi = true
                                viewModel.requestAiExplanationForCurrentQuestion { explanation ->
                                    aiExplanationText = explanation
                                    isLoadingAi = false
                                }
                            },
                            modifier = Modifier
                                .size(40.dp)
                                .clip(RoundedMd)
                                .background(GamifyBlueLight)
                                .border(1.5.dp, GamifyBlue, RoundedMd)
                                .testTag("btn_ai_studio_help")
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = "Google AI Studio",
                                tint = GamifyBlueDark
                            )
                        }
                    }

                    IconButton(
                        onClick = { viewModel.resetPveBattle() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedMd)
                            .background(NeutralCard)
                            .border(1.5.dp, NeutralBorder, RoundedMd)
                            .testTag("btn_reset_battle")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Chơi lại",
                            tint = NeutralText
                        )
                    }
                }
            }

            // Boss & User HP Cards
            Gamified3DCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("boss_hp_card"),
                backgroundColor = Color(0xFF2C3440),
                borderColor = Color(0xFF1E232B),
                borderWidth = 2.5.dp
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Shield,
                                contentDescription = "Boss",
                                tint = GamifyRed,
                                modifier = Modifier.size(18.dp)
                            )
                            Text(
                                text = "👹 ${uiState.bossName}",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 14.sp
                                )
                            )
                        }
                        Text(
                            text = "${uiState.bossHp} / ${uiState.maxBossHp} HP",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )
                        )
                    }

                    GamifiedProgressBar(
                        progress = (uiState.bossHp.toFloat() / uiState.maxBossHp.coerceAtLeast(1)).coerceIn(0f, 1f),
                        fillColor = GamifyRed,
                        trackColor = Color(0xFF404854),
                        height = 10.dp
                    )
                }
            }

            Gamified3DCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("user_hp_card"),
                backgroundColor = if (uiState.isUserHit) GamifyRedLight else NeutralWhite,
                borderColor = if (uiState.isUserHit) GamifyRed else NeutralBorder,
                borderWidth = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(10.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Favorite,
                                contentDescription = "User HP",
                                tint = GamifyRed,
                                modifier = Modifier.size(16.dp)
                            )
                            Text(
                                text = "NGƯỜI CHƠI (BẠN) • ${uiState.score} PTS",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeutralText
                                )
                            )
                        }
                        Text(
                            text = "${uiState.userHp} / ${uiState.maxUserHp} HP",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GamifyRed
                            )
                        )
                    }

                    GamifiedProgressBar(
                        progress = (uiState.userHp.toFloat() / uiState.maxUserHp.coerceAtLeast(1)).coerceIn(0f, 1f),
                        fillColor = GamifyGreen,
                        trackColor = NeutralBorder,
                        height = 10.dp
                    )
                }
            }

            // Feedback Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedMd)
                    .background(if (uiState.isUserHit) GamifyRedLight else GamifyBlueLight)
                    .border(1.dp, if (uiState.isUserHit) GamifyRed else GamifyBlue.copy(alpha = 0.4f), RoundedMd)
                    .padding(horizontal = 12.dp, vertical = 8.dp)
            ) {
                Text(
                    text = uiState.pveCombatFeedback,
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = if (uiState.isUserHit) GamifyRedDark else GamifyBlueDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 12.sp
                    )
                )
            }

            // Result Victory/Defeat Banner
            if (uiState.isVictory || uiState.isDefeat) {
                Gamified3DCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("game_result_card"),
                    backgroundColor = if (uiState.isVictory) GamifyGreenLight else GamifyRedLight,
                    borderColor = if (uiState.isVictory) GamifyGreen else GamifyRed,
                    borderWidth = 3.dp
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Text(
                            text = if (uiState.isVictory) "🏆 CHIẾN THẮNG! HOÀN THÀNH GHÉP THẺ!" else "💀 THẤT BẠI - HẾT MÁU!",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (uiState.isVictory) GamifyGreenDark else GamifyRedDark
                            ),
                            textAlign = TextAlign.Center
                        )
                        Text(
                            text = if (uiState.isVictory)
                                "Chúc mừng bạn đã nối đúng tất cả cặp Tiếng Nhật ⇄ Tiếng Việt! Tiến trình bài học đã được lưu."
                            else
                                "Bạn đã ghép sai nhiều lần. Nhấn nút để thử lại!",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = LoraFontFamily,
                                color = NeutralText,
                                textAlign = TextAlign.Center
                            )
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Gamified3DButton(
                                text = "Quay lại Bài học",
                                onClick = { viewModel.goBack() },
                                backgroundColor = GamifyBlue,
                                shadowColor = GamifyBlueShadow,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_match_back_to_lesson"
                            )
                            Gamified3DButton(
                                text = "Chơi lại",
                                onClick = { viewModel.resetPveBattle() },
                                backgroundColor = if (uiState.isVictory) GamifyGreen else GamifyRed,
                                shadowColor = if (uiState.isVictory) GamifyGreenShadow else GamifyRedShadow,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_pve_replay"
                            )
                        }
                    }
                }
            }

            if (isMatchMode) {
                // ========================================================
                // CHẾ ĐỘ GHÉP THẺ NHẬT ⇄ VIỆT (2 CỘT TRÁI - PHẢI)
                // ========================================================
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Text(
                        text = "🇯🇵 TIẾNG NHẬT",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = GamifyBlueDark,
                            fontSize = 12.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedSm)
                            .background(GamifyBlueLight)
                            .padding(vertical = 6.dp)
                    )
                    Text(
                        text = "🇻🇳 TIẾNG VIỆT",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = GamifyGreenDark,
                            fontSize = 12.sp
                        ),
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                            .clip(RoundedSm)
                            .background(GamifyGreenLight)
                            .padding(vertical = 6.dp)
                    )
                }

                val leftCards = uiState.leftJapaneseCards
                val rightCards = uiState.rightVietnameseCards
                val rowCount = maxOf(leftCards.size, rightCards.size)

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 24.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    for (i in 0 until rowCount) {
                        val jpItem = leftCards.getOrNull(i)
                        val vnItem = rightCards.getOrNull(i)
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            if (jpItem != null) {
                                MatchJapaneseCard(
                                    item = jpItem,
                                    isSelected = uiState.selectedMatchJapaneseId == jpItem.id,
                                    isMatched = jpItem.id in uiState.matchedPairIds,
                                    isError = uiState.matchErrorPairIds?.first == jpItem.id,
                                    enabled = !uiState.isVictory && !uiState.isDefeat && jpItem.id !in uiState.matchedPairIds,
                                    onClick = {
                                        tts.speak(jpItem.japanese)
                                        viewModel.selectMatchJapaneseCard(jpItem.id)
                                    },
                                    onSpeakClick = { tts.speak(jpItem.japanese) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }

                            if (vnItem != null) {
                                MatchVietnameseCard(
                                    item = vnItem,
                                    isSelected = uiState.selectedMatchVietnameseId == vnItem.id,
                                    isMatched = vnItem.id in uiState.matchedPairIds,
                                    isError = uiState.matchErrorPairIds?.second == vnItem.id,
                                    enabled = !uiState.isVictory && !uiState.isDefeat && vnItem.id !in uiState.matchedPairIds,
                                    onClick = { viewModel.selectMatchVietnameseCard(vnItem.id) },
                                    modifier = Modifier.weight(1f)
                                )
                            } else {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            } else {
                // Fallback Legacy PvE Card Battle mode
                Gamified3DCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("sentence_challenge_card"),
                    backgroundColor = NeutralWhite,
                    borderColor = NeutralBorder,
                    borderWidth = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(18.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        val displaySentence = if (uiState.filledBlankWord != null) {
                            currentQuestion.sentenceWithBlank.replace("＿＿＿", uiState.filledBlankWord!!)
                        } else {
                            currentQuestion.sentenceWithBlank
                        }
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = displaySentence,
                                style = MaterialTheme.typography.headlineMedium.copy(
                                    color = NeutralText,
                                    fontSize = 20.sp,
                                    fontWeight = FontWeight.Bold,
                                    lineHeight = 30.sp
                                ),
                                modifier = Modifier
                                    .weight(1f)
                                    .testTag("japanese_sentence_text")
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            JapaneseAudioSpeakerButton(
                                textToSpeak = displaySentence,
                                size = 40.dp,
                                iconSize = 20.dp,
                                primaryColor = GamifyBlue,
                                backgroundColor = GamifyBlueLight,
                                testTag = "btn_tts_read_battle_sentence"
                            )
                        }
                    }
                }

                if (isLoadingAi || aiExplanationText != null) {
                    Gamified3DCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = GamifyBlueLight,
                        borderColor = GamifyBlue
                    ) {
                        Text(
                            text = if (isLoadingAi) "Đang phân tích cùng Gemini..." else (aiExplanationText ?: ""),
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = LoraFontFamily,
                                color = NeutralText
                            ),
                            modifier = Modifier.padding(14.dp)
                        )
                    }
                }

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 20.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    currentQuestion.options.forEachIndexed { index, vocab ->
                        GamifiedPveCard(
                            vocabulary = vocab,
                            enabled = !uiState.isVictory && !uiState.isDefeat && uiState.filledBlankWord == null,
                            testTag = "pve_card_$index",
                            onClick = {
                                val isMatch = vocab.kanji == currentQuestion.missingWord
                                tts.speak(vocab.kanji)
                                viewModel.playPveCard(vocab)
                                if (isMatch) tts.playHitSound() else tts.playErrorSound()
                            },
                            onAudioClick = { tts.speak(vocab.kanji) },
                            modifier = Modifier
                                .weight(1f)
                                .padding(horizontal = 4.dp)
                        )
                    }
                }
            }
        }

        // Screen Flash animation on correct match
        if (flashAlpha > 0f) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.White.copy(alpha = flashAlpha))
            )
        }
    }
}

@Composable
private fun MatchJapaneseCard(
    item: MatchPairItem,
    isSelected: Boolean,
    isMatched: Boolean,
    isError: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    onSpeakClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isMatched -> GamifyGreenLight
        isError -> GamifyRedLight
        isSelected -> GamifyBlueLight
        else -> NeutralWhite
    }
    val borderColor = when {
        isMatched -> GamifyGreen
        isError -> GamifyRed
        isSelected -> GamifyBlue
        else -> NeutralBorder
    }

    Gamified3DCard(
        modifier = modifier
            .defaultMinSize(minHeight = 76.dp)
            .testTag("match_jp_${item.id}"),
        backgroundColor = bgColor,
        borderColor = borderColor,
        borderWidth = if (isSelected || isMatched || isError) 2.5.dp else 2.dp,
        shape = RoundedMd,
        onClick = if (enabled) onClick else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(2.dp)
            ) {
                Text(
                    text = item.japanese,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isMatched) GamifyGreenDark else NeutralText,
                        fontSize = 14.5.sp
                    ),
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
                if (item.subJapanese.isNotBlank()) {
                    Text(
                        text = item.subJapanese,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeutralSubtext,
                            fontSize = 10.5.sp
                        ),
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }
            Spacer(modifier = Modifier.width(6.dp))
            if (isMatched) {
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Đã ghép",
                    tint = GamifyGreenDark,
                    modifier = Modifier.size(18.dp)
                )
            } else {
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(CircleShape)
                        .background(GamifyBlueLight)
                        .clickable { onSpeakClick() },
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = "Nghe ${item.japanese}",
                        tint = GamifyBlueDark,
                        modifier = Modifier.size(15.dp)
                    )
                }
            }
        }
    }
}

@Composable
private fun MatchVietnameseCard(
    item: MatchPairItem,
    isSelected: Boolean,
    isMatched: Boolean,
    isError: Boolean,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val bgColor = when {
        isMatched -> GamifyGreenLight
        isError -> GamifyRedLight
        isSelected -> GamifyYellowLight
        else -> NeutralWhite
    }
    val borderColor = when {
        isMatched -> GamifyGreen
        isError -> GamifyRed
        isSelected -> GamifyYellow
        else -> NeutralBorder
    }

    Gamified3DCard(
        modifier = modifier
            .defaultMinSize(minHeight = 76.dp)
            .testTag("match_vn_${item.id}"),
        backgroundColor = bgColor,
        borderColor = borderColor,
        borderWidth = if (isSelected || isMatched || isError) 2.5.dp else 2.dp,
        shape = RoundedMd,
        onClick = if (enabled) onClick else null
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 10.dp, vertical = 10.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(
                text = item.vietnamese,
                style = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = LoraFontFamily,
                    fontWeight = if (isSelected || isMatched) FontWeight.Bold else FontWeight.Medium,
                    color = if (isMatched) GamifyGreenDark else NeutralText,
                    fontSize = 12.sp,
                    lineHeight = 16.sp
                ),
                maxLines = 4,
                overflow = TextOverflow.Ellipsis,
                modifier = Modifier.weight(1f)
            )
            if (isMatched) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Check,
                    contentDescription = "Đã ghép",
                    tint = GamifyGreenDark,
                    modifier = Modifier.size(18.dp)
                )
            } else if (isError) {
                Spacer(modifier = Modifier.width(4.dp))
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Sai cặp",
                    tint = GamifyRed,
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}

/**
 * 3D Gamified Card for PvE
 * - 2:3 aspect ratio
 * - White background with 3D shadow
 * - Translate-y upwards on press/hover (-14.dp)
 */
@Composable
private fun GamifiedPveCard(
    vocabulary: VocabularyItem,
    enabled: Boolean,
    testTag: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onAudioClick: (() -> Unit)? = null
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val isElevated = isHovered || isPressed

    val offsetY by animateDpAsState(
        targetValue = if (isElevated) (-14).dp else 0.dp,
        animationSpec = tween(durationMillis = 140),
        label = "card_elevation_y"
    )

    Box(
        modifier = modifier
            .aspectRatio(2f / 3f)
            .offset(y = offsetY)
            .clip(RoundedMd)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag(testTag)
    ) {
        // Shadow base
        Box(
            modifier = Modifier
                .fillMaxSize()
                .offset(y = 4.dp)
                .clip(RoundedMd)
                .background(Color(0xFFD6D6D6))
        )

        // Card body
        Box(
            modifier = Modifier
                .fillMaxSize()
                .clip(RoundedMd)
                .background(NeutralWhite)
                .border(2.dp, NeutralBorder, RoundedMd)
                .padding(6.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                // Header badge & mini audio speaker
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedSm)
                            .background(GamifyBlueLight)
                            .padding(horizontal = 4.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "N3",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontSize = 9.sp,
                                color = GamifyBlueDark,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .size(20.dp)
                            .clip(CircleShape)
                            .background(GamifyBlueLight)
                            .clickable { onAudioClick?.invoke() }
                            .testTag("btn_audio_${vocabulary.kanji}"),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                            contentDescription = "Phát âm ${vocabulary.kanji}",
                            tint = GamifyBlueDark,
                            modifier = Modifier.size(12.dp)
                        )
                    }
                }

                // Center: Kanji & Hiragana
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = vocabulary.hiragana,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontSize = 10.sp,
                            color = NeutralSubtext
                        )
                    )
                    Text(
                        text = vocabulary.kanji,
                        style = MaterialTheme.typography.headlineMedium.copy(
                            color = NeutralText,
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }

                // Footer: Word tag (meaning shown after match)
                Text(
                    text = if (enabled) "${vocabulary.level} 語彙" else vocabulary.meaning.take(12),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontFamily = LoraFontFamily,
                        fontSize = 9.sp,
                        color = NeutralSubtext,
                        textAlign = TextAlign.Center
                    ),
                    maxLines = 1
                )
            }
        }
    }
}
