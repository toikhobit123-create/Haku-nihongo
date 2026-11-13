package com.example.ui.state

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.model.ChatMessage
import com.example.data.model.ChatRole
import com.example.data.model.GeminiModelChoice
import com.example.data.model.SenseiPersona
import com.example.data.service.AiService
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AiChatUiState(
    val messages: List<ChatMessage> = emptyList(),
    val currentPersona: SenseiPersona = SenseiPersona.SENSEI_AKIRA,
    val selectedModel: GeminiModelChoice = GeminiModelChoice.GEMINI_2_5_FLASH,
    val isSearchGroundingEnabled: Boolean = false,
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val inputText: String = "",
    val suggestedPrompts: List<String> = listOf(
        "Giải thích chi tiết ngữ pháp 〜ことにしています kèm ví dụ N3",
        "Phân biệt sắc thái giữa 〜わけだ và 〜はずだ",
        "Lịch và thông tin kỳ thi JLPT N3 mới nhất năm nay",
        "Luyện hội thoại Kaiwa khi gọi món ở quán ăn Nhật",
        "Cho mình 5 từ vựng N3 xuất hiện nhiều nhất trong đời sống"
    )
)

class AiChatViewModel(application: Application) : AndroidViewModel(application) {

    private val _uiState = MutableStateFlow(AiChatUiState())
    val uiState: StateFlow<AiChatUiState> = _uiState.asStateFlow()

    init {
        // Khởi tạo lời chào mở đầu từ Sensei Akira
        resetChatWithGreeting(SenseiPersona.SENSEI_AKIRA)
    }

    fun onInputTextChanged(text: String) {
        _uiState.update { it.copy(inputText = text, errorMessage = null) }
    }

    fun setPersona(persona: SenseiPersona) {
        if (_uiState.value.currentPersona == persona) return
        _uiState.update { it.copy(currentPersona = persona) }
        resetChatWithGreeting(persona)
    }

    fun setModel(model: GeminiModelChoice) {
        _uiState.update { it.copy(selectedModel = model) }
    }

    fun toggleSearchGrounding(enabled: Boolean) {
        _uiState.update { it.copy(isSearchGroundingEnabled = enabled) }
    }

    fun clearChat() {
        resetChatWithGreeting(_uiState.value.currentPersona)
    }

    fun sendMessage(presetText: String? = null) {
        val textToSend = (presetText ?: _uiState.value.inputText).trim()
        if (textToSend.isBlank() || _uiState.value.isLoading) return

        val userMessage = ChatMessage(
            role = ChatRole.USER,
            text = textToSend
        )

        // Cập nhật giao diện với tin nhắn của user
        val updatedHistory = _uiState.value.messages + userMessage
        _uiState.update {
            it.copy(
                messages = updatedHistory,
                inputText = if (presetText == null) "" else it.inputText,
                isLoading = true,
                errorMessage = null
            )
        }

        viewModelScope.launch {
            try {
                val currentPersona = _uiState.value.currentPersona
                val currentModel = _uiState.value.selectedModel
                val searchGrounding = _uiState.value.isSearchGroundingEnabled

                val result = AiService.sendChatMessage(
                    history = updatedHistory,
                    newUserMessage = textToSend,
                    persona = currentPersona,
                    modelChoice = currentModel,
                    enableSearchGrounding = searchGrounding
                )

                result.onSuccess { response ->
                    val botMessage = ChatMessage(
                        role = ChatRole.MODEL,
                        text = response.replyText,
                        modelUsed = response.modelUsed,
                        searchQueries = response.searchQueries,
                        groundingSources = response.groundingSources
                    )
                    _uiState.update {
                        it.copy(
                            messages = it.messages + botMessage,
                            isLoading = false,
                            errorMessage = null
                        )
                    }
                }.onFailure { err ->
                    Log.e("AiChatViewModel", "Lỗi gửi tin nhắn", err)
                    val errorMsg = err.localizedMessage ?: "Không thể kết nối với Gemini API."
                    val errorBubble = ChatMessage(
                        role = ChatRole.MODEL,
                        text = "⚠️ $errorMsg\nVui lòng thử lại hoặc chuyển đổi mô hình.",
                        isError = true
                    )
                    _uiState.update {
                        it.copy(
                            messages = it.messages + errorBubble,
                            isLoading = false,
                            errorMessage = errorMsg
                        )
                    }
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                Log.e("AiChatViewModel", "Ngoại lệ khi chat", e)
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        errorMessage = e.localizedMessage ?: "Có lỗi xảy ra."
                    )
                }
            }
        }
    }

    fun retryLastMessage() {
        val lastUserMessage = _uiState.value.messages.lastOrNull { it.role == ChatRole.USER }
        if (lastUserMessage != null) {
            // Xóa tin nhắn lỗi cuối cùng nếu có
            _uiState.update { state ->
                val filtered = state.messages.filterNot { it.isError }
                state.copy(messages = filtered)
            }
            sendMessage(lastUserMessage.text)
        }
    }

    private fun resetChatWithGreeting(persona: SenseiPersona) {
        val greetingMessage = ChatMessage(
            role = ChatRole.MODEL,
            text = "${persona.avatarEmoji} ${persona.greeting}",
            modelUsed = _uiState.value.selectedModel.modelId
        )
        _uiState.update {
            it.copy(
                messages = listOf(greetingMessage),
                isLoading = false,
                errorMessage = null
            )
        }
    }
}
