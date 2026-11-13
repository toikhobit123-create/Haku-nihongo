package com.example

import com.example.ui.state.NihongoViewModel
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

class NihongoViewModelTest {

    private lateinit var viewModel: NihongoViewModel

    @Before
    fun setUp() {
        com.example.data.repository.SrsRepository.resetAllProgress()
        viewModel = NihongoViewModel()
    }

    @Test
    fun testGrammarCorrectAnswer_updatesScoreAndCorrectState() {
        val initialScore = viewModel.uiState.value.score
        val correctIndex = viewModel.uiState.value.activeQuestion.correctAnswerIndex

        viewModel.selectGrammarOption(correctIndex)
        viewModel.submitGrammarAnswer()

        val state = viewModel.uiState.value
        assertTrue(state.isAnswerEvaluated)
        assertTrue(state.isAnswerCorrect)
        assertEquals(initialScore + 50, state.score)
    }

    @Test
    fun testGrammarWrongAnswer_andRetrySimilarQuestion() {
        val correctIndex = viewModel.uiState.value.activeQuestion.correctAnswerIndex
        val wrongIndex = if (correctIndex == 0) 1 else 0
        viewModel.selectGrammarOption(wrongIndex)
        viewModel.submitGrammarAnswer()

        val stateAfterSubmit = viewModel.uiState.value
        assertTrue(stateAfterSubmit.isAnswerEvaluated)
        assertFalse(stateAfterSubmit.isAnswerCorrect)

        // Retry similar question
        viewModel.retrySimilarQuestion()
        val stateAfterRetry = viewModel.uiState.value
        assertFalse(stateAfterRetry.isAnswerEvaluated)
        assertFalse(stateAfterRetry.isAnswerCorrect)
        assertTrue(stateAfterRetry.isShowingSimilarQuestion)
        assertNotNull(stateAfterRetry.activeQuestion)
    }

    @Test
    fun testSm2Algorithm_ratingAgain_setsShortTermRelearningAndIncreasesLapses() {
        val now = 1_778_000_000_000L
        val initialProgress = com.example.data.model.SrsProgress(
            id = "test_card",
            type = com.example.data.model.SrsItemType.VOCABULARY,
            repetitions = 3,
            intervalDays = 6,
            easeFactor = 2.5,
            stage = com.example.data.model.SrsStage.REVIEW,
            lapses = 0
        )

        val updated = com.example.data.srs.Sm2Algorithm.calculateNextReview(
            initialProgress,
            com.example.data.model.SrsRating.AGAIN,
            currentTime = now
        )

        assertEquals(0, updated.repetitions)
        assertEquals(0, updated.intervalDays)
        assertEquals(1, updated.lapses)
        assertEquals(com.example.data.model.SrsStage.LEARNING, updated.stage)
        assertEquals(
            now + com.example.data.srs.Sm2Algorithm.RELEARNING_STEP_MINUTES * 60_000L,
            updated.nextReviewTimestamp
        )
        assertEquals(
            "10 phút",
            com.example.data.srs.Sm2Algorithm.previewIntervalText(
                initialProgress,
                com.example.data.model.SrsRating.AGAIN
            )
        )
    }

    @Test
    fun testSm2Algorithm_ratingGoodAndEasy_progressesInterval() {
        var progress = com.example.data.model.SrsProgress(
            id = "test_2",
            type = com.example.data.model.SrsItemType.VOCABULARY
        )

        // First review Good -> interval 1, reps 1
        progress = com.example.data.srs.Sm2Algorithm.calculateNextReview(
            progress,
            com.example.data.model.SrsRating.GOOD
        )
        assertEquals(1, progress.repetitions)
        assertEquals(1, progress.intervalDays)

        // Second review Good -> interval 6, reps 2
        progress = com.example.data.srs.Sm2Algorithm.calculateNextReview(
            progress,
            com.example.data.model.SrsRating.GOOD
        )
        assertEquals(2, progress.repetitions)
        assertEquals(6, progress.intervalDays)

        // Third review Easy -> interval increases with EF bonus
        val previousInterval = progress.intervalDays
        progress = com.example.data.srs.Sm2Algorithm.calculateNextReview(
            progress,
            com.example.data.model.SrsRating.EASY
        )
        assertEquals(3, progress.repetitions)
        assertTrue(progress.intervalDays > previousInterval)
        assertTrue(progress.easeFactor >= 2.5)
    }

