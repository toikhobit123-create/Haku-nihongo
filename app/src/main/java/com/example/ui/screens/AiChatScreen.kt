package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.DeleteSweep
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.model.ChatMessage
import com.example.data.model.ChatRole
import com.example.data.model.GeminiModelChoice
import com.example.data.model.GroundingSource
import com.example.data.model.SenseiPersona
import com.example.ui.components.Gamified3DButton
import com.example.ui.components.Gamified3DCard
import com.example.ui.state.AiChatViewModel
import com.example.ui.state.Screen
import com.example.ui.theme.GamifyBlue
import com.example.ui.theme.GamifyBlueDark
import com.example.ui.theme.GamifyBlueLight
import com.example.ui.theme.GamifyBlueShadow
import com.example.ui.theme.GamifyGreen
import com.example.ui.theme.GamifyGreenDark
import com.example.ui.theme.GamifyGreenLight
import com.example.ui.theme.GamifyGreenShadow
import com.example.ui.theme.GamifyRed
import com.example.ui.theme.GamifyRedLight
import com.example.ui.theme.GamifyYellow
import com.example.ui.theme.GamifyYellowShadow
import com.example.ui.theme.NeutralBorder
import com.example.ui.theme.NeutralCard
import com.example.ui.theme.NeutralSubtext
import com.example.ui.theme.NeutralText
import com.example.ui.theme.NeutralWhite
import com.example.ui.theme.RoundedFull
import com.example.ui.theme.RoundedLg
import com.example.ui.theme.RoundedMd
import com.example.ui.theme.RoundedSm
import com.example.util.JapaneseAudioSpeakerButton
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun AiChatScreen(
    viewModel: AiChatViewModel,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onNavigateBack()
    }

    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val listState = rememberLazyListState()
    var isControlPanelExpanded by remember { mutableStateOf(false) }

    // Tự động cuộn xuống tin nhắn mới nhất
    LaunchedEffect(uiState.messages.size, uiState.isLoading) {
        if (uiState.messages.isNotEmpty()) {
            listState.animateScrollToItem(uiState.messages.size - 1)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(Color(0xFFF9FAFB))
            .imePadding()
    ) {
        // 1. Header Bar
        AiChatHeader(
            currentPersona = uiState.currentPersona,
            selectedModel = uiState.selectedModel,
            isSearchGroundingEnabled = uiState.isSearchGroundingEnabled,
            isPanelExpanded = isControlPanelExpanded,
            onTogglePanel = { isControlPanelExpanded = !isControlPanelExpanded },
            onNavigateBack = onNavigateBack,
            onClearChat = { viewModel.clearChat() }
        )

        // 2. Control Panel (Persona + Model + Google Search Grounding toggle)
        AnimatedVisibility(
            visible = isControlPanelExpanded,
            enter = fadeIn(),
            exit = fadeOut()
        ) {
            AiChatControlPanel(
                currentPersona = uiState.currentPersona,
                selectedModel = uiState.selectedModel,
                isSearchGroundingEnabled = uiState.isSearchGroundingEnabled,
                onSelectPersona = { viewModel.setPersona(it) },
                onSelectModel = { viewModel.setModel(it) },
                onToggleSearchGrounding = { viewModel.toggleSearchGrounding(it) }
            )
        }

        // 3. Search Grounding Status Banner (nếu đang bật)
        if (uiState.isSearchGroundingEnabled) {
            SearchGroundingActiveBanner()
        }

        // 4. Chat Messages List
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
        ) {
            LazyColumn(
                state = listState,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = 16.dp, vertical = 8.dp),
                verticalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                items(
                    items = uiState.messages,
                    key = { it.id }
                ) { message ->
                    ChatMessageItem(
                        message = message,
                        persona = uiState.currentPersona
                    )
                }

                // Bubble trạng thái đang suy nghĩ
                if (uiState.isLoading) {
                    item(key = "loading_indicator") {
                        AiThinkingBubble(
                            persona = uiState.currentPersona,
                            modelName = uiState.selectedModel.displayName,
                            isSearchActive = uiState.isSearchGroundingEnabled
                        )
                    }
                }

                // Error message banner nếu có lỗi
                if (uiState.errorMessage != null && !uiState.isLoading) {
                    item(key = "error_retry") {
                        AiErrorRetryCard(
                            errorMessage = uiState.errorMessage ?: "",
                            onRetry = { viewModel.retryLastMessage() }
                        )
                    }
                }
            }
        }

        // 5. Suggested Prompts Row (Chips nhanh)
        SuggestedPromptsRow(
            prompts = uiState.suggestedPrompts,
            enabled = !uiState.isLoading,
            onSelectPrompt = { prompt -> viewModel.sendMessage(prompt) }
        )

        // 6. Bottom Input Bar
        AiChatInputBar(
            inputText = uiState.inputText,
            isLoading = uiState.isLoading,
            onTextChanged = { viewModel.onInputTextChanged(it) },
            onSend = { viewModel.sendMessage() }
        )
    }
}

