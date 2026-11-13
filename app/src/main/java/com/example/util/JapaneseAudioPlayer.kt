package com.example.util

import android.content.Context
import android.media.AudioManager
import android.media.ToneGenerator
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.util.Log
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Stop
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.GamifyBlue
import com.example.ui.theme.GamifyBlueDark
import com.example.ui.theme.GamifyBlueLight
import com.example.ui.theme.GamifyGreen
import com.example.ui.theme.GamifyGreenDark
import com.example.ui.theme.GamifyGreenLight
import com.example.ui.theme.NeutralBorder
import com.example.ui.theme.NeutralCard
import com.example.ui.theme.NeutralSubtext
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

/**
 * Trình quản lý âm thanh đọc tiếng Nhật (Japanese TTS) và hiệu ứng âm thanh tương tác.
 * Sử dụng native Android TextToSpeech với Locale.JAPANESE.
 */
class JapaneseTtsManager(context: Context) {

    private val appContext = context.applicationContext
    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _currentText = MutableStateFlow<String?>(null)
    val currentText: StateFlow<String?> = _currentText.asStateFlow()

    private var toneGenerator: ToneGenerator? = null

    init {
        try {
            toneGenerator = ToneGenerator(AudioManager.STREAM_MUSIC, 60)
        } catch (e: Exception) {
            Log.w("JapaneseTtsManager", "Could not initialize ToneGenerator: ${e.message}")
        }

        try {
            tts = TextToSpeech(appContext) { status ->
                if (status == TextToSpeech.SUCCESS) {
                    val result = tts?.setLanguage(Locale.JAPANESE)
                    if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                        Log.w("JapaneseTtsManager", "Japanese locale not supported by default engine, trying Locale.JAPAN")
                        tts?.language = Locale.JAPAN
                    }
                    tts?.setSpeechRate(0.85f) // Tốc độ 0.85x chuẩn cho người học tiếng Nhật
                    tts?.setPitch(1.0f)

                    tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
                        override fun onStart(utteranceId: String?) {
                            _isSpeaking.value = true
                        }

                        override fun onDone(utteranceId: String?) {
                            _isSpeaking.value = false
                            _currentText.value = null
                        }

                        @Deprecated("Deprecated in Java")
                        override fun onError(utteranceId: String?) {
                            _isSpeaking.value = false
                            _currentText.value = null
                        }

                        override fun onError(utteranceId: String?, errorCode: Int) {
                            _isSpeaking.value = false
                            _currentText.value = null
                        }
                    })
                    isTtsReady = true
                    Log.d("JapaneseTtsManager", "Japanese TTS Initialized successfully")
                } else {
                    Log.e("JapaneseTtsManager", "Failed to initialize TextToSpeech status: $status")
                }
            }
        } catch (e: Exception) {
            Log.e("JapaneseTtsManager", "Exception initializing TextToSpeech: ${e.message}")
        }
    }

    /**
     * Làm sạch văn bản trước khi đọc:
     * - Loại bỏ gạch chân '＿＿＿'
     * - Loại bỏ chú thích tiếng Việt trong ngoặc nếu đọc nguyên câu
     */
    private fun sanitizeJapaneseText(text: String): String {
        return text
            .replace("＿＿＿", "")
            .replace("...", "")
            .replace("…", "")
            .trim()
    }

    /**
     * Phát âm đoạn văn bản tiếng Nhật
     * @param text Văn bản tiếng Nhật cần đọc
     * @param speechRate Tốc độ nói (mặc định 0.85f cho luyện nghe N3)
     */
    fun speak(text: String, speechRate: Float = 0.85f) {
        val clean = sanitizeJapaneseText(text)
        if (clean.isBlank()) return

        try {
            _currentText.value = clean
            _isSpeaking.value = true
            tts?.setSpeechRate(speechRate)
            val utteranceId = "jp_tts_${System.currentTimeMillis()}"
            tts?.speak(clean, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
        } catch (e: Exception) {
            Log.e("JapaneseTtsManager", "Error speaking text: ${e.message}")
            _isSpeaking.value = false
            _currentText.value = null
        }
    }

    /**
     * Dừng phát âm hiện tại
     */
    fun stop() {
        try {
            tts?.stop()
        } catch (e: Exception) {
            Log.e("JapaneseTtsManager", "Error stopping TTS: ${e.message}")
        }
        _isSpeaking.value = false
        _currentText.value = null
    }

    /**
     * Phát hiệu ứng âm thanh khi trả lời đúng
     */
    fun playSuccessSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP2, 180)
        } catch (e: Exception) {
            Log.w("JapaneseTtsManager", "Tone error: ${e.message}")
        }
    }

    /**
     * Phát hiệu ứng âm thanh khi trả lời sai / boss tấn công
     */
    fun playErrorSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_NACK, 220)
        } catch (e: Exception) {
            Log.w("JapaneseTtsManager", "Tone error: ${e.message}")
        }
    }

    /**
     * Phát hiệu ứng âm thanh khi gây sát thương cho Boss
     */
    fun playHitSound() {
        try {
            toneGenerator?.startTone(ToneGenerator.TONE_PROP_BEEP, 120)
        } catch (e: Exception) {
            Log.w("JapaneseTtsManager", "Tone error: ${e.message}")
        }
    }

    /**
     * Giải phóng tài nguyên khi Activity kết thúc
     */
    fun shutdown() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            Log.e("JapaneseTtsManager", "Error shutting down TTS: ${e.message}")
        }
        try {
            toneGenerator?.release()
        } catch (e: Exception) {
            Log.w("JapaneseTtsManager", "Error releasing toneGenerator: ${e.message}")
        }
        tts = null
        toneGenerator = null
        isTtsReady = false
        _isSpeaking.value = false
        _currentText.value = null
    }
}

