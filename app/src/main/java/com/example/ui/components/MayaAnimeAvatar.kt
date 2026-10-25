package com.example.ui.components

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.LinearEasing
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.rotate
import com.example.data.model.MayaMood
import com.example.ui.theme.MayaDarkBackground
import com.example.ui.theme.MayaJarvisCyan
import com.example.ui.theme.MayaPinkGlow
import com.example.ui.theme.MayaPinkPrimary
import kotlinx.coroutines.delay
import kotlin.math.cos
import kotlin.math.sin

@Composable
fun MayaAnimeAvatar(
    mood: MayaMood,
    isSpeaking: Boolean,
    onAvatarTapped: () -> Unit,
    modifier: Modifier = Modifier
) {
    val infiniteTransition = rememberInfiniteTransition(label = "avatar_anim")

    // Subtle breathing / idle floating motion
    val breatheOffset by infiniteTransition.animateFloat(
        initialValue = -6f,
        targetValue = 6f,
        animationSpec = infiniteRepeatable(
            animation = tween(2800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "breathe"
    )

    // Holographic ring rotation for Jarvis mode & high tech aura
    val holoRotation by infiniteTransition.animateFloat(
        initialValue = 0f,
        targetValue = 360f,
        animationSpec = infiniteRepeatable(
            animation = tween(14000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "holo_rotation"
    )

    // Holographic counter ring rotation
    val counterHoloRotation by infiniteTransition.animateFloat(
        initialValue = 360f,
        targetValue = 0f,
        animationSpec = infiniteRepeatable(
            animation = tween(9000, easing = LinearEasing),
            repeatMode = RepeatMode.Restart
        ),
        label = "counter_holo_rotation"
    )

    // Pulse glow animation
    val pulseGlow by infiniteTransition.animateFloat(
        initialValue = 0.5f,
        targetValue = 1.0f,
        animationSpec = infiniteRepeatable(
            animation = tween(1800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_glow"
    )

    // Speaking mouth animation when TTS is active
    val mouthOpenRatio by infiniteTransition.animateFloat(
        initialValue = 0.1f,
        targetValue = if (isSpeaking) 0.85f else 0.1f,
        animationSpec = infiniteRepeatable(
            animation = tween(160, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "mouth"
    )

    // Blinking logic: eyes close briefly every 3-4 seconds
    var isBlinking by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) {
        while (true) {
            delay((2800..4500).random().toLong())
            isBlinking = true
            delay(130)
            isBlinking = false
        }
    }

    // Touch tap burst effect
    val tapScale = remember { Animatable(1f) }
    var tapBurstCount by remember { mutableFloatStateOf(0f) }

    Box(
        modifier = modifier
            .aspectRatio(9f / 16f)
            .fillMaxWidth()
            .clickable(
                interactionSource = remember { MutableInteractionSource() },
                indication = null
            ) {
                tapBurstCount += 1f
                onAvatarTapped()
            },
        contentAlignment = Alignment.Center
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val canvasW = size.width
            val canvasH = size.height
            val centerX = canvasW / 2f
            val centerY = canvasH * 0.44f + breatheOffset

            // 1. Cyber Ambient Background & Holographic Glow
            drawCyberBackground(canvasW, canvasH, mood, pulseGlow)

            // 2. Jarvis Holographic Rings & Data Circles
            drawJarvisHoloRings(centerX, centerY, holoRotation, counterHoloRotation, mood, pulseGlow)

            // 3. Floating Digital Sparkles / Heart Particles
            drawFloatingParticles(canvasW, canvasH, mood, pulseGlow)

            // 4. Maya Body & High-Tech Anime Attire
            drawAnimeBody(centerX, centerY, mood)

            // 5. Hair (Back Strands)
            drawBackHair(centerX, centerY)

            // 6. Anime Face & Soft Skin
            drawAnimeHead(centerX, centerY)

            // 7. Dynamic Blushing Cheeks
            drawAnimeBlush(centerX, centerY, mood, pulseGlow)

            // 8. Sparkling Expressive Anime Eyes
            val effectiveBlinking = isBlinking || (mood == MayaMood.PLAYFUL && !isSpeaking && (pulseGlow > 0.8f))
            drawAnimeEyes(centerX, centerY, mood, isBlinking = isBlinking, isWinking = (mood == MayaMood.PLAYFUL && isSpeaking))

            // 9. Expressive Mouth (Speaking / Pouting / Smiling)
            drawAnimeMouth(centerX, centerY, mood, mouthOpenRatio, isSpeaking)

            // 10. Front Hair & Cyber Hair Accessories / Holographic Headset
            drawFrontHairAndAccessories(centerX, centerY, mood, holoRotation)

            // 11. Holographic HUD Diagnostic Overlay
            drawHudDiagnostics(canvasW, canvasH, mood)
        }
    }
}

private fun DrawScope.drawCyberBackground(
    w: Float,
    h: Float,
    mood: MayaMood,
    pulse: Float
) {
    // Deep starry gradient
    drawRect(
        brush = Brush.verticalGradient(
            colors = listOf(
                Color(0xFF07040E),
                Color(0xFF120822),
                Color(0xFF190C30)
            )
        )
    )

    // Dynamic aura burst from center
    val auraColor = mood.primaryColor.copy(alpha = 0.18f * pulse)
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(auraColor, Color.Transparent),
            center = Offset(w * 0.5f, h * 0.44f),
            radius = w * 0.75f
        ),
        radius = w * 0.75f,
        center = Offset(w * 0.5f, h * 0.44f)
    )
}

private fun DrawScope.drawJarvisHoloRings(
    cx: Float,
    cy: Float,
    rot: Float,
    counterRot: Float,
    mood: MayaMood,
    pulse: Float
) {
    val ringColor = if (mood == MayaMood.JARVIS) MayaJarvisCyan else mood.primaryColor
    val radiusOuter = size.width * 0.42f
    val radiusInner = size.width * 0.38f

    // Outer rotating segmented arc
    rotate(rot, pivot = Offset(cx, cy)) {
        drawArc(
            color = ringColor.copy(alpha = 0.35f * pulse),
            startAngle = 15f,
            sweepAngle = 100f,
            useCenter = false,
            topLeft = Offset(cx - radiusOuter, cy - radiusOuter),
            size = Size(radiusOuter * 2, radiusOuter * 2),
            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
        )
        drawArc(
            color = ringColor.copy(alpha = 0.45f * pulse),
            startAngle = 195f,
            sweepAngle = 90f,
            useCenter = false,
            topLeft = Offset(cx - radiusOuter, cy - radiusOuter),
            size = Size(radiusOuter * 2, radiusOuter * 2),
            style = Stroke(width = 2.5f, cap = StrokeCap.Round)
        )

        // Holographic tick marks
        for (i in 0 until 12) {
            val angle = i * 30.0 * Math.PI / 180.0
            val x1 = cx + (radiusOuter - 6f) * cos(angle).toFloat()
            val y1 = cy + (radiusOuter - 6f) * sin(angle).toFloat()
            val x2 = cx + (radiusOuter + 6f) * cos(angle).toFloat()
            val y2 = cy + (radiusOuter + 6f) * sin(angle).toFloat()
            drawLine(
                color = ringColor.copy(alpha = 0.28f),
                start = Offset(x1, y1),
                end = Offset(x2, y2),
                strokeWidth = 1.8f
            )
        }
    }

    // Inner counter-rotating ring
    rotate(counterRot, pivot = Offset(cx, cy)) {
        drawArc(
            color = mood.secondaryColor.copy(alpha = 0.30f),
            startAngle = 60f,
            sweepAngle = 80f,
            useCenter = false,
            topLeft = Offset(cx - radiusInner, cy - radiusInner),
            size = Size(radiusInner * 2, radiusInner * 2),
            style = Stroke(width = 1.5f, cap = StrokeCap.Round)
        )
        drawArc(
            color = mood.secondaryColor.copy(alpha = 0.30f),
            startAngle = 240f,
            sweepAngle = 70f,
            useCenter = false,
            topLeft = Offset(cx - radiusInner, cy - radiusInner),
            size = Size(radiusInner * 2, radiusInner * 2),
            style = Stroke(width = 1.5f, cap = StrokeCap.Round)
        )
    }
}

private fun DrawScope.drawFloatingParticles(
    w: Float,
    h: Float,
    mood: MayaMood,
    pulse: Float
) {
    val particleColor = mood.primaryColor
    val count = 8
    for (i in 0 until count) {
        val px = (w * (0.15f + 0.1f * i)).let { (it + pulse * 20f * (if (i % 2 == 0) 1 else -1)) % w }
        val py = (h * 0.25f + (i * 45f) + pulse * 15f) % (h * 0.7f)
        val pRadius = if (i % 3 == 0) 3.5f else 2.0f

        drawCircle(
            color = particleColor.copy(alpha = 0.4f * pulse),
            radius = pRadius,
            center = Offset(px, py)
        )

        // Draw diamond spark for every third particle
        if (i % 2 == 0) {
            val sparkPath = Path().apply {
                moveTo(px, py - 6f)
                lineTo(px + 4f, py)
                lineTo(px, py + 6f)
                lineTo(px - 4f, py)
                close()
            }
            drawPath(sparkPath, color = Color.White.copy(alpha = 0.5f * pulse))
        }
    }
}

private fun DrawScope.drawBackHair(cx: Float, cy: Float) {
    val hairDark = Color(0xFF1E1032)
    val hairMid = Color(0xFF2C1848)

    // Left flowing hair mass
    val leftHair = Path().apply {
        moveTo(cx - 90f, cy - 80f)
        cubicTo(cx - 150f, cy, cx - 140f, cy + 180f, cx - 110f, cy + 260f)
        cubicTo(cx - 85f, cy + 200f, cx - 70f, cy + 100f, cx - 70f, cy + 30f)
        close()
    }
    drawPath(leftHair, hairMid)

    // Right flowing hair mass
    val rightHair = Path().apply {
        moveTo(cx + 90f, cy - 80f)
        cubicTo(cx + 150f, cy, cx + 140f, cy + 180f, cx + 110f, cy + 260f)
        cubicTo(cx + 85f, cy + 200f, cx + 70f, cy + 100f, cx + 70f, cy + 30f)
        close()
    }
    drawPath(rightHair, hairMid)
}

private fun DrawScope.drawAnimeBody(cx: Float, cy: Float, mood: MayaMood) {
    // Slender Anime Neck
    val neck = Path().apply {
        moveTo(cx - 24f, cy + 60f)
        lineTo(cx - 28f, cy + 115f)
        lineTo(cx + 28f, cy + 115f)
        lineTo(cx + 24f, cy + 60f)
        close()
    }
    drawPath(neck, Color(0xFFFFDEC9))

    // High Tech Cyber Collar
    val collarColor = if (mood == MayaMood.JARVIS) MayaJarvisCyan else Color(0xFF24143D)
    val collar = Path().apply {
        moveTo(cx - 36f, cy + 110f)
        lineTo(cx + 36f, cy + 110f)
        lineTo(cx + 42f, cy + 130f)
        lineTo(cx, cy + 140f)
        lineTo(cx - 42f, cy + 130f)
        close()
    }
    drawPath(collar, collarColor)
    drawPath(collar, color = mood.primaryColor, style = Stroke(width = 2f))

    // Cyber Heart Jewel on collar
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(mood.primaryColor, mood.secondaryColor),
            center = Offset(cx, cy + 124f),
            radius = 9f
        ),
        radius = 7f,
        center = Offset(cx, cy + 124f)
    )

    // Anime Jacket / Cyber Blouse
    val jacket = Path().apply {
        moveTo(cx - 42f, cy + 130f)
        cubicTo(cx - 85f, cy + 145f, cx - 130f, cy + 180f, cx - 150f, cy + 340f)
        lineTo(cx + 150f, cy + 340f)
        cubicTo(cx + 130f, cy + 180f, cx + 85f, cy + 145f, cx + 42f, cy + 130f)
        close()
    }
    drawPath(
        jacket,
        Brush.verticalGradient(
            colors = listOf(Color(0xFF2D1B4E), Color(0xFF1A0F2E), Color(0xFF100720)),
            startY = cy + 130f,
            endY = cy + 340f
        )
    )

    // Jacket neon piping lines
    val leftPiping = Path().apply {
        moveTo(cx - 32f, cy + 135f)
        cubicTo(cx - 65f, cy + 160f, cx - 90f, cy + 220f, cx - 105f, cy + 340f)
    }
    val rightPiping = Path().apply {
        moveTo(cx + 32f, cy + 135f)
        cubicTo(cx + 65f, cy + 160f, cx + 90f, cy + 220f, cx + 105f, cy + 340f)
    }
    drawPath(leftPiping, mood.primaryColor.copy(alpha = 0.8f), style = Stroke(2.5f))
    drawPath(rightPiping, mood.primaryColor.copy(alpha = 0.8f), style = Stroke(2.5f))
}

private fun DrawScope.drawAnimeHead(cx: Float, cy: Float) {
    // Anime jaw and face shape: soft tapered chin
    val face = Path().apply {
        moveTo(cx - 66f, cy - 20f)
        cubicTo(cx - 70f, cy + 25f, cx - 55f, cy + 65f, cx, cy + 85f)
        cubicTo(cx + 55f, cy + 65f, cx + 70f, cy + 25f, cx + 66f, cy - 20f)
        cubicTo(cx + 66f, cy - 70f, cx - 66f, cy - 70f, cx - 66f, cy - 20f)
        close()
    }
    // Soft creamy anime skin
    drawPath(face, Color(0xFFFFF0E6))
    // Shadow under chin
    drawCircle(
        color = Color(0x18FF7A90),
        radius = 18f,
        center = Offset(cx, cy + 74f)
    )
}

private fun DrawScope.drawAnimeBlush(
    cx: Float,
    cy: Float,
    mood: MayaMood,
    pulse: Float
) {
    val blushBaseAlpha = when (mood) {
        MayaMood.SHY -> 0.70f + 0.15f * pulse
        MayaMood.LOVING -> 0.50f + 0.10f * pulse
        MayaMood.JOYFUL, MayaMood.PLAYFUL -> 0.38f
        MayaMood.POUTY -> 0.45f
        else -> 0.25f
    }
    val blushRadius = if (mood == MayaMood.SHY) 24f else 17f

    // Left cheek blush
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFF4081).copy(alpha = blushBaseAlpha), Color.Transparent),
            center = Offset(cx - 40f, cy + 28f),
            radius = blushRadius
        ),
        radius = blushRadius,
        center = Offset(cx - 40f, cy + 28f)
    )

    // Right cheek blush
    drawCircle(
        brush = Brush.radialGradient(
            colors = listOf(Color(0xFFFF4081).copy(alpha = blushBaseAlpha), Color.Transparent),
            center = Offset(cx + 40f, cy + 28f),
            radius = blushRadius
        ),
        radius = blushRadius,
        center = Offset(cx + 40f, cy + 28f)
    )

    // Cute manga blush lines if shy or loving
    if (mood == MayaMood.SHY || mood == MayaMood.LOVING) {
        for (i in -1..1) {
            drawLine(
                color = Color(0xFFFF1493).copy(alpha = 0.55f),
                start = Offset(cx - 43f + i * 6f, cy + 24f),
                end = Offset(cx - 38f + i * 6f, cy + 32f),
                strokeWidth = 1.8f
            )
            drawLine(
                color = Color(0xFFFF1493).copy(alpha = 0.55f),
                start = Offset(cx + 37f + i * 6f, cy + 24f),
                end = Offset(cx + 42f + i * 6f, cy + 32f),
                strokeWidth = 1.8f
            )
        }
    }
}

