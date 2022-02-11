package de.erikspall.audiobookapp.data.model

import androidx.room.Embedded
import androidx.room.Junction
import androidx.room.Relation

data class AudiobookWithInfo (
    @Embedded
    val audiobook: Audiobook,
    @Relation(
        parentColumn = "authorId", // Column in Audiobook
        entityColumn = "personId" // Column in Person
    )
    val author: Person,

    @Relation(
    parentColumn = "readerId", // Column in Audiobook
    entityColumn = "personId" // Column in Person
    )
    val narrator: Person,

    @Relation(
        parentColumn = "audiobookId", // Column in Audiobook
        entityColumn = "audiobookId" // Column in Chapter
    )
    val chapters: List<Chapter>,

    @Relation(
    parentColumn = "audiobookId",
    entityColumn = "genreId",
    associateBy = Junction(BelongsTo::class)
    )
    val genres: List<Genre>
)