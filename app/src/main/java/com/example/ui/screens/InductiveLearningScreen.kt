package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.slideInVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsHoveredAsState
import androidx.compose.foundation.interaction.collectIsPressedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Sync
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
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
import com.example.data.model.QuizExercise
import com.example.data.model.QuizExerciseType
import com.example.data.repository.QuizExerciseRepository
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
import com.example.util.LocalJapaneseTts

/**
 * InductiveLearningScreen: Màn hình Học Quy Nạp N3 với Quiz Engine Đa Dạng Hóa
 * Hỗ trợ luân phiên 4 dạng bài tập:
 * 1. Multiple Choice: Chọn đáp án đúng từ 4 lựa chọn 3D.
 * 2. Fill-in-the-blank: Điền từ/ngữ pháp khuyết vào câu.
 * 3. Sentence Builder (Ghép câu 3D): Xếp các thẻ từ 3D theo thứ tự câu tiếng Nhật.
 * 4. Translation (Dịch 2 chiều): Dịch câu Nhật -> Việt & Việt -> Nhật.
 *
 * Tích hợp Trợ lý AI (Gemini 1.5 Flash): Tự động phân tích lỗi sai và hiển thị
 * khung báo lỗi Danger Red dưới đáy màn hình khi làm sai bất kỳ bài tập nào.
 */
