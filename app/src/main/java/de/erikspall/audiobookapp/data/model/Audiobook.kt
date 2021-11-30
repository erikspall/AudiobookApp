package de.erikspall.audiobookapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A data class to represent the audiobook database entity
 */
@Entity(tableName = "audiobook")
data class Audiobook (
    /* data that shows in library */
    @PrimaryKey(autoGenerate = true) val audiobookId: Int = 0,

    @ColumnInfo(name = "path")
    val audiobookPath: String,
    @ColumnInfo(name = "title")
    val audiobookTitle: String,
    @ColumnInfo(name = "progress")
    val audiobookProgress: Int
    //TODO: Add position

    /* more meta data can go here */
)