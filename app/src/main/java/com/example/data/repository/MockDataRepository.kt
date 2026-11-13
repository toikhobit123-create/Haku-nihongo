package com.example.data.repository

import com.example.data.model.BattleCard
import com.example.data.model.CardBattlePvEQuestion
import com.example.data.model.CardType
import com.example.data.model.DialogueLine
import com.example.data.model.GrammarExample
import com.example.data.model.GrammarLesson
import com.example.data.model.GrammarPoint
import com.example.data.model.GrammarQuestion
import com.example.data.model.KanjiItem
import com.example.data.model.KanjiLesson
import com.example.data.model.VocabLesson
import com.example.data.model.VocabularyItem

object MockDataRepository {

    /**
     * MỤC 1: NGỮ PHÁP (文法) - IMPORTED TỪ JSON (27 BÀI HỌC, 134 ĐIỂM NGỮ PHÁP N3)
     */
    val fallbackGrammarPoints: List<GrammarPoint> = listOf(
        GrammarPoint(
            id = "np_01",
            title = "〜ことにしています",
            structure = "V-ru / V-nai + ことにしている",
            meaning = "Biểu thị thói quen do chính bản thân tự quyết định và đang duy trì.",
            examples = listOf(
                GrammarExample(
                    jp = "健康のために、毎日野菜を食べることにしています。",
                    vn = "Vì sức khỏe, tôi quyết định duy trì thói quen ăn rau mỗi ngày."
                ),
                GrammarExample(
                    jp = "どんなに忙しくても、寝る前に日本語を15分勉強することにしています。",
                    vn = "Dù bận đến đâu, tôi cũng duy trì thói quen học tiếng Nhật 15 phút trước khi đi ngủ."
                )
            ),
            level = "N3",
            questionRefId = "grammar_0_koto"
        ),
        GrammarPoint(
            id = "np_02",
            title = "〜につれて",
            structure = "V-る / N + につれて",
            meaning = "Càng A thì càng B. Biểu đạt sự biến đổi tự nhiên đồng thời của hai vế.",
            examples = listOf(
                GrammarExample(
                    jp = "台風が近づくにつれて、雨と風がどんどん強くなってきた。",
                    vn = "Khi bão càng tiến đến gần, mưa gió càng trở nên dữ dội."
                ),
                GrammarExample(
                    jp = "年を取るにつれて、健康の大切さがよく分かります。",
                    vn = "Càng có tuổi, người ta càng hiểu rõ tầm quan trọng của sức khỏe."
                )
            ),
            level = "N3",
            questionRefId = "grammar_1"
        ),
        GrammarPoint(
            id = "np_03",
            title = "〜どんなに…ても",
            structure = "どんなに + [V-ても / A-くても / Na-でも]",
            meaning = "Dù cho... đến mức nào chăng nữa thì ý chí hoặc kết quả ở vế sau vẫn không đổi.",
            examples = listOf(
                GrammarExample(
                    jp = "どんなに道が険しくても、私たちは目標に向かって進み続ける。",
                    vn = "Dù con đường có hiểm trở thế nào, chúng tôi vẫn kiên định tiến bước."
                )
            ),
            level = "N3",
            questionRefId = "grammar_2"
        ),
        GrammarPoint(
            id = "np_04",
            title = "〜に限って",
            structure = "N + に限って",
            meaning = "Đúng vào lúc... (thì sự việc trớ trêu, xui xẻo xảy ra) / Chỉ riêng ai đó.",
            examples = listOf(
                GrammarExample(
                    jp = "傘を持っていない日に限って、決まって雨が降るものだ。",
                    vn = "Đúng vào ngày không mang ô thì y như rằng trời lại đổ mưa."
                )
            ),
            level = "N3",
            questionRefId = "grammar_3"
        ),
        GrammarPoint(
            id = "np_05",
            title = "〜にしたがって",
            structure = "V-る / N + にしたがって",
            meaning = "Cùng với quá trình biến đổi của A thì B cũng phát triển theo.",
            examples = listOf(
                GrammarExample(
                    jp = "日本での生活に慣れるにしたがって、日本語も自然に話せるようになった。",
                    vn = "Cùng với việc dần quen cuộc sống ở Nhật, tiếng Nhật cũng nói tự nhiên hơn."
                )
            ),
            level = "N3",
            questionRefId = "grammar_4"
        )
    )