@Composable
fun InductiveLearningScreen(
    uiState: NihongoUiState,
    viewModel: NihongoViewModel,
    modifier: Modifier = Modifier
) {
    BackHandler {
        viewModel.goBack()
    }

    val tts = LocalJapaneseTts.current
    val scrollState = rememberScrollState()
    val activeEx = uiState.activeExercise

    val isEvaluated = uiState.isAnswerEvaluated
    val isCorrect = uiState.isAnswerCorrect

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralWhite)
    ) {
        if (uiState.isSessionFinished) {
            val totalQ = uiState.sessionTotalCount.coerceAtLeast(1)
            val correctQ = uiState.sessionCorrectCount.coerceIn(0, totalQ)
            val accuracyPct = ((correctQ.toFloat() / totalQ.toFloat()) * 100).toInt()

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(scrollState)
                    .padding(20.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Gamified3DCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("quiz_session_finished_card"),
                    backgroundColor = Color(0xFFF6FFF0),
                    borderColor = GamifyGreen,
                    borderWidth = 2.5.dp
                ) {
                    Column(
                        modifier = Modifier.padding(22.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(72.dp)
                                .clip(CircleShape)
                                .background(GamifyGreenLight)
                                .border(3.dp, GamifyGreen, CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = "🎉", fontSize = 34.sp)
                        }

                        Text(
                            text = if (uiState.isTestMode) "HOÀN THÀNH BÀI KIỂM TRA!" else "HOÀN THÀNH PHIÊN HỌC!",
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GamifyGreenDark,
                                fontSize = 19.sp,
                                textAlign = TextAlign.Center
                            )
                        )

                        Text(
                            text = "✓ Tiến trình các nội dung trả lời đúng đã được lưu tự động vào Bài học của bạn.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = NeutralText,
                                textAlign = TextAlign.Center,
                                fontSize = 13.sp,
                                lineHeight = 19.sp
                            )
                        )

                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedMd)
                                .background(NeutralWhite)
                                .border(1.dp, NeutralBorder, RoundedMd)
                                .padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceAround
                        ) {
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Số câu đúng",
                                    style = MaterialTheme.typography.labelSmall.copy(color = NeutralSubtext, fontSize = 10.sp)
                                )
                                Text(
                                    text = "$correctQ / $totalQ",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GamifyGreenDark
                                    )
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Độ chính xác",
                                    style = MaterialTheme.typography.labelSmall.copy(color = NeutralSubtext, fontSize = 10.sp)
                                )
                                Text(
                                    text = "$accuracyPct%",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GamifyBlueDark
                                    )
                                )
                            }
                            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                Text(
                                    text = "Tổng điểm",
                                    style = MaterialTheme.typography.labelSmall.copy(color = NeutralSubtext, fontSize = 10.sp)
                                )
                                Text(
                                    text = "${uiState.score} PTS",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFFD97706)
                                    )
                                )
                            }
                        }

                        Gamified3DButton(
                            text = "Quay lại Bài học",
                            icon = Icons.AutoMirrored.Filled.ArrowBack,
                            onClick = { viewModel.goBack() },
                            backgroundColor = GamifyGreen,
                            shadowColor = GamifyGreenShadow,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "btn_quiz_back_to_lesson"
                        )

                        Gamified3DButton(
                            text = "Làm lại bài này",
                            icon = Icons.Default.Refresh,
                            onClick = {
                                if (uiState.isTestMode) {
                                    viewModel.startTestForLesson(
                                        uiState.selectedStudyCategory,
                                        uiState.selectedLessonId,
                                        uiState.previousScreen
                                    )
                                } else {
                                    viewModel.startPracticeForLesson(
                                        uiState.selectedStudyCategory,
                                        uiState.selectedLessonId,
                                        uiState.previousScreen
                                    )
                                }
                            },
                            backgroundColor = GamifyBlue,
                            shadowColor = GamifyBlueShadow,
                            modifier = Modifier.fillMaxWidth(),
                            testTag = "btn_quiz_restart"
                        )
                    }
                }
            }
            return@Box
        }

        // Khoảng trống đáy co giãn linh hoạt theo trạng thái của thanh hành động / phân tích lỗi
        val dynamicBottomPadding = when {
            !isEvaluated -> 90.dp
            isCorrect -> 130.dp
            else -> 220.dp
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(scrollState)
                .padding(horizontal = 18.dp, vertical = 12.dp)
                .padding(bottom = dynamicBottomPadding),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            // 1. Top Bar: Nút Back + Tiến độ bài học + Điểm số + Nút Mở Kho 3 Mục Học
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                IconButton(
                    onClick = { viewModel.goBack() },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedMd)
                        .background(NeutralCard)
                        .border(1.5.dp, NeutralBorder, RoundedMd)
                        .testTag("btn_back_home")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Trở về Lộ trình",
                        tint = NeutralText
                    )
                }

                Column(modifier = Modifier.weight(1f)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedSm)
                                    .background(if (uiState.isTestMode) GamifyRedLight else GamifyGreenLight)
                                    .padding(horizontal = 5.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = if (uiState.isTestMode) "BÀI KIỂM TRA" else "PHIÊN 5-10 PHÚT",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (uiState.isTestMode) GamifyRedDark else GamifyGreenDark,
                                        fontSize = 8.5.sp
                                    )
                                )
                            }
                            Text(
                                text = "CÂU ${(uiState.currentExerciseIndex) + 1} / ${uiState.exerciseList.size.coerceAtLeast(1)}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeutralSubtext,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Text(
                            text = "${uiState.score} PTS",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GamifyBlue,
                                fontSize = 12.sp
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(4.dp))
                    val totalExercises = uiState.exerciseList.size.coerceAtLeast(1)
                    val progressRatio = (uiState.currentExerciseIndex + 1f) / totalExercises.toFloat()
                    GamifiedProgressBar(
                        progress = progressRatio,
                        fillColor = if (uiState.isTestMode) GamifyRed else GamifyGreen,
                        height = 10.dp
                    )
                }

                IconButton(
                    onClick = { viewModel.navigateTo(com.example.ui.state.Screen.STUDY_HUB) },
                    modifier = Modifier
                        .size(40.dp)
                        .clip(RoundedMd)
                        .background(GamifyGreenLight)
                        .border(1.5.dp, GamifyGreen, RoundedMd)
                        .testTag("btn_open_study_hub_from_quiz")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = "Kho 3 Mục Học",
                        tint = GamifyGreenDark,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            // Test Mode Countdown Banner
            if (uiState.isTestMode) {
                val mins = uiState.testTimeRemainingSeconds / 60
                val secs = uiState.testTimeRemainingSeconds % 60
                val timeFormatted = String.format("%02d:%02d", mins, secs)
                val isUrgent = uiState.testTimeRemainingSeconds <= 30
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedMd)
                        .background(if (isUrgent) GamifyRedLight else GamifyYellow.copy(alpha = 0.15f))
                        .border(1.5.dp, if (isUrgent) GamifyRed else GamifyYellowShadow, RoundedMd)
                        .padding(horizontal = 12.dp, vertical = 8.dp)
                        .testTag("test_countdown_banner"),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Timer,
                            contentDescription = "Đồng hồ đếm ngược",
                            tint = if (isUrgent) GamifyRed else NeutralText,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = if (isUrgent) "SẮP HẾT GIỜ KIỂM TRA!" else "THỜI GIAN LÀM BÀI",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.ExtraBold,
                                color = if (isUrgent) GamifyRedDark else NeutralText,
                                fontSize = 10.5.sp
                            )
                        )
                    }
                    Text(
                        text = timeFormatted,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Black,
                            color = if (isUrgent) GamifyRed else GamifyBlueDark,
                            fontSize = 15.sp
                        )
                    )
                }
            }

            // Banner câu tương tự nếu đang luyện lại
            if (uiState.isShowingSimilarQuestion) {
                Gamified3DCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = GamifyBlueLight,
                    borderColor = GamifyBlue,
                    borderWidth = 1.5.dp
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "Similar question",
                            tint = GamifyBlue,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "Đang làm câu tương tự để củng cố quy luật!",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GamifyBlueDark
                            )
                        )
                    }
                }
            }

            // 2. Main Exercise Container
            Gamified3DCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = NeutralWhite,
                borderColor = NeutralBorder
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(14.dp)
                ) {
                    // Header thẻ bài tập: Badge dạng bài tập + Điểm ngữ pháp
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        val badgeColor = when (activeEx.type) {
                            QuizExerciseType.MULTIPLE_CHOICE -> GamifyBlue
                            QuizExerciseType.FILL_IN_BLANK -> GamifyGreen
                            QuizExerciseType.SENTENCE_BUILDER -> GamifyYellow
                            QuizExerciseType.TRANSLATION -> GamifyRed
                        }

                        val badgeBg = when (activeEx.type) {
                            QuizExerciseType.MULTIPLE_CHOICE -> GamifyBlueLight
                            QuizExerciseType.FILL_IN_BLANK -> GamifyGreenLight
                            QuizExerciseType.SENTENCE_BUILDER -> Color(0xFFFFFBEB)
                            QuizExerciseType.TRANSLATION -> GamifyRedLight
                        }

                        Box(
                            modifier = Modifier
                                .clip(RoundedSm)
                                .background(badgeBg)
                                .border(1.dp, badgeColor, RoundedSm)
                                .padding(horizontal = 8.dp, vertical = 3.dp)
                        ) {
                            Text(
                                text = activeEx.type.badge,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = badgeColor,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        val headerLabel = if (isEvaluated) {
                            activeEx.grammarPoint
                        } else {
                            when (activeEx.type) {
                                QuizExerciseType.MULTIPLE_CHOICE -> "Trắc nghiệm ngữ cảnh"
                                QuizExerciseType.FILL_IN_BLANK -> "Điền từ vào ô khuyết"
                                QuizExerciseType.SENTENCE_BUILDER -> "Sắp xếp cấu trúc câu"
                                QuizExerciseType.TRANSLATION -> if (activeEx.isJpToVn) {
                                    "Nhật → Việt (Active Recall)"
                                } else {
                                    "Việt → Nhật (Active Recall)"
                                }
                            }
                        }

                        Text(
                            text = headerLabel,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (isEvaluated) GamifyGreenDark else NeutralSubtext,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis,
                            modifier = Modifier.padding(start = 6.dp)
                        )
                    }

                    // Lời hướng dẫn của dạng bài tập
                    Text(
                        text = activeEx.instruction,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = NeutralSubtext,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Medium
                        )
                    )

                    // 3. Render 4 Dạng Bài Tập Đa Dạng
                    when (activeEx.type) {
                        QuizExerciseType.MULTIPLE_CHOICE -> {
                            MultipleChoiceLayout(
                                exercise = activeEx,
                                selectedOptionIndex = uiState.selectedOptionIndex,
                                isEvaluated = isEvaluated,
                                onSelectOption = { viewModel.selectGrammarOption(it) }
                            )
                        }
                        QuizExerciseType.FILL_IN_BLANK -> {
                            FillInTheBlankLayout(
                                exercise = activeEx,
                                selectedOptionIndex = uiState.selectedOptionIndex,
                                isEvaluated = isEvaluated,
                                onSelectOption = { viewModel.selectGrammarOption(it) }
                            )
                        }
                        QuizExerciseType.SENTENCE_BUILDER -> {
                            SentenceBuilderLayout(
                                exercise = activeEx,
                                selectedChips = uiState.sentenceBuilderSelectedChips,
                                availableChips = uiState.sentenceBuilderAvailableChips,
                                isEvaluated = isEvaluated,
                                onSelectChip = { viewModel.selectSentenceBuilderChip(it) },
                                onRemoveChip = { viewModel.removeSentenceBuilderChip(it) }
                            )
                        }
                        QuizExerciseType.TRANSLATION -> {
                            TranslationLayout(
                                exercise = activeEx,
                                selectedOptionIndex = uiState.selectedOptionIndex,
                                isEvaluated = isEvaluated,
                                onSelectOption = { viewModel.selectGrammarOption(it) }
                            )
                        }
                    }
                }
            }

            // 4. Section Tham khảo: Chỉ hiển thị sau khi đã trả lời để không lộ trước đáp án
            if (isEvaluated) {
                Text(
                    text = "TỪ VỰNG N3 THAM KHẢO (語彙・漢字)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeutralSubtext,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(top = 4.dp)
                )

                LazyRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    itemsIndexed(uiState.vocabularyList.take(10)) { index, item ->
                        val isSelected = index == uiState.currentVocabIndex
                        Box(
                            modifier = Modifier
                                .clip(RoundedMd)
                                .background(if (isSelected) GamifyGreenLight else NeutralCard)
                                .border(1.5.dp, if (isSelected) GamifyGreen else NeutralBorder, RoundedMd)
                                .clickable { viewModel.selectVocabIndex(index) }
                                .padding(horizontal = 14.dp, vertical = 6.dp)
                                .testTag("vocab_tab_$index")
                        ) {
                            Text(
                                text = item.kanji,
                                style = MaterialTheme.typography.labelLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isSelected) GamifyGreenDark else NeutralText,
                                    fontSize = 14.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // ========================================================
        // 5. STICKY DANGER RED / SUCCESS BOTTOM VERIFICATION BAR
        // ========================================================
        val bottomBg = when {
            !isEvaluated -> NeutralWhite
            isCorrect -> GamifyGreenLight
            else -> GamifyRedLight
        }

        val bottomBorder = when {
            !isEvaluated -> NeutralBorder
            isCorrect -> GamifyGreen
            else -> GamifyRed
        }

        val canSubmit = when (activeEx.type) {
            QuizExerciseType.SENTENCE_BUILDER -> uiState.sentenceBuilderSelectedChips.isNotEmpty()
            else -> uiState.selectedOptionIndex != null
        }

        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .fillMaxWidth()
                .background(bottomBg)
                .border(width = 2.dp, color = bottomBorder)
                .padding(horizontal = 18.dp, vertical = 12.dp)
        ) {
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                if (isEvaluated) {
                    if (isCorrect) {
                        // Trạng thái Đúng: Xanh lá chúc mừng
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.CheckCircle,
                                contentDescription = "Chính xác",
                                tint = GamifyGreenDark,
                                modifier = Modifier.size(24.dp)
                            )
                            Column {
                                Text(
                                    text = "TUYỆT VỜI! CHÍNH XÁC (+50 PTS)",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GamifyGreenDark,
                                        fontSize = 15.sp
                                    )
                                )
                                Text(
                                    text = activeEx.explanation,
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        fontFamily = LoraFontFamily,
                                        color = GamifyGreenDark,
                                        fontSize = 11.5.sp
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }
                        }
                    } else {
                        // TRẠNG THÁI SAI: KHUNG BÁO LỖI MÀU ĐỎ (DANGER RED #FF4B4B)
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.ErrorOutline,
                                    contentDescription = "Sai",
                                    tint = GamifyRed,
                                    modifier = Modifier.size(22.dp)
                                )
                                Text(
                                    text = "CHƯA CHÍNH XÁC!",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GamifyRed,
                                        fontSize = 15.sp
                                    )
                                )
                            }

                            // Đối chiếu đáp án đúng
                            val correctAnswerDisplay = when (activeEx.type) {
                                QuizExerciseType.SENTENCE_BUILDER -> activeEx.correctTokenOrder.joinToString(" ")
                                QuizExerciseType.FILL_IN_BLANK -> activeEx.options.getOrNull(activeEx.correctAnswerIndex).orEmpty()
                                    .ifEmpty { activeEx.blankMissingWord }
                                QuizExerciseType.TRANSLATION -> activeEx.translationOptions.ifEmpty { activeEx.options }
                                    .getOrNull(activeEx.correctTranslationIndex).orEmpty()
                                QuizExerciseType.MULTIPLE_CHOICE -> activeEx.options.getOrNull(activeEx.correctAnswerIndex).orEmpty()
                            }

                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedMd)
                                    .background(NeutralWhite)
                                    .border(1.dp, GamifyRed.copy(alpha = 0.4f), RoundedMd)
                                    .padding(horizontal = 10.dp, vertical = 6.dp)
                            ) {
                                Text(
                                    text = "Đáp án đúng: $correctAnswerDisplay",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GamifyGreenDark,
                                        fontSize = 12.sp
                                    ),
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // AI (Gemini 1.5 Flash) Phân tích lỗi sai
                            Box(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .clip(RoundedMd)
                                    .background(NeutralWhite)
                                    .border(1.5.dp, GamifyBlue, RoundedMd)
                                    .padding(8.dp)
                            ) {
                                Column(verticalArrangement = Arrangement.spacedBy(3.dp)) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "Gemini AI",
                                            tint = GamifyBlue,
                                            modifier = Modifier.size(16.dp)
                                        )
                                        Text(
                                            text = "SENSEI AI (GEMINI 1.5 FLASH) PHÂN TÍCH:",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontWeight = FontWeight.Bold,
                                                color = GamifyBlueDark,
                                                fontSize = 10.sp
                                            )
                                        )
                                    }

                                    if (uiState.isAiMistakeLoading) {
                                        Row(
                                            verticalAlignment = Alignment.CenterVertically,
                                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                                            modifier = Modifier.padding(vertical = 4.dp)
                                        ) {
                                            CircularProgressIndicator(
                                                modifier = Modifier.size(14.dp),
                                                color = GamifyBlue,
                                                strokeWidth = 2.dp
                                            )
                                            Text(
                                                text = "Sensei đang phân tích sắc thái lỗi sai của bạn...",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = NeutralSubtext,
                                                    fontSize = 11.sp
                                                )
                                            )
                                        }
                                    } else {
                                        val analysis = uiState.aiMistakeExplanation
                                            ?: activeEx.mistakeExplanation.ifEmpty {
                                                "Phương án bạn chọn chưa đúng ngữ cảnh. Hãy ghi nhớ quy tắc ngữ pháp N3 chuẩn."
                                            }
                                        Text(
                                            text = analysis,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                fontFamily = LoraFontFamily,
                                                color = NeutralText,
                                                fontSize = 11.5.sp,
                                                lineHeight = 16.sp
                                            ),
                                            maxLines = 4,
                                            overflow = TextOverflow.Ellipsis
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                // Nút bấm hành động 3D bên dưới
                if (!isEvaluated) {
                    Gamified3DButton(
                        text = "KIỂM TRA",
                        onClick = {
                            viewModel.submitGrammarAnswer()
                            if (viewModel.uiState.value.isAnswerCorrect) {
                                tts.playSuccessSound()
                            } else {
                                tts.playErrorSound()
                            }
                        },
                        enabled = canSubmit,
                        backgroundColor = GamifyGreen,
                        shadowColor = GamifyGreenShadow,
                        modifier = Modifier.fillMaxWidth(),
                        height = 48.dp,
                        testTag = "btn_submit_grammar"
                    )
                } else if (isCorrect) {
                    Gamified3DButton(
                        text = "TIẾP TỤC",
                        onClick = { viewModel.nextGrammarQuestion() },
                        backgroundColor = GamifyGreen,
                        shadowColor = GamifyGreenShadow,
                        modifier = Modifier.fillMaxWidth(),
                        height = 48.dp,
                        testTag = "btn_next_question"
                    )
                } else {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Gamified3DButton(
                            text = "Làm lại",
                            icon = Icons.Default.Refresh,
                            onClick = { viewModel.retrySimilarQuestion() },
                            backgroundColor = GamifyYellow,
                            shadowColor = GamifyYellowShadow,
                            textColor = NeutralText,
                            height = 46.dp,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_retry_similar"
                        )

                        Gamified3DButton(
                            text = "Tiếp tục",
                            onClick = { viewModel.nextGrammarQuestion() },
                            backgroundColor = GamifyRed,
                            shadowColor = GamifyRedShadow,
                            height = 46.dp,
                            modifier = Modifier.weight(1f),
                            testTag = "btn_next_question"
                        )
                    }
                }
            }
        }
    }
}

