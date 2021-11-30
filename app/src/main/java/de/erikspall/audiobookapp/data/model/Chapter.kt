package de.erikspall.audiobookapp.data.model
import kotlinx.serialization.Serializable

@Serializable
data class Chapter (
    val id: Int,
    val time_base: String,
    val start: Long,
    val start_time: String,
    val end: Long,
    val end_time: String,
    val title: String
) {
    override fun toString(): String {
        return "[$id, $title]"
    }
}
