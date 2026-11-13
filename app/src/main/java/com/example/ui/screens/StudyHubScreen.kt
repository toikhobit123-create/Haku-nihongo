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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Translate
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.itemsIndexed
import com.example.data.model.GrammarLesson
import com.example.data.model.GrammarPoint
import com.example.data.model.KanjiItem
import com.example.data.model.StudyCategory
import com.example.data.model.VocabLesson
import com.example.data.model.VocabularyItem
import com.example.ui.components.Gamified3DButton
import com.example.ui.components.Gamified3DCard
import com.example.ui.state.NihongoUiState
import com.example.ui.state.NihongoViewModel
import com.example.ui.state.Screen
import com.example.ui.theme.GamifyBlue
import com.example.ui.theme.GamifyBlueDark
import com.example.ui.theme.GamifyBlueLight
import com.example.ui.theme.GamifyBlueShadow
import com.example.ui.theme.GamifyGreen
import com.example.ui.theme.GamifyGreenDark
import com.example.ui.theme.GamifyGreenLight
import com.example.ui.theme.GamifyGreenShadow
import com.example.ui.theme.GamifyRed
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

@Composable
fun StudyHubScreen(
    uiState: NihongoUiState,
    viewModel: NihongoViewModel,
    modifier: Modifier = Modifier,
    onAskSenseiAboutVocab: ((String) -> Unit)? = null
) {
    BackHandler {
        viewModel.goBack()
    }

    var aiExplanationText by remember { mutableStateOf<String?>(null) }
    var aiLoadingGrammarId by remember { mutableStateOf<String?>(null) }

    val filteredGrammar = remember(
        uiState.grammarPoints,
        uiState.selectedGrammarLessonId,
        uiState.grammarSearchQuery
    ) {
        var list = uiState.grammarPoints
        if (uiState.selectedGrammarLessonId != null) {
            list = list.filter { it.lessonId == uiState.selectedGrammarLessonId }
        }
        if (uiState.grammarSearchQuery.isNotBlank()) {
            val query = uiState.grammarSearchQuery.trim().lowercase()
            list = list.filter {
                it.title.lowercase().contains(query) ||
                        it.meaning.lowercase().contains(query) ||
                        it.structure.lowercase().contains(query) ||
                        it.lessonTitle.lowercase().contains(query) ||
                        it.examples.any { ex -> ex.jp.lowercase().contains(query) || ex.vn.lowercase().contains(query) }
            }
        }
        list
    }

    val filteredVocabulary = remember(
        uiState.vocabularyList,
        uiState.selectedVocabLessonId,
        uiState.vocabSearchQuery
    ) {
        var list = uiState.vocabularyList
        if (uiState.selectedVocabLessonId != null) {
            list = list.filter { it.lessonId == uiState.selectedVocabLessonId }
        }
        if (uiState.vocabSearchQuery.isNotBlank()) {
            val q = uiState.vocabSearchQuery.trim().lowercase()
            list = list.filter {
                it.kanji.lowercase().contains(q) ||
                        it.hiragana.lowercase().contains(q) ||
                        it.meaning.lowercase().contains(q)
            }
        }
        list
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralWhite)
            .padding(horizontal = 16.dp, vertical = 12.dp)
    ) {
        // Top Bar
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            IconButton(
                onClick = { viewModel.goBack() },
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedMd)
                    .background(NeutralCard)
                    .border(1.5.dp, NeutralBorder, RoundedMd)
                    .testTag("btn_study_back_home")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back to Home",
                    tint = NeutralText
                )
            }

            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = "3 MỤC HỌC TẬP JLPT N3",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeutralText,
                        fontSize = 17.sp
                    )
                )
                Text(
                    text = "Ngữ pháp • Từ vựng • Kanji",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = NeutralSubtext,
                        fontSize = 11.sp
                    )
                )
            }

            Box(
                modifier = Modifier
                    .clip(RoundedFull)
                    .background(GamifyGreenLight)
                    .border(1.5.dp, GamifyGreen, RoundedFull)
                    .padding(horizontal = 10.dp, vertical = 4.dp)
            ) {
                Text(
                    text = "${uiState.score} PTS",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = GamifyGreenDark,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // ========================================================
        // 3-CATEGORY SEGMENTED TAB SELECTOR (Gamified 3D Pills)
        // ========================================================
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedLg)
                .background(NeutralCard)
                .border(2.dp, NeutralBorder, RoundedLg)
                .padding(4.dp),
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            StudyCategoryTabItem(
                title = "Ngữ pháp",
                subtitle = "文法 (${uiState.grammarPoints.size})",
                icon = Icons.AutoMirrored.Filled.MenuBook,
                isSelected = uiState.selectedStudyCategory == StudyCategory.GRAMMAR,
                activeColor = GamifyGreen,
                activeShadow = GamifyGreenShadow,
                modifier = Modifier.weight(1f),
                testTag = "tab_grammar",
                onClick = { viewModel.selectStudyCategory(StudyCategory.GRAMMAR) }
            )

            StudyCategoryTabItem(
                title = "Từ vựng",
                subtitle = "語彙 (${uiState.vocabularyList.size})",
                icon = Icons.Default.Translate,
                isSelected = uiState.selectedStudyCategory == StudyCategory.VOCABULARY,
                activeColor = GamifyBlue,
                activeShadow = GamifyBlueShadow,
                modifier = Modifier.weight(1f),
                testTag = "tab_vocabulary",
                onClick = { viewModel.selectStudyCategory(StudyCategory.VOCABULARY) }
            )

            StudyCategoryTabItem(
                title = "Kanji",
                subtitle = "漢字 (${uiState.kanjiList.size})",
                icon = Icons.Default.Spellcheck,
                isSelected = uiState.selectedStudyCategory == StudyCategory.KANJI,
                activeColor = GamifyRed,
                activeShadow = Color(0xFFEA1515),
                modifier = Modifier.weight(1f),
                testTag = "tab_kanji",
                onClick = { viewModel.selectStudyCategory(StudyCategory.KANJI) }
            )
        }

        Spacer(modifier = Modifier.height(12.dp))

        // Content Area depending on Selected Category
        when (uiState.selectedStudyCategory) {
            StudyCategory.GRAMMAR -> {
                // Section 1: NGỮ PHÁP (27 Bài học, 134 cấu trúc ngữ pháp JLPT N3)
                GrammarSectionView(
                    grammarPoints = filteredGrammar,
                    totalGrammarCount = uiState.grammarPoints.size,
                    lessons = uiState.grammarLessons,
                    selectedLessonId = uiState.selectedGrammarLessonId,
                    searchQuery = uiState.grammarSearchQuery,
                    onSelectLesson = { viewModel.selectGrammarLesson(it) },
                    onSearchChange = { viewModel.setGrammarSearchQuery(it) },
                    onPracticeQuiz = { grammarPoint ->
                        viewModel.startQuizForGrammar(grammarPoint.id)
                    },
                    aiExplanationText = aiExplanationText,
                    aiLoadingGrammarId = aiLoadingGrammarId,
                    onRequestAiExplanation = { grammarPoint ->
                        aiLoadingGrammarId = grammarPoint.id
                        val qPrompt = "Cấu trúc: ${grammarPoint.structure}. Ý nghĩa: ${grammarPoint.meaning}. Ví dụ: ${grammarPoint.examples.firstOrNull()?.jp ?: ""}"
                        viewModel.explainCurrentGrammarErrorWithAi { explanation ->
                            aiExplanationText = "【${grammarPoint.title}】\n$explanation"
                            aiLoadingGrammarId = null
                        }
                    }
                )
            }
            StudyCategory.VOCABULARY -> {
                // Section 2: TỪ VỰNG (Nằm trong 1 mục lớn có thể mở ra để xem 23 Bài học & 4 chế độ ôn tập)
                VocabularySectionView(
                    vocabularyList = filteredVocabulary,
                    totalVocabCount = uiState.vocabularyList.size,
                    lessons = uiState.vocabLessons,
                    selectedLessonId = uiState.selectedVocabLessonId,
                    searchQuery = uiState.vocabSearchQuery,
                    srsStatsVersion = uiState.srsStats.masteredCount,
                    onSelectLesson = { viewModel.selectVocabLesson(it) },
                    onSearchChange = { viewModel.setVocabSearchQuery(it) },
                    onStartFlashcard = { lessonId ->
                        if (lessonId != null) {
                            viewModel.startSrsForLesson(StudyCategory.VOCABULARY, lessonId, Screen.STUDY_HUB)
                        } else {
                            viewModel.startSrsForCategory(StudyCategory.VOCABULARY, Screen.STUDY_HUB)
                        }
                    },
                    onStartPractice = { lessonId ->
                        viewModel.startPracticeForLesson(
                            category = StudyCategory.VOCABULARY,
                            lessonId = lessonId ?: 1,
                            returnScreen = Screen.STUDY_HUB
                        )
                    },
                    onStartTest = { lessonId ->
                        viewModel.startTestForLesson(
                            category = StudyCategory.VOCABULARY,
                            lessonId = lessonId ?: 1,
                            returnScreen = Screen.STUDY_HUB
                        )
                    },
                    onStartMatch = { lessonId ->
                        viewModel.startCardBattleForLesson(
                            category = StudyCategory.VOCABULARY,
                            lessonId = lessonId ?: 1,
                            returnScreen = Screen.STUDY_HUB
                        )
                    },
                    onToggleMastered = { vocabId ->
                        viewModel.toggleCardMastered(vocabId, com.example.data.model.SrsItemType.VOCABULARY)
                    },
                    onAskSensei = { vocab ->
                        val prompt = "Sensei ơi, giải thích giúp em từ vựng N3「${vocab.kanji}」(${vocab.hiragana}) mang nghĩa là「${vocab.meaning}」. Hãy nêu rõ sắc thái, các cụm từ hay đi kèm và 2 câu ví dụ thực tế chuẩn JLPT N3 nhé!"
                        if (onAskSenseiAboutVocab != null) {
                            onAskSenseiAboutVocab(prompt)
                        } else {
                            viewModel.navigateTo(Screen.AI_CHAT)
                        }
                    }
                )
            }
            StudyCategory.KANJI -> {
                // Section 3: KANJI
                KanjiSectionView(
                    kanjiList = uiState.kanjiList
                )
            }
        }
    }
}