// ========================================================
// 1. MULTIPLE CHOICE LAYOUT (Trắc nghiệm 4 lựa chọn)
// ========================================================
@Composable
private fun MultipleChoiceLayout(
    exercise: QuizExercise,
    selectedOptionIndex: Int?,
    isEvaluated: Boolean,
    onSelectOption: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = exercise.promptJp,
                style = MaterialTheme.typography.titleMedium.copy(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = NeutralText,
                    lineHeight = 26.sp
                ),
                modifier = Modifier.weight(1f)
            )
            Spacer(modifier = Modifier.width(8.dp))
            JapaneseAudioSpeakerButton(
                textToSpeak = if (isEvaluated) {
                    exercise.completeSentenceJp.ifEmpty { exercise.promptJp }
                } else {
                    exercise.promptJp.replace(Regex("【?\\s*＿+\\s*】?"), "…")
                },
                size = 38.dp,
                iconSize = 20.dp,
                primaryColor = GamifyGreen,
                backgroundColor = GamifyGreenLight,
                testTag = "btn_tts_mc_prompt"
            )
        }

        if (isEvaluated && exercise.promptVn.isNotEmpty()) {
            Text(
                text = "Dịch nghĩa: ${exercise.promptVn}",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = NeutralSubtext,
                    fontFamily = LoraFontFamily,
                    fontSize = 12.sp
                )
            )
        }

        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            exercise.options.forEachIndexed { index, optionText ->
                GamifiedAnswerOption(
                    index = index,
                    text = optionText,
                    isSelected = selectedOptionIndex == index,
                    isEvaluated = isEvaluated,
                    isCorrectAnswer = index == exercise.correctAnswerIndex,
                    enabled = !isEvaluated,
                    onClick = { onSelectOption(index) }
                )
            }
        }
    }
}

