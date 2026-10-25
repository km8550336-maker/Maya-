package com.example.audio

import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import com.example.data.local.MayaPreferences
import com.example.data.model.MayaMood
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.Locale

class MayaVoiceEngine(
    context: Context,
    private val preferences: MayaPreferences
) {
    private var tts: TextToSpeech? = null
    private var isInitialized = false

    private val _isSpeaking = MutableStateFlow(false)
    val isSpeaking: StateFlow<Boolean> = _isSpeaking.asStateFlow()

    private val _audioWaveLevels = MutableStateFlow(List(7) { 0.2f })
    val audioWaveLevels: StateFlow<List<Float>> = _audioWaveLevels.asStateFlow()

    init {
        tts = TextToSpeech(context.applicationContext) { status ->
            if (status == TextToSpeech.SUCCESS) {
                // Try Hindi India locale first, then Hindi generic, fallback to device locale
                val hindiLocale = Locale.forLanguageTag("hi-IN")
                val result = tts?.setLanguage(hindiLocale)
                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    val fallbackHindi = Locale.forLanguageTag("hi")
                    val fallbackResult = tts?.setLanguage(fallbackHindi)
                    if (fallbackResult == TextToSpeech.LANG_MISSING_DATA || fallbackResult == TextToSpeech.LANG_NOT_SUPPORTED) {
                        tts?.language = Locale.getDefault()
                    }
                }
                isInitialized = true
                setupListener()
            }
        }
    }

    private fun setupListener() {
        tts?.setOnUtteranceProgressListener(object : UtteranceProgressListener() {
            override fun onStart(utteranceId: String?) {
                _isSpeaking.value = true
            }

            override fun onDone(utteranceId: String?) {
                _isSpeaking.value = false
            }

            @Deprecated("Deprecated in Java")
            override fun onError(utteranceId: String?) {
                _isSpeaking.value = false
            }

            override fun onError(utteranceId: String?, errorCode: Int) {
                _isSpeaking.value = false
            }
        })
    }

    fun speak(text: String, mood: MayaMood = MayaMood.LOVING) {
        if (!isInitialized || tts == null) return

        // Clean speech text
        val clean = cleanTextForSpeech(text)
        if (clean.isBlank()) return

        // Stop any ongoing speech
        stop()

        // Calculate pitch & rate based on mood and user preferences
        val basePitch = mood.pitch * preferences.speechPitchOffset
        val baseRate = mood.speechRate * preferences.speechRateOffset

        tts?.setPitch(basePitch.coerceIn(0.5f, 2.0f))
        tts?.setSpeechRate(baseRate.coerceIn(0.5f, 2.0f))

        val utteranceId = "maya_speech_${System.currentTimeMillis()}"
        tts?.speak(clean, TextToSpeech.QUEUE_FLUSH, null, utteranceId)
    }

    fun stop() {
        if (tts?.isSpeaking == true) {
            tts?.stop()
        }
        _isSpeaking.value = false
    }

    fun shutdown() {
        stop()
        tts?.shutdown()
        tts = null
    }

    private fun cleanTextForSpeech(raw: String): String {
        var text = raw
        // Remove mood tags
        text = text.replace(Regex("\\[MOOD:[A-Z_]+\\]"), "")
        // Remove markdown formatting
        text = text.replace(Regex("[*#_`~<>\\[\\]{}]"), " ")
        // Remove emojis
        text = text.replace(Regex("[\\p{So}\\p{Cn}]"), "")
        return text.trim()
    }
}
