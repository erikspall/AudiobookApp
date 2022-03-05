package de.erikspall.audiobookapp.domain.services.playback.background.callbacks

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import androidx.media3.common.MediaItem
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import de.erikspall.audiobookapp.data.data_source.local.player_controller.MediaItemTree
import de.erikspall.audiobookapp.domain.services.playback.background.PlayerService.Companion.SEARCH_QUERY_PREFIX
import de.erikspall.audiobookapp.domain.services.playback.background.PlayerService.Companion.SEARCH_QUERY_PREFIX_COMPAT

class CustomMediaLibrarySessionCallback(
    private val setMediaItemFromSearchQuery: (String) -> Unit
) : MediaLibrarySession.MediaLibrarySessionCallback {
    @SuppressLint("UnsafeOptInUsageError")
    override fun onSetMediaUri(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        uri: Uri,
        extras: Bundle
    ): Int {
        if (uri.toString().startsWith(SEARCH_QUERY_PREFIX) ||
            uri.toString().startsWith(SEARCH_QUERY_PREFIX_COMPAT)
        ) {
            var searchQuery =
                uri.getQueryParameter("query") ?: return SessionResult.RESULT_ERROR_NOT_SUPPORTED
            setMediaItemFromSearchQuery(searchQuery)

            return SessionResult.RESULT_SUCCESS
        } else {
            return SessionResult.RESULT_ERROR_NOT_SUPPORTED
        }
    }


    @SuppressLint("UnsafeOptInUsageError")
    override fun onGetLibraryRoot(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<MediaItem>> {
        return Futures.immediateFuture(LibraryResult.ofItem(MediaItemTree.getRootItem(), params))
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onGetItem(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        mediaId: String
    ): ListenableFuture<LibraryResult<MediaItem>> {
        val item =
            MediaItemTree.getItem(mediaId)
                ?: return Futures.immediateFuture(
                    LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                )
        return Futures.immediateFuture(LibraryResult.ofItem(item, null))
    }

    @SuppressLint("UnsafeOptInUsageError")
    override fun onGetChildren(
        session: MediaLibraryService.MediaLibrarySession,
        browser: MediaSession.ControllerInfo,
        parentId: String,
        page: Int,
        pageSize: Int,
        params: MediaLibraryService.LibraryParams?
    ): ListenableFuture<LibraryResult<ImmutableList<MediaItem>>> {
        val children =
            MediaItemTree.getChildren(parentId)
                ?: return Futures.immediateFuture(
                    LibraryResult.ofError(LibraryResult.RESULT_ERROR_BAD_VALUE)
                )

        return Futures.immediateFuture(LibraryResult.ofItemList(children, params))
    }


}
