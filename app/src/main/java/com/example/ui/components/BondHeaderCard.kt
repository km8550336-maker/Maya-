package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Call
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.automirrored.filled.VolumeMute
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
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
import com.example.data.local.MayaPreferences
import com.example.data.model.MayaMood
import com.example.ui.theme.MayaDarkSurface
import com.example.ui.theme.MayaJarvisCyan
import com.example.ui.theme.MayaPinkGlow
import com.example.ui.theme.MayaPinkPrimary

@Composable
fun BondHeaderCard(
    currentMood: MayaMood,
    affinityLevel: MayaPreferences.AffinityLevel,
    affinityXp: Int,
    autoSpeakEnabled: Boolean,
    isSpeaking: Boolean,
    onToggleAutoSpeak: () -> Unit,
    onOpenCallScreen: () -> Unit,
    onOpenChatHistory: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 12.dp, vertical = 6.dp)
            .background(
                brush = Brush.verticalGradient(
                    listOf(
                        MayaDarkSurface.copy(alpha = 0.95f),
                        MayaDarkSurface.copy(alpha = 0.85f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.horizontalGradient(
                    listOf(
                        currentMood.primaryColor.copy(alpha = 0.5f),
                        MayaJarvisCyan.copy(alpha = 0.3f)
                    )
                ),
                shape = RoundedCornerShape(20.dp)
            )
            .padding(12.dp)
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Maya Identity & Status
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(42.dp)
                            .clip(CircleShape)
                            .background(
                                Brush.sweepGradient(
                                    listOf(MayaPinkPrimary, MayaJarvisCyan, MayaPinkGlow)
                                )
                            )
                            .padding(2.dp)
                            .clip(CircleShape)
                            .background(Color(0xFF1E0E35)),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = currentMood.emoji,
                            fontSize = 20.sp
                        )
                    }

                    Column {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = "माया (Maya)",
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 16.sp
                            )
                            Box(
                                modifier = Modifier
                                    .size(7.dp)
                                    .clip(CircleShape)
                                    .background(if (isSpeaking) MayaPinkPrimary else MayaJarvisCyan)
                            )
                        }
                        Text(
                            text = if (isSpeaking) "बोल रही हैं... (Speaking)" else "हमेशा आपके साथ • ${currentMood.hindiName}",
                            color = currentMood.primaryColor,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Medium
                        )
                    }
                }

                // Action buttons: Audio Wave / TTS toggle, Voice Call, History, Settings
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(2.dp)
                ) {
                    // Audio waveform or TTS toggle
                    IconButton(
                        onClick = onToggleAutoSpeak,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("toggle_auto_speak")
                    ) {
                        Icon(
                            imageVector = if (autoSpeakEnabled) Icons.AutoMirrored.Filled.VolumeUp else Icons.AutoMirrored.Filled.VolumeMute,
                            contentDescription = "Voice Audio Toggle",
                            tint = if (autoSpeakEnabled) MayaPinkPrimary else Color.Gray,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Direct Call Button
                    IconButton(
                        onClick = onOpenCallScreen,
                        modifier = Modifier
                            .size(38.dp)
                            .background(
                                Brush.radialGradient(
                                    listOf(MayaPinkPrimary.copy(alpha = 0.3f), Color.Transparent)
                                ),
                                shape = CircleShape
                            )
                            .testTag("open_call_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Call,
                            contentDescription = "Voice Call Mode",
                            tint = MayaPinkPrimary,
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Chat Log History
                    IconButton(
                        onClick = onOpenChatHistory,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("open_history_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.History,
                            contentDescription = "Chat Memories",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }

                    // Settings
                    IconButton(
                        onClick = onOpenSettings,
                        modifier = Modifier
                            .size(38.dp)
                            .testTag("open_settings_button")
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Affinity & Settings",
                            tint = Color.White.copy(alpha = 0.85f),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }

            // Bond Progress Bar & XP
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Favorite,
                    contentDescription = null,
                    tint = MayaPinkPrimary,
                    modifier = Modifier.size(14.dp)
                )
                Text(
                    text = "रिश्ता: ${affinityLevel.titleHindi}",
                    color = Color.White.copy(alpha = 0.9f),
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
                LinearProgressIndicator(
                    progress = { affinityLevel.progress },
                    modifier = Modifier
                        .weight(1f)
                        .height(5.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = MayaPinkPrimary,
                    trackColor = Color.White.copy(alpha = 0.12f)
                )
                Text(
                    text = "${affinityXp} XP",
                    color = MayaJarvisCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Bold
                )
            }
        }
    }
}