/**
 * CompositionLocal cho JapaneseTtsManager để các màn hình Compose dễ dàng truy cập
 */
val LocalJapaneseTts = compositionLocalOf<JapaneseTtsManager> {
    error("No JapaneseTtsManager provided in LocalJapaneseTts")
}

/**
 * Nút bấm biểu tượng loa phát âm tiếng Nhật chuẩn gamification phong cách Duolingo.
 */
@Composable
fun JapaneseAudioSpeakerButton(
    textToSpeak: String,
    modifier: Modifier = Modifier,
    size: Dp = 42.dp,
    iconSize: Dp = 22.dp,
    primaryColor: Color = GamifyGreen,
    backgroundColor: Color = GamifyGreenLight,
    testTag: String = "btn_japanese_audio_speaker"
) {
    val tts = LocalJapaneseTts.current
    var isSpeakingThis by remember { mutableStateOf(false) }

    // Kiểm tra xem đoạn văn bản này có đang được phát không
    DisposableEffect(tts.currentText.value) {
        val current = tts.currentText.value
        isSpeakingThis = current != null && (current == textToSpeak || textToSpeak.contains(current))
        onDispose { }
    }

    val infiniteTransition = rememberInfiniteTransition(label = "audio_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(450),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    val scaleModifier = if (isSpeakingThis) Modifier.scale(pulseScale) else Modifier

    Box(
        modifier = modifier
            .size(size)
            .then(scaleModifier)
            .clip(CircleShape)
            .background(if (isSpeakingThis) primaryColor else backgroundColor)
            .border(
                width = 2.dp,
                color = if (isSpeakingThis) primaryColor else primaryColor.copy(alpha = 0.5f),
                shape = CircleShape
            )
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                if (isSpeakingThis) {
                    tts.stop()
                    isSpeakingThis = false
                } else {
                    tts.speak(textToSpeak)
                    isSpeakingThis = true
                }
            }
            .testTag(testTag),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = if (isSpeakingThis) Icons.Default.Stop else Icons.AutoMirrored.Filled.VolumeUp,
            contentDescription = "Phát âm tiếng Nhật: $textToSpeak",
            tint = if (isSpeakingThis) Color.White else primaryColor,
            modifier = Modifier.size(iconSize)
        )
    }
}