// ========================================================
// 2. FILL-IN-THE-BLANK LAYOUT (Điền từ khuyết)
// ========================================================
@Composable
private fun FillInTheBlankLayout(
    exercise: QuizExercise,
    selectedOptionIndex: Int?,
    isEvaluated: Boolean,
    onSelectOption: (Int) -> Unit
) {
    val blankRegex = remember { Regex("【?\\s*＿+\\s*】?") }
    val safeBlankSentence = remember(exercise) {
        QuizExerciseRepository.maskedBlankSentence(exercise)
    }
    val selectedText = selectedOptionIndex?.let { exercise.options.getOrNull(it) } ?: "＿＿＿"

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Hộp câu hỏi có ô khuyết tương tác (đã ẩn hoàn toàn đáp án từ danh sách lựa chọn)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedMd)
                .background(Color(0xFFF7FBF8))
                .border(1.5.dp, GamifyGreen.copy(alpha = 0.5f), RoundedMd)
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    val displaySentence = safeBlankSentence.replace(blankRegex, "【 $selectedText 】")
                    Text(
                        text = displaySentence,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeutralText,
                            lineHeight = 26.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    JapaneseAudioSpeakerButton(
                        textToSpeak = if (isEvaluated) {
                            exercise.completeSentenceJp.ifEmpty {
                                safeBlankSentence.replace(
                                    blankRegex,
                                    exercise.blankMissingWord.ifEmpty {
                                        exercise.options.getOrNull(exercise.correctAnswerIndex).orEmpty()
                                    }
                                )
                            }
                        } else {
                            safeBlankSentence.replace(blankRegex, "…")
                        },
                        size = 36.dp,
                        iconSize = 18.dp,
                        primaryColor = GamifyGreen,
                        backgroundColor = GamifyGreenLight
                    )
                }

                if (isEvaluated && exercise.promptVn.isNotEmpty()) {
                    Text(
                        text = "Dịch nghĩa: ${exercise.promptVn}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NeutralSubtext,
                            fontFamily = LoraFontFamily,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        Text(
            text = "Chạm vào từ thích hợp để điền vào ô trống:",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = NeutralSubtext,
                fontSize = 11.sp
            )
        )

        // 4 lựa chọn điền khuyết dạng thẻ 3D
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            exercise.options.forEachIndexed { index, optionText ->
                GamifiedAnswerOption(
                    index = index,
                    text = optionText,
                    isSelected = selectedOptionIndex == index,
                    isEvaluated = isEvaluated,
                    isCorrectAnswer = index == exercise.correctAnswerIndex,
                    enabled = !isEvaluated,
                    onClick = { onSelectOption(index) }
                )
            }
        }
    }
}

