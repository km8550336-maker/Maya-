package com.example.audio

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.speech.RecognitionListener
import android.speech.RecognizerIntent
import android.speech.SpeechRecognizer
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.max

class MayaSpeechInput(private val context: Context) {
    private val mainHandler = Handler(Looper.getMainLooper())
    private var speechRecognizer: SpeechRecognizer? = null

    private val _isListening = MutableStateFlow(false)
    val isListening: StateFlow<Boolean> = _isListening.asStateFlow()

    private val _spokenTextLive = MutableStateFlow("")
    val spokenTextLive: StateFlow<String> = _spokenTextLive.asStateFlow()

    private val _rmsNormalized = MutableStateFlow(0f)
    val rmsNormalized: StateFlow<Float> = _rmsNormalized.asStateFlow()

    private val _lastError = MutableStateFlow<String?>(null)
    val lastError: StateFlow<String?> = _lastError.asStateFlow()

    fun isAvailable(): Boolean {
        return SpeechRecognizer.isRecognitionAvailable(context)
    }

    fun startListening(
        onResult: (String) -> Unit,
        onError: (String) -> Unit
    ) {
        mainHandler.post {
            if (!SpeechRecognizer.isRecognitionAvailable(context)) {
                val errorMsg = "इस डिवाइस पर स्पीच रिकग्निशन उपलब्ध नहीं है (Speech recognition unavailable)"
                _lastError.value = errorMsg
                onError(errorMsg)
                return@post
            }

            cleanupInternal()
            _spokenTextLive.value = ""
            _lastError.value = null

            try {
                speechRecognizer = SpeechRecognizer.createSpeechRecognizer(context).apply {
                    setRecognitionListener(object : RecognitionListener {
                        override fun onReadyForSpeech(params: Bundle?) {
                            _isListening.value = true
                        }

                        override fun onBeginningOfSpeech() {
                            _isListening.value = true
                        }

                        override fun onRmsChanged(rmsdB: Float) {
                            // rmsdB typically ranges from -2 to ~10 dB. Normalize to 0f..1f
                            val normalized = ((rmsdB + 2f) / 12f).coerceIn(0f, 1f)
                            _rmsNormalized.value = normalized
                        }

                        override fun onBufferReceived(buffer: ByteArray?) {}

                        override fun onEndOfSpeech() {
                            _isListening.value = false
                            _rmsNormalized.value = 0f
                        }

                        override fun onError(error: Int) {
                            _isListening.value = false
                            _rmsNormalized.value = 0f
                            val (hindiMsg, shouldReport) = when (error) {
                                SpeechRecognizer.ERROR_NO_MATCH -> Pair("कोई आवाज़ पहचानी नहीं गई, कृपया फिर से बोलें", true)
                                SpeechRecognizer.ERROR_SPEECH_TIMEOUT -> Pair("समय समाप्त, कृपया फिर से बोलें", true)
                                SpeechRecognizer.ERROR_AUDIO -> Pair("ऑडियो रिकॉर्डिंग में समस्या आई", true)
                                SpeechRecognizer.ERROR_NETWORK, SpeechRecognizer.ERROR_NETWORK_TIMEOUT -> Pair("नेटवर्क समस्या, कृपया इंटरनेट चेक करें", true)
                                SpeechRecognizer.ERROR_INSUFFICIENT_PERMISSIONS -> Pair("माइक्रोफोन की अनुमति आवश्यक है", true)
                                SpeechRecognizer.ERROR_CLIENT -> Pair("", false) // Cancelled or client stopped
                                else -> Pair("त्रुटि ($error)", true)
                            }
                            if (shouldReport && hindiMsg.isNotBlank()) {
                                _lastError.value = hindiMsg
                                onError(hindiMsg)
                            }
                        }

                        override fun onResults(results: Bundle?) {
                            _isListening.value = false
                            _rmsNormalized.value = 0f
                            val matches = results?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            val bestMatch = matches?.firstOrNull() ?: ""
                            if (bestMatch.isNotBlank()) {
                                _spokenTextLive.value = bestMatch
                                onResult(bestMatch)
                            }
                        }

                        override fun onPartialResults(partialResults: Bundle?) {
                            val matches = partialResults?.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION)
                            matches?.firstOrNull()?.let {
                                if (it.isNotBlank()) {
                                    _spokenTextLive.value = it
                                }
                            }
                        }

                        override fun onEvent(eventType: Int, params: Bundle?) {}
                    })
                }

                val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
                    // Primary Hindi language setting
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE, "hi-IN")
                    putExtra(RecognizerIntent.EXTRA_LANGUAGE_PREFERENCE, "hi-IN")
                    putExtra(RecognizerIntent.EXTRA_ONLY_RETURN_LANGUAGE_PREFERENCE, true)
                    putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, 5)
                    putExtra(RecognizerIntent.EXTRA_PARTIAL_RESULTS, true)
                    putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, context.packageName)
                    putExtra(RecognizerIntent.EXTRA_PROMPT, "सुनो ना माया... (हिंदी में बोलिए)")
                    // Keep speech listening alive for comfortable conversational pauses
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_COMPLETE_SILENCE_LENGTH_MILLIS, 2000L)
                    putExtra(RecognizerIntent.EXTRA_SPEECH_INPUT_POSSIBLY_COMPLETE_SILENCE_LENGTH_MILLIS, 1500L)
                }

                speechRecognizer?.startListening(intent)
            } catch (e: Exception) {
                _isListening.value = false
                _rmsNormalized.value = 0f
                val err = "स्पीच शुरू करने में विफल: ${e.message}"
                _lastError.value = err
                onError(err)
            }
        }
    }

    fun stopListening() {
        mainHandler.post {
            cleanupInternal()
        }
    }

    private fun cleanupInternal() {
        try {
            speechRecognizer?.stopListening()
            speechRecognizer?.cancel()
            speechRecognizer?.destroy()
        } catch (_: Exception) {}
        speechRecognizer = null
        _isListening.value = false
        _rmsNormalized.value = 0f
    }
}
