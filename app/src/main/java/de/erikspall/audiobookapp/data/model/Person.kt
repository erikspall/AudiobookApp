package de.erikspall.audiobookapp.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "person")
data class Person (
    @PrimaryKey(autoGenerate = true) val personId: Long = 0,
    val firstName: String,
    val lastName: String
)