// ========================================================
// 3. SENTENCE BUILDER LAYOUT (Ghép câu 3D)
// ========================================================
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun SentenceBuilderLayout(
    exercise: QuizExercise,
    selectedChips: List<String>,
    availableChips: List<String>,
    isEvaluated: Boolean,
    onSelectChip: (String) -> Unit,
    onRemoveChip: (Int) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Banner câu tiếng Việt mục tiêu (tuyệt đối không lộ câu tiếng Nhật trước khi kiểm tra)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedMd)
                .background(Color(0xFFFFFBEB))
                .border(1.5.dp, GamifyYellow.copy(alpha = 0.6f), RoundedMd)
                .padding(12.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Text(
                    text = "CÂU TIẾNG VIỆT MỤC TIÊU:",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309),
                        fontSize = 10.sp
                    )
                )
                Text(
                    text = exercise.completeSentenceVn.ifEmpty { exercise.promptVn },
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeutralText,
                        fontSize = 15.sp,
                        fontFamily = LoraFontFamily
                    )
                )

                if (isEvaluated && exercise.completeSentenceJp.isNotEmpty()) {
                    Spacer(modifier = Modifier.height(4.dp))
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedSm)
                            .background(NeutralWhite)
                            .border(1.dp, GamifyGreen.copy(alpha = 0.4f), RoundedSm)
                            .padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = exercise.completeSentenceJp,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GamifyGreenDark,
                                fontSize = 13.5.sp
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        JapaneseAudioSpeakerButton(
                            textToSpeak = exercise.completeSentenceJp,
                            size = 32.dp,
                            iconSize = 16.dp,
                            primaryColor = GamifyGreen,
                            backgroundColor = GamifyGreenLight
                        )
                    }
                }
            }
        }

        // Hàng xếp câu (Sequence Tray / Drop area)
        Text(
            text = "Hàng câu của bạn (Chạm vào thẻ đã chọn để hoàn tác):",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = NeutralSubtext,
                fontSize = 11.sp
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .heightIn(min = 68.dp)
                .clip(RoundedLg)
                .background(if (selectedChips.isEmpty()) Color(0xFFF9FAFB) else NeutralWhite)
                .border(
                    width = 2.dp,
                    color = if (selectedChips.isNotEmpty()) GamifyBlue else NeutralBorder,
                    shape = RoundedLg
                )
                .padding(10.dp),
            contentAlignment = Alignment.CenterStart
        ) {
            if (selectedChips.isEmpty()) {
                Text(
                    text = "Chạm vào các thẻ từ 3D bên dưới để bắt đầu ghép câu...",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeutralSubtext,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    selectedChips.forEachIndexed { index, chip ->
                        TactileWordChip(
                            text = chip,
                            isPlaced = true,
                            enabled = !isEvaluated,
                            onClick = { onRemoveChip(index) }
                        )
                    }
                }
            }
        }

        // Kho thẻ từ 3D còn lại (Available Chips Bank - tự động ẩn các thẻ đã đặt)
        Text(
            text = "Kho thẻ từ 3D (Chạm để chọn):",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = NeutralSubtext,
                fontSize = 11.sp
            )
        )

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedMd)
                .background(Color(0xFFF3F4F6))
                .padding(10.dp)
        ) {
            if (availableChips.isEmpty()) {
                Text(
                    text = "Đã đặt hết tất cả thẻ từ. Hãy kiểm tra lại thứ tự hoặc bấm KIỂM TRA.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeutralSubtext,
                        fontSize = 12.sp
                    ),
                    modifier = Modifier.padding(4.dp)
                )
            } else {
                FlowRow(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    availableChips.forEach { chip ->
                        TactileWordChip(
                            text = chip,
                            isPlaced = false,
                            enabled = !isEvaluated,
                            onClick = { onSelectChip(chip) }
                        )
                    }
                }
            }
        }
    }
}

