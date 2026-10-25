package com.example.ui.components

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.ui.theme.MayaJarvisCyan
import com.example.ui.theme.MayaPinkPrimary

@Composable
fun HoloAudioWaveform(
    isSpeaking: Boolean,
    primaryColor: Color = MayaPinkPrimary,
    secondaryColor: Color = MayaJarvisCyan,
    maxHeight: Dp = 24.dp,
    barCount: Int = 9,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "waveform_anim")

    // Generate animation phases for each bar
    val barDelays = listOf(0, 150, 300, 450, 200, 350, 100, 250, 400)

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        for (i in 0 until barCount) {
            val delay = barDelays.getOrElse(i) { i * 60 }
            val animatedHeightFraction by infiniteTransition.animateFloat(
                initialValue = 0.2f,
                targetValue = if (isSpeaking) 1.0f else 0.2f,
                animationSpec = infiniteRepeatable(
                    animation = tween(durationMillis = 350 + (i % 3) * 100, delayMillis = delay, easing = FastOutSlowInEasing),
                    repeatMode = RepeatMode.Reverse
                ),
                label = "bar_$i"
            )

            val currentHeight = if (isSpeaking) {
                (maxHeight * animatedHeightFraction).coerceAtLeast(4.dp)
            } else {
                4.dp
            }

            Box(
                modifier = Modifier
                    .width(3.dp)
                    .height(currentHeight)
                    .background(
                        brush = Brush.verticalGradient(
                            listOf(primaryColor, secondaryColor)
                        ),
                        shape = RoundedCornerShape(2.dp)
                    )
            )
        }
    }
}
