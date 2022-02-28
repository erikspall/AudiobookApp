package de.erikspall.audiobookapp.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class ReaderWithAudiobooks (
    @Embedded
    val narrator: Person,
    @Relation(
        parentColumn = "personId",
        entityColumn = "narratorId"
    )
    val audiobooks: List<Audiobook>
)