/**
 * Top Header
 */
@Composable
private fun AiChatHeader(
    currentPersona: SenseiPersona,
    selectedModel: GeminiModelChoice,
    isSearchGroundingEnabled: Boolean,
    isPanelExpanded: Boolean,
    onTogglePanel: () -> Unit,
    onNavigateBack: () -> Unit,
    onClearChat: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NeutralWhite)
            .border(width = 1.dp, color = NeutralBorder)
            .statusBarsPadding()
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            modifier = Modifier.weight(1f)
        ) {
            IconButton(
                onClick = onNavigateBack,
                modifier = Modifier
                    .size(40.dp)
                    .clip(RoundedMd)
                    .background(Color(0xFFF2F4F7))
                    .testTag("btn_chat_back")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Quay lại",
                    tint = NeutralText
                )
            }

            Box(
                modifier = Modifier
                    .size(42.dp)
                    .clip(CircleShape)
                    .background(GamifyGreenLight)
                    .border(2.dp, GamifyGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = currentPersona.avatarEmoji,
                    fontSize = 22.sp
                )
            }

            Column {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = currentPersona.title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            fontSize = 15.sp,
                            color = NeutralText
                        )
                    )
                    // Model badge nhỏ
                    Box(
                        modifier = Modifier
                            .clip(RoundedSm)
                            .background(GamifyBlue.copy(alpha = 0.15f))
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = selectedModel.badge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GamifyBlueDark,
                                fontSize = 10.sp
                            )
                        )
                    }
                }
                Text(
                    text = if (isSearchGroundingEnabled) "Google Search Grounding • Sẵn sàng" else "Hỏi đáp & Luyện ngữ pháp N3",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = if (isSearchGroundingEnabled) GamifyGreenDark else NeutralSubtext,
                        fontSize = 11.sp
                    )
                )
            }
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Nút mở/đóng cấu hình Persona & Search
            IconButton(
                onClick = onTogglePanel,
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedMd)
                    .background(if (isPanelExpanded) GamifyGreenLight else Color.Transparent)
                    .testTag("btn_chat_settings")
            ) {
                Icon(
                    imageVector = Icons.Default.Tune,
                    contentDescription = "Cài đặt Persona & Model",
                    tint = if (isPanelExpanded) GamifyGreenDark else NeutralSubtext
                )
            }

            // Nút làm mới đoạn chat
            IconButton(
                onClick = onClearChat,
                modifier = Modifier
                    .size(38.dp)
                    .testTag("btn_chat_clear")
            ) {
                Icon(
                    imageVector = Icons.Default.DeleteSweep,
                    contentDescription = "Xóa lịch sử",
                    tint = NeutralSubtext
                )
            }
        }
    }
}

/**
 * Control Panel: Lựa chọn Persona, Model và kích hoạt Google Search Grounding
 */
