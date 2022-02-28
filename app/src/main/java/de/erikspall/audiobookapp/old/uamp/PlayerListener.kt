package de.erikspall.audiobookapp.old.uamp

import android.annotation.SuppressLint
import android.util.Log
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player

@SuppressLint("UnsafeOptInUsageError")
class PlayerListener(
    private val onMediaMetadataChangeEvent: (MediaMetadata) -> Unit,
    private val onPlayChangeEvent: (Boolean) -> Unit
): Player.Listener {
    private var lastMediaMetadata = MediaMetadata.EMPTY

    override fun onIsPlayingChanged(isPlaying: Boolean) {
        super.onIsPlayingChanged(isPlaying)
        onPlayChangeEvent(isPlaying)
        Log.d("StateManagement", "isPlaying: $isPlaying")
    }

    override fun onMediaMetadataChanged(mediaMetadata: MediaMetadata) {
        super.onMediaMetadataChanged(mediaMetadata)
        //Log.d("StateManagement", "Last Metadata: ${lastMediaMetadata.}")
        //TODO: Bad and hacky way to prevent this function fromk being calles twice in a row
        // what happens if title is the same, but diffrent books!!!
        if (lastMediaMetadata.title != mediaMetadata.title) {
            onMediaMetadataChangeEvent(mediaMetadata)
            lastMediaMetadata = mediaMetadata
            Log.d("StateManagement", "New Metadata: ${mediaMetadata.title}")
        }
    }


}