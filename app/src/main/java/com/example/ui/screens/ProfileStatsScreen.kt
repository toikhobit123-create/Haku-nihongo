package com.example.ui.screens

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.DeleteForever
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.LocalFireDepartment
import androidx.compose.material.icons.filled.OfflinePin
import androidx.compose.material.icons.filled.Psychology
import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import com.example.util.NotificationHelper
import java.util.Locale
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.PathEffect
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MasteryDataPoint
import com.example.data.model.SrsCardItem
import com.example.data.model.SrsItemType
import com.example.data.model.SrsProgress
import com.example.ui.components.Gamified3DButton
import com.example.ui.components.Gamified3DCard
import com.example.ui.components.GamifiedProgressBar
import com.example.ui.state.NihongoUiState
import com.example.ui.state.NihongoViewModel
import com.example.ui.state.Screen
import com.example.ui.theme.GamifyBlue
import com.example.ui.theme.GamifyBlueLight
import com.example.ui.theme.GamifyBlueShadow
import com.example.ui.theme.GamifyGreen
import com.example.ui.theme.GamifyGreenDark
import com.example.ui.theme.GamifyGreenLight
import com.example.ui.theme.GamifyGreenShadow
import com.example.ui.theme.GamifyRed
import com.example.ui.theme.GamifyRedLight
import com.example.ui.theme.GamifyRedShadow
import com.example.ui.theme.GamifyYellow
import com.example.ui.theme.GamifyYellowLight
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

/**
 * Tab 3: Tiến độ (Profile / Stats Dashboard)
 * Bảng điều khiển theo dõi tiến độ học tập:
 * - Biểu đồ trực quan hóa (Mastery Chart) theo dõi mức độ thuần thục Kanji, Ngữ pháp & Từ vựng theo thời gian.
 * - Thống kê số lượng đã thuộc (Mastered) theo thuật toán SM-2.
 * - Chuỗi ngày học liên tiếp (Streak) và Tỷ lệ chính xác (Accuracy %).
 * - Mục Điểm Yếu (Weak Points) với nút "Ôn tập điểm yếu" riêng biệt cho phiên học ngắn 5-10 phút.
 * - Trạng thái hoạt động 100% Offline.
 */