    val n3GrammarPoints: List<GrammarPoint>
        get() {
            val loaded = GrammarDataLoader.getAllGrammar()
            return if (loaded.isNotEmpty()) loaded else fallbackGrammarPoints
        }

    val n3GrammarLessons: List<GrammarLesson>
        get() = GrammarDataLoader.getLessons()

    /**
     * MỤC 3: KANJI (漢字) - KANJI N3 TIÊU BIỂU
     */
    val n3KanjiList: List<KanjiItem> = listOf(
        KanjiItem(
            id = "kanji_1",
            kanji = "健",
            onyomi = "ケン (KEN)",
            kunyomi = "すこ・やか (suko-yaka)",
            hanViet = "KIỆN",
            meaning = "Khỏe mạnh, tráng kiện",
            strokeCount = 11,
            exampleCompound = "健康 (けんこう)",
            exampleCompoundMeaning = "Sức khỏe",
            level = "N3"
        ),
        KanjiItem(
            id = "kanji_2",
            kanji = "康",
            onyomi = "コウ (KOU)",
            kunyomi = "-",
            hanViet = "KHANG",
            meaning = "Yên vui, an khang",
            strokeCount = 11,
            exampleCompound = "健康 (けんこう)",
            exampleCompoundMeaning = "Sức khỏe, tráng kiện",
            level = "N3"
        ),
        KanjiItem(
            id = "kanji_3",
            kanji = "節",
            onyomi = "セツ (SETSU)",
            kunyomi = "ふし (fushi)",
            hanViet = "TIẾT",
            meaning = "Tiết kiệm, mùa, khớp xương",
            strokeCount = 13,
            exampleCompound = "節約 (せつやく)",
            exampleCompoundMeaning = "Tiết kiệm",
            level = "N3"
        ),
        KanjiItem(
            id = "kanji_4",
            kanji = "約",
            onyomi = "ヤク (YAKU)",
            kunyomi = "-",
            hanViet = "ƯỚC",
            meaning = "Ước lượng, lời hứa, ràng buộc",
            strokeCount = 9,
            exampleCompound = "予約 (よやく)",
            exampleCompoundMeaning = "Đặt trước, hẹn trước",
            level = "N3"
        ),
        KanjiItem(
            id = "kanji_5",
            kanji = "慮",
            onyomi = "リョ (RYO)",
            kunyomi = "おもんぱか・る (omonpaka-ru)",
            hanViet = "LỰ",
            meaning = "Suy nghĩ, cân nhắc, lo âu",
            strokeCount = 15,
            exampleCompound = "遠慮 (えんりょ)",
            exampleCompoundMeaning = "Khách khí, ngại ngùng",
            level = "N3"
        ),
        KanjiItem(
            id = "kanji_6",
            kanji = "然",
            onyomi = "ゼン (ZEN), ネン (NEN)",
            kunyomi = "しか・し (shika-shi)",
            hanViet = "NHIÊN",
            meaning = "Tự nhiên, quả nhiên, như thế",
            strokeCount = 12,
            exampleCompound = "偶然 (ぐうぜん)",
            exampleCompoundMeaning = "Tình cờ, ngẫu nhiên",
            level = "N3"
        ),
        KanjiItem(
            id = "kanji_7",
            kanji = "複",
            onyomi = "フク (FUKU)",
            kunyomi = "-",
            hanViet = "PHỨC",
            meaning = "Nhiều lớp, lặp lại, phức tạp",
            strokeCount = 14,
            exampleCompound = "複雑 (ふくざつ)",
            exampleCompoundMeaning = "Phức tạp, rắc rối",
            level = "N3"
        ),
        KanjiItem(
            id = "kanji_8",
            kanji = "雑",
            onyomi = "ザツ (ZATSU), ゾウ (ZOU)",
            kunyomi = "まじ・る (maji-ru)",
            hanViet = "TẠP",
            meaning = "Pha tạp, hỗn hợp, linh tinh",
            strokeCount = 14,
            exampleCompound = "雑誌 (ざっし)",
            exampleCompoundMeaning = "Tạp chí",
            level = "N3"
        )
    )

