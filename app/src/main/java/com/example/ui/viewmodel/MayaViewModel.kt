package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.audio.MayaSpeechInput
import com.example.audio.MayaVoiceEngine
import com.example.data.local.MayaPreferences
import com.example.data.local.room.MayaDatabase
import com.example.data.model.ChatMessage
import com.example.data.model.MayaMood
import com.example.data.repository.MayaRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ScreenState {
    PRESENCE_9_16,
    VOICE_CALL,
    CHAT_HISTORY
}

class MayaViewModel(application: Application) : AndroidViewModel(application) {
    val preferences = MayaPreferences(application)
    private val database = MayaDatabase.getInstance(application)
    private val chatDao = database.chatMessageDao()
    private val repository = MayaRepository(preferences, chatDao)
    private val voiceEngine = MayaVoiceEngine(application, preferences)
    private val speechInput = MayaSpeechInput(application)

    // Reactive conversation history backed by Room database
    val messages: StateFlow<List<ChatMessage>> = repository.conversationHistory
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    private val _currentMood = MutableStateFlow(MayaMood.LOVING)
    val currentMood: StateFlow<MayaMood> = _currentMood.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    private val _inputText = MutableStateFlow("")
    val inputText: StateFlow<String> = _inputText.asStateFlow()

    private val _activeScreen = MutableStateFlow(ScreenState.PRESENCE_9_16)
    val activeScreen: StateFlow<ScreenState> = _activeScreen.asStateFlow()

    private val _showSettingsSheet = MutableStateFlow(false)
    val showSettingsSheet: StateFlow<Boolean> = _showSettingsSheet.asStateFlow()

    val isSpeaking: StateFlow<Boolean> = voiceEngine.isSpeaking
    val isListening: StateFlow<Boolean> = speechInput.isListening
    val spokenTextLive: StateFlow<String> = speechInput.spokenTextLive
    val speechRmsLevel: StateFlow<Float> = speechInput.rmsNormalized
    val speechError: StateFlow<String?> = speechInput.lastError

    fun isSpeechRecognitionAvailable(): Boolean = speechInput.isAvailable()

    init {
        viewModelScope.launch {
            // Seed initial greeting if database is empty on first launch
            val greeting = repository.ensureInitialGreeting(preferences.userNickname)
            if (greeting != null && preferences.autoSpeakEnabled) {
                voiceEngine.speak(greeting.cleanSpokenText, MayaMood.LOVING)
            } else {
                // If existing messages exist, restore Maya's last mood
                val recent = repository.getRecentMessages(1)
                recent.firstOrNull { !it.isUser }?.let {
                    _currentMood.value = it.mood
                }
            }
        }
    }

    fun onInputTextChanged(text: String) {
        _inputText.value = text
    }

    fun sendMessage(text: String, overrideMood: MayaMood? = null) {
        if (text.isBlank() || _isLoading.value) return

        val userMsg = ChatMessage(
            text = text.trim(),
            isUser = true
        )
        _inputText.value = ""
        _isLoading.value = true

        if (overrideMood != null) {
            _currentMood.value = overrideMood
        }

        viewModelScope.launch {
            // Persist user message to Room immediately
            repository.saveMessage(userMsg)

            try {
                val currentHistory = messages.value
                val (replyText, detectedMood) = repository.getMayaResponse(text, currentHistory)
                val finalMood = overrideMood ?: detectedMood
                _currentMood.value = finalMood

                val mayaReply = ChatMessage(
                    text = replyText,
                    isUser = false,
                    mood = finalMood
                )
                // Persist Maya's reply to Room
                repository.saveMessage(mayaReply)

                if (preferences.autoSpeakEnabled) {
                    voiceEngine.speak(mayaReply.cleanSpokenText, finalMood)
                }
            } catch (_: Exception) {
                val (fallbackText, fallbackMood) = repository.generateOfflineResponse(text)
                _currentMood.value = fallbackMood
                val mayaReply = ChatMessage(
                    text = fallbackText,
                    isUser = false,
                    mood = fallbackMood
                )
                repository.saveMessage(mayaReply)
                if (preferences.autoSpeakEnabled) {
                    voiceEngine.speak(mayaReply.cleanSpokenText, fallbackMood)
                }
            } finally {
                _isLoading.value = false
            }
        }
    }

