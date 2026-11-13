package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.GrammarPoint
import com.example.data.model.KanjiItem
import com.example.data.model.SrsItemType
import com.example.data.model.StudyCategory
import com.example.data.model.VocabularyItem
import com.example.data.repository.SrsRepository
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

/**
 * BƯỚC 2: MÀN HÌNH CHI TIẾT BÀI HỌC (LESSON DETAIL SCREEN - CHUẨN QUIZLET)
 * Giao diện gồm 3 phần:
 * 1. Top Header: Nút Back, Tiêu đề bài học lớn, tiến độ % Mastered.
 * 2. Khu vực Chế Độ Học (Study Modes Grid 2x2):
 *    - 🃏 Thẻ ghi nhớ (Flashcard / SRS)
 *    - 📝 Học quy nạp (Learn / Practice)
 *    - ✍️ Kiểm tra (Test / Quiz - 10 câu đếm ngược)
 *    - 🧩 Ghép thẻ (Card Battle / Match)
 * 3. Danh sách xem trước (Preview List):
 *    - Mặt trước (Tiếng Nhật) ── Mặt sau (Tiếng Việt) kèm âm thanh phát âm.
 */
@Composable
fun LessonDetailScreen(
    uiState: NihongoUiState,
    viewModel: NihongoViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBack()
    }

    val category = uiState.selectedStudyCategory
    val lessonId = uiState.selectedLessonId

    // Lấy danh sách item thuộc bài học này
    val grammarItems = remember(uiState.grammarPoints, lessonId) {
        uiState.grammarPoints.filter { it.lessonId == lessonId }
    }
    val vocabItems = remember(uiState.vocabularyList, lessonId) {
        uiState.vocabularyList.filter { it.lessonId == lessonId }.ifEmpty {
            uiState.vocabularyList.take(20)
        }
    }
    val kanjiItems = remember(uiState.kanjiList, lessonId) {
        val lesson = uiState.kanjiLessons.find { it.lessonId == lessonId }
        lesson?.kanjiList ?: uiState.kanjiList
    }

    val totalItemsCount = when (category) {
        StudyCategory.GRAMMAR -> grammarItems.size
        StudyCategory.VOCABULARY -> vocabItems.size
        StudyCategory.KANJI -> kanjiItems.size
    }

    val srsType = when (category) {
        StudyCategory.GRAMMAR -> SrsItemType.GRAMMAR
        StudyCategory.VOCABULARY -> SrsItemType.VOCABULARY
        StudyCategory.KANJI -> SrsItemType.KANJI
    }

    val cardIds = when (category) {
        StudyCategory.GRAMMAR -> grammarItems.map { it.id }
        StudyCategory.VOCABULARY -> vocabItems.map { it.id }
        StudyCategory.KANJI -> kanjiItems.map { it.id }
    }

    val (masteredCount, masteredPct) = remember(lessonId, category, uiState.srsStats) {
        SrsRepository.getLessonMastery(srsType, cardIds, lessonId)
    }

    val categoryColor = when (category) {
        StudyCategory.GRAMMAR -> GamifyGreen
        StudyCategory.VOCABULARY -> GamifyBlue
        StudyCategory.KANJI -> GamifyRed
    }

    val categoryTitle = when (category) {
        StudyCategory.GRAMMAR -> "NGỮ PHÁP N3"
        StudyCategory.VOCABULARY -> "TỪ VỰNG N3"
        StudyCategory.KANJI -> "KANJI N3"
    }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralWhite)
            .padding(horizontal = 16.dp, vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // ==========================================
        // 1. TOP HEADER (Back button, Title, % Mastered)
        // ==========================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    IconButton(
                        onClick = onNavigateBack,
                        modifier = Modifier
                            .size(42.dp)
                            .clip(RoundedMd)
                            .background(NeutralCard)
                            .border(1.5.dp, NeutralBorder, RoundedMd)
                            .testTag("btn_lesson_detail_back")
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Quay lại danh sách bài học",
                            tint = NeutralText
                        )
                    }

                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedSm)
                                    .background(categoryColor.copy(alpha = 0.15f))
                                    .padding(horizontal = 6.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = categoryTitle,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = categoryColor,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                            Text(
                                text = "BÀI $lessonId",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = NeutralSubtext,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Text(
                            text = uiState.selectedLessonTitle,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeutralText,
                                fontSize = 18.sp
                            ),
                            maxLines = 1,
                            overflow = TextOverflow.Ellipsis
                        )
                    }

                    // Badge trạng thái
                    val isFullyMastered = masteredPct >= 100f
                    val isLearning = masteredPct > 0f
                    Box(
                        modifier = Modifier
                            .clip(RoundedFull)
                            .background(
                                when {
                                    isFullyMastered -> GamifyGreenLight
                                    isLearning -> GamifyBlueLight
                                    else -> Color(0xFFF3F4F6)
                                }
                            )
                            .border(
                                1.5.dp,
                                when {
                                    isFullyMastered -> GamifyGreen
                                    isLearning -> GamifyBlue
                                    else -> NeutralBorder
                                },
                                RoundedFull
                            )
                            .padding(horizontal = 10.dp, vertical = 4.dp)
                    ) {
                        Text(
                            text = when {
                                isFullyMastered -> "Hoàn thành"
                                isLearning -> "Đang học"
                                else -> "Chưa học"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = when {
                                    isFullyMastered -> GamifyGreenDark
                                    isLearning -> GamifyBlueDark
                                    else -> NeutralSubtext
                                },
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                // Header Progress Card
                Gamified3DCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = Color(0xFFFAFAFA),
                    borderColor = NeutralBorder
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
                            Text(
                                text = "Tiến độ bài học: Đã thuộc $masteredCount / $totalItemsCount thẻ",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeutralText,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = "${masteredPct.toInt()}% Mastered",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (masteredPct >= 100f) GamifyGreenDark else GamifyBlueDark,
                                    fontSize = 12.sp
                                )
                            )
                        }
                        GamifiedProgressBar(
                            progress = (masteredPct / 100f).coerceIn(0.04f, 1f),
                            fillColor = if (masteredPct >= 100f) GamifyGreen else categoryColor,
                            height = 10.dp
                        )
                    }
                }
            }
        }

        // ==========================================
        // 2. KHU VỰC CHẾ ĐỘ HỌC (STUDY MODES GRID 2x2 - CHUẨN QUIZLET)
        // ==========================================
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Text(
                    text = "CHẾ ĐỘ HỌC (STUDY MODES)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeutralSubtext,
                        fontSize = 12.sp,
                        letterSpacing = 0.5.sp
                    )
                )

                // Grid 2x2: Hàng 1
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StudyModeButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Psychology,
                        emoji = "🃏",
                        title = "Thẻ ghi nhớ",
                        subTitle = "Flashcard & SRS SM-2",
                        badge = "SRS LẬT THẺ",
                        themeColor = GamifyBlue,
                        themeColorLight = GamifyBlueLight,
                        themeColorShadow = GamifyBlueShadow,
                        testTag = "btn_study_mode_flashcard",
                        onClick = {
                            viewModel.startSrsForLesson(category, lessonId)
                        }
                    )

                    StudyModeButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.School,
                        emoji = "📝",
                        title = "Học quy nạp",
                        subTitle = "Trắc nghiệm & Điền từ",
                        badge = "4 DẠNG BÀI",
                        themeColor = GamifyGreen,
                        themeColorLight = GamifyGreenLight,
                        themeColorShadow = GamifyGreenShadow,
                        testTag = "btn_study_mode_practice",
                        onClick = {
                            viewModel.startPracticeForLesson(category, lessonId)
                        }
                    )
                }

                // Grid 2x2: Hàng 2
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StudyModeButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Timer,
                        emoji = "✍️",
                        title = "Kiểm tra",
                        subTitle = "10 câu tính giờ",
                        badge = "ĐẾM NGƯỢC",
                        themeColor = GamifyYellow,
                        themeColorLight = Color(0xFFFFFBEB),
                        themeColorShadow = GamifyYellowShadow,
                        testTag = "btn_study_mode_test",
                        onClick = {
                            viewModel.startTestForLesson(category, lessonId)
                        }
                    )

                    StudyModeButton(
                        modifier = Modifier.weight(1f),
                        icon = Icons.Default.Casino,
                        emoji = "🧩",
                        title = "Ghép thẻ",
                        subTitle = "Nối Nhật ⇄ Việt",
                        badge = "MATCH GAME",
                        themeColor = GamifyRed,
                        themeColorLight = GamifyRedLight,
                        themeColorShadow = GamifyRedShadow,
                        testTag = "btn_study_mode_match",
                        onClick = {
                            viewModel.startCardBattleForLesson(category, lessonId)
                        }
                    )
                }
            }
        }

        // ==========================================
        // 3. DANH SÁCH XEM TRƯỚC (PREVIEW LIST)
        // ==========================================
        item {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "DANH SÁCH TRONG BÀI ($totalItemsCount thẻ)",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeutralText,
                        fontSize = 13.sp,
                        letterSpacing = 0.5.sp
                    )
                )
                Text(
                    text = "Chạm để phát âm",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeutralSubtext,
                        fontSize = 11.sp
                    )
                )
            }
        }

        when (category) {
            StudyCategory.GRAMMAR -> {
                itemsIndexed(grammarItems) { index, item ->
                    val isMastered = remember(item.id, uiState.srsStats) {
                        SrsRepository.isCardMastered(item.id)
                    }
                    GrammarPreviewCard(
                        item = item,
                        index = index + 1,
                        isMastered = isMastered,
                        onToggleMastered = {
                            viewModel.toggleCardMastered(item.id, SrsItemType.GRAMMAR)
                        }
                    )
                }
            }
            StudyCategory.VOCABULARY -> {
                itemsIndexed(vocabItems) { index, item ->
                    val isMastered = remember(item.id, uiState.srsStats) {
                        SrsRepository.isCardMastered(item.id)
                    }
                    VocabPreviewCard(
                        item = item,
                        index = index + 1,
                        isMastered = isMastered,
                        onToggleMastered = {
                            viewModel.toggleCardMastered(item.id, SrsItemType.VOCABULARY)
                        }
                    )
                }
            }
            StudyCategory.KANJI -> {
                itemsIndexed(kanjiItems) { index, item ->
                    val isMastered = remember(item.id, uiState.srsStats) {
                        SrsRepository.isCardMastered(item.id)
                    }
                    KanjiPreviewCard(
                        item = item,
                        index = index + 1,
                        isMastered = isMastered,
                        onToggleMastered = {
                            viewModel.toggleCardMastered(item.id, SrsItemType.KANJI)
                        }
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(20.dp))
        }
    }
}

