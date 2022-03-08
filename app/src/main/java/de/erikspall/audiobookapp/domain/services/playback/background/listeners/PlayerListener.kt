package de.erikspall.audiobookapp.domain.services.playback.background.listeners

import android.annotation.SuppressLint
import android.util.Log
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player

@SuppressLint("UnsafeOptInUsageError")
class PlayerListener(
    private val onPlayingChanged: (Boolean) -> Unit = {},
    private val onMediaMetadataChangeEvent: (MediaMetadata) -> Unit = {},
    private val onPlaybackState: (Int) -> Unit = {}
): Player.Listener {
    private var lastMediaMetadata = MediaMetadata.EMPTY

    /* Fired everytime the controller gets paused/resumed */
    override fun onIsPlayingChanged(isPlaying: Boolean) {
        onPlayingChanged(isPlaying)
        super.onIsPlayingChanged(isPlaying)
        Log.d("StateManagement", "isPlaying: $isPlaying")
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
        if (lastMediaMetadata.title != mediaMetadata.title) {
            onMediaMetadataChangeEvent(mediaMetadata)
            lastMediaMetadata = mediaMetadata
            Log.d("StateManagement", "New Metadata: ${mediaMetadata.title}")
        }
    }

    override fun onPlaybackStateChanged(playbackState: Int) {
        super.onPlaybackStateChanged(playbackState)
        onPlaybackState(playbackState)
    }
}