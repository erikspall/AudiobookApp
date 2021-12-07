package de.erikspall.audiobookapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

@Entity(
    tableName = "chapter",
    foreignKeys = [ForeignKey(
        entity = Audiobook::class,
        parentColumns = arrayOf("audiobookId"),
        childColumns = arrayOf("audiobookId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Chapter (
    @PrimaryKey(autoGenerate = true) val chapterId: Long = 0,
    val audiobookId: Long,
    val time_base: String,
    val start: Double,
    val start_time: String,
    val end: Double,
    val end_time: String,
    val title: String
)