// ========================================================
// 4. TRANSLATION LAYOUT (Dịch 2 chiều - Active Recall nghiêm ngặt)
// ========================================================
@Composable
private fun TranslationLayout(
    exercise: QuizExercise,
    selectedOptionIndex: Int?,
    isEvaluated: Boolean,
    onSelectOption: (Int) -> Unit
) {
    // Tách biệt tuyệt đối câu hỏi tiếng Nhật và nghĩa tiếng Việt trong pha nhập liệu (!isEvaluated)
    val sourcePrompt = if (exercise.isJpToVn) {
        exercise.promptJp.ifEmpty { exercise.translationQuestion }
    } else {
        exercise.promptVn.ifEmpty { exercise.translationQuestion }
    }
    val options = exercise.translationOptions.ifEmpty { exercise.options }

    Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
        // Huy hiệu chiều dịch
        Box(
            modifier = Modifier
                .clip(RoundedSm)
                .background(GamifyRedLight)
                .border(1.dp, GamifyRed, RoundedSm)
                .padding(horizontal = 8.dp, vertical = 3.dp)
        ) {
            Text(
                text = if (exercise.isJpToVn) "🇯🇵 TIẾNG NHẬT  →  🇻🇳 TIẾNG VIỆT" else "🇻🇳 TIẾNG VIỆT  →  🇯🇵 TIẾNG NHẬT",
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = GamifyRed,
                    fontSize = 10.sp
                )
            )
        }

        // Hộp câu gốc cần dịch (chỉ hiển thị 1 ngôn ngữ nguồn khi chưa kiểm tra)
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedMd)
                .background(Color(0xFFFFF7F7))
                .border(1.5.dp, GamifyRed.copy(alpha = 0.5f), RoundedMd)
                .padding(14.dp)
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = sourcePrompt,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold,
                            color = NeutralText,
                            lineHeight = 25.sp
                        ),
                        modifier = Modifier.weight(1f)
                    )

                    if (exercise.isJpToVn) {
                        Spacer(modifier = Modifier.width(8.dp))
                        JapaneseAudioSpeakerButton(
                            textToSpeak = sourcePrompt,
                            size = 38.dp,
                            iconSize = 20.dp,
                            primaryColor = GamifyRed,
                            backgroundColor = GamifyRedLight
                        )
                    }
                }

                // Chỉ mở khóa phần đối chiếu ngôn ngữ đích SAU KHI đã kiểm tra (Active Recall)
                if (isEvaluated) {
                    val revealedCounterpart = if (exercise.isJpToVn) {
                        exercise.completeSentenceVn.ifEmpty {
                            options.getOrNull(exercise.correctTranslationIndex).orEmpty()
                        }
                    } else {
                        exercise.completeSentenceJp.ifEmpty {
                            options.getOrNull(exercise.correctTranslationIndex).orEmpty()
                        }
                    }
                    if (revealedCounterpart.isNotBlank()) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedSm)
                                .background(NeutralWhite)
                                .border(1.dp, GamifyGreen.copy(alpha = 0.45f), RoundedSm)
                                .padding(horizontal = 10.dp, vertical = 8.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = if (exercise.isJpToVn) "NGHĨA TIẾNG VIỆT CHUẨN:" else "CÂU TIẾNG NHẬT CHUẨN:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GamifyGreenDark,
                                        fontSize = 9.5.sp
                                    )
                                )
                                Text(
                                    text = revealedCounterpart,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = NeutralText,
                                        fontFamily = if (exercise.isJpToVn) LoraFontFamily else MaterialTheme.typography.bodyMedium.fontFamily,
                                        fontSize = 13.5.sp
                                    )
                                )
                            }
                            if (!exercise.isJpToVn) {
                                Spacer(modifier = Modifier.width(8.dp))
                                JapaneseAudioSpeakerButton(
                                    textToSpeak = revealedCounterpart,
                                    size = 32.dp,
                                    iconSize = 16.dp,
                                    primaryColor = GamifyGreen,
                                    backgroundColor = GamifyGreenLight
                                )
                            }
                        }
                    }
                }
            }
        }

        Text(
            text = if (exercise.isJpToVn) {
                "Tự nhớ nghĩa tiếng Việt rồi chọn bản dịch đúng nhất:"
            } else {
                "Tự nhớ câu tiếng Nhật rồi chọn phương án đúng nhất:"
            },
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = NeutralSubtext,
                fontSize = 11.sp
            )
        )

        // Danh sách lựa chọn dịch (tắt TTS ở pha nhập liệu để tránh gợi ý âm thanh/đọc nhầm tiếng Việt)
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            options.forEachIndexed { index, optionText ->
                GamifiedAnswerOption(
                    index = index,
                    text = optionText,
                    isSelected = selectedOptionIndex == index,
                    isEvaluated = isEvaluated,
                    isCorrectAnswer = index == exercise.correctTranslationIndex,
                    enabled = !isEvaluated,
                    allowAudio = !exercise.isJpToVn && isEvaluated,
                    onClick = { onSelectOption(index) }
                )
            }
        }
    }
}

