package com.example.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.data.model.ChatMessage
import com.example.data.model.MayaMood

@Entity(tableName = "chat_messages")
data class ChatMessageEntity(
    @PrimaryKey
    val id: String,
    val text: String,
    val isUser: Boolean,
    val moodName: String,
    val timestamp: Long
)

fun ChatMessageEntity.toChatMessage(): ChatMessage {
    val mood = try {
        MayaMood.valueOf(moodName)
    } catch (_: Exception) {
        MayaMood.LOVING
    }
    return ChatMessage(
        id = id,
        text = text,
        isUser = isUser,
        mood = mood,
        timestamp = timestamp
    )
}

fun ChatMessage.toEntity(): ChatMessageEntity {
    return ChatMessageEntity(
        id = id,
        text = text,
        isUser = isUser,
        moodName = mood.name,
        timestamp = timestamp
    )
}