@Composable
fun ProfileStatsScreen(
    uiState: NihongoUiState,
    viewModel: NihongoViewModel,
    onNavigate: (Screen) -> Unit,
    modifier: Modifier = Modifier
) {
    val stats = uiState.srsStats
    val scrollState = rememberScrollState()
    var showResetDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current
    var hasNotifPermission by remember {
        mutableStateOf(NotificationHelper.hasNotificationPermission(context))
    }
    var isDailyReminderEnabled by remember {
        mutableStateOf(NotificationHelper.isDailyReminderEnabled(context))
    }
    var reminderTime by remember {
        mutableStateOf(NotificationHelper.getReminderTime(context))
    }
    val notifPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission(),
        onResult = { granted ->
            hasNotifPermission = granted
            if (granted) {
                isDailyReminderEnabled = true
                NotificationHelper.scheduleDailyReminder(context, reminderTime.first, reminderTime.second)
                NotificationHelper.sendStudyReminderNotification(
                    context,
                    title = "🥋 Thông báo Haku N3 đã bật!",
                    message = "Đã đặt lịch nhắc nhở ôn tập tự động hằng ngày lúc ${String.format(Locale.US, "%02d:%02d", reminderTime.first, reminderTime.second)}."
                )
            }
        }
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(NeutralWhite)
            .verticalScroll(scrollState)
            .padding(horizontal = 18.dp, vertical = 14.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // 1. User Profile Header Card
        Gamified3DCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = GamifyGreenLight,
            borderColor = GamifyGreen,
            borderWidth = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(14.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(58.dp)
                        .clip(CircleShape)
                        .background(GamifyGreen)
                        .border(3.dp, Color.White, CircleShape),
                    contentAlignment = Alignment.Center
                ) {
                    Text(text = "🥋", fontSize = 30.sp)
                }

                Column(
                    modifier = Modifier.weight(1f),
                    verticalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedSm)
                            .background(GamifyGreen)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = "HAKU N3 WARRIOR",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.5.sp
                            )
                        )
                    }

                    Text(
                        text = "Bảng Tiến Độ Học Tập JLPT N3",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeutralText,
                            fontSize = 16.sp
                        )
                    )

                    Text(
                        text = "Phiên học ngắn 5-10 phút • Tối ưu ghi nhớ SM-2",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NeutralSubtext,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }

        // 2. Offline Status Badge
        Gamified3DCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFFF0FDF4),
            borderColor = GamifyGreen.copy(alpha = 0.5f),
            borderWidth = 1.5.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 12.dp, vertical = 8.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.OfflinePin,
                    contentDescription = "Offline Mode",
                    tint = GamifyGreenDark,
                    modifier = Modifier.size(20.dp)
                )
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = "100% Hoạt động Offline",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GamifyGreenDark,
                            fontSize = 12.sp
                        )
                    )
                    Text(
                        text = "Tiến độ học tập và SM-2 được lưu an toàn trực tiếp trên thiết bị",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NeutralSubtext,
                            fontSize = 10.5.sp
                        )
                    )
                }
            }
        }

        // 3. Chuỗi Ngày Học (Streak) & Tỷ Lệ Chính Xác (Accuracy)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            // Streak Card
            Gamified3DCard(
                modifier = Modifier
                    .weight(1f)
                    .testTag("card_study_streak"),
                backgroundColor = GamifyYellowLight,
                borderColor = GamifyYellow,
                borderWidth = 1.5.dp
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.LocalFireDepartment,
                            contentDescription = "Streak",
                            tint = Color(0xFFD97706),
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "CHUỖI NGÀY HỌC",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF92400E),
                                fontSize = 9.5.sp
                            )
                        )
                    }

                    Text(
                        text = "${stats.streakDays} ngày",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFFB45309),
                            fontSize = 24.sp
                        )
                    )

                    Text(
                        text = "Chăm chỉ mỗi ngày!",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NeutralSubtext,
                            fontSize = 10.sp
                        )
                    )
                }
            }

            // Accuracy Rate Card
            Gamified3DCard(
                modifier = Modifier
                    .weight(1f)
                    .testTag("card_accuracy_rate"),
                backgroundColor = GamifyGreenLight,
                borderColor = GamifyGreen,
                borderWidth = 1.5.dp
            ) {
                Column(
                    modifier = Modifier.padding(12.dp),
                    verticalArrangement = Arrangement.spacedBy(4.dp)
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(4.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Accuracy",
                            tint = GamifyGreenDark,
                            modifier = Modifier.size(16.dp)
                        )
                        Text(
                            text = "TỶ LỆ CHÍNH XÁC",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = GamifyGreenDark,
                                fontSize = 9.5.sp
                            )
                        )
                    }

                    Text(
                        text = "${stats.accuracyRatePercent}%",
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GamifyGreenDark,
                            fontSize = 24.sp
                        )
                    )

                    Text(
                        text = "Độ chuẩn xác N3",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = NeutralSubtext,
                            fontSize = 10.sp
                        )
                    )
                }
            }
        }

        // Streak 7-Day Visual Row
        Gamified3DCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = NeutralWhite,
            borderColor = NeutralBorder
        ) {
            Column(
                modifier = Modifier.padding(12.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Text(
                    text = "MỤC TIÊU DUY TRÌ TUẦN NÀY (5-10 PHÚT MỖI NGÀY)",
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeutralSubtext,
                        fontSize = 10.sp
                    )
                )

                val dayList = listOf("T2", "T3", "T4", "T5", "T6", "T7", "CN")
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    dayList.forEachIndexed { index, day ->
                        val isDone = index < stats.streakDays.coerceAtMost(7)
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.spacedBy(4.dp)
                        ) {
                            Box(
                                modifier = Modifier
                                    .size(34.dp)
                                    .clip(CircleShape)
                                    .background(if (isDone) GamifyGreen else NeutralCard)
                                    .border(
                                        1.5.dp,
                                        if (isDone) GamifyGreenDark else NeutralBorder,
                                        CircleShape
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (isDone) {
                                    Icon(
                                        imageVector = Icons.Default.Check,
                                        contentDescription = "Done",
                                        tint = Color.White,
                                        modifier = Modifier.size(18.dp)
                                    )
                                } else {
                                    Text(
                                        text = "•",
                                        color = NeutralSubtext,
                                        fontWeight = FontWeight.Bold
                                    )
                                }
                            }
                            Text(
                                text = day,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isDone) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isDone) GamifyGreenDark else NeutralSubtext,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // 4. BIỂU ĐỒ TIẾN ĐỘ THUẦN THỤC THEO THỜI GIAN (MASTERY OVER TIME CHART)
        Text(
            text = "BIỂU ĐỒ TIẾN ĐỘ THUẦN THỤC (7 NGÀY GẦN NHẤT)",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = NeutralText,
                fontSize = 13.5.sp,
                letterSpacing = 0.5.sp
            )
        )

        MasteryProgressChart(
            dataPoints = stats.masteryOverTime,
            grammarMastered = stats.masteredGrammar,
            kanjiMastered = stats.masteredKanji,
            vocabMastered = stats.masteredVocab
        )

        // 5. THỐNG KÊ TỔNG SỐ ĐÃ THUỘC THEO SRS (MASTERED STATS)
        Text(
            text = "TỔNG SỐ ĐÃ THUỘC (MASTERED THEO SM-2)",
            style = MaterialTheme.typography.titleMedium.copy(
                fontWeight = FontWeight.Bold,
                color = NeutralText,
                fontSize = 13.5.sp,
                letterSpacing = 0.5.sp
            )
        )

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            MasteredItemBox(
                modifier = Modifier.weight(1f),
                title = "Ngữ pháp",
                count = stats.masteredGrammar,
                total = stats.totalGrammar,
                color = GamifyGreen,
                bgColor = GamifyGreenLight,
                testTag = "stat_mastered_grammar"
            )

            MasteredItemBox(
                modifier = Modifier.weight(1f),
                title = "Kanji N3",
                count = stats.masteredKanji,
                total = stats.totalKanji,
                color = GamifyRed,
                bgColor = GamifyRedLight,
                testTag = "stat_mastered_kanji"
            )

            MasteredItemBox(
                modifier = Modifier.weight(1f),
                title = "Từ vựng N3",
                count = stats.masteredVocab,
                total = stats.totalVocab,
                color = GamifyBlue,
                bgColor = GamifyBlueLight,
                testTag = "stat_mastered_vocab"
            )
        }

        // 6. MỤC ĐIỂM YẾU (WEAK POINTS) & NÚT ÔN TẬP ĐIỂM YẾU
        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Weak points",
                        tint = GamifyRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "MỤC ĐIỂM YẾU (WEAK POINTS)",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeutralText,
                            fontSize = 14.sp
                        )
                    )
                }

                Box(
                    modifier = Modifier
                        .clip(RoundedFull)
                        .background(GamifyRedLight)
                        .padding(horizontal = 8.dp, vertical = 2.dp)
                ) {
                    Text(
                        text = "${stats.topLapsedCards.size} thẻ hay quên",
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = GamifyRed,
                            fontSize = 10.5.sp
                        )
                    )
                }
            }

            Text(
                text = "Hệ thống tự động gom nhóm các câu hỏi và từ vựng hay làm sai nhiều nhất để bạn củng cố trong phiên học ngắn.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = NeutralSubtext,
                    fontSize = 11.sp
                )
            )

            // NÚT ÔN TẬP ĐIỂM YẾU RIÊNG BIỆT (Phiên 5-10 phút)
            Gamified3DButton(
                text = "Ôn Tập Điểm Yếu (Phiên 5-10 phút)",
                icon = Icons.Default.ElectricBolt,
                onClick = {
                    viewModel.startWeakPointsReviewSession()
                    onNavigate(Screen.SRS_REVIEW)
                },
                backgroundColor = GamifyRed,
                shadowColor = GamifyRedShadow,
                height = 52.dp,
                modifier = Modifier.fillMaxWidth(),
                testTag = "btn_review_weak_points"
            )

            // Danh sách các thẻ điểm yếu
            if (stats.topLapsedCards.isEmpty()) {
                Gamified3DCard(
                    modifier = Modifier.fillMaxWidth(),
                    backgroundColor = NeutralWhite,
                    borderColor = NeutralBorder
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(20.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "No weak points",
                            tint = GamifyGreen,
                            modifier = Modifier.size(36.dp)
                        )
                        Text(
                            text = "Tuyệt vời! Không có điểm yếu nghiêm trọng",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeutralText
                            )
                        )
                        Text(
                            text = "Bạn đang ghi nhớ rất tốt các thẻ đã học theo lịch SM-2.",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = NeutralSubtext,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            } else {
                for (pair in stats.topLapsedCards.take(6)) {
                    WeakCardItem(card = pair.first, progress = pair.second)
                }
            }
        }

        // 7. CÀI ĐẶT THÔNG BÁO NHẮC NHỞ HỌC TẬP
        Gamified3DCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = if (hasNotifPermission) Color(0xFFF0FDF4) else Color(0xFFFFFBEB),
            borderColor = if (hasNotifPermission) GamifyGreen.copy(alpha = 0.5f) else GamifyYellow.copy(alpha = 0.6f),
            borderWidth = 1.5.dp
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = if (hasNotifPermission) Icons.Default.NotificationsActive else Icons.Default.Notifications,
                            contentDescription = "Notification",
                            tint = if (hasNotifPermission) GamifyGreenDark else Color(0xFFB45309),
                            modifier = Modifier.size(22.dp)
                        )
                        Text(
                            text = "Thông Báo Nhắc Học Hàng Ngày",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = NeutralText,
                                fontSize = 14.sp
                            )
                        )
                    }

                    Box(
                        modifier = Modifier
                            .clip(RoundedSm)
                            .background(if (hasNotifPermission) GamifyGreenLight else GamifyYellowLight)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = if (hasNotifPermission) "ĐÃ BẬT" else "CHƯA BẬT",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (hasNotifPermission) GamifyGreenDark else Color(0xFFB45309),
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                }

                Text(
                    text = if (hasNotifPermission && isDailyReminderEnabled) {
                        val timeText = String.format(Locale.US, "%02d:%02d", reminderTime.first, reminderTime.second)
                        "Hệ thống tự động đặt lịch nhắc ôn tập hằng ngày lúc $timeText để bảo vệ chuỗi ngày học và thẻ SRS đến hạn."
                    } else if (hasNotifPermission) {
                        "Nhắc học tự động hằng ngày đang tạm tắt. Bật công tắc bên dưới để tự động nhắc vào khung giờ bạn chọn."
                    } else {
                        "Bật quyền thông báo để nhận lịch nhắc tự động hằng ngày cho các thẻ SRS cần ôn tập."
                    },
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeutralSubtext,
                        fontSize = 11.5.sp
                    )
                )

                if (!hasNotifPermission && Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                    Gamified3DButton(
                        text = "Cấp Quyền Thông Báo Ngay",
                        icon = Icons.Default.Notifications,
                        onClick = {
                            notifPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                        },
                        backgroundColor = GamifyGreen,
                        shadowColor = GamifyGreenShadow,
                        height = 42.dp,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_grant_notification"
                    )
                } else {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clip(RoundedMd)
                            .background(NeutralWhite)
                            .border(1.dp, NeutralBorder, RoundedMd)
                            .padding(horizontal = 12.dp, vertical = 6.dp),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "Tự động nhắc hằng ngày",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = NeutralText
                                )
                            )
                            Text(
                                text = "Giờ hẹn: ${String.format(Locale.US, "%02d:%02d", reminderTime.first, reminderTime.second)}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = NeutralSubtext,
                                    fontSize = 11.sp
                                )
                            )
                        }
                        Switch(
                            checked = isDailyReminderEnabled,
                            onCheckedChange = { enabled ->
                                isDailyReminderEnabled = enabled
                                if (enabled) {
                                    NotificationHelper.scheduleDailyReminder(context, reminderTime.first, reminderTime.second)
                                } else {
                                    NotificationHelper.cancelDailyReminder(context)
                                }
                            },
                            colors = SwitchDefaults.colors(
                                checkedThumbColor = Color.White,
                                checkedTrackColor = GamifyGreen
                            ),
                            modifier = Modifier.testTag("switch_daily_reminder")
                        )
                    }

                    if (isDailyReminderEnabled) {
                        val presets = listOf(
                            8 to 0,
                            12 to 30,
                            20 to 0,
                            21 to 30
                        )
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            presets.forEach { (h, m) ->
                                val isSelected = reminderTime.first == h && reminderTime.second == m
                                val label = String.format(Locale.US, "%02d:%02d", h, m)
                                Box(
                                    modifier = Modifier
                                        .weight(1f)
                                        .clip(RoundedSm)
                                        .background(if (isSelected) GamifyGreen else NeutralWhite)
                                        .border(
                                            1.dp,
                                            if (isSelected) GamifyGreenDark else NeutralBorder,
                                            RoundedSm
                                        )
                                        .clickable {
                                            reminderTime = h to m
                                            NotificationHelper.scheduleDailyReminder(context, h, m)
                                        }
                                        .padding(vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = label,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = if (isSelected) Color.White else NeutralText,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }

                    Gamified3DButton(
                        text = "Gửi Thông Báo Nhắc Nhở Thử Nghiệm",
                        icon = Icons.Default.NotificationsActive,
                        onClick = {
                            NotificationHelper.sendStudyReminderNotification(
                                context,
                                title = "🥋 Haku N3: Đã đến giờ ôn tập!",
                                message = "Có ${stats.dueCount.coerceAtLeast(1)} thẻ đang chờ bạn ôn tập hôm nay."
                            )
                        },
                        backgroundColor = GamifyBlue,
                        shadowColor = GamifyBlueShadow,
                        height = 42.dp,
                        modifier = Modifier.fillMaxWidth(),
                        testTag = "btn_send_test_notification"
                    )
                }
            }
        }

        // 8. Reset Data Danger Section
        Gamified3DCard(
            modifier = Modifier.fillMaxWidth(),
            backgroundColor = Color(0xFFFFF5F5),
            borderColor = GamifyRed.copy(alpha = 0.5f),
            borderWidth = 1.5.dp
        ) {
            Column(
                modifier = Modifier.padding(14.dp),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.DeleteForever,
                        contentDescription = "Reset",
                        tint = GamifyRed,
                        modifier = Modifier.size(20.dp)
                    )
                    Text(
                        text = "Đặt Lại Tiến Độ Ôn Tập SRS",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = GamifyRed,
                            fontSize = 13.sp
                        )
                    )
                }
                Text(
                    text = "Xóa toàn bộ lịch sử SM-2, chuỗi ngày học (Streak) và tỷ lệ chính xác để bắt đầu lại từ đầu.",
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeutralSubtext,
                        fontSize = 11.sp
                    )
                )

                Gamified3DButton(
                    text = "Xóa và Đặt Lại Tiến Độ",
                    onClick = { showResetDialog = true },
                    backgroundColor = GamifyRed,
                    shadowColor = GamifyRedShadow,
                    height = 38.dp,
                    shape = RoundedMd,
                    modifier = Modifier.fillMaxWidth(),
                    testTag = "btn_reset_srs_data"
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }

    // Confirmation Dialog
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = {
                Text(
                    text = "Xác nhận xóa toàn bộ tiến độ?",
                    fontWeight = FontWeight.Bold,
                    color = GamifyRed
                )
            },
            text = {
                Text(
                    text = "Thao tác này sẽ xóa toàn bộ tiến độ thẻ SRS (Repetitions, Intervals, Ease Factor), chuỗi ngày học (Streak) và tỷ lệ chính xác về 0. Bạn có chắc chắn muốn thực hiện?",
                    fontSize = 13.sp,
                    color = NeutralText
                )
            },
            confirmButton = {
                TextButton(
                    onClick = {
                        viewModel.resetSrsProgress()
                        showResetDialog = false
                    }
                ) {
                    Text("Đồng ý đặt lại", color = GamifyRed, fontWeight = FontWeight.Bold)
                }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Hủy", color = NeutralSubtext)
                }
            }
        )
    }
}

