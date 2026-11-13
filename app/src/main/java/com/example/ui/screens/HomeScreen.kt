package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
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
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.Casino
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Diamond
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Spellcheck
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material.icons.filled.Translate
import com.example.data.model.SrsItemType
import com.example.data.model.SrsStatsSummary
import com.example.data.model.StudyCategory
import com.example.data.repository.SrsRepository
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.ui.components.Gamified3DButton
import com.example.ui.components.Gamified3DCard
import com.example.ui.components.GamifiedProgressBar
import com.example.ui.state.NihongoUiState
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
import com.example.ui.theme.GamifyRedLight
import com.example.ui.theme.GamifyRedShadow
import com.example.ui.theme.GamifyYellow
import com.example.ui.theme.GamifyYellowShadow
import com.example.ui.theme.NeutralBorder
import com.example.ui.theme.NeutralCard
import com.example.ui.theme.NeutralDisabled
import com.example.ui.theme.NeutralSubtext
import com.example.ui.theme.NeutralText
import com.example.ui.theme.NeutralWhite
import com.example.ui.theme.LoraFontFamily
import com.example.ui.theme.RoundedFull
import com.example.ui.theme.RoundedLg
import com.example.ui.theme.RoundedMd
import com.example.ui.theme.RoundedSm
import com.example.util.JapaneseAudioSpeakerButton

