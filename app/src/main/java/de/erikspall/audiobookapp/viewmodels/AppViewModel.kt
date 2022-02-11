package de.erikspall.audiobookapp.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.content.ComponentName
import android.content.Context
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.media3.common.MediaItem
import androidx.media3.session.MediaBrowser
import androidx.media3.session.MediaController
import androidx.media3.session.SessionToken
import com.google.common.util.concurrent.ListenableFuture
import de.erikspall.audiobookapp.const.Layout
import de.erikspall.audiobookapp.data.model.AudiobookWithAuthor
import de.erikspall.audiobookapp.uamp.MediaItemTree
import de.erikspall.audiobookapp.uamp.PlaybackService

class AppViewModel(app: Application): AndroidViewModel(app) {
    var layout = Layout.GRID




    /** Stuff for playback **/
    private lateinit var controllerFuture: ListenableFuture<MediaController>
    val controller: MediaController?
        get() = if (controllerFuture.isDone) controllerFuture.get() else null

    private lateinit var browserFuture: ListenableFuture<MediaBrowser>
    val browser: MediaBrowser?
        get() = if (browserFuture.isDone) browserFuture.get() else null

    @SuppressLint("UnsafeOptInUsageError")
    private val _sessionToken: SessionToken = SessionToken(getContext(), ComponentName(getContext(), PlaybackService::class.java))
    val sessionToken: SessionToken = _sessionToken
    /** ~~~~~~~~~~~~~~~~~~ **/

    init {
        controllerFuture = MediaController.Builder(
            getContext(),
            _sessionToken
        ).buildAsync()
        browserFuture = MediaBrowser.Builder(
            getContext(),
            _sessionToken
        ).buildAsync()
        Log.d("FragmentStuff", "AppViewModel created!")
    }

    override fun onCleared() {
        super.onCleared()
        releaseBrowserAndController()
        Log.d("FragmentStuff", "AppViewModel destroyed!")
    }

    private fun getContext(): Context{
        return getApplication<Application>().applicationContext
    }


    /** Stuff for playback **/
    @SuppressLint("UnsafeOptInUsageError")
    fun playMedia(audiobookWithAuthor: AudiobookWithAuthor) {
       // val mediaItem = getMediaItemFromAudiobook(audiobookWithAuthor)
        val mediaItem = getRandomMediaItem()
        if (mediaItem == MediaItem.EMPTY)
            Log.e(
                "Playback",
                "MediaItem not found"
            )
        else {
            controller?.setMediaItem(mediaItem)
            controller?.prepare()
            controller?.playWhenReady = true
        }
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun getMediaItemFromAudiobook(audiobookWithAuthor: AudiobookWithAuthor): MediaItem {
        return MediaItemTree.getItemFromTitle(audiobookWithAuthor.audiobook.title)
            ?: MediaItem.EMPTY
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun getRandomMediaItem(): MediaItem {
        return MediaItemTree.getRandomItem()
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun getCurrentMediaItem(): MediaItem{
        return controller!!.currentMediaItem
            ?: MediaItem.EMPTY
    }

    private fun releaseBrowserAndController(){
        MediaBrowser.releaseFuture(browserFuture)
        MediaController.releaseFuture(controllerFuture)
    }

    fun getPositionInPercentage(): Int{
        return ((controller!!.currentPosition.toDouble()/controller!!.duration)*100).toInt()
    }
    /** ~~~~~~~~~~~~~~~~~~ **/
}