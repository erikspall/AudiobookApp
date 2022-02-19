package de.erikspall.audiobookapp.data.model

import androidx.room.Embedded
import androidx.room.Relation
import java.util.*

data class AudiobookWithAuthor (
    @Embedded
    val audiobook: Audiobook,
    @Relation(
        parentColumn = "authorId", // Column in Audiobook
        entityColumn = "personId" // Column in Person
    )
    val author: Person
) {

    override fun equals(other: Any?): Boolean {
        return if (other is AudiobookWithAuthor) {
            (
                    this.audiobook == other.audiobook &&
                    this.author == other.author
            )
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(this.audiobook, this.author)
    }

    override fun toString(): String {
        return "AudiobookWithAuthor{\n" +
                "\t $audiobook,\n" +
                "\t $author, \n}"
    }
}
