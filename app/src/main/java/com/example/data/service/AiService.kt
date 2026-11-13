package com.example.data.service

import android.content.Context
import android.content.SharedPreferences
import android.util.Log
import com.example.BuildConfig
import com.example.data.model.CardBattlePvEQuestion
import com.example.data.model.ChatMessage
import com.example.data.model.ChatRole
import com.example.data.model.GeminiModelChoice
import com.example.data.model.GroundingSource
import com.example.data.model.SenseiPersona
import com.example.data.repository.MockDataRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeout
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.InputStreamReader
import java.io.OutputStreamWriter
import java.net.HttpURLConnection
import java.net.URL

data class GeminiChatResponse(
    val replyText: String,
    val modelUsed: String,
    val searchQueries: List<String> = emptyList(),
    val groundingSources: List<GroundingSource> = emptyList()
)

/**
 * AiService.kt - Dịch vụ hợp nhất Google AI Studio (Gemini API) cho toàn bộ ứng dụng.
 *
 * - Đọc API key từ BuildConfig.GEMINI_API_KEY hoặc SharedPreferences (được lưu bền vững khi người dùng nhập trên UI).
 * - Sử dụng header 'x-goog-api-key' thay vì đưa key vào URL query parameter.
 * - Model mặc định duy nhất: gemini-2.5-flash (hiện hành, ổn định, rẻ nhất).
 * - Tự động xử lý lỗi xác thực 401 / UNAUTHENTICATED và kích hoạt chế độ giải thích sư phạm ngoại tuyến (Offline Knowledge Fallback)
 *   giúp ứng dụng luôn phản hồi mượt mà, không bao giờ bị đơ hay báo lỗi gãy đổ.
 */
object AiService {

    private const val TAG = "AiService"
    private const val PREFS_NAME = "haku_n3_ai_prefs"
    private const val KEY_CUSTOM_API_KEY = "gemini_custom_api_key"

    /**
     * Hằng số định danh model Gemini duy nhất, dễ dàng thay đổi và nâng cấp.
     */
    const val DEFAULT_GEMINI_MODEL = "gemini-2.5-flash"
    private const val BASE_URL = "https://generativelanguage.googleapis.com/v1beta/models"

    private var prefs: SharedPreferences? = null
    private var customApiKey: String? = null

    /**
     * Khởi tạo lưu trữ bền vững với Context ứng dụng
     */
    fun initialize(context: Context) {
        if (prefs == null) {
            prefs = context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
            val savedKey = prefs?.getString(KEY_CUSTOM_API_KEY, null)
            if (!savedKey.isNullOrBlank()) {
                customApiKey = savedKey
            }
        }
    }

    /**
     * Lấy API key hiện tại: ưu tiên customApiKey nếu người dùng đã nhập trên UI / SharedPreferences,
     * mặc định lấy từ BuildConfig.GEMINI_API_KEY.
     */
    fun getApiKey(): String {
        val custom = customApiKey
        if (!custom.isNullOrBlank()) return custom
        return try {
            val key = BuildConfig.GEMINI_API_KEY
            if (key == "your_key_here") "" else key
        } catch (e: Exception) {
            ""
        }
    }

    /**
     * Cập nhật API key mới và lưu bền vững vào SharedPreferences
     */
    fun updateCustomApiKey(key: String) {
        val trimmed = key.trim()
        customApiKey = trimmed.ifBlank { null }
        prefs?.edit()?.putString(KEY_CUSTOM_API_KEY, customApiKey ?: "")?.apply()
    }

    /**
     * Biến API_KEY cho phép đọc/ghi tương thích ngược với các thành phần và test cũ.
     */
    var API_KEY: String
        get() = getApiKey()
        set(value) {
            updateCustomApiKey(value)
        }