/**
 * Thẻ từ 3D Tactile Word Chip cho Sentence Builder
 */
@Composable
private fun TactileWordChip(
    text: String,
    isPlaced: Boolean,
    enabled: Boolean,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val bgColor = if (isPlaced) GamifyBlueLight else NeutralWhite
    val borderColor = if (isPlaced) GamifyBlue else NeutralBorder
    val shadowColor = if (isPlaced) GamifyBlueShadow.copy(alpha = 0.6f) else Color(0xFFD6D6D6)

    Box(
        modifier = Modifier
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                enabled = enabled,
                onClick = onClick
            )
    ) {
        // Shadow base
        Box(
            modifier = Modifier
                .matchParentSize()
                .padding(top = 3.dp)
                .clip(RoundedMd)
                .background(shadowColor)
        )

        // Face
        Box(
            modifier = Modifier
                .padding(bottom = 3.dp)
                .clip(RoundedMd)
                .background(bgColor)
                .border(2.dp, borderColor, RoundedMd)
                .padding(horizontal = 12.dp, vertical = 8.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(4.dp)
            ) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.labelLarge.copy(
                        fontWeight = FontWeight.Bold,
                        color = if (isPlaced) GamifyBlueDark else NeutralText,
                        fontSize = 14.sp
                    )
                )
                if (isPlaced) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Bỏ chọn",
                        tint = GamifyBlue,
                        modifier = Modifier.size(14.dp)
                    )
                }
            }
        }
    }
}

