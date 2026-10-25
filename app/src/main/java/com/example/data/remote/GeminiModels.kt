package com.example.data.remote

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class GeminiApiRequest(
    @param:Json(name = "contents") val contents: List<ContentItem>,
    @param:Json(name = "systemInstruction") val systemInstruction: ContentItem? = null,
    @param:Json(name = "generationConfig") val generationConfig: GenerationConfigItem? = null
)

@JsonClass(generateAdapter = true)
data class ContentItem(
    @param:Json(name = "role") val role: String? = null,
    @param:Json(name = "parts") val parts: List<PartItem>
)

@JsonClass(generateAdapter = true)
data class PartItem(
    @param:Json(name = "text") val text: String
)

@JsonClass(generateAdapter = true)
data class GenerationConfigItem(
    @param:Json(name = "temperature") val temperature: Float? = null,
    @param:Json(name = "topP") val topP: Float? = null,
    @param:Json(name = "topK") val topK: Int? = null,
    @param:Json(name = "maxOutputTokens") val maxOutputTokens: Int? = null
)

@JsonClass(generateAdapter = true)
data class GeminiApiResponse(
    @param:Json(name = "candidates") val candidates: List<CandidateItem>? = null
)

@JsonClass(generateAdapter = true)
data class CandidateItem(
    @param:Json(name = "content") val content: ContentItem? = null
)