/**
 * Nút chọn Chế độ học dạng Card 3D lớn chuẩn Quizlet
 */
@Composable
private fun StudyModeButton(
    modifier: Modifier = Modifier,
    icon: ImageVector,
    emoji: String,
    title: String,
    subTitle: String,
    badge: String,
    themeColor: Color,
    themeColorLight: Color,
    themeColorShadow: Color,
    testTag: String,
    onClick: () -> Unit
) {
    Gamified3DCard(
        modifier = modifier.testTag(testTag),
        backgroundColor = NeutralWhite,
        borderColor = themeColor.copy(alpha = 0.8f),
        borderWidth = 2.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedMd)
                        .background(themeColorLight)
                        .border(1.5.dp, themeColor, RoundedMd),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = emoji, fontSize = 18.sp)
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedSm)
                        .background(themeColor.copy(alpha = 0.15f))
                        .padding(horizontal = 6.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = badge,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = themeColor,
                            fontSize = 9.sp
                        )
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeutralText,
                        fontSize = 14.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = subTitle,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeutralSubtext,
                        fontSize = 11.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }
    }
}

/**
 * Card xem trước Ngữ Pháp (Mặt trước: Cấu trúc & Tên tiếng Nhật ── Mặt sau: Ý nghĩa tiếng Việt)
 */
