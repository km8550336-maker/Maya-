package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Scaffold
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.data.local.MayaPreferences
import com.example.data.model.ChatMessage
import com.example.data.model.MayaMood
import com.example.ui.components.BondHeaderCard
import com.example.ui.components.DialogueBubbleCard
import com.example.ui.components.MayaAnimeAvatar
import com.example.ui.components.MayaInputBar
import com.example.ui.components.MoodChipSelector
import com.example.ui.components.QuickPromptChips
import com.example.ui.theme.MayaDarkBackground
import com.example.ui.theme.MayaPinkPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MayaPresenceScreen(
    currentMood: MayaMood,
    latestMayaMessage: ChatMessage?,
    isSpeaking: Boolean,
    isListening: Boolean,
    spokenTextLive: String = "",
    speechRmsLevel: Float = 0f,
    isLoading: Boolean,
    inputText: String,
    affinityLevel: MayaPreferences.AffinityLevel,
    affinityXp: Int,
    preferences: MayaPreferences,
    showSettingsSheet: Boolean,
    onInputChange: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    onQuickPrompt: (String, MayaMood) -> Unit,
    onMoodSelected: (MayaMood) -> Unit,
    onAvatarTapped: () -> Unit,
    onToggleAutoSpeak: () -> Unit,
    onToggleListening: () -> Unit,
    onReplaySpeech: () -> Unit,
    onOpenCallScreen: () -> Unit,
    onOpenChatHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    onDismissSettings: () -> Unit,
    onNicknameChanged: (String) -> Unit,
    onPitchChanged: (Float) -> Unit,
    onSpeedChanged: (Float) -> Unit,
    onTestVoice: () -> Unit,
    modifier: Modifier = Modifier
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)

    Scaffold(
        containerColor = MayaDarkBackground,
        modifier = modifier.fillMaxSize()
    ) { innerPadding ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceBetween
            ) {
                // Top Header Card
                BondHeaderCard(
                    currentMood = currentMood,
                    affinityLevel = affinityLevel,
                    affinityXp = affinityXp,
                    autoSpeakEnabled = preferences.autoSpeakEnabled,
                    isSpeaking = isSpeaking,
                    onToggleAutoSpeak = onToggleAutoSpeak,
                    onOpenCallScreen = onOpenCallScreen,
                    onOpenChatHistory = onOpenChatHistory,
                    onOpenSettings = onOpenSettings
                )

                // Mood selector pills
                MoodChipSelector(
                    selectedMood = currentMood,
                    onMoodSelected = onMoodSelected
                )

                // Central Vertical 9:16 Anime Companion Presence
                Box(
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    MayaAnimeAvatar(
                        mood = currentMood,
                        isSpeaking = isSpeaking,
                        onAvatarTapped = onAvatarTapped,
                        modifier = Modifier.fillMaxSize()
                    )

                    // Loading spinner over Maya while thinking
                    if (isLoading) {
                        Box(
                            modifier = Modifier
                                .align(Alignment.TopCenter)
                                .padding(top = 16.dp)
                        ) {
                            CircularProgressIndicator(
                                color = MayaPinkPrimary,
                                strokeWidth = 3.dp,
                                modifier = Modifier.height(28.dp)
                            )
                        }
                    }

                    // Floating Glassmorphic Dialogue Bubble over the lower portion of the portrait
                    Box(
                        modifier = Modifier
                            .align(Alignment.BottomCenter)
                            .padding(bottom = 4.dp)
                    ) {
                        DialogueBubbleCard(
                            latestMayaMessage = latestMayaMessage,
                            currentMood = currentMood,
                            isSpeaking = isSpeaking,
                            onReplaySpeech = onReplaySpeech
                        )
                    }
                }

                // Interactive Quick Prompts Row
                QuickPromptChips(
                    onPromptSelected = onQuickPrompt
                )

                // Bottom Input Bar (Voice Mic + Text)
                MayaInputBar(
                    inputText = inputText,
                    onInputChange = onInputChange,
                    onSendMessage = onSendMessage,
                    isListening = isListening,
                    spokenTextLive = spokenTextLive,
                    speechRmsLevel = speechRmsLevel,
                    onToggleListening = onToggleListening,
                    currentMood = currentMood,
                    isLoading = isLoading
                )

                Spacer(modifier = Modifier.height(4.dp))
            }

            // Settings Bottom Sheet
            if (showSettingsSheet) {
                ModalBottomSheet(
                    onDismissRequest = onDismissSettings,
                    sheetState = sheetState,
                    containerColor = MayaDarkBackground
                ) {
                    MayaSettingsSheet(
                        preferences = preferences,
                        onDismiss = onDismissSettings,
                        onTestVoice = onTestVoice,
                        onNicknameChanged = onNicknameChanged,
                        onAutoSpeakToggled = { onToggleAutoSpeak() },
                        onPitchChanged = onPitchChanged,
                        onSpeedChanged = onSpeedChanged
                    )
                }
            }
        }
    }
}
