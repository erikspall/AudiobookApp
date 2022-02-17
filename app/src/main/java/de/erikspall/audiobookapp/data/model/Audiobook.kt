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
data class Audiobook(
    /* data that shows in library */
    @PrimaryKey(autoGenerate = true) val audiobookId: Long = 0,
    val uri: String,
    val coverUri: String,
    val title: String,
    val duration: Long,
    val authorId: Long? = null,
    val readerId: Long? = null,
    val position: Long
    /* more meta data can go here */
) {
    /*override fun equals(other: Any?): Boolean {
        return if (other is Audiobook) {
            (this.audiobookId == other.audiobookId
                    && this.uri == other.uri
                    && this.title == other.title
                    && this.duration == other.duration
                    && this.authorId == other.authorId
                    && this.readerId == other.readerId
                    && this.position == other.position)

        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(this.audiobookId, this.uri, this.title, this.duration, this.authorId, this.readerId, this.position)
    }*/

    override fun toString(): String {
        return "{$uri, $coverUri, $title}"
    }
}