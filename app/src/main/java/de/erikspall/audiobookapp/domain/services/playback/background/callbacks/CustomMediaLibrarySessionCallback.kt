package de.erikspall.audiobookapp.domain.services.playback.background.callbacks

import android.annotation.SuppressLint
import android.net.Uri
import android.os.Bundle
import android.util.Log
import androidx.media3.common.MediaItem
import androidx.media3.common.Player
import androidx.media3.session.LibraryResult
import androidx.media3.session.MediaLibraryService
import androidx.media3.session.MediaLibraryService.MediaLibrarySession
import androidx.media3.session.MediaSession
import androidx.media3.session.SessionResult
import com.google.common.collect.ImmutableList
import com.google.common.util.concurrent.Futures
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.ListeningExecutorService
import com.google.common.util.concurrent.MoreExecutors
import de.erikspall.audiobookapp.data.source.local.player_controller.MediaItemTree
import de.erikspall.audiobookapp.domain.services.playback.background.PlayerService.Companion.SEARCH_QUERY_PREFIX
import de.erikspall.audiobookapp.domain.services.playback.background.PlayerService.Companion.SEARCH_QUERY_PREFIX_COMPAT
import de.erikspall.audiobookapp.domain.use_case.audiobook.AudiobookUseCases
import de.erikspall.audiobookapp.domain.use_case.playback.PlaybackUseCases
import kotlinx.coroutines.MainScope
import kotlinx.coroutines.launch
import java.util.concurrent.Callable
import java.util.concurrent.Executors

class CustomMediaLibrarySessionCallback(
    private val setMediaItemFromSearchQuery: (String) -> Unit,
    private val audiobookUseCases: AudiobookUseCases,
    private val playbackUseCases: PlaybackUseCases
) : MediaLibrarySession.Callback {


    @SuppressLint("UnsafeOptInUsageError")
    fun onSetMediaUri(
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

    override fun onAddMediaItems(
        mediaSession: MediaSession,
        controller: MediaSession.ControllerInfo,
        mediaItems: MutableList<MediaItem>
    ): ListenableFuture<MutableList<MediaItem>> {
        val service = MoreExecutors.listeningDecorator(Executors.newSingleThreadExecutor())
        val futureMediaItems: ListenableFuture<MutableList<MediaItem>> = service.submit(
            Callable<MutableList<MediaItem>> {
                val tempMediaItems = mutableListOf<MediaItem>()
                for (mediaItem in mediaItems) {
                    if (mediaItems.contains(mediaItem))
                        tempMediaItems.add(
                            MediaItemTree.getItem(mediaItem.mediaId) ?: mediaItem
                        )

                }
                tempMediaItems
            }
        )
        return futureMediaItems
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

    @SuppressLint("UnsafeOptInUsageError")
    override fun onPlayerCommandRequest(
        session: MediaSession,
        controller: MediaSession.ControllerInfo,
        playerCommand: Int
    ): Int {

        when (playerCommand) {
            Player.COMMAND_PLAY_PAUSE -> {
                if (session.player.isPlaying) {
                    Log.d("PlayerCommand", "Pause requested")
                    // Pause is requested -> Save progress
                    val bookId = playbackUseCases.getCurrent.bookId(session.player) //TODO: Does not work, when controller dead?
                    val chapterId = playbackUseCases.getCurrent.chapterId(session.player)
                    val pos = playbackUseCases.getCurrent.positionInBook(session.player)
                    MainScope().launch {
                        audiobookUseCases.set.position(
                            bookId,
                            chapterId,
                            pos,
                            false
                        )
                    }
                } else {
                    Log.d("PlayerCommand", "Play requested")
                    MainScope().launch {
                        audiobookUseCases.set.bothArePlaying(
                            playbackUseCases.getCurrent.bookId(session.player),
                            playbackUseCases.getCurrent.chapterId(session.player),
                            true
                        )
                    }
                }
            }
            Player.COMMAND_STOP -> {
                Log.d("PlayerCommand", "Stop requested")
                val bookId = playbackUseCases.getCurrent.bookId(session.player)
                val chapterId = playbackUseCases.getCurrent.chapterId(session.player)
                val pos = playbackUseCases.getCurrent.positionInBook(session.player)
                MainScope().launch {
                    audiobookUseCases.set.position(
                        bookId,
                        chapterId,
                        pos,
                        false
                    )
                }
            }
            Player.COMMAND_SEEK_TO_NEXT_MEDIA_ITEM -> {
                Log.d("PlayerCommand", "Next chapter")
            }
            Player.COMMAND_SEEK_TO_PREVIOUS_MEDIA_ITEM -> {
                Log.d("PlayerCommand", "Previous chapter")
            }
            Player.COMMAND_SEEK_TO_PREVIOUS -> {
                Log.d("PlayerCommand", "Seek to beginning or previous chapter")

            }
            else -> {
                Log.d("PlayerCommand", "Unknown: $playerCommand")
            }
        }
        return super.onPlayerCommandRequest(session, controller, playerCommand)
    }
}
