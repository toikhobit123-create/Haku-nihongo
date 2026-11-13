package com.example.data.repository

import android.content.Context
import android.content.SharedPreferences
import com.example.data.model.SrsCardItem
import com.example.data.model.SrsItemType
import com.example.data.model.SrsProgress
import com.example.data.model.SrsStage
import com.example.data.model.SrsStatsSummary
import com.example.data.srs.Sm2Algorithm
import org.json.JSONObject
import java.util.Calendar
import java.util.Locale
import java.util.TimeZone

/**
 * Quản lý lưu trữ và truy xuất dữ liệu ôn tập ngắt quãng SRS (SM-2)
 * Lưu trữ offline 100% bằng SharedPreferences trên thiết bị, hoạt động mượt mà không cần mạng.
 */
object SrsRepository {

    private const val PREFS_NAME = "haku_n3_srs_storage"
    private const val KEY_PROGRESS_DATA = "srs_progress_json"
    private const val KEY_STUDY_STREAK = "user_study_streak"
    private const val KEY_LAST_STUDY_DATE = "last_study_date"
    private const val KEY_TOTAL_ANSWERED = "user_total_answered"
    private const val KEY_TOTAL_CORRECT = "user_total_correct"
    private const val KEY_DAILY_HISTORY = "srs_daily_mastery_history_json"

    private data class DailyMasterySnapshot(
        val grammarMastered: Int,
        val kanjiMastered: Int,
        val vocabMastered: Int
    )

    private var appContext: Context? = null
    private var prefs: SharedPreferences? = null
    private val progressCache: MutableMap<String, SrsProgress> = mutableMapOf()
    private val dailyHistoryCache: MutableMap<String, DailyMasterySnapshot> = mutableMapOf()
    private val canonicalIdMap: MutableMap<String, String> = mutableMapOf()
    private var allCardsCache: List<SrsCardItem>? = null

    private var studyStreak: Int = 0
    private var lastStudyDate: String? = null
    private var totalAnswered: Int = 0
    private var totalCorrect: Int = 0

    fun initialize(context: Context) {
        val appCtx = context.applicationContext
        appContext = appCtx
        GrammarDataLoader.initialize(appCtx)
        VocabularyDataLoader.initialize(appCtx)
        if (prefs == null) {
            prefs = appCtx.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            loadFromStorage()
        }
    }

    fun invalidateCardsCache() {
        allCardsCache = null
        canonicalIdMap.clear()
    }

    /**
     * Trả về ID chuẩn (canonical ID) để các từ vựng trùng lặp giữa các bài dùng chung 1 thẻ SRS duy nhất.
     */
    fun resolveCanonicalId(cardId: String): String {
        if (allCardsCache == null && canonicalIdMap.isEmpty()) {
            getAllCards()
        }
        return canonicalIdMap[cardId] ?: cardId
    }