    /**
     * 1. Giải thích lỗi sai ngữ pháp N3 cho Quiz Engine (Học Quy Nạp)
     */
    suspend fun explainGrammarError(
        question: String,
        wrongAnswer: String,
        correctAnswer: String = "",
        exerciseType: String = "Trắc nghiệm"
    ): Result<String> = withContext(Dispatchers.IO) {
        val systemInstruction =
            "Bạn là một giáo viên dạy tiếng Nhật trình độ JLPT N3 giàu kinh nghiệm, ân cần và sư phạm. " +
            "Nhiệm vụ của bạn là phân tích bài tập tiếng Nhật và giải thích chi tiết, tự nhiên bằng tiếng Việt thuần khiết (tuyệt đối không dùng dịch máy cứng nhắc). " +
            "Chỉ ra tại sao phương án người học chọn hoặc xếp là chưa đúng về sắc thái hay cấu trúc, đối chiếu với đáp án chuẩn và rút ra quy tắc ngữ pháp cốt lõi cần nhớ."

        val userPrompt =
            "Học viên vừa làm một bài tập tiếng Nhật JLPT N3 dạng \"$exerciseType\" và trả lời chưa chính xác:\n" +
            "- Câu hỏi / Ngữ cảnh: \"$question\"\n" +
            "- Phương án học viên đã chọn/xếp: \"$wrongAnswer\"\n" +
            (if (correctAnswer.isNotBlank()) "- Đáp án đúng chuẩn: \"$correctAnswer\"\n" else "") +
            "\nHãy giải thích ngắn gọn, súc tích (khoảng 3-4 câu) bằng tiếng Việt tự nhiên:\n" +
            "1. Phân tích lỗi sai: Tại sao phương án \"$wrongAnswer\" lại không phù hợp trong ngữ cảnh này?\n" +
            "2. Quy luật cốt lõi & sắc thái: Điểm ngữ pháp N3 chuẩn là gì và mẹo ghi nhớ để không bị bẫy."

        try {
            val text = executeSinglePrompt(systemInstruction, userPrompt)
            Result.success(text)
        } catch (e: Exception) {
            Log.w(TAG, "Không thể kết nối Gemini API (${e.message}), chuyển sang giải thích sư phạm ngoại tuyến")
            val fallback = buildString {
                append("• Phân tích: Phương án \"$wrongAnswer\" chưa phù hợp về mặt cấu trúc kết nối hoặc sắc thái ngữ cảnh trong câu này.")
                if (correctAnswer.isNotBlank()) {
                    append("\n• Đáp án chuẩn: \"$correctAnswer\".")
                }
                append("\n• Quy tắc cốt lõi: Hãy chú ý thể của động từ đi trước và mục đích biểu đạt của mẫu câu N3.")
                if (e.message?.contains("API_KEY") == true || e.message?.contains("401") == true) {
                    append("\n(💡 Mẹo: Cập nhật Gemini API Key mới tại màn hình Lộ trình để AI phân tích chi tiết trực tuyến).")
                }
            }
            Result.success(fallback)
        }
    }

    /**
     * 2. Phân tích câu và từ vựng tiếng Nhật N3
     */
    suspend fun explainSentenceAndWord(
        sentence: String,
        selectedWord: String
    ): Result<String> = withContext(Dispatchers.IO) {
        val systemInstruction = "Bạn là một giáo viên tiếng Nhật JLPT N3 chuyên nghiệp."
        val prompt = "Hãy phân tích câu sau: \"$sentence\" và từ vựng \"$selectedWord\". " +
                "Giải thích ngắn gọn súc tích trong 2-3 câu bằng tiếng Việt về ý nghĩa, cách dùng và sắc thái ngữ cảnh."

        try {
            val text = executeSinglePrompt(systemInstruction, prompt)
            Result.success(text)
        } catch (e: Exception) {
            Log.w(TAG, "Lỗi gọi explainSentenceAndWord (${e.message}), sử dụng giải thích chuẩn bị sẵn")
            val fallback = "Từ vựng \"$selectedWord\" trong câu \"$sentence\": Đóng vai trò then chốt trong cấu trúc ngữ nghĩa câu JLPT N3. Hãy lưu ý trợ từ đi kèm và sắc thái văn cảnh giao tiếp tự nhiên."
            Result.success(fallback)
        }
    }