    @Test
    fun testSrsSession_cardFlippingAndRating() {
        viewModel.loadSrsSession()
        val initialDue = viewModel.uiState.value.srsDueCards.size
        assertTrue(initialDue > 0)

        // Initial front face
        assertFalse(viewModel.uiState.value.isSrsCardFlipped)

        // Flip card
        viewModel.flipSrsCard()
        assertTrue(viewModel.uiState.value.isSrsCardFlipped)

        // Rate card as Good
        val activePair = viewModel.uiState.value.srsDueCards.getOrNull(viewModel.uiState.value.currentSrsIndex)
        assertNotNull(activePair)
        viewModel.rateSrsCard(com.example.data.model.SrsRating.GOOD)

        // Verifies card flip resets for next card and score increases
        assertFalse(viewModel.uiState.value.isSrsCardFlipped)
        assertTrue(viewModel.uiState.value.score > 0)
    }

    @Test
    fun testPveCardBattle_matchWord_reducesBossHpAndAddsScore() {
        val initialBossHp = viewModel.uiState.value.bossHp
        val initialScore = viewModel.uiState.value.score
        val activeQ = viewModel.uiState.value.activePveQuestion
        val correctVocab = activeQ.options.first { it.kanji == activeQ.missingWord }

        viewModel.playPveCard(correctVocab)

        val state = viewModel.uiState.value
        assertEquals(initialBossHp - 40, state.bossHp)
        assertEquals(initialScore + 100, state.score)
        assertEquals(correctVocab.kanji, state.filledBlankWord)
    }

    @Test
    fun testPveCardBattle_wrongWord_reducesUserHp() {
        val initialUserHp = viewModel.uiState.value.userHp
        val activeQ = viewModel.uiState.value.activePveQuestion
        val wrongVocab = activeQ.options.first { it.kanji != activeQ.missingWord }

        viewModel.playPveCard(wrongVocab)

        val state = viewModel.uiState.value
        assertEquals(initialUserHp - 25, state.userHp)
        assertTrue(state.isUserHit)
    }

    @Test
    fun testAiApiKey_isConfigured() {
        // Kiểm tra cơ chế cấu hình và cập nhật API key an toàn qua AiService
        val initialKey = com.example.data.service.AiService.getApiKey()
        assertNotNull(initialKey)

        com.example.data.service.AiService.updateCustomApiKey("test_api_key_12345")
        assertEquals("test_api_key_12345", com.example.data.service.AiService.getApiKey())
        assertTrue(com.example.data.service.AiService.API_KEY.isNotBlank())

        // Đặt lại trạng thái
        com.example.data.service.AiService.updateCustomApiKey("")
    }

    @Test
    fun testStudyCategories_switchAndSelectGrammarFirst() {
        val state = viewModel.uiState.value
        // Verify default category is GRAMMAR (imported first)
        assertEquals(com.example.data.model.StudyCategory.GRAMMAR, state.selectedStudyCategory)

        // Switch to VOCABULARY
        viewModel.selectStudyCategory(com.example.data.model.StudyCategory.VOCABULARY)
        assertEquals(com.example.data.model.StudyCategory.VOCABULARY, viewModel.uiState.value.selectedStudyCategory)

        // Switch to KANJI
        viewModel.selectStudyCategory(com.example.data.model.StudyCategory.KANJI)
        assertEquals(com.example.data.model.StudyCategory.KANJI, viewModel.uiState.value.selectedStudyCategory)
    }

    @Test
    fun testImportedGrammar_np01_existsWithExactJsonContent() {
        val grammarPoints = viewModel.uiState.value.grammarPoints
        val np01 = grammarPoints.firstOrNull { it.id == "np_01" }

        assertNotNull(np01)
        assertEquals("〜ことにしています", np01!!.title)
        assertEquals("V-ru / V-nai + ことにしている", np01.structure)
        assertEquals("Biểu thị thói quen do chính bản thân tự quyết định và đang duy trì.", np01.meaning)
        assertTrue(np01.examples.isNotEmpty())
        assertEquals("健康のために、毎日野菜を食べることにしています。", np01.examples[0].jp)
        assertEquals("Vì sức khỏe, tôi quyết định duy trì thói quen ăn rau mỗi ngày.", np01.examples[0].vn)
    }

    @Test
    fun testStartQuizForImportedGrammar_navigatesToQuestion() {
        viewModel.startQuizForGrammar("np_01")
        val state = viewModel.uiState.value

        assertEquals(com.example.ui.state.Screen.INDUCTIVE_LEARNING, state.currentScreen)
        assertEquals("grammar_0_koto", state.activeQuestion.id)
        assertTrue(state.activeQuestion.grammarPoint.contains("〜ことにしています"))
    }

