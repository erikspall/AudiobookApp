package de.erikspall.audiobookapp.ui.global.viewmodels

import android.annotation.SuppressLint
import android.app.Application
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.media3.common.MediaMetadata
import dagger.hilt.android.lifecycle.HiltViewModel
import de.erikspall.audiobookapp.domain.const.Player
import de.erikspall.audiobookapp.domain.services.playback.background.listeners.PlayerListener
import de.erikspall.audiobookapp.domain.use_case.audiobook.AudiobookUseCases
import de.erikspall.audiobookapp.domain.use_case.playback.PlaybackUseCases
import de.erikspall.audiobookapp.ui.global.events.PlayerEvent
import de.erikspall.audiobookapp.ui.global.viewmodels.state.PlayerState
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class PlayerViewModel @Inject constructor(
    app: Application,
    private val bookUseCases: AudiobookUseCases,
    private val playbackUseCases: PlaybackUseCases
) : AndroidViewModel(app) {
    /* State of player */
    val state = PlayerState()
    private var isInBackground = true


    private val handler = Handler(Looper.getMainLooper())

    init {
        initializeController()
    }

    override fun onCleared() {
        playbackUseCases.releaseController()
        state.reset()
        super.onCleared()
    }

    @SuppressLint("UnsafeOptInUsageError")
    fun initializeController() {
        playbackUseCases.initizialize.controller {

            // state.controllerCreated.postValue(true)
            state.isPlaying = playbackUseCases.state.isPlaying()
            state.mediaMetadata.value = (playbackUseCases.getCurrent.mediaMetaData())
            //state.isPrepared.postValue(playbackUseCases.state.isPrepared())
            state.sliderProgress.postValue(progressBig())

            if (playbackUseCases.state.isPlaying()) {
                state.currentlyPlayingBookId = (playbackUseCases.getCurrent.bookId())
                recoverState(Player.STATE_PLAYING)
            } else if (playbackUseCases.state.isPrepared()) {
                recoverState(Player.STATE_PAUSED)
            }
            // Check if controller connected to a playing media session


            playbackUseCases.addListener(PlayerListener(
                { playing ->
                    Log.d("LiveData", "Playback toggled! Posting ...")
                    state.isPlaying = playing
                    //state.isPrepared.postValue(playbackUseCases.state.isPrepared())
                    if (!playing) {
                        state.currentlyPlayingBookId = (-1)
                        stopAllUpdates()
                        state.playbackState.value = (Player.STATE_PAUSED)
                    } else {
                        state.currentlyPlayingBookId = (playbackUseCases.getCurrent.bookId())
                        if (!isInBackground) {
                            Log.d("LiveData", "Started updating progress")
                            keepBookProgressUpdated()
                            keepChapterProgressUpdated()
                        }
                        state.playbackState.postValue(Player.STATE_PLAYING)
                    }
                },
                { mediaMetadata ->
                    Log.d("LiveData", "Metadata changed! Posting ...")
                    state.mediaMetadata.value = (mediaMetadata)
                    // Only triggerUpdate if
                    if (state.playbackState.value == Player.STATE_PLAYING)
                        triggerUpdate()
                    //triggerMetaUpate(state.playbackState.value!!)
                    //state.currentlyPlayingBookId.postValue(-1)
                },
                { playbackState ->
                    //Log.d("PlaybackStateLib", "Changed by event to: $playbackState")
                    state.playbackState.value = (playbackState)
                }
            ), true
            )
        }
    }

    /* fired by ui */
    @SuppressLint("UnsafeOptInUsageError")
    fun onEvent(event: PlayerEvent) {
        when (event) {
            is PlayerEvent.TogglePlayPause -> {
                if (playbackUseCases.state.isPlaying()) {
                    val currentMediaMetadata = playbackUseCases.getCurrent.mediaMetaData()
                    //if (currentMediaMetadata != MediaMetadata.EMPTY)
                    //savePosition(currentMediaMetadata.mediaUri.toString(), playbackUseCases.getCurrent.positionInBook())
                }
                playbackUseCases.togglePlayback()
            }
            is PlayerEvent.StartPlayback -> {
                // Save position of current Book before switching to new book
                val currentMediaMetadata = playbackUseCases.getCurrent.mediaMetaData()
                if (currentMediaMetadata != MediaMetadata.EMPTY)
                    savePosition(
                        currentMediaMetadata.mediaUri.toString(),
                        playbackUseCases.getCurrent.positionInBook()
                    )

                playbackUseCases.playBook(event.audiobook)
            }
            is PlayerEvent.LibraryWentToForeground -> {
                isInBackground = false
                Log.d("LiveData", "Library in Foreground")
                // Resume updating progress if needed
                if (state.isPlaying) {
                    recoverState(Player.STATE_PLAYING)
                    Log.d("LiveData", "Resumed updating progress")
                    keepBookProgressUpdated()
                    keepChapterProgressUpdated()
                } else if (playbackUseCases.state.isPrepared()) {
                    recoverState(Player.STATE_PAUSED)
                }
            }
            is PlayerEvent.LibraryWentToBackground -> {
                isInBackground = true
                Log.d("LiveData", "Library in Background")
                stopAllUpdates()
            }
        }
    }

    private fun savePosition(uri: String, position: Long) {
        if (uri.isNotEmpty())
            viewModelScope.launch { // TODO: Should probably save in service
                bookUseCases.savePosition(
                    uri,
                    position
                )
            }
    }

    fun progressBig(): Int {
        return ((playbackUseCases.getCurrent.positionInChapter()
            .toDouble() / playbackUseCases.getCurrent.chapterDuration()) * 1000).toInt()
    }

    // Progress of mini-player
    private fun keepChapterProgressUpdated() {
        handler.postDelayed({
            state.sliderProgress.postValue(progressBig())
            keepChapterProgressUpdated()
        }, 300)
    }

    @SuppressLint("UnsafeOptInUsageError")
    private fun keepBookProgressUpdated() {
        handler.postDelayed({
            savePosition(
                playbackUseCases.getCurrent.mediaMetaData().mediaUri.toString(),
                playbackUseCases.getCurrent.positionInBook()
            )
            keepBookProgressUpdated()
        }, 60000) //Every minute
    }

    private fun stopAllUpdates() {
        Log.d("LiveData", "Stopped updating progress")
        handler.removeCallbacksAndMessages(null)
        //state.sliderProgress.postValue(progressBig())
    }

    private fun recoverState(playerState: Int) {
        Log.d("PlaybackStateLib", "Recovering state: $playerState")
        //state.playbackState.value = Player.STATE_BUFFERING
        state.playbackState.value = Player.STATE_READY
        state.playbackState.value = playerState
    }

    private fun triggerUpdate() {
        Log.d("PlaybackStateLib", "Trigger UI Update")
        state.playbackState.value = Player.STATE_READY //Triggers UI updates

        if (playbackUseCases.state.isPlaying()) {
            state.playbackState.value = (Player.STATE_PLAYING)
        } else if (playbackUseCases.state.isPrepared()) {
            state.playbackState.value = (Player.STATE_PAUSED)
        }
    }
}