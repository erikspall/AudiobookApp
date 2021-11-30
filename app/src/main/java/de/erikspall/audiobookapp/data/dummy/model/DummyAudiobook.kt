package de.erikspall.audiobookapp.data.dummy.model

import androidx.annotation.DrawableRes

/**
 * A data class to represent the information presented in the library cards and beyond
 */
data class DummyAudiobook (
    /* data that shows in library */
    @DrawableRes val imageResourceId: Int,
    val title: String,
    val progress: Int,
    val chipAttributes: Map<Int, List<String>>
    /* more meta data can go here */
)