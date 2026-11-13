package com.example.data.repository

import com.example.data.model.GrammarQuestion
import com.example.data.model.QuizExercise
import com.example.data.model.QuizExerciseType

/**
 * QuizExerciseRepository: Kho bài tập 4 dạng đa dạng hóa cho màn hình Học Quy Nạp
 * 1. Multiple Choice: Chọn đáp án đúng từ 4 lựa chọn 3D.
 * 2. Fill-in-the-blank: Điền từ/ngữ pháp khuyết vào câu.
 * 3. Sentence Builder (Ghép câu 3D): Xếp các thẻ từ 3D theo thứ tự câu tiếng Nhật hoàn chỉnh.
 * 4. Translation (Dịch 2 chiều): Dịch Nhật sang Việt & Việt sang Nhật.
 */
object QuizExerciseRepository {

    private val exercises: List<QuizExercise> = listOf(
        // ==========================================
        // 1. MULTIPLE CHOICE (Trắc nghiệm 3D)
        // ==========================================
        QuizExercise(
            id = "quiz_mc_01",
            type = QuizExerciseType.MULTIPLE_CHOICE,
            grammarPoint = "〜ことにしています (Thói quen tự quyết định)",
            instruction = "Chọn đáp án đúng nhất để hoàn thành thói quen bản thân tự duy trì:",
            promptJp = "健康のために、毎日野菜を＿＿＿ことにしています。",
            promptVn = "Vì sức khỏe, tôi duy trì thói quen ăn rau mỗi ngày.",
            options = listOf("食べた", "食べよう", "食べる", "食べて"),
            correctAnswerIndex = 2,
            explanation = "【〜ことにしています】Cấu trúc: V-る / V-ない + ことにしている. Biểu thị thói quen do chính bản thân tự quyết định và đang duy trì đều đặn.",
            mistakeExplanation = "Sai ngữ pháp: Cấu trúc này bắt buộc đi với động từ thể từ điển (V-る) hoặc phủ định (V-ない). Các dạng quá khứ (た), ý chí (よう) hay thể て không kết hợp với ことにしている.",
            ruleSummary = "QUY LUẬT QUY NẠP: V-る / V-ない + ことにしている\n→ Thói quen do chính bản thân chủ động quyết định và duy trì."
        ),

        // ==========================================
        // 2. FILL-IN-THE-BLANK (Điền từ khuyết)
        // ==========================================
        QuizExercise(
            id = "quiz_fib_01",
            type = QuizExerciseType.FILL_IN_BLANK,
            grammarPoint = "～どんなに…ても (Dù có... đến mức nào chăng nữa)",
            instruction = "Chọn từ/đuôi liên kết nhượng bộ thích hợp để điền vào chỗ khuyết:",
            promptJp = "どんなに道が険しく【 ＿＿＿ 】、私たちは目標に向かって進み続ける。",
            promptVn = "Dù cho con đường có hiểm trở thế nào, chúng tôi vẫn kiên định tiến bước.",
            sentenceWithBlank = "どんなに道が険しく【 ＿＿＿ 】、私たちは目標に向かって進み続ける。",
            blankMissingWord = "ても",
            options = listOf("たら", "ても", "なら", "ば"),
            correctAnswerIndex = 1,
            explanation = "【どんなに…ても】Đi với dạng Te-mo mang nghĩa nhượng bộ cao: 'Dù cho con đường có hiểm trở thế nào, chúng tôi vẫn kiên định tiến bước'.",
            mistakeExplanation = "Phó từ 'どんなに' (dù có thế nào) bắt buộc phải cặp đôi với đuôi nhượng bộ '～ても / ～でも'. Các dạng giả định たら, なら, ば không tương thích ngữ nghĩa.",
            ruleSummary = "QUY LUẬT QUY NẠP: どんなに + [Tính từ đuôi い → くても / Động từ → ても]\n→ Dù mức độ của vế trước có cao đến mấy, thực tế ở vế sau vẫn không lay chuyển."
        ),

        // ==========================================
        // 3. SENTENCE BUILDER (Ghép câu 3D)
        // ==========================================
        QuizExercise(
            id = "quiz_sb_01",
            type = QuizExerciseType.SENTENCE_BUILDER,
            grammarPoint = "～にしたがって (Theo như / Cùng với)",
            instruction = "Chạm vào các thẻ từ 3D theo thứ tự để xếp thành câu tiếng Nhật hoàn chỉnh:",
            promptJp = "",
            promptVn = "Cùng với việc dần quen cuộc sống ở Nhật, tiếng Nhật cũng nói tự nhiên hơn.",
            completeSentenceJp = "日本での生活に慣れるにしたがって、日本語も自然に話せるようになった。",
            completeSentenceVn = "Cùng với việc dần quen cuộc sống ở Nhật, tiếng Nhật cũng nói tự nhiên hơn.",
            builderTokens = listOf(
                "生活に",
                "日本での",
                "日本語も",
                "慣れるにしたがって、",
                "話せるようになった",
                "自然に"
            ),
            correctTokenOrder = listOf(
                "日本での",
                "生活に",
                "慣れるにしたがって、",
                "日本語も",
                "自然に",
                "話せるようになった"
            ),
            explanation = "【にしたがって】Diễn đạt quá trình biến đổi liên tục kéo theo kết quả: 'Cùng với việc dần quen cuộc sống ở Nhật, tiếng Nhật cũng nói tự nhiên hơn'.",
            mistakeExplanation = "Trật tự câu tiếng Nhật tiêu chuẩn: Thành phần bối cảnh (日本での生活に) + Vế biến đổi cùng ngữ pháp (慣れるにしたがって、) + Kết quả theo sau (日本語も自然に話せるようになった).",
            ruleSummary = "QUY LUẬT QUY NẠP: [V-る / N] + にしたがって\n→ Diễn tả sự biến đổi kéo theo theo thời gian: Cùng với tiến trình của vế trước thì vế sau cũng dần thay đổi."
        ),

        // ==========================================
        // 4. TRANSLATION (Dịch 2 chiều: Nhật -> Việt)
        // ==========================================
        QuizExercise(
            id = "quiz_trans_01",
            type = QuizExerciseType.TRANSLATION,
            grammarPoint = "～につれて (Càng... thì càng...)",
            instruction = "Tự nhớ lại nghĩa tiếng Việt của câu tiếng Nhật sau, rồi chọn bản dịch đúng nhất:",
            isJpToVn = true,
            promptJp = "台風が近づくにつれて、雨と風がどんどん強くなってきた。",
            promptVn = "",
            completeSentenceJp = "台風が近づくにつれて、雨と風がどんどん強くなってきた。",
            completeSentenceVn = "Khi bão càng tiến đến gần, mưa gió càng trở nên dữ dội.",
            translationQuestion = "台風が近づくにつれて、雨と風がどんどん強くなってきた。",
            translationOptions = listOf(
                "Vì cơn bão đã tan nên mưa gió bắt đầu tạnh dần.",
                "Khi bão càng tiến đến gần, mưa gió càng trở nên dữ dội.",
                "Chỉ riêng lúc có bão thì mưa gió mới bất ngờ xuất hiện.",
                "Đối với cơn bão, mưa gió không có ảnh hưởng gì lớn."
            ),
            options = listOf(
                "Vì cơn bão đã tan nên mưa gió bắt đầu tạnh dần.",
                "Khi bão càng tiến đến gần, mưa gió càng trở nên dữ dội.",
                "Chỉ riêng lúc có bão thì mưa gió mới bất ngờ xuất hiện.",
                "Đối với cơn bão, mưa gió không có ảnh hưởng gì lớn."
            ),
            correctAnswerIndex = 1,
            correctTranslationIndex = 1,
            explanation = "【につれて】Diễn tả hai sự việc cùng biến đổi tỉ lệ thuận với nhau: 'Khi bão càng tiến đến gần, mưa gió càng trở nên dữ dội'.",
            mistakeExplanation = "Cần phân biệt rõ: ～につれて biểu thị hai sự biến đổi song hành cùng chiều (càng A thì càng B). Các phương án khác biểu thị nguyên nhân đơn thuần hoặc sự việc trớ trêu, không đúng nghĩa tỉ lệ thuận.",
            ruleSummary = "QUY LUẬT QUY NẠP: [V-る / N] + につれて\n→ Diễn đạt sự biến đổi tỉ lệ thuận song hành tự nhiên giữa hai vế."
        ),

        // ==========================================
        // 5. FILL-IN-THE-BLANK (Điền từ khuyết)
        // ==========================================
        QuizExercise(
            id = "quiz_fib_02",
            type = QuizExerciseType.FILL_IN_BLANK,
            grammarPoint = "〜にかぎって (Chỉ riêng / Đúng vào lúc)",
            instruction = "Chọn từ biểu thị sự trớ trêu, không may vào chỗ trống:",
            promptJp = "傘を持っていない日【 ＿＿＿ 】、決まって雨が降るものだ。",
            promptVn = "Đúng vào cái ngày không mang ô thì y như rằng trời đổ mưa.",
            sentenceWithBlank = "傘を持っていない日【 ＿＿＿ 】、決まって雨が降るものだ。",
            blankMissingWord = "に限って",
            options = listOf("を通して", "について", "に限って", "を通じて"),
            correctAnswerIndex = 2,
            explanation = "【に限って (にかぎって)】Diễn tả sự trớ trêu, xui xẻo: 'Đúng vào cái ngày không mang ô thì y như rằng trời đổ mưa'.",
            mistakeExplanation = "Sai cấu trúc: 'を通して / を通じて' mang nghĩa thông qua hoặc suốt khoảng thời gian, còn 'について' là về vấn đề gì đó. Để nói về sự trớ trêu đúng lúc cần dùng に限って.",
            ruleSummary = "QUY LUẬT QUY NẠP: N + に限って\n→ Nhấn mạnh trường hợp ngoại lệ trớ trêu, không may: Đúng vào lúc đặc biệt đó thì sự việc bất như ý lại xảy ra."
        ),

        // ==========================================
        // 6. TRANSLATION (Dịch 2 chiều: Việt -> Nhật)
        // ==========================================
        QuizExercise(
            id = "quiz_trans_02",
            type = QuizExerciseType.TRANSLATION,
            grammarPoint = "〜わけにはいかない (Không thể vì trách nhiệm/lương tâm)",
            instruction = "Tự nhớ lại cách diễn đạt tiếng Nhật cho câu tiếng Việt sau, rồi chọn đáp án đúng:",
            isJpToVn = false,
            promptJp = "",
            promptVn = "Ngày mai có kỳ thi JLPT quan trọng nên đêm nay tôi không thể thức khuya được.",
            completeSentenceJp = "明日は大事なJLPT試験があるので、今夜夜更かしするわけにはいかない。",
            completeSentenceVn = "Ngày mai có kỳ thi JLPT quan trọng nên đêm nay tôi không thể thức khuya được.",
            translationQuestion = "Ngày mai có kỳ thi JLPT quan trọng nên đêm nay tôi không thể thức khuya được.",
            translationOptions = listOf(
                "明日は大事なJLPT試験があるので、今夜夜更かしするわけがない。",
                "明日は大事なJLPT試験があるので、今夜夜更かしするわけではない。",
                "明日は大事なJLPT試験があるので、今夜夜更かしするわけにはいかない。",
                "明日は大事なJLPT試験があるので、今夜夜更かしするはずがない。"
            ),
            options = listOf(
                "明日は大事なJLPT試験があるので、今夜夜更かしするわけがない。",
                "明日は大事なJLPT試験があるので、今夜夜更かしするわけではない。",
                "明日は大事なJLPT試験があるので、今夜夜更かしするわけにはいかない。",
                "明日は大事なJLPT試験があるので、今夜夜更かしするはずがない。"
            ),
            correctAnswerIndex = 2,
            correctTranslationIndex = 2,
            explanation = "【〜わけにはいかない】Biểu thị: vì lương tâm, trách nhiệm, đạo đức hoặc hoàn cảnh bắt buộc mà không thể làm một việc gì đó.",
            mistakeExplanation = "Sắc thái khác biệt: 'わけがない' là phán đoán logic (tuyệt đối không thể nào), 'わけではない' là phủ định một phần (không hẳn là), còn 'わけにはいかない' mới là không thể làm vì bổn phận và trách nhiệm của bản thân.",
            ruleSummary = "QUY LUẬT QUY NẠP: V-る + わけにはいかない\n→ Không thể làm một việc gì đó vì định kiến xã hội, trách nhiệm, lương tâm hoặc hoàn cảnh bắt buộc."
        ),

        // ==========================================
        // 7. SENTENCE BUILDER (Ghép câu 3D)
        // ==========================================
        QuizExercise(
            id = "quiz_sb_02",
            type = QuizExerciseType.SENTENCE_BUILDER,
            grammarPoint = "〜ことにしている (Thói quen tự quyết định)",
            instruction = "Xếp các khối từ 3D theo thứ tự câu tiếng Nhật tương ứng:",
            promptJp = "",
            promptVn = "Cho dù bận rộn đến mấy, tôi vẫn duy trì thói quen đọc sách trước khi ngủ.",
            completeSentenceJp = "どんなに忙しくても、寝る前に本を読むことにしている。",
            completeSentenceVn = "Cho dù bận rộn đến mấy, tôi vẫn duy trì thói quen đọc sách trước khi ngủ.",
            builderTokens = listOf(
                "寝る前に",
                "本を読む",
                "どんなに忙しくても、",
                "ことにしている"
            ),
            correctTokenOrder = listOf(
                "どんなに忙しくても、",
                "寝る前に",
                "本を読む",
                "ことにしている"
            ),
            explanation = "Cấu trúc: どんなに...ても (nhượng bộ) + 寝る前に (thời điểm) + V-る + ことにしている (thói quen tự duy trì).",
            mistakeExplanation = "Thứ tự câu: Vế nhượng bộ đứng đầu câu (どんなに忙しくても、) -> Bối cảnh thời gian (寝る前に) -> Hành động duy trì thói quen (本を読むことにしている).",
            ruleSummary = "QUY LUẬT QUY NẠP: どんなに...ても + V-る + ことにしている\n→ Dù điều kiện bên ngoài thế nào thì thói quen do bản thân chủ động đặt ra vẫn được thực hiện."
        ),

        // ==========================================
        // 8. MULTIPLE CHOICE (Trắc nghiệm 3D)
        // ==========================================
        QuizExercise(
            id = "quiz_mc_02",
            type = QuizExerciseType.MULTIPLE_CHOICE,
            grammarPoint = "〜から〜にかけて (Từ khoảng... đến khoảng...)",
            instruction = "Chọn từ biểu thị khoảng thời gian liên tục đại khái:",
            promptJp = "今夜から明日の朝【 ＿＿＿ 】、強い雨が降る恐れがあります。",
            promptVn = "Từ đêm nay đến khoảng sáng mai, có nguy cơ mưa to.",
            sentenceWithBlank = "今夜から明日の朝【 ＿＿＿ 】、強い雨が降る恐れがあります。",
            blankMissingWord = "にかけて",
            options = listOf("について", "にとって", "に対して", "にかけて"),
            correctAnswerIndex = 3,
            explanation = "【〜から〜にかけて】Biểu thị một khoảng thời gian hoặc không gian đại khái từ mốc A đến mốc B (ranh giới không rạch ròi).",
            mistakeExplanation = "Sai ngữ nghĩa: 'について' là về vấn đề, 'にとって' là đối với (lập trường), 'に対して' là đối với/hướng tới. Để chỉ khoảng đại khái thời tiết cần dùng にかけて.",
            ruleSummary = "QUY LUẬT QUY NẠP: N1 から N2 にかけて\n→ Từ khoảng thời gian/không gian A đến B (diễn biến kéo dài liên tục, không rõ ranh giới kết thúc)."
        )
    )