/**
 * Native Jetpack Compose Canvas Chart: Biểu đồ trực quan hóa tiến độ thuần thục theo thời gian.
 * Hiển thị đường cong Bézier mượt mà với hiệu ứng chuyển màu gradient cho Kanji, Ngữ pháp và Từ vựng.
 */
@Composable
private fun MasteryProgressChart(
    dataPoints: List<MasteryDataPoint>,
    grammarMastered: Int,
    kanjiMastered: Int,
    vocabMastered: Int,
    modifier: Modifier = Modifier
) {
    var selectedFilter by remember { mutableStateOf<String>("ALL") }

    Gamified3DCard(
        modifier = modifier
            .fillMaxWidth()
            .testTag("chart_mastery_over_time"),
        backgroundColor = NeutralWhite,
        borderColor = NeutralBorder
    ) {
        Column(
            modifier = Modifier.padding(14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // Chart Header with Filter Chips
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Timeline,
                        contentDescription = "Chart",
                        tint = GamifyBlue,
                        modifier = Modifier.size(18.dp)
                    )
                    Text(
                        text = "Mức độ thuần thục",
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeutralText,
                            fontSize = 13.sp
                        )
                    )
                }

                // Interactive Series Selector
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    ChartFilterChip(
                        label = "Tất cả",
                        isSelected = selectedFilter == "ALL",
                        color = GamifyBlue,
                        onClick = { selectedFilter = "ALL" }
                    )
                    ChartFilterChip(
                        label = "Ngữ pháp",
                        isSelected = selectedFilter == "GRAMMAR",
                        color = GamifyGreen,
                        onClick = { selectedFilter = "GRAMMAR" }
                    )
                    ChartFilterChip(
                        label = "Kanji",
                        isSelected = selectedFilter == "KANJI",
                        color = GamifyRed,
                        onClick = { selectedFilter = "KANJI" }
                    )
                    ChartFilterChip(
                        label = "Từ vựng",
                        isSelected = selectedFilter == "VOCAB",
                        color = GamifyBlue,
                        onClick = { selectedFilter = "VOCAB" }
                    )
                }
            }

            // Legend indicators
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(14.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                ChartLegendItem(color = GamifyGreen, label = "Ngữ pháp ($grammarMastered)")
                ChartLegendItem(color = GamifyRed, label = "Kanji ($kanjiMastered)")
                ChartLegendItem(color = GamifyBlue, label = "Từ vựng ($vocabMastered)")
            }

            // Canvas Drawing Area
            val maxVal = remember(dataPoints) {
                val m = dataPoints.maxOfOrNull {
                    maxOf(it.grammarMastered, it.kanjiMastered, it.vocabMastered)
                } ?: 50
                (m * 1.25f).coerceAtLeast(20f)
            }

            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(180.dp)
                    .clip(RoundedMd)
                    .background(Color(0xFFFAFAFA))
                    .padding(horizontal = 8.dp, vertical = 8.dp)
            ) {
                Canvas(modifier = Modifier.fillMaxSize()) {
                    val w = size.width
                    val h = size.height
                    val n = dataPoints.size
                    if (n < 2) return@Canvas

                    val stepX = w / (n - 1)

                    // 1. Draw horizontal dashed grid lines
                    val gridSteps = 4
                    val dashEffect = PathEffect.dashPathEffect(floatArrayOf(10f, 10f), 0f)
                    for (i in 0..gridSteps) {
                        val y = h - (h / gridSteps) * i
                        drawLine(
                            color = Color(0xFFE5E7EB),
                            start = Offset(0f, y),
                            end = Offset(w, y),
                            strokeWidth = 1.dp.toPx(),
                            pathEffect = dashEffect
                        )
                    }

                    // Helper function to plot and draw Bézier curve with gradient
                    fun drawSeries(
                        values: List<Int>,
                        lineColor: Color,
                        gradientColor: Color
                    ) {
                        if (values.size < n) return

                        val points = values.mapIndexed { idx, value ->
                            val x = idx * stepX
                            val normalizedY = (value.toFloat() / maxVal).coerceIn(0f, 1f)
                            val y = h - (normalizedY * (h - 20.dp.toPx())) - 10.dp.toPx()
                            Offset(x, y)
                        }

                        // Path for stroke
                        val strokePath = Path().apply {
                            moveTo(points[0].x, points[0].y)
                            for (i in 0 until points.size - 1) {
                                val p0 = points[i]
                                val p1 = points[i + 1]
                                val cx = (p0.x + p1.x) / 2f
                                cubicTo(cx, p0.y, cx, p1.y, p1.x, p1.y)
                            }
                        }

                        // Path for filled gradient
                        val fillPath = Path().apply {
                            addPath(strokePath)
                            lineTo(points.last().x, h)
                            lineTo(points.first().x, h)
                            close()
                        }

                        // Draw fill
                        drawPath(
                            path = fillPath,
                            brush = Brush.verticalGradient(
                                colors = listOf(
                                    gradientColor.copy(alpha = 0.28f),
                                    gradientColor.copy(alpha = 0.02f)
                                ),
                                startY = 0f,
                                endY = h
                            )
                        )

                        // Draw line
                        drawPath(
                            path = strokePath,
                            color = lineColor,
                            style = Stroke(
                                width = 2.8.dp.toPx(),
                                cap = StrokeCap.Round
                            )
                        )

                        // Draw data points dots
                        points.forEachIndexed { idx, pt ->
                            val isLast = idx == points.size - 1
                            drawCircle(
                                color = Color.White,
                                radius = if (isLast) 5.dp.toPx() else 3.5.dp.toPx(),
                                center = pt
                            )
                            drawCircle(
                                color = lineColor,
                                radius = if (isLast) 3.5.dp.toPx() else 2.5.dp.toPx(),
                                center = pt
                            )
                        }
                    }

                    // Draw active series based on selected filter
                    if (selectedFilter == "ALL" || selectedFilter == "VOCAB") {
                        drawSeries(
                            values = dataPoints.map { it.vocabMastered },
                            lineColor = GamifyBlue,
                            gradientColor = GamifyBlue
                        )
                    }

                    if (selectedFilter == "ALL" || selectedFilter == "GRAMMAR") {
                        drawSeries(
                            values = dataPoints.map { it.grammarMastered },
                            lineColor = GamifyGreen,
                            gradientColor = GamifyGreen
                        )
                    }

                    if (selectedFilter == "ALL" || selectedFilter == "KANJI") {
                        drawSeries(
                            values = dataPoints.map { it.kanjiMastered },
                            lineColor = GamifyRed,
                            gradientColor = GamifyRed
                        )
                    }
                }
            }

            // X-Axis Day Labels
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                dataPoints.forEach { pt ->
                    Text(
                        text = pt.dayLabel,
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = NeutralSubtext,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun ChartLegendItem(color: Color, label: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(5.dp)
    ) {
        Box(
            modifier = Modifier
                .size(8.dp)
                .clip(CircleShape)
                .background(color)
        )
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.SemiBold,
                color = NeutralText,
                fontSize = 10.5.sp
            )
        )
    }
}

