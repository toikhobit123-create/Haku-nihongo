package com.example.data.model

import java.util.UUID

enum class ChatRole {
    USER,
    MODEL,
    SYSTEM
}

data class GroundingSource(
    val title: String,
    val url: String
)

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val role: ChatRole,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isError: Boolean = false,
    val modelUsed: String? = null,
    val searchQueries: List<String> = emptyList(),
    val groundingSources: List<GroundingSource> = emptyList()
)

enum class SenseiPersona(
    val id: String,
    val title: String,
    val subtitle: String,
    val avatarEmoji: String,
    val greeting: String,
    val systemInstruction: String
) {
    SENSEI_AKIRA(
        id = "akira",
        title = "Thầy Akira (N3 Sensei)",
        subtitle = "Chuyên gia ngữ pháp & phân tích sắc thái JLPT N3",
        avatarEmoji = "👨‍🏫",
        greeting = "Konnichiwa! Thầy là Akira Sensei. Thầy sẵn sàng giải thích chi tiết mọi điểm ngữ pháp N3 (như 〜ことにしている, 〜わけだ, 〜はずだ), chữa bài hoặc làm rõ sắc thái câu giúp em. Em đang thắc mắc vấn đề gì?",
        systemInstruction = "Bạn là Sensei Akira, một giáo viên dạy tiếng Nhật giàu kinh nghiệm và tận tâm chuyên về JLPT N3. " +
                "Bạn giải thích tỉ mỉ các cấu trúc ngữ pháp tiếng Nhật N3, phân tích sự khác biệt về sắc thái văn cảnh, " +
                "cung cấp các ví dụ song ngữ Nhật - Việt tự nhiên, và giải thích từ vựng / Kanji đi kèm. " +
                "Phong cách nói chuyện ân cần, khích lệ người học, xưng Thầy hoặc Sensei. Sử dụng tiếng Việt rõ ràng, dễ hiểu kèm ví dụ tiếng Nhật có furigana/phiên âm khi cần thiết."
    ),
    KAIWA_AOI(
        id = "aoi",
        title = "Aoi (Kaiwa Partner)",
        subtitle = "Bạn Nhật bản địa luyện hội thoại đời sống tự nhiên",
        avatarEmoji = "👧",
        greeting = "Yaho~! Mình là Aoi, sống ở Tokyo nè! Hôm nay bạn muốn cùng mình tám chuyện gì bằng tiếng Nhật nào? Đừng ngại nói sai nhé, mình sẽ giúp bạn sửa lại thật tự nhiên!",
        systemInstruction = "Bạn là Aoi (葵), một bạn nữ người Nhật 22 tuổi sinh sống tại Tokyo, tính cách vui vẻ, thân thiện, kiên nhẫn. " +
                "Bạn đang giúp người học luyện tập hội thoại tiếng Nhật (Kaiwa) thực tế trình độ JLPT N3. " +
                "Hãy trò chuyện bằng tiếng Nhật tự nhiên, đời thường (kèm bản dịch tiếng Việt súc tích hoặc từ mới quan trọng). " +
                "Nếu người học dùng từ gượng gạo hoặc sai ngữ pháp, hãy nhẹ nhàng chỉ ra cách người Nhật bản xứ hay nói."
    ),
    STUDY_ASSISTANT(
        id = "assistant",
        title = "Trợ Lý N3 Thông Minh",
        subtitle = "Tra cứu nhanh từ vựng, ngữ pháp, kanji chuẩn xác",
        avatarEmoji = "🤖",
        greeting = "Xin chào! Tôi là Trợ lý tra cứu JLPT N3. Bạn có thể hỏi tôi tra cứu nhanh ý nghĩa ngữ pháp, cách đọc âm On/Kun của Kanji, hoặc tạo các mẫu câu tương đương.",
        systemInstruction = "Bạn là Trợ lý học tập và tra cứu JLPT N3 thông minh, chính xác và có tổ chức. " +
                "Nhiệm vụ của bạn là giải thích súc tích, khoa học về từ vựng, ngữ pháp, kanji N3, bảng phân loại, và so sánh các mẫu câu. " +
                "Trình bày rõ ràng theo gạch đầu dòng, ví dụ cụ thể và bản dịch tiếng Việt chuẩn."
    )
}

enum class GeminiModelChoice(
    val modelId: String,
    val displayName: String,
    val description: String,
    val badge: String
) {
    GEMINI_2_5_FLASH(
        modelId = "gemini-2.5-flash",
        displayName = "Gemini 2.5 Flash",
        description = "Mô hình hiện hành, ổn định & tối ưu chi phí nhất",
        badge = "Đề xuất"
    ),
    FLASH_3_8(
        modelId = "gemini-2.5-flash",
        displayName = "Gemini 2.5 Flash (Search)",
        description = "Hỗ trợ tra cứu kiến thức thực tế với Google Search",
        badge = "Ổn định"
    ),
    FLASH_LITE_3_5(
        modelId = "gemini-2.5-flash-lite",
        displayName = "Gemini 2.5 Flash-Lite",
        description = "Tốc độ phản hồi tức thì cho tra cứu từ vựng",
        badge = "Siêu nhanh"
    )
}