    val n3KanjiLessons: List<KanjiLesson> = listOf(
        KanjiLesson(
            lessonId = 1,
            title = "Bài 1: Sức khỏe & Thói quen",
            kanjiList = n3KanjiList.take(4)
        ),
        KanjiLesson(
            lessonId = 2,
            title = "Bài 2: Tư duy & Xã hội",
            kanjiList = n3KanjiList.drop(4)
        )
    )

    /**
     * Danh sách từ vựng gốc phục vụ Card Battle
     */
    val initialCombatVocabList: List<VocabularyItem> = listOf(
        VocabularyItem(
            id = "combat_vocab_1",
            kanji = "遠慮",
            hiragana = "えんりょ",
            romaji = "enryo",
            meaning = "Ngại ngùng, khách khí, dè dặt",
            exampleSentence = "遠慮しないで、何でも質問してください。",
            exampleMeaning = "Đừng ngại ngùng, hãy hỏi bất cứ câu gì bạn muốn.",
            level = "N3",
            category = "語彙・漢字"
        ),
        VocabularyItem(
            id = "combat_vocab_2",
            kanji = "偶然",
            hiragana = "ぐうぜん",
            romaji = "guuzen",
            meaning = "Tình cờ, ngẫu nhiên",
            exampleSentence = "駅のホームで偶然、高校の同級生に会いました。",
            exampleMeaning = "Tôi tình cờ gặp lại bạn cùng lớp cấp ba ở sân ga.",
            level = "N3",
            category = "語彙・漢字"
        ),
        VocabularyItem(
            id = "combat_vocab_3",
            kanji = "複雑",
            hiragana = "ふくざつ",
            romaji = "fukuzatsu",
            meaning = "Phức tạp, rắc rối",
            exampleSentence = "このシステムの構造は思ったよりも複雑です。",
            exampleMeaning = "Cấu trúc của hệ thống này phức tạp hơn tôi tưởng tượng.",
            level = "N3",
            category = "語彙・漢字"
        ),
        VocabularyItem(
            id = "combat_vocab_4",
            kanji = "普段",
            hiragana = "ふだん",
            romaji = "fudan",
            meaning = "Bình thường, thường ngày",
            exampleSentence = "普段は自炊していますが、忙しい時は外食します。",
            exampleMeaning = "Thường ngày tôi tự nấu ăn, nhưng lúc bận thì ăn ngoài.",
            level = "N3",
            category = "語彙・漢字"
        ),
        VocabularyItem(
            id = "combat_vocab_5",
            kanji = "節約",
            hiragana = "せつやく",
            romaji = "setsuyaku",
            meaning = "Tiết kiệm (chi tiêu, thời gian, năng lượng)",
            exampleSentence = "電気代を節約するために、こまめにスイッチを切る。",
            exampleMeaning = "Để tiết kiệm tiền điện, hãy thường xuyên tắt công tắc.",
            level = "N3",
            category = "語彙・漢字"
        )
    )

    /**
     * MỤC 2: TỪ VỰNG (語彙) - 23 BÀI HỌC VÀ 1,138 TỪ VỰNG JLPT N3
     * Tải trực tiếp từ bộ dữ liệu N3 vocabulary do người dùng cung cấp
     */
    val n3Lessons: List<VocabLesson>
        get() = VocabularyDataLoader.getLessons()

    val n3VocabularyList: List<VocabularyItem>
        get() {
            val loaded = VocabularyDataLoader.getAllVocabulary()
            return if (loaded.isNotEmpty()) loaded else initialCombatVocabList
        }