    /**
     * 3. Sinh câu hỏi thử thách Card Battle (PvE) mới tự động
     */
    suspend fun generateAiBattleChallenge(): Result<CardBattlePvEQuestion> = withContext(Dispatchers.IO) {
        try {
            val apiKey = getApiKey()
            if (apiKey.isBlank() || apiKey == "your_key_here") {
                val fallback = MockDataRepository.pveBattleQuestions.random()
                return@withContext Result.success(fallback)
            }

            val prompt = """
                Hãy tạo 1 câu tiếng Nhật trình độ JLPT N3 có khuyết 1 từ vựng (chỗ khuyết ghi là 【 ＿＿＿ 】), 
                kèm theo từ vựng đúng và 3 từ vựng gây nhiễu, cùng nghĩa tiếng Việt.
                Trả về JSON duy nhất không có markdown:
                {
                   "fullSentence": "câu đầy đủ",
                   "sentenceWithBlank": "câu có 【 ＿＿＿ 】",
                   "missingWord": "từ đúng",
                   "translation": "nghĩa tiếng Việt"
                }
            """.trimIndent()

            val rawJson = executeSinglePrompt("", prompt)
            val cleanJson = rawJson.replace("```json", "").replace("```", "").trim()
            val jsonObj = JSONObject(cleanJson)

            val question = CardBattlePvEQuestion(
                id = "pve_ai_${System.currentTimeMillis()}",
                fullSentence = jsonObj.getString("fullSentence"),
                sentenceWithBlank = jsonObj.getString("sentenceWithBlank"),
                missingWord = jsonObj.getString("missingWord"),
                translation = jsonObj.getString("translation"),
                options = MockDataRepository.n3VocabularyList.shuffled().take(4)
            )

            Result.success(question)
        } catch (e: Exception) {
            Log.w(TAG, "Lỗi tạo câu hỏi Card Battle AI (${e.message}), dùng câu hỏi chuẩn bị sẵn")
            Result.success(MockDataRepository.pveBattleQuestions.random())
        }
    }

    /**
     * 4. Gửi tin nhắn trong phiên hội thoại đa lượt (Multi-turn chat) tới Gemini API
     * Có fallback thông minh sang chế độ sư phạm ngoại tuyến khi API key chưa hợp lệ.
     */
    suspend fun sendChatMessage(
        history: List<ChatMessage>,
        newUserMessage: String,
        persona: SenseiPersona,
        modelChoice: GeminiModelChoice = GeminiModelChoice.GEMINI_2_5_FLASH,
        enableSearchGrounding: Boolean = false
    ): Result<GeminiChatResponse> = withContext(Dispatchers.IO) {
        try {
            val response = withTimeout(60_000L) {
                executeChatCall(
                    modelName = DEFAULT_GEMINI_MODEL,
                    history = history,
                    newUserMessage = newUserMessage,
                    persona = persona,
                    enableSearchGrounding = enableSearchGrounding
                )
            }
            Result.success(response)
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            Log.w(TAG, "Kết nối Gemini API không thành công: ${e.message}. Kích hoạt phản hồi sư phạm Ngoại Tuyến.")
            val isAuthIssue = e.message?.contains("401") == true ||
                    e.message?.contains("API_KEY") == true ||
                    e.message?.contains("UNAUTHENTICATED") == true

            val reply = buildPedagogicalChatReply(persona, newUserMessage, isAuthIssue)
            Result.success(
                GeminiChatResponse(
                    replyText = reply,
                    modelUsed = "${persona.title} (Ngoại Tuyến)"
                )
            )
        }
    }

    // ==========================================
    // CƠ CHẾ PHẢN HỒI SƯ PHẠM NGOẠI TUYẾN
    // ==========================================

