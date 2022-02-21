package de.erikspall.audiobookapp.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaItem
import androidx.media3.common.MediaMetadata
import androidx.media3.common.Player
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import com.google.common.util.concurrent.MoreExecutors
import de.erikspall.audiobookapp.data.database.AudiobookRoomDatabase
import de.erikspall.audiobookapp.data.model.Audiobook
import de.erikspall.audiobookapp.data.model.AudiobookWithAuthor
import de.erikspall.audiobookapp.uamp.MediaItemTree
import de.erikspall.audiobookapp.uamp.PlaybackService
import de.erikspall.audiobookapp.uamp.PlayerListener
import de.erikspall.audiobookapp.utils.Conversion
import kotlinx.coroutines.launch
import java.util.*
import kotlin.math.ceil
import kotlin.math.roundToInt

class PlayerViewModel(app: Application) : AndroidViewModel(app) {
    val isPlaying: Boolean
        get() = controller?.isPlaying ?: false
    val isPaused: Boolean
        get() = controller?.playbackState == Player.STATE_READY && !isPlaying
    val isPreparing: Boolean
        get() = controller?.playbackState == Player.STATE_BUFFERING

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

    val database: AudiobookRoomDatabase by lazy { AudiobookRoomDatabase.getDatabase(getContext()
    )}

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

        Log.d("ViewModelLife", "PlayerViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        releaseBrowserAndController()
        Log.d("ViewModelLife", "PlayerViewModel destroyed!")
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
            controller?.setMediaItems(chapters, true)
            controller?.prepare()
            controller?.play()
            moveToLastPosition(audiobookWithAuthor.audiobook)
            //controller?.playWhenReady = true
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun moveToLastPosition(audiobook: Audiobook){
        if (controller == null || controller?.mediaItemCount == 0){
            throw IllegalStateException()
        }
        var chapterIndex = 0
        while (controller!!.getMediaItemAt(chapterIndex).clippingConfiguration.endPositionMs < audiobook.position) {
            chapterIndex++
            if (chapterIndex > controller!!.mediaItemCount-1){
                chapterIndex--
                break
            }
        }

        controller!!.seekTo(chapterIndex, audiobook.position - controller!!.getMediaItemAt(chapterIndex).clippingConfiguration.startPositionMs)
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
        if (controller != null) {
            controller!!.seekTo(position)
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun resume() {
        if (controller != null && getCurrentMediaItem() != MediaItem.EMPTY)
            controller!!.play()
    }

    fun addListener(listener: PlayerListener) {
        if (controller != null) {
            controller!!.addListener(listener)
        }
    }

    fun removeListener(listener: PlayerListener) {
        if (controller != null) {
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
        return getCurrentMediaItem().clippingConfiguration.endPositionMs - getCurrentMediaItem().clippingConfiguration.startPositionMs
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

    fun getBookProgressInPercent(): Double {
        return getCurrentPositionInBook().toDouble()/getBookDuration()
    }

    fun getTimeLeftInChapter(): Long {
        return getChapterDuration() - getCurrentPositionInChapter()
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun getCurrentMediaItem(): MediaItem {
        return controller?.currentMediaItem ?: MediaItem.EMPTY
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun getCurrentMediaMetadata(): MediaMetadata {
        return getCurrentMediaItem().mediaMetadata ?: MediaMetadata.EMPTY
    }

    fun skipChapter() {
        if (controller?.currentMediaItemIndex ?: 0 < controller?.mediaItemCount ?: 0)
            controller?.seekToNextMediaItem() ?: Log.d(
                "PlayerViewModel",
                "Cannot skip chapter, controller not created!"
            )
    }

    fun goBackChapter() {
        if (getCurrentPositionInChapter() <= 5000) {
            if (controller?.currentMediaItemIndex ?: 0 > 0) {
                controller?.seekToPreviousMediaItem() ?: Log.d(
                    "PlayerViewModel",
                    "Cannot go back chapter, controller not created!"
                )
            } else
                controller?.seekToDefaultPosition()
        } else
            controller?.seekToDefaultPosition()
    }

    fun forward(ms: Long) {
        if (getTimeLeftInChapter() - ms > 0) {
            controller?.seekTo(getCurrentPositionInChapter() + ms) ?: Log.d(
                "PlayerViewModel",
                "Controller not created!"
            )
        } else {
            skipChapter()
        }
    }

    /**
     * Amount of milliseconds to next percent
     */
    fun milliPerPercentTotal(): Long {
        val percent = getBookProgressInPercent()*100.0
        val offsetToNext = if (percent.roundToInt() > percent) 1 else 0
        val msPerPercent = getBookDuration()/100

        // Add 1s additional just to be safe
        Log.d("ProgressTracker", "Percent: $percent, msPerPercent: $msPerPercent + PercentToNext: ${(ceil(percent) - percent) + offsetToNext}")
        var left = ((ceil(percent) - percent + offsetToNext) * msPerPercent).toLong()
        if (left < 1000)
            left = 1000

        val rightNow = Calendar.getInstance()
        val offset = (rightNow[Calendar.ZONE_OFFSET] +
                rightNow[Calendar.DST_OFFSET]).toLong()

        val sinceMidnight = (rightNow.timeInMillis + offset) %
                (24 * 60 * 60 * 1000) + left

        Log.d("ProgressTracker", "Started updating Book Progress with delay: ${Conversion.millisToStr(left)} \n" +
                "expect update on: ${Conversion.millisToStr(sinceMidnight)}")
        return left
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun saveProgressToDatabase() {
        viewModelScope.launch {
            database.audiobookDao().setPosition(
                getCurrentMediaItem().mediaMetadata.mediaUri.toString(),
                getCurrentPositionInBook())
        }
    }
}