@Composable
fun HomeScreen(
    uiState: NihongoUiState,
    onNavigate: (Screen) -> Unit,
    onSelectStudyCategory: (StudyCategory) -> Unit = {},
    onOpenLesson: (StudyCategory, Int, String) -> Unit = { _, _, _ -> },
    onStartCategoryFlashcard: (StudyCategory) -> Unit = {},
    onStartCategoryTest: (StudyCategory) -> Unit = {},
    onStartCategoryMatch: (StudyCategory) -> Unit = {},
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val selectedCategoryTab = uiState.selectedStudyCategory
    var isVocabParentExpanded by remember { mutableStateOf(false) }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralWhite)
            .verticalScroll(scrollState)
            .padding(horizontal = 20.dp, vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(18.dp)
    ) {
        // Hero Unit Card (Duolingo style unit header)
        Gamified3DCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = GamifyGreen,
            borderColor = GamifyGreenShadow,
            borderWidth = 3.dp
        ) {
            Box(modifier = Modifier.fillMaxWidth()) {
                Image(
                    painter = painterResource(id = R.drawable.nihongo_banner_1793236649505),
                    contentDescription = "Learning Banner",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                )
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(130.dp)
                        .background(GamifyGreen.copy(alpha = 0.72f))
                        .padding(16.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .clip(RoundedSm)
                                    .background(Color.White)
                                    .padding(horizontal = 8.dp, vertical = 2.dp)
                            ) {
                                Text(
                                    text = "CHƯƠNG 1",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = GamifyGreenDark,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                            Text(
                                text = "LỘ TRÌNH JLPT N3",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    color = Color.White,
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Học Quy Nạp & Đấu Trường Thẻ Bài",
                            style = MaterialTheme.typography.titleLarge.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 18.sp
                            )
                        )
                    }
                }
            }
        }

        // Daily Progress Bar Card
        Gamified3DCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = NeutralWhite,
            borderColor = NeutralBorder
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "TIẾN ĐỘ BÀI HỌC HÔM NAY",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeutralSubtext
                        ),
                        modifier = Modifier.weight(1f, fill = false),
                        maxLines = 1,
                        softWrap = false,
                        overflow = TextOverflow.Ellipsis
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = "${uiState.score} / 300 PTS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GamifyBlue
                        ),
                        maxLines = 1,
                        softWrap = false
                    )
                }
                GamifiedProgressBar(
                    progress = (uiState.score.toFloat() / 300f).coerceIn(0.1f, 1f),
                    fillColor = GamifyGreen,
                    height = 12.dp
                )
            }
        }

        // ========================================================
        // BƯỚC 1: XÂY DỰNG MÀN HÌNH DANH SÁCH BÀI HỌC (STUDY SET GRID VIEW)
        // ========================================================
        Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
            // Header tiêu đề
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "BỘ BÀI HỌC JLPT N3 (STUDY SETS)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeutralText,
                        fontSize = 15.sp,
                        letterSpacing = 0.5.sp
                    ),
                    modifier = Modifier.weight(1f, fill = false),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                Box(
                    modifier = Modifier
                        .clip(RoundedSm)
                        .background(GamifyGreen.copy(alpha = 0.12f))
                        .clickable { onNavigate(Screen.STUDY_HUB) }
                        .padding(horizontal = 8.dp, vertical = 4.dp)
                        .testTag("btn_see_all_study_hub")
                ) {
                    Text(
                        text = "Kho tổng hợp",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GamifyGreenDark,
                            fontSize = 12.sp
                        )
                    )
                }
            }

            // 1. Tab phân loại top: Giữ 3 nút lọc chính ở trên cùng: Ngữ pháp, Từ vựng, Kanji
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                StudyCategoryFilterTab(
                    title = "Ngữ pháp (${uiState.grammarLessons.size})",
                    icon = Icons.AutoMirrored.Filled.MenuBook,
                    isSelected = selectedCategoryTab == StudyCategory.GRAMMAR,
                    activeColor = GamifyGreen,
                    activeColorShadow = GamifyGreenShadow,
                    modifier = Modifier.weight(1f),
                    testTag = "tab_filter_grammar",
                    onClick = {
                        onSelectStudyCategory(StudyCategory.GRAMMAR)
                    }
                )

                StudyCategoryFilterTab(
                    title = "Từ vựng (${uiState.vocabLessons.size})",
                    icon = Icons.Default.Translate,
                    isSelected = selectedCategoryTab == StudyCategory.VOCABULARY,
                    activeColor = GamifyBlue,
                    activeColorShadow = GamifyBlueShadow,
                    modifier = Modifier.weight(1f),
                    testTag = "tab_filter_vocab",
                    onClick = {
                        onSelectStudyCategory(StudyCategory.VOCABULARY)
                    }
                )

                StudyCategoryFilterTab(
                    title = "Kanji (${uiState.kanjiLessons.size})",
                    icon = Icons.Default.Spellcheck,
                    isSelected = selectedCategoryTab == StudyCategory.KANJI,
                    activeColor = GamifyRed,
                    activeColorShadow = GamifyRedShadow,
                    modifier = Modifier.weight(1f),
                    testTag = "tab_filter_kanji",
                    onClick = {
                        onSelectStudyCategory(StudyCategory.KANJI)
                    }
                )
            }

            // 2. Danh sách Bài học (Grid Layout các Card 3D lớn)
            Column(
                modifier = Modifier.fillMaxWidth(),
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                when (selectedCategoryTab) {
                    StudyCategory.GRAMMAR -> {
                        uiState.grammarLessons.forEach { lesson ->
                            StudySetCard(
                                category = StudyCategory.GRAMMAR,
                                lessonId = lesson.lessonId,
                                title = lesson.title,
                                itemCount = lesson.grammarList.size,
                                itemCountLabel = "cấu trúc ngữ pháp",
                                cardIds = lesson.grammarList.map { it.id },
                                srsStats = uiState.srsStats,
                                onClick = {
                                    onOpenLesson(StudyCategory.GRAMMAR, lesson.lessonId, lesson.title)
                                }
                            )
                        }
                    }
                    StudyCategory.VOCABULARY -> {
                        // YÊU CẦU 2 & 3: Phần Từ vựng nằm trong 1 Mục Lớn -> Bấm vô mới hiện nội dung bài học từ vựng bên trong,
                        // kèm đầy đủ chế độ ôn tập (Flashcard, Test, Ghép thẻ) giống Ngữ pháp!
                        val totalVocab = uiState.vocabularyList.size
                        val masteredVocab = uiState.srsStats.masteredVocab
                        val vocabPct = if (totalVocab > 0) (masteredVocab.toFloat() / totalVocab.toFloat()) * 100f else 0f

                        Gamified3DCard(
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("card_vocab_parent_folder"),
                            backgroundColor = GamifyBlueLight.copy(alpha = 0.45f),
                            borderColor = GamifyBlue,
                            borderWidth = 2.5.dp,
                            onClick = { isVocabParentExpanded = !isVocabParentExpanded }
                        ) {
                            Column(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
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
                                        Box(
                                            modifier = Modifier
                                                .size(44.dp)
                                                .clip(RoundedMd)
                                                .background(GamifyBlue),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Icon(
                                                imageVector = Icons.Default.Translate,
                                                contentDescription = "Kho Từ Vựng N3",
                                                tint = Color.White,
                                                modifier = Modifier.size(24.dp)
                                            )
                                        }
                                        Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                                            Text(
                                                text = "KHO TỪ VỰNG JLPT N3 (語彙)",
                                                style = MaterialTheme.typography.titleMedium.copy(
                                                    fontWeight = FontWeight.Bold,
                                                    color = GamifyBlueDark,
                                                    fontSize = 15.5.sp
                                                )
                                            )
                                            Text(
                                                text = "${uiState.vocabLessons.size} bài học • $totalVocab từ vựng • Đã thuộc $masteredVocab",
                                                style = MaterialTheme.typography.bodySmall.copy(
                                                    color = NeutralText,
                                                    fontSize = 12.sp
                                                )
                                            )
                                        }
                                    }

                                    Box(
                                        modifier = Modifier
                                            .size(36.dp)
                                            .clip(CircleShape)
                                            .background(NeutralWhite)
                                            .border(1.5.dp, GamifyBlue, CircleShape),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Icon(
                                            imageVector = if (isVocabParentExpanded) Icons.Default.ExpandLess else Icons.Default.ExpandMore,
                                            contentDescription = if (isVocabParentExpanded) "Thu gọn từ vựng" else "Mở danh sách từ vựng",
                                            tint = GamifyBlueDark
                                        )
                                    }
                                }

                                GamifiedProgressBar(
                                    progress = (vocabPct / 100f).coerceIn(0.03f, 1f),
                                    fillColor = GamifyBlue,
                                    height = 8.dp
                                )

                                // Thanh chế độ ôn tập nhanh cho Từ vựng: Flashcard, Test, Ghép thẻ
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    VocabQuickModePill(
                                        emoji = "🃏",
                                        label = "Flashcard",
                                        color = GamifyBlue,
                                        modifier = Modifier.weight(1f),
                                        testTag = "btn_vocab_parent_flashcard",
                                        onClick = { onStartCategoryFlashcard(StudyCategory.VOCABULARY) }
                                    )
                                    VocabQuickModePill(
                                        emoji = "✍️",
                                        label = "Test",
                                        color = GamifyGreen,
                                        modifier = Modifier.weight(1f),
                                        testTag = "btn_vocab_parent_test",
                                        onClick = { onStartCategoryTest(StudyCategory.VOCABULARY) }
                                    )
                                    VocabQuickModePill(
                                        emoji = "🧩",
                                        label = "Ghép thẻ",
                                        color = GamifyRed,
                                        modifier = Modifier.weight(1f),
                                        testTag = "btn_vocab_parent_match",
                                        onClick = { onStartCategoryMatch(StudyCategory.VOCABULARY) }
                                    )
                                }

                                Text(
                                    text = if (isVocabParentExpanded) {
                                        "▲ Chạm để thu gọn ${uiState.vocabLessons.size} bài từ vựng"
                                    } else {
                                        "▼ Chạm vào mục này để mở ${uiState.vocabLessons.size} bài từ vựng bên trong"
                                    },
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GamifyBlueDark,
                                        fontSize = 11.5.sp
                                    )
                                )
                            }
                        }

                        AnimatedVisibility(visible = isVocabParentExpanded) {
                            Column(
                                modifier = Modifier.fillMaxWidth(),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                uiState.vocabLessons.forEach { lesson ->
                                    StudySetCard(
                                        category = StudyCategory.VOCABULARY,
                                        lessonId = lesson.lessonId,
                                        title = lesson.title,
                                        itemCount = lesson.vocabList.size,
                                        itemCountLabel = "từ vựng N3",
                                        cardIds = lesson.vocabList.map { it.id },
                                        srsStats = uiState.srsStats,
                                        onClick = {
                                            onOpenLesson(StudyCategory.VOCABULARY, lesson.lessonId, lesson.title)
                                        }
                                    )
                                }
                            }
                        }
                    }
                    StudyCategory.KANJI -> {
                        uiState.kanjiLessons.forEach { lesson ->
                            StudySetCard(
                                category = StudyCategory.KANJI,
                                lessonId = lesson.lessonId,
                                title = lesson.title,
                                itemCount = lesson.kanjiList.size,
                                itemCountLabel = "chữ Hán N3",
                                cardIds = lesson.kanjiList.map { it.id },
                                srsStats = uiState.srsStats,
                                onClick = {
                                    onOpenLesson(StudyCategory.KANJI, lesson.lessonId, lesson.title)
                                }
                            )
                        }
                    }
                }
            }
        }

        // ==========================================
        // SENSEI AKIRA & TRỢ LÝ AI (GEMINI AI CHAT)
        // ==========================================
        Gamified3DCard(
            modifier = Modifier
                .fillMaxWidth()
                .testTag("home_card_gemini_sensei"),
            backgroundColor = Color(0xFFF0FDF4),
            borderColor = GamifyGreen,
            borderWidth = 2.5.dp,
            onClick = { onNavigate(Screen.AI_CHAT) }
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(GamifyGreenLight)
                        .border(2.dp, GamifyGreen, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "👨‍🏫", fontSize = 22.sp)
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Text(
                            text = "HỎI SENSEI AKIRA (GEMINI AI)",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GamifyGreenDark,
                                fontSize = 11.sp
                            )
                        )
                        Box(
                            modifier = Modifier
                                .clip(RoundedSm)
                                .background(GamifyBlue)
                                .padding(horizontal = 6.dp, vertical = 1.dp)
                        ) {
                            Text(
                                text = "SEARCH LIVE",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White,
                                    fontSize = 9.sp
                                )
                            )
                        }
                    }
                    Text(
                        text = "Giải đáp ngữ pháp & Luyện Kaiwa N3",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeutralText,
                            fontSize = 13.5.sp
                        )
                    )
                }

                Gamified3DButton(
                    text = "Chat",
                    onClick = { onNavigate(Screen.AI_CHAT) },
                    backgroundColor = GamifyGreen,
                    shadowColor = GamifyGreenShadow,
                    height = 38.dp,
                    shape = RoundedMd,
                    modifier = Modifier.width(66.dp),
                    testTag = "btn_home_chat_now"
                )
            }
        }

        // ========================================================
        // GOOGLE AI STUDIO (GEMINI API) SETUP CARD
        // ========================================================
        var inputApiKey by remember { mutableStateOf(com.example.data.service.AiService.API_KEY) }
        var isKeySaved by remember { mutableStateOf(false) }

        Gamified3DCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = GamifyBlueLight,
            borderColor = GamifyBlue,
            borderWidth = 2.dp,
            testTag = "ai_studio_setup_card"
        ) {
            Column(
                modifier = Modifier.padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(10.dp)
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
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "AI Ready",
                            tint = GamifyBlue,
                            modifier = Modifier.size(18.dp)
                        )
                        Text(
                            text = "GOOGLE AI STUDIO (GEMINI API)",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = GamifyBlue
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedSm)
                            .background(GamifyGreen)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "KẾT NỐI THỰC",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontSize = 9.sp,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }

                Text(
                    text = "Đã cấu hình sẵn với system_instruction trong AiService.kt:\n• explainGrammarError(question, wrongAnswer)\n• Sensei Akira Kaiwa & JLPT N3 Grounding Chat",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeutralText,
                        fontSize = 12.sp,
                        lineHeight = 18.sp
                    )
                )

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputApiKey,
                        onValueChange = {
                            inputApiKey = it
                            isKeySaved = false
                        },
                        placeholder = { Text("Nhập hoặc sửa Gemini API Key...", fontSize = 12.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .height(50.dp),
                        singleLine = true,
                        shape = RoundedMd
                    )

                    Gamified3DButton(
                        text = if (isKeySaved) "Đã lưu" else "Lưu Key",
                        onClick = {
                            com.example.data.service.AiService.API_KEY = inputApiKey.trim()
                            isKeySaved = true
                        },
                        backgroundColor = GamifyGreen,
                        shadowColor = GamifyGreenShadow,
                        height = 46.dp,
                        modifier = Modifier.width(96.dp),
                        testTag = "btn_save_api_key"
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(20.dp))
    }
}

