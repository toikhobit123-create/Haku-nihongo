package com.example.ui.state

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.BattleCard
import com.example.data.model.CardBattlePvEQuestion
import com.example.data.model.CardType
import com.example.data.model.MatchPairItem
import com.example.data.model.QuizExercise
import com.example.data.model.QuizExerciseType
import com.example.data.model.SrsItemType
import com.example.data.model.SrsRating
import com.example.data.model.SrsStage
import com.example.data.model.StudyCategory
import com.example.data.model.VocabularyItem
import com.example.data.repository.GrammarExamplesData
import com.example.data.repository.MockDataRepository
import com.example.data.repository.QuizExerciseRepository
import com.example.data.repository.SrsRepository
import com.example.data.service.AiService
import com.example.data.srs.Sm2Algorithm
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NihongoViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(NihongoUiState())
    val uiState: StateFlow<NihongoUiState> = _uiState.asStateFlow()

    private var testTimerJob: Job? = null

    init {
        loadSrsSession()
        refreshSrsStats()
    }

    fun navigateTo(screen: Screen) {
        if (screen != Screen.INDUCTIVE_LEARNING) {
            testTimerJob?.cancel()
        }
        if (screen == Screen.SRS_REVIEW && _uiState.value.srsDueCards.isEmpty()) {
            loadSrsSession()
        }
        if (screen == Screen.PROFILE_STATS || screen == Screen.HOME || screen == Screen.LESSON_DETAIL) {
            refreshSrsStats()
        }
        _uiState.update { it.copy(currentScreen = screen) }
    }

    fun goBack() {
        testTimerJob?.cancel()
        refreshSrsStats()
        val prev = _uiState.value.previousScreen
        val curr = _uiState.value.currentScreen
        if (curr == Screen.LESSON_DETAIL) {
            _uiState.update { it.copy(currentScreen = Screen.HOME, previousScreen = Screen.HOME) }
        } else if (prev == Screen.LESSON_DETAIL) {
            _uiState.update { it.copy(currentScreen = Screen.LESSON_DETAIL, previousScreen = Screen.HOME) }
        } else if (prev == Screen.STUDY_HUB) {
            _uiState.update { it.copy(currentScreen = Screen.STUDY_HUB, previousScreen = Screen.HOME) }
        } else {
            _uiState.update { it.copy(currentScreen = Screen.HOME, previousScreen = Screen.HOME) }
        }
    }

    /**
     * Mở màn hình Chi tiết Bài học (Lesson Detail Screen)
     */
    fun openLessonDetail(category: StudyCategory, lessonId: Int, lessonTitle: String) {
        refreshSrsStats()
        _uiState.update {
            it.copy(
                selectedStudyCategory = category,
                selectedLessonId = lessonId,
                selectedLessonTitle = lessonTitle,
                previousScreen = Screen.HOME,
                currentScreen = Screen.LESSON_DETAIL
            )
        }
    }

    /**
     * Đánh dấu / Bỏ đánh dấu Đã thuộc trực tiếp cho 1 thẻ
     */
    fun toggleCardMastered(cardId: String, type: SrsItemType) {
        SrsRepository.toggleCardMastered(cardId, type)
        refreshSrsStats()
    }

    /**
     * 1. Chế độ Thẻ ghi nhớ (Flashcard / SRS) riêng cho bài học đã chọn
     */
    fun startSrsForLesson(
        category: StudyCategory,
        lessonId: Int,
        returnScreen: Screen = Screen.LESSON_DETAIL
    ) {
        val srsType = when (category) {
            StudyCategory.GRAMMAR -> SrsItemType.GRAMMAR
            StudyCategory.VOCABULARY -> SrsItemType.VOCABULARY
            StudyCategory.KANJI -> SrsItemType.KANJI
        }
        val lessonCards = SrsRepository.getCardsForLesson(srsType, lessonId)
        val cardsToStudy = if (lessonCards.isNotEmpty()) lessonCards else SrsRepository.getAllCards().filter { it.type == srsType }.take(15)

        val pairs = cardsToStudy.map { card ->
            Pair(card, SrsRepository.getProgress(card.id, card.type))
        }

        _uiState.update {
            it.copy(
                srsDueCards = pairs,
                currentSrsIndex = 0,
                isSrsCardFlipped = false,
                selectedSrsFilter = srsType,
                isWeakPointsSession = false,
                sessionTotalCount = pairs.size.coerceAtLeast(1),
                sessionCompletedCount = 0,
                sessionCorrectCount = 0,
                isSessionFinished = false,
                previousScreen = returnScreen,
                currentScreen = Screen.SRS_REVIEW
            )
        }
    }

    /**
     * Khởi động Flashcard ôn tập tổng hợp theo loại (Từ vựng / Ngữ pháp / Kanji)
     */
    fun startSrsForCategory(category: StudyCategory, returnScreen: Screen = Screen.HOME) {
        val srsType = when (category) {
            StudyCategory.GRAMMAR -> SrsItemType.GRAMMAR
            StudyCategory.VOCABULARY -> SrsItemType.VOCABULARY
            StudyCategory.KANJI -> SrsItemType.KANJI
        }
        val pairs = SrsRepository.getDueOrNewCards(filterType = srsType, limit = 15)
        _uiState.update {
            it.copy(
                srsDueCards = pairs,
                currentSrsIndex = 0,
                isSrsCardFlipped = false,
                selectedSrsFilter = srsType,
                isWeakPointsSession = false,
                sessionTotalCount = pairs.size.coerceAtLeast(1),
                sessionCompletedCount = 0,
                sessionCorrectCount = 0,
                isSessionFinished = false,
                previousScreen = returnScreen,
                currentScreen = Screen.SRS_REVIEW
            )
        }
    }

    /**
     * 2. Chế độ Học quy nạp (Learn / Practice) với bài tập riêng của bài học
     */
    fun startPracticeForLesson(
        category: StudyCategory,
        lessonId: Int,
        returnScreen: Screen = Screen.LESSON_DETAIL
    ) {
        testTimerJob?.cancel()
        val exercises = buildExercisesForLesson(category, lessonId)
        val firstEx = QuizExerciseRepository.sanitizeExercise(
            exercises.firstOrNull() ?: QuizExerciseRepository.getExerciseByIndex(0)
        )
        _uiState.update {
            it.copy(
                exerciseList = exercises,
                activeExercise = firstEx,
                activeQuestion = QuizExerciseRepository.toGrammarQuestion(firstEx),
                currentExerciseIndex = 0,
                selectedOptionIndex = null,
                sentenceBuilderSelectedChips = emptyList(),
                sentenceBuilderAvailableChips = QuizExerciseRepository.scrambleTokensNonMatching(
                    firstEx.builderTokens,
                    firstEx.correctTokenOrder
                ),
                isAnswerEvaluated = false,
                isAnswerCorrect = false,
                isShowingSimilarQuestion = false,
                aiMistakeExplanation = null,
                isAiMistakeLoading = false,
                isTestMode = false,
                isLessonExerciseSession = true,
                sessionTotalCount = exercises.size.coerceAtLeast(1),
                sessionCompletedCount = 0,
                sessionCorrectCount = 0,
                isSessionFinished = false,
                previousScreen = returnScreen,
                currentScreen = Screen.INDUCTIVE_LEARNING
            )
        }
    }

    /**
     * 3. Chế độ Kiểm tra (Test / Quiz): Bài test 10 câu đếm ngược thời gian
     */
    fun startTestForLesson(
        category: StudyCategory,
        lessonId: Int,
        returnScreen: Screen = Screen.LESSON_DETAIL
    ) {
        testTimerJob?.cancel()
        val exercises = buildExercisesForLesson(category, lessonId).take(10)
        val firstEx = QuizExerciseRepository.sanitizeExercise(
            exercises.firstOrNull() ?: QuizExerciseRepository.getExerciseByIndex(0)
        )
        _uiState.update {
            it.copy(
                exerciseList = exercises,
                activeExercise = firstEx,
                activeQuestion = QuizExerciseRepository.toGrammarQuestion(firstEx),
                currentExerciseIndex = 0,
                selectedOptionIndex = null,
                sentenceBuilderSelectedChips = emptyList(),
                sentenceBuilderAvailableChips = QuizExerciseRepository.scrambleTokensNonMatching(
                    firstEx.builderTokens,
                    firstEx.correctTokenOrder
                ),
                isAnswerEvaluated = false,
                isAnswerCorrect = false,
                isShowingSimilarQuestion = false,
                aiMistakeExplanation = null,
                isAiMistakeLoading = false,
                isTestMode = true,
                isLessonExerciseSession = true,
                testTimeRemainingSeconds = 180,
                sessionTotalCount = exercises.size.coerceAtLeast(1),
                sessionCompletedCount = 0,
                sessionCorrectCount = 0,
                isSessionFinished = false,
                previousScreen = returnScreen,
                currentScreen = Screen.INDUCTIVE_LEARNING
            )
        }
        startTestCountdownTimer()
    }

    private fun startTestCountdownTimer() {
        testTimerJob?.cancel()
        testTimerJob = viewModelScope.launch {
            while (_uiState.value.isTestMode &&
                _uiState.value.currentScreen == Screen.INDUCTIVE_LEARNING &&
                !_uiState.value.isSessionFinished &&
                _uiState.value.testTimeRemainingSeconds > 0
            ) {
                delay(1000L)
                _uiState.update { state ->
                    val nextSec = (state.testTimeRemainingSeconds - 1).coerceAtLeast(0)
                    if (nextSec == 0) {
                        SrsRepository.completeSession()
                        state.copy(
                            testTimeRemainingSeconds = 0,
                            isSessionFinished = true
                        )
                    } else {
                        state.copy(testTimeRemainingSeconds = nextSec)
                    }
                }
            }
        }
    }

    /**
     * 4. Chế độ Ghép thẻ Nhật <-> Việt (Match Game) với ngữ pháp / từ vựng / Kanji của bài học
     */
    fun startCardBattleForLesson(
        category: StudyCategory,
        lessonId: Int,
        returnScreen: Screen = Screen.LESSON_DETAIL
    ) {
        val allPairs = buildMatchPairsForLesson(category, lessonId)
        val roundSize = if (allPairs.size == 5) 5 else 4
        val rounds = allPairs.chunked(roundSize)
        val firstRound = rounds.firstOrNull().orEmpty()
        val totalBossHp = (allPairs.size * 30).coerceAtLeast(120)

        _uiState.update {
            it.copy(
                matchCategory = category,
                matchLessonId = lessonId,
                matchRounds = rounds,
                currentMatchRoundIndex = 0,
                leftJapaneseCards = firstRound.shuffled(),
                rightVietnameseCards = firstRound.shuffled(),
                selectedMatchJapaneseId = null,
                selectedMatchVietnameseId = null,
                matchedPairIds = emptySet(),
                matchErrorPairIds = null,
                matchCombo = 0,
                userHp = 100,
                maxUserHp = 100,
                bossHp = totalBossHp,
                maxBossHp = totalBossHp,
                turnCount = 1,
                isVictory = false,
                isDefeat = false,
                isScreenFlashing = false,
                isUserHit = false,
                pveCombatFeedback = "Chọn 1 thẻ Tiếng Nhật (trái) rồi nối với nghĩa Tiếng Việt đúng (phải)!",
                battleCombatLog = listOf("Trận Ghép Thẻ bắt đầu! Hãy nối đúng ${allPairs.size} cặp thẻ để chiến thắng."),
                previousScreen = returnScreen,
                currentScreen = Screen.CARD_BATTLE
            )
        }
    }

    fun buildExercisesForLesson(category: StudyCategory, lessonId: Int): List<QuizExercise> {
        val allExercises = QuizExerciseRepository.getAllExercises()
        when (category) {
            StudyCategory.GRAMMAR -> {
                val allGrammar = _uiState.value.grammarPoints
                val lessonGrammar = allGrammar.filter { it.lessonId == lessonId }
                if (lessonGrammar.isNotEmpty()) {
                    return lessonGrammar.mapIndexed { idx, gp ->
                        val ex = gp.examples.firstOrNull()
                            ?: GrammarExamplesData.getExamples(gp.title, gp.title, gp.meaning).first()
                        val rawJp = ex.jp
                        val rawVn = ex.vn
                        val missingTarget = GrammarExamplesData.findTemplate(gp.title)?.targetSegment
                            ?: gp.title.replace("〜", "").replace("。", "").trim()
                        val blankedJp = QuizExerciseRepository.blankOutGrammar(rawJp, gp.title, gp.title)

                        // Chọn 3 đáp án nhiễu khác hoàn toàn với gp.title và không trùng targetSegment
                        val grammarDistractors = allGrammar
                            .filter { other ->
                                other.id != gp.id &&
                                    other.title != gp.title &&
                                    (missingTarget.length < 2 ||
                                        GrammarExamplesData.findTemplate(other.title)?.targetSegment != missingTarget)
                            }
                            .shuffled()
                            .map { it.title }
                            .distinct()
                            .take(3)
                        val opt = (listOf(gp.title) + grammarDistractors).distinct().shuffled()
                        val correctIdx = opt.indexOf(gp.title).coerceAtLeast(0)

                        val tokens = QuizExerciseRepository.segmentJapaneseSentence(rawJp)
                        val canBuildSentence = tokens.size >= 2 && tokens.toSet().size >= 2

                        val vnDistractors = allGrammar
                            .filter { it.id != gp.id }
                            .shuffled()
                            .map { it.examples.firstOrNull()?.vn ?: it.meaning }
                            .filter { it != rawVn && it.isNotBlank() }
                            .distinct()
                            .take(3)
                        val vnTransOpts = (listOf(rawVn) + vnDistractors).distinct().shuffled()
                        val correctVnTransIdx = vnTransOpts.indexOf(rawVn).coerceAtLeast(0)

                        val jpDistractors = allGrammar
                            .filter { it.id != gp.id }
                            .shuffled()
                            .mapNotNull { it.examples.firstOrNull()?.jp }
                            .filter { it != rawJp && it.isNotBlank() }
                            .distinct()
                            .take(3)
                        val jpTransOpts = (listOf(rawJp) + jpDistractors).distinct().shuffled()
                        val correctJpTransIdx = jpTransOpts.indexOf(rawJp).coerceAtLeast(0)

                        val exType = when (idx % 4) {
                            0 -> QuizExerciseType.MULTIPLE_CHOICE
                            1 -> QuizExerciseType.FILL_IN_BLANK
                            2 -> if (canBuildSentence) QuizExerciseType.SENTENCE_BUILDER else QuizExerciseType.MULTIPLE_CHOICE
                            else -> QuizExerciseType.TRANSLATION
                        }
                        val jpToVn = (idx / 4) % 2 == 0
                        val scrambledTokens = if (canBuildSentence) {
                            QuizExerciseRepository.scrambleTokensNonMatching(tokens, tokens)
                        } else {
                            emptyList()
                        }

                        val activeTransQuestion = if (jpToVn) rawJp else rawVn
                        val activeTransOpts = if (jpToVn) vnTransOpts else jpTransOpts
                        val activeTransIdx = if (jpToVn) correctVnTransIdx else correctJpTransIdx

                        QuizExerciseRepository.sanitizeExercise(
                            QuizExercise(
                                id = "gen_ex_${gp.id}",
                                type = exType,
                                grammarPoint = gp.title,
                                instruction = when (exType) {
                                    QuizExerciseType.MULTIPLE_CHOICE -> "Chọn cấu trúc ngữ pháp thích hợp điền vào chỗ trống:"
                                    QuizExerciseType.FILL_IN_BLANK -> "Điền mẫu ngữ pháp còn thiếu vào ô khuyết:"
                                    QuizExerciseType.SENTENCE_BUILDER -> "Sắp xếp các mảnh ghép theo đúng trật tự câu tiếng Nhật:"
                                    QuizExerciseType.TRANSLATION -> if (jpToVn) {
                                        "Tự nhớ lại nghĩa tiếng Việt của câu tiếng Nhật sau, rồi chọn bản dịch đúng nhất:"
                                    } else {
                                        "Tự nhớ lại câu tiếng Nhật tương ứng với nghĩa tiếng Việt sau:"
                                    }
                                },
                                promptJp = when (exType) {
                                    QuizExerciseType.SENTENCE_BUILDER -> ""
                                    QuizExerciseType.TRANSLATION -> if (jpToVn) rawJp else ""
                                    else -> blankedJp
                                },
                                promptVn = when (exType) {
                                    QuizExerciseType.TRANSLATION -> if (jpToVn) "" else rawVn
                                    else -> rawVn
                                },
                                targetCardId = gp.id,
                                targetItemType = SrsItemType.GRAMMAR,
                                options = if (exType == QuizExerciseType.TRANSLATION) activeTransOpts else opt,
                                correctAnswerIndex = if (exType == QuizExerciseType.TRANSLATION) activeTransIdx else correctIdx,
                                sentenceWithBlank = if (exType == QuizExerciseType.FILL_IN_BLANK || exType == QuizExerciseType.MULTIPLE_CHOICE) blankedJp else "",
                                blankMissingWord = if (exType == QuizExerciseType.FILL_IN_BLANK || exType == QuizExerciseType.MULTIPLE_CHOICE) missingTarget else "",
                                builderTokens = scrambledTokens,
                                correctTokenOrder = if (canBuildSentence) tokens else emptyList(),
                                completeSentenceJp = rawJp,
                                completeSentenceVn = rawVn,
                                isJpToVn = jpToVn,
                                translationQuestion = activeTransQuestion,
                                translationOptions = activeTransOpts,
                                correctTranslationIndex = activeTransIdx,
                                explanation = "【${gp.title}】${gp.meaning}. Cấu trúc: ${gp.structure}",
                                mistakeExplanation = "Hãy chú ý cấu trúc ${gp.structure} và ý nghĩa: ${gp.meaning}",
                                ruleSummary = "${gp.title}: ${gp.meaning}"
                            )
                        )
                    }
                }
            }
            StudyCategory.VOCABULARY -> {
                val allVocabs = _uiState.value.vocabularyList
                val lessonVocabs = allVocabs.filter { it.lessonId == lessonId }.ifEmpty {
                    allVocabs.take(10)
                }
                return lessonVocabs.mapIndexed { idx, v ->
                    val otherVocabs = allVocabs
                        .filter { it.id != v.id && it.kanji != v.kanji && it.meaning != v.meaning }
                        .distinctBy { it.kanji }
                        .shuffled()
                        .take(3)
                    val meaningOpts = (listOf(v.meaning) + otherVocabs.map { it.meaning }).distinct().shuffled()
                    val correctMeaningIdx = meaningOpts.indexOf(v.meaning).coerceAtLeast(0)
                    val kanjiOpts = (listOf(v.kanji) + otherVocabs.map { it.kanji }).distinct().shuffled()
                    val correctKanjiIdx = kanjiOpts.indexOf(v.kanji).coerceAtLeast(0)

                    val exSentence = v.exampleSentence
                    val hasCuratedSentence = exSentence.isNotBlank()
                    val matchedBlankTarget = when {
                        hasCuratedSentence && exSentence.contains(v.kanji) -> v.kanji
                        hasCuratedSentence && v.hiragana.isNotBlank() && exSentence.contains(v.hiragana) -> v.hiragana
                        else -> null
                    }
                    val tokens = if (hasCuratedSentence) QuizExerciseRepository.segmentJapaneseSentence(exSentence) else emptyList()
                    val canBuildSentence = tokens.size >= 2 && tokens.toSet().size >= 2

                    val type = if (canBuildSentence) {
                        when (idx % 4) {
                            0 -> QuizExerciseType.MULTIPLE_CHOICE
                            1 -> QuizExerciseType.FILL_IN_BLANK
                            2 -> QuizExerciseType.SENTENCE_BUILDER
                            else -> QuizExerciseType.TRANSLATION
                        }
                    } else {
                        when (idx % 3) {
                            0 -> QuizExerciseType.MULTIPLE_CHOICE
                            1 -> QuizExerciseType.FILL_IN_BLANK
                            else -> QuizExerciseType.TRANSLATION
                        }
                    }

                    val sentenceBlank = if (matchedBlankTarget != null) {
                        var masked = exSentence.replace(matchedBlankTarget, "【 ＿＿＿ 】")
                        if (v.kanji.length >= 2) masked = masked.replace(v.kanji, "【 ＿＿＿ 】")
                        if (v.hiragana.length >= 2 && v.hiragana != v.kanji) {
                            masked = masked.replace(v.hiragana, "【 ＿＿＿ 】")
                        }
                        masked
                    } else {
                        "Nghĩa:「${v.meaning}」→ Cách viết tiếng Nhật đúng là 【 ＿＿＿ 】?"
                    }

                    val jpToVn = (idx / 3) % 2 == 0
                    val sentenceMeaningOpts = if (hasCuratedSentence && v.exampleMeaning.isNotBlank()) {
                        val vnOthers = otherVocabs
                            .map { it.exampleMeaning.ifBlank { it.meaning } }
                            .filter { it != v.exampleMeaning }
                        (listOf(v.exampleMeaning) + vnOthers).distinct().shuffled()
                    } else {
                        meaningOpts
                    }
                    val correctSentenceMeaningIdx = sentenceMeaningOpts
                        .indexOf(v.exampleMeaning.ifBlank { v.meaning })
                        .coerceAtLeast(0)

                    val transPrompt: String
                    val transOpts: List<String>
                    val transCorrectIdx: Int
                    val transCompleteJp: String
                    val transCompleteVn: String

                    if (jpToVn) {
                        if (hasCuratedSentence && v.exampleMeaning.isNotBlank()) {
                            transPrompt = exSentence
                            transOpts = sentenceMeaningOpts
                            transCorrectIdx = correctSentenceMeaningIdx
                            transCompleteJp = exSentence
                            transCompleteVn = v.exampleMeaning
                        } else {
                            transPrompt = "${v.kanji}（${v.hiragana}）"
                            transOpts = meaningOpts
                            transCorrectIdx = correctMeaningIdx
                            transCompleteJp = "${v.kanji}（${v.hiragana}）"
                            transCompleteVn = v.meaning
                        }
                    } else {
                        transPrompt = v.meaning
                        transOpts = kanjiOpts
                        transCorrectIdx = correctKanjiIdx
                        transCompleteJp = v.kanji
                        transCompleteVn = v.meaning
                    }

                    val (promptText, currentOpts, currentCorrectIdx) = when (type) {
                        QuizExerciseType.FILL_IN_BLANK -> Triple(sentenceBlank, kanjiOpts, correctKanjiIdx)
                        QuizExerciseType.TRANSLATION -> Triple(transPrompt, transOpts, transCorrectIdx)
                        QuizExerciseType.SENTENCE_BUILDER -> Triple("", emptyList(), 0)
                        QuizExerciseType.MULTIPLE_CHOICE -> Triple("${v.kanji}（${v.hiragana}）", meaningOpts, correctMeaningIdx)
                    }

                    val scrambledTokens = if (canBuildSentence) {
                        QuizExerciseRepository.scrambleTokensNonMatching(tokens, tokens)
                    } else {
                        emptyList()
                    }
                    val canonicalCardId = SrsRepository.resolveCanonicalId(v.id)

                    QuizExerciseRepository.sanitizeExercise(
                        QuizExercise(
                            id = "vocab_ex_${v.id}",
                            type = type,
                            grammarPoint = "${v.kanji} (${v.hiragana})",
                            instruction = when (type) {
                                QuizExerciseType.MULTIPLE_CHOICE -> "Chọn nghĩa tiếng Việt chính xác của từ vựng sau:"
                                QuizExerciseType.FILL_IN_BLANK -> if (matchedBlankTarget != null) {
                                    "Chọn từ vựng thích hợp để điền vào chỗ trống:"
                                } else {
                                    "Chọn từ vựng tiếng Nhật đúng với nghĩa sau:"
                                }
                                QuizExerciseType.SENTENCE_BUILDER -> "Sắp xếp các thẻ từ 3D theo thứ tự câu tiếng Nhật:"
                                QuizExerciseType.TRANSLATION -> if (jpToVn) {
                                    "Tự nhớ lại nghĩa tiếng Việt, rồi chọn bản dịch chính xác:"
                                } else {
                                    "Tự nhớ lại từ tiếng Nhật tương ứng với nghĩa tiếng Việt sau:"
                                }
                            },
                            promptJp = when (type) {
                                QuizExerciseType.SENTENCE_BUILDER -> ""
                                QuizExerciseType.TRANSLATION -> if (jpToVn) transPrompt else ""
                                else -> promptText
                            },
                            promptVn = when (type) {
                                QuizExerciseType.TRANSLATION -> if (jpToVn) "" else transPrompt
                                else -> v.exampleMeaning.ifEmpty { v.meaning }
                            },
                            targetCardId = canonicalCardId,
                            targetItemType = SrsItemType.VOCABULARY,
                            options = currentOpts,
                            correctAnswerIndex = currentCorrectIdx,
                            sentenceWithBlank = if (type == QuizExerciseType.FILL_IN_BLANK) sentenceBlank else "",
                            blankMissingWord = if (type == QuizExerciseType.FILL_IN_BLANK) (matchedBlankTarget ?: v.kanji) else "",
                            builderTokens = scrambledTokens,
                            correctTokenOrder = if (canBuildSentence) tokens else emptyList(),
                            completeSentenceJp = if (type == QuizExerciseType.TRANSLATION) transCompleteJp else exSentence.ifEmpty { "${v.kanji}（${v.hiragana}）" },
                            completeSentenceVn = if (type == QuizExerciseType.TRANSLATION) transCompleteVn else v.exampleMeaning.ifEmpty { v.meaning },
                            isJpToVn = if (type == QuizExerciseType.TRANSLATION) jpToVn else true,
                            translationQuestion = transPrompt,
                            translationOptions = transOpts,
                            correctTranslationIndex = transCorrectIdx,
                            explanation = "【${v.kanji} - ${v.hiragana} (${v.romaji})】Nghĩa: ${v.meaning}.",
                            mistakeExplanation = "Từ [${v.kanji}] (${v.hiragana}) có nghĩa là: ${v.meaning}",
                            ruleSummary = "${v.kanji}: ${v.meaning}"
                        )
                    )
                }
            }
            StudyCategory.KANJI -> {
                val allKanji = _uiState.value.kanjiList.ifEmpty { MockDataRepository.n3KanjiList }
                val lessonKanji = _uiState.value.kanjiLessons.find { it.lessonId == lessonId }?.kanjiList ?: allKanji
                return lessonKanji.map { k ->
                    val distractors = allKanji.filter { it.id != k.id }.shuffled().take(3).map { it.meaning }
                    val opts = (listOf(k.meaning) + distractors).distinct().shuffled()
                    QuizExercise(
                        id = "kanji_ex_${k.id}",
                        type = QuizExerciseType.MULTIPLE_CHOICE,
                        grammarPoint = "Hán tự: ${k.kanji}",
                        instruction = "Chọn ý nghĩa chính xác của chữ Hán 【 ${k.kanji} 】:",
                        promptJp = "Chữ Hán: 【 ${k.kanji} 】\nÂm On: ${k.onyomi} • Âm Kun: ${k.kunyomi}",
                        promptVn = "",
                        targetCardId = k.id,
                        targetItemType = SrsItemType.KANJI,
                        options = opts,
                        correctAnswerIndex = opts.indexOf(k.meaning).coerceAtLeast(0),
                        explanation = "Hán tự 【${k.kanji}】 (Hán Việt: ${k.hanViet}). Nghĩa: ${k.meaning}. Ví dụ: ${k.exampleCompound} (${k.exampleCompoundMeaning})",
                        mistakeExplanation = "Chữ 【${k.kanji}】 (${k.hanViet}) mang nghĩa là: ${k.meaning}",
                        ruleSummary = "${k.kanji} [${k.hanViet}]: ${k.meaning}"
                    )
                }
            }
        }
        return allExercises
    }

    private fun buildMatchPairsForLesson(category: StudyCategory, lessonId: Int): List<MatchPairItem> {
        return when (category) {
            StudyCategory.GRAMMAR -> {
                val items = _uiState.value.grammarPoints.filter { it.lessonId == lessonId }
                    .ifEmpty { _uiState.value.grammarPoints.take(5) }
                items.mapIndexed { idx, gp ->
                    MatchPairItem(
                        id = "match_gp_${gp.id}_$idx",
                        cardId = gp.id,
                        itemType = SrsItemType.GRAMMAR,
                        japanese = gp.title,
                        subJapanese = gp.structure,
                        vietnamese = gp.meaning
                    )
                }
            }
            StudyCategory.VOCABULARY -> {
                val lessonItems = _uiState.value.vocabularyList.filter { it.lessonId == lessonId }
                    .ifEmpty { _uiState.value.vocabularyList.take(12) }
                // Ưu tiên từ chưa thuộc và lấy tối đa 12 từ mỗi lượt ghép thẻ để trải nghiệm gọn gàng
                val prioritized = (lessonItems.filter { !SrsRepository.isCardMastered(it.id) } +
                        lessonItems.filter { SrsRepository.isCardMastered(it.id) })
                    .distinctBy { it.id }
                    .take(12)
                prioritized.mapIndexed { idx, v ->
                    MatchPairItem(
                        id = "match_vocab_${v.id}_$idx",
                        cardId = v.id,
                        itemType = SrsItemType.VOCABULARY,
                        japanese = v.kanji,
                        subJapanese = v.hiragana,
                        vietnamese = v.meaning
                    )
                }
            }
            StudyCategory.KANJI -> {
                val lessonItems = _uiState.value.kanjiLessons.find { it.lessonId == lessonId }?.kanjiList
                    ?: _uiState.value.kanjiList
                lessonItems.mapIndexed { idx, k ->
                    MatchPairItem(
                        id = "match_kanji_${k.id}_$idx",
                        cardId = k.id,
                        itemType = SrsItemType.KANJI,
                        japanese = k.kanji,
                        subJapanese = "On: ${k.onyomi}",
                        vietnamese = "${k.hanViet} — ${k.meaning}"
                    )
                }
            }
        }
    }

    // ==========================================
    // 3 MỤC HỌC: NGỮ PHÁP, TỪ VỰNG, KANJI
    // ==========================================

    fun selectStudyCategory(category: StudyCategory) {
        _uiState.update { it.copy(selectedStudyCategory = category) }
    }

    fun selectGrammarPoint(id: String) {
        _uiState.update { it.copy(selectedGrammarPointId = id) }
    }

    fun selectKanji(id: String) {
        _uiState.update { it.copy(selectedKanjiId = id) }
    }

    fun setGrammarSearchQuery(query: String) {
        _uiState.update { it.copy(grammarSearchQuery = query) }
    }

    fun selectGrammarLesson(lessonId: Int?) {
        _uiState.update { it.copy(selectedGrammarLessonId = lessonId) }
    }

    fun selectVocabLesson(lessonId: Int?) {
        _uiState.update { it.copy(selectedVocabLessonId = lessonId) }
    }

    fun setVocabSearchQuery(query: String) {
        _uiState.update { it.copy(vocabSearchQuery = query) }
    }

    /**
     * Chuyển trực tiếp sang làm câu hỏi trắc nghiệm tương ứng với điểm ngữ pháp
     */
    fun startQuizForGrammar(grammarId: String) {
        testTimerJob?.cancel()
        val current = _uiState.value
        val targetIdx = current.grammarQuestions.indexOfFirst {
            it.id == grammarId || (grammarId == "np_01" && it.id == "grammar_0_koto")
        }

        if (targetIdx >= 0) {
            val targetQ = current.grammarQuestions[targetIdx]
            val matchingEx = QuizExerciseRepository.sanitizeExercise(
                current.exerciseList.find { it.grammarPoint.contains(targetQ.grammarPoint) }
                    ?: QuizExerciseRepository.getExerciseByIndex(targetIdx)
            )

            _uiState.update {
                it.copy(
                    previousScreen = Screen.STUDY_HUB,
                    currentScreen = Screen.INDUCTIVE_LEARNING,
                    isTestMode = false,
                    isLessonExerciseSession = false,
                    isSessionFinished = false,
                    currentGrammarIndex = targetIdx,
                    currentExerciseIndex = targetIdx % it.exerciseList.size.coerceAtLeast(1),
                    activeQuestion = targetQ,
                    activeExercise = matchingEx,
                    selectedOptionIndex = null,
                    sentenceBuilderSelectedChips = emptyList(),
                    sentenceBuilderAvailableChips = QuizExerciseRepository.scrambleTokensNonMatching(
                        matchingEx.builderTokens,
                        matchingEx.correctTokenOrder
                    ),
                    isAnswerEvaluated = false,
                    isAnswerCorrect = false,
                    isShowingSimilarQuestion = false,
                    aiMistakeExplanation = null,
                    isAiMistakeLoading = false
                )
            }
            return
        }

        // Tạo bài tập riêng có đục lỗ đúng cho điểm ngữ pháp vừa chọn
        val gp = current.grammarPoints.firstOrNull { it.id == grammarId } ?: current.grammarPoints.first()
        val ex = gp.examples.firstOrNull()
            ?: GrammarExamplesData.getExamples(gp.title, gp.title, gp.meaning).first()
        val distractors = current.grammarPoints.filter { it.id != gp.id }.shuffled().take(3).map { it.title }
        val customEx = QuizExerciseRepository.createExerciseForGrammarPoint(
            grammarTitle = gp.title,
            structure = gp.structure,
            meaning = gp.meaning,
            exampleJp = ex.jp,
            exampleVn = ex.vn,
            cardId = gp.id,
            distractors = distractors
        )
        val customQ = QuizExerciseRepository.toGrammarQuestion(customEx)

        _uiState.update {
            it.copy(
                previousScreen = Screen.STUDY_HUB,
                currentScreen = Screen.INDUCTIVE_LEARNING,
                isTestMode = false,
                isLessonExerciseSession = false,
                isSessionFinished = false,
                activeQuestion = customQ,
                activeExercise = customEx,
                selectedOptionIndex = null,
                sentenceBuilderSelectedChips = emptyList(),
                sentenceBuilderAvailableChips = emptyList(),
                isAnswerEvaluated = false,
                isAnswerCorrect = false,
                isShowingSimilarQuestion = false,
                aiMistakeExplanation = null,
                isAiMistakeLoading = false
            )
        }
    }

    // ==========================================
    // TÍNH NĂNG 3: CARD BATTLE (PvE) LOGIC
    // ==========================================

    /**
     * Khi click vào một thẻ bài từ vựng 語彙:
     * - Kiểm tra xem có khớp vào chỗ khuyết không
     * - Khớp: Trừ HP Boss và nháy màn hình nhẹ (flash screen)
     * - Sai: Trừ HP User
     * - Nếu HP Boss = 0: Hiện thông báo chiến thắng
     */
    fun playPveCard(vocab: VocabularyItem) {
        val current = _uiState.value
        if (current.isVictory || current.isDefeat) return

        val isMatch = vocab.kanji == current.activePveQuestion.missingWord
        val logs = current.battleCombatLog.toMutableList()

        if (isMatch) {
            // Đúng: Trừ 40-50 HP Boss, nháy màn hình nhẹ
            val damage = 40
            val newBossHp = (current.bossHp - damage).coerceAtLeast(0)
            val newScore = current.score + 100
            val isVictorious = newBossHp <= 0

            logs.add(0, "⚔️ KHỚP! Bạn đặt thẻ「${vocab.kanji}」vào câu. Trừ $damage HP của Boss!")
            if (isVictorious) {
                logs.add(0, "🏆 CHIẾN THẮNG! Boss N3 đã bị tiêu diệt hoàn toàn!")
            }

            _uiState.update {
                it.copy(
                    bossHp = newBossHp,
                    score = newScore,
                    isScreenFlashing = true,
                    filledBlankWord = vocab.kanji,
                    turnCount = it.turnCount + 1,
                    isVictory = isVictorious,
                    battleCombatLog = logs.take(8),
                    pveCombatFeedback = "Chính xác! Thẻ「${vocab.kanji}」lắp vừa khít câu tiếng Nhật."
                )
            }

            // Tắt hiệu ứng flash sau 180ms để tạo hiệu ứng chớp sáng tinh tế
            viewModelScope.launch {
                delay(180L)
                _uiState.update { it.copy(isScreenFlashing = false) }

                // Nếu chưa thắng, sau 1.5s chuyển sang câu hỏi tiếp theo
                if (!isVictorious) {
                    delay(1200L)
                    nextPveQuestion()
                }
            }
        } else {
            // Sai: Trừ 25 HP của User
            val damage = 25
            val newUserHp = (current.userHp - damage).coerceAtLeast(0)
            val isDefeated = newUserHp <= 0

            logs.add(0, "💥 KHÔNG KHỚP! Thẻ「${vocab.kanji}」chưa đúng. Boss phản đòn trừ $damage HP của bạn!")
            if (isDefeated) {
                logs.add(0, "💀 BẠN ĐÃ THẤT BẠI! Hãy ôn lại từ vựng và tái đấu.")
            }

            _uiState.update {
                it.copy(
                    userHp = newUserHp,
                    isUserHit = true,
                    turnCount = it.turnCount + 1,
                    isDefeat = isDefeated,
                    battleCombatLog = logs.take(8),
                    pveCombatFeedback = "Sai rồi! Thẻ「${vocab.kanji}」không phù hợp với ngữ cảnh câu này."
                )
            }

            // Tắt rung/chớp đỏ sau 350ms
            viewModelScope.launch {
                delay(350L)
                _uiState.update { it.copy(isUserHit = false) }
            }
        }
    }

    /**
     * Chuyển sang câu hỏi PvE kế tiếp
     */
    fun nextPveQuestion() {
        val current = _uiState.value
        val questions = current.pveBattleQuestions
        val nextIdx = (current.currentPveQuestionIndex + 1) % questions.size
        _uiState.update {
            it.copy(
                currentPveQuestionIndex = nextIdx,
                activePveQuestion = questions[nextIdx],
                filledBlankWord = null,
                pveCombatFeedback = "Câu mới xuất hiện! Hãy chọn thẻ bài thích hợp để giải quyết chỗ khuyết."
            )
        }
    }

    /**
     * Reset trận đấu Ghép thẻ (hoặc PvE)
     */
    fun resetPveBattle() {
        val current = _uiState.value
        if (current.matchRounds.isNotEmpty()) {
            startCardBattleForLesson(current.matchCategory, current.matchLessonId, current.previousScreen)
            return
        }
        val firstQ = current.pveBattleQuestions[0]
        _uiState.update {
            it.copy(
                userHp = 100,
                bossHp = 150,
                turnCount = 1,
                currentPveQuestionIndex = 0,
                activePveQuestion = firstQ,
                filledBlankWord = null,
                isScreenFlashing = false,
                isUserHit = false,
                isVictory = false,
                isDefeat = false,
                pveCombatFeedback = "Trận đấu mới đã sẵn sàng! Boss N3 鬼 xuất hiện với 150 HP.",
                battleCombatLog = listOf("Trận chiến đã được thiết lập lại! Boss N3 鬼 xuất hiện với 150 HP.")
            )
        }
    }

    /**
     * Chọn 1 thẻ Tiếng Nhật trong chế độ Ghép thẻ Nhật <-> Việt
     */
    fun selectMatchJapaneseCard(pairId: String) {
        val current = _uiState.value
        if (current.isVictory || current.isDefeat || pairId in current.matchedPairIds) return

        val alreadySelectedVn = current.selectedMatchVietnameseId
        if (alreadySelectedVn != null) {
            evaluateMatchSelection(pairId, alreadySelectedVn)
        } else {
            _uiState.update {
                it.copy(
                    selectedMatchJapaneseId = if (it.selectedMatchJapaneseId == pairId) null else pairId,
                    matchErrorPairIds = null
                )
            }
        }
    }

    /**
     * Chọn 1 thẻ Nghĩa Tiếng Việt trong chế độ Ghép thẻ Nhật <-> Việt
     */
    fun selectMatchVietnameseCard(pairId: String) {
        val current = _uiState.value
        if (current.isVictory || current.isDefeat || pairId in current.matchedPairIds) return

        val alreadySelectedJp = current.selectedMatchJapaneseId
        if (alreadySelectedJp != null) {
            evaluateMatchSelection(alreadySelectedJp, pairId)
        } else {
            _uiState.update {
                it.copy(
                    selectedMatchVietnameseId = if (it.selectedMatchVietnameseId == pairId) null else pairId,
                    matchErrorPairIds = null
                )
            }
        }
    }

    private fun evaluateMatchSelection(jpId: String, vnId: String) {
        val current = _uiState.value
        val jpItem = current.leftJapaneseCards.firstOrNull { it.id == jpId } ?: return
        val vnItem = current.rightVietnameseCards.firstOrNull { it.id == vnId } ?: return

        if (jpId == vnId) {
            val newMatched = current.matchedPairIds + jpId
            val newCombo = current.matchCombo + 1
            val damage = 30
            val newBossHp = (current.bossHp - damage).coerceAtLeast(0)
            val newScore = current.score + 50 + (newCombo - 1) * 10
            val isRoundComplete = newMatched.size >= current.leftJapaneseCards.size
            val hasNextRound = current.currentMatchRoundIndex + 1 < current.matchRounds.size
            val isGameVictory = isRoundComplete && !hasNextRound

            // Lưu tiến trình Đã thuộc cho thẻ vừa ghép đúng
            SrsRepository.markCardMastered(jpItem.cardId, jpItem.itemType)
            SrsRepository.recordAnswerResult(true)
            if (isGameVictory) {
                SrsRepository.completeSession()
            }
            refreshSrsStats()

            val logs = (listOf("🔗 KHỚP!「${jpItem.japanese}」=「${jpItem.vietnamese}」(+50 PTS)") + current.battleCombatLog).take(8)
            _uiState.update {
                it.copy(
                    matchedPairIds = newMatched,
                    selectedMatchJapaneseId = null,
                    selectedMatchVietnameseId = null,
                    matchErrorPairIds = null,
                    matchCombo = newCombo,
                    bossHp = if (isGameVictory) 0 else newBossHp,
                    score = newScore,
                    turnCount = it.turnCount + 1,
                    isScreenFlashing = true,
                    isVictory = isGameVictory,
                    battleCombatLog = logs,
                    pveCombatFeedback = if (isGameVictory) {
                        "🎉 HOÀN THÀNH! Bạn đã nối đúng toàn bộ các cặp thẻ và lưu tiến trình!"
                    } else {
                        "Chính xác!「${jpItem.japanese}」→「${jpItem.vietnamese}」(Combo x$newCombo)"
                    }
                )
            }

            viewModelScope.launch {
                delay(180L)
                _uiState.update { it.copy(isScreenFlashing = false) }
                if (isRoundComplete && hasNextRound) {
                    delay(500L)
                    advanceMatchRound()
                }
            }
        } else {
            val newUserHp = (current.userHp - 20).coerceAtLeast(0)
            val isDefeated = newUserHp <= 0
            SrsRepository.recordAnswerResult(false)
            val logs = (listOf("💥 SAI CẶP!「${jpItem.japanese}」không khớp với「${vnItem.vietnamese}」") + current.battleCombatLog).take(8)
            _uiState.update {
                it.copy(
                    selectedMatchJapaneseId = null,
                    selectedMatchVietnameseId = null,
                    matchErrorPairIds = Pair(jpId, vnId),
                    matchCombo = 0,
                    userHp = newUserHp,
                    isUserHit = true,
                    turnCount = it.turnCount + 1,
                    isDefeat = isDefeated,
                    battleCombatLog = logs,
                    pveCombatFeedback = "Chưa khớp!「${jpItem.japanese}」không mang nghĩa「${vnItem.vietnamese}」."
                )
            }
            viewModelScope.launch {
                delay(380L)
                _uiState.update { it.copy(isUserHit = false, matchErrorPairIds = null) }
            }
        }
    }

    private fun advanceMatchRound() {
        val current = _uiState.value
        val nextRoundIdx = current.currentMatchRoundIndex + 1
        if (nextRoundIdx in current.matchRounds.indices) {
            val roundItems = current.matchRounds[nextRoundIdx]
            _uiState.update {
                it.copy(
                    currentMatchRoundIndex = nextRoundIdx,
                    leftJapaneseCards = roundItems.shuffled(),
                    rightVietnameseCards = roundItems.shuffled(),
                    matchedPairIds = emptySet(),
                    selectedMatchJapaneseId = null,
                    selectedMatchVietnameseId = null,
                    pveCombatFeedback = "Vòng ${nextRoundIdx + 1}/${current.matchRounds.size}: Tiếp tục nối Tiếng Nhật với Tiếng Việt!"
                )
            }
        }
    }

    // ==========================================
    // HÀM TÍCH HỢP SẴN GOOGLE AI STUDIO (GEMINI API)
    // ==========================================
    fun requestAiExplanationForCurrentQuestion(onResult: (String) -> Unit) {
        val activeQ = _uiState.value.activePveQuestion
        viewModelScope.launch {
            val result = AiService.explainSentenceAndWord(
                sentence = activeQ.fullSentence,
                selectedWord = activeQ.missingWord
            )
            val explanation = result.getOrElse { "Chưa thể kết nối tới Google AI Studio: ${it.localizedMessage}" }
            onResult(explanation)
        }
    }

    /**
     * explainGrammarError: Gọi AI giải thích lỗi sai ngữ pháp cho bài tập hiện tại
     * Sử dụng model gemini-1.5-flash với system_instruction sư phạm chuyên sâu.
     */
    fun explainCurrentGrammarErrorWithAi(onResult: ((String) -> Unit)? = null) {
        val current = _uiState.value
        val ex = current.activeExercise
        val question = when (ex.type) {
            QuizExerciseType.MULTIPLE_CHOICE -> ex.promptJp
            QuizExerciseType.FILL_IN_BLANK -> ex.sentenceWithBlank
            QuizExerciseType.SENTENCE_BUILDER -> ex.completeSentenceVn
            QuizExerciseType.TRANSLATION -> ex.translationQuestion.ifEmpty { ex.promptJp }
        }

        val wrongAnswer = when (ex.type) {
            QuizExerciseType.SENTENCE_BUILDER -> {
                if (current.sentenceBuilderSelectedChips.isNotEmpty()) {
                    current.sentenceBuilderSelectedChips.joinToString("")
                } else {
                    "Chưa ghép đủ câu"
                }
            }
            else -> {
                val idx = current.selectedOptionIndex ?: 0
                ex.options.getOrElse(idx) { current.activeQuestion.options.getOrElse(idx) { "" } }
            }
        }

        val correctAnswer = when (ex.type) {
            QuizExerciseType.SENTENCE_BUILDER -> ex.correctTokenOrder.joinToString("")
            else -> ex.options.getOrElse(ex.correctAnswerIndex) { current.activeQuestion.options.getOrElse(current.activeQuestion.correctAnswerIndex) { "" } }
        }

        _uiState.update { it.copy(isAiMistakeLoading = true, aiMistakeExplanation = null) }

        viewModelScope.launch {
            val result = com.example.data.service.AiService.explainGrammarError(
                question = question,
                wrongAnswer = wrongAnswer,
                correctAnswer = correctAnswer,
                exerciseType = ex.type.title
            )
            val explanation = result.getOrElse { "Lỗi kết nối Gemini: ${it.localizedMessage}" }
            _uiState.update {
                it.copy(
                    aiMistakeExplanation = explanation,
                    isAiMistakeLoading = false
                )
            }
            onResult?.invoke(explanation)
        }
    }

    // ==========================================
    // SPACED REPETITION SYSTEM (SM-2) ACTIONS
    // ==========================================

    fun loadSrsSession(filterType: SrsItemType? = _uiState.value.selectedSrsFilter) {
        val dueCards = SrsRepository.getDueOrNewCards(filterType = filterType, limit = 12)
        _uiState.update {
            it.copy(
                srsDueCards = dueCards,
                currentSrsIndex = 0,
                isSrsCardFlipped = false,
                selectedSrsFilter = filterType,
                isWeakPointsSession = false,
                sessionTotalCount = 12.coerceAtMost(dueCards.size.coerceAtLeast(1)),
                sessionCompletedCount = 0,
                sessionCorrectCount = 0,
                isSessionFinished = false
            )
        }
    }

    /**
     * Khởi động phiên học ngắn (5-10 phút) tập trung 100% vào các ĐIỂM YẾU (Weak Points)
     */
    fun startWeakPointsReviewSession() {
        val weakCards = SrsRepository.getWeakPointsSessionCards(limit = 12)
        _uiState.update {
            it.copy(
                currentScreen = Screen.SRS_REVIEW,
                srsDueCards = weakCards,
                currentSrsIndex = 0,
                isSrsCardFlipped = false,
                isWeakPointsSession = true,
                sessionTotalCount = weakCards.size.coerceAtLeast(1),
                sessionCompletedCount = 0,
                sessionCorrectCount = 0,
                isSessionFinished = false
            )
        }
    }

    fun restartSession() {
        if (_uiState.value.isWeakPointsSession) {
            startWeakPointsReviewSession()
        } else {
            loadSrsSession()
        }
    }

    fun flipSrsCard() {
        _uiState.update { it.copy(isSrsCardFlipped = !it.isSrsCardFlipped) }
    }

    fun rateSrsCard(rating: SrsRating) {
        val current = _uiState.value
        val list = current.srsDueCards
        val idx = current.currentSrsIndex
        if (idx in list.indices) {
            val (card, progress) = list[idx]
            val sm2Progress = Sm2Algorithm.calculateNextReview(progress, rating)
            // Khi người dùng bấm "Dễ / Đã thuộc" (EASY) -> đánh dấu MASTERED ngay lập tức để lưu tiến trình bài học
            val updatedProgress = if (rating == SrsRating.EASY) {
                sm2Progress.copy(
                    repetitions = maxOf(sm2Progress.repetitions, 3),
                    intervalDays = maxOf(sm2Progress.intervalDays, 21),
                    stage = SrsStage.MASTERED
                )
            } else {
                sm2Progress
            }
            SrsRepository.saveProgress(updatedProgress)

            val isCorrect = rating != SrsRating.AGAIN
            SrsRepository.recordAnswerResult(isCorrect)

            val scoreBonus = when (rating) {
                SrsRating.EASY -> 15
                SrsRating.GOOD -> 10
                SrsRating.HARD -> 5
                SrsRating.AGAIN -> 0
            }

            val nextIdx = idx + 1
            val completed = current.sessionCompletedCount + 1
            val correctCount = current.sessionCorrectCount + (if (isCorrect) 1 else 0)

            // Nếu người dùng bấm "Quên" (AGAIN) -> đưa thẻ trở lại vào hàng đợi cuối/giữa phiên để học lại ngắn hạn
            val updatedDueList = if (rating == SrsRating.AGAIN) {
                val mutable = list.toMutableList()
                val insertPos = minOf(nextIdx + 2, mutable.size)
                mutable.add(insertPos, Pair(card, updatedProgress))
                mutable.toList()
            } else {
                list
            }
            val updatedTotal = if (rating == SrsRating.AGAIN) {
                updatedDueList.size
            } else {
                current.sessionTotalCount
            }

            val isFinished = nextIdx >= updatedDueList.size || completed >= updatedTotal

            if (isFinished) {
                SrsRepository.completeSession()
            }

            _uiState.update {
                it.copy(
                    srsDueCards = updatedDueList,
                    currentSrsIndex = nextIdx,
                    isSrsCardFlipped = false,
                    score = it.score + scoreBonus,
                    reviewedCountToday = it.reviewedCountToday + 1,
                    sessionTotalCount = updatedTotal,
                    sessionCompletedCount = completed,
                    sessionCorrectCount = correctCount,
                    isSessionFinished = isFinished
                )
            }
            refreshSrsStats()
        }
    }

    fun selectSrsFilter(filterType: SrsItemType?) {
        loadSrsSession(filterType)
    }

    fun refreshSrsStats() {
        val stats = SrsRepository.getStatsSummary()
        _uiState.update { it.copy(srsStats = stats) }
    }

    fun resetSrsProgress() {
        SrsRepository.resetAllProgress()
        _uiState.update {
            it.copy(
                score = 0,
                turnCount = 1,
                reviewedCountToday = 0,
                sessionCompletedCount = 0,
                sessionCorrectCount = 0
            )
        }
        loadSrsSession()
        refreshSrsStats()
    }

    /**
     * Cập nhật API Key do người dùng điền trực tiếp
     */
    fun updateCustomApiKey(key: String) {
        com.example.data.service.AiService.API_KEY = key.trim()
    }

    // ==========================================
    // INDUCTIVE LEARNING: 4 DẠNG BÀI TẬP ĐA DẠNG
    // ==========================================

    fun selectGrammarOption(index: Int) {
        _uiState.update {
            it.copy(
                selectedOptionIndex = index,
                isAnswerEvaluated = false
            )
        }
    }

    /**
     * Sentence Builder (Ghép câu 3D): Khi người dùng bấm 1 thẻ từ trong kho
     */
    fun selectSentenceBuilderChip(chip: String) {
        _uiState.update { state ->
            val available = state.sentenceBuilderAvailableChips.toMutableList()
            val chipIndex = available.indexOf(chip)
            if (chipIndex >= 0) {
                available.removeAt(chipIndex)
                state.copy(
                    sentenceBuilderSelectedChips = state.sentenceBuilderSelectedChips + chip,
                    sentenceBuilderAvailableChips = available,
                    isAnswerEvaluated = false
                )
            } else {
                state
            }
        }
    }

    /**
     * Sentence Builder (Ghép câu 3D): Khi người dùng bấm bỏ 1 thẻ từ đã chọn để hoàn tác
     */
    fun removeSentenceBuilderChip(index: Int) {
        _uiState.update { state ->
            val selected = state.sentenceBuilderSelectedChips.toMutableList()
            if (index in selected.indices) {
                val chip = selected.removeAt(index)
                state.copy(
                    sentenceBuilderSelectedChips = selected,
                    sentenceBuilderAvailableChips = state.sentenceBuilderAvailableChips + chip,
                    isAnswerEvaluated = false
                )
            } else {
                state
            }
        }
    }

    fun submitGrammarAnswer() {
        val current = _uiState.value
        val ex = current.activeExercise

        val isCorrect = when (ex.type) {
            QuizExerciseType.SENTENCE_BUILDER -> {
                val userBuilt = current.sentenceBuilderSelectedChips.joinToString("")
                val targetBuilt = ex.correctTokenOrder.joinToString("")
                userBuilt.isNotEmpty() && (userBuilt == targetBuilt || current.sentenceBuilderSelectedChips == ex.correctTokenOrder)
            }
            QuizExerciseType.MULTIPLE_CHOICE,
            QuizExerciseType.FILL_IN_BLANK -> {
                val selected = current.selectedOptionIndex ?: return
                selected == ex.correctAnswerIndex ||
                    (!current.isLessonExerciseSession && selected == current.activeQuestion.correctAnswerIndex)
            }
            QuizExerciseType.TRANSLATION -> {
                val selected = current.selectedOptionIndex ?: return
                val targetIdx = if (ex.translationOptions.isNotEmpty()) ex.correctTranslationIndex else ex.correctAnswerIndex
                selected == targetIdx
            }
        }

        val userSummary = when (ex.type) {
            QuizExerciseType.SENTENCE_BUILDER -> current.sentenceBuilderSelectedChips.joinToString(" ")
            QuizExerciseType.TRANSLATION -> {
                val idx = current.selectedOptionIndex ?: 0
                ex.translationOptions.ifEmpty { ex.options }.getOrElse(idx) { "" }
            }
            else -> {
                val idx = current.selectedOptionIndex ?: 0
                ex.options.getOrElse(idx) { current.activeQuestion.options.getOrElse(idx) { "" } }
            }
        }

        SrsRepository.recordAnswerResult(isCorrect)
        if (isCorrect && ex.targetCardId.isNotBlank()) {
            SrsRepository.markCardMastered(ex.targetCardId, ex.targetItemType)
            refreshSrsStats()
        }

        val completed = current.sessionCompletedCount + 1
        val correctCount = current.sessionCorrectCount + (if (isCorrect) 1 else 0)

        val scoreBonus = if (isCorrect) 50 else 0
        _uiState.update {
            it.copy(
                isAnswerEvaluated = true,
                isAnswerCorrect = isCorrect,
                score = it.score + scoreBonus,
                userSubmittedAnswerSummary = userSummary,
                sessionCompletedCount = completed,
                sessionCorrectCount = correctCount
            )
        }

        // Tự động phân tích lỗi sai qua Gemini 1.5 Flash khi làm sai
        if (!isCorrect) {
            explainCurrentGrammarErrorWithAi()
        }
    }

    fun retrySimilarQuestion() {
        val current = _uiState.value
        val ex = current.activeExercise
        val similar = QuizExerciseRepository.sanitizeExercise(
            ex.similarExercise
                ?: current.exerciseList.shuffled().firstOrNull { it.id != ex.id }
                ?: ex
        )

        val correspondingQuestion = QuizExerciseRepository.toGrammarQuestion(similar)

        _uiState.update {
            it.copy(
                activeExercise = similar,
                activeQuestion = correspondingQuestion,
                selectedOptionIndex = null,
                sentenceBuilderSelectedChips = emptyList(),
                sentenceBuilderAvailableChips = QuizExerciseRepository.scrambleTokensNonMatching(
                    similar.builderTokens,
                    similar.correctTokenOrder
                ),
                isAnswerEvaluated = false,
                isAnswerCorrect = false,
                isShowingSimilarQuestion = true,
                aiMistakeExplanation = null,
                isAiMistakeLoading = false
            )
        }
    }

    fun nextGrammarQuestion() {
        val current = _uiState.value
        val allExercises = current.exerciseList.ifEmpty { QuizExerciseRepository.getAllExercises() }
        if (current.isLessonExerciseSession && current.currentExerciseIndex + 1 >= allExercises.size) {
            testTimerJob?.cancel()
            SrsRepository.completeSession()
            refreshSrsStats()
            _uiState.update {
                it.copy(
                    isSessionFinished = true,
                    isAnswerEvaluated = false
                )
            }
            return
        }

        val nextIdx = (current.currentExerciseIndex + 1) % allExercises.size
        val nextEx = QuizExerciseRepository.sanitizeExercise(allExercises[nextIdx])
        val nextQuestion = QuizExerciseRepository.toGrammarQuestion(nextEx)

        _uiState.update {
            it.copy(
                currentExerciseIndex = nextIdx,
                currentGrammarIndex = (it.currentGrammarIndex + 1) % it.grammarQuestions.size,
                activeExercise = nextEx,
                activeQuestion = nextQuestion,
                selectedOptionIndex = null,
                sentenceBuilderSelectedChips = emptyList(),
                sentenceBuilderAvailableChips = QuizExerciseRepository.scrambleTokensNonMatching(
                    nextEx.builderTokens,
                    nextEx.correctTokenOrder
                ),
                isAnswerEvaluated = false,
                isAnswerCorrect = false,
                isShowingSimilarQuestion = false,
                aiMistakeExplanation = null,
                isAiMistakeLoading = false
            )
        }
    }

    fun selectVocabIndex(index: Int) {
        _uiState.update { it.copy(currentVocabIndex = index) }
    }

    fun nextVocab() {
        val current = _uiState.value
        val nextIdx = (current.currentVocabIndex + 1) % current.vocabularyList.size
        _uiState.update { it.copy(currentVocabIndex = nextIdx) }
    }

    fun prevVocab() {
        val current = _uiState.value
        val prevIdx = if (current.currentVocabIndex > 0) current.currentVocabIndex - 1 else current.vocabularyList.size - 1
        _uiState.update { it.copy(currentVocabIndex = prevIdx) }
    }
}