    /**
     * Nạp toàn bộ kho thẻ Ngữ pháp, Từ vựng (đã khử trùng lặp), Kanji.
     * Không cache nếu dữ liệu từ vựng/ngữ pháp chưa nạp được do thiếu Context.
     */
    fun getAllCards(): List<SrsCardItem> {
        allCardsCache?.let { return it }

        val cards = mutableListOf<SrsCardItem>()
        val newCanonicalMap = mutableMapOf<String, String>()

        // 1. Kho Ngữ pháp N3
        val grammarList = GrammarDataLoader.getAllGrammar(appContext)
        grammarList.forEach { gp ->
            cards.add(
                SrsCardItem(
                    id = gp.id,
                    type = SrsItemType.GRAMMAR,
                    frontText = gp.title,
                    readingText = gp.structure,
                    meaningText = gp.meaning,
                    structureOrRomaji = "Cấu trúc: ${gp.structure}",
                    exampleJp = gp.examples.firstOrNull()?.jp ?: "",
                    exampleVn = gp.examples.firstOrNull()?.vn ?: "",
                    level = gp.level,
                    lessonId = gp.lessonId,
                    lessonTitle = gp.lessonTitle
                )
            )
        }

        // 2. Kho Từ vựng N3 (gộp các từ trùng chữ + cách đọc giữa các bài để không ôn lặp trong SRS)
        val vocabList = VocabularyDataLoader.getAllVocabulary(appContext)
        val seenVocabKeys = mutableMapOf<String, String>() // "kanji|hiragana" -> canonicalId
        vocabList.forEach { v ->
            val dedupKey = "${v.kanji.trim()}|${v.hiragana.trim()}"
            val existingCanonicalId = seenVocabKeys[dedupKey]
            if (existingCanonicalId != null) {
                newCanonicalMap[v.id] = existingCanonicalId
            } else {
                seenVocabKeys[dedupKey] = v.id
                newCanonicalMap[v.id] = v.id
                cards.add(
                    SrsCardItem(
                        id = v.id,
                        type = SrsItemType.VOCABULARY,
                        frontText = v.kanji,
                        readingText = v.hiragana,
                        meaningText = v.meaning,
                        structureOrRomaji = v.romaji,
                        exampleJp = v.exampleSentence,
                        exampleVn = v.exampleMeaning,
                        level = v.level,
                        lessonId = v.lessonId,
                        lessonTitle = v.lessonTitle
                    )
                )
            }
        }

        // 3. Kho Kanji N3
        val kanjiList = MockDataRepository.n3KanjiList
        kanjiList.forEachIndexed { idx, k ->
            cards.add(
                SrsCardItem(
                    id = k.id,
                    type = SrsItemType.KANJI,
                    frontText = k.kanji,
                    readingText = "On: ${k.onyomi} • Kun: ${k.kunyomi}",
                    meaningText = "Hán Việt: ${k.hanViet} — ${k.meaning}",
                    structureOrRomaji = "Từ ghép: ${k.exampleCompound} (${k.exampleCompoundMeaning})",
                    exampleJp = "${k.exampleCompound}（${k.exampleCompoundMeaning}）",
                    exampleVn = "Hán tự N3 [${k.kanji}] (${k.strokeCount} nét) nghĩa là ${k.meaning}.",
                    level = k.level,
                    lessonId = idx + 1,
                    lessonTitle = "Kanji N3"
                )
            )
        }

        canonicalIdMap.clear()
        canonicalIdMap.putAll(newCanonicalMap)

        // Chỉ cache khi dữ liệu đã nạp đầy đủ, tránh cache list rỗng khi chưa có Context
        if (grammarList.isNotEmpty() && vocabList.isNotEmpty()) {
            allCardsCache = cards
        }
        return cards
    }

    /**
     * Lấy danh sách thẻ thuộc một bài học cụ thể (gắn canonical ID để đồng bộ tiến độ với kho chung).
     */
    fun getCardsForLesson(type: SrsItemType, lessonId: Int): List<SrsCardItem> {
        val allCards = getAllCards()
        if (type != SrsItemType.VOCABULARY) {
            return allCards.filter { it.type == type && it.lessonId == lessonId }
        }

        val lesson = VocabularyDataLoader.getLessons(appContext).firstOrNull { it.lessonId == lessonId }
            ?: return allCards.filter { it.type == SrsItemType.VOCABULARY && it.lessonId == lessonId }

        val seenKeys = mutableSetOf<String>()
        return lesson.vocabList.mapNotNull { v ->
            val key = "${v.kanji.trim()}|${v.hiragana.trim()}"
            if (!seenKeys.add(key)) return@mapNotNull null
            val canonicalId = resolveCanonicalId(v.id)
            SrsCardItem(
                id = canonicalId,
                type = SrsItemType.VOCABULARY,
                frontText = v.kanji,
                readingText = v.hiragana,
                meaningText = v.meaning,
                structureOrRomaji = v.romaji,
                exampleJp = v.exampleSentence,
                exampleVn = v.exampleMeaning,
                level = v.level,
                lessonId = v.lessonId,
                lessonTitle = v.lessonTitle
            )
        }
    }

    /**
     * Lấy tiến độ SRS của một thẻ (nếu chưa có thì trả về thẻ mới)
     */
    fun getProgress(cardId: String, type: SrsItemType): SrsProgress {
        val canonicalId = resolveCanonicalId(cardId)
        return progressCache[canonicalId] ?: SrsProgress(
            id = canonicalId,
            type = type,
            repetitions = 0,
            intervalDays = 0,
            easeFactor = 2.5,
            stage = SrsStage.NEW
        )
    }