/**
 * 3D Circular Duolingo Pathway Node Button
 */
@Composable
private fun PathwayNode(
    title: String,
    subtitle: String,
    icon: ImageVector,
    color: Color,
    shadowColor: Color,
    offsetX: Dp,
    isCompleted: Boolean,
    testTag: String,
    onClick: () -> Unit
) {
    val interactionSource = remember { MutableInteractionSource() }
    val isPressed by interactionSource.collectIsPressedAsState()

    val pressOffset by animateDpAsState(
        targetValue = if (isPressed) 4.dp else 0.dp,
        animationSpec = tween(durationMillis = 60),
        label = "node_press"
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier
            .offset(x = offsetX)
            .clickable(
                interactionSource = interactionSource,
                indication = null,
                onClick = onClick
            )
            .testTag(testTag)
    ) {
        // 3D Circular Node
        Box(
            modifier = Modifier.size(76.dp),
            contentAlignment = Alignment.Center
        ) {
            // Shadow circle base
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .offset(y = 6.dp)
                    .clip(CircleShape)
                    .background(shadowColor)
            )

            // Top interactive circle
            Box(
                modifier = Modifier
                    .size(70.dp)
                    .offset(y = pressOffset)
                    .clip(CircleShape)
                    .background(color),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = icon,
                    contentDescription = title,
                    tint = Color.White,
                    modifier = Modifier.size(34.dp)
                )

                // Completed star badge on top right
                if (isCompleted) {
                    Box(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .offset(x = 2.dp, y = (-2).dp)
                            .size(22.dp)
                            .clip(CircleShape)
                            .background(GamifyYellow)
                            .border(1.5.dp, Color.White, CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Check,
                            contentDescription = "Completed",
                            tint = Color.White,
                            modifier = Modifier.size(14.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Title Label with 3D Pill background
        Box(
            modifier = Modifier
                .clip(RoundedFull)
                .background(NeutralWhite)
                .border(2.dp, NeutralBorder, RoundedFull)
                .padding(horizontal = 14.dp, vertical = 6.dp)
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeutralText,
                        fontSize = 13.sp
                    )
                )
                Text(
                    text = subtitle,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = NeutralSubtext,
                        fontSize = 10.sp
                    )
                )
            }
        }
    }
}