    private val blankRegex = Regex("【?\\s*＿+\\s*】?")
    private val splitMarkers = arrayOf("〜", "～", "。", "…", "...", "・", "？", "?", "(", ")", "（", "）")

    fun getAllExercises(): List<QuizExercise> = exercises.map { sanitizeExercise(it) }

    fun getExerciseByIndex(index: Int): QuizExercise {
        return sanitizeExercise(exercises[index.coerceIn(0, exercises.lastIndex)])
    }

    /**
     * Chuyển đổi QuizExercise sang GrammarQuestion để tương thích ngược hoàn hảo với ViewModel và Unit Test
     */
    fun toGrammarQuestion(exercise: QuizExercise): GrammarQuestion {
        val clean = sanitizeExercise(exercise)
        val questionText = when (clean.type) {
            QuizExerciseType.MULTIPLE_CHOICE -> clean.promptJp
            QuizExerciseType.FILL_IN_BLANK -> clean.sentenceWithBlank
            QuizExerciseType.SENTENCE_BUILDER -> clean.completeSentenceVn
            QuizExerciseType.TRANSLATION -> clean.translationQuestion.ifEmpty {
                if (clean.isJpToVn) clean.promptJp else clean.promptVn
            }
        }

        val optionsList = when (clean.type) {
            QuizExerciseType.MULTIPLE_CHOICE -> clean.options
            QuizExerciseType.FILL_IN_BLANK -> clean.options
            QuizExerciseType.SENTENCE_BUILDER -> clean.builderTokens
            QuizExerciseType.TRANSLATION -> clean.translationOptions.ifEmpty { clean.options }
        }

        val answerIdx = if (clean.type == QuizExerciseType.TRANSLATION && clean.translationOptions.isNotEmpty()) {
            clean.correctTranslationIndex
        } else {
            clean.correctAnswerIndex
        }

        return GrammarQuestion(
            id = clean.id,
            grammarPoint = clean.grammarPoint,
            question = questionText,
            options = optionsList,
            correctAnswerIndex = answerIdx,
            explanation = clean.explanation,
            mistakeExplanation = clean.mistakeExplanation,
            ruleSummary = clean.ruleSummary,
            similarQuestion = clean.similarExercise?.let { toGrammarQuestion(it) }
        )
    }