    /**
     * Lưu tiến độ SRS đã cập nhật vào bộ nhớ và ghi xuống bộ nhớ offline
     */
    fun saveProgress(progress: SrsProgress, now: Long = System.currentTimeMillis(), timeZone: TimeZone = TimeZone.getDefault()) {
        val canonicalId = resolveCanonicalId(progress.id)
        val normalized = if (canonicalId == progress.id) progress else progress.copy(id = canonicalId)
        progressCache[canonicalId] = normalized
        persistToStorage()
        recordTodaySnapshot(now, timeZone)
    }

    /**
     * Kiểm tra thẻ đã được đánh dấu thuộc / thành thạo chưa
     */
    fun isCardMastered(cardId: String): Boolean {
        val canonicalId = resolveCanonicalId(cardId)
        val p = progressCache[canonicalId] ?: return false
        return p.stage == SrsStage.MASTERED || p.repetitions >= 3 || p.intervalDays >= 21
    }

    /**
     * Đánh dấu thẻ là ĐÃ THUỘC (MASTERED) và lưu ngay vào SharedPreferences.
     * Lịch ôn được căn theo đầu ngày dương lịch thay vì cộng cứng 24 giờ.
     */
    fun markCardMastered(
        cardId: String,
        type: SrsItemType,
        now: Long = System.currentTimeMillis(),
        timeZone: TimeZone = TimeZone.getDefault()
    ) {
        if (cardId.isBlank()) return
        val canonicalId = resolveCanonicalId(cardId)
        val current = getProgress(canonicalId, type)
        val intervalDays = maxOf(current.intervalDays, 21)
        val updated = current.copy(
            id = canonicalId,
            repetitions = maxOf(current.repetitions, 3),
            intervalDays = intervalDays,
            easeFactor = maxOf(current.easeFactor, 2.5),
            lastReviewedTimestamp = now,
            nextReviewTimestamp = Sm2Algorithm.startOfDayAfterDays(now, intervalDays, timeZone),
            totalReviews = current.totalReviews + 1,
            stage = SrsStage.MASTERED
        )
        saveProgress(updated, now, timeZone)
    }

    /**
     * Bật/tắt trạng thái Đã thuộc cho một thẻ (dùng trong màn hình chi tiết bài học)
     */
    fun toggleCardMastered(cardId: String, type: SrsItemType): Boolean {
        val canonicalId = resolveCanonicalId(cardId)
        return if (isCardMastered(canonicalId)) {
            val reset = SrsProgress(
                id = canonicalId,
                type = type,
                repetitions = 0,
                intervalDays = 0,
                easeFactor = 2.5,
                stage = SrsStage.NEW
            )
            saveProgress(reset)
            false
        } else {
            markCardMastered(canonicalId, type)
            true
        }
    }

    /**
     * Lấy danh sách các thẻ đến hạn ôn tập hôm nay (nextReviewTimestamp <= now) hoặc thẻ mới cần học
     */
    fun getDueOrNewCards(
        filterType: SrsItemType? = null,
        limit: Int = 30,
        now: Long = System.currentTimeMillis()
    ): List<Pair<SrsCardItem, SrsProgress>> {
        val allCards = getAllCards()
        val filteredCards = if (filterType == null) allCards else allCards.filter { it.type == filterType }

        val result = mutableListOf<Pair<SrsCardItem, SrsProgress>>()
        val dueCards = mutableListOf<Pair<SrsCardItem, SrsProgress>>()
        val newCards = mutableListOf<Pair<SrsCardItem, SrsProgress>>()

        for (card in filteredCards) {
            val progress = getProgress(card.id, card.type)
            if (progress.stage == SrsStage.NEW || progress.totalReviews == 0) {
                newCards.add(Pair(card, progress))
            } else if (progress.nextReviewTimestamp <= now) {
                dueCards.add(Pair(card, progress))
            }
        }

        dueCards.sortBy { it.second.nextReviewTimestamp }

        result.addAll(dueCards.take(limit))
        if (result.size < limit) {
            val remaining = limit - result.size
            result.addAll(newCards.take(remaining))
        }

        return result
    }

