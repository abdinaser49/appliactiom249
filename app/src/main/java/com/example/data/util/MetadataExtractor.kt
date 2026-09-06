package com.example.data.util

import com.example.data.model.ExtractedMetadata
import com.example.data.model.LessonType

object MetadataExtractor {

    /**
     * Determines lesson media type from content URL or file extension
     */
    fun detectLessonType(url: String): LessonType {
        val lower = url.lowercase()
        return when {
            lower.endsWith(".mp4") || lower.endsWith(".mov") || lower.endsWith(".webm") || lower.endsWith(".mkv") -> LessonType.VIDEO
            lower.endsWith(".mp3") || lower.endsWith(".m4a") || lower.endsWith(".wav") || lower.endsWith(".aac") -> LessonType.AUDIO
            lower.endsWith(".pdf") || lower.endsWith(".doc") || lower.endsWith(".docx") -> LessonType.PDF
            lower.contains("/video/") || lower.contains("videos") -> LessonType.VIDEO
            lower.contains("/audio/") || lower.contains("audio") -> LessonType.AUDIO
            else -> LessonType.VIDEO
        }
    }

    /**
     * Extracts readable title from media filename if needed
     */
    fun extractTitleFromUrl(url: String): String {
        return try {
            val filename = url.substringAfterLast("/").substringBeforeLast(".")
            filename.replace("_", " ").replace("-", " ").capitalizeWords()
        } catch (e: Exception) {
            "Cashar Diini ah"
        }
    }

    private fun String.capitalizeWords(): String = split(" ").joinToString(" ") { word ->
        word.replaceFirstChar { if (it.isLowerCase()) it.titlecase() else it.toString() }
    }

    fun extractMetadata(url: String): ExtractedMetadata {
        val type = detectLessonType(url)
        val title = extractTitleFromUrl(url)
        return ExtractedMetadata(
            title = title,
            type = type,
            thumbnailUrl = "https://images.unsplash.com/photo-1609599006353-e629aaabfeae?w=600",
            duration = "15:00"
        )
    }
}
