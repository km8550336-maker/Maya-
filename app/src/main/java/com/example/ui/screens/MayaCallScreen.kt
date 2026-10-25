package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CallEnd
import androidx.compose.material.icons.filled.Mic
import androidx.compose.material.icons.filled.MicOff
import androidx.compose.material.icons.filled.VolumeMute
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.ChatMessage
import com.example.data.model.MayaMood
import com.example.ui.components.HoloAudioWaveform
import com.example.ui.components.MayaAnimeAvatar
import com.example.ui.theme.MayaDarkBackground
import com.example.ui.theme.MayaDarkSurface
import com.example.ui.theme.MayaJarvisCyan
import com.example.ui.theme.MayaPinkGlow
import com.example.ui.theme.MayaPinkPrimary
import kotlinx.coroutines.delay

@Composable
fun MayaCallScreen(
    currentMood: MayaMood,
    isSpeaking: Boolean,
    isListening: Boolean,
    spokenTextLive: String = "",
    speechRmsLevel: Float = 0f,
    latestMayaMessage: ChatMessage?,
    onToggleMic: () -> Unit,
    onEndCall: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler {
        onEndCall()
    }

    var callDurationSeconds by remember { mutableIntStateOf(0) }
    LaunchedEffect(Unit) {
        while (true) {
            delay(1000)
            callDurationSeconds++
        }
    }

    val minutes = callDurationSeconds / 60
    val seconds = callDurationSeconds % 60
    val timeFormatted = String.format("%02d:%02d", minutes, seconds)

    val infiniteTransition = rememberInfiniteTransition(label = "call_pulse")
    val pulseRing by infiniteTransition.animateFloat(
        initialValue = 0.95f,
        targetValue = 1.15f,
        animationSpec = infiniteRepeatable(
            animation = tween(1200, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "call_ring"
    )

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MayaDarkBackground)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(vertical = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            // Top Call Info Bar
            Column(
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(4.dp),
                modifier = Modifier.padding(top = 16.dp)
            ) {
                Text(
                    text = "माया (Maya) • लाइव वॉयस कॉल",
                    color = Color.White,
                    fontWeight = FontWeight.Bold,
                    fontSize = 20.sp
                )
                Text(
                    text = timeFormatted,
                    color = MayaJarvisCyan,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(8.dp)
                            .background(
                                color = if (isSpeaking) MayaPinkPrimary else MayaJarvisCyan,
                                shape = CircleShape
                            )
                    )
                    Text(
                        text = if (isSpeaking) "माया बोल रही हैं... (Speaking)" else if (isListening) "आपकी आवाज़ सुन रही हूँ... (Listening)" else "कनेक्टेड • ${currentMood.hindiName}",
                        color = Color.White.copy(alpha = 0.8f),
                        fontSize = 12.sp
                    )
                }
            }

            // Central 9:16 Anime Visual
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(0.92f),
                contentAlignment = Alignment.Center
            ) {
                // Pulsating concentric halo rings
                Box(
                    modifier = Modifier
                        .scale(pulseRing)
                        .size(320.dp)
                        .background(
                            brush = Brush.radialGradient(
                                listOf(currentMood.primaryColor.copy(alpha = 0.25f), Color.Transparent)
                            ),
                            shape = CircleShape
                        )
                )

                MayaAnimeAvatar(
                    mood = currentMood,
                    isSpeaking = isSpeaking,
                    onAvatarTapped = {}
                )
            }

            // Real-time Subtitle Banner
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 24.dp)
                    .background(
                        color = MayaDarkSurface.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = currentMood.primaryColor.copy(alpha = 0.4f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .padding(14.dp)
            ) {
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "माया की बात:",
                            color = currentMood.primaryColor,
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                        HoloAudioWaveform(
                            isSpeaking = isSpeaking,
                            primaryColor = currentMood.primaryColor,
                            secondaryColor = currentMood.secondaryColor,
                            maxHeight = 14.dp,
                            barCount = 7
                        )
                    }

                    if (isListening && spokenTextLive.isNotBlank()) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "आप (बोल रहे हैं):",
                                color = MayaJarvisCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                        Text(
                            text = spokenTextLive,
                            color = MayaJarvisCyan,
                            fontSize = 14.sp,
                            fontWeight = FontWeight.Medium
                        )
                    } else {
                        Text(
                            text = latestMayaMessage?.text ?: "सुनो ना... मैं यहाँ हूँ आपके साथ, कुछ कहिए ना!",
                            color = Color.White,
                            fontSize = 14.sp,
                            lineHeight = 20.sp
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Bottom Call Action Controls
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 32.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mic Mute / Listen Toggle
                IconButton(
                    onClick = onToggleMic,
                    modifier = Modifier
                        .size(60.dp)
                        .background(
                            color = if (isListening) MayaPinkPrimary else MayaDarkSurface,
                            shape = CircleShape
                        )
                        .border(1.dp, Color.White.copy(alpha = 0.2f), CircleShape)
                        .testTag("call_mic_toggle")
                ) {
                    Icon(
                        imageVector = if (isListening) Icons.Default.Mic else Icons.Default.MicOff,
                        contentDescription = "Mute or Unmute",
                        tint = Color.White,
                        modifier = Modifier.size(28.dp)
                    )
                }

                // End Call Red Button
                IconButton(
                    onClick = onEndCall,
                    modifier = Modifier
                        .size(72.dp)
                        .background(Color(0xFFFF1744), CircleShape)
                        .testTag("end_call_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.CallEnd,
                        contentDescription = "End Call",
                        tint = Color.White,
                        modifier = Modifier.size(34.dp)
                    )
                }
            }
        }
    }
}