    /**
     * Tổng hợp thống kê kết quả học tập và điểm yếu
     */
    fun getStatsSummary(
        now: Long = System.currentTimeMillis(),
        timeZone: TimeZone = TimeZone.getDefault()
    ): SrsStatsSummary {
        val allCards = getAllCards()
        var dueCount = 0
        var newCount = 0
        var learningCount = 0
        var reviewCount = 0
        var masteredCount = 0
        var totalReviews = 0
        var totalLapses = 0

        val lapsedList = mutableListOf<Pair<SrsCardItem, SrsProgress>>()

        var masteredGrammar = 0
        var masteredVocab = 0
        var masteredKanji = 0
        var totalGrammar = 0
        var totalVocab = 0
        var totalKanji = 0

        for (card in allCards) {
            when (card.type) {
                SrsItemType.GRAMMAR -> totalGrammar++
                SrsItemType.VOCABULARY -> totalVocab++
                SrsItemType.KANJI -> totalKanji++
            }

            val progress = progressCache[resolveCanonicalId(card.id)]
            if (progress == null || progress.totalReviews == 0 || progress.stage == SrsStage.NEW) {
                newCount++
            } else {
                totalReviews += progress.totalReviews
                totalLapses += progress.lapses

                if (progress.lapses > 0) {
                    lapsedList.add(Pair(card, progress))
                }

                if (progress.nextReviewTimestamp <= now) {
                    dueCount++
                }

                val isMastered = progress.stage == SrsStage.MASTERED || progress.repetitions >= 3 || progress.intervalDays >= 21
                when {
                    isMastered -> {
                        masteredCount++
                        when (card.type) {
                            SrsItemType.GRAMMAR -> masteredGrammar++
                            SrsItemType.VOCABULARY -> masteredVocab++
                            SrsItemType.KANJI -> masteredKanji++
                        }
                    }
                    progress.stage == SrsStage.REVIEW -> reviewCount++
                    progress.stage == SrsStage.LEARNING -> learningCount++
                    else -> newCount++
                }
            }
        }

        val retentionRate = if (totalReviews > 0) {
            val successReviews = (totalReviews - totalLapses).coerceAtLeast(0)
            ((successReviews.toFloat() / totalReviews.toFloat()) * 100).toInt()
        } else {
            0
        }

        // Kiểm tra streak còn hiệu lực theo ngày dương lịch (hôm nay hoặc hôm qua)
        val todayEpoch = epochDayFromTimestamp(now, timeZone)
        val lastEpoch = lastStudyDate?.let(::parseIsoDateToEpochDay)
        val activeStreak = when {
            lastEpoch == null -> 0
            todayEpoch - lastEpoch in 0L..1L -> studyStreak
            else -> 0
        }

        val accuracy = if (totalAnswered > 0) {
            ((totalCorrect.toFloat() / totalAnswered.toFloat()) * 100).toInt()
        } else {
            0
        }

        // Ghi nhận snapshot thực tế của hôm nay và dựng biểu đồ 7 ngày gần nhất từ lịch sử thực tế
        val todayStr = formatIsoDate(now, timeZone)
        val gMax = masteredGrammar
        val kMax = masteredKanji
        val vMax = masteredVocab

        if (gMax > 0 || kMax > 0 || vMax > 0 || dailyHistoryCache.containsKey(todayStr)) {
            val todaySnapshot = DailyMasterySnapshot(gMax, kMax, vMax)
            if (dailyHistoryCache[todayStr] != todaySnapshot) {
                dailyHistoryCache[todayStr] = todaySnapshot
                persistDailyHistory()
            }
        }

        val sortedPriorEntries = dailyHistoryCache.entries
            .mapNotNull { entry -> parseIsoDateToEpochDay(entry.key)?.let { it to entry.value } }
            .sortedBy { it.first }

        val chartData = (6 downTo 0).map { daysAgo ->
            val targetEpoch = todayEpoch - daysAgo
            val targetDateStr = formatIsoDateFromEpochOffset(now, -daysAgo, timeZone)
            val dayLabel = formatVietnameseDayLabel(now, -daysAgo, timeZone)

            val exact = dailyHistoryCache[targetDateStr]
            val snapshot = when {
                daysAgo == 0 -> DailyMasterySnapshot(gMax, kMax, vMax)
                exact != null -> exact
                else -> {
                    // Nếu ngày đó không mở app, giữ mức tích lũy gần nhất trước ngày đó (hoặc 0 nếu chưa học)
                    sortedPriorEntries.lastOrNull { it.first < targetEpoch }?.second
                        ?: DailyMasterySnapshot(0, 0, 0)
                }
            }

            com.example.data.model.MasteryDataPoint(
                dayLabel = dayLabel,
                grammarMastered = snapshot.grammarMastered,
                kanjiMastered = snapshot.kanjiMastered,
                vocabMastered = snapshot.vocabMastered
            )
        }

        val topLapsed = if (lapsedList.isNotEmpty()) {
            lapsedList.sortedByDescending { it.second.lapses }.take(12)
        } else {
            emptyList()
        }

        return SrsStatsSummary(
            totalCards = allCards.size,
            dueCount = dueCount,
            newCount = newCount,
            learningCount = learningCount,
            reviewCount = reviewCount,
            masteredCount = masteredCount,
            retentionRatePercent = retentionRate,
            topLapsedCards = topLapsed,
            masteredGrammar = gMax,
            masteredVocab = vMax,
            masteredKanji = kMax,
            totalGrammar = totalGrammar,
            totalVocab = totalVocab,
            totalKanji = totalKanji,
            streakDays = activeStreak,
            accuracyRatePercent = accuracy,
            masteryOverTime = chartData
        )
    }

