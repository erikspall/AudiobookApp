package de.erikspall.audiobookapp.domain.const

object Player {
    /** The player is idle, and must be prepared before it will play the media. */
    const val STATE_IDLE = androidx.media3.common.Player.STATE_IDLE

    /** The player is not able to immediately play the media,
     *  but is doing work toward being able to do so.
     *  This state typically occurs when the player needs to buffer more data before playback
     *  can start. */
    const val STATE_BUFFERING = androidx.media3.common.Player.STATE_BUFFERING

    /** The player is able to immediately play from its current position.
     *  The player will be playing if getPlayWhenReady() is true, and paused otherwise.
     */
    const val STATE_READY = androidx.media3.common.Player.STATE_READY

    /** The player has finished playing the media. */
    const val STATE_ENDED = androidx.media3.common.Player.STATE_ENDED

    // Custom
    /**
     * This is the default playback state and indicates that no media has been added yet,
     * or the performer has been reset and has no content to play.
     */
    const val STATE_NONE = -1

    /** State indicating this item is currently paused. */
    const val STATE_PAUSED = -2

    /** State indicating this item is currently playing. */
    const val STATE_PLAYING = -3
}