/**
 * Visual connector dots on the winding pathway
 */
@Composable
private fun PathwayConnector() {
    Column(
        verticalArrangement = Arrangement.spacedBy(4.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.padding(vertical = 4.dp)
    ) {
        repeat(3) {
            Box(
                modifier = Modifier
                    .size(8.dp)
                    .clip(CircleShape)
                    .background(NeutralBorder)
            )
        }
    }
}

/**
 * Tab lọc trên cùng: Ngữ pháp / Từ vựng / Kanji
 */
@Composable
private fun StudyCategoryFilterTab(
    title: String,
    icon: ImageVector,
    isSelected: Boolean,
    activeColor: Color,
    activeColorShadow: Color,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: () -> Unit
) {
    Gamified3DCard(
        modifier = modifier.testTag(testTag),
        backgroundColor = if (isSelected) activeColor.copy(alpha = 0.12f) else NeutralWhite,
        borderColor = if (isSelected) activeColor else NeutralBorder,
        borderWidth = if (isSelected) 2.dp else 1.5.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 10.dp, horizontal = 4.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = if (isSelected) activeColor else NeutralSubtext,
                modifier = Modifier.size(20.dp)
            )
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                    color = if (isSelected) activeColor else NeutralText,
                    fontSize = 11.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

@Composable
private fun VocabQuickModePill(
    emoji: String,
    label: String,
    color: Color,
    modifier: Modifier = Modifier,
    testTag: String = "",
    onClick: () -> Unit
) {
    Box(
        modifier = modifier
            .clip(RoundedMd)
            .background(NeutralWhite)
            .border(1.5.dp, color, RoundedMd)
            .clickable(onClick = onClick)
            .padding(vertical = 8.dp, horizontal = 6.dp)
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(text = emoji, fontSize = 13.sp)
            Text(
                text = label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 11.5.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
        }
    }
}

/**
 * Card Bài Học 3D lớn hiển thị trong danh sách Grid bài học
 */
@Composable
private fun StudySetCard(
    category: StudyCategory,
    lessonId: Int,
    title: String,
    itemCount: Int,
    itemCountLabel: String,
    cardIds: List<String>,
    srsStats: SrsStatsSummary,
    onClick: () -> Unit
) {
    val srsType = when (category) {
        StudyCategory.GRAMMAR -> SrsItemType.GRAMMAR
        StudyCategory.VOCABULARY -> SrsItemType.VOCABULARY
        StudyCategory.KANJI -> SrsItemType.KANJI
    }

    val (masteredCount, masteredPct) = remember(lessonId, category, cardIds, srsStats) {
        SrsRepository.getLessonMastery(srsType, cardIds, lessonId)
    }

    val isMastered = masteredPct >= 100f
    val isLearning = masteredPct > 0f

    val themeColor = when (category) {
        StudyCategory.GRAMMAR -> GamifyGreen
        StudyCategory.VOCABULARY -> GamifyBlue
        StudyCategory.KANJI -> GamifyRed
    }

    Gamified3DCard(
        modifier = Modifier
            .fillMaxWidth()
            .testTag("set_card_${category.name.lowercase()}_$lessonId"),
        backgroundColor = NeutralWhite,
        borderColor = if (isMastered) GamifyGreen.copy(alpha = 0.8f) else if (isLearning) themeColor.copy(alpha = 0.7f) else NeutralBorder,
        borderWidth = if (isMastered || isLearning) 2.dp else 1.5.dp,
        onClick = onClick
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
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
                            .size(32.dp)
                            .clip(RoundedMd)
                            .background(themeColor.copy(alpha = 0.15f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "$lessonId",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = themeColor,
                                fontSize = 13.sp
                            )
                        )
                    }

                    Text(
                        text = "BÀI $lessonId",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeutralSubtext,
                            letterSpacing = 0.5.sp
                        )
                    )
                }

                // Badge trạng thái: Hoàn thành / Đang học / Chưa học
                Box(
                    modifier = Modifier
                        .clip(RoundedFull)
                        .background(
                            when {
                                isMastered -> GamifyGreenLight
                                isLearning -> GamifyBlueLight
                                else -> Color(0xFFF3F4F6)
                            }
                        )
                        .border(
                            1.dp,
                            when {
                                isMastered -> GamifyGreen
                                isLearning -> GamifyBlue
                                else -> NeutralBorder
                            },
                            RoundedFull
                        )
                        .padding(horizontal = 8.dp, vertical = 3.dp)
                ) {
                    Text(
                        text = when {
                            isMastered -> "✓ Hoàn thành"
                            isLearning -> "🔥 Đang học"
                            else -> "Chưa học"
                        },
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = when {
                                isMastered -> GamifyGreenDark
                                isLearning -> GamifyBlueDark
                                else -> NeutralSubtext
                            },
                            fontSize = 10.sp
                        )
                    )
                }
            }

            Column(verticalArrangement = Arrangement.spacedBy(2.dp)) {
                Text(
                    text = title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeutralText,
                        fontSize = 15.5.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Text(
                    text = "$itemCount $itemCountLabel",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeutralSubtext,
                        fontSize = 11.5.sp
                    )
                )
            }

            Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Đã thuộc $masteredCount / $itemCount thẻ",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeutralSubtext,
                            fontSize = 11.sp
                        )
                    )
                    Text(
                        text = "${masteredPct.toInt()}% Mastered",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (isMastered) GamifyGreenDark else themeColor,
                            fontSize = 11.sp
                        )
                    )
                }

                GamifiedProgressBar(
                    progress = (masteredPct / 100f).coerceIn(0.04f, 1f),
                    fillColor = if (isMastered) GamifyGreen else themeColor,
                    height = 8.dp
                )
            }
        }
    }
}