    /**
     * Lấy danh sách thẻ điểm yếu cho phiên học ngắn (10-15 câu)
     */
    fun getWeakPointsSessionCards(limit: Int = 12): List<Pair<SrsCardItem, SrsProgress>> {
        val allCards = getAllCards()
        val lapsedList = mutableListOf<Pair<SrsCardItem, SrsProgress>>()

        for (card in allCards) {
            val progress = progressCache[resolveCanonicalId(card.id)]
            if (progress != null && progress.lapses > 0) {
                lapsedList.add(Pair(card, progress))
            }
        }

        val sorted = lapsedList.sortedByDescending { it.second.lapses }.take(limit)
        return if (sorted.size >= 5) {
            sorted
        } else {
            val learningOrNew = allCards.filter { card ->
                val p = progressCache[resolveCanonicalId(card.id)]
                p == null || p.stage == SrsStage.LEARNING || p.stage == SrsStage.NEW
            }.take(limit - sorted.size).map { card ->
                val canonicalId = resolveCanonicalId(card.id)
                Pair(card, progressCache[canonicalId] ?: SrsProgress(canonicalId, card.type))
            }
            sorted + learningOrNew
        }
    }

    /**
     * Ghi nhận kết quả trả lời câu hỏi để tính Tỷ lệ đúng
     */
    fun recordAnswerResult(isCorrect: Boolean) {
        totalAnswered += 1
        if (isCorrect) {
            totalCorrect += 1
        }
        prefs?.edit()?.apply {
            putInt(KEY_TOTAL_ANSWERED, totalAnswered)
            putInt(KEY_TOTAL_CORRECT, totalCorrect)
            apply()
        }
    }

    /**
     * Hoàn thành một phiên học (Session Complete) -> Cập nhật Streak theo ngày dương lịch (yyyy-MM-dd).
     * - Cùng ngày: giữ nguyên streak
     * - Liên tiếp (hôm qua): tăng +1 (xử lý chuẩn qua giao thừa 31/12 -> 01/01)
     * - Nghỉ >= 1 ngày (ví dụ nghỉ 5 ngày): đặt lại streak về 1
     */
    fun completeSession(
        now: Long = System.currentTimeMillis(),
        timeZone: TimeZone = TimeZone.getDefault()
    ) {
        val todayStr = formatIsoDate(now, timeZone)
        val todayEpoch = epochDayFromTimestamp(now, timeZone)
        val lastEpoch = lastStudyDate?.let(::parseIsoDateToEpochDay)

        val updatedStreak = when {
            lastEpoch == null -> 1
            todayEpoch == lastEpoch -> studyStreak.coerceAtLeast(1)
            todayEpoch - lastEpoch == 1L -> studyStreak + 1
            else -> 1
        }

        studyStreak = updatedStreak
        lastStudyDate = todayStr
        recordTodaySnapshot(now, timeZone)

        prefs?.edit()?.apply {
            putInt(KEY_STUDY_STREAK, studyStreak)
            putString(KEY_LAST_STUDY_DATE, lastStudyDate)
            apply()
        }
    }

    /**
     * Đặt lại toàn bộ dữ liệu ôn tập (bao gồm tiến độ thẻ SM-2, lịch sử biểu đồ, Streak và Tỷ lệ chính xác).
     */
    fun resetAllProgress() {
        progressCache.clear()
        dailyHistoryCache.clear()
        studyStreak = 0
        lastStudyDate = null
        totalAnswered = 0
        totalCorrect = 0
        prefs?.edit()?.clear()?.apply()
    }

