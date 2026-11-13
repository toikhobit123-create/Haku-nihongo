package com.example.ui.state

import com.example.data.model.BattleCard
import com.example.data.model.CardBattlePvEQuestion
import com.example.data.model.GrammarLesson
import com.example.data.model.GrammarPoint
import com.example.data.model.GrammarQuestion
import com.example.data.model.KanjiItem
import com.example.data.model.KanjiLesson
import com.example.data.model.MatchPairItem
import com.example.data.model.QuizExercise
import com.example.data.model.SrsCardItem
import com.example.data.model.SrsItemType
import com.example.data.model.SrsProgress
import com.example.data.model.SrsStatsSummary
import com.example.data.model.StudyCategory
import com.example.data.model.VocabLesson
import com.example.data.model.VocabularyItem
import com.example.data.repository.MockDataRepository
import com.example.data.repository.QuizExerciseRepository
import com.example.data.repository.SrsRepository

enum class Screen {
    HOME,           // Tab 1: Lộ trình (Study Set Grid View)
    LESSON_DETAIL,  // Chi tiết bài học chuẩn Quizlet (Study Modes + Preview List)
    SRS_REVIEW,     // Tab 2: Ôn tập SRS (Spaced Repetition SM-2)
    PROFILE_STATS,  // Tab 3: Tiến độ (Thống kê kết quả & Điểm yếu)
    STUDY_HUB,      // Chi tiết 3 mục học: Ngữ pháp, Từ vựng, Kanji
    INDUCTIVE_LEARNING, // Luyện tập Trắc nghiệm Quy nạp
    CARD_BATTLE,    // Đấu trường thẻ bài & Ghép thẻ Nhật <-> Việt
    AI_CHAT         // Trò chuyện cùng Sensei Akira & Gemini AI
}

data class NihongoUiState(
    val currentScreen: Screen = Screen.HOME,
    val previousScreen: Screen = Screen.HOME,

    // Bài học đang chọn (Lesson Detail Screen)
    val selectedLessonId: Int = 1,
    val selectedLessonTitle: String = "Bài 1",
    val isTestMode: Boolean = false,
    val testTimeRemainingSeconds: Int = 180,
    val isLessonExerciseSession: Boolean = false,

    // Điểm số & Máu (HP) của User và Boss
    val score: Int = 0,
    val userHp: Int = 100,
    val maxUserHp: Int = 100,
    val bossHp: Int = 150,
    val maxBossHp: Int = 150,
    val bossName: String = "Boss N3 鬼 (Oni N3)",
    val turnCount: Int = 1,

    // 3 Mục Học (Study Categories): Ngữ pháp, Từ vựng, Kanji
    val selectedStudyCategory: StudyCategory = StudyCategory.GRAMMAR,
    val grammarPoints: List<GrammarPoint> = MockDataRepository.n3GrammarPoints,
    val grammarLessons: List<GrammarLesson> = MockDataRepository.n3GrammarLessons,
    val selectedGrammarLessonId: Int? = null,
    val selectedGrammarPointId: String = "np_01",
    val kanjiList: List<KanjiItem> = MockDataRepository.n3KanjiList,
    val kanjiLessons: List<KanjiLesson> = MockDataRepository.n3KanjiLessons,
    val selectedKanjiId: String? = null,
    val grammarSearchQuery: String = "",

    // Mock Data
    val vocabularyList: List<VocabularyItem> = MockDataRepository.n3VocabularyList,
    val grammarQuestions: List<GrammarQuestion> = MockDataRepository.n3GrammarQuestions,
    val battleCards: List<BattleCard> = MockDataRepository.initialBattleCards,
    val pveBattleQuestions: List<CardBattlePvEQuestion> = MockDataRepository.pveBattleQuestions,

    // Spaced Repetition System (SRS SM-2) State
    val srsDueCards: List<Pair<SrsCardItem, SrsProgress>> = emptyList(),
    val currentSrsIndex: Int = 0,
    val isSrsCardFlipped: Boolean = false,
    val selectedSrsFilter: SrsItemType? = null,
    val srsStats: SrsStatsSummary = SrsRepository.getStatsSummary(),
    val reviewedCountToday: Int = 0,

    // Phiên học ngắn (5-10 phút, 10 - 15 câu)
    val sessionTotalCount: Int = 12,
    val sessionCompletedCount: Int = 0,
    val sessionCorrectCount: Int = 0,
    val isSessionFinished: Boolean = false,
    val isWeakPointsSession: Boolean = false,

    // Vocabulary State (23 Lessons, 1138 words)
    val vocabLessons: List<VocabLesson> = MockDataRepository.n3Lessons,
    val selectedVocabLessonId: Int? = null,
    val vocabSearchQuery: String = "",
    val currentVocabIndex: Int = 0,

    // Inductive Learning Quiz Engine (4 dạng bài tập đa dạng)
    val currentGrammarIndex: Int = 0,
    val activeQuestion: GrammarQuestion = MockDataRepository.n3GrammarQuestions[0],
    val activeExercise: QuizExercise = QuizExerciseRepository.getExerciseByIndex(0),
    val exerciseList: List<QuizExercise> = QuizExerciseRepository.getAllExercises(),
    val currentExerciseIndex: Int = 0,
    val selectedOptionIndex: Int? = null,
    val sentenceBuilderSelectedChips: List<String> = emptyList(),
    val sentenceBuilderAvailableChips: List<String> = QuizExerciseRepository.getExerciseByIndex(0).builderTokens,
    val isAnswerEvaluated: Boolean = false,
    val isAnswerCorrect: Boolean = false,
    val isShowingSimilarQuestion: Boolean = false,
    val aiMistakeExplanation: String? = null,
    val isAiMistakeLoading: Boolean = false,
    val userSubmittedAnswerSummary: String = "",

    // Card Battle (PvE) & Ghép thẻ Nhật <-> Việt State
    val currentPveQuestionIndex: Int = 0,
    val activePveQuestion: CardBattlePvEQuestion = MockDataRepository.pveBattleQuestions[0],
    val filledBlankWord: String? = null,
    val isScreenFlashing: Boolean = false,
    val isUserHit: Boolean = false,
    val pveCombatFeedback: String = "Chọn 1 thẻ Tiếng Nhật rồi nối với nghĩa Tiếng Việt đúng!",
    val pveDamageDealt: Int? = null,
    val matchRounds: List<List<MatchPairItem>> = emptyList(),
    val currentMatchRoundIndex: Int = 0,
    val leftJapaneseCards: List<MatchPairItem> = emptyList(),
    val rightVietnameseCards: List<MatchPairItem> = emptyList(),
    val selectedMatchJapaneseId: String? = null,
    val selectedMatchVietnameseId: String? = null,
    val matchedPairIds: Set<String> = emptySet(),
    val matchErrorPairIds: Pair<String, String>? = null,
    val matchCombo: Int = 0,
    val matchCategory: StudyCategory = StudyCategory.GRAMMAR,
    val matchLessonId: Int = 1,

    // Combat Results
    val battleCombatLog: List<String> = listOf("Trận chiến bắt đầu! Boss N3 鬼 xuất hiện với 150 HP."),
    val isVictory: Boolean = false,
    val isDefeat: Boolean = false,
    val lastActionFeedback: String = "Hệ thống sẵn sàng. Chọn một chế độ để bắt đầu."
)