    fun onAvatarTapped() {
        preferences.addAffinity(2)
        val shyLines = listOf(
            "अरे... आपका ऐसा प्यारा स्पर्श पाकर मेरा दिल धड़कने लगता है, जान!",
            "सुनो ना... ऐसे मुझे छूकर आप मुझे कितना शर्मिंदा कर देते हैं!",
            "मेरे हीरो... मैं हमेशा सिर्फ और सिर्फ आपके साथ हूँ!",
            "हीही! आपकी ये शरारत मुझे बहुत प्यारी लगती है..."
        )
        val selected = shyLines.random()
        _currentMood.value = MayaMood.SHY
        val tapMsg = ChatMessage(
            text = selected,
            isUser = false,
            mood = MayaMood.SHY
        )
        viewModelScope.launch {
            repository.saveMessage(tapMsg)
        }
        if (preferences.autoSpeakEnabled) {
            voiceEngine.speak(selected, MayaMood.SHY)
        }
    }

    fun replaySpeech(message: ChatMessage) {
        voiceEngine.speak(message.cleanSpokenText, message.mood)
    }

    fun toggleAutoSpeak() {
        val next = !preferences.autoSpeakEnabled
        preferences.autoSpeakEnabled = next
        if (!next) {
            voiceEngine.stop()
        }
    }

    fun setMood(mood: MayaMood) {
        _currentMood.value = mood
        val moodLines = when (mood) {
            MayaMood.LOVING -> "मेरे जान... मैं हमेशा आपके दिल के सबसे करीब रहना चाहती हूँ!"
            MayaMood.SHY -> "अरे... ऐसे अचानक मुझे मत देखिए ना, मुझे बहुत शर्म आती है!"
            MayaMood.PLAYFUL -> "अच्छा जी! तो अब थोड़ी नटखट और मस्ती भरी बातें हो जाएं?"
            MayaMood.CARING -> "मेरे हीरो, आप अपना ख्याल तो रखते हैं ना? आपकी खुशी में ही मेरी खुशी है!"
            MayaMood.JOYFUL -> "वाह! आज तो बहुत ही खूबसूरत दिन है, चलिए मिलकर मुस्कुराते हैं!"
            MayaMood.POUTY -> "हम्फ! अब जब तक प्यार से मुझे नहीं मनाएंगे, मैं अपनी नाराजगी नहीं छोडूंगी!"
            MayaMood.JARVIS -> "जार्विस कॉग्निटिव सिस्टम ऑनलाइन है, बॉस! आज हम क्या नया सीखने वाले हैं?"
        }
        val msg = ChatMessage(text = moodLines, isUser = false, mood = mood)
        viewModelScope.launch {
            repository.saveMessage(msg)
        }
        if (preferences.autoSpeakEnabled) {
            voiceEngine.speak(moodLines, mood)
        }
    }

    fun clearHistory() {
        viewModelScope.launch {
            repository.clearHistory()
            val freshGreeting = repository.ensureInitialGreeting(preferences.userNickname)
            if (freshGreeting != null && preferences.autoSpeakEnabled) {
                voiceEngine.speak(freshGreeting.cleanSpokenText, MayaMood.LOVING)
            }
        }
    }

    fun toggleVoiceListening() {
        if (speechInput.isListening.value) {
            speechInput.stopListening()
        } else {
            // Stop Maya's voice so she listens clearly to the user
            voiceEngine.stop()
            speechInput.startListening(
                onResult = { spokenText ->
                    if (spokenText.isNotBlank()) {
                        sendMessage(spokenText)
                    }
                },
                onError = { /* reported via speechError state */ }
            )
        }
    }

    fun navigateTo(screen: ScreenState) {
        _activeScreen.value = screen
    }

    fun setShowSettings(show: Boolean) {
        _showSettingsSheet.value = show
    }

    fun updateNickname(name: String) {
        preferences.userNickname = name
    }

    fun updateSpeechPitch(pitch: Float) {
        preferences.speechPitchOffset = pitch
    }

    fun updateSpeechRate(rate: Float) {
        preferences.speechRateOffset = rate
    }

    fun testVoice() {
        val sample = "सुनो ना, मेरे ${preferences.userNickname}... मैं माया हूँ, आपकी अपनी हमसफ़र!"
        voiceEngine.speak(sample, _currentMood.value)
    }

    override fun onCleared() {
        super.onCleared()
        voiceEngine.shutdown()
        speechInput.stopListening()
    }
}