/**
 * 3D Tab Item for Category Selection
 */
@Composable
private fun StudyCategoryTabItem(
    title: String,
    subtitle: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    activeShadow: Color,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedMd)
            .background(if (isSelected) activeColor else Color.Transparent)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 4.dp)
            .then(if (testTag.isNotEmpty()) Modifier.testTag(testTag) else Modifier),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(2.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = title,
                tint = if (isSelected) Color.White else NeutralSubtext,
                modifier = Modifier.size(18.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = if (isSelected) Color.White else NeutralText,
                    fontSize = 13.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = if (isSelected) Color.White.copy(alpha = 0.85f) else NeutralSubtext,
                    fontSize = 10.sp
                )
            )
        }
    }
}

/**
 * MỤC 1: NGỮ PHÁP (GRAMMAR SECTION)
 * Chứa 27 Bài học với 134 cấu trúc ngữ pháp JLPT N3 đầy đủ.
 */
@Composable
private fun GrammarSectionView(
    grammarPoints: List<GrammarPoint>,
    totalGrammarCount: Int,
    lessons: List<GrammarLesson>,
    selectedLessonId: Int?,
    searchQuery: String,
    onSelectLesson: (Int?) -> Unit,
    onSearchChange: (String) -> Unit,
    onPracticeQuiz: (GrammarPoint) -> Unit,
    aiExplanationText: String?,
    aiLoadingGrammarId: String?,
    onRequestAiExplanation: (GrammarPoint) -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Search Box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Tìm ngữ pháp, cấu trúc, ý nghĩa...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = NeutralSubtext,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear",
                            tint = NeutralSubtext,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("input_search_grammar"),
            shape = RoundedMd,
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GamifyGreen,
                unfocusedBorderColor = NeutralBorder
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search)
        )

        // Horizontal Scrollable Row of 27 Lesson Chips (Gamified 3D Pills)
        LazyRow(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // "Tất cả" chip
            item {
                val isAllSelected = selectedLessonId == null
                Box(
                    modifier = Modifier
                        .clip(RoundedFull)
                        .background(if (isAllSelected) GamifyGreen else NeutralCard)
                        .border(
                            1.5.dp,
                            if (isAllSelected) GamifyGreenDark else NeutralBorder,
                            RoundedFull
                        )
                        .clickable { onSelectLesson(null) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("chip_grammar_lesson_all"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Tất cả ($totalGrammarCount)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isAllSelected) Color.White else NeutralText,
                            fontSize = 11.sp
                        )
                    )
                }
            }

            // 27 Lesson chips
            items(lessons, key = { it.lessonId }) { lesson ->
                val isSelected = selectedLessonId == lesson.lessonId
                Box(
                    modifier = Modifier
                        .clip(RoundedFull)
                        .background(if (isSelected) GamifyGreen else NeutralCard)
                        .border(
                            1.5.dp,
                            if (isSelected) GamifyGreenDark else NeutralBorder,
                            RoundedFull
                        )
                        .clickable { onSelectLesson(lesson.lessonId) }
                        .padding(horizontal = 14.dp, vertical = 6.dp)
                        .testTag("chip_grammar_lesson_${lesson.lessonId}"),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "${lesson.title} (${lesson.grammarList.size})",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isSelected) Color.White else NeutralText,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // AI Global Explanation Box if active
        if (aiExplanationText != null) {
            Gamified3DCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = GamifyBlueLight,
                borderColor = GamifyBlue,
                borderWidth = 2.dp
            ) {
                Column(
                    modifier = Modifier.padding(14.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoAwesome,
                            contentDescription = "AI",
                            tint = GamifyBlueDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "GIẢI THÍCH SÂU TỪ GIÁO VIÊN AI (GEMINI):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GamifyBlueDark
                            )
                        )
                    }
                    Text(
                        text = aiExplanationText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            fontFamily = LoraFontFamily,
                            color = NeutralText,
                            fontSize = 12.sp,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }

        // List of Grammar Points
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            items(grammarPoints, key = { it.id }) { grammarPoint ->
                val isAiLoading = aiLoadingGrammarId == grammarPoint.id

                Gamified3DCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("grammar_card_${grammarPoint.id}"),
                    backgroundColor = NeutralWhite,
                    borderColor = GamifyGreen.copy(alpha = 0.5f),
                    borderWidth = 2.dp
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp),
                        verticalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Header with Lesson badge, ID, and Level
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedSm)
                                        .background(GamifyGreen)
                                        .padding(horizontal = 7.dp, vertical = 2.dp)
                                ) {
                                    Text(
                                        text = grammarPoint.lessonTitle.uppercase(),
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 9.sp
                                        )
                                    )
                                }

                                Text(
                                    text = grammarPoint.id.uppercase(),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = NeutralSubtext,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }

                            Text(
                                text = grammarPoint.level,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GamifyGreenDark
                                )
                            )
                        }

                        // Grammar Title & Speaker
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = grammarPoint.title,
                                style = MaterialTheme.typography.titleLarge.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeutralText,
                                    fontSize = 20.sp
                                ),
                                modifier = Modifier.weight(1f)
                            )

                            JapaneseAudioSpeakerButton(
                                textToSpeak = grammarPoint.title,
                                size = 36.dp,
                                iconSize = 18.dp,
                                primaryColor = GamifyGreen,
                                backgroundColor = GamifyGreenLight,
                                testTag = "btn_tts_grammar_title_${grammarPoint.id}"
                            )
                        }

                        // Structure Chip
                        Box(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedMd)
                                .background(GamifyGreenLight)
                                .border(1.dp, GamifyGreen.copy(alpha = 0.4f), RoundedMd)
                                .padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = "Cấu trúc:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GamifyGreenDark
                                    )
                                )
                                Text(
                                    text = grammarPoint.structure,
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = NeutralText,
                                        fontSize = 13.sp
                                    )
                                )
                            }
                        }

                        // Meaning
                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                            Text(
                                text = "Ý nghĩa:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeutralSubtext
                                )
                            )
                            Text(
                                text = grammarPoint.meaning,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = LoraFontFamily,
                                    color = NeutralText,
                                    lineHeight = 20.sp
                                )
                            )
                        }

                        // Example Sentences with Japanese Audio Playback
                        if (grammarPoint.examples.isNotEmpty()) {
                            Text(
                                text = "Ví dụ minh họa (例文):",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeutralSubtext
                                )
                            )

                            Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                                grammarPoint.examples.forEachIndexed { exIndex, ex ->
                                    Box(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedMd)
                                            .background(NeutralWhite)
                                            .border(1.dp, NeutralBorder, RoundedMd)
                                            .padding(10.dp)
                                    ) {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Column(modifier = Modifier.weight(1f)) {
                                                Text(
                                                    text = ex.jp,
                                                    style = MaterialTheme.typography.bodyMedium.copy(
                                                        fontWeight = FontWeight.Bold,
                                                        color = NeutralText,
                                                        fontSize = 14.sp
                                                    )
                                                )
                                                Spacer(modifier = Modifier.height(2.dp))
                                                Text(
                                                    text = ex.vn,
                                                    style = MaterialTheme.typography.bodySmall.copy(
                                                        fontFamily = LoraFontFamily,
                                                        color = NeutralSubtext,
                                                        fontSize = 12.sp
                                                    )
                                                )
                                            }

                                            Spacer(modifier = Modifier.width(8.dp))

                                            JapaneseAudioSpeakerButton(
                                                textToSpeak = ex.jp,
                                                size = 34.dp,
                                                iconSize = 18.dp,
                                                primaryColor = GamifyBlue,
                                                backgroundColor = GamifyBlueLight,
                                                testTag = "btn_tts_ex_${grammarPoint.id}_$exIndex"
                                            )
                                        }
                                    }
                                }
                            }
                        }

                        // Action Buttons: Practice Quiz & Ask Gemini
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Gamified3DButton(
                                text = "Luyện Tập Câu Này",
                                icon = Icons.Default.PlayArrow,
                                onClick = { onPracticeQuiz(grammarPoint) },
                                backgroundColor = GamifyGreen,
                                shadowColor = GamifyGreenShadow,
                                height = 40.dp,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_practice_quiz_${grammarPoint.id}"
                            )

                            Gamified3DButton(
                                text = if (isAiLoading) "Đang hỏi AI..." else "Hỏi AI Gemini",
                                icon = Icons.Default.AutoAwesome,
                                onClick = { onRequestAiExplanation(grammarPoint) },
                                enabled = !isAiLoading,
                                backgroundColor = GamifyBlue,
                                shadowColor = GamifyBlueShadow,
                                height = 40.dp,
                                modifier = Modifier.weight(1f),
                                testTag = "btn_ai_explain_${grammarPoint.id}"
                            )
                        }
                    }
                }
            }

            item {
                Spacer(modifier = Modifier.height(30.dp))
            }
        }
    }
}