@Composable
private fun ChartFilterChip(
    label: String,
    isSelected: Boolean,
    color: Color,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(RoundedFull)
            .background(if (isSelected) color else NeutralCard)
            .border(1.dp, if (isSelected) color else NeutralBorder, RoundedFull)
            .clickable { onClick() }
            .padding(horizontal = 7.dp, vertical = 3.dp)
    ) {
        Text(
            text = label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = FontWeight.Bold,
                color = if (isSelected) Color.White else NeutralSubtext,
                fontSize = 9.5.sp
            )
        )
    }
}

@Composable
private fun MasteredItemBox(
    title: String,
    count: Int,
    total: Int,
    color: Color,
    bgColor: Color,
    modifier: Modifier = Modifier,
    testTag: String = ""
) {
    Gamified3DCard(
        modifier = modifier.testTag(testTag),
        backgroundColor = bgColor,
        borderColor = color.copy(alpha = 0.5f),
        borderWidth = 1.5.dp
    ) {
        Column(
            modifier = Modifier.padding(10.dp),
            verticalArrangement = Arrangement.spacedBy(4.dp)
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = color,
                    fontSize = 10.sp
                ),
                maxLines = 1,
                overflow = TextOverflow.Ellipsis
            )
            Text(
                text = "$count",
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = NeutralText,
                    fontSize = 20.sp
                )
            )
            Text(
                text = "/ $total tổng",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = NeutralSubtext,
                    fontSize = 10.sp
                )
            )
        }
    }
}

