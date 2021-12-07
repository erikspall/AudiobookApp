package de.erikspall.audiobookapp.data.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey

/**
 * A data class to represent the audiobook database entity
 */
@Entity(
    tableName = "audiobook",
    foreignKeys = [ForeignKey(
        entity = Person::class,
        parentColumns = arrayOf("personId"),
        childColumns = arrayOf("authorId"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    ), ForeignKey(
        entity = Person::class,
        parentColumns = arrayOf("personId"),
        childColumns = arrayOf("readerId"),
        onUpdate = ForeignKey.CASCADE,
        onDelete = ForeignKey.CASCADE
    )]
)
data class Audiobook (
    /* data that shows in library */
    @PrimaryKey(autoGenerate = true) val audiobookId: Long = 0,
    val uri: String,
    val title: String,
    val duration: Long,
    val authorId: Long? = null,
    val readerId: Long? = null,
    val position: Long
    /* more meta data can go here */
)