/**
 * 3D Answer Option Button
 */
@Composable
private fun GamifiedAnswerOption(
    index: Int,
    text: String,
    isSelected: Boolean,
    isEvaluated: Boolean,
    isCorrectAnswer: Boolean,
    enabled: Boolean,
    allowAudio: Boolean = true,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isHovered by interactionSource.collectIsHoveredAsState()
    val isPressed by interactionSource.collectIsPressedAsState()
    val hasJapaneseChars = remember(text) {
        text.any { it in '\u3040'..'\u30FF' || it in '\u4E00'..'\u9FFF' }
    }

    val bgColor = when {
        isEvaluated && isCorrectAnswer -> GamifyGreenLight
        isEvaluated && isSelected && !isCorrectAnswer -> GamifyRedLight
        isSelected -> GamifyBlueLight
        isHovered || isPressed -> Color(0xFFF0F7FF)
        else -> NeutralWhite
    }

    val borderColor = when {
        isEvaluated && isCorrectAnswer -> GamifyGreen
        isEvaluated && isSelected && !isCorrectAnswer -> GamifyRed
        isSelected -> GamifyBlue
        isHovered || isPressed -> GamifyBlue.copy(alpha = 0.5f)
        else -> NeutralBorder
    }

    val borderWidth = if (isSelected || (isEvaluated && isCorrectAnswer)) 2.5.dp else 2.dp

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedLg)
            .background(bgColor)
            .border(borderWidth, borderColor, RoundedLg)
            .clickable(
                enabled = enabled,
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .padding(horizontal = 14.dp, vertical = 12.dp)
            .testTag("option_$index")
    ) {
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
                // Number badge
                Box(
                    modifier = Modifier
                        .size(28.dp)
                        .clip(RoundedSm)
                        .background(if (isSelected) GamifyBlue else NeutralCard)
                        .border(1.dp, if (isSelected) GamifyBlue else NeutralBorder, RoundedSm),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${index + 1}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else NeutralText
                        )
                    )
                }

                Text(
                    text = text,
                    style = MaterialTheme.typography.bodyLarge.copy(
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                        color = NeutralText,
                        fontSize = 15.sp,
                        lineHeight = 21.sp
                    )
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                if (allowAudio && hasJapaneseChars) {
                    JapaneseAudioSpeakerButton(
                        textToSpeak = text,
                        size = 28.dp,
                        iconSize = 14.dp,
                        primaryColor = GamifyBlue,
                        backgroundColor = GamifyBlueLight,
                        testTag = "btn_tts_read_option_$index"
                    )
                }

                if (isEvaluated) {
                    if (isCorrectAnswer) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Đúng",
                            tint = GamifyGreen,
                            modifier = Modifier.size(20.dp)
                        )
                    } else if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Sai",
                            tint = GamifyRed,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }
    }
}
