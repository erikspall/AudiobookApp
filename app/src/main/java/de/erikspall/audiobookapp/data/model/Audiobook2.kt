package de.erikspall.audiobookapp.data.model

import android.net.Uri
import androidx.core.net.UriCompat
import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * A data class to represent the audiobook database entity
 */
data class Audiobook2 (
    /* data that shows in library */
    val id: Long,
    val uri: Uri,
    val title: String,
    val duration: Int,
    val artist: String
)