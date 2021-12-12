package de.erikspall.audiobookapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "person")
data class Person (
    @PrimaryKey(autoGenerate = true) val personId: Long = 0,
    val firstName: String,
    val lastName: String
) {
    override fun toString(): String {
        return "$firstName $lastName"
    }

   /* override fun equals(other: Any?): Boolean {
        return if (other is Person) {
            other.firstName == this.firstName && other.lastName == this.lastName
        } else {
            false
        }
    }

    override fun hashCode(): Int {
        return Objects.hash(this.firstName, this.lastName)
    }*/
}