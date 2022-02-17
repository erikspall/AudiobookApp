package de.erikspall.audiobookapp.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import de.erikspall.audiobookapp.data.model.AudiobookWithAuthor
import de.erikspall.audiobookapp.uamp.MediaItemTree
import de.erikspall.audiobookapp.uamp.PlaybackService
import de.erikspall.audiobookapp.uamp.PlayerListener

class PlayerViewModel(app: Application) : AndroidViewModel(app) {
    val isPlaying: Boolean
        get() = controller?.isPlaying ?: false
    val isPaused: Boolean
        get() = controller?.playbackState == Player.STATE_READY && !isPlaying

    private lateinit var controllerFuture: ListenableFuture<MediaController>
    val controller: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null
    val controllerCreated = MutableLiveData(false)

    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    val browser: MediaBrowser?
        get() = if (browserFuture.isDone) browserFuture.get() else null

    @SuppressLint("UnsafeOptInUsageError")
    private val _sessionToken: SessionToken =
        SessionToken(getContext(), ComponentName(getContext(), PlaybackService::class.java))
    val sessionToken: SessionToken = _sessionToken

    init {
        controllerFuture = MediaController.Builder(
            getContext(),
            _sessionToken
        ).buildAsync()
        browserFuture = MediaBrowser.Builder(
            getContext(),
            _sessionToken
        ).buildAsync()

        controllerFuture.addListener( //TODO: Changed
            {
                controllerCreated.postValue(true)
            },
            MoreExecutors.directExecutor()
        )

        Log.d("FragmentStuff", "PlayerViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        releaseBrowserAndController()
        Log.d("FragmentStuff", "PlayerViewModel destroyed!")
    }

    private fun getContext(): Context {
        return getApplication<Application>().applicationContext
    }

    private fun releaseBrowserAndController() {
        MediaBrowser.releaseFuture(browserFuture)
        MediaController.releaseFuture(controllerFuture)
        controllerCreated.postValue(false)
    }

    /** Useful stuff for playback **/

    @SuppressLint("UnsafeOptInUsageError")
    fun playMedia(audiobookWithAuthor: AudiobookWithAuthor) {
        // val mediaItem = getMediaItemFromAudiobook(audiobookWithAuthor)
        val chapters = MediaItemTree.getChaptersOfBook(audiobookWithAuthor.audiobook.audiobookId)
        //TODO: Find last position etc.

        if (chapters.isEmpty())
            Log.e(
                "Playback",
                "Chapters not found for ${audiobookWithAuthor.audiobook}"
            )
        else {
            // TODO: OnMediaData changed is called twice
            controller?.setMediaItems(chapters)
            controller?.prepare()
            controller?.play()
            //controller?.playWhenReady = true
        }
    }

    fun pause() {
        if (controller != null)
            controller!!.pause()
    }

    fun togglePlayback() {
        if (isPlaying) {
            pause()
        } else {
            resume()
        }
    }

    fun seekTo(position: Long) {
        if (controller!= null) {
            controller!!.seekTo(position)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun resume() {
        if (controller != null && getCurrentMediaItem() != MediaItem.EMPTY)
            controller!!.play()
    }

    fun addListener(listener: PlayerListener) {
        if (controller != null)  {
            controller!!.addListener(listener)
        }
    }

    fun removeListener(listener: PlayerListener) {
        if (controller != null)  {
            controller!!.removeListener(listener)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun getBookDuration(): Long {
        return MediaItemTree.getItemFromUri(
            getCurrentMediaItem().mediaMetadata.mediaUri?.toString() ?: ""
        ).mediaMetadata.extras?.getLong(MediaItemTree.METADATA_KEY_DURATION) ?: 1
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun getChapterDuration(): Long {
        return controller?.duration
            ?: 1
    }

    fun getCurrentPositionInChapter(): Long {
        return controller?.currentPosition ?: 0
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun getCurrentPositionInBook(): Long {
        return (getCurrentPositionInChapter() + getCurrentMediaItem().clippingConfiguration.startPositionMs)
    }

    /**
     * Ranges from 0 to 1000
     */
    fun getChapterProgress(): Int {
        return ((getCurrentPositionInChapter().toDouble() / getChapterDuration()) * 1000).toInt()
    }

    /**
     * Ranges from 0 to 1000
     */
    fun getBookProgress(): Int {
        return ((getCurrentPositionInBook().toDouble() / getBookDuration()) * 1000).toInt()
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun getCurrentMediaItem(): MediaItem {
        return controller?.currentMediaItem ?: MediaItem.EMPTY
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun getCurrentMediaMetadata(): MediaMetadata {
        return getCurrentMediaItem().mediaMetadata ?: MediaMetadata.EMPTY
    }
}