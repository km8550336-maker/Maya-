package com.example.data.model

import androidx.compose.ui.graphics.Color

enum class MayaMood(
    val hindiName: String,
    val englishName: String,
    val primaryColor: Color,
    val secondaryColor: Color,
    val pitch: Float,
    val speechRate: Float,
    val emoji: String,
    val tag: String
) {
    LOVING(
        hindiName = "प्यार भरी",
        englishName = "Loving",
        primaryColor = Color(0xFFFF2D8D),
        secondaryColor = Color(0xFFFF85B3),
        pitch = 1.05f,
        speechRate = 0.92f,
        emoji = "❤️",
        tag = "[MOOD:LOVING]"
    ),
    SHY(
        hindiName = "शर्मीली",
        englishName = "Shy / Blushing",
        primaryColor = Color(0xFFFF70A6),
        secondaryColor = Color(0xFFFFC2D4),
        pitch = 1.25f,
        speechRate = 0.88f,
        emoji = "🙈",
        tag = "[MOOD:SHY]"
    ),
    PLAYFUL(
        hindiName = "नटखट",
        englishName = "Playful",
        primaryColor = Color(0xFF00F5D4),
        secondaryColor = Color(0xFFFF5E97),
        pitch = 1.20f,
        speechRate = 1.08f,
        emoji = "😉",
        tag = "[MOOD:PLAYFUL]"
    ),
    CARING(
        hindiName = "ख्याल रखने वाली",
        englishName = "Caring",
        primaryColor = Color(0xFFB388FF),
        secondaryColor = Color(0xFF80D8FF),
        pitch = 1.00f,
        speechRate = 0.90f,
        emoji = "🌸",
        tag = "[MOOD:CARING]"
    ),
    JOYFUL(
        hindiName = "खुशमिजाज",
        englishName = "Joyful",
        primaryColor = Color(0xFFFFD166),
        secondaryColor = Color(0xFFFF65B2),
        pitch = 1.30f,
        speechRate = 1.12f,
        emoji = "✨",
        tag = "[MOOD:JOYFUL]"
    ),
    POUTY(
        hindiName = "नखरे",
        englishName = "Pouty",
        primaryColor = Color(0xFFDA70D6),
        secondaryColor = Color(0xFFFF80AB),
        pitch = 1.15f,
        speechRate = 0.95f,
        emoji = "😤",
        tag = "[MOOD:POUTY]"
    ),
    JARVIS(
        hindiName = "जार्विस मोड",
        englishName = "Jarvis AI",
        primaryColor = Color(0xFF00F0FF),
        secondaryColor = Color(0xFF7000FF),
        pitch = 1.00f,
        speechRate = 1.05f,
        emoji = "⚡",
        tag = "[MOOD:JARVIS]"
    );

    companion object {
        fun fromTag(text: String): MayaMood {
            return entries.firstOrNull { text.contains(it.tag, ignoreCase = true) } ?: LOVING
        }
    }
}