    private fun buildPedagogicalChatReply(
        persona: SenseiPersona,
        userMsg: String,
        isAuthIssue: Boolean
    ): String {
        val lower = userMsg.lowercase()
        val explanation = when {
            lower.contains("ことにしている") || lower.contains("ことにしています") -> {
                "Về cấu trúc 『〜ことにしている／ことにしています』:\n" +
                "• Ý nghĩa: Biểu thị một thói quen hoặc quy tắc do chính bản thân người nói tự quyết định và đang duy trì đều đặn.\n" +
                "• Công thức nối: V-ru / V-nai + ことにしている\n" +
                "• Ví dụ minh họa: 健康のために、毎朝ジョギングをすることにしています (Vì sức khỏe, tôi duy trì thói quen chạy bộ mỗi sáng)."
            }
            lower.contains("わけだ") || lower.contains("わけではない") -> {
                "Về mẫu câu 『〜わけだ』 & 『〜わけではない』:\n" +
                "• 『〜わけだ』: Đương nhiên là, thảo nào mà... (rút ra kết luận hợp lý từ thực tế hoặc nguyên nhân đã biết).\n" +
                "• 『〜わけではない』: Không hẳn là, không có nghĩa là... (phủ định một phần nhận định)."
            }
            lower.contains("はずだ") -> {
                "Về mẫu câu 『〜はずだ』:\n" +
                "• Ý nghĩa: Chắc chắn là, thảo nào... (thể hiện sự phán đoán có căn cứ xác thực của người nói).\n" +
                "• Chú ý: Không dùng khi nói về hành động cố ý của chính bản thân người nói trong tương lai."
            }
            lower.contains("từ vựng") || lower.contains("kanji") -> {
                "Kho tài liệu N3 của chúng ta có 1,138 từ vựng và hệ thống chữ Hán trọng tâm. Bạn có thể mở mục 'Kho 3 Mục Học' ở màn hình Lộ trình để tra cứu theo từng bài học kèm âm đọc Hiragana và ví dụ cụ thể nhé!"
            }
            lower.contains("lịch thi") || lower.contains("jlpt") -> {
                "Kỳ thi năng lực Nhật ngữ JLPT thường được tổ chức vào Chủ Nhật đầu tiên của tháng 7 và tháng 12 hàng năm. Hãy duy trì ôn tập 10-15 câu mỗi ngày theo thuật toán SM-2 để đảm bảo điểm số tối ưu nhé!"
            }
            persona == SenseiPersona.KAIWA_AOI -> {
                "いいね！Aoi đây! Tiếng Nhật của bạn rất thú vị đó! Khi trò chuyện hàng ngày ở Tokyo, chúng mình thường dùng các câu ngắn gọn, tự nhiên. Bạn muốn cùng mình luyện thêm về chủ đề đời sống nào tiếp theo?"
            }
            else -> {
                "Thầy Akira đã nhận được câu hỏi của em: \"$userMsg\". Trong chương trình JLPT N3, việc nắm vững cấu trúc kết hợp động từ và hiểu rõ sắc thái văn cảnh của từng mẫu câu là chìa khóa then chốt để đạt điểm cao."
            }
        }

        return if (isAuthIssue) {
            explanation + "\n\n⚠️ Lưu ý hệ thống: Hiện tại ứng dụng đang trả lời ở chế độ Ngoại Tuyến (Offline) do Gemini API Key chưa được kích hoạt hoặc đã hết hạn. Bạn có thể nhấn vào biểu tượng ⚙️ (Cài đặt) ở góc phải phía trên để nạp API Key mới từ aistudio.google.com."
        } else {
            explanation
        }
    }

    // ==========================================
    // CORE REST API ENGINE (Dùng chung duy nhất)
    // ==========================================

    private fun executeSinglePrompt(
        systemInstruction: String,
        userPrompt: String,
        modelName: String = DEFAULT_GEMINI_MODEL
    ): String {
        val contentsArray = JSONArray().apply {
            val contentObj = JSONObject().apply {
                put("role", "user")
                val partsArray = JSONArray().apply {
                    put(JSONObject().put("text", userPrompt))
                }
                put("parts", partsArray)
            }
            put(contentObj)
        }

        val jsonResponse = callGeminiRestEndpoint(
            modelName = modelName,
            systemInstruction = systemInstruction,
            contentsArray = contentsArray,
            toolsArray = null,
            temperature = 0.7,
            maxOutputTokens = 1024
        )

        val root = JSONObject(jsonResponse)
        val candidates = root.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
        val content = firstCandidate?.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val text = parts?.optJSONObject(0)?.optString("text")

        if (text.isNullOrBlank()) {
            throw IllegalStateException("Mô hình Gemini không trả về dữ liệu văn bản.")
        }
        return text
    }