    /**
     * Đục lỗ phần ngữ pháp trong câu tiếng Nhật và ẩn toàn bộ phần trùng với đáp án đúng
     * để không bao giờ lộ đáp án trong câu hỏi (kể cả cấu trúc 2 vế như もし〜ても, どんなに〜ても).
     */
    fun blankOutGrammar(
        sentence: String,
        grammarTitle: String,
        correctOptionText: String = ""
    ): String {
        val blankMarker = "【 ＿＿＿ 】"
        var masked = sentence.replace(blankRegex, blankMarker)

        val template = GrammarExamplesData.findTemplate(grammarTitle)
        val candidateTokens = (
            grammarTitle.split(*splitMarkers) +
                correctOptionText.split(*splitMarkers)
            )
            .map { it.trim() }
            .filter { it.length >= 2 }
            .distinct()
            .sortedByDescending { it.length }

        // 1. Luôn ưu tiên ẩn targetSegment trước tiên
        if (template != null && template.targetSegment.isNotBlank() && masked.contains(template.targetSegment)) {
            masked = masked.replace(template.targetSegment, blankMarker)
        }

        // 2. Ẩn tất cả các mảnh (>= 2 ký tự) của đáp án đúng / tên mẫu ngữ pháp còn sót lại trong câu
        for (part in candidateTokens) {
            if (masked.contains(part)) {
                masked = masked.replace(part, blankMarker)
            }
            val politeVariants = buildList {
                if (part.endsWith("だ")) add(part.dropLast(1) + "です")
                if (part.endsWith("る")) add(part.dropLast(1) + "ます")
                if (part.endsWith("ない")) add(part.dropLast(2) + "ません")
                if (part.length >= 3) add(part.dropLast(1))
            }
            for (variant in politeVariants) {
                if (variant.length >= 2 && masked.contains(variant)) {
                    masked = masked.replace(variant, blankMarker)
                }
            }
        }

        // 3. Nếu đã có ô khuyết (đã mask thành công), gom khoảng trắng thừa quanh placeholder và trả về
        if (masked.contains("＿＿＿")) {
            return masked.replace(Regex("(?:【 ＿＿＿ 】\\s*){2,}"), blankMarker)
        }

        // 4. Fallback cho mẫu 1 ký tự (như 〜さ) nếu vẫn còn sót
        val singleCharCandidates = (grammarTitle.split(*splitMarkers) + correctOptionText.split(*splitMarkers))
            .map { it.trim() }
            .filter { it.length == 1 }
        for (ch in singleCharCandidates) {
            if (masked.contains(ch)) {
                return masked.replaceFirst(ch, blankMarker)
            }
        }

        // 5. Fallback an toàn cuối cùng
        val strippedEnd = sentence.trimEnd('。', '？', '?', '！', '!')
        return if (strippedEnd.length > 6) {
            "${strippedEnd.dropLast(4)}$blankMarker。"
        } else {
            "$sentence $blankMarker"
        }
    }

