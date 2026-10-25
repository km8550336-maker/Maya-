package com.example.data.model

import java.util.UUID

data class ChatMessage(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val isUser: Boolean,
    val mood: MayaMood = MayaMood.LOVING,
    val timestamp: Long = System.currentTimeMillis()
) {
    /**
     * Strips mood tags, asterisks, hashtags, emojis, brackets, and markdown artifacts
     * so that the Android TextToSpeech engine speaks pure, smooth Hindi.
     */
    val cleanSpokenText: String
        get() {
            var cleaned = text
            // Strip any [MOOD:...] tag
            cleaned = cleaned.replace(Regex("\\[MOOD:[A-Z_]+\\]"), "")
            // Strip markdown asterisks, hashtags, underscores, brackets
            cleaned = cleaned.replace(Regex("[*#_\\[\\]~`]"), "")
            // Strip common emoji ranges
            cleaned = cleaned.replace(Regex("[\\p{So}\\p{Cn}]"), "")
            // Trim whitespace
            return cleaned.trim()
        }
}