    private fun executeChatCall(
        modelName: String,
        history: List<ChatMessage>,
        newUserMessage: String,
        persona: SenseiPersona,
        enableSearchGrounding: Boolean
    ): GeminiChatResponse {
        val contentsArray = JSONArray()

        for (msg in history.filter { !it.isError }) {
            val roleStr = when (msg.role) {
                ChatRole.USER -> "user"
                ChatRole.MODEL -> "model"
                ChatRole.SYSTEM -> continue
            }
            val contentObj = JSONObject().apply {
                put("role", roleStr)
                val parts = JSONArray().apply {
                    put(JSONObject().put("text", msg.text))
                }
                put("parts", parts)
            }
            contentsArray.put(contentObj)
        }

        val newContentObj = JSONObject().apply {
            put("role", "user")
            val parts = JSONArray().apply {
                put(JSONObject().put("text", newUserMessage))
            }
            put("parts", parts)
        }
        contentsArray.put(newContentObj)

        val toolsArray = if (enableSearchGrounding) {
            JSONArray().apply {
                put(JSONObject().apply {
                    put("google_search", JSONObject())
                })
            }
        } else null

        val jsonResponse = callGeminiRestEndpoint(
            modelName = modelName,
            systemInstruction = persona.systemInstruction,
            contentsArray = contentsArray,
            toolsArray = toolsArray,
            temperature = 0.7,
            maxOutputTokens = 2048
        )

        val root = JSONObject(jsonResponse)
        val candidates = root.optJSONArray("candidates")
        val firstCandidate = candidates?.optJSONObject(0)
            ?: throw IllegalStateException("Không nhận được câu trả lời từ mô hình.")

        val content = firstCandidate.optJSONObject("content")
        val parts = content?.optJSONArray("parts")
        val replyText = parts?.optJSONObject(0)?.optString("text")
            ?: throw IllegalStateException("Nội dung phản hồi trống.")

        val searchQueries = mutableListOf<String>()
        val groundingSources = mutableListOf<GroundingSource>()

        val groundingMetadata = firstCandidate.optJSONObject("grounding_metadata")
            ?: firstCandidate.optJSONObject("groundingMetadata")

        if (groundingMetadata != null) {
            val webQueries = groundingMetadata.optJSONArray("webSearchQueries")
            if (webQueries != null) {
                for (i in 0 until webQueries.length()) {
                    val query = webQueries.optString(i)
                    if (query.isNotBlank()) searchQueries.add(query)
                }
            }

            val chunks = groundingMetadata.optJSONArray("groundingChunks")
            if (chunks != null) {
                for (i in 0 until chunks.length()) {
                    val chunkObj = chunks.optJSONObject(i)
                    val webObj = chunkObj?.optJSONObject("web")
                    if (webObj != null) {
                        val title = webObj.optString("title", "Nguồn Google")
                        val uri = webObj.optString("uri", "")
                        if (uri.isNotBlank()) {
                            groundingSources.add(GroundingSource(title = title, url = uri))
                        }
                    }
                }
            }
        }

        return GeminiChatResponse(
            replyText = replyText,
            modelUsed = modelName,
            searchQueries = searchQueries.distinct(),
            groundingSources = groundingSources.distinctBy { it.url }
        )
    }

    /**
     * Gọi HTTP POST duy nhất đến Gemini REST API:
     * - Header x-goog-api-key chứa API key (không đưa vào URL query parameter)
     * - URL sạch: BASE_URL/$modelName:generateContent
     */
    private fun callGeminiRestEndpoint(
        modelName: String,
        systemInstruction: String,
        contentsArray: JSONArray,
        toolsArray: JSONArray?,
        temperature: Double,
        maxOutputTokens: Int
    ): String {
        val apiKey = getApiKey()
        if (apiKey.isBlank()) {
            throw IllegalStateException("API_KEY_MISSING: Chưa cấu hình GEMINI_API_KEY. Vui lòng thiết lập key trong file .env hoặc tab Lộ trình.")
        }

        val urlString = "$BASE_URL/$modelName:generateContent"
        val url = URL(urlString)
        val conn = url.openConnection() as HttpURLConnection
        conn.requestMethod = "POST"
        conn.setRequestProperty("Content-Type", "application/json; charset=utf-8")
        conn.setRequestProperty("x-goog-api-key", apiKey)
        conn.doOutput = true
        conn.connectTimeout = 20000
        conn.readTimeout = 40000

        val payload = JSONObject().apply {
            if (systemInstruction.isNotBlank()) {
                val sysInstructionObj = JSONObject().apply {
                    val partsArray = JSONArray().apply {
                        put(JSONObject().put("text", systemInstruction))
                    }
                    put("parts", partsArray)
                }
                put("system_instruction", sysInstructionObj)
            }

            put("contents", contentsArray)

            if (toolsArray != null && toolsArray.length() > 0) {
                put("tools", toolsArray)
            }

            val generationConfig = JSONObject().apply {
                put("temperature", temperature)
                put("maxOutputTokens", maxOutputTokens)
            }
            put("generationConfig", generationConfig)
        }

        OutputStreamWriter(conn.outputStream, "UTF-8").use { writer ->
            writer.write(payload.toString())
            writer.flush()
        }

        val responseCode = conn.responseCode
        if (responseCode != HttpURLConnection.HTTP_OK) {
            val errorStream = conn.errorStream?.let { BufferedReader(InputStreamReader(it)).readText() } ?: ""
            if (responseCode == 401 || responseCode == 403 || errorStream.contains("API key not valid", ignoreCase = true) || errorStream.contains("UNAUTHENTICATED", ignoreCase = true)) {
                throw IllegalStateException("API_KEY_INVALID: API Key Google AI Studio chưa hợp lệ hoặc đã bị khóa/hết hạn (Mã 401). Vui lòng cập nhật key mới tại aistudio.google.com.")
            }
            throw IllegalStateException("Google AI Studio HTTP $responseCode: $errorStream")
        }

        return conn.inputStream.bufferedReader().use { it.readText() }
    }
}
