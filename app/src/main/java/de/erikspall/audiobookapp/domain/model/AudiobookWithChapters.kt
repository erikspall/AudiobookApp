package de.erikspall.audiobookapp.domain.model

import androidx.room.Embedded
import androidx.room.Relation


data class AudiobookWithChapters (
    @Embedded
    val audiobook: Audiobook,
    @Relation(
        parentColumn = "audiobookId", // Column in Audiobook
        entityColumn = "audiobookId" // Column in Chapter
    )
    val chapters: List<Chapter>
)