/**
 * MỤC 2: TỪ VỰNG (VOCABULARY SECTION)
 * Từ vựng nằm trong 1 Mục Lớn duy nhất; bấm vào mục lớn để mở danh sách 23 bài học & nội dung từ vựng,
 * đồng thời tích hợp đủ 4 chế độ ôn tập: Flashcard, Học quy nạp, Kiểm tra (Test), Ghép thẻ Nhật <-> Việt.
 */
@Composable
private fun VocabularySectionView(
    vocabularyList: List<VocabularyItem>,
    totalVocabCount: Int,
    lessons: List<VocabLesson>,
    selectedLessonId: Int?,
    searchQuery: String,
    srsStatsVersion: Int,
    onSelectLesson: (Int?) -> Unit,
    onSearchChange: (String) -> Unit,
    onStartFlashcard: (Int?) -> Unit,
    onStartPractice: (Int?) -> Unit,
    onStartTest: (Int?) -> Unit,
    onStartMatch: (Int?) -> Unit,
    onToggleMastered: (String) -> Unit,
    onAskSensei: (VocabularyItem) -> Unit
) {
    var isVocabParentExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // 1. Search Box
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onSearchChange,
            placeholder = { Text("Tìm Hán tự, Hiragana hoặc Nghĩa...", fontSize = 13.sp) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search Vocab",
                    tint = GamifyBlue,
                    modifier = Modifier.size(20.dp)
                )
            },
            trailingIcon = {
                if (searchQuery.isNotEmpty()) {
                    IconButton(onClick = { onSearchChange("") }) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear search",
                            tint = NeutralSubtext,
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            },
            singleLine = true,
            modifier = Modifier
                .fillMaxWidth()
                .testTag("input_vocab_search"),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = GamifyBlue,
                unfocusedBorderColor = NeutralBorder,
                focusedContainerColor = NeutralCard,
                unfocusedContainerColor = NeutralCard
            ),
            shape = RoundedMd
        )

        // 2. Nếu chưa chọn bài cụ thể & chưa tìm kiếm: hiển thị 1 MỤC LỚN TỪ VỰNG
        if (selectedLessonId == null && searchQuery.isBlank()) {
            val masteredTotal = remember(srsStatsVersion, lessons) {
                lessons.sumOf { lesson ->
                    com.example.data.repository.SrsRepository.getLessonMastery(
                        com.example.data.model.SrsItemType.VOCABULARY,
                        lesson.vocabList.map { it.id },
                        lesson.lessonId
                    ).first
                }
            }

            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                item {
                    Gamified3DCard(
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("card_studyhub_vocab_folder"),
                        backgroundColor = GamifyBlueLight,
                        borderColor = GamifyBlue,
                        borderWidth = 2.dp,
                        onClick = { isVocabParentExpanded = !isVocabParentExpanded }
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(modifier = Modifier.weight(1f)) {
                                    Text(
                                        text = "KHO TỪ VỰNG JLPT N3 (語彙)",
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = GamifyBlueDark,
                                            fontSize = 15.sp
                                        )
                                    )
                                    Text(
                                        text = "${lessons.size} bài học • $masteredTotal / $totalVocabCount từ đã thuộc",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = NeutralText,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedFull)
                                        .background(GamifyBlue)
                                        .padding(horizontal = 12.dp, vertical = 6.dp)
                                ) {
                                    Text(
                                        text = if (isVocabParentExpanded) "Thu gọn ▲" else "Mở mục ▼",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = Color.White,
                                            fontWeight = FontWeight.Bold,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }

                            // 4 chế độ ôn tập cho Từ vựng giống hệt Ngữ pháp
                            VocabStudyModeBar(
                                onFlashcard = { onStartFlashcard(null) },
                                onPractice = { onStartPractice(1) },
                                onTest = { onStartTest(1) },
                                onMatch = { onStartMatch(1) }
                            )
                        }
                    }
                }

                if (isVocabParentExpanded) {
                    items(lessons, key = { it.lessonId }) { lesson ->
                        val totalCount = lesson.vocabList.size
                        val (masteredCount, _) = remember(srsStatsVersion, lesson.lessonId) {
                            com.example.data.repository.SrsRepository.getLessonMastery(
                                com.example.data.model.SrsItemType.VOCABULARY,
                                lesson.vocabList.map { it.id },
                                lesson.lessonId
                            )
                        }
                        Gamified3DCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("card_studyhub_vocab_lesson_${lesson.lessonId}"),
                            backgroundColor = NeutralWhite,
                            borderColor = GamifyBlue.copy(alpha = 0.45f),
                            onClick = { onSelectLesson(lesson.lessonId) }
                        ) {
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(14.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Column(
                                    modifier = Modifier.weight(1f),
                                    verticalArrangement = Arrangement.spacedBy(3.dp)
                                ) {
                                    Text(
                                        text = lesson.title,
                                        style = MaterialTheme.typography.titleMedium.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = NeutralText,
                                            fontSize = 15.sp
                                        )
                                    )
                                    Text(
                                        text = "${lesson.vocabList.size} từ vựng • Đã thuộc: $masteredCount/$totalCount",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = NeutralSubtext,
                                            fontSize = 12.sp
                                        )
                                    )
                                }
                                Text(
                                    text = "Xem từ →",
                                    style = MaterialTheme.typography.labelMedium.copy(
                                        color = GamifyBlueDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }
                } else {
                    item {
                        Gamified3DCard(
                            modifier = Modifier.fillMaxWidth(),
                            backgroundColor = NeutralCard,
                            borderColor = NeutralBorder,
                            onClick = { isVocabParentExpanded = true }
                        ) {
                            Text(
                                text = "Chạm vào mục lớn「KHO TỪ VỰNG JLPT N3」ở trên để mở danh sách 23 bài học Từ vựng bên trong, hoặc chọn ngay Flashcard / Luyện tập / Test / Ghép thẻ.",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = NeutralSubtext,
                                    fontSize = 12.sp,
                                    lineHeight = 18.sp
                                ),
                                modifier = Modifier.padding(14.dp)
                            )
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
            return@Column
        }

        // 3. Khi đã bấm vào 1 bài từ vựng hoặc đang tìm kiếm
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            if (selectedLessonId != null) {
                Box(
                    modifier = Modifier
                        .clip(RoundedFull)
                        .background(NeutralCard)
                        .border(1.5.dp, GamifyBlue, RoundedFull)
                        .clickable { onSelectLesson(null) }
                        .padding(horizontal = 12.dp, vertical = 7.dp)
                        .testTag("btn_vocab_back_to_folder")
                ) {
                    Text(
                        text = "← Kho Từ vựng (23 bài)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GamifyBlueDark,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.5.sp
                        )
                    )
                }
            }

            Text(
                text = if (selectedLessonId != null) {
                    "Bài $selectedLessonId • ${vocabularyList.size} từ"
                } else {
                    "Kết quả tìm kiếm: ${vocabularyList.size} từ"
                },
                style = MaterialTheme.typography.labelMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = NeutralText
                )
            )
        }

        if (selectedLessonId != null) {
            Gamified3DCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = GamifyBlueLight,
                borderColor = GamifyBlue
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "ÔN TẬP BÀI $selectedLessonId (TỪ VỰNG)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GamifyBlueDark
                        )
                    )
                    VocabStudyModeBar(
                        onFlashcard = { onStartFlashcard(selectedLessonId) },
                        onPractice = { onStartPractice(selectedLessonId) },
                        onTest = { onStartTest(selectedLessonId) },
                        onMatch = { onStartMatch(selectedLessonId) }
                    )
                }
            }
        }

        // 4. Vocabulary Cards List (bên trong bài học đã chọn hoặc kết quả search)
        if (vocabularyList.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Text(
                        text = "Không tìm thấy từ vựng phù hợp",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeutralText
                        )
                    )
                    Text(
                        text = "Thử nhập từ khóa khác hoặc chọn bài học khác nhé!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NeutralSubtext,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                items(vocabularyList, key = { it.id }) { vocab ->
                    val isMastered = remember(srsStatsVersion, vocab.id) {
                        com.example.data.repository.SrsRepository.isCardMastered(vocab.id)
                    }
                    Gamified3DCard(
                        modifier = Modifier.fillMaxWidth(),
                        backgroundColor = NeutralWhite,
                        borderColor = if (isMastered) GamifyGreen else NeutralBorder
                    ) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(14.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            // Top Row: Word & Furigana + Badges + Audio Speaker
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(6.dp),
                                    modifier = Modifier.weight(1f)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedSm)
                                            .background(GamifyBlueLight)
                                            .border(1.dp, GamifyBlue.copy(alpha = 0.4f), RoundedSm)
                                            .padding(horizontal = 6.dp, vertical = 2.dp)
                                    ) {
                                        Text(
                                            text = "Bài ${vocab.lessonId}",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = GamifyBlueDark
                                            )
                                        )
                                    }

                                    Box(
                                        modifier = Modifier
                                            .clip(RoundedFull)
                                            .background(if (isMastered) GamifyGreenLight else NeutralCard)
                                            .border(
                                                1.dp,
                                                if (isMastered) GamifyGreen else NeutralBorder,
                                                RoundedFull
                                            )
                                            .clickable { onToggleMastered(vocab.id) }
                                            .padding(horizontal = 8.dp, vertical = 3.dp)
                                            .testTag("btn_vocab_mastered_${vocab.id}")
                                    ) {
                                        Text(
                                            text = if (isMastered) "✓ Đã thuộc" else "Đánh dấu thuộc",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                fontSize = 10.sp,
                                                fontWeight = FontWeight.Bold,
                                                color = if (isMastered) GamifyGreenDark else NeutralSubtext
                                            )
                                        )
                                    }
                                }

                                JapaneseAudioSpeakerButton(
                                    textToSpeak = "${vocab.kanji}。${vocab.hiragana}",
                                    size = 36.dp,
                                    iconSize = 18.dp,
                                    primaryColor = GamifyBlue,
                                    backgroundColor = GamifyBlueLight,
                                    testTag = "btn_tts_vocab_${vocab.id}"
                                )
                            }

                            // Kanji and Hiragana
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text(
                                    text = vocab.kanji,
                                    style = MaterialTheme.typography.titleLarge.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = NeutralText,
                                        fontSize = 20.sp
                                    )
                                )
                                Text(
                                    text = "【${vocab.hiragana}】",
                                    style = MaterialTheme.typography.bodyMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = GamifyBlue,
                                        fontSize = 14.sp
                                    )
                                )
                            }

                            // Meaning
                            Text(
                                text = vocab.meaning,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontFamily = LoraFontFamily,
                                    color = NeutralText,
                                    fontWeight = FontWeight.Normal,
                                    fontSize = 14.sp
                                )
                            )

                            // Action: Ask Sensei Akira about this word
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.End
                            ) {
                                Box(
                                    modifier = Modifier
                                        .clip(RoundedFull)
                                        .background(NeutralCard)
                                        .border(1.dp, NeutralBorder, RoundedFull)
                                        .clickable { onAskSensei(vocab) }
                                        .padding(horizontal = 10.dp, vertical = 4.dp)
                                        .testTag("btn_ask_sensei_${vocab.id}")
                                ) {
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.AutoAwesome,
                                            contentDescription = "Ask Sensei",
                                            tint = GamifyBlue,
                                            modifier = Modifier.size(13.dp)
                                        )
                                        Text(
                                            text = "Hỏi Sensei Akira",
                                            style = MaterialTheme.typography.labelSmall.copy(
                                                color = GamifyBlueDark,
                                                fontWeight = FontWeight.Bold,
                                                fontSize = 11.sp
                                            )
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(30.dp))
                }
            }
        }
    }
}