    @Test
    fun testN3Vocabulary_loads23LessonsAnd1138Items() {
        val state = viewModel.uiState.value
        assertEquals(23, state.vocabLessons.size)
        assertEquals(1138, state.vocabularyList.size)

        // Verify Lesson 1 first item
        val lesson1 = state.vocabLessons.first()
        assertEquals(1, lesson1.lessonId)
        assertEquals("Bài 1", lesson1.title)
        assertEquals(50, lesson1.vocabList.size)
        assertEquals("相手", lesson1.vocabList[0].kanji)
        assertEquals("あいて", lesson1.vocabList[0].hiragana)
        assertTrue(lesson1.vocabList[0].meaning.contains("Đối phương"))

        // Verify Lesson 23
        val lesson23 = state.vocabLessons.last()
        assertEquals(23, lesson23.lessonId)
        assertEquals("Bài 23", lesson23.title)
        assertEquals(38, lesson23.vocabList.size)
    }

    @Test
    fun testN3Vocabulary_lessonAndSearchFiltering() {
        viewModel.selectVocabLesson(2)
        assertEquals(2, viewModel.uiState.value.selectedVocabLessonId)

        viewModel.setVocabSearchQuery("幸運")
        assertEquals("幸運", viewModel.uiState.value.vocabSearchQuery)

        // Reset
        viewModel.selectVocabLesson(null)
        assertEquals(null, viewModel.uiState.value.selectedVocabLessonId)
        viewModel.setVocabSearchQuery("")
        assertEquals("", viewModel.uiState.value.vocabSearchQuery)
    }

    @Test
    fun testSentenceBuilder_selectAndRemoveChips_andSubmitCorrectly() {
        // Advance until a SENTENCE_BUILDER exercise is active
        var attempts = 0
        while (viewModel.uiState.value.activeExercise.type != com.example.data.model.QuizExerciseType.SENTENCE_BUILDER && attempts < 10) {
            viewModel.nextGrammarQuestion()
            attempts++
        }

        val ex = viewModel.uiState.value.activeExercise
        assertEquals(com.example.data.model.QuizExerciseType.SENTENCE_BUILDER, ex.type)
        assertTrue(viewModel.uiState.value.sentenceBuilderAvailableChips.isNotEmpty())

        // Select first chip
        val firstTargetChip = ex.correctTokenOrder[0]
        viewModel.selectSentenceBuilderChip(firstTargetChip)
        assertEquals(listOf(firstTargetChip), viewModel.uiState.value.sentenceBuilderSelectedChips)
        assertFalse(viewModel.uiState.value.sentenceBuilderAvailableChips.contains(firstTargetChip))

        // Remove that chip to test undo
        viewModel.removeSentenceBuilderChip(0)
        assertTrue(viewModel.uiState.value.sentenceBuilderSelectedChips.isEmpty())
        assertTrue(viewModel.uiState.value.sentenceBuilderAvailableChips.contains(firstTargetChip))

        // Build entire correct sentence
        ex.correctTokenOrder.forEach { chip ->
            viewModel.selectSentenceBuilderChip(chip)
        }
        assertEquals(ex.correctTokenOrder, viewModel.uiState.value.sentenceBuilderSelectedChips)

        // Submit
        val initialScore = viewModel.uiState.value.score
        viewModel.submitGrammarAnswer()
        assertTrue(viewModel.uiState.value.isAnswerEvaluated)
        assertTrue(viewModel.uiState.value.isAnswerCorrect)
        assertEquals(initialScore + 50, viewModel.uiState.value.score)
    }

    @Test
    fun testQuizEngine_containsAllFourExerciseTypes() {
        val exercises = viewModel.uiState.value.exerciseList
        assertTrue(exercises.isNotEmpty())

        val types = exercises.map { it.type }.toSet()
        assertTrue(types.contains(com.example.data.model.QuizExerciseType.MULTIPLE_CHOICE))
        assertTrue(types.contains(com.example.data.model.QuizExerciseType.FILL_IN_BLANK))
        assertTrue(types.contains(com.example.data.model.QuizExerciseType.SENTENCE_BUILDER))
        assertTrue(types.contains(com.example.data.model.QuizExerciseType.TRANSLATION))
    }

    @Test
    fun testAiMistakeAnalysis_triggeredOnWrongAnswer() {
        // Submit a wrong answer
        val wrongIndex = 99 // out of range / invalid index
        viewModel.selectGrammarOption(wrongIndex)
        viewModel.submitGrammarAnswer()

        val state = viewModel.uiState.value
        assertTrue(state.isAnswerEvaluated)
        assertFalse(state.isAnswerCorrect)
        // AI analysis is triggered in viewModelScope
        assertTrue(state.isAiMistakeLoading || state.aiMistakeExplanation != null)
    }

