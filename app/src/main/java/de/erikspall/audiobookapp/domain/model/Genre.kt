package de.erikspall.audiobookapp.domain.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "genre")
data class Genre (
    @PrimaryKey(autoGenerate = true) val genreId: Long = 0,
    @ColumnInfo(name = "name")
    val name: String
    )