@Composable
private fun AiChatControlPanel(
    currentPersona: SenseiPersona,
    selectedModel: GeminiModelChoice,
    isSearchGroundingEnabled: Boolean,
    onSelectPersona: (SenseiPersona) -> Unit,
    onSelectModel: (GeminiModelChoice) -> Unit,
    onToggleSearchGrounding: (Boolean) -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .background(NeutralWhite)
            .border(1.dp, NeutralBorder)
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // A. Chọn vai trò (Persona)
        Text(
            text = "CHỌN GIA SƯ / BẠN ĐỒNG HÀNH",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = NeutralSubtext,
                letterSpacing = 0.5.sp
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            SenseiPersona.entries.forEach { persona ->
                val isSelected = currentPersona == persona
                Gamified3DCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = if (isSelected) GamifyGreenLight else NeutralCard,
                    borderColor = if (isSelected) GamifyGreen else NeutralBorder,
                    borderWidth = if (isSelected) 2.dp else 1.dp,
                    onClick = { onSelectPersona(persona) }
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Text(text = persona.avatarEmoji, fontSize = 20.sp)
                        Text(
                            text = persona.title.substringBefore(" ("),
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = if (isSelected) GamifyGreenDark else NeutralText,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // B. Chọn mô hình Gemini
        Text(
            text = "CHỌN MÔ HÌNH GEMINI",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = NeutralSubtext,
                letterSpacing = 0.5.sp
            )
        )
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            GeminiModelChoice.entries.forEach { model ->
                val isSelected = selectedModel == model
                Gamified3DCard(
                    modifier = Modifier.weight(1f),
                    backgroundColor = if (isSelected) GamifyBlueLight else NeutralCard,
                    borderColor = if (isSelected) GamifyBlue else NeutralBorder,
                    borderWidth = if (isSelected) 2.dp else 1.dp,
                    onClick = { onSelectModel(model) }
                ) {
                    Column(
                        modifier = Modifier.padding(8.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Text(
                            text = model.badge,
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) GamifyBlueDark else NeutralSubtext,
                                fontSize = 10.sp
                            )
                        )
                        Text(
                            text = model.displayName.replace("Gemini ", ""),
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = if (isSelected) GamifyBlueDark else NeutralText,
                                fontSize = 12.sp
                            )
                        )
                    }
                }
            }
        }

        // C. Google Search Grounding Toggle
        Gamified3DCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = if (isSearchGroundingEnabled) Color(0xFFF0FDF4) else Color(0xFFFAFAFA),
            borderColor = if (isSearchGroundingEnabled) GamifyGreen else NeutralBorder
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(RoundedMd)
                            .background(if (isSearchGroundingEnabled) GamifyGreen else Color(0xFFE5E7EB)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = if (isSearchGroundingEnabled) Color.White else NeutralSubtext,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(
                                text = "Google Search Grounding",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeutralText,
                                    fontSize = 13.sp
                                )
                            )
                            Box(
                                modifier = Modifier
                                    .clip(RoundedSm)
                                    .background(GamifyGreen)
                                    .padding(horizontal = 6.dp, vertical = 1.dp)
                            ) {
                                Text(
                                    text = "LIVE",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = Color.White,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 9.sp
                                    )
                                )
                            }
                        }
                        Text(
                            text = "Tra cứu nguồn web thực tế (Lịch thi, tin tức, văn hóa Nhật)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NeutralSubtext,
                                fontSize = 11.sp
                            )
                        )
                    }
                }

                Switch(
                    checked = isSearchGroundingEnabled,
                    onCheckedChange = onToggleSearchGrounding,
                    colors = SwitchDefaults.colors(
                        checkedThumbColor = Color.White,
                        checkedTrackColor = GamifyGreen,
                        uncheckedThumbColor = Color.White,
                        uncheckedTrackColor = NeutralBorder
                    ),
                    modifier = Modifier.testTag("switch_search_grounding")
                )
            }
        }

        // D. Cấu hình Gemini API Key
        var inputKey by remember { mutableStateOf(com.example.data.service.AiService.getApiKey()) }
        var isSaved by remember { mutableStateOf(false) }

        Gamified3DCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFFF8FAFC),
            borderColor = NeutralBorder
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "GEMINI API KEY (GOOGLE AI STUDIO)",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeutralSubtext,
                            letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = if (inputKey.isNotBlank()) "Đã cấu hình" else "Chưa có key",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (inputKey.isNotBlank()) GamifyGreenDark else GamifyRed,
                            fontSize = 10.sp
                        )
                    )
                }

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    OutlinedTextField(
                        value = inputKey,
                        onValueChange = {
                            inputKey = it
                            isSaved = false
                        },
                        placeholder = { Text("Nhập Gemini API Key mới...", fontSize = 11.sp) },
                        modifier = Modifier
                            .weight(1f)
                            .height(48.dp),
                        singleLine = true,
                        shape = RoundedMd
                    )

                    Gamified3DButton(
                        text = if (isSaved) "Đã lưu" else "Lưu Key",
                        onClick = {
                            com.example.data.service.AiService.updateCustomApiKey(inputKey.trim())
                            isSaved = true
                        },
                        backgroundColor = GamifyGreen,
                        shadowColor = GamifyGreenShadow,
                        height = 44.dp,
                        modifier = Modifier.width(90.dp),
                        testTag = "btn_save_key_chat"
                    )
                }
            }
        }
    }
}

