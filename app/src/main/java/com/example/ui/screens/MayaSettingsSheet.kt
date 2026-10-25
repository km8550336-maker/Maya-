package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.VolumeUp
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.RecordVoiceOver
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import com.example.ui.theme.MayaDarkSurface
import com.example.ui.theme.MayaDarkSurfaceElevated
import com.example.ui.theme.MayaJarvisCyan
import com.example.ui.theme.MayaPinkGlow
import com.example.ui.theme.MayaPinkPrimary

@Composable
fun MayaSettingsSheet(
    preferences: MayaPreferences,
    onDismiss: () -> Unit,
    onTestVoice: () -> Unit,
    onNicknameChanged: (String) -> Unit,
    onAutoSpeakToggled: (Boolean) -> Unit,
    onPitchChanged: (Float) -> Unit,
    onSpeedChanged: (Float) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val affinity = preferences.affinityLevel
    val nicknames = listOf("मेरे हीरो", "जान", "Boss", "Aap (आप)")

    Box(
        modifier = modifier
            .fillMaxWidth()
            .background(
                color = MayaDarkSurface,
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            )
            .border(
                width = 1.dp,
                brush = Brush.verticalGradient(
                    listOf(MayaPinkPrimary.copy(alpha = 0.5f), Color.Transparent)
                ),
                shape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
            )
            .padding(20.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.spacedBy(18.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Favorite,
                        contentDescription = null,
                        tint = MayaPinkPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                    Text(
                        text = "माया सेटिंग्स और रिश्ता (Affinity)",
                        color = Color.White,
                        fontWeight = FontWeight.Bold,
                        fontSize = 18.sp
                    )
                }

                IconButton(
                    onClick = onDismiss,
                    modifier = Modifier.testTag("close_settings_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Close,
                        contentDescription = "Close",
                        tint = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // Affinity Card
            Card(
                colors = CardDefaults.cardColors(containerColor = MayaDarkSurfaceElevated),
                shape = RoundedCornerShape(18.dp)
            ) {
                Column(
                    modifier = Modifier.padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(
                            text = "रिश्ता स्तर: ${affinity.titleHindi}",
                            fontWeight = FontWeight.Bold,
                            color = Color.White,
                            fontSize = 15.sp
                        )
                        Text(
                            text = "${preferences.affinityXp} XP",
                            fontWeight = FontWeight.Bold,
                            color = MayaPinkPrimary,
                            fontSize = 14.sp
                        )
                    }

                    LinearProgressIndicator(
                        progress = { affinity.progress },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp)),
                        color = MayaPinkPrimary,
                        trackColor = Color.White.copy(alpha = 0.1f)
                    )

                    Text(
                        text = affinity.description,
                        color = Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp
                    )
                }
            }

            // Endearment / Nickname selector
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                Text(
                    text = "माया आपको किस नाम से पुकारे?",
                    fontWeight = FontWeight.SemiBold,
                    color = Color.White,
                    fontSize = 14.sp
                )

                Row(
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    nicknames.forEach { nick ->
                        val isSelected = preferences.userNickname == nick
                        Box(
                            modifier = Modifier
                                .weight(1f)
                                .background(
                                    color = if (isSelected) MayaPinkPrimary.copy(alpha = 0.3f) else MayaDarkSurfaceElevated,
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .border(
                                    width = 1.dp,
                                    color = if (isSelected) MayaPinkPrimary else Color.White.copy(alpha = 0.1f),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                .clickable { onNicknameChanged(nick) }
                                .padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = nick,
                                color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        }
                    }
                }
            }

            // Voice Engine Settings
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = MayaJarvisCyan,
                            modifier = Modifier.size(20.dp)
                        )
                        Text(
                            text = "माया की आवाज़ (Hindi TTS)",
                            color = Color.White,
                            fontWeight = FontWeight.SemiBold,
                            fontSize = 14.sp
                        )
                    }

                    Switch(
                        checked = preferences.autoSpeakEnabled,
                        onCheckedChange = onAutoSpeakToggled,
                        colors = SwitchDefaults.colors(
                            checkedThumbColor = Color.White,
                            checkedTrackColor = MayaPinkPrimary
                        ),
                        modifier = Modifier.testTag("auto_speak_switch")
                    )
                }

                // Speech Pitch Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("स्वर की मिठास (Pitch):", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        Text("${String.format("%.1f", preferences.speechPitchOffset)}x", color = MayaJarvisCyan, fontSize = 12.sp)
                    }
                    Slider(
                        value = preferences.speechPitchOffset,
                        onValueChange = onPitchChanged,
                        valueRange = 0.8f..1.4f,
                        colors = SliderDefaults.colors(
                            thumbColor = MayaPinkPrimary,
                            activeTrackColor = MayaPinkPrimary,
                            inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                        )
                    )
                }

                // Speech Speed Slider
                Column(verticalArrangement = Arrangement.spacedBy(4.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text("बोलने की गति (Speech Rate):", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp)
                        Text("${String.format("%.1f", preferences.speechRateOffset)}x", color = MayaJarvisCyan, fontSize = 12.sp)
                    }
                    Slider(
                        value = preferences.speechRateOffset,
                        onValueChange = onSpeedChanged,
                        valueRange = 0.8f..1.3f,
                        colors = SliderDefaults.colors(
                            thumbColor = MayaJarvisCyan,
                            activeTrackColor = MayaJarvisCyan,
                            inactiveTrackColor = Color.White.copy(alpha = 0.15f)
                        )
                    )
                }

                Button(
                    onClick = onTestVoice,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("test_voice_button"),
                    colors = ButtonDefaults.buttonColors(containerColor = MayaDarkSurfaceElevated),
                    shape = RoundedCornerShape(14.dp)
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.VolumeUp,
                        contentDescription = null,
                        tint = MayaPinkPrimary,
                        modifier = Modifier.size(18.dp)
                    )
                    Spacer(modifier = Modifier.size(8.dp))
                    Text("आवाज़ टेस्ट करें (Listen to Maya)", color = Color.White, fontSize = 13.sp)
                }
            }

            // Cognitive Engine Info
            Card(
                colors = CardDefaults.cardColors(containerColor = MayaDarkSurfaceElevated),
                shape = RoundedCornerShape(16.dp)
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Psychology,
                        contentDescription = null,
                        tint = MayaJarvisCyan,
                        modifier = Modifier.size(24.dp)
                    )
                    Column {
                        Text(
                            text = "JARVIS Cognitive Core",
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 13.sp
                        )
                        Text(
                            text = "सक्रिय मॉडल: Gemini 3.5 Flash • देवनागरी हिंदी वॉयस सिंथेसिस",
                            color = Color.White.copy(alpha = 0.65f),
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }
    }
}
