package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ChatBubble
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.HealthAndSafety
import androidx.compose.material.icons.filled.SelfImprovement
import androidx.compose.material.icons.outlined.ChatBubbleOutline
import androidx.compose.material.icons.outlined.FavoriteBorder
import androidx.compose.material.icons.outlined.HealthAndSafety
import androidx.compose.material.icons.outlined.SelfImprovement
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationRail
import androidx.compose.material3.NavigationRailItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.ui.RefugioTab
import com.example.ui.RefugioViewModel
import com.example.ui.screens.ChatContencionScreen
import com.example.ui.screens.CrisisYLimitesScreen
import com.example.ui.screens.MicroRegulacionScreen
import com.example.ui.screens.MiEstadoScreen
import com.example.ui.screens.PersonalizarAvatarDialog
import com.example.ui.theme.MyApplicationTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MyApplicationTheme {
                RefugioCuidadorApp()
            }
        }
    }
}

private data class NavDestination(
    val tab: RefugioTab,
    val labelRes: Int,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val testTag: String
)

@Composable
fun RefugioCuidadorApp(
    viewModel: RefugioViewModel = viewModel()
) {
    val profile by viewModel.profileState.collectAsStateWithLifecycle()
    val messages by viewModel.messagesState.collectAsStateWithLifecycle()
    val bookmarkedMessages by viewModel.bookmarkedMessagesState.collectAsStateWithLifecycle()
    val checkIns by viewModel.checkInsState.collectAsStateWithLifecycle()
    val selectedTab by viewModel.selectedTab.collectAsStateWithLifecycle()
    val isSending by viewModel.isSendingMessage.collectAsStateWithLifecycle()
    val showAvatarSheet by viewModel.showAvatarSheet.collectAsStateWithLifecycle()
    val showCrisisBanner by viewModel.showCrisisBanner.collectAsStateWithLifecycle()
    val activeExercise by viewModel.activeExercise.collectAsStateWithLifecycle()
    val latestCheckInFeedback by viewModel.latestCheckInFeedback.collectAsStateWithLifecycle()

    LaunchedEffect(messages.isEmpty()) {
        if (messages.isEmpty()) {
            viewModel.ensureWelcomeGreetingIfEmpty(messages)
        }
    }

    val destinations = listOf(
        NavDestination(
            tab = RefugioTab.CONTENCION,
            labelRes = R.string.nav_contencion,
            selectedIcon = Icons.Filled.ChatBubble,
            unselectedIcon = Icons.Outlined.ChatBubbleOutline,
            testTag = "nav_tab_contencion"
        ),
        NavDestination(
            tab = RefugioTab.MI_ESTADO,
            labelRes = R.string.nav_mi_estado,
            selectedIcon = Icons.Filled.Favorite,
            unselectedIcon = Icons.Outlined.FavoriteBorder,
            testTag = "nav_tab_mi_estado"
        ),
        NavDestination(
            tab = RefugioTab.MICRO_PAUSAS,
            labelRes = R.string.nav_pausas,
            selectedIcon = Icons.Filled.SelfImprovement,
            unselectedIcon = Icons.Outlined.SelfImprovement,
            testTag = "nav_tab_pausas"
        ),
        NavDestination(
            tab = RefugioTab.CRISIS_Y_LIMITES,
            labelRes = R.string.nav_crisis_limites,
            selectedIcon = Icons.Filled.HealthAndSafety,
            unselectedIcon = Icons.Outlined.HealthAndSafety,
            testTag = "nav_tab_crisis"
        )
    )

    if (showAvatarSheet) {
        PersonalizarAvatarDialog(
            currentProfile = profile,
            onDismiss = { viewModel.setShowAvatarSheet(false) },
            onSave = { avatarName, caregiverName, trenchContext ->
                viewModel.updateCompanionProfile(avatarName, caregiverName, trenchContext)
            }
        )
    }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isExpandedScreen = maxWidth >= 600.dp

        Scaffold(
            modifier = Modifier.fillMaxSize(),
            contentWindowInsets = WindowInsets.safeDrawing,
            bottomBar = {
                if (!isExpandedScreen) {
                    NavigationBar(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        destinations.forEach { dest ->
                            val selected = selectedTab == dest.tab
                            NavigationBarItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(dest.tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = stringResource(dest.labelRes)
                                    )
                                },
                                label = { Text(stringResource(dest.labelRes)) },
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }
            }
        ) { innerPadding ->
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                if (isExpandedScreen) {
                    NavigationRail(
                        containerColor = MaterialTheme.colorScheme.surface
                    ) {
                        destinations.forEach { dest ->
                            val selected = selectedTab == dest.tab
                            NavigationRailItem(
                                selected = selected,
                                onClick = { viewModel.selectTab(dest.tab) },
                                icon = {
                                    Icon(
                                        imageVector = if (selected) dest.selectedIcon else dest.unselectedIcon,
                                        contentDescription = stringResource(dest.labelRes)
                                    )
                                },
                                label = { Text(stringResource(dest.labelRes)) },
                                modifier = Modifier.testTag(dest.testTag)
                            )
                        }
                    }
                }

                when (selectedTab) {
                    RefugioTab.CONTENCION -> {
                        ChatContencionScreen(
                            profile = profile,
                            messages = messages,
                            isSending = isSending,
                            showCrisisBanner = showCrisisBanner,
                            onSendMessage = viewModel::sendCaregiverMessage,
                            onToggleBookmark = viewModel::toggleMessageBookmark,
                            onOpenEditAvatar = { viewModel.setShowAvatarSheet(true) },
                            onResetConversation = viewModel::startFreshConversation,
                            onOpenMicroPause = viewModel::openMicroExerciseFromChat,
                            onOpenCrisisScreen = viewModel::triggerCrisisSupportTab,
                            onDismissCrisisBanner = viewModel::dismissCrisisBanner,
                            modifier = Modifier.weight(1f)
                        )
                    }

                    RefugioTab.MI_ESTADO -> {
                        MiEstadoScreen(
                            profile = profile,
                            checkIns = checkIns,
                            bookmarkedMessages = bookmarkedMessages,
                            latestFeedback = latestCheckInFeedback,
                            onSaveCheckIn = viewModel::recordCaregiverCheckIn,
                            onDeleteCheckIn = viewModel::deleteCheckIn,
                            onRemoveBookmark = viewModel::toggleMessageBookmark,
                            onNavigateBackToChat = { viewModel.selectTab(RefugioTab.CONTENCION) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    RefugioTab.MICRO_PAUSAS -> {
                        MicroRegulacionScreen(
                            profile = profile,
                            activeExercise = activeExercise,
                            onSelectExercise = viewModel::selectMicroExercise,
                            onSendTabooCardToChat = viewModel::sendQuickPromptAndNavigateToChat,
                            onNavigateBackToChat = { viewModel.selectTab(RefugioTab.CONTENCION) },
                            modifier = Modifier.weight(1f)
                        )
                    }

                    RefugioTab.CRISIS_Y_LIMITES -> {
                        CrisisYLimitesScreen(
                            profile = profile,
                            onOpenCustomizeAvatar = { viewModel.setShowAvatarSheet(true) },
                            onNavigateBackToChat = { viewModel.selectTab(RefugioTab.CONTENCION) },
                            modifier = Modifier.weight(1f)
                        )
                    }
                }
            }
        }
    }
}