private fun DrawScope.drawAnimeEyes(
    cx: Float,
    cy: Float,
    mood: MayaMood,
    isBlinking: Boolean,
    isWinking: Boolean
) {
    val eyeY = cy + 6f
    val eyeSpacing = 34f

    if (isBlinking) {
        // Closed happy curving eye line
        val leftClosed = Path().apply {
            moveTo(cx - eyeSpacing - 20f, eyeY + 4f)
            cubicTo(cx - eyeSpacing - 10f, eyeY + 12f, cx - eyeSpacing + 10f, eyeY + 12f, cx - eyeSpacing + 18f, eyeY + 4f)
        }
        val rightClosed = Path().apply {
            moveTo(cx + eyeSpacing - 18f, eyeY + 4f)
            cubicTo(cx + eyeSpacing - 10f, eyeY + 12f, cx + eyeSpacing + 10f, eyeY + 12f, cx + eyeSpacing + 20f, eyeY + 4f)
        }
        drawPath(leftClosed, Color(0xFF2C1644), style = Stroke(3.5f, cap = StrokeCap.Round))
        drawPath(rightClosed, Color(0xFF2C1644), style = Stroke(3.5f, cap = StrokeCap.Round))
        return
    }

    // Left Eye
    drawSingleAnimeEye(cx - eyeSpacing, eyeY, mood, isWink = false)

    // Right Eye (can wink if playful)
    if (isWinking) {
        val rightWink = Path().apply {
            moveTo(cx + eyeSpacing - 18f, eyeY + 4f)
            cubicTo(cx + eyeSpacing - 8f, eyeY + 12f, cx + eyeSpacing + 8f, eyeY + 12f, cx + eyeSpacing + 18f, eyeY + 4f)
        }
        drawPath(rightWink, Color(0xFF2C1644), style = Stroke(4.0f, cap = StrokeCap.Round))
        // Eyelash detail
        drawLine(Color(0xFF2C1644), Offset(cx + eyeSpacing + 16f, eyeY + 4f), Offset(cx + eyeSpacing + 22f, eyeY + 1f), 2.5f)
    } else {
        drawSingleAnimeEye(cx + eyeSpacing, eyeY, mood, isWink = false)
    }

    // Cute Anime Eyebrows
    val browY = cy - 14f
    val browTilt = when (mood) {
        MayaMood.POUTY -> 5f
        MayaMood.CARING, MayaMood.SHY -> -3f
        else -> 0f
    }
    val leftBrow = Path().apply {
        moveTo(cx - eyeSpacing - 18f, browY + browTilt)
        cubicTo(cx - eyeSpacing - 5f, browY - 6f, cx - eyeSpacing + 8f, browY - 4f, cx - eyeSpacing + 18f, browY + 2f)
    }
    val rightBrow = Path().apply {
        moveTo(cx + eyeSpacing - 18f, browY + 2f)
        cubicTo(cx + eyeSpacing - 8f, browY - 4f, cx + eyeSpacing + 5f, browY - 6f, cx + eyeSpacing + 18f, browY + browTilt)
    }
    drawPath(leftBrow, Color(0xFF3B1E5C), style = Stroke(2.5f, cap = StrokeCap.Round))
    drawPath(rightBrow, Color(0xFF3B1E5C), style = Stroke(2.5f, cap = StrokeCap.Round))
}