    /**
     * Đảm bảo câu điền khuyết / trắc nghiệm luôn ẩn hoàn toàn đáp án đúng từ danh sách lựa chọn.
     */
    fun maskedBlankSentence(exercise: QuizExercise): String {
        val blankMarker = "【 ＿＿＿ 】"
        var text = exercise.sentenceWithBlank.ifBlank { exercise.promptJp }
            .replace(blankRegex, blankMarker)

        val correctOpt = exercise.options.getOrNull(exercise.correctAnswerIndex).orEmpty()
        val leaks = buildList {
            if (exercise.blankMissingWord.length >= 2) add(exercise.blankMissingWord)
            if (correctOpt.isNotBlank()) {
                val cleanOpt = correctOpt.replace(Regex("[〜～…・。?？()（）]"), " ").trim()
                cleanOpt.split(Regex("\\s+")).filter { it.length >= 2 }.forEach { add(it) }
                if (correctOpt.length >= 2 && !correctOpt.contains("〜") && !correctOpt.contains("～")) {
                    add(correctOpt)
                }
            }
        }.distinct().sortedByDescending { it.length }

        for (leak in leaks) {
            // Chỉ ẩn nếu leak thực sự có ký tự tiếng Nhật (tránh cắt nhầm chữ tiếng Việt)
            val hasJapanese = leak.any { it in '\u3040'..'\u30FF' || it in '\u4E00'..'\u9FFF' }
            if (hasJapanese && text.contains(leak)) {
                text = text.replace(leak, blankMarker)
            }
        }

        if (!text.contains("＿＿＿") && exercise.blankMissingWord.isNotBlank() && text.contains(exercise.blankMissingWord)) {
            text = text.replaceFirst(exercise.blankMissingWord, blankMarker)
        }
        return text
    }

