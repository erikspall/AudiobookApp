package de.erikspall.audiobookapp.data.model

import androidx.room.*

@Entity(primaryKeys = [
    "audiobookId",
    "personId"
])
data class ReadBy (
    val audiobookId: Int,
    val personId:Int
)

data class ReaderWithAudiobooks(
    @Embedded val reader: Person,
    @Relation(
        parentColumn = "personId",
        entityColumn = "audiobookId",
        associateBy = Junction(ReadBy::class)
    )
    val audiobooks: List<Audiobook>
)

data class AudiobookWithReaders(
    @Embedded val audiobook: Audiobook,
    @Relation(
        parentColumn = "audiobookId",
        entityColumn = "personId",
        associateBy = Junction(ReadBy::class)
    )
    val readers: List<Person>
)