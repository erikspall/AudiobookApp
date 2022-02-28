package de.erikspall.audiobookapp.domain.model

import androidx.room.Embedded
import androidx.room.Relation
import java.util.*

data class AudiobookWithPersons (
    @Embedded
    val audiobook: Audiobook,
    @Relation(
        parentColumn = "authorId", // Column in Audiobook
        entityColumn = "personId" // Column in Person
    )
    val author: Person,
    @Relation(
        parentColumn = "narratorId",
        entityColumn = "personId"
    )
    val narrator: Person
) {

    override fun equals(other: Any?): Boolean {
        return if (other is AudiobookWithPersons) {
            (
                    this.audiobook == other.audiobook &&
                    this.author == other.author &&
                     this.narrator == other.narrator
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