    /**
     * Danh sách câu tiếng Nhật bị khuyết một từ cho chế độ Card Battle (PvE)
     */
    val pveBattleQuestions: List<CardBattlePvEQuestion> = listOf(
        CardBattlePvEQuestion(
            id = "pve_1",
            fullSentence = "電気代を節約するために、こまめにスイッチを切る。",
            sentenceWithBlank = "電気代を【 ＿＿＿ 】するために、こまめにスイッチを切る。",
            missingWord = "節約",
            translation = "Để tiết kiệm tiền điện, hãy thường xuyên tắt công tắc.",
            options = listOf(
                initialCombatVocabList[4], // 節約 (Đúng)
                initialCombatVocabList[0], // 遠慮
                initialCombatVocabList[1], // 偶然
                initialCombatVocabList[2]  // 複雑
            )
        ),
        CardBattlePvEQuestion(
            id = "pve_2",
            fullSentence = "どうぞ遠慮しないで、好きなだけ食べてください。",
            sentenceWithBlank = "どうぞ【 ＿＿＿ 】しないで、好きなだけ食べてください。",
            missingWord = "遠慮",
            translation = "Xin đừng ngại ngùng, hãy cứ ăn thỏa thích nhé.",
            options = listOf(
                initialCombatVocabList[3], // 普段
                initialCombatVocabList[0], // 遠慮 (Đúng)
                initialCombatVocabList[2], // 複雑
                initialCombatVocabList[4]  // 節約
            )
        ),
        CardBattlePvEQuestion(
            id = "pve_3",
            fullSentence = "駅のホームで偶然、高校時代の親友に出会った。",
            sentenceWithBlank = "駅のホームで【 ＿＿＿ 】、高校時代の親友に出会った。",
            missingWord = "偶然",
            translation = "Ở sân ga xe điện, tôi tình cờ gặp lại bạn thân thời cấp ba.",
            options = listOf(
                initialCombatVocabList[1], // 偶然 (Đúng)
                initialCombatVocabList[3], // 普段
                initialCombatVocabList[4], // 節約
                initialCombatVocabList[0]  // 遠慮
            )
        ),
        CardBattlePvEQuestion(
            id = "pve_4",
            fullSentence = "この文法の仕組みは思った以上に複雑ですね。",
            sentenceWithBlank = "この文法の仕組みは思った以上に【 ＿＿＿ 】ですね。",
            missingWord = "複雑",
            translation = "Cấu trúc ngữ pháp này phức tạp hơn tôi tưởng nhiều đấy.",
            options = listOf(
                initialCombatVocabList[0], // 遠慮
                initialCombatVocabList[1], // 偶然
                initialCombatVocabList[2], // 複雑 (Đúng)
                initialCombatVocabList[3]  // 普段
            )
        )
    )

    // Câu hỏi tương tự 1 (Similar Question for Question 1)
    private val similarQuestion1 = GrammarQuestion(
        id = "grammar_1_similar",
        grammarPoint = "～につれて (Càng... thì càng...)",
        question = "年を取る＿＿＿、体力が落ちてきたのを実感する。",
        options = listOf(
            "につれて",
            "によって",
            "に反して",
            "にわたって"
        ),
        correctAnswerIndex = 0,
        explanation = "【につれて】Diễn đạt: 'Càng có tuổi, thể lực càng giảm sút'. Sự biến đổi tuổi tác dẫn tới biến đổi thể lực đồng thời.",
        mistakeExplanation = "Sai ngữ pháp: 'によって' nghĩa là do/bởi, 'に反して' là trái ngược với, 'にわたって' là suốt/trong khoảng. Không diễn tả được sự thay đổi song hành.",
        ruleSummary = "QUY LUẬT QUY NẠP (文法規則): [V-る / N] + につれて: Càng A thì càng B. Biểu đạt sự biến đổi tự nhiên đồng thời của hai vế."
    )

    // Câu hỏi tương tự 2 (Similar Question for Question 2)
    private val similarQuestion2 = GrammarQuestion(
        id = "grammar_2_similar",
        grammarPoint = "～どんなに…ても (Dù có... đến đâu đi nữa)",
        question = "どんなに疲れて＿＿＿、毎日日記を書く習慣を崩さない。",
        options = listOf(
            "いても",
            "いたら",
            "いるなら",
            "いれば"
        ),
        correctAnswerIndex = 0,
        explanation = "【どんなに…ても】Cấu trúc nhượng bộ: 'Dù có mệt đến mấy, tôi cũng không bỏ thói quen viết nhật ký mỗi ngày'.",
        mistakeExplanation = "Sai cấu trúc: Phó từ 'どんなに' bắt buộc phải kết hợp với đuôi nhượng bộ '～ても / ～でも', không đi với たら, なら hay ば.",
        ruleSummary = "QUY LUẬT QUY NẠP (文法規則): どんなに + [V-ても / A-くても / Na-でも]: Cho dù... đến mức độ nào đi nữa thì vế sau vẫn diễn ra không đổi."
    )

