# Haku N3 (Nihongo Quest) 🥋🇯🇵
**Ứng Dụng Học Tiếng Nhật JLPT N3 Gamified Thông Minh**

Haku N3 là ứng dụng học tiếng Nhật trình độ JLPT N3 toàn diện với phong cách Gamification 3D hiện đại (Gamified Tactile UI, font chữ Plus Jakarta Sans). Ứng dụng tích hợp thuật toán lặp lại ngắt quãng **SuperMemo-2 (SM-2)**, hệ thống bài tập quy nạp đa dạng, bảng điều khiển thống kê tiến độ với biểu đồ trực quan, và **Trợ lý AI (Gemini 2.5 Flash)** đồng hành giải thích lỗi sai & luyện tập giao tiếp.

---

## 🌟 Tính Năng Nổi Bật

### 1. Lộ Trình Học Tập (Pathway & Study Hub)
- **134 Điểm Ngữ Pháp N3**: Phân loại theo bài học, cấu trúc kết nối, ý nghĩa chi tiết và các câu ví dụ song ngữ Nhật - Việt kèm phát âm giọng bản xứ (TTS).
- **1,138 Từ Vựng N3 (23 Bài Học)**: Phân bố chuẩn theo cấp độ JLPT N3, đầy đủ Kanji, Hiragana, âm Hán Việt và nghĩa tiếng Việt.
- **Kanji N3**: Danh sách các chữ Hán trọng điểm trình độ N3.

### 2. Hệ Thống Ôn Tập SRS (Spaced Repetition - Thuật Toán SM-2)
- Hỗ trợ Flashcard 3D xoay lật mặt trước/sau mượt mà cho cả Từ vựng, Kanji và Ngữ pháp.
- 4 Nút đánh giá chuẩn SM-2: **Quên (Again)**, **Khó (Hard)**, **Tốt (Good)**, **Dễ (Easy)**.
- Tự động tính toán chu kỳ nhắc lại (Interval), số lần lặp (Repetition), và hệ số ghi nhớ (Ease Factor).
- **Hoạt động 100% Offline**: Lưu trữ dữ liệu an toàn trên bộ nhớ thiết bị (`SharedPreferences`), học mọi lúc mọi nơi không cần kết nối mạng.

### 3. Quiz Engine 4 Dạng Bài Tập Đa Dạng
- **Multiple Choice**: Trắc nghiệm 4 đáp án 3D trực quan kèm âm thanh phát âm.
- **Fill-in-the-blank**: Điền từ/ngữ pháp khuyết vào câu tiếng Nhật 【 ＿＿＿ 】.
- **Sentence Builder (Ghép câu 3D)**: Khay xếp thẻ từ 3D tương tác, hỗ trợ hoàn tác và đổi thứ tự từ linh hoạt.
- **Translation (Dịch 2 chiều)**: Dịch câu hai chiều 🇯🇵 Nhật ↔ 🇻🇳 Việt, phân tích sắc thái ngữ nghĩa tinh tế.

### 4. Trợ Lý AI (Gemini 2.5 Flash) Sư Phạm
- **Phân tích lỗi sai tức thì**: Khi trả lời sai bất kỳ dạng bài tập nào, khung báo lỗi Danger Red xuất hiện dưới đáy màn hình. Trợ lý AI (model `gemini-2.5-flash`) tự động phân tích lý do sai về mặt sắc thái, chỉ rõ quy tắc cốt lõi và mẹo phân biệt bằng tiếng Việt tự nhiên (không dùng dịch máy thô).
- **Sensei Akira & Hội thoại Kaiwa**: Chế độ trò chuyện đa lượt (Multi-turn chat) với các persona giáo viên chuyên sâu, hỗ trợ tra cứu thông tin thực tế với Google Search Grounding.
- **Bảo mật API Key**: Truyền khóa an toàn qua header `x-goog-api-key`, không đưa khóa vào query string URL.

### 5. Bảng Tiến Độ Dashboard & Phiên Học Ngắn (5-10 Phút)
- **Phiên học ngắn (Micro-learning)**: Mỗi lượt học/ôn tập kéo dài 10 - 15 câu (khoảng 5-10 phút) giúp tối ưu thời gian và chống quá tải trí nhớ.
- **Biểu đồ tiến độ (Compose Canvas)**: Đường cong Bézier spline mượt mà với dải gradient màu thể hiện số lượng Kanji, Ngữ pháp và Từ vựng đạt cấp độ thuần thục qua 7 ngày gần nhất.
- **Chuỗi ngày học (Streak) & Tỷ lệ chính xác (%)**: Theo dõi sự chăm chỉ và độ chuẩn xác trong việc trả lời.
- **Mục Điểm Yếu (Weak Points)**: Tự động gom nhóm các từ vựng và câu hỏi hay bị quên nhất kèm nút **"Ôn tập điểm yếu"** riêng biệt.