@Composable
private fun VocabStudyModeBar(
    onFlashcard: () -> Unit,
    onPractice: () -> Unit,
    onTest: () -> Unit,
    onMatch: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        val items = listOf(
            Triple("🃏 Flashcard", GamifyBlue, onFlashcard),
            Triple("📝 Luyện tập", GamifyGreen, onPractice),
            Triple("✍️ Test", GamifyYellow, onTest),
            Triple("🧩 Ghép thẻ", GamifyRed, onMatch)
        )
        items.forEachIndexed { idx, (label, color, action) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .clip(RoundedMd)
                    .background(NeutralWhite)
                    .border(1.5.dp, color, RoundedMd)
                    .clickable(onClick = action)
                    .padding(vertical = 8.dp, horizontal = 4.dp)
                    .testTag("btn_vocab_mode_$idx"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = label,
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeutralText,
                        fontSize = 10.5.sp
                    ),
                    maxLines = 1
                )
            }
        }
    }
}

/**
 * MỤC 3: KANJI (KANJI SECTION)
 */
@Composable
private fun KanjiSectionView(
    kanjiList: List<KanjiItem>
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        item {
            Gamified3DCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = Color(0xFFFFF7F7),
                borderColor = GamifyRed.copy(alpha = 0.5f)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = "DANH SÁCH HÁN TỰ N3 (漢字)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GamifyRed
                        )
                    )
                    Text(
                        text = "Tra cứu âm Hán Việt, Onyomi, Kunyomi và từ ghép đi kèm phát âm.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NeutralText,
                            fontSize = 12.sp
                        )
                    )
                }
            }
        }

        items(kanjiList, key = { it.id }) { kanji ->
            Gamified3DCard(
                modifier = Modifier.fillMaxWidth(),
                backgroundColor = NeutralWhite,
                borderColor = NeutralBorder
            ) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(14.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Kanji Character Box
                    Box(
                        modifier = Modifier
                            .size(60.dp)
                            .clip(RoundedMd)
                            .background(NeutralCard)
                            .border(1.5.dp, NeutralBorder, RoundedMd),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = kanji.kanji,
                            style = MaterialTheme.typography.headlineMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeutralText,
                                fontSize = 32.sp
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    // Kanji Details
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "Hán Việt: ${kanji.hanViet}",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = GamifyRed,
                                    fontSize = 13.sp
                                )
                            )
                            Text(
                                text = "• ${kanji.strokeCount} nét",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = NeutralSubtext,
                                    fontSize = 11.sp
                                )
                            )
                        }

                        Text(
                            text = "Nghĩa: ${kanji.meaning}",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = FontWeight.Medium,
                                color = NeutralText,
                                fontSize = 13.sp
                            )
                        )

                        Text(
                            text = "On: ${kanji.onyomi} | Kun: ${kanji.kunyomi}",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NeutralSubtext,
                                fontSize = 11.sp
                            )
                        )

                        Text(
                            text = "Từ ghép: ${kanji.exampleCompound} (${kanji.exampleCompoundMeaning})",
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontFamily = LoraFontFamily,
                                color = GamifyGreenDark,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(6.dp))

                    JapaneseAudioSpeakerButton(
                        textToSpeak = "${kanji.kanji}。${kanji.exampleCompound}",
                        size = 38.dp,
                        iconSize = 18.dp,
                        primaryColor = GamifyRed,
                        backgroundColor = Color(0xFFFFECEC),
                        testTag = "btn_tts_kanji_${kanji.id}"
                    )
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}
