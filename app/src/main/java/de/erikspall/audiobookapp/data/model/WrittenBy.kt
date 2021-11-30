package de.erikspall.audiobookapp.data.model

import androidx.room.*

@Entity(primaryKeys = [
    "audiobookId",
    "personId"
])
data class WrittenBy (
    val audiobookId: Int,
    val personId:Int
)

data class AuthorWithAudiobooks(
    @Embedded val person: Person,
    @Relation(
        parentColumn = "personId",
        entityColumn = "audiobookId",
        associateBy = Junction(ReadBy::class)
    )
    val audiobooks: List<Audiobook>
)

data class AudiobookWithAuthors(
    @Embedded val audiobook: Audiobook,
    @Relation(
        parentColumn = "audiobookId",
        entityColumn = "personId",
        associateBy = Junction(ReadBy::class)
    )
    val authors: List<Person>
)