---

## 🛠️ Hướng Dẫn Cài Đặt & Chạy Ứng Dụng

### Yêu Cầu Môi Trường
- **Android Studio**: Android Studio Ladybug / Koala hoặc Google AI Studio.
- **JDK**: Java 11 hoặc Java 17.
- **Android SDK**: `minSdk: 24`, `targetSdk: 36`, `compileSdk: 36`.

### Bước 1: Cấu Hình API Key (.env)
1. Tạo file `.env` tại thư mục gốc của dự án (copy từ `.env.example`):
   ```bash
   cp .env.example .env
   ```
2. Mở file `.env` và điền API Key của bạn lấy từ [Google AI Studio](https://aistudio.google.com/):
   ```properties
   GEMINI_API_KEY=AIzaSyYourActualApiKeyHere
   ```
3. *Lưu ý bảo mật:* File `.env` đã được cấu hình trong `.gitignore` để bảo vệ mã khóa của bạn không bị commit lên GitHub. Secrets Gradle Plugin sẽ tự động nạp key vào `BuildConfig.GEMINI_API_KEY` khi biên dịch.

### Bước 2: Build & Chạy Ứng Dụng
1. Mở dự án trong Android Studio.
2. Nhấn **Sync Project with Gradle Files** để Gradle tải các thư viện phụ thuộc.
3. Chọn thiết bị Emulator hoặc máy thật Android (Android 7.0 trở lên) và nhấn nút **Run 'app'** (Shift + F10).

### ⚠️ Lưu ý về Signing Config (`signingConfig debugConfig`):
- Trong môi trường **Google AI Studio**, cấu hình `signingConfig = signingConfigs.getByName("debugConfig")` trong file `app/build.gradle.kts` là **bắt buộc** để nền tảng ký và cài đặt APK lên Streaming Emulator.
- Nếu bạn tải source code về máy tính cá nhân để build offline trong Android Studio và gặp lỗi thiếu file `debug.keystore`, bạn có thể mở `app/build.gradle.kts`, tìm khối `buildTypes { debug { ... } }` và tạm thời xóa hoặc chú thích (comment out) dòng:
  ```kotlin
  // signingConfig = signingConfigs.getByName("debugConfig")
  ```

---

## 📂 Cấu Trúc Thư Mục Chính

```
├── .env.example               # File mẫu cấu hình biến môi trường và placeholder GEMINI_API_KEY
├── .env                       # File cấu hình khóa API thực tế (nằm trong .gitignore)
├── app/
│   ├── build.gradle.kts       # Cấu hình Gradle module app, Secrets Plugin, signingConfigs
│   └── src/
│       ├── main/
│       │   ├── java/com/example/
│       │   │   ├── MainActivity.kt                  # Activity chính, điều hướng Scaffolding
│       │   │   ├── data/
│       │   │   │   ├── model/                       # Data models (Nihongo, SRS, Quiz, Chat)
│       │   │   │   ├── repository/                  # Repositories (Data N3, SRS Cache, Quiz Repo)
│       │   │   │   ├── service/
│       │   │   │   │   └── AiService.kt             # Service hợp nhất gọi Gemini REST API (gemini-2.5-flash)
│       │   │   │   └── srs/
│       │   │   │       └── Sm2Algorithm.kt          # Thuật toán Spaced Repetition SuperMemo-2
│       │   │   ├── ui/
│       │   │   │   ├── components/                  # Gamified 3D Buttons, Cards, Bottom Navigation
│       │   │   │   ├── screens/                     # HomeScreen, SrsReviewScreen, ProfileStatsScreen,
│       │   │   │   │                                # InductiveLearningScreen, StudyHubScreen, AiChatScreen
│       │   │   │   ├── state/                       # ViewModels & UiStates (NihongoViewModel, AiChatViewModel)
│       │   │   │   └── theme/                       # Bảng màu Gamification M3 (#58CC02, #1CB0F6, #FF4B4B)
│       │   └── res/                             # Resource strings, drawables, fonts
│       └── test/java/com/example/               # Robolectric & JUnit test cases
```