private fun DrawScope.drawSingleAnimeEye(
    x: Float,
    y: Float,
    mood: MayaMood,
    isWink: Boolean
) {
    if (isWink) return

    val eyeW = 34f
    val eyeH = 40f

    // Sclera (White of eye)
    val eyePath = Path().apply {
        moveTo(x - eyeW / 2f, y)
        cubicTo(x - eyeW / 2f, y - eyeH * 0.45f, x + eyeW / 2f, y - eyeH * 0.45f, x + eyeW / 2f, y)
        cubicTo(x + eyeW / 2f, y + eyeH * 0.45f, x - eyeW / 2f, y + eyeH * 0.45f, x - eyeW / 2f, y)
        close()
    }
    drawPath(eyePath, Color.White)

    // Iris Gradient (Vibrant purple-magenta, or glowing cyan in Jarvis mode)
    val irisTopColor = if (mood == MayaMood.JARVIS) Color(0xFF005577) else Color(0xFF3A084E)
    val irisMidColor = if (mood == MayaMood.JARVIS) MayaJarvisCyan else Color(0xFFFF2D8D)
    val irisBottomColor = if (mood == MayaMood.JARVIS) Color(0xFF80F7FF) else Color(0xFFFF85B3)

    val irisPath = Path().apply {
        moveTo(x - 13f, y)
        cubicTo(x - 13f, y - 18f, x + 13f, y - 18f, x + 13f, y)
        cubicTo(x + 13f, y + 18f, x - 13f, y + 18f, x - 13f, y)
        close()
    }
    drawPath(
        irisPath,
        Brush.verticalGradient(
            colors = listOf(irisTopColor, irisMidColor, irisBottomColor),
            startY = y - 18f,
            endY = y + 18f
        )
    )

    // Pupil
    drawCircle(Color(0xFF130420), radius = 6f, center = Offset(x, y - 1f))

    // Big Sparkle Highlight (Top Left)
    drawCircle(Color.White, radius = 5.0f, center = Offset(x - 4f, y - 6f))

    // Small Sparkle Highlight (Bottom Right)
    drawCircle(Color.White, radius = 2.8f, center = Offset(x + 5f, y + 5f))

    // Heart sparkle in eye if LOVING or SHY mood!
    if (mood == MayaMood.LOVING || mood == MayaMood.SHY) {
        drawCircle(Color(0xFFFFC2D4), radius = 2f, center = Offset(x - 4f, y + 7f))
    }

    // Upper Eyelash line (Bold, dramatic anime lash)
    val topLash = Path().apply {
        moveTo(x - eyeW / 2f - 3f, y - 2f)
        cubicTo(x - eyeW * 0.2f, y - eyeH * 0.52f, x + eyeW * 0.2f, y - eyeH * 0.52f, x + eyeW / 2f + 5f, y - 4f)
    }
    drawPath(topLash, Color(0xFF1A0A2E), style = Stroke(4.5f, cap = StrokeCap.Round))
    // Eyelash flicks
    drawLine(Color(0xFF1A0A2E), Offset(x + eyeW / 2f + 4f, y - 4f), Offset(x + eyeW / 2f + 10f, y - 9f), 3.0f, cap = StrokeCap.Round)
}

