package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.MayaMood
import com.example.ui.theme.MayaDarkSurface
import com.example.ui.theme.MayaPinkPrimary

@Composable
fun DialogueBubbleCard(
    latestMayaMessage: ChatMessage?,
    currentMood: MayaMood,
    isSpeaking: Boolean,
    onReplaySpeech: () -> Unit,
    modifier: Modifier = Modifier
) {
    if (latestMayaMessage == null) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 14.dp, vertical = 6.dp)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MayaDarkSurface.copy(alpha = 0.92f),
                        MayaDarkSurface.copy(alpha = 0.98f)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .border(
                width = 1.5.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        currentMood.primaryColor.copy(alpha = 0.8f),
                        currentMood.secondaryColor.copy(alpha = 0.5f)
                    )
                ),
                shape = RoundedCornerShape(22.dp)
            )
            .padding(14.dp)
            .testTag("dialogue_bubble_card")
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            // Header: Mood tag & audio wave
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = currentMood.emoji,
                        fontSize = 15.sp
                    )
                    Text(
                        text = "माया • ${currentMood.hindiName}",
                        color = currentMood.primaryColor,
                        fontWeight = FontWeight.Bold,
                        fontSize = 13.sp
                    )
                }

                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    HoloAudioWaveform(
                        isSpeaking = isSpeaking,
                        primaryColor = currentMood.primaryColor,
                        secondaryColor = currentMood.secondaryColor,
                        maxHeight = 16.dp,
                        barCount = 7
                    )

                    IconButton(
                        onClick = onReplaySpeech,
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = currentMood.primaryColor.copy(alpha = 0.18f),
                                shape = CircleShape
                            )
                            .testTag("replay_speech_button")
                    ) {
                        Icon(
                            imageVector = if (isSpeaking) Icons.AutoMirrored.Filled.VolumeUp else Icons.Default.PlayArrow,
                            contentDescription = "Replay Speech",
                            tint = currentMood.primaryColor,
                            modifier = Modifier.size(16.dp)
                        )
                    }
                }
            }

            // Hindi Dialogue Text
            Text(
                text = latestMayaMessage.text,
                color = Color.White,
                fontSize = 15.sp,
                lineHeight = 22.sp,
                fontWeight = FontWeight.Medium
            )
        }
    }
}
