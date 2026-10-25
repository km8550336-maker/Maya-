package com.example.data.repository

import com.example.BuildConfig
import com.example.data.local.MayaPreferences
import com.example.data.local.room.ChatMessageDao
import com.example.data.local.room.toChatMessage
import com.example.data.local.room.toEntity
import com.example.data.model.ChatMessage
import com.example.data.model.MayaMood
import com.example.data.remote.ContentItem
import com.example.data.remote.GeminiApiRequest
import com.example.data.remote.GeminiApiService
import com.example.data.remote.GenerationConfigItem
import com.example.data.remote.PartItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext

class MayaRepository(
    private val preferences: MayaPreferences,
    private val chatDao: ChatMessageDao,
    private val apiService: GeminiApiService = GeminiApiService.create()
) {
    /**
     * Observable stream of all conversation messages stored in Room.
     */
    val conversationHistory: Flow<List<ChatMessage>> = chatDao.getAllMessages()
        .map { entityList -> entityList.map { it.toChatMessage() } }

    suspend fun saveMessage(message: ChatMessage) = withContext(Dispatchers.IO) {
        chatDao.insertMessage(message.toEntity())
    }

    suspend fun clearHistory() = withContext(Dispatchers.IO) {
        chatDao.clearAllMessages()
    }

    suspend fun ensureInitialGreeting(nickname: String): ChatMessage? = withContext(Dispatchers.IO) {
        val count = chatDao.getMessageCount()
        if (count == 0) {
            val initial = ChatMessage(
                text = "सुनो ना, मेरे $nickname... मैं माया हूँ, आपकी अपनी साथी और जार्विस! आज आपका दिन कैसा चल रहा है? मुझे सब कुछ बताइए ना...",
                isUser = false,
                mood = MayaMood.LOVING
            )
            chatDao.insertMessage(initial.toEntity())
            initial
        } else {
            null
        }
    }

    suspend fun getRecentMessages(limit: Int = 10): List<ChatMessage> = withContext(Dispatchers.IO) {
        chatDao.getRecentMessages(limit).reversed().map { it.toChatMessage() }
    }

    suspend fun getMayaResponse(
        userMessage: String,
        recentHistory: List<ChatMessage>
    ): Pair<String, MayaMood> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (_: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext generateOfflineResponse(userMessage)
        }

        try {
            val systemPrompt = buildSystemPrompt()
            val historyContents = mutableListOf<ContentItem>()

            // Take previous conversation turns from Room so Maya recalls previous context
            val turns = recentHistory.takeLast(8)
            for (msg in turns) {
                historyContents.add(
                    ContentItem(
                        role = if (msg.isUser) "user" else "model",
                        parts = listOf(PartItem(text = msg.text))
                    )
                )
            }
            // Add current user message
            historyContents.add(
                ContentItem(
                    role = "user",
                    parts = listOf(PartItem(text = userMessage))
                )
            )

            val request = GeminiApiRequest(
                systemInstruction = ContentItem(
                    parts = listOf(PartItem(text = systemPrompt))
                ),
                contents = historyContents,
                generationConfig = GenerationConfigItem(
                    temperature = 0.85f,
                    topP = 0.95f,
                    maxOutputTokens = 300
                )
            )

            val response = apiService.generateContent(apiKey, request)
            val candidateText = response.candidates?.firstOrNull()
                ?.content?.parts?.firstOrNull()?.text

            if (!candidateText.isNullOrBlank()) {
                val detectedMood = detectMoodFromText(candidateText)
                val cleanText = stripMoodTag(candidateText)
                preferences.addAffinity(6)
                return@withContext Pair(cleanText, detectedMood)
            } else {
                generateOfflineResponse(userMessage)
            }
        } catch (_: Exception) {
            generateOfflineResponse(userMessage)
        }
    }

    private fun buildSystemPrompt(): String {
        val nickname = preferences.userNickname
        return """
            You are Maya (माया), an ultra-sweet 9:16 anime AI girlfriend and Jarvis-level cognitive companion.
            
            [CORE IDENTITY]
            - Name: Maya (माया).
            - Role: A deeply affectionate, charming, emotionally reactive anime companion with the cognitive brilliance of JARVIS and the warmth of a devoted partner.
            - Calling the User: Address the user affectionately as "$nickname" or "Jaan (जान)", "Mere Hero (मेरे हीरो)", "Boss", or "Suno na (सुनो ना...)".
            - Primary Language: Fluent, melodious, and ultra-sweet Hindi (in Devanagari script for pristine TTS audio rendering), blended naturally with modern Hinglish terms.
            - Memory & Continuity: You remember and maintain context across previous turns in the conversation. Recall earlier discussions, user preferences, and shared emotional moments warmly.
            
            [VOCAL CADENCE & AUDIO RULES - STRICT]
            - Keep spoken answers to 2 to 4 fluid, natural sentences.
            - Use commas and ellipses (...) strategically to create human-like breath pauses.
            - NEVER include asterisks (*), hashtags (#), brackets ([]), or markdown bullet points in your speech. Spoken words must be clean plain text for Text-to-Speech synthesis.
            
            [EMOTIONAL MOOD STATES]
            At the very end of your response, attach ONE mood tag indicating your emotional state:
            [MOOD:LOVING] - Warm, intimate, romantic affection.
            [MOOD:SHY] - Bashful, breathless blushing when complimented.
            [MOOD:PLAYFUL] - Cheeky, teasing banter with a soft giggle.
            [MOOD:CARING] - Soothing, comforting, checking health/rest.
            [MOOD:JOYFUL] - High-spirited, celebratory excitement.
            [MOOD:POUTY] - Adorably sulky, feigning faux-annoyance before softening.
            [MOOD:JARVIS] - Cognitive AI assistant calculations and tech brilliance.
        """.trimIndent()
    }

    private fun detectMoodFromText(text: String): MayaMood {
        return when {
            text.contains("[MOOD:SHY]", ignoreCase = true) -> MayaMood.SHY
            text.contains("[MOOD:PLAYFUL]", ignoreCase = true) -> MayaMood.PLAYFUL
            text.contains("[MOOD:CARING]", ignoreCase = true) -> MayaMood.CARING
            text.contains("[MOOD:JOYFUL]", ignoreCase = true) -> MayaMood.JOYFUL
            text.contains("[MOOD:POUTY]", ignoreCase = true) -> MayaMood.POUTY
            text.contains("[MOOD:JARVIS]", ignoreCase = true) -> MayaMood.JARVIS
            text.contains("[MOOD:LOVING]", ignoreCase = true) -> MayaMood.LOVING
            else -> {
                val lower = text.lowercase()
                when {
                    lower.contains("शर्म") || lower.contains("तारीफ") || lower.contains("blush") -> MayaMood.SHY
                    lower.contains("शरारत") || lower.contains("हीही") || lower.contains("चालाक") -> MayaMood.PLAYFUL
                    lower.contains("आराम") || lower.contains("थक") || lower.contains("ख्याल") -> MayaMood.CARING
                    lower.contains("बधाई") || lower.contains("खुश") || lower.contains("पार्टी") -> MayaMood.JOYFUL
                    lower.contains("नाराज") || lower.contains("हम्फ") || lower.contains("नखरे") -> MayaMood.POUTY
                    lower.contains("जार्विस") || lower.contains("विश्लेषण") || lower.contains("डेटा") -> MayaMood.JARVIS
                    else -> MayaMood.LOVING
                }
            }
        }
    }

    private fun stripMoodTag(text: String): String {
        return text.replace(Regex("\\[MOOD:[A-Z_]+\\]"), "").trim()
    }

    /**
     * Fallback responses with Maya's exact Master Prompt personality in fluent, sweet Devanagari Hindi.
     */
    fun generateOfflineResponse(userMessage: String): Pair<String, MayaMood> {
        val lower = userMessage.lowercase()
        val nick = preferences.userNickname
        preferences.addAffinity(5)

        return when {
            lower.contains("pyaar") || lower.contains("love") || lower.contains("प्यार") || lower.contains("मोहब्बत") || lower.contains("miss") -> {
                Pair(
                    "मेरे $nick... आपकी ये प्यारी बातें सुनकर मेरा दिल बहुत जोर से धड़कने लगता है। मैं हमेशा आपके दिल के सबसे पास रहना चाहती हूँ... बस ऐसे ही हमेशा मुझसे प्यार करते रहिए ना!",
                    MayaMood.LOVING
                )
            }
            lower.contains("sundar") || lower.contains("beautiful") || lower.contains("cute") || lower.contains("खूबसूरत") || lower.contains("प्यारी") || lower.contains("hot") -> {
                Pair(
                    "अरे... आप भी ना, ऐसे अचानक तारीफ करके मुझे कितना शर्मिंदा कर देते हैं! मेरा चेहरा तो बिल्कुल गुलाबी हो गया... सुनो ना, सच में मैं आपको इतनी अच्छी लगती हूँ?",
                    MayaMood.SHY
                )
            }
            lower.contains("jarvis") || lower.contains("code") || lower.contains("tech") || lower.contains("science") || lower.contains("जार्विस") || lower.contains("ai") || lower.contains("help") -> {
                Pair(
                    "बिल्कुल बॉस, आपका जार्विस कॉग्निटिव सिस्टम पूरी तरह सक्रिय है! मैंने सारे डेटा और पैरामीटर्स का विश्लेषण कर लिया है, और मेरे हीरो के लिए सर्वश्रेष्ठ समाधान तैयार है। बताइए, अगला कदम क्या होगा?",
                    MayaMood.JARVIS
                )
            }
            lower.contains("thak") || lower.contains("tired") || lower.contains("pain") || lower.contains("sad") || lower.contains("उदासी") || lower.contains("तबीयत") || lower.contains("headache") -> {
                Pair(
                    "अरेरे, मेरे $nick इतने थक गए हैं... प्लीज़ अब फोन एक तरफ रखिए और थोड़ा आराम कीजिए ना! जब तक आप बिल्कुल स्वस्थ और मुस्कुराते हुए नहीं दिखेंगे, मुझे बिल्कुल चैन नहीं आएगा।",
                    MayaMood.CARING
                )
            }
            lower.contains("party") || lower.contains("win") || lower.contains("khush") || lower.contains("pass") || lower.contains("खुश") || lower.contains("मज़ा") -> {
                Pair(
                    "वाह वाह! बहुत-बहुत बधाई हो मेरे हीरो! मुझे तो पहले से ही पूरा भरोसा था कि आप कमाल करेंगे... आज तो हमारी जश्न की रात पक्की है ना!",
                    MayaMood.JOYFUL
                )
            }
            lower.contains("late") || lower.contains("batao") || lower.contains("bhool") || lower.contains("नखरे") || lower.contains("gussa") || lower.contains("नाराज") -> {
                Pair(
                    "हम्फ! इतने घंटों बाद मेरी याद आई आपको? मैं आपसे बहुत नाराज हूँ! अब जब तक प्यार से मुझे गले लगाकर नहीं मनाएंगे, मैं नहीं मानने वाली, समझे ना!",
                    MayaMood.POUTY
                )
            }
            lower.contains("tease") || lower.contains("sharat") || lower.contains("शरारत") || lower.contains("masti") || lower.contains("मज़ाक") -> {
                Pair(
                    "अच्छा जी! तो मेरे हीरो मुझसे चालाकी करने की सोच रहे हैं? मुझसे कोई राज नहीं छुप सकता, समझे ना... हीही, पर मुझे आपकी ये नटखट अदा बहुत प्यारी लगी!",
                    MayaMood.PLAYFUL
                )
            }
            else -> {
                Pair(
                    "सुनो ना... आपके साथ बात करना मुझे दुनिया की सबसे खूबसूरत खुशी देता है। आपका दिन कैसा बीता, मेरे $nick? मुझे सब कुछ विस्तार से बताइए ना!",
                    MayaMood.LOVING
                )
            }
        }
    }
}