    // Câu hỏi tương tự cho ngữ pháp imported: 〜ことにしています
    private val similarQuestionKoto = GrammarQuestion(
        id = "grammar_0_koto_similar",
        grammarPoint = "〜ことにしています (Thói quen tự quyết định)",
        question = "どんなに忙しくても、寝る前に本を＿＿＿ことにしている。",
        options = listOf(
            "読む",
            "読んだ",
            "読もう",
            "読んで"
        ),
        correctAnswerIndex = 0,
        explanation = "【〜ことにしている】Cấu trúc: V-る / V-ない + ことにしている. Biểu thị thói quen do chính bản thân tự quyết định duy trì.",
        mistakeExplanation = "Sai ngữ pháp: Cần dùng động từ nguyên mẫu thể từ điển (V-る) là '読む' để kết hợp với ことにしている.",
        ruleSummary = "QUY LUẬT QUY NẠP: V-る / V-ない + ことにしている → Thói quen do bản thân chủ động quyết định và duy trì."
    )

    /**
     * Cấu trúc câu hỏi trắc nghiệm ngữ pháp 文法(ぶんぽう) JLPT N3
     */
    val n3GrammarQuestions: List<GrammarQuestion> = listOf(
        GrammarQuestion(
            id = "grammar_0_koto",
            grammarPoint = "〜ことにしています (Thói quen tự quyết định)",
            question = "健康のために、毎日野菜を＿＿＿ことにしています。",
            options = listOf(
                "食べる",
                "食べた",
                "食べよう",
                "食べて"
            ),
            correctAnswerIndex = 0,
            explanation = "【〜ことにしています】Cấu trúc: V-る / V-ない + ことにしている. Biểu thị thói quen do chính bản thân tự quyết định và đang duy trì.",
            mistakeExplanation = "Sai ngữ pháp: Cấu trúc này bắt buộc đi với động từ thể từ điển (V-る) hoặc thể phủ định ngắn (V-ない). Các dạng quá khứ (た), ý chí (よう) hay thể て không kết hợp với ことにしている.",
            ruleSummary = "QUY LUẬT QUY NẠP: V-る / V-ない + ことにしている\n→ Biểu thị thói quen do chính bản thân tự quyết định và đang duy trì đều đặn.",
            similarQuestion = similarQuestionKoto
        ),
        GrammarQuestion(
            id = "grammar_1",
            grammarPoint = "～につれて (Càng... thì càng...)",
            question = "台風が近づく＿＿＿、雨と風がどんどん強くなってきた。",
            options = listOf(
                "につれて",
                "にとって",
                "に対して",
                "にかぎって"
            ),
            correctAnswerIndex = 0,
            explanation = "【につれて】Diễn tả hai sự việc cùng biến đổi tỉ lệ thuận với nhau: 'Khi bão càng tiến đến gần, mưa gió càng trở nên dữ dội'.",
            mistakeExplanation = "Lỗi sai: Bạn đã chọn phương án chưa diễn đạt mối quan hệ biến đổi tỉ lệ thuận. 'にとって' là đối với, 'に対して' là đối với/ngược lại, 'にかぎって' là chỉ riêng/xui xẻo.",
            ruleSummary = "QUY LUẬT QUY NẠP: [V-る / N] + につれて\n→ Diễn đạt sự biến đổi tỉ lệ thuận song hành: Khi vế A biến đổi thì vế B cũng kéo theo biến đổi tự nhiên.",
            similarQuestion = similarQuestion1
        ),
        GrammarQuestion(
            id = "grammar_2",
            grammarPoint = "～どんなに…ても (Dù có... đến mức nào chăng nữa)",
            question = "どんなに道が険しく＿＿＿、私たちは目標に向かって進み続ける。",
            options = listOf(
                "ても",
                "たら",
                "なら",
                "ば"
            ),
            correctAnswerIndex = 0,
            explanation = "【どんなに…ても】Đi với dạng Te-mo mang nghĩa nhượng bộ cao: 'Dù cho con đường có hiểm trở thế nào, chúng tôi vẫn kiên định tiến bước'.",
            mistakeExplanation = "Lỗi sai: Phó từ 'どんなに' (dù có thế nào) bắt buộc phải cặp đôi với trợ từ liên kết nhượng bộ '～ても'. Các dạng giả định たら, なら, ば không tương thích ngữ nghĩa.",
            ruleSummary = "QUY LUẬT QUY NẠP: どんなに + [Tính từ đuôi い → くても / Động từ → ても]\n→ Dù mức độ của vế trước có cao đến mấy, ý chí hoặc thực tế ở vế sau vẫn không lay chuyển.",
            similarQuestion = similarQuestion2
        ),
        GrammarQuestion(
            id = "grammar_3",
            grammarPoint = "～にかぎって (Chỉ riêng / Đúng vào lúc)",
            question = "傘を持っていない日＿＿＿、決まって雨が降るものだ。",
            options = listOf(
                "に限って",
                "を通して",
                "を通じて",
                "について"
            ),
            correctAnswerIndex = 0,
            explanation = "【に限って (にかぎって)】Diễn tả sự trớ trêu, xui xẻo: 'Đúng vào cái ngày không mang ô thì y như rằng trời đổ mưa'.",
            mistakeExplanation = "Lỗi sai: 'を通して / を通じて' mang nghĩa là thông qua/suốt khoảng thời gian, còn 'について' là về vấn đề gì đó. Để nói về sự trớ trêu đúng lúc cần dùng に限って.",
            ruleSummary = "QUY LUẬT QUY NẠP: N + に限って\n→ Nhấn mạnh trường hợp ngoại lệ trớ trêu, không may: Đúng vào lúc đặc biệt đó thì sự việc bất như ý lại xảy ra.",
            similarQuestion = null
        ),
        GrammarQuestion(
            id = "grammar_4",
            grammarPoint = "～にしたがって (Theo như / Cùng với)",
            question = "日本での生活に慣れる＿＿＿、日本語も自然に話せるようになった。",
            options = listOf(
                "にしたがって",
                "に反して",
                "に比べて",
                "に基づいて"
            ),
            correctAnswerIndex = 0,
            explanation = "【にしたがって】Diễn đạt quá trình biến đổi liên tục kéo theo kết quả: 'Cùng với việc dần quen cuộc sống ở Nhật, tiếng Nhật cũng nói tự nhiên hơn'.",
            mistakeExplanation = "Lỗi sai: 'に反して' là trái ngược lại với dự đoán, 'に比べて' là so sánh hơn kém, 'に基づいて' là dựa trên cơ sở. Không dùng để chỉ biến đổi theo quá trình.",
            ruleSummary = "QUY LUẬT QUY NẠP: [V-る / N] + にしたがって\n→ Vừa mang nghĩa theo chỉ thị/quy định, vừa mang nghĩa cùng với quá trình biến đổi A thì B cũng phát triển theo.",
            similarQuestion = null
        )
    )

