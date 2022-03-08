package de.erikspall.audiobookapp.domain.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.PrimaryKey
import java.util.*

@Entity(
    tableName = "chapter",
    foreignKeys = [ForeignKey(
        entity = Audiobook::class,
        parentColumns = arrayOf("audiobookId"),
        childColumns = arrayOf("audiobookId"),
        onDelete = ForeignKey.CASCADE,
        onUpdate = ForeignKey.CASCADE
    )]
)
data class Chapter(
    @PrimaryKey(autoGenerate = true) val chapterId: Long = 0,
    val audiobookId: Long,
    val time_base: String,
    val start: Double,
    val start_time: String,
    val end: Double,
    val end_time: String,
    val title: String,
    val isPlaying: Boolean = false
) {
    override fun equals(other: Any?): Boolean {
        return if (other is Chapter) {
            (this.audiobookId == other.audiobookId &&
                    this.time_base == other.time_base &&
                    this.start == other.start &&
                    this.start_time == other.start_time &&
                    this.end == other.end &&
                    this.end_time == other.end_time &&
                    this.title == other.title &&
                    this.isPlaying == other.isPlaying)
        } else false
    }

    override fun hashCode(): Int {
        return Objects.hash(
            audiobookId,
            time_base,
            start,
            start_time,
            end,
            end_time,
            title,
            isPlaying
        )
    }
}
