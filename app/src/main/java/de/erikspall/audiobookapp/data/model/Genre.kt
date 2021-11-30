package de.erikspall.audiobookapp.data.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genre")
data class Genre (
    @PrimaryKey(autoGenerate = true) val genreId: Int = 0,

    @ColumnInfo(name = "name")
    val genreName: String,

    )