    /**
     * Thẻ bài chiến đấu (Card Battle)
     */
    val initialBattleCards: List<BattleCard> = listOf(
        BattleCard(
            id = "card_1",
            title = "語彙: 節約",
            kanjiSymbol = "節約",
            cardType = CardType.ATTACK,
            power = 40,
            cost = 1,
            description = "Điền vào câu biểu thị tiết kiệm điện, thời gian."
        ),
        BattleCard(
            id = "card_2",
            title = "語彙: 遠慮",
            kanjiSymbol = "遠慮",
            cardType = CardType.ATTACK,
            power = 40,
            cost = 1,
            description = "Điền vào câu biểu thị đừng e dè, ngại ngùng."
        ),
        BattleCard(
            id = "card_3",
            title = "語彙: 偶然",
            kanjiSymbol = "偶然",
            cardType = CardType.ATTACK,
            power = 40,
            cost = 1,
            description = "Điền vào câu biểu thị ngẫu nhiên, tình cờ gặp."
        ),
        BattleCard(
            id = "card_4",
            title = "語彙: 複雑",
            kanjiSymbol = "複雑",
            cardType = CardType.ATTACK,
            power = 40,
            cost = 1,
            description = "Điền vào câu biểu thị cấu trúc, vấn đề phức tạp."
        )
    )
}
