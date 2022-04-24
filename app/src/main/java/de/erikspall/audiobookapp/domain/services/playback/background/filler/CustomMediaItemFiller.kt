package de.erikspall.audiobookapp.domain.services.playback.background.filler

import android.annotation.SuppressLint
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaSession
import de.erikspall.audiobookapp.data.source.local.player_controller.MediaItemTree

class CustomMediaItemFiller : MediaSession.MediaItemFiller {
    @SuppressLint("UnsafeOptInUsageError")
    override fun fillInLocalConfiguration(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItem: MediaItem
    ): MediaItem {
        return MediaItemTree.getItem(mediaItem.mediaId) ?: mediaItem
    }
}
