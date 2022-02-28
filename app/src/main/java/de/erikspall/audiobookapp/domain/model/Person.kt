package de.erikspall.audiobookapp.domain.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.*

@Entity(tableName = "person")
data class Person (
    @PrimaryKey(autoGenerate = true) val personId: Long = 0,
    val firstName: String,
    val lastName: String
) {
    override fun toString(): String {
        return "Person: {\n\t $firstName, \n\t $lastName \n}"
    }

    override fun equals(other: Any?): Boolean {
        return if (other is Person) {
            other.personId == this.personId &&
            other.firstName == this.firstName && other.lastName == this.lastName
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(this.personId, this.firstName, this.lastName)
    }

}