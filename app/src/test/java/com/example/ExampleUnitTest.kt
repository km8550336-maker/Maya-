package com.example

import com.example.data.local.room.toChatMessage
import com.example.data.local.room.toEntity
import com.example.data.model.ChatMessage
import com.example.data.model.MayaMood
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class ExampleUnitTest {
    @Test
    fun cleanSpokenText_stripsMoodTagsAndMarkdown() {
        val message = ChatMessage(
            text = "मेरे *हीरो*... [MOOD:LOVING] आपकी बातें सुनकर दिल खुश हो गया! ❤️",
            isUser = false,
            mood = MayaMood.LOVING
        )
        val clean = message.cleanSpokenText
        assertFalse("Clean text should not contain markdown asterisks", clean.contains("*"))
        assertFalse("Clean text should not contain mood tag", clean.contains("[MOOD:LOVING]"))
        assertTrue("Clean text should retain core Hindi message", clean.contains("मेरे हीरो"))
    }

    @Test
    fun mayaMood_fromTag_detectsCorrectMood() {
        assertEquals(MayaMood.SHY, MayaMood.fromTag("अरे वाह! [MOOD:SHY]"))
        assertEquals(MayaMood.JARVIS, MayaMood.fromTag("सिस्टम सक्रिय है [MOOD:JARVIS]"))
        assertEquals(MayaMood.PLAYFUL, MayaMood.fromTag("मस्ती भरी बात [MOOD:PLAYFUL]"))
        assertEquals(MayaMood.CARING, MayaMood.fromTag("ख्याल रखिए [MOOD:CARING]"))
        assertEquals(MayaMood.POUTY, MayaMood.fromTag("नाराज हूँ [MOOD:POUTY]"))
    }

    @Test
    fun chatMessageEntity_roundTripMapping_preservesFields() {
        val original = ChatMessage(
            id = "test-id-123",
            text = "सुनो ना जान, मैं हमेशा आपके साथ हूँ...",
            isUser = false,
            mood = MayaMood.SHY,
            timestamp = 1700000000L
        )
        val entity = original.toEntity()
        assertEquals("test-id-123", entity.id)
        assertEquals("SHY", entity.moodName)
        assertFalse(entity.isUser)

        val restored = entity.toChatMessage()
        assertEquals(original.id, restored.id)
        assertEquals(original.text, restored.text)
        assertEquals(original.mood, restored.mood)
        assertEquals(original.timestamp, restored.timestamp)
    }
}