@Composable
private fun WeakCardItem(card: SrsCardItem, progress: SrsProgress) {
    Gamified3DCard(
        modifier = Modifier.fillMaxWidth(),
        backgroundColor = Color(0xFFFFF8F8),
        borderColor = GamifyRed.copy(alpha = 0.4f),
        borderWidth = 1.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(modifier = Modifier.weight(1f)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .clip(RoundedSm)
                            .background(GamifyRed)
                            .padding(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Text(
                            text = when (card.type) {
                                SrsItemType.GRAMMAR -> "NGỮ PHÁP"
                                SrsItemType.VOCABULARY -> "TỪ VỰNG"
                                SrsItemType.KANJI -> "KANJI"
                            },
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 9.sp
                            )
                        )
                    }
                    Text(
                        text = "Quên: ${progress.lapses} lần • EF: ${progress.easeFactor}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = GamifyRed,
                            fontWeight = FontWeight.Bold,
                            fontSize = 10.sp
                        )
                    )
                }

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = card.frontText,
                    style = MaterialTheme.typography.titleSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = NeutralText,
                        fontSize = 15.sp
                    )
                )

                if (card.readingText.isNotBlank()) {
                    Text(
                        text = card.readingText,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = GamifyBlue,
                            fontSize = 11.5.sp
                        )
                    )
                }

                Text(
                    text = card.meaningText,
                    style = MaterialTheme.typography.bodySmall.copy(
                        color = NeutralSubtext,
                        fontSize = 11.5.sp
                    ),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(8.dp))

            val ttsText = if (card.readingText.isNotBlank()) {
                "${card.frontText}。${card.readingText}"
            } else {
                card.frontText
            }

            JapaneseAudioSpeakerButton(
                textToSpeak = ttsText,
                size = 36.dp,
                iconSize = 18.dp,
                primaryColor = GamifyRed,
                backgroundColor = GamifyRedLight
            )
        }
    }
}