    /**
     * Xáo trộn các thẻ từ sao cho thứ tự ban đầu không bao giờ trùng khớp với đáp án đúng.
     */
    fun scrambleTokensNonMatching(
        tokens: List<String>,
        correctOrder: List<String> = tokens
    ): List<String> {
        if (tokens.size <= 1 || tokens.toSet().size <= 1) return tokens
        var candidate = tokens.shuffled()
        var attempts = 0
        while (candidate == correctOrder && attempts < 12) {
            candidate = tokens.shuffled()
            attempts++
        }
        if (candidate == correctOrder) {
            candidate = tokens.drop(1) + tokens.first()
        }
        return candidate
    }

    /**
     * Chuẩn hóa QuizExercise để triệt tiêu rò rỉ đáp án ở tất cả 4 chế độ trước khi đưa lên UI/State.
     */
    fun sanitizeExercise(exercise: QuizExercise): QuizExercise {
        return when (exercise.type) {
            QuizExerciseType.FILL_IN_BLANK -> {
                val masked = maskedBlankSentence(exercise)
                exercise.copy(
                    promptJp = masked,
                    sentenceWithBlank = masked
                )
            }
            QuizExerciseType.MULTIPLE_CHOICE -> {
                val masked = if (exercise.sentenceWithBlank.isNotBlank() || exercise.promptJp.contains("＿＿＿")) {
                    maskedBlankSentence(exercise)
                } else {
                    exercise.promptJp
                }
                exercise.copy(promptJp = masked)
            }
            QuizExerciseType.SENTENCE_BUILDER -> {
                val scrambled = if (exercise.builderTokens.isNotEmpty() && exercise.builderTokens != exercise.correctTokenOrder) {
                    exercise.builderTokens
                } else {
                    scrambleTokensNonMatching(exercise.correctTokenOrder.ifEmpty { exercise.builderTokens }, exercise.correctTokenOrder)
                }
                val targetVn = exercise.completeSentenceVn.ifBlank { exercise.promptVn }
                exercise.copy(
                    promptJp = "",
                    sentenceWithBlank = "",
                    blankMissingWord = "",
                    promptVn = targetVn,
                    completeSentenceVn = targetVn,
                    builderTokens = scrambled
                )
            }
            QuizExerciseType.TRANSLATION -> {
                val opts = exercise.translationOptions.ifEmpty { exercise.options }
                val correctIdx = exercise.correctTranslationIndex.coerceIn(0, (opts.size - 1).coerceAtLeast(0))
                if (exercise.isJpToVn) {
                    val jpPrompt = exercise.promptJp.ifBlank { exercise.translationQuestion }
                    val vnReveal = exercise.completeSentenceVn.ifBlank { opts.getOrNull(correctIdx).orEmpty() }
                    exercise.copy(
                        promptJp = jpPrompt,
                        promptVn = "",
                        translationQuestion = jpPrompt,
                        translationOptions = opts,
                        options = opts,
                        correctAnswerIndex = correctIdx,
                        correctTranslationIndex = correctIdx,
                        completeSentenceJp = exercise.completeSentenceJp.ifBlank { jpPrompt },
                        completeSentenceVn = vnReveal
                    )
                } else {
                    val vnPrompt = exercise.promptVn.ifBlank { exercise.translationQuestion }
                    val jpReveal = exercise.completeSentenceJp.ifBlank { opts.getOrNull(correctIdx).orEmpty() }
                    exercise.copy(
                        promptJp = "",
                        promptVn = vnPrompt,
                        translationQuestion = vnPrompt,
                        translationOptions = opts,
                        options = opts,
                        correctAnswerIndex = correctIdx,
                        correctTranslationIndex = correctIdx,
                        completeSentenceJp = jpReveal,
                        completeSentenceVn = exercise.completeSentenceVn.ifBlank { vnPrompt }
                    )
                }
            }
        }
    }