private fun DrawScope.drawAnimeMouth(
    cx: Float,
    cy: Float,
    mood: MayaMood,
    openRatio: Float,
    isSpeaking: Boolean
) {
    val mouthY = cy + 54f

    if (isSpeaking) {
        // Open anime speaking mouth
        val openH = 6f + openRatio * 16f
        val openW = 12f + openRatio * 6f

        val openMouth = Path().apply {
            moveTo(cx - openW, mouthY)
            cubicTo(cx - openW * 0.5f, mouthY + openH, cx + openW * 0.5f, mouthY + openH, cx + openW, mouthY)
            close()
        }
        drawPath(openMouth, Color(0xFF8A1838))
        // Cute tongue inside mouth
        drawCircle(Color(0xFFFF6584), radius = openW * 0.45f, center = Offset(cx, mouthY + openH * 0.6f))
        // Mouth outline
        drawPath(openMouth, Color(0xFF380814), style = Stroke(2.0f))
    } else {
        when (mood) {
            MayaMood.POUTY -> {
                // Adorably sulking curved pout
                val pout = Path().apply {
                    moveTo(cx - 10f, mouthY + 3f)
                    cubicTo(cx - 4f, mouthY - 2f, cx + 4f, mouthY - 2f, cx + 10f, mouthY + 3f)
                }
                drawPath(pout, Color(0xFFC2185B), style = Stroke(3.0f, cap = StrokeCap.Round))
                // Lower pout lip shadow
                drawCircle(Color(0x33FF4081), radius = 4f, center = Offset(cx, mouthY + 6f))
            }
            MayaMood.PLAYFUL -> {
                // Cat/Anime teasing smile :3
                val smile = Path().apply {
                    moveTo(cx - 14f, mouthY - 2f)
                    cubicTo(cx - 7f, mouthY + 6f, cx, mouthY + 3f, cx, mouthY + 3f)
                    cubicTo(cx, mouthY + 3f, cx + 7f, mouthY + 6f, cx + 14f, mouthY - 2f)
                }
                drawPath(smile, Color(0xFFC2185B), style = Stroke(3.0f, cap = StrokeCap.Round))
            }
            else -> {
                // Gentle romantic smile
                val smile = Path().apply {
                    moveTo(cx - 13f, mouthY - 1f)
                    cubicTo(cx - 6f, mouthY + 5f, cx + 6f, mouthY + 5f, cx + 13f, mouthY - 1f)
                }
                drawPath(smile, Color(0xFFC2185B), style = Stroke(2.8f, cap = StrokeCap.Round))
                // Soft pink lip shine
                drawCircle(Color(0x40FF65B2), radius = 3.5f, center = Offset(cx, mouthY + 4f))
            }
        }
    }
}