/**
 * Banner nhỏ báo hiệu Search Grounding đang hoạt động
 */
@Composable
private fun SearchGroundingActiveBanner() {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(Color(0xFFEBFDF2))
            .border(width = 1.dp, color = GamifyGreen.copy(alpha = 0.3f))
            .padding(horizontal = 16.dp, vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Icon(
            imageVector = Icons.Default.Public,
            contentDescription = null,
            tint = GamifyGreenDark,
            modifier = Modifier.size(16.dp)
        )
        Text(
            text = "Google Search Grounding: Câu trả lời sẽ kèm nguồn web & trích dẫn trực tiếp",
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = GamifyGreenDark,
                fontSize = 11.sp
            )
        )
    }
}

/**
 * Mỗi tin nhắn trong chuỗi trò chuyện
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
private fun ChatMessageItem(
    message: ChatMessage,
    persona: SenseiPersona
) {
    val context = LocalContext.current
    val clipboardManager = LocalClipboardManager.current
    val isUser = message.role == ChatRole.USER

    val timeString = remember(message.timestamp) {
        SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date(message.timestamp))
    }

    // Kiểm tra xem tin nhắn có chứa tiếng Nhật không để hiển thị nút đọc âm thanh
    val containsJapanese = remember(message.text) {
        message.text.any { char ->
            val code = char.code
            // Hiragana, Katakana, CJK Unified Ideographs (Kanji)
            (code in 0x3040..0x309F) || (code in 0x30A0..0x30FF) || (code in 0x4E00..0x9FAF)
        }
    }

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start,
        verticalAlignment = Alignment.Top
    ) {
        if (!isUser) {
            // Avatar Sensei
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(GamifyGreenLight)
                    .border(1.5.dp, GamifyGreen, CircleShape),
                contentAlignment = Alignment.Center
            ) {
                Text(text = persona.avatarEmoji, fontSize = 18.sp)
            }
            Spacer(modifier = Modifier.width(8.dp))
        }

        Column(
            modifier = Modifier.fillMaxWidth(if (isUser) 0.85f else 0.88f),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            // Khung nội dung tin nhắn dạng 3D Duolingo
            Box(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        )
                    )
                    .background(
                        if (isUser) GamifyBlue else if (message.isError) GamifyRedLight else NeutralWhite
                    )
                    .border(
                        width = if (isUser) 0.dp else 2.dp,
                        color = if (isUser) Color.Transparent else if (message.isError) GamifyRed else NeutralBorder,
                        shape = RoundedCornerShape(
                            topStart = 18.dp,
                            topEnd = 18.dp,
                            bottomStart = if (isUser) 18.dp else 4.dp,
                            bottomEnd = if (isUser) 4.dp else 18.dp
                        )
                    )
                    .padding(horizontal = 14.dp, vertical = 12.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    // Tên Model / Persona nhỏ ở trên
                    if (!isUser) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Text(
                                    text = persona.title.substringBefore(" ("),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (message.isError) GamifyRed else GamifyGreenDark,
                                        fontSize = 11.sp
                                    )
                                )
                                if (message.modelUsed != null) {
                                    Text(
                                        text = "• ${message.modelUsed}",
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = NeutralSubtext,
                                            fontSize = 10.sp
                                        )
                                    )
                                }
                            }

                            // Nút sao chép nội dung
                            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                                Icon(
                                    imageVector = Icons.Default.ContentCopy,
                                    contentDescription = "Sao chép",
                                    tint = NeutralSubtext,
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clickable {
                                            clipboardManager.setText(AnnotatedString(message.text))
                                            Toast.makeText(context, "Đã sao chép câu trả lời!", Toast.LENGTH_SHORT).show()
                                        }
                                )
                            }
                        }
                    }

                    // Nội dung chữ
                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isUser) Color.White else if (message.isError) GamifyRed else NeutralText,
                            fontSize = 14.5.sp,
                            lineHeight = 22.sp
                        )
                    )

                    // Nếu có đoạn tiếng Nhật, cung cấp nút đọc âm thanh bản xứ
                    if (!isUser && containsJapanese && !message.isError) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedSm)
                                .background(GamifyGreenLight)
                                .padding(horizontal = 8.dp, vertical = 4.dp)
                        ) {
                            JapaneseAudioSpeakerButton(
                                textToSpeak = message.text,
                                size = 32.dp,
                                iconSize = 18.dp,
                                primaryColor = GamifyGreen,
                                backgroundColor = Color.White
                            )
                            Text(
                                text = "Phát âm tiếng Nhật câu này",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = GamifyGreenDark,
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }

                    // Nếu có trích dẫn từ Google Search Grounding
                    if (message.groundingSources.isNotEmpty() || message.searchQueries.isNotEmpty()) {
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .clip(RoundedMd)
                                .background(Color(0xFFF3F4F6))
                                .border(1.dp, Color(0xFFE5E7EB), RoundedMd)
                                .padding(10.dp),
                            verticalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Search,
                                    contentDescription = null,
                                    tint = GamifyBlueDark,
                                    modifier = Modifier.size(16.dp)
                                )
                                Text(
                                    text = "Nguồn tham khảo từ Google Search:",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = GamifyBlueDark,
                                        fontSize = 11.sp
                                    )
                                )
                            }

                            // Câu truy vấn tìm kiếm
                            if (message.searchQueries.isNotEmpty()) {
                                FlowRow(
                                    horizontalArrangement = Arrangement.spacedBy(4.dp),
                                    verticalArrangement = Arrangement.spacedBy(4.dp)
                                ) {
                                    message.searchQueries.forEach { query ->
                                        Box(
                                            modifier = Modifier
                                                .clip(RoundedFull)
                                                .background(Color.White)
                                                .border(1.dp, Color(0xFFD1D5DB), RoundedFull)
                                                .padding(horizontal = 8.dp, vertical = 2.dp)
                                        ) {
                                            Text(
                                                text = "🔍 $query",
                                                style = MaterialTheme.typography.labelSmall.copy(
                                                    color = NeutralText,
                                                    fontSize = 10.sp
                                                )
                                            )
                                        }
                                    }
                                }
                            }

                            // Các liên kết nguồn web
                            if (message.groundingSources.isNotEmpty()) {
                                message.groundingSources.take(3).forEach { source ->
                                    Row(
                                        verticalAlignment = Alignment.CenterVertically,
                                        horizontalArrangement = Arrangement.spacedBy(6.dp),
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clip(RoundedSm)
                                            .clickable {
                                                try {
                                                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(source.url))
                                                    context.startActivity(intent)
                                                } catch (e: Exception) {
                                                    Toast.makeText(context, source.url, Toast.LENGTH_LONG).show()
                                                }
                                            }
                                            .padding(vertical = 2.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.AutoMirrored.Filled.OpenInNew,
                                            contentDescription = "Mở nguồn",
                                            tint = GamifyBlue,
                                            modifier = Modifier.size(14.dp)
                                        )
                                        Text(
                                            text = source.title,
                                            style = MaterialTheme.typography.bodySmall.copy(
                                                color = GamifyBlue,
                                                fontWeight = FontWeight.Medium,
                                                fontSize = 11.sp
                                            ),
                                            maxLines = 1
                                        )
                                    }
                                }
                            }
                        }
                    }
                }
            }

            // Timestamp nhỏ
            Text(
                text = timeString,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = NeutralSubtext,
                    fontSize = 10.sp
                ),
                modifier = Modifier
                    .padding(horizontal = 4.dp)
                    .padding(top = 2.dp)
            )
        }
    }
}

/**
 * Bubble khi AI đang suy nghĩ / tải phản hồi
 */