    /**
     * Tách câu tiếng Nhật thành 4-6 cụm từ tự nhiên cho bài tập Sentence Builder
     */
    fun segmentJapaneseSentence(sentence: String): List<String> {
        val clean = sentence.trim()
        if (clean.isEmpty()) return emptyList()

        // Nếu câu đã có dấu cách rõ ràng và >= 3 cụm thì dùng luôn
        val spaceSplit = clean.split(" ").filter { it.isNotBlank() }
        if (spaceSplit.size >= 3) return spaceSplit

        // Tách theo dấu câu và các trợ từ chính giữ lại dấu/trợ từ ở cuối mỗi cụm
        val regex = Regex("""[^、。]+?[、。]|[^、。]+?(?:は|が|を|に|で|と|も|へ|から|まで|ので|のに|けど|けれど)""")
        val chunks = mutableListOf<String>()
        var lastEnd = 0
        for (match in regex.findAll(clean)) {
            if (match.range.first > lastEnd) {
                val gap = clean.substring(lastEnd, match.range.first)
                if (gap.isNotBlank()) chunks.add(gap)
            }
            if (match.value.isNotBlank()) chunks.add(match.value)
            lastEnd = match.range.last + 1
        }
        if (lastEnd < clean.length) {
            val tail = clean.substring(lastEnd)
            if (tail.isNotBlank()) chunks.add(tail)
        }

        // Gộp các cụm quá ngắn (<= 2 ký tự) vào cụm kề để tránh quá vụn
        val merged = mutableListOf<String>()
        for (c in chunks) {
            if (merged.isNotEmpty() && c.length <= 2) {
                merged[merged.lastIndex] = merged.last() + c
            } else {
                merged.add(c)
            }
        }

        if (merged.size >= 3) return merged

        // Nếu vẫn ít hơn 3 cụm, chia đều theo độ dài
        val step = (clean.length / 4).coerceAtLeast(3)
        return clean.chunked(step).filter { it.isNotBlank() }
    }

