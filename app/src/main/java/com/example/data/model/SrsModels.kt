package com.example.data.model

/**
 * Phân loại thẻ trong hệ thống SRS (Spaced Repetition System)
 */
enum class SrsItemType(val title: String) {
    VOCABULARY("Từ vựng"),
    GRAMMAR("Ngữ pháp"),
    KANJI("Kanji")
}

/**
 * 4 Nút đánh giá của thuật toán SM-2 (SuperMemo-2)
 */
enum class SrsRating(val label: String, val quality: Int) {
    AGAIN("Quên", 1),
    HARD("Khó", 2),
    GOOD("Tốt", 4),
    EASY("Dễ", 5)
}

/**
 * Giai đoạn ghi nhớ của thẻ
 */
enum class SrsStage(val label: String) {
    NEW("Thẻ mới"),
    LEARNING("Đang học"),
    REVIEW("Đang ôn tập"),
    MASTERED("Thuần thục")
}

/**
 * Model thẻ bài ôn tập hiển thị trong UI
 */
data class SrsCardItem(
    val id: String,
    val type: SrsItemType,
    val frontText: String,             // Chữ Hán / Tên Ngữ pháp / Kanji
    val readingText: String,           // Hiragana / On-Kun
    val meaningText: String,           // Nghĩa tiếng Việt
    val structureOrRomaji: String = "",
    val exampleJp: String = "",
    val exampleVn: String = "",
    val level: String = "N3",
    val lessonId: Int = 1,
    val lessonTitle: String = "Bài 1"
)

/**
 * Tiến độ ghi nhớ của thẻ theo thuật toán SM-2 lưu trữ offline
 */
data class SrsProgress(
    val id: String,
    val type: SrsItemType,
    val repetitions: Int = 0,
    val intervalDays: Int = 0,
    val easeFactor: Double = 2.5,
    val lastReviewedTimestamp: Long = 0L,
    val nextReviewTimestamp: Long = 0L,
    val lapses: Int = 0,               // Số lần bấm "Quên"
    val totalReviews: Int = 0,
    val stage: SrsStage = SrsStage.NEW
)

/**
 * Điểm dữ liệu biểu đồ tiến độ theo thời gian (Mastery Over Time)
 */
data class MasteryDataPoint(
    val dayLabel: String,
    val grammarMastered: Int,
    val kanjiMastered: Int,
    val vocabMastered: Int
)

/**
 * Thống kê tổng hợp tiến độ và điểm yếu SRS
 */
data class SrsStatsSummary(
    val totalCards: Int,
    val dueCount: Int,
    val newCount: Int,
    val learningCount: Int,
    val reviewCount: Int,
    val masteredCount: Int,
    val retentionRatePercent: Int,
    val topLapsedCards: List<Pair<SrsCardItem, SrsProgress>>,
    val masteredGrammar: Int = 0,
    val masteredVocab: Int = 0,
    val masteredKanji: Int = 0,
    val totalGrammar: Int = 134,
    val totalVocab: Int = 1095,
    val totalKanji: Int = 8,
    val streakDays: Int = 0,
    val accuracyRatePercent: Int = 0,
    val masteryOverTime: List<MasteryDataPoint> = emptyList()
)
