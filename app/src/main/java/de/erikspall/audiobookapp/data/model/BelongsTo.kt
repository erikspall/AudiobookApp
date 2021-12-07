package de.erikspall.audiobookapp.data.model

import androidx.room.*

@Entity(primaryKeys = [
    "audiobookId",
    "genreId"
],
foreignKeys = [ForeignKey(
    entity = Audiobook::class,
    parentColumns = arrayOf("audiobookId"),
    childColumns = arrayOf("audiobookId"),
    onUpdate = ForeignKey.CASCADE,
    onDelete = ForeignKey.CASCADE
), ForeignKey(
    entity = Genre::class,
    parentColumns = arrayOf("genreId"),
    childColumns = arrayOf("genreId"),
    onUpdate = ForeignKey.CASCADE,
    onDelete = ForeignKey.CASCADE
)])
data class BelongsTo (
    val audiobookId: Long,
    val genreId:Long
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