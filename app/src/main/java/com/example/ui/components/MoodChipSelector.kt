package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MayaMood
import com.example.ui.theme.MayaDarkSurfaceElevated

@Composable
fun MoodChipSelector(
    selectedMood: MayaMood,
    onMoodSelected: (MayaMood) -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 6.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        MayaMood.entries.forEach { mood ->
            val isSelected = mood == selectedMood
            val bgModifier = if (isSelected) {
                Modifier.background(
                    brush = Brush.horizontalGradient(
                        listOf(mood.primaryColor.copy(alpha = 0.35f), mood.secondaryColor.copy(alpha = 0.25f))
                    ),
                    shape = RoundedCornerShape(16.dp)
                ).border(
                    width = 1.5.dp,
                    brush = Brush.horizontalGradient(listOf(mood.primaryColor, mood.secondaryColor)),
                    shape = RoundedCornerShape(16.dp)
                )
            } else {
                Modifier.background(
                    color = MayaDarkSurfaceElevated.copy(alpha = 0.8f),
                    shape = RoundedCornerShape(16.dp)
                ).border(
                    width = 1.dp,
                    color = Color.White.copy(alpha = 0.08f),
                    shape = RoundedCornerShape(16.dp)
                )
            }

            Box(
                modifier = Modifier
                    .then(bgModifier)
                    .clickable { onMoodSelected(mood) }
                    .padding(horizontal = 12.dp, vertical = 8.dp)
                    .testTag("mood_chip_${mood.name.lowercase()}"),
                contentAlignment = Alignment.Center
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Text(
                        text = mood.emoji,
                        fontSize = 14.sp
                    )
                    Text(
                        text = "${mood.hindiName} (${mood.englishName})",
                        color = if (isSelected) Color.White else Color.White.copy(alpha = 0.7f),
                        fontSize = 12.sp,
                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal
                    )
                }
            }
        }
    }
}
