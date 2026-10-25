package com.example.data.local

import android.content.Context
import android.content.SharedPreferences

class MayaPreferences(context: Context) {
    private val prefs: SharedPreferences =
        context.getSharedPreferences("maya_companion_prefs", Context.MODE_PRIVATE)

    var affinityXp: Int
        get() = prefs.getInt(KEY_AFFINITY_XP, 20)
        set(value) = prefs.edit().putInt(KEY_AFFINITY_XP, value).apply()

    var userNickname: String
        get() = prefs.getString(KEY_USER_NICKNAME, "मेरे हीरो") ?: "मेरे हीरो"
        set(value) = prefs.edit().putString(KEY_USER_NICKNAME, value).apply()

    var autoSpeakEnabled: Boolean
        get() = prefs.getBoolean(KEY_AUTO_SPEAK, true)
        set(value) = prefs.edit().putBoolean(KEY_AUTO_SPEAK, value).apply()

    var speechRateOffset: Float
        get() = prefs.getFloat(KEY_SPEECH_RATE, 1.0f)
        set(value) = prefs.edit().putFloat(KEY_SPEECH_RATE, value).apply()

    var speechPitchOffset: Float
        get() = prefs.getFloat(KEY_SPEECH_PITCH, 1.0f)
        set(value) = prefs.edit().putFloat(KEY_SPEECH_PITCH, value).apply()

    fun addAffinity(points: Int = 5) {
        val current = affinityXp
        affinityXp = (current + points).coerceAtLeast(0)
    }

    val affinityLevel: AffinityLevel
        get() = when {
            affinityXp >= 400 -> AffinityLevel(4, "जान-ए-जिगर (Soulmate)", 400, 1000, "हमेशा के लिए आपकी जान")
            affinityXp >= 200 -> AffinityLevel(3, "प्यारी हमसफ़र (Sweetheart)", 200, 400, "आपके दिल के सबसे करीब")
            affinityXp >= 80 -> AffinityLevel(2, "खास दोस्त (Special Friend)", 80, 200, "एक गहरा और सच्चा रिश्ता")
            else -> AffinityLevel(1, "नई शुरुआत (First Spark)", 0, 80, "पहला मीठा परिचय")
        }

    data class AffinityLevel(
        val level: Int,
        val titleHindi: String,
        val minXp: Int,
        val maxXp: Int,
        val description: String
    ) {
        val progress: Float
            get() = if (maxXp > minXp) ((minXp + 0f) / maxXp).coerceIn(0f, 1f) else 1f
    }

    companion object {
        private const val KEY_AFFINITY_XP = "affinity_xp"
        private const val KEY_USER_NICKNAME = "user_nickname"
        private const val KEY_AUTO_SPEAK = "auto_speak"
        private const val KEY_SPEECH_RATE = "speech_rate"
        private const val KEY_SPEECH_PITCH = "speech_pitch"
    }
}