    @Test
    fun testResetSrsProgress_onlyClearsWhenExplicitlyCalled() {
        // Rate a card to increase score and SRS data
        viewModel.loadSrsSession()
        viewModel.rateSrsCard(com.example.data.model.SrsRating.GOOD)
        assertTrue(viewModel.uiState.value.score > 0)

        // Explicitly calling resetSrsProgress clears data
        viewModel.resetSrsProgress()
        assertEquals(0, viewModel.uiState.value.score)
        assertEquals(0, viewModel.uiState.value.reviewedCountToday)
    }

    @Test
    fun testGrammarLessonExercises_alwaysBlankGrammarWithoutLeakingAnswer() {
        for (lessonId in 1..5) {
            val exercises = viewModel.buildExercisesForLesson(
                com.example.data.model.StudyCategory.GRAMMAR,
                lessonId
            )
            assertTrue("Lesson $lessonId should have exercises", exercises.isNotEmpty())
            exercises.forEach { ex ->
                if (ex.type == com.example.data.model.QuizExerciseType.MULTIPLE_CHOICE ||
                    ex.type == com.example.data.model.QuizExerciseType.FILL_IN_BLANK
                ) {
                    assertTrue(
                        "Exercise ${ex.id} (${ex.grammarPoint}) should contain blank placeholder",
                        ex.promptJp.contains("＿＿＿") && ex.sentenceWithBlank.contains("＿＿＿")
                    )
                    val cleanTarget = ex.grammarPoint.replace("〜", "").replace("。", "").trim()
                    if (cleanTarget.length >= 3) {
                        assertFalse(
                            "Exercise ${ex.id} leaked $cleanTarget in ${ex.promptJp}",
                            ex.promptJp.contains(cleanTarget)
                        )
                    }
                }
            }
        }
    }

    @Test
    fun testSrsLessonMastery_persistsAfterFinishingLesson() {
        viewModel.startSrsForLesson(com.example.data.model.StudyCategory.GRAMMAR, 1)
        val lessonCardIds = viewModel.uiState.value.srsDueCards.map { it.first.id }
        assertEquals(5, lessonCardIds.size)

        repeat(lessonCardIds.size) {
            viewModel.flipSrsCard()
            viewModel.rateSrsCard(com.example.data.model.SrsRating.EASY)
        }

        assertTrue(viewModel.uiState.value.isSessionFinished)
        val (masteredCount, pct) = com.example.data.repository.SrsRepository.getLessonMastery(
            com.example.data.model.SrsItemType.GRAMMAR,
            lessonCardIds,
            1
        )
        assertEquals(5, masteredCount)
        assertEquals(100f, pct, 0.01f)

        // Recreate ViewModel to ensure progress is not wiped on init
        val newViewModel = NihongoViewModel()
        val (persistedMastered, persistedPct) = com.example.data.repository.SrsRepository.getLessonMastery(
            com.example.data.model.SrsItemType.GRAMMAR,
            newViewModel.uiState.value.grammarPoints.filter { it.lessonId == 1 }.map { it.id },
            1
        )
        assertEquals(5, persistedMastered)
        assertEquals(100f, persistedPct, 0.01f)
    }

    @Test
    fun testVocabularyPractice_correctAnswerSavesMastery() {
        viewModel.startPracticeForLesson(com.example.data.model.StudyCategory.VOCABULARY, 1)
        val ex = viewModel.uiState.value.activeExercise
        assertTrue(ex.targetCardId.isNotBlank())

        when (ex.type) {
            com.example.data.model.QuizExerciseType.SENTENCE_BUILDER -> {
                ex.correctTokenOrder.forEach { viewModel.selectSentenceBuilderChip(it) }
            }
            com.example.data.model.QuizExerciseType.TRANSLATION -> {
                viewModel.selectGrammarOption(ex.correctTranslationIndex)
            }
            else -> {
                viewModel.selectGrammarOption(ex.correctAnswerIndex)
            }
        }
        viewModel.submitGrammarAnswer()

        assertTrue(viewModel.uiState.value.isAnswerCorrect)
        assertTrue(com.example.data.repository.SrsRepository.isCardMastered(ex.targetCardId))
    }

