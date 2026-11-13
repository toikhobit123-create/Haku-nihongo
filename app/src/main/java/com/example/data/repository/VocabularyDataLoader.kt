package com.example.data.repository

import android.content.Context
import com.example.data.model.VocabLesson
import com.example.data.model.VocabularyItem
import com.example.util.JapaneseRomajiConverter
import com.squareup.moshi.Json
import com.squareup.moshi.Moshi
import com.squareup.moshi.Types
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import java.io.InputStream

/**
 * Loads and parses JLPT N3 vocabulary lessons and words from assets or classpath resources using Moshi.
 */
object VocabularyDataLoader {

    private var appContext: Context? = null
    private var cachedLessons: List<VocabLesson>? = null
    private var cachedAllVocab: List<VocabularyItem>? = null

    private data class VocabLessonDto(
        @param:Json(name = "lesson_id") val lessonId: Int = 1,
        @param:Json(name = "title") val title: String = "",
        @param:Json(name = "vocab_list") val vocabList: List<VocabItemDto> = emptyList()
    )

    private data class VocabItemDto(
        @param:Json(name = "word") val word: String = "",
        @param:Json(name = "reading") val reading: String = "",
        @param:Json(name = "meaning") val meaning: String = ""
    )

    private val moshi: Moshi by lazy {
        Moshi.Builder()
            .addLast(KotlinJsonAdapterFactory())
            .build()
    }

    private val lessonListAdapter by lazy {
        val type = Types.newParameterizedType(List::class.java, VocabLessonDto::class.java)
        moshi.adapter<List<VocabLessonDto>>(type)
    }

    fun initialize(context: Context) {
        appContext = context.applicationContext
    }

    fun clearCache() {
        cachedLessons = null
        cachedAllVocab = null
    }

    fun getLessons(context: Context? = null): List<VocabLesson> {
        if (context != null) {
            appContext = context.applicationContext
        }
        cachedLessons?.takeIf { it.isNotEmpty() }?.let { return it }

        val lessons = loadLessons(context ?: appContext)
        if (lessons.isNotEmpty()) {
            cachedLessons = lessons
            cachedAllVocab = lessons.flatMap { it.vocabList }
        }
        return lessons
    }

    fun getAllVocabulary(context: Context? = null): List<VocabularyItem> {
        if (context != null) {
            appContext = context.applicationContext
        }
        cachedAllVocab?.takeIf { it.isNotEmpty() }?.let { return it }
        val lessons = getLessons(context)
        return if (lessons.isNotEmpty()) cachedAllVocab ?: emptyList() else emptyList()
    }

    private fun loadLessons(context: Context?): List<VocabLesson> {
        val stream: InputStream? = try {
            context?.assets?.open("n3_vocabulary.json")
        } catch (_: Exception) {
            null
        } ?: try {
            VocabularyDataLoader::class.java.classLoader?.getResourceAsStream("n3_vocabulary.json")
        } catch (_: Exception) {
            null
        } ?: try {
            VocabularyDataLoader::class.java.classLoader?.getResourceAsStream("assets/n3_vocabulary.json")
        } catch (_: Exception) {
            null
        } ?: try {
            VocabularyDataLoader::class.java.getResourceAsStream("/n3_vocabulary.json")
        } catch (_: Exception) {
            null
        }

        if (stream == null) {
            return emptyList()
        }

        val text = stream.bufferedReader().use { it.readText() }
        return parseVocabularyJson(text)
    }

    fun parseVocabularyJson(text: String): List<VocabLesson> {
        if (text.isBlank()) return emptyList()
        val parsed = try {
            lessonListAdapter.fromJson(text) ?: emptyList()
        } catch (_: Exception) {
            emptyList()
        }

        return parsed.mapIndexed { idx, dto ->
            val lessonId = if (dto.lessonId > 0) dto.lessonId else idx + 1
            val title = dto.title.ifBlank { "Bài $lessonId" }
            val vocabList = dto.vocabList.mapIndexedNotNull { wIdx, item ->
                val word = item.word.trim()
                val reading = item.reading.trim()
                val meaning = item.meaning.trim()
                if (word.isEmpty() && reading.isEmpty()) return@mapIndexedNotNull null

                val example = VocabularyExamplesData.getExample(word)
                VocabularyItem(
                    id = "vocab_${lessonId}_${wIdx + 1}",
                    kanji = word,
                    hiragana = reading,
                    romaji = JapaneseRomajiConverter.toRomaji(reading),
                    meaning = meaning,
                    exampleSentence = example?.jp ?: "",
                    exampleMeaning = example?.vn ?: "",
                    level = "N3",
                    category = "語彙",
                    lessonId = lessonId,
                    lessonTitle = title
                )
            }
            VocabLesson(lessonId = lessonId, title = title, vocabList = vocabList)
        }
    }
}
