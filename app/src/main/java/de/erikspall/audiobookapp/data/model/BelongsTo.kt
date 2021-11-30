package de.erikspall.audiobookapp.data.model

import androidx.room.*
import androidx.room.ForeignKey.CASCADE

@Entity(primaryKeys = [
    "audiobookId",
    "genreId"
])
data class BelongsTo (
    val audiobookId: Int,
    val genreId:Int
)

data class GenreWithAudiobooks(
    @Embedded val genre: Genre,
    @Relation(
        parentColumn = "genreId",
        entityColumn = "audiobookId",
        associateBy = Junction(BelongsTo::class)
    )
    val audiobooks: List<Audiobook>
)

data class AudiobookWithGenres(
    @Embedded val audiobook: Audiobook,
    @Relation(
        parentColumn = "audiobookId",
        entityColumn = "genreId",
        associateBy = Junction(BelongsTo::class)
    )
    val genres: List<Genre>
)