@Composable
private fun AiThinkingBubble(
    persona: SenseiPersona,
    modelName: String,
    isSearchActive: Boolean
) {
    val infiniteTransition = rememberInfiniteTransition(label = "pulse_thinking")
    val dotAlpha by infiniteTransition.animateFloat(
        initialValue = 0.3f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600),
            repeatMode = RepeatMode.Reverse
        ),
        label = "dot_alpha"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Start,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(GamifyGreenLight)
                .border(1.5.dp, GamifyGreen, CircleShape),
            contentAlignment = Alignment.Center
        ) {
            Text(text = persona.avatarEmoji, fontSize = 18.sp)
        }
        Spacer(modifier = Modifier.width(8.dp))

        Box(
            modifier = Modifier
                .clip(RoundedLg)
                .background(NeutralWhite)
                .border(2.dp, NeutralBorder, RoundedLg)
                .padding(horizontal = 14.dp, vertical = 10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                CircularProgressIndicator(
                    modifier = Modifier.size(16.dp),
                    color = if (isSearchActive) GamifyBlue else GamifyGreen,
                    strokeWidth = 2.dp
                )
                Text(
                    text = if (isSearchActive) {
                        "Đang tìm kiếm Google & tổng hợp câu trả lời..."
                    } else {
                        "${persona.title.substringBefore(" (")} đang chuẩn bị lời giải thích..."
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeutralText,
                        fontWeight = FontWeight.Medium,
                        fontSize = 12.5.sp
                    )
                )
            }
        }
    }
}

