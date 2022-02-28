package de.erikspall.audiobookapp.data.data_source.local.disk

import android.net.Uri

data class AudiobookMetadata (
    val uri: Uri,
    val coverUri: Uri,
    val title: String,
    val duration: Long,
    val author: String?,
    val narrator: String?,
    val position: Long,
    val genre: String,
    val chapters: List<ChapterMetadata> //As Json
)