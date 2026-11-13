package com.example.data.model

/**
 * 3 Mục Học Chính trong ứng dụng
 */
enum class StudyCategory {
    GRAMMAR,     // Mục 1: Ngữ pháp (文法) - Đã import trước tiên
    VOCABULARY,  // Mục 2: Từ vựng (語彙)
    KANJI        // Mục 3: Kanji (漢字)
}

/**
 * Ví dụ câu tiếng Nhật kèm dịch nghĩa
 */
data class GrammarExample(
    val jp: String,
    val vn: String
)

/**
 * Model Ngữ pháp chuẩn theo schema JSON import:
 * { "id", "title", "structure", "meaning", "examples": [{"jp", "vn"}] }
 */
data class GrammarPoint(
    val id: String,
    val title: String,
    val structure: String,
    val meaning: String,
    val examples: List<GrammarExample>,
    val level: String = "N3",
    val questionRefId: String? = null,
    val lessonId: Int = 1,
    val lessonTitle: String = "Bài 1"
)

/**
 * Model cho 1 bài học ngữ pháp JLPT N3 (Bài 1 -> Bài 27)
 */
data class GrammarLesson(
    val lessonId: Int,
    val title: String,
    val grammarList: List<GrammarPoint>
)

/**
 * Model Kanji (Hán tự) N3
 */
data class KanjiItem(
    val id: String,
    val kanji: String,
    val onyomi: String,
    val kunyomi: String,
    val hanViet: String,
    val meaning: String,
    val strokeCount: Int,
    val exampleCompound: String,
    val exampleCompoundMeaning: String,
    val level: String = "N3"
)

/**
 * Model cho 1 bài học Kanji N3
 */
data class KanjiLesson(
    val lessonId: Int,
    val title: String,
    val kanjiList: List<KanjiItem>
)

/**
 * Data Model for Japanese Vocabulary (語彙 & 漢字)
 */
data class VocabularyItem(
    val id: String,
    val kanji: String,
    val hiragana: String,
    val romaji: String = "",
    val meaning: String,
    val exampleSentence: String = "",
    val exampleMeaning: String = "",
    val level: String = "N3",
    val category: String = "語彙",
    val lessonId: Int = 1,
    val lessonTitle: String = "Bài 1"
)

/**
 * Data Model for a JLPT N3 Vocabulary Lesson (Bài 1 -> Bài 23)
 */
data class VocabLesson(
    val lessonId: Int,
    val title: String,
    val vocabList: List<VocabularyItem>
)

/**
 * Data Model for Card Battle PvE Sentence with Missing Word Slot
 */
data class CardBattlePvEQuestion(
    val id: String,
    val fullSentence: String,
    val sentenceWithBlank: String,
    val missingWord: String,
    val translation: String,
    val options: List<VocabularyItem>
)

/**
 * Cặp thẻ ghép đôi Tiếng Nhật <-> Tiếng Việt cho chế độ Ghép thẻ
 */
data class MatchPairItem(
    val id: String,
    val cardId: String,
    val itemType: SrsItemType,
    val japanese: String,
    val subJapanese: String = "",
    val vietnamese: String
)

/**
 * 4 dạng bài tập đa dạng cho Quiz Engine Học Quy Nạp
 */
enum class QuizExerciseType(val title: String, val badge: String) {
    MULTIPLE_CHOICE("Trắc nghiệm 3D", "MULTIPLE CHOICE"),
    FILL_IN_BLANK("Điền khuyết", "FILL-IN-THE-BLANK"),
    SENTENCE_BUILDER("Ghép câu 3D", "SENTENCE BUILDER"),
    TRANSLATION("Dịch 2 chiều", "TRANSLATION")
}

data class QuizExercise(
    val id: String,
    val type: QuizExerciseType,
    val grammarPoint: String,
    val instruction: String,
    val promptJp: String,
    val promptVn: String = "",

    // Liên kết thẻ SRS để lưu tiến trình học tập khi làm đúng
    val targetCardId: String = "",
    val targetItemType: SrsItemType = SrsItemType.GRAMMAR,

    // Cho Multiple Choice, Fill-in-the-blank & Translation
    val options: List<String> = emptyList(),
    val correctAnswerIndex: Int = 0,

    // Riêng cho Fill-in-the-blank
    val sentenceWithBlank: String = "",
    val blankMissingWord: String = "",

    // Riêng cho Sentence Builder (Ghép câu 3D)
    val builderTokens: List<String> = emptyList(),
    val correctTokenOrder: List<String> = emptyList(),
    val completeSentenceJp: String = "",
    val completeSentenceVn: String = "",

    // Riêng cho Translation (Dịch 2 chiều)
    val isJpToVn: Boolean = true,
    val translationQuestion: String = "",
    val translationOptions: List<String> = emptyList(),
    val correctTranslationIndex: Int = 0,

    // Quy luật & giải thích sư phạm
    val explanation: String = "",
    val mistakeExplanation: String = "",
    val ruleSummary: String = "",
    val similarExercise: QuizExercise? = null
)

/**
 * Data Model for JLPT N3 Grammar Multiple-Choice Question (文法) with Inductive Rules
 */
data class GrammarQuestion(
    val id: String,
    val grammarPoint: String,
    val question: String,
    val options: List<String>,
    val correctAnswerIndex: Int,
    val explanation: String,
    val mistakeExplanation: String = "",
    val ruleSummary: String = "",
    val similarQuestion: GrammarQuestion? = null
)

/**
 * Card for the Card Battle game mode
 */
enum class CardType {
    ATTACK,
    DEFENSE,
    HEAL,
    SPECIAL
}

data class BattleCard(
    val id: String,
    val title: String,
    val kanjiSymbol: String,
    val cardType: CardType,
    val power: Int,
    val cost: Int,
    val description: String,
    val questionRefId: String? = null
)

/**
 * Visual Novel Dialogue line
 */
data class DialogueLine(
    val id: String,
    val speaker: String,
    val japaneseText: String,
    val translation: String,
    val grammarQuestion: GrammarQuestion? = null
)
