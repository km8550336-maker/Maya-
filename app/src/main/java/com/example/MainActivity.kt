package com.example

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import com.example.ui.screens.MayaCallScreen
import com.example.ui.screens.MayaChatHistoryScreen
import com.example.ui.screens.MayaPresenceScreen
import com.example.ui.theme.MayaDarkBackground
import com.example.ui.theme.MayaTheme
import com.example.ui.viewmodel.MayaViewModel
import com.example.ui.viewmodel.ScreenState

class MainActivity : ComponentActivity() {
    private val viewModel: MayaViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MayaTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MayaDarkBackground
                ) {
                    MayaAppContent(viewModel = viewModel)
                }
            }
        }
    }
}

@Composable
fun MayaAppContent(viewModel: MayaViewModel) {
    val messages by viewModel.messages.collectAsState()
    val currentMood by viewModel.currentMood.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val isSpeaking by viewModel.isSpeaking.collectAsState()
    val isListening by viewModel.isListening.collectAsState()
    val spokenTextLive by viewModel.spokenTextLive.collectAsState()
    val speechRmsLevel by viewModel.speechRmsLevel.collectAsState()
    val inputText by viewModel.inputText.collectAsState()
    val activeScreen by viewModel.activeScreen.collectAsState()
    val showSettingsSheet by viewModel.showSettingsSheet.collectAsState()

    val context = androidx.compose.ui.platform.LocalContext.current

    // Audio recording permission launcher
    val audioPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { isGranted ->
        if (isGranted) {
            viewModel.toggleVoiceListening()
        } else {
            Toast.makeText(context, "Voice input requires microphone permission", Toast.LENGTH_SHORT).show()
        }
    }

    val onToggleMicWithPermission = {
        val hasPermission = ContextCompat.checkSelfPermission(
            context,
            Manifest.permission.RECORD_AUDIO
        ) == PackageManager.PERMISSION_GRANTED

        if (hasPermission) {
            viewModel.toggleVoiceListening()
        } else {
            audioPermissionLauncher.launch(Manifest.permission.RECORD_AUDIO)
        }
    }

    val latestMayaMessage = messages.lastOrNull { !it.isUser }

    when (activeScreen) {
        ScreenState.PRESENCE_9_16 -> {
            MayaPresenceScreen(
                currentMood = currentMood,
                latestMayaMessage = latestMayaMessage,
                isSpeaking = isSpeaking,
                isListening = isListening,
                spokenTextLive = spokenTextLive,
                speechRmsLevel = speechRmsLevel,
                isLoading = isLoading,
                inputText = inputText,
                affinityLevel = viewModel.preferences.affinityLevel,
                affinityXp = viewModel.preferences.affinityXp,
                preferences = viewModel.preferences,
                showSettingsSheet = showSettingsSheet,
                onInputChange = viewModel::onInputTextChanged,
                onSendMessage = { text -> viewModel.sendMessage(text) },
                onQuickPrompt = { prompt, mood -> viewModel.sendMessage(prompt, mood) },
                onMoodSelected = viewModel::setMood,
                onAvatarTapped = viewModel::onAvatarTapped,
                onToggleAutoSpeak = viewModel::toggleAutoSpeak,
                onToggleListening = onToggleMicWithPermission,
                onReplaySpeech = {
                    latestMayaMessage?.let { viewModel.replaySpeech(it) }
                },
                onOpenCallScreen = { viewModel.navigateTo(ScreenState.VOICE_CALL) },
                onOpenChatHistory = { viewModel.navigateTo(ScreenState.CHAT_HISTORY) },
                onOpenSettings = { viewModel.setShowSettings(true) },
                onDismissSettings = { viewModel.setShowSettings(false) },
                onNicknameChanged = viewModel::updateNickname,
                onPitchChanged = viewModel::updateSpeechPitch,
                onSpeedChanged = viewModel::updateSpeechRate,
                onTestVoice = viewModel::testVoice
            )
        }

        ScreenState.VOICE_CALL -> {
            MayaCallScreen(
                currentMood = currentMood,
                isSpeaking = isSpeaking,
                isListening = isListening,
                spokenTextLive = spokenTextLive,
                speechRmsLevel = speechRmsLevel,
                latestMayaMessage = latestMayaMessage,
                onToggleMic = onToggleMicWithPermission,
                onEndCall = { viewModel.navigateTo(ScreenState.PRESENCE_9_16) }
            )
        }

        ScreenState.CHAT_HISTORY -> {
            MayaChatHistoryScreen(
                messages = messages,
                isSpeaking = isSpeaking,
                onReplayMessage = viewModel::replaySpeech,
                onClearHistory = viewModel::clearHistory,
                onBack = { viewModel.navigateTo(ScreenState.PRESENCE_9_16) }
            )
        }
    }
}