/**
 * Thẻ hiển thị lỗi kèm nút Thử lại (Retry) và cấu hình API Key trực tiếp nếu lỗi do xác thực
 */
@Composable
private fun AiErrorRetryCard(
    errorMessage: String,
    onRetry: () -> Unit
) {
    val isApiKeyIssue = remember(errorMessage) {
        errorMessage.contains("API key", ignoreCase = true) ||
        errorMessage.contains("401", ignoreCase = true) ||
        errorMessage.contains("UNAUTHENTICATED", ignoreCase = true)
    }
    var newKeyInput by remember { mutableStateOf("") }
    var keySavedNotice by remember { mutableStateOf(false) }

    Gamified3DCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = GamifyRedLight,
        borderColor = GamifyRed,
        borderWidth = 2.dp
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.ErrorOutline,
                    contentDescription = null,
                    tint = GamifyRed,
                    modifier = Modifier.size(20.dp)
                )
                Text(
                    text = if (isApiKeyIssue) "Cần cập nhật Gemini API Key" else "Không thể hoàn thành câu trả lời",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = GamifyRed
                    )
                )
            }
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = Color(0xFF7F1D1D),
                    fontSize = 12.sp
                )
            )

            if (isApiKeyIssue) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedMd)
                        .background(Color.White)
                        .padding(8.dp),
                    verticalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = "Nhập Gemini API Key hợp lệ từ Google AI Studio:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeutralText,
                            fontSize = 11.sp
                        )
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        OutlinedTextField(
                            value = newKeyInput,
                            onValueChange = {
                                newKeyInput = it
                                keySavedNotice = false
                            },
                            placeholder = { Text("Dán Gemini API Key mới tại đây...", fontSize = 11.sp) },
                            modifier = Modifier
                                .weight(1f)
                                .height(46.dp),
                            singleLine = true,
                            shape = RoundedMd
                        )
                        Gamified3DButton(
                            text = if (keySavedNotice) "Đã lưu" else "Lưu",
                            onClick = {
                                if (newKeyInput.isNotBlank()) {
                                    com.example.data.service.AiService.updateCustomApiKey(newKeyInput.trim())
                                    keySavedNotice = true
                                    onRetry()
                                }
                            },
                            backgroundColor = GamifyGreen,
                            shadowColor = GamifyGreenShadow,
                            height = 42.dp,
                            modifier = Modifier.width(76.dp),
                            testTag = "btn_save_key_retry"
                        )
                    }
                }
            }

            Gamified3DButton(
                text = "Thử lại câu hỏi này",
                icon = Icons.Default.Refresh,
                onClick = onRetry,
                backgroundColor = GamifyRed,
                shadowColor = Color(0xFFB91C1C),
                height = 42.dp,
                testTag = "btn_retry_chat"
            )
        }
    }
}