    @Test
    fun testMatchGame_selectJapaneseAndVietnamese_pairsCorrectlyAndSavesProgress() {
        viewModel.startCardBattleForLesson(com.example.data.model.StudyCategory.VOCABULARY, 1)
        val initialState = viewModel.uiState.value
        assertTrue(initialState.matchRounds.isNotEmpty())
        assertTrue(initialState.leftJapaneseCards.isNotEmpty())
        assertEquals(initialState.leftJapaneseCards.size, initialState.rightVietnameseCards.size)

        val targetPair = initialState.leftJapaneseCards.first()
        val wrongPair = initialState.rightVietnameseCards.first { it.id != targetPair.id }

        // Test wrong pair reduces HP
        val hpBefore = viewModel.uiState.value.userHp
        viewModel.selectMatchJapaneseCard(targetPair.id)
        viewModel.selectMatchVietnameseCard(wrongPair.id)
        assertTrue(viewModel.uiState.value.userHp < hpBefore)
        assertFalse(targetPair.id in viewModel.uiState.value.matchedPairIds)

        // Test correct pair matches and marks mastered
        val scoreBefore = viewModel.uiState.value.score
        viewModel.selectMatchJapaneseCard(targetPair.id)
        viewModel.selectMatchVietnameseCard(targetPair.id)
        val afterMatch = viewModel.uiState.value
        assertTrue(targetPair.id in afterMatch.matchedPairIds)
        assertTrue(afterMatch.score > scoreBefore)
        assertTrue(com.example.data.repository.SrsRepository.isCardMastered(targetPair.cardId))
    }

