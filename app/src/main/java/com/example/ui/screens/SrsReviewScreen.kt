package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
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
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.FitnessCenter
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.SrsItemType
import com.example.data.model.SrsRating
import com.example.data.srs.Sm2Algorithm
import com.example.ui.components.Gamified3DButton
import com.example.ui.components.Gamified3DCard
import com.example.ui.components.GamifiedProgressBar
import com.example.ui.state.NihongoUiState
import com.example.ui.state.NihongoViewModel
import com.example.ui.state.Screen
import com.example.ui.theme.GamifyBlue
import com.example.ui.theme.GamifyBlueLight
import com.example.ui.theme.GamifyBlueShadow
import com.example.ui.theme.GamifyGreen
import com.example.ui.theme.GamifyGreenDark
import com.example.ui.theme.GamifyGreenLight
import com.example.ui.theme.GamifyGreenShadow
import com.example.ui.theme.GamifyRed
import com.example.ui.theme.GamifyRedLight
import com.example.ui.theme.GamifyRedShadow
import com.example.ui.theme.GamifyYellow
import com.example.ui.theme.GamifyYellowLight
import com.example.ui.theme.GamifyYellowShadow
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

@Composable
fun SrsReviewScreen(
    uiState: NihongoUiState,
    viewModel: NihongoViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.goBack()
    }

    val cards = uiState.srsDueCards
    val currentIndex = uiState.currentSrsIndex
    val isCompleted = cards.isEmpty() || currentIndex >= cards.size || uiState.isSessionFinished
    val currentPair = if (!isCompleted && currentIndex in cards.indices) cards[currentIndex] else null

    val scrollState = rememberScrollState()

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralWhite)
            .padding(horizontal = 18.dp, vertical = 12.dp)
            .verticalScroll(scrollState),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // 1. Header Bar: Back (nếu từ bài học) + Tiêu đề + Huy hiệu Offline SM-2
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                modifier = Modifier.weight(1f)
            ) {
                if (uiState.previousScreen == Screen.LESSON_DETAIL || uiState.previousScreen == Screen.STUDY_HUB) {
                    IconButton(
                        onClick = { viewModel.goBack() },
                        modifier = Modifier
                            .size(40.dp)
                            .clip(RoundedMd)
                            .background(NeutralCard)
                            .border(1.5.dp, NeutralBorder, RoundedMd)
                            .testTag("btn_srs_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại",
                            tint = NeutralText
                        )
                    }
                }
                Column {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "ÔN TẬP SRS (SM-2)",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeutralText,
                                fontSize = 18.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedSm)
                                .background(GamifyBlueLight)
                                .border(1.dp, GamifyBlue, RoundedSm)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = "OFFLINE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GamifyBlue,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                    Text(
                        text = "Chọn 'Đã thuộc' để lưu tiến trình hoàn thành bài học ngay",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeutralSubtext,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // Streak Flame Badge
            Box(
                modifier = Modifier
                    .clip(RoundedFull)
                    .background(GamifyYellowLight)
                    .border(1.5.dp, GamifyYellow, RoundedFull)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "🔥 ${uiState.reviewedCountToday} hôm nay",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = Color(0xFFB27B00),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }
        }

        // 2. Horizontal Filter Tabs (Tất cả, Từ vựng, Ngữ pháp, Kanji)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val filters: List<Pair<String, SrsItemType?>> = listOf(
                "Tất cả" to null,
                "Từ vựng" to SrsItemType.VOCABULARY,
                "Ngữ pháp" to SrsItemType.GRAMMAR,
                "Kanji" to SrsItemType.KANJI
            )

            items(filters.size) { i ->
                val (title, type) = filters[i]
                val isSelected = uiState.selectedSrsFilter == type
                val chipColor = when (type) {
                    SrsItemType.VOCABULARY -> GamifyBlue
                    SrsItemType.GRAMMAR -> GamifyGreen
                    SrsItemType.KANJI -> GamifyRed
                    null -> GamifyGreen
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedFull)
                        .background(if (isSelected) chipColor else NeutralCard)
                        .border(
                            1.5.dp,
                            if (isSelected) chipColor else NeutralBorder,
                            RoundedFull
                        )
                        .clickable { viewModel.selectSrsFilter(type) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("srs_filter_${title}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = title,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else NeutralText,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        if (isCompleted || currentPair == null) {
            // ==========================================
            // MÀN HÌNH HOÀN THÀNH PHIÊN HỌC NGẮN (5-10 PHÚT)
            // ==========================================
            val isWeakSession = uiState.isWeakPointsSession
            val sessionCompleted = uiState.sessionCompletedCount.coerceAtLeast(uiState.reviewedCountToday)
            val sessionAccuracy = if (uiState.sessionCompletedCount > 0) {
                ((uiState.sessionCorrectCount.toFloat() / uiState.sessionCompletedCount.toFloat()) * 100).toInt()
            } else {
                95
            }

            Gamified3DCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 12.dp)
                    .testTag("srs_completion_card"),
                backgroundColor = Color(0xFFF6FFF0),
                borderColor = if (isWeakSession) GamifyRed else GamifyGreen,
                borderWidth = 2.5.dp
            ) {
                Column(
                    modifier = Modifier.padding(20.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(70.dp)
                            .clip(CircleShape)
                            .background(if (isWeakSession) GamifyRedLight else GamifyGreenLight)
                            .border(3.dp, if (isWeakSession) GamifyRed else GamifyGreen, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = if (isWeakSession) "⚡" else "🎉", fontSize = 34.sp)
                    }

                    Text(
                        text = if (isWeakSession) "HOÀN THÀNH ÔN TẬP ĐIỂM YẾU!" else "HOÀN THÀNH PHIÊN 5-10 PHÚT!",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isWeakSession) GamifyRed else GamifyGreenDark,
                            fontSize = 18.sp,
                            textAlign = TextAlign.Center
                        )
                    )

                    Text(
                        text = if (isWeakSession)
                            "Bạn đã củng cố thành công các điểm ngữ pháp & từ vựng hay bị quên. Trí nhớ dài hạn đã được phục hồi!"
                        else
                            "Tuyệt vời! Phiên học ngắn đã hoàn thành trọn vẹn. Thuật toán SuperMemo-2 đã cập nhật khoảng thời gian ngắt quãng tiếp theo.",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            textAlign = TextAlign.Center,
                            color = NeutralText,
                            fontSize = 12.5.sp,
                            lineHeight = 18.sp
                        )
                    )

                    // Session Performance Stats Grid
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedMd)
                            .background(NeutralWhite)
                            .border(1.dp, NeutralBorder, RoundedMd)
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceAround
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Đã hoàn thành", style = MaterialTheme.typography.labelSmall.copy(color = NeutralSubtext, fontSize = 10.sp))
                            Text(
                                text = "$sessionCompleted câu",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeutralText,
                                    fontSize = 15.sp
                                )
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Độ chính xác", style = MaterialTheme.typography.labelSmall.copy(color = NeutralSubtext, fontSize = 10.sp))
                            Text(
                                text = "$sessionAccuracy%",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GamifyGreenDark,
                                    fontSize = 15.sp
                                )
                            )
                        }
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Text(text = "Chuỗi học", style = MaterialTheme.typography.labelSmall.copy(color = NeutralSubtext, fontSize = 10.sp))
                            Text(
                                text = "🔥 +1 ngày",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFFD97706),
                                    fontSize = 15.sp
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(2.dp))

                    if (uiState.previousScreen == Screen.LESSON_DETAIL || uiState.previousScreen == Screen.STUDY_HUB) {
                        Gamified3DButton(
                            text = "Quay lại Bài học (Lưu tiến trình)",
                            icon = Icons.Default.Check,
                            onClick = { viewModel.goBack() },
                            backgroundColor = GamifyGreen,
                            shadowColor = GamifyGreenShadow,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "btn_srs_back_to_lesson"
                        )
                    }

                    Gamified3DButton(
                        text = "Bắt đầu phiên học mới",
                        icon = Icons.Default.Refresh,
                        onClick = { viewModel.restartSession() },
                        backgroundColor = GamifyBlue,
                        shadowColor = GamifyBlueShadow,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_srs_load_more"
                    )

                    Gamified3DButton(
                        text = "Xem thống kê tiến độ (Dashboard)",
                        icon = Icons.Default.AutoAwesome,
                        onClick = { viewModel.navigateTo(Screen.PROFILE_STATS) },
                        backgroundColor = GamifyYellow,
                        shadowColor = GamifyYellowShadow,
                        textColor = NeutralText,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_srs_view_stats"
                    )
                }
            }
        } else {
            val (card, progress) = currentPair
            val isFlipped = uiState.isSrsCardFlipped
            val isWeakSession = uiState.isWeakPointsSession
            val sessionTotal = uiState.sessionTotalCount.coerceAtLeast(1)
            val currentSessionIndex = (currentIndex % sessionTotal) + 1

            // 3. Tiến độ phiên học hiện tại (5-10 phút)
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .clip(RoundedSm)
                                .background(if (isWeakSession) GamifyRedLight else GamifyBlueLight)
                                .border(1.dp, if (isWeakSession) GamifyRed else GamifyBlue, RoundedSm)
                                .padding(horizontal = 6.dp, vertical = 2.dp)
                        ) {
                            Text(
                                text = if (isWeakSession) "ÔN ĐIỂM YẾU" else "PHIÊN 5-10 PHÚT",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isWeakSession) GamifyRed else GamifyBlue,
                                    fontSize = 9.sp
                                )
                            )
                        }
                        Text(
                            text = "CÂU $currentSessionIndex / $sessionTotal",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeutralText,
                                fontSize = 11.5.sp
                            )
                        )
                    }

                    Text(
                        text = "Lặp lại: ${progress.repetitions} • EF: ${progress.easeFactor}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = GamifyBlue,
                            fontSize = 10.5.sp
                        )
                    )
                }

                GamifiedProgressBar(
                    progress = (currentSessionIndex.toFloat() / sessionTotal.toFloat()).coerceIn(0.05f, 1f),
                    fillColor = if (isWeakSession) GamifyRed else GamifyGreen,
                    height = 9.dp
                )
            }

            // 4. FLASHCARD 3D TACTILE (Chạm để lật thẻ)
            val cardBorderColor = when (card.type) {
                SrsItemType.VOCABULARY -> GamifyBlue
                SrsItemType.GRAMMAR -> GamifyGreen
                SrsItemType.KANJI -> GamifyRed
            }

            Gamified3DCard(
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { viewModel.flipSrsCard() }
                    .testTag("srs_flashcard_main"),
                backgroundColor = NeutralWhite,
                borderColor = cardBorderColor,
                borderWidth = 2.5.dp
            ) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(20.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header thẻ: Loại thẻ, Bài học, Cấp độ N3
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedSm)
                                    .background(cardBorderColor)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = card.type.title.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }

                            Box(
                                modifier = Modifier
                                    .clip(RoundedSm)
                                    .background(NeutralCard)
                                    .border(1.dp, NeutralBorder, RoundedSm)
                                    .padding(horizontal = 8.dp, vertical = 3.dp)
                            ) {
                                Text(
                                    text = card.lessonTitle,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeutralText,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        // Giai đoạn ghi nhớ (Stage)
                        Text(
                            text = progress.stage.label,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = when (progress.stage) {
                                    com.example.data.model.SrsStage.MASTERED -> GamifyGreenDark
                                    com.example.data.model.SrsStage.REVIEW -> GamifyBlue
                                    com.example.data.model.SrsStage.LEARNING -> GamifyYellow
                                    com.example.data.model.SrsStage.NEW -> NeutralSubtext
                                },
                                fontSize = 11.sp
                            )
                        )
                    }

                    // ==========================================
                    // MẶT TRƯỚC (FRONT): Từ / Ngữ pháp / Kanji
                    // ==========================================
                    Column(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = card.frontText,
                            style = MaterialTheme.typography.displayLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeutralText,
                                fontSize = if (card.type == SrsItemType.KANJI) 56.sp else 30.sp,
                                textAlign = TextAlign.Center
                            )
                        )

                        // Nút loa phát âm tiếng Nhật
                        JapaneseAudioSpeakerButton(
                            textToSpeak = card.frontText,
                            size = 46.dp,
                            iconSize = 22.dp,
                            primaryColor = cardBorderColor,
                            backgroundColor = cardBorderColor.copy(alpha = 0.15f),
                            testTag = "btn_tts_srs_front"
                        )
                    }

                    // Gợi ý lật thẻ
                    if (!isFlipped) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedMd)
                                .background(NeutralCard)
                                .border(1.dp, NeutralBorder, RoundedMd)
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Visibility,
                                    contentDescription = "Flip Hint",
                                    tint = NeutralSubtext,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Chạm vào thẻ để lật mặt sau & xem nghĩa",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeutralSubtext,
                                        fontWeight = FontWeight.Medium
                                    )
                                )
                            }
                        }
                    }

                    // ==========================================
                    // MẶT SAU (BACK): Đọc, Nghĩa, Ví dụ
                    // ==========================================
                    AnimatedVisibility(visible = isFlipped) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedMd)
                                .background(NeutralCard)
                                .border(1.dp, NeutralBorder, RoundedMd)
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            // Cách đọc Hiragana / On-Kun
                            if (card.readingText.isNotBlank()) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = "Cách đọc: ",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = NeutralSubtext
                                        )
                                    )
                                    Text(
                                        text = card.readingText,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GamifyBlue
                                        )
                                    )
                                }
                            }

                            // Nghĩa tiếng Việt
                            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                Text(
                                    text = "Ý nghĩa:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NeutralSubtext
                                    )
                                )
                                Text(
                                    text = card.meaningText,
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NeutralText,
                                        lineHeight = 22.sp
                                    )
                                )
                            }

                            // Cấu trúc / Romaji / Ghi chú
                            if (card.structureOrRomaji.isNotBlank()) {
                                Text(
                                    text = card.structureOrRomaji,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = GamifyGreenDark,
                                        fontWeight = FontWeight.SemiBold
                                    )
                                )
                            }

                            // Câu ví dụ minh họa kèm TTS
                            if (card.exampleJp.isNotBlank()) {
                                Box(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clip(RoundedSm)
                                        .background(NeutralWhite)
                                        .border(1.dp, NeutralBorder, RoundedSm)
                                        .padding(10.dp)
                                ) {
                                    Row(
                                        modifier = Modifier.fillMaxWidth(),
                                        horizontalArrangement = Arrangement.SpaceBetween,
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Column(modifier = Modifier.weight(1f)) {
                                            Text(
                                                text = card.exampleJp,
                                                style = MaterialTheme.typography.bodyMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = NeutralText
                                                )
                                            )
                                            if (card.exampleVn.isNotBlank()) {
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = card.exampleVn,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontFamily = LoraFontFamily,
                                                        color = NeutralSubtext
                                                    )
                                                )
                                            }
                                        }

                                        Spacer(modifier = Modifier.width(8.dp))

                                        JapaneseAudioSpeakerButton(
                                            textToSpeak = card.exampleJp,
                                            size = 36.dp,
                                            iconSize = 18.dp,
                                            primaryColor = cardBorderColor,
                                            backgroundColor = cardBorderColor.copy(alpha = 0.15f),
                                            testTag = "btn_tts_srs_example"
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // ==========================================
            // CÁC NÚT ĐIỀU KHIỂN & ĐÁNH GIÁ (SM-2)
            // ==========================================
            if (!isFlipped) {
                // Nút "Hiện Đáp Án / Lật Thẻ"
                Gamified3DButton(
                    text = "Lật Thẻ (Hiện Đáp Án)",
                    icon = Icons.Default.Visibility,
                    onClick = { viewModel.flipSrsCard() },
                    backgroundColor = GamifyBlue,
                    shadowColor = GamifyBlueShadow,
                    height = 52.dp,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_srs_flip"
                )
            } else {
                // 4 Nút đánh giá chuẩn SM-2: Quên, Khó, Tốt, Dễ
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    Text(
                        text = "ĐÁNH GIÁ MỨC ĐỘ GHI NHỚ (THUẬT TOÁN SM-2):",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeutralSubtext,
                            letterSpacing = 0.5.sp
                        )
                    )

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        // 1. Quên (Again)
                        SrsRatingButton(
                            title = "Quên",
                            intervalText = Sm2Algorithm.previewIntervalText(progress, SrsRating.AGAIN),
                            rating = SrsRating.AGAIN,
                            backgroundColor = GamifyRed,
                            shadowColor = GamifyRedShadow,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_srs_again",
                            onClick = { viewModel.rateSrsCard(SrsRating.AGAIN) }
                        )

                        // 2. Khó (Hard)
                        SrsRatingButton(
                            title = "Khó",
                            intervalText = Sm2Algorithm.previewIntervalText(progress, SrsRating.HARD),
                            rating = SrsRating.HARD,
                            backgroundColor = GamifyYellow,
                            shadowColor = GamifyYellowShadow,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_srs_hard",
                            onClick = { viewModel.rateSrsCard(SrsRating.HARD) }
                        )

                        // 3. Tốt (Good)
                        SrsRatingButton(
                            title = "Tốt",
                            intervalText = Sm2Algorithm.previewIntervalText(progress, SrsRating.GOOD),
                            rating = SrsRating.GOOD,
                            backgroundColor = GamifyBlue,
                            shadowColor = GamifyBlueShadow,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_srs_good",
                            onClick = { viewModel.rateSrsCard(SrsRating.GOOD) }
                        )

                        // 4. Đã thuộc (Easy / Mastered)
                        SrsRatingButton(
                            title = "Đã thuộc",
                            intervalText = Sm2Algorithm.previewIntervalText(progress, SrsRating.EASY),
                            rating = SrsRating.EASY,
                            backgroundColor = GamifyGreen,
                            shadowColor = GamifyGreenShadow,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_srs_easy",
                            onClick = { viewModel.rateSrsCard(SrsRating.EASY) }
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SrsRatingButton(
    title: String,
    intervalText: String,
    rating: SrsRating,
    backgroundColor: Color,
    shadowColor: Color,
    modifier: Modifier = Modifier,
    testTag: String,
    onClick: () -> Unit
) {
    Gamified3DCard(
        modifier = modifier
            .testTag(testTag),
        backgroundColor = backgroundColor,
        borderColor = shadowColor,
        borderWidth = 2.dp,
        shape = RoundedMd,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color.White,
                    fontSize = 13.sp
                )
            )
            Text(
                text = intervalText,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 10.sp
                )
            )
        }
    }
}
