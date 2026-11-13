package com.example.data.repository

import android.content.Context
import com.example.data.model.GrammarExample
import com.example.data.model.GrammarLesson
import com.example.data.model.GrammarPoint
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.InputStream

/**
 * Loads and parses JLPT N3 grammar lessons and grammar points from assets or classpath resources using Moshi.
 */
object GrammarDataLoader {

    private var appContext: Context? = null
    private var cachedLessons: List<GrammarLesson>? = null
    private var cachedAllGrammar: List<GrammarPoint>? = null

    private data class GrammarLessonDto(
        @param:Json(name = "lesson_id") val lessonId: Int = 1,
        @param:Json(name = "title") val title: String = "",
        @param:Json(name = "grammar_list") val grammarList: List<GrammarItemDto> = emptyList()
    )

    private data class GrammarItemDto(
        @param:Json(name = "grammar") val grammar: String = "",
        @param:Json(name = "meaning") val meaning: String = ""
    )

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private val lessonListAdapter by lazy {
        val type = Types.newParameterizedType(List::class.java, GrammarLessonDto::class.java)
        moshi.adapter<List<GrammarLessonDto>>(type)
    }

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    fun clearCache() {
        cachedLessons = null
        cachedAllGrammar = null
    }

    fun getLessons(context: Context? = null): List<GrammarLesson> {
        if (context != null) {
            appContext = context.applicationContext
        }
        cachedLessons?.takeIf { it.isNotEmpty() }?.let { return it }

        val lessons = loadLessons(context ?: appContext)
        if (lessons.isNotEmpty()) {
            cachedLessons = lessons
            cachedAllGrammar = lessons.flatMap { it.grammarList }
        }
        return lessons
    }

    fun getAllGrammar(context: Context? = null): List<GrammarPoint> {
        if (context != null) {
            appContext = context.applicationContext
        }
        cachedAllGrammar?.takeIf { it.isNotEmpty() }?.let { return it }
        val lessons = getLessons(context)
        return if (lessons.isNotEmpty()) cachedAllGrammar ?: emptyList() else emptyList()
    }

    private fun loadLessons(context: Context?): List<GrammarLesson> {
        val stream: InputStream? = try {
            context?.assets?.open("n3_grammar.json")
        } catch (_: Exception) {
            null
        } ?: try {
            GrammarDataLoader::class.java.classLoader?.getResourceAsStream("n3_grammar.json")
        } catch (_: Exception) {
            null
        } ?: try {
            GrammarDataLoader::class.java.classLoader?.getResourceAsStream("assets/n3_grammar.json")
        } catch (_: Exception) {
            null
        } ?: try {
            GrammarDataLoader::class.java.getResourceAsStream("/n3_grammar.json")
        } catch (_: Exception) {
            null
        }

        if (stream == null) {
            return emptyList()
        }

        val text = stream.bufferedReader().use { it.readText() }
        return parseGrammarJson(text)
    }

    fun parseGrammarJson(text: String): List<GrammarLesson> {
        if (text.isBlank()) return emptyList()
        val parsed = try {
            lessonListAdapter.fromJson(text) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        var kotoAssigned = false
        val usedIds = mutableSetOf<String>()

        return parsed.mapIndexed { idx, dto ->
            val lessonId = if (dto.lessonId > 0) dto.lessonId else idx + 1
            val title = dto.title.ifBlank { "Bài $lessonId" }
            val grammarList = dto.grammarList.mapIndexedNotNull { gIdx, item ->
                val grammarTitle = item.grammar.trim()
                val meaning = item.meaning.trim()
                if (grammarTitle.isEmpty()) return@mapIndexedNotNull null

                val cleanGrammar = grammarTitle.replace("〜", "").replace("。", "").trim()
                val matchesKoto = grammarTitle.contains("ことにしている") || grammarTitle.contains("ことにしています")
                val isPrimaryKoto = matchesKoto && !kotoAssigned
                if (isPrimaryKoto) {
                    kotoAssigned = true
                }

                val resolvedTitle = if (isPrimaryKoto) "〜ことにしています" else grammarTitle
                val baseId = if (isPrimaryKoto) "np_01" else "gp_${lessonId}_${gIdx + 1}"
                var uniqueId = baseId
                var suffix = 2
                while (!usedIds.add(uniqueId)) {
                    uniqueId = "${baseId}_$suffix"
                    suffix++
                }

                val examples = createExamplesForGrammar(resolvedTitle, cleanGrammar, meaning)

                GrammarPoint(
                    id = uniqueId,
                    title = resolvedTitle,
                    structure = if (isPrimaryKoto) "V-ru / V-nai + ことにしている" else grammarTitle,
                    meaning = if (isPrimaryKoto) "Biểu thị thói quen do chính bản thân tự quyết định và đang duy trì." else meaning,
                    examples = examples,
                    level = "N3",
                    questionRefId = if (isPrimaryKoto) "grammar_0_koto" else null,
                    lessonId = lessonId,
                    lessonTitle = title
                )
            }
            GrammarLesson(lessonId = lessonId, title = title, grammarList = grammarList)
        }
    }

    private fun createExamplesForGrammar(
        grammar: String,
        cleanGrammar: String,
        meaning: String
    ): List<GrammarExample> {
        return GrammarExamplesData.getExamples(grammar, cleanGrammar, meaning)
    }
}