    private fun loadFromStorage() {
        val p = prefs ?: return
        studyStreak = p.getInt(KEY_STUDY_STREAK, 0)
        lastStudyDate = p.getString(KEY_LAST_STUDY_DATE, null)
        totalAnswered = p.getInt(KEY_TOTAL_ANSWERED, 0)
        totalCorrect = p.getInt(KEY_TOTAL_CORRECT, 0)

        val jsonStr = p.getString(KEY_PROGRESS_DATA, null) ?: return
        try {
            val root = JSONObject(jsonStr)
            val keys = root.keys()
            while (keys.hasNext()) {
                val rawKey = keys.next()
                val canonicalKey = resolveCanonicalId(rawKey)
                val obj = root.getJSONObject(rawKey)
                val typeStr = obj.optString("type", SrsItemType.VOCABULARY.name)
                val type = try { SrsItemType.valueOf(typeStr) } catch (_: Exception) { SrsItemType.VOCABULARY }
                val stageStr = obj.optString("stage", SrsStage.NEW.name)
                val stage = try { SrsStage.valueOf(stageStr) } catch (_: Exception) { SrsStage.NEW }

                val progress = SrsProgress(
                    id = canonicalKey,
                    type = type,
                    repetitions = obj.optInt("repetitions", 0),
                    intervalDays = obj.optInt("intervalDays", 0),
                    easeFactor = obj.optDouble("easeFactor", 2.5),
                    lastReviewedTimestamp = obj.optLong("lastReviewedTimestamp", 0L),
                    nextReviewTimestamp = obj.optLong("nextReviewTimestamp", 0L),
                    lapses = obj.optInt("lapses", 0),
                    totalReviews = obj.optInt("totalReviews", 0),
                    stage = stage
                )

                val existing = progressCache[canonicalKey]
                if (existing == null || progress.totalReviews > existing.totalReviews) {
                    progressCache[canonicalKey] = progress
                }
            }
        } catch (_: Exception) {
            // Trường hợp dữ liệu cũ hoặc lỗi định dạng
        }

        val historyStr = p.getString(KEY_DAILY_HISTORY, null) ?: return
        try {
            val hRoot = JSONObject(historyStr)
            val hKeys = hRoot.keys()
            while (hKeys.hasNext()) {
                val dateKey = hKeys.next()
                val obj = hRoot.getJSONObject(dateKey)
                dailyHistoryCache[dateKey] = DailyMasterySnapshot(
                    grammarMastered = obj.optInt("g", 0),
                    kanjiMastered = obj.optInt("k", 0),
                    vocabMastered = obj.optInt("v", 0)
                )
            }
        } catch (_: Exception) {
        }
    }

    /**
     * Tính toán số lượng và % đã thuộc (% Mastered) cho một bài học cụ thể
     */
    fun getLessonMastery(type: SrsItemType, cardIds: List<String>, lessonId: Int): Pair<Int, Float> {
        if (cardIds.isEmpty()) return Pair(0, 0f)
        var mastered = 0
        for (id in cardIds) {
            val p = progressCache[resolveCanonicalId(id)]
            if (p != null) {
                if (p.stage == SrsStage.MASTERED || p.repetitions >= 3 || p.intervalDays >= 21) {
                    mastered++
                }
            }
        }
        val pct = (mastered.toFloat() / cardIds.size.toFloat()) * 100f
        return Pair(mastered, pct)
    }

    private fun persistToStorage() {
        try {
            val root = JSONObject()
            for ((key, p) in progressCache) {
                val obj = JSONObject().apply {
                    put("type", p.type.name)
                    put("repetitions", p.repetitions)
                    put("intervalDays", p.intervalDays)
                    put("easeFactor", p.easeFactor)
                    put("lastReviewedTimestamp", p.lastReviewedTimestamp)
                    put("nextReviewTimestamp", p.nextReviewTimestamp)
                    put("lapses", p.lapses)
                    put("totalReviews", p.totalReviews)
                    put("stage", p.stage.name)
                }
                root.put(key, obj)
            }
            prefs?.edit()?.putString(KEY_PROGRESS_DATA, root.toString())?.apply()
        } catch (_: Exception) {
        }
    }

