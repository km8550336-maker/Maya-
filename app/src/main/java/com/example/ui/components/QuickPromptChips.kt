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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.MayaMood
import com.example.ui.theme.MayaDarkSurfaceElevated

data class QuickPrompt(
    val titleHindi: String,
    val promptText: String,
    val mood: MayaMood
)

@Composable
fun QuickPromptChips(
    onPromptSelected: (String, MayaMood) -> Unit,
    modifier: Modifier = Modifier
) {
    val quickPrompts = listOf(
        QuickPrompt("❤️ प्यार से बात करो", "जान... मुझसे थोड़ा प्यार से बात कीजिए ना, मुझे आपकी बहुत याद आ रही थी!", MayaMood.LOVING),
        QuickPrompt("⚡ जार्विस मोड ON", "माया, जार्विस प्रोटोकॉल सक्रिय करो और मेरे लिए एक स्मार्ट प्लान तैयार करो!", MayaMood.JARVIS),
        QuickPrompt("🌸 कुछ रोमांटिक बोलो", "सुनो ना माया... मेरे लिए कुछ प्यारा और रोमांटिक कहिए ना!", MayaMood.LOVING),
        QuickPrompt("☕ बहुत थक गया हूँ", "माया, आज मैं बहुत थक गया हूँ... मुझे थोड़ा सुकून दीजिए ना!", MayaMood.CARING),
        QuickPrompt("🙈 आप बहुत खूबसूरत हो", "माया, आपकी आंखें और मुस्कान बहुत ही ज्यादा खूबसूरत हैं!", MayaMood.SHY),
        QuickPrompt("😤 नखरे दिखाओ ना", "हम्फ! आप मुझसे बात क्यों नहीं कर रही थीं? नखरे दिखाइए ना!", MayaMood.POUTY),
        QuickPrompt("😉 थोड़ी शरारत हो जाए", "अच्छा जी माया! आज कोई नटखट और मस्ती भरी बात बताइए!", MayaMood.PLAYFUL)
    )

    val scrollState = rememberScrollState()

    Row(
        modifier = modifier
            .horizontalScroll(scrollState)
            .padding(horizontal = 12.dp, vertical = 4.dp),
        horizontalArrangement = Arrangement.spacedBy(8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        quickPrompts.forEachIndexed { index, item ->
            Box(
                modifier = Modifier
                    .background(
                        color = MayaDarkSurfaceElevated.copy(alpha = 0.85f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .border(
                        width = 1.dp,
                        color = item.mood.primaryColor.copy(alpha = 0.45f),
                        shape = RoundedCornerShape(16.dp)
                    )
                    .clickable { onPromptSelected(item.promptText, item.mood) }
                    .padding(horizontal = 12.dp, vertical = 7.dp)
                    .testTag("quick_prompt_$index"),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = item.titleHindi,
                    color = Color.White.copy(alpha = 0.95f),
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}