private fun DrawScope.drawFrontHairAndAccessories(
    cx: Float,
    cy: Float,
    mood: MayaMood,
    rot: Float
) {
    val hairDark = Color(0xFF26123D)
    val hairHighlight = Color(0xFF4A2578)

    // Front Bangs (Anime fringe)
    val bangs = Path().apply {
        moveTo(cx - 68f, cy - 40f)
        cubicTo(cx - 45f, cy - 25f, cx - 35f, cy - 5f, cx - 25f, cy + 8f)
        lineTo(cx - 18f, cy - 10f)
        cubicTo(cx - 8f, cy + 12f, cx + 8f, cy + 12f, cx + 16f, cy - 12f)
        lineTo(cx + 25f, cy + 6f)
        cubicTo(cx + 38f, cy - 8f, cx + 55f, cy - 25f, cx + 68f, cy - 40f)
        cubicTo(cx + 60f, cy - 85f, cx - 60f, cy - 85f, cx - 68f, cy - 40f)
        close()
    }
    drawPath(bangs, hairDark)

    // Side Locks framing the cheeks
    val leftLock = Path().apply {
        moveTo(cx - 65f, cy - 30f)
        cubicTo(cx - 75f, cy + 20f, cx - 65f, cy + 80f, cx - 50f, cy + 120f)
        cubicTo(cx - 58f, cy + 70f, cx - 55f, cy + 20f, cx - 52f, cy - 20f)
        close()
    }
    val rightLock = Path().apply {
        moveTo(cx + 65f, cy - 30f)
        cubicTo(cx + 75f, cy + 20f, cx + 65f, cy + 80f, cx + 50f, cy + 120f)
        cubicTo(cx + 58f, cy + 70f, cx + 55f, cy + 20f, cx + 52f, cy - 20f)
        close()
    }
    drawPath(leftLock, hairDark)
    drawPath(rightLock, hairDark)

    // Hair Shimmer / Angel Ring
    val shimmerPath = Path().apply {
        moveTo(cx - 40f, cy - 50f)
        cubicTo(cx - 20f, cy - 55f, cx + 20f, cy - 55f, cx + 40f, cy - 50f)
    }
    drawPath(shimmerPath, Color(0x60FF85B3), style = Stroke(4.0f, cap = StrokeCap.Round))

    // High-Tech Cyber Hair Accessory (Left side holographic clip)
    val clipX = cx - 58f
    val clipY = cy - 44f
    val clipColor = if (mood == MayaMood.JARVIS) MayaJarvisCyan else MayaPinkPrimary

    // Diamond crystal hairpin
    val diamondClip = Path().apply {
        moveTo(clipX, clipY - 12f)
        lineTo(clipX + 10f, clipY)
        lineTo(clipX, clipY + 12f)
        lineTo(clipX - 10f, clipY)
        close()
    }
    drawPath(diamondClip, clipColor)
    drawPath(diamondClip, Color.White, style = Stroke(1.5f))

    // Glowing Holographic Headset Ribbon / Antenna
    drawArc(
        color = clipColor.copy(alpha = 0.85f),
        startAngle = 180f,
        sweepAngle = 180f,
        useCenter = false,
        topLeft = Offset(cx - 72f, cy - 82f),
        size = Size(144f, 80f),
        style = Stroke(3f, cap = StrokeCap.Round)
    )
}

private fun DrawScope.drawHudDiagnostics(w: Float, h: Float, mood: MayaMood) {
    val hudColor = if (mood == MayaMood.JARVIS) MayaJarvisCyan else mood.primaryColor
    val alpha = 0.40f

    // Top-left HUD cyber brackets
    drawLine(hudColor.copy(alpha = alpha), Offset(20f, 24f), Offset(60f, 24f), 2f)
    drawLine(hudColor.copy(alpha = alpha), Offset(20f, 24f), Offset(20f, 64f), 2f)

    // Top-right HUD cyber brackets
    drawLine(hudColor.copy(alpha = alpha), Offset(w - 60f, 24f), Offset(w - 20f, 24f), 2f)
    drawLine(hudColor.copy(alpha = alpha), Offset(w - 20f, 24f), Offset(w - 20f, 64f), 2f)

    // Bottom decorative tech dots
    for (i in 0 until 5) {
        drawCircle(
            color = hudColor.copy(alpha = 0.35f),
            radius = 2.5f,
            center = Offset(28f + i * 14f, h - 28f)
        )
    }
}
