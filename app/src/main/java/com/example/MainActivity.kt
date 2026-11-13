package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.example.data.repository.SrsRepository
import com.example.ui.components.GamifiedBottomNav
import com.example.ui.components.GamifiedTopHeader
import com.example.ui.screens.CardBattleScreen
import com.example.ui.screens.HomeScreen
import com.example.ui.screens.InductiveLearningScreen
import com.example.ui.screens.ProfileStatsScreen
import com.example.ui.screens.SrsReviewScreen
import com.example.ui.state.NihongoUiState
import com.example.ui.state.NihongoViewModel
import com.example.ui.state.Screen
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.theme.NeutralWhite

import android.Manifest
import android.os.Build
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import com.example.ui.screens.AiChatScreen
import com.example.ui.state.AiChatViewModel
import com.example.util.JapaneseTtsManager
import com.example.util.LocalJapaneseTts
import com.example.util.NotificationHelper
import com.example.util.configureAppCheck

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Configure App Check before UI or ViewModels per Firebase guidelines
        configureAppCheck(this, intent)

        enableEdgeToEdge()
        NotificationHelper.createNotificationChannel(this)
        if (NotificationHelper.hasNotificationPermission(this) &&
            NotificationHelper.isDailyReminderEnabled(this)
        ) {
            NotificationHelper.scheduleDailyReminder(this)
        }
        japaneseTts = JapaneseTtsManager(this)
        SrsRepository.initialize(this)
        com.example.data.service.AiService.initialize(this)

        setContent {
            MyApplicationTheme {
                CompositionLocalProvider(LocalJapaneseTts provides japaneseTts) {
                    val context = LocalContext.current
                    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

                    // Yêu cầu quyền thông báo runtime trên Android 13+ (API 33+)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        val notificationPermissionLauncher = rememberLauncherForActivityResult(
                            contract = ActivityResultContracts.RequestPermission(),
                            onResult = { isGranted ->
                                if (isGranted) {
                                    NotificationHelper.scheduleDailyReminder(context)
                                    NotificationHelper.sendStudyReminderNotification(
                                        context,
                                        title = "🥋 Thông báo Haku N3 đã bật!",
                                        message = "Đã đặt lịch nhắc nhở ôn tập tự động hằng ngày lúc 20:00."
                                    )
                                }
                            }
                        )

                        LaunchedEffect(Unit) {
                            if (!NotificationHelper.hasNotificationPermission(context)) {
                                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                            }
                        }
                    }

                    Scaffold(
                        topBar = {
                            if (uiState.currentScreen != Screen.AI_CHAT) {
                                GamifiedTopHeader(
                                    userHp = uiState.userHp,
                                    maxUserHp = uiState.maxUserHp,
                                    score = uiState.score
                                )
                            }
                        },
                        bottomBar = {
                            GamifiedBottomNav(
                                currentScreen = uiState.currentScreen,
                                onNavigate = { screen -> viewModel.navigateTo(screen) }
                            )
                        },
                        contentWindowInsets = WindowInsets(0, 0, 0, 0),
                        modifier = Modifier
                            .fillMaxSize()
                            .background(NeutralWhite)
                    ) { innerPadding ->
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .padding(innerPadding)
                                .consumeWindowInsets(innerPadding)
                        ) {
                            NihongoAppNavHost(
                                uiState = uiState,
                                viewModel = viewModel,
                                aiChatViewModel = aiChatViewModel
                            )
                        }
                    }
                }
            }
        }
    }

    private val viewModel: NihongoViewModel by viewModels()
    private val aiChatViewModel: AiChatViewModel by viewModels()
    private lateinit var japaneseTts: JapaneseTtsManager

    override fun onDestroy() {
        super.onDestroy()
        if (::japaneseTts.isInitialized) {
            japaneseTts.shutdown()
        }
    }
}

@Composable
fun NihongoAppNavHost(
    uiState: NihongoUiState,
    viewModel: NihongoViewModel,
    aiChatViewModel: AiChatViewModel,
    modifier: Modifier = Modifier
) {
    when (uiState.currentScreen) {
        Screen.HOME -> HomeScreen(
            uiState = uiState,
            onNavigate = { screen -> viewModel.navigateTo(screen) },
            onSelectStudyCategory = { category ->
                viewModel.selectStudyCategory(category)
            },
            onOpenLesson = { category, lessonId, title ->
                viewModel.openLessonDetail(category, lessonId, title)
            },
            onStartCategoryFlashcard = { category ->
                viewModel.startSrsForCategory(category, Screen.HOME)
            },
            onStartCategoryTest = { category ->
                viewModel.startTestForLesson(category, 1, Screen.HOME)
            },
            onStartCategoryMatch = { category ->
                viewModel.startCardBattleForLesson(category, 1, Screen.HOME)
            },
            modifier = modifier
        )
        Screen.LESSON_DETAIL -> com.example.ui.screens.LessonDetailScreen(
            uiState = uiState,
            viewModel = viewModel,
            onNavigateBack = { viewModel.goBack() },
            modifier = modifier
        )
        Screen.STUDY_HUB -> com.example.ui.screens.StudyHubScreen(
            uiState = uiState,
            viewModel = viewModel,
            onAskSenseiAboutVocab = { prompt ->
                aiChatViewModel.sendMessage(prompt)
                viewModel.navigateTo(Screen.AI_CHAT)
            },
            modifier = modifier
        )
        Screen.INDUCTIVE_LEARNING -> InductiveLearningScreen(
            uiState = uiState,
            viewModel = viewModel,
            modifier = modifier
        )
        Screen.SRS_REVIEW -> SrsReviewScreen(
            uiState = uiState,
            viewModel = viewModel,
            modifier = modifier
        )
        Screen.PROFILE_STATS -> ProfileStatsScreen(
            uiState = uiState,
            viewModel = viewModel,
            onNavigate = { screen -> viewModel.navigateTo(screen) },
            modifier = modifier
        )
        Screen.CARD_BATTLE -> CardBattleScreen(
            uiState = uiState,
            viewModel = viewModel,
            modifier = modifier
        )
        Screen.AI_CHAT -> AiChatScreen(
            viewModel = aiChatViewModel,
            onNavigateBack = { viewModel.navigateTo(Screen.HOME) },
            modifier = modifier
        )
    }
}

