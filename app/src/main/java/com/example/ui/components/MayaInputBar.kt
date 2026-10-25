package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.shrinkVertically
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
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MayaMood
import com.example.ui.theme.MayaDarkSurface
import com.example.ui.theme.MayaDarkSurfaceElevated
import com.example.ui.theme.MayaJarvisCyan
import com.example.ui.theme.MayaPinkPrimary

@Composable
fun MayaInputBar(
    inputText: String,
    onInputChange: (String) -> Unit,
    onSendMessage: (String) -> Unit,
    isListening: Boolean,
    spokenTextLive: String,
    speechRmsLevel: Float,
    onToggleListening: () -> Unit,
    currentMood: MayaMood,
    isLoading: Boolean,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val micPulseScale by infiniteTransition.animateFloat(
        initialValue = 1.0f,
        targetValue = if (isListening) 1.25f else 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mic_scale"
    )

    // Dynamic scale driven by microphone loudness
    val voiceRippleScale = 1.0f + (speechRmsLevel * 0.45f)

    val micBgColor by animateColorAsState(
        targetValue = if (isListening) Color(0xFFFF1744) else MayaDarkSurfaceElevated,
        label = "mic_bg"
    )

    Column(
        modifier = modifier.fillMaxWidth(),
        verticalArrangement = Arrangement.spacedBy(6.dp)
    ) {
        // Active Speech-To-Text live banner
        AnimatedVisibility(
            visible = isListening,
            enter = fadeIn() + expandVertically(),
            exit = fadeOut() + shrinkVertically()
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 14.dp)
                    .background(
                        brush = Brush.horizontalGradient(
                            listOf(
                                Color(0xFF2E0824),
                                MayaDarkSurfaceElevated
                            )
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.5.dp,
                        brush = Brush.horizontalGradient(
                            listOf(MayaPinkPrimary, MayaJarvisCyan)
                        ),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(horizontal = 14.dp, vertical = 10.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Row(
                        modifier = Modifier.weight(1f),
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        // Pulsating mic audio indicator
                        Box(
                            modifier = Modifier
                                .scale(voiceRippleScale)
                                .size(28.dp)
                                .background(Color(0xFFFF1744).copy(alpha = 0.25f), CircleShape),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Mic,
                                contentDescription = null,
                                tint = Color(0xFFFF1744),
                                modifier = Modifier.size(18.dp)
                            )
                        }

                        Column {
                            Text(
                                text = "माया सुन रही हैं (Listening in Hindi):",
                                color = MayaPinkPrimary,
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Bold
                            )
                            Text(
                                text = if (spokenTextLive.isNotBlank()) spokenTextLive else "हिंदी में बोलिए... (e.g. सुनो ना माया...)",
                                color = Color.White,
                                fontSize = 14.sp,
                                fontWeight = if (spokenTextLive.isNotBlank()) FontWeight.SemiBold else FontWeight.Normal
                            )
                        }
                    }

                    // Cancel button
                    IconButton(
                        onClick = onToggleListening,
                        modifier = Modifier.size(28.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Stop speech recognition",
                            tint = Color.White.copy(alpha = 0.7f),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
            }
        }

        // Bottom controls row
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            // Voice Input Mic Button
            Box(
                modifier = Modifier
                    .scale(micPulseScale)
                    .size(46.dp)
                    .background(micBgColor, shape = CircleShape)
                    .border(
                        width = 1.5.dp,
                        color = if (isListening) Color.White else currentMood.primaryColor.copy(alpha = 0.5f),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = onToggleListening,
                    modifier = Modifier.testTag("voice_input_mic_button")
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.MicOff else Icons.Default.Mic,
                        contentDescription = if (isListening) "Stop Listening" else "Speak to Maya in Hindi",
                        tint = if (isListening) Color.White else currentMood.primaryColor,
                        modifier = Modifier.size(22.dp)
                    )
                }
            }

            // Text Input Field
            OutlinedTextField(
                value = inputText,
                onValueChange = onInputChange,
                modifier = Modifier
                    .weight(1f)
                    .testTag("chat_input_field"),
                placeholder = {
                    Text(
                        text = if (isListening) "सुन रही हूँ... (Listening...)" else "माया से कहिए (Suno na Maya...)",
                        color = Color.White.copy(alpha = 0.45f),
                        fontSize = 14.sp
                    )
                },
                singleLine = true,
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MayaDarkSurface,
                    unfocusedContainerColor = MayaDarkSurface,
                    focusedBorderColor = currentMood.primaryColor,
                    unfocusedBorderColor = Color.White.copy(alpha = 0.15f),
                    focusedTextColor = Color.White,
                    unfocusedTextColor = Color.White
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Send),
                keyboardActions = KeyboardActions(onSend = {
                    if (inputText.isNotBlank() && !isLoading) {
                        onSendMessage(inputText)
                    }
                })
            )

            // Send Button
            Box(
                modifier = Modifier
                    .size(46.dp)
                    .background(
                        brush = Brush.radialGradient(
                            listOf(currentMood.primaryColor, currentMood.secondaryColor)
                        ),
                        shape = CircleShape
                    ),
                contentAlignment = Alignment.Center
            ) {
                IconButton(
                    onClick = {
                        if (inputText.isNotBlank() && !isLoading) {
                            onSendMessage(inputText)
                        }
                    },
                    enabled = inputText.isNotBlank() && !isLoading,
                    modifier = Modifier.testTag("send_message_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }
        }
    }
}