/**
 * Hàng các gợi ý câu hỏi nhanh (Prompt chips)
 */
@Composable
private fun SuggestedPromptsRow(
    prompts: List<String>,
    enabled: Boolean,
    onSelectPrompt: (String) -> Unit
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .horizontalScroll(scrollState)
            .padding(horizontal = 16.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        prompts.forEach { prompt ->
            Box(
                modifier = Modifier
                    .clip(RoundedFull)
                    .background(Color.White)
                    .border(1.5.dp, NeutralBorder, RoundedFull)
                    .clickable(enabled = enabled) { onSelectPrompt(prompt) }
                    .padding(horizontal = 12.dp, vertical = 6.dp)
                    .testTag("chip_prompt")
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = null,
                        tint = GamifyYellow,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = prompt,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = NeutralText,
                            fontWeight = FontWeight.Medium,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

/**
 * Thanh nhập tin nhắn cố định ở đáy
 */
@Composable
private fun AiChatInputBar(
    inputText: String,
    isLoading: Boolean,
    onTextChanged: (String) -> Unit,
    onSend: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .background(NeutralWhite)
            .border(width = 1.dp, color = NeutralBorder)
            .padding(horizontal = 12.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        OutlinedTextField(
            value = inputText,
            onValueChange = onTextChanged,
            placeholder = {
                Text(
                    text = "Hỏi Sensei về ngữ pháp N3, từ vựng, kanji...",
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = NeutralSubtext,
                        fontSize = 13.5.sp
                    )
                )
            },
            modifier = Modifier
                .weight(1f)
                .testTag("input_chat_text"),
            shape = RoundedLg,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFFF9FAFB),
                unfocusedContainerColor = Color(0xFFF9FAFB),
                focusedBorderColor = GamifyGreen,
                unfocusedBorderColor = NeutralBorder
            ),
            singleLine = false,
            maxLines = 4,
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
            keyboardActions = KeyboardActions(onSend = { onSend() })
        )

        Gamified3DButton(
            text = "",
            icon = Icons.AutoMirrored.Filled.Send,
            onClick = onSend,
            enabled = inputText.isNotBlank() && !isLoading,
            backgroundColor = GamifyGreen,
            shadowColor = GamifyGreenShadow,
            shape = RoundedLg,
            height = 50.dp,
            modifier = Modifier.width(52.dp),
            testTag = "btn_send_chat"
        )
    }
}