@Composable
private fun GrammarPreviewCard(
    item: GrammarPoint,
    index: Int,
    isMastered: Boolean,
    onToggleMastered: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Gamified3DCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("preview_grammar_$index"),
        backgroundColor = if (isMastered) Color(0xFFF6FFF0) else NeutralWhite,
        borderColor = if (isMastered) GamifyGreen else NeutralBorder,
        borderWidth = 1.5.dp,
        onClick = { isExpanded = !isExpanded }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                // Bên trái: Mặt trước (Tiếng Nhật)
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(GamifyGreenLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$index",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GamifyGreenDark,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Column {
                        Text(
                            text = item.title,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeutralText,
                                fontSize = 15.sp
                            )
                        )
                        Text(
                            text = "CT: ${item.structure}",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = GamifyGreenDark,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                MasteredTogglePill(
                    isMastered = isMastered,
                    testTag = "btn_master_grammar_$index",
                    onClick = onToggleMastered
                )

                Spacer(modifier = Modifier.width(6.dp))

                // Nút loa phát âm
                JapaneseAudioSpeakerButton(
                    textToSpeak = "${item.title}。${item.examples.firstOrNull()?.jp ?: ""}",
                    size = 38.dp,
                    iconSize = 18.dp,
                    primaryColor = GamifyGreen,
                    backgroundColor = GamifyGreenLight,
                    testTag = "btn_tts_grammar_$index"
                )
            }

            // Bên phải/dưới: Mặt sau (Ý nghĩa tiếng Việt)
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedMd)
                    .background(Color(0xFFF9FAFB))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = item.meaning,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = LoraFontFamily,
                        color = NeutralText,
                        fontSize = 12.sp
                    )
                )
            }

            // Chi tiết ví dụ khi chạm vào
            AnimatedVisibility(visible = isExpanded) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 4.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    item.examples.forEach { ex ->
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedSm)
                                .background(GamifyGreenLight.copy(alpha = 0.5f))
                                .padding(8.dp)
                        ) {
                            Text(
                                text = "🇯🇵 ${ex.jp}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeutralText,
                                    fontSize = 12.sp
                                )
                            )
                            Text(
                                text = "🇻🇳 ${ex.vn}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = LoraFontFamily,
                                    color = NeutralSubtext,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card xem trước Từ Vựng (Mặt trước: Kanji & Hiragana ── Mặt sau: Nghĩa tiếng Việt)
 */
@Composable
private fun VocabPreviewCard(
    item: VocabularyItem,
    index: Int,
    isMastered: Boolean,
    onToggleMastered: () -> Unit
) {
    var isExpanded by remember { mutableStateOf(false) }

    Gamified3DCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("preview_vocab_$index"),
        backgroundColor = if (isMastered) Color(0xFFF6FFF0) else NeutralWhite,
        borderColor = if (isMastered) GamifyGreen else NeutralBorder,
        borderWidth = 1.5.dp,
        onClick = { isExpanded = !isExpanded }
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(24.dp)
                            .clip(CircleShape)
                            .background(GamifyBlueLight),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$index",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GamifyBlueDark,
                                fontSize = 11.sp
                            )
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Text(
                                text = item.kanji,
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeutralText,
                                    fontSize = 16.sp
                                )
                            )
                            Text(
                                text = "(${item.hiragana})",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = GamifyBlueDark,
                                    fontWeight = FontWeight.Medium,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                MasteredTogglePill(
                    isMastered = isMastered,
                    testTag = "btn_master_vocab_$index",
                    onClick = onToggleMastered
                )

                Spacer(modifier = Modifier.width(6.dp))

                JapaneseAudioSpeakerButton(
                    textToSpeak = "${item.kanji}。${item.exampleSentence.ifEmpty { item.hiragana }}",
                    size = 38.dp,
                    iconSize = 18.dp,
                    primaryColor = GamifyBlue,
                    backgroundColor = GamifyBlueLight,
                    testTag = "btn_tts_vocab_$index"
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedMd)
                    .background(Color(0xFFF9FAFB))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Text(
                    text = item.meaning,
                    style = MaterialTheme.typography.bodySmall.copy(
                        fontFamily = LoraFontFamily,
                        color = NeutralText,
                        fontSize = 12.5.sp
                    )
                )
            }

            if (item.exampleSentence.isNotEmpty()) {
                AnimatedVisibility(visible = isExpanded) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedSm)
                            .background(GamifyBlueLight.copy(alpha = 0.4f))
                            .padding(8.dp)
                    ) {
                        Text(
                            text = "🇯🇵 ${item.exampleSentence}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeutralText,
                                fontSize = 12.sp
                            )
                        )
                        if (item.exampleMeaning.isNotEmpty()) {
                            Text(
                                text = "🇻🇳 ${item.exampleMeaning}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontFamily = LoraFontFamily,
                                    color = NeutralSubtext,
                                    fontSize = 11.5.sp
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

/**
 * Card xem trước Kanji (Mặt trước: Hán tự & On/Kun ── Mặt sau: Hán Việt & Từ ghép)
 */
@Composable
private fun KanjiPreviewCard(
    item: KanjiItem,
    index: Int,
    isMastered: Boolean,
    onToggleMastered: () -> Unit
) {
    Gamified3DCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("preview_kanji_$index"),
        backgroundColor = if (isMastered) Color(0xFFF6FFF0) else NeutralWhite,
        borderColor = if (isMastered) GamifyGreen else NeutralBorder,
        borderWidth = 1.5.dp
    ) {
        Column(
            modifier = Modifier.padding(12.dp),
            verticalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    modifier = Modifier.weight(1f),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(RoundedMd)
                            .background(GamifyRedLight)
                            .border(1.5.dp, GamifyRed, RoundedMd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = item.kanji,
                            style = MaterialTheme.typography.titleLarge.copy(
                                fontWeight = FontWeight.Bold,
                                color = GamifyRedDark,
                                fontSize = 24.sp
                            )
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = item.hanViet,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeutralText,
                                    fontSize = 15.sp
                                )
                            )
                            Text(
                                text = "(${item.strokeCount} nét)",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = NeutralSubtext,
                                    fontSize = 10.sp
                                )
                            )
                        }

                        Text(
                            text = "On: ${item.onyomi} • Kun: ${item.kunyomi}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NeutralSubtext,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                MasteredTogglePill(
                    isMastered = isMastered,
                    testTag = "btn_master_kanji_$index",
                    onClick = onToggleMastered
                )

                Spacer(modifier = Modifier.width(6.dp))

                JapaneseAudioSpeakerButton(
                    textToSpeak = "${item.kanji}。${item.exampleCompound}",
                    size = 38.dp,
                    iconSize = 18.dp,
                    primaryColor = GamifyRed,
                    backgroundColor = GamifyRedLight,
                    testTag = "btn_tts_kanji_$index"
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedMd)
                    .background(Color(0xFFF9FAFB))
                    .padding(horizontal = 8.dp, vertical = 6.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                    Text(
                        text = "Ý nghĩa: ${item.meaning}",
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontWeight = FontWeight.Medium,
                            color = NeutralText,
                            fontSize = 12.sp
                        )
                    )
                    Text(
                        text = "Từ ghép: ${item.exampleCompound} (${item.exampleCompoundMeaning})",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GamifyRedDark,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun MasteredTogglePill(
    isMastered: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedFull)
            .background(if (isMastered) GamifyGreen else Color(0xFFF3F4F6))
            .border(1.dp, if (isMastered) GamifyGreenDark else NeutralBorder, RoundedFull)
            .clickable(onClick = onClick)
            .padding(horizontal = 8.dp, vertical = 5.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = if (isMastered) "✓ Đã thuộc" else "Thuộc",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isMastered) Color.White else NeutralSubtext,
                fontSize = 10.sp
            )
        )
    }
}
