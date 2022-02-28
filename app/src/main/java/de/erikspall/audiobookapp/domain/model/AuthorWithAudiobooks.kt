package de.erikspall.audiobookapp.domain.model

import androidx.room.Embedded
import androidx.room.Relation

data class AuthorWithAudiobooks (
    @Embedded
    val author: Person,
    @Relation(
        parentColumn = "personId",
        entityColumn = "authorId"
    )
    val audiobooks: List<Audiobook>
)