    /**
     * Tạo bài tập cho một điểm ngữ pháp bất kỳ từ kho 134 cấu trúc đã import
     */
    fun createExerciseForGrammarPoint(
        grammarTitle: String,
        structure: String,
        meaning: String,
        exampleJp: String,
        exampleVn: String,
        cardId: String = "",
        distractors: List<String> = emptyList()
    ): QuizExercise {
        val missingTarget = GrammarExamplesData.findTemplate(grammarTitle)?.targetSegment
            ?: grammarTitle.replace("〜", "").replace("。", "").trim()
        val blankQuestion = blankOutGrammar(exampleJp, grammarTitle, grammarTitle)

        val fallbackDistractors = listOf("〜わけがない", "〜に反して", "〜に基づいて", "〜にしたがって", "〜せいで", "〜おかげで")
            .filter { candidate ->
                candidate != grammarTitle &&
                    candidate != structure &&
                    (missingTarget.length < 2 || GrammarExamplesData.findTemplate(candidate)?.targetSegment != missingTarget)
            }
        val pool = (
            distractors.filter { candidate ->
                candidate != grammarTitle &&
                    candidate.isNotBlank() &&
                    (missingTarget.length < 2 || GrammarExamplesData.findTemplate(candidate)?.targetSegment != missingTarget)
            } + fallbackDistractors
            )
            .distinct()
            .take(3)
        val shuffledOpts = (listOf(grammarTitle) + pool).shuffled()
        val correctIdx = shuffledOpts.indexOf(grammarTitle).coerceAtLeast(0)

        return sanitizeExercise(
            QuizExercise(
                id = "custom_${cardId.ifEmpty { System.currentTimeMillis().toString() }}",
                type = QuizExerciseType.FILL_IN_BLANK,
                grammarPoint = grammarTitle,
                instruction = "Chọn cấu trúc ngữ pháp thích hợp để điền vào chỗ trống:",
                promptJp = blankQuestion,
                promptVn = exampleVn,
                targetCardId = cardId,
                targetItemType = com.example.data.model.SrsItemType.GRAMMAR,
                sentenceWithBlank = blankQuestion,
                blankMissingWord = missingTarget,
                options = shuffledOpts,
                correctAnswerIndex = correctIdx,
                completeSentenceJp = exampleJp,
                completeSentenceVn = exampleVn,
                explanation = "【$grammarTitle】$meaning\nCấu trúc: $structure",
                mistakeExplanation = "Cần nắm vững cấu trúc $structure để diễn đạt ý nghĩa '$meaning'.",
                ruleSummary = "QUY LUẬT QUY NẠP: $structure → $meaning"
            )
        )
    }
}
