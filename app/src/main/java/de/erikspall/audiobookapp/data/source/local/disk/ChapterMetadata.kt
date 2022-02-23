package de.erikspall.audiobookapp.data.source.local.disk

data class ChapterMetadata (
    val time_base: String,
    val start: Double,
    val start_time: String,
    val end: Double,
    val end_time: String,
    val title: String
)