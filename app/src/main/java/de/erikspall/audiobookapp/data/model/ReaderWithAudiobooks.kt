package de.erikspall.audiobookapp.data.model

import androidx.room.Embedded
import androidx.room.Relation

data class ReaderWithAudiobooks (
    @Embedded
    val reader: Person,
    @Relation(
        parentColumn = "personId",
        entityColumn = "readerId"
    )
    val audiobooks: List<Audiobook>
)