    @Test
    fun testStreak_yearRolloverIncrementsAndFiveDayGapResetsToOne() {
        com.example.data.repository.SrsRepository.resetAllProgress()
        val tz = java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
        val cal = java.util.Calendar.getInstance(tz)

        // Day 1: 2025-12-31 21:00
        cal.set(2025, java.util.Calendar.DECEMBER, 31, 21, 0, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        val dec31 = cal.timeInMillis
        com.example.data.repository.SrsRepository.completeSession(dec31, tz)
        assertEquals(1, com.example.data.repository.SrsRepository.getStatsSummary(dec31, tz).streakDays)

        // Same day again -> still 1
        com.example.data.repository.SrsRepository.completeSession(dec31 + 3600_000L, tz)
        assertEquals(1, com.example.data.repository.SrsRepository.getStatsSummary(dec31 + 3600_000L, tz).streakDays)

        // Next day across year boundary: 2026-01-01 08:00 -> increments to 2
        cal.set(2026, java.util.Calendar.JANUARY, 1, 8, 0, 0)
        val jan1 = cal.timeInMillis
        com.example.data.repository.SrsRepository.completeSession(jan1, tz)
        assertEquals(2, com.example.data.repository.SrsRepository.getStatsSummary(jan1, tz).streakDays)

        // Skip 5 days: 2026-01-06 09:00 -> expired streak shows 0 before studying, resets to 1 after session
        cal.set(2026, java.util.Calendar.JANUARY, 6, 9, 0, 0)
        val jan6 = cal.timeInMillis
        assertEquals(0, com.example.data.repository.SrsRepository.getStatsSummary(jan6, tz).streakDays)
        com.example.data.repository.SrsRepository.completeSession(jan6, tz)
        assertEquals(1, com.example.data.repository.SrsRepository.getStatsSummary(jan6, tz).streakDays)
    }

    @Test
    fun testSm2Algorithm_hardIntervalAdvancesAndDueTimeSnapsToStartOfDay() {
        val tz = java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
        val cal = java.util.Calendar.getInstance(tz)
        cal.set(2026, java.util.Calendar.MAY, 10, 22, 30, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        val nightTime = cal.timeInMillis

        // Initial GOOD -> interval 1, reps 1, due 2026-05-11 00:00:00
        val afterGood = com.example.data.srs.Sm2Algorithm.calculateNextReview(
            com.example.data.model.SrsProgress("card_hard", com.example.data.model.SrsItemType.VOCABULARY),
            com.example.data.model.SrsRating.GOOD,
            nightTime,
            tz
        )
        assertEquals(1, afterGood.intervalDays)
        assertEquals(1, afterGood.repetitions)

        // Due at 07:00 next morning (even though < 24h passed since 22:30!)
        cal.set(2026, java.util.Calendar.MAY, 11, 7, 0, 0)
        val nextMorning = cal.timeInMillis
        assertTrue(afterGood.nextReviewTimestamp <= nextMorning)

        // Pressing HARD on interval=1, reps=1 MUST advance to >= 2 (not stay stuck at 1)
        assertEquals("2 ngày", com.example.data.srs.Sm2Algorithm.previewIntervalText(afterGood, com.example.data.model.SrsRating.HARD))
        val afterHard = com.example.data.srs.Sm2Algorithm.calculateNextReview(
            afterGood,
            com.example.data.model.SrsRating.HARD,
            nextMorning,
            tz
        )
        assertEquals(2, afterHard.intervalDays)
        assertEquals(2, afterHard.repetitions)
    }

    @Test
    fun testSrsRepository_deduplicatesVocabularyCardsAndSharesCanonicalMastery() {
        val vocabCards = com.example.data.repository.SrsRepository.getAllCards()
            .filter { it.type == com.example.data.model.SrsItemType.VOCABULARY }
        val uniquePairs = vocabCards.map { "${it.frontText}|${it.readingText}" }.toSet()
        assertEquals(vocabCards.size, uniquePairs.size)
        assertEquals(1095, vocabCards.size)

        // Check cross-lesson canonical mastery: "現在" appears in Lesson 1 and Lesson 21
        val lessons = viewModel.uiState.value.vocabLessons
        val l1Card = lessons.first { it.lessonId == 1 }.vocabList.first { it.kanji == "現在" }
        val l21Card = lessons.first { it.lessonId == 21 }.vocabList.first { it.kanji == "現在" }
        assertFalse(com.example.data.repository.SrsRepository.isCardMastered(l1Card.id))
        com.example.data.repository.SrsRepository.markCardMastered(l1Card.id, com.example.data.model.SrsItemType.VOCABULARY)
        assertTrue(com.example.data.repository.SrsRepository.isCardMastered(l21Card.id))
    }

    @Test
    fun testVocabulary_realRomajiAndNoFakeTemplates() {
        val allVocab = viewModel.uiState.value.vocabularyList
        val aite = allVocab.first { it.kanji == "相手" }
        assertEquals("aite", aite.romaji)

        val kekka = allVocab.first { it.kanji == "結果" }
        assertEquals("kekka", kekka.romaji)

        // No fake boilerplate template anywhere
        assertFalse(allVocab.any { it.exampleSentence.contains("JLPT N3で重要です") })
    }

    @Test
    fun testMoshiParsers_handleEscapedQuotesProperly() {
        val vocabJson = """
            [
              {
                "lesson_id": 1,
                "title": "Test \"Quoted\" Lesson",
                "vocab_list": [
                  { "word": "約束", "reading": "やくそく", "meaning": "Lời \"hứa\", giao ước" }
                ]
              }
            ]
        """.trimIndent()
        val parsedVocab = com.example.data.repository.VocabularyDataLoader.parseVocabularyJson(vocabJson)
        assertEquals(1, parsedVocab.size)
        assertEquals("Test \"Quoted\" Lesson", parsedVocab[0].title)
        assertEquals("Lời \"hứa\", giao ước", parsedVocab[0].vocabList[0].meaning)
        assertEquals("yakusoku", parsedVocab[0].vocabList[0].romaji)

        val grammarJson = """
            [
              {
                "lesson_id": 1,
                "title": "Ngữ pháp \"N3\"",
                "grammar_list": [
                  { "grammar": "〜つもり", "meaning": "Dự định \"sẽ\" làm gì" }
                ]
              }
            ]
        """.trimIndent()
        val parsedGrammar = com.example.data.repository.GrammarDataLoader.parseGrammarJson(grammarJson)
        assertEquals(1, parsedGrammar.size)
        assertEquals("Ngữ pháp \"N3\"", parsedGrammar[0].title)
        assertEquals("Dự định \"sẽ\" làm gì", parsedGrammar[0].grammarList[0].meaning)
    }

    @Test
    fun testGrammarDataLoader_multipleKotoItemsReceiveDistinctIds() {
        val json = """
            [
              {
                "lesson_id": 1,
                "title": "Bài 1",
                "grammar_list": [
                  { "grammar": "〜ことにしている", "meaning": "Quyết định tự thân" },
                  { "grammar": "〜ことにしています", "meaning": "Thói quen duy trì" }
                ]
              }
            ]
        """.trimIndent()
        val parsed = com.example.data.repository.GrammarDataLoader.parseGrammarJson(json)
        val items = parsed[0].grammarList
        assertEquals(2, items.size)
        assertEquals("np_01", items[0].id)
        assertEquals("gp_1_2", items[1].id)
        assertEquals(2, items.map { it.id }.toSet().size)

        // Real bundled grammar JSON must also have 100% unique IDs
        val allGrammarIds = com.example.data.repository.GrammarDataLoader.getAllGrammar().map { it.id }
        assertEquals(allGrammarIds.size, allGrammarIds.toSet().size)
    }

    @Test
    fun testSrsRepository_masteryChartUsesRealDailySnapshotsNotInterpolation() {
        com.example.data.repository.SrsRepository.resetAllProgress()
        val tz = java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
        val cal = java.util.Calendar.getInstance(tz)

        // Day 1: 2026-05-01 - master 1 grammar card
        cal.set(2026, java.util.Calendar.MAY, 1, 10, 0, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        val day1 = cal.timeInMillis
        com.example.data.repository.SrsRepository.markCardMastered(
            "np_01",
            com.example.data.model.SrsItemType.GRAMMAR,
            now = day1,
            timeZone = tz
        )

        // Day 3: 2026-05-03 - master a second grammar card
        cal.set(2026, java.util.Calendar.MAY, 3, 10, 0, 0)
        val day3 = cal.timeInMillis
        com.example.data.repository.SrsRepository.markCardMastered(
            "gp_1_1",
            com.example.data.model.SrsItemType.GRAMMAR,
            now = day3,
            timeZone = tz
        )

        val stats = com.example.data.repository.SrsRepository.getStatsSummary(now = day3, timeZone = tz)
        val points = stats.masteryOverTime
        assertEquals(7, points.size)
        // Days before 2026-05-01 (index 0..3: Apr 27..Apr 30) must be 0, NOT interpolated > 0
        assertEquals(0, points[0].grammarMastered)
        assertEquals(0, points[3].grammarMastered)
        // 2026-05-01 (index 4) -> 1 mastered
        assertEquals(1, points[4].grammarMastered)
        // 2026-05-02 (index 5) -> carried forward 1
        assertEquals(1, points[5].grammarMastered)
        // 2026-05-03 (index 6) -> 2 mastered
        assertEquals(2, points[6].grammarMastered)
    }

    @Test
    fun testSrsSession_ratingAgainRequeuesCardInActiveSession() {
        viewModel.startSrsForLesson(com.example.data.model.StudyCategory.GRAMMAR, 1)
        val initialCards = viewModel.uiState.value.srsDueCards
        assertEquals(5, initialCards.size)
        val forgottenCardId = initialCards[0].first.id

        viewModel.flipSrsCard()
        viewModel.rateSrsCard(com.example.data.model.SrsRating.AGAIN)

        val afterAgain = viewModel.uiState.value
        assertFalse(afterAgain.isSessionFinished)
        assertEquals(6, afterAgain.srsDueCards.size)
        assertEquals(1, afterAgain.srsDueCards.drop(1).count { it.first.id == forgottenCardId })
    }

    @Test
    fun testNotificationHelper_computeNextTriggerTimeMillis() {
        val tz = java.util.TimeZone.getTimeZone("Asia/Ho_Chi_Minh")
        val cal = java.util.Calendar.getInstance(tz)

        // At 18:00, scheduling 20:00 triggers same day at 20:00
        cal.set(2026, java.util.Calendar.MAY, 10, 18, 0, 0)
        cal.set(java.util.Calendar.MILLISECOND, 0)
        val beforeTarget = cal.timeInMillis
        val triggerSameDay = com.example.util.NotificationHelper.computeNextTriggerTimeMillis(20, 0, beforeTarget, tz)
        cal.timeInMillis = triggerSameDay
        assertEquals(10, cal.get(java.util.Calendar.DAY_OF_MONTH))
        assertEquals(20, cal.get(java.util.Calendar.HOUR_OF_DAY))
        assertEquals(0, cal.get(java.util.Calendar.MINUTE))

        // At 21:00, scheduling 20:00 rolls over to May 11 at 20:00
        cal.set(2026, java.util.Calendar.MAY, 10, 21, 0, 0)
        val afterTarget = cal.timeInMillis
        val triggerNextDay = com.example.util.NotificationHelper.computeNextTriggerTimeMillis(20, 0, afterTarget, tz)
        cal.timeInMillis = triggerNextDay
        assertEquals(11, cal.get(java.util.Calendar.DAY_OF_MONTH))
        assertEquals(20, cal.get(java.util.Calendar.HOUR_OF_DAY))
    }

    @Test
    fun testQuizEngine_fillInBlank_dynamicallyHidesCorrectAnswerFragments() {
        // Test dynamic sanitization on a synthetic exercise whose sentence leaked the correct choice
        val leaky = com.example.data.model.QuizExercise(
            id = "leaky_fib",
            type = com.example.data.model.QuizExerciseType.FILL_IN_BLANK,
            grammarPoint = "もし〜ても",
            instruction = "Test",
            promptJp = "もし失敗しても、あきらめない。",
            promptVn = "Dù thất bại cũng không bỏ cuộc.",
            sentenceWithBlank = "もし失敗しても、あきらめない。",
            blankMissingWord = "ても",
            options = listOf("〜ばよかった", "もし〜ても", "〜につれて", "〜せいで"),
            correctAnswerIndex = 1
        )
        val sanitized = com.example.data.repository.QuizExerciseRepository.sanitizeExercise(leaky)
        assertTrue(sanitized.sentenceWithBlank.contains("＿＿＿"))
        assertFalse(sanitized.sentenceWithBlank.contains("もし"))
        assertFalse(sanitized.sentenceWithBlank.contains("ても"))

        // Verify all generated grammar lessons never leak >=2-char answer tokens in promptJp or sentenceWithBlank
        for (lessonId in 1..27) {
            val exercises = viewModel.buildExercisesForLesson(
                com.example.data.model.StudyCategory.GRAMMAR,
                lessonId
            )
            exercises.filter {
                it.type == com.example.data.model.QuizExerciseType.FILL_IN_BLANK ||
                    it.type == com.example.data.model.QuizExerciseType.MULTIPLE_CHOICE
            }.forEach { ex ->
                assertTrue("Lesson $lessonId ${ex.id} missing blank", ex.sentenceWithBlank.contains("＿＿＿"))
                val correctOpt = ex.options.getOrNull(ex.correctAnswerIndex).orEmpty()
                val tokens = correctOpt
                    .split("〜", "～", "。", "…", "...", "・", "？", "?")
                    .map { it.trim() }
                    .filter { it.length >= 2 }
                tokens.forEach { tok ->
                    assertFalse(
                        "Lesson $lessonId ${ex.id} leaked '$tok' in '${ex.sentenceWithBlank}'",
                        ex.sentenceWithBlank.contains(tok)
                    )
                }
            }
        }
    }

    @Test
    fun testQuizEngine_sentenceBuilder_scramblesTokensAndHidesJapanesePrompt() {
        val allStatic = com.example.data.repository.QuizExerciseRepository.getAllExercises()
            .filter { it.type == com.example.data.model.QuizExerciseType.SENTENCE_BUILDER }
        assertTrue(allStatic.isNotEmpty())
        allStatic.forEach { ex ->
            assertTrue(ex.builderTokens.size >= 2)
            assertFalse(
                "Static SB ${ex.id} builderTokens matched correctTokenOrder",
                ex.builderTokens == ex.correctTokenOrder
            )
            assertEquals("", ex.promptJp)
            assertEquals("", ex.sentenceWithBlank)
        }

        for (lessonId in 1..10) {
            val generated = viewModel.buildExercisesForLesson(
                com.example.data.model.StudyCategory.GRAMMAR,
                lessonId
            ).filter { it.type == com.example.data.model.QuizExerciseType.SENTENCE_BUILDER }
            generated.forEach { ex ->
                assertTrue(ex.builderTokens.size >= 2)
                assertFalse(
                    "Generated SB ${ex.id} builderTokens matched correctTokenOrder",
                    ex.builderTokens == ex.correctTokenOrder
                )
                assertEquals("", ex.promptJp)
                assertEquals("", ex.sentenceWithBlank)
            }
        }
    }

    @Test
    fun testQuizEngine_translation_strictlySeparatesJapaneseAndVietnameseDuringInput() {
        val staticTrans = com.example.data.repository.QuizExerciseRepository.getAllExercises()
            .filter { it.type == com.example.data.model.QuizExerciseType.TRANSLATION }
        assertTrue(staticTrans.any { it.isJpToVn })
        assertTrue(staticTrans.any { !it.isJpToVn })

        val generatedGrammarTrans = (1..4).flatMap {
            viewModel.buildExercisesForLesson(com.example.data.model.StudyCategory.GRAMMAR, it)
        }.filter { it.type == com.example.data.model.QuizExerciseType.TRANSLATION }

        val generatedVocabTrans = (1..3).flatMap {
            viewModel.buildExercisesForLesson(com.example.data.model.StudyCategory.VOCABULARY, it)
        }.filter { it.type == com.example.data.model.QuizExerciseType.TRANSLATION }

        val allTrans = staticTrans + generatedGrammarTrans + generatedVocabTrans
        assertTrue(allTrans.isNotEmpty())
        assertTrue(allTrans.any { it.isJpToVn })
        assertTrue(allTrans.any { !it.isJpToVn })

        allTrans.forEach { ex ->
            if (ex.isJpToVn) {
                assertTrue("${ex.id} JP->VN should have Japanese promptJp", ex.promptJp.isNotBlank())
                assertEquals("${ex.id} JP->VN must clear promptVn during input", "", ex.promptVn)
                assertEquals(ex.promptJp, ex.translationQuestion)
                assertTrue("${ex.id} JP->VN must keep Vietnamese answer for post-evaluation reveal", ex.completeSentenceVn.isNotBlank())
            } else {
                assertTrue("${ex.id} VN->JP should have Vietnamese promptVn", ex.promptVn.isNotBlank())
                assertEquals("${ex.id} VN->JP must clear promptJp during input", "", ex.promptJp)
                assertEquals(ex.promptVn, ex.translationQuestion)
                assertTrue("${ex.id} VN->JP must keep Japanese answer for post-evaluation reveal", ex.completeSentenceJp.isNotBlank())
            }
        }
    }
}