    private fun recordTodaySnapshot(now: Long, timeZone: TimeZone) {
        val allCards = getAllCards()
        var g = 0
        var v = 0
        var k = 0
        for (card in allCards) {
            val p = progressCache[resolveCanonicalId(card.id)] ?: continue
            val isMastered = p.stage == SrsStage.MASTERED || p.repetitions >= 3 || p.intervalDays >= 21
            if (isMastered) {
                when (card.type) {
                    SrsItemType.GRAMMAR -> g++
                    SrsItemType.VOCABULARY -> v++
                    SrsItemType.KANJI -> k++
                }
            }
        }
        val todayStr = formatIsoDate(now, timeZone)
        val snap = DailyMasterySnapshot(g, k, v)
        if (dailyHistoryCache[todayStr] != snap) {
            dailyHistoryCache[todayStr] = snap
            persistDailyHistory()
        }
    }

    private fun persistDailyHistory() {
        try {
            val root = JSONObject()
            for ((dateStr, snap) in dailyHistoryCache) {
                val obj = JSONObject().apply {
                    put("g", snap.grammarMastered)
                    put("k", snap.kanjiMastered)
                    put("v", snap.vocabMastered)
                }
                root.put(dateStr, obj)
            }
            prefs?.edit()?.putString(KEY_DAILY_HISTORY, root.toString())?.apply()
        } catch (_: Exception) {
        }
    }

    private fun formatIsoDateFromEpochOffset(baseNow: Long, dayOffset: Int, timeZone: TimeZone): String {
        val cal = Calendar.getInstance(timeZone).apply {
            timeInMillis = baseNow
            add(Calendar.DAY_OF_YEAR, dayOffset)
        }
        return String.format(
            Locale.US,
            "%04d-%02d-%02d",
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    private fun formatVietnameseDayLabel(baseNow: Long, dayOffset: Int, timeZone: TimeZone): String {
        val cal = Calendar.getInstance(timeZone).apply {
            timeInMillis = baseNow
            add(Calendar.DAY_OF_YEAR, dayOffset)
        }
        return when (cal.get(Calendar.DAY_OF_WEEK)) {
            Calendar.MONDAY -> "T2"
            Calendar.TUESDAY -> "T3"
            Calendar.WEDNESDAY -> "T4"
            Calendar.THURSDAY -> "T5"
            Calendar.FRIDAY -> "T6"
            Calendar.SATURDAY -> "T7"
            Calendar.SUNDAY -> "CN"
            else -> ""
        }
    }

    // ==========================================
    // DATE HELPERS (API 24+ safe, DST-proof)
    // ==========================================

    internal fun formatIsoDate(timestampMs: Long, timeZone: TimeZone = TimeZone.getDefault()): String {
        val cal = Calendar.getInstance(timeZone).apply { timeInMillis = timestampMs }
        val year = cal.get(Calendar.YEAR)
        val month = cal.get(Calendar.MONTH) + 1
        val day = cal.get(Calendar.DAY_OF_MONTH)
        return String.format(Locale.US, "%04d-%02d-%02d", year, month, day)
    }

    internal fun epochDayFromTimestamp(timestampMs: Long, timeZone: TimeZone = TimeZone.getDefault()): Long {
        val cal = Calendar.getInstance(timeZone).apply { timeInMillis = timestampMs }
        return daysFromCivil(
            cal.get(Calendar.YEAR),
            cal.get(Calendar.MONTH) + 1,
            cal.get(Calendar.DAY_OF_MONTH)
        )
    }

    internal fun parseIsoDateToEpochDay(isoDate: String): Long? {
        val parts = isoDate.split("-")
        if (parts.size != 3) return null
        val y = parts[0].toIntOrNull() ?: return null
        val m = parts[1].toIntOrNull() ?: return null
        val d = parts[2].toIntOrNull() ?: return null
        if (m !in 1..12 || d !in 1..31) return null
        return daysFromCivil(y, m, d)
    }

    private fun daysFromCivil(year: Int, month: Int, day: Int): Long {
        val y = year - (if (month <= 2) 1 else 0)
        val era = (if (y >= 0) y else y - 399) / 400
        val yoe = y - era * 400
        val doy = (153 * (month + (if (month > 2) -3 else 9)) + 2) / 5 + day - 1
        val doe = yoe * 365 + yoe / 4 - yoe / 100 + doy
        return era * 146097L + doe - 719468L
    }
}
