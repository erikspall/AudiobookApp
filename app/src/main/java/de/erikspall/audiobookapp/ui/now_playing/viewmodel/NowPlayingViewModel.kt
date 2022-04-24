package de.erikspall.audiobookapp.ui.now_playing.viewmodel

import android.content.SharedPreferences
import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.domain.const.Player.SEEK_INCREMENT
import de.erikspall.audiobookapp.domain.use_case.audiobook.AudiobookUseCases
import de.erikspall.audiobookapp.domain.use_case.playback.PlaybackUseCases
import de.erikspall.audiobookapp.ui.now_playing.event.NowPlayingEvent
import de.erikspall.audiobookapp.ui.now_playing.viewmodel.state.NowPlayingState
import de.erikspall.audiobookapp.ui.now_playing.viewmodel.state.PositionInfo
import de.erikspall.audiobookapp.ui.now_playing.viewmodel.state.StaticInfo
import javax.inject.Inject

@HiltViewModel
class NowPlayingViewModel @Inject constructor(
    private val bookUseCases: AudiobookUseCases,
    private val playbackUseCases: PlaybackUseCases
) : ViewModel() {
    val state = NowPlayingState()
    private val handler = Handler(Looper.getMainLooper())
    private var isUpdating = false

    init {
        //state.isSleepTimerSet.value = playbackUseCases.sleepTimer.isScheduled()
    }

    fun onEvent(event: NowPlayingEvent) {
        when (event) {
            is NowPlayingEvent.WentToForeground -> {

                if (playbackUseCases.sleepTimer.isScheduled())
                    onEvent(NowPlayingEvent.SleepTimerSet)
                // Else not needed, because default value is false

                playbackUseCases.sleepTimer.registerOnChangeListener { sharedPref, key ->
                    Log.d("SharedPreferences", "key=$key was changed!")
                    if (key == playbackUseCases.sleepTimer.getSharedPrefKey()) {
                        Log.d("SharedPreferences", "sleepTimerIsSet was changed!")
                        if (sharedPref.getBoolean(playbackUseCases.sleepTimer.getSharedPrefKey(), false)) {
                            onEvent(NowPlayingEvent.SleepTimerSet)
                        } else {
                            onEvent(NowPlayingEvent.CancelSleepTimer)
                        }
                    }
                }

                resumeAllUpdates()
            }
            is NowPlayingEvent.WentToBackground -> {
                playbackUseCases.sleepTimer.unregisterLastListener()
                stopAllUpdates()
            }
            is NowPlayingEvent.SliderDragged -> {
                state.sliderIsBeingDragged = event.isBeingDragged
                //if (!state.sliderIsBeingDragged)
                //resumeChapterSliderUpdates()
            }
            is NowPlayingEvent.OnPlay -> {
                updateMetadata()
                resumeAllUpdates()
            }
            is NowPlayingEvent.OnPause -> {
                stopAllUpdates()
            }
            is NowPlayingEvent.TogglePlayPause -> {
                playbackUseCases.togglePlayback()
            }
            is NowPlayingEvent.SeekTo -> {
                playbackUseCases.seekTo(event.position)
            }
            is NowPlayingEvent.SkipBackward -> {
                playbackUseCases.skip.backward()
            }
            is NowPlayingEvent.SkipForward -> {
                playbackUseCases.skip.forward()
            }
            is NowPlayingEvent.SeekForward -> {
                playbackUseCases.skip.forward(SEEK_INCREMENT)
            }
            is NowPlayingEvent.SeekBackward -> {
                playbackUseCases.skip.backward(SEEK_INCREMENT)
            }
            is NowPlayingEvent.StartedInPause -> {
                updateMetadata()
                resumeAllUpdates(onlyOnce = true)
            }
            is NowPlayingEvent.SleepTimerSet -> {
                state.isSleepTimerSet.value = true
            }
            is NowPlayingEvent.CancelSleepTimer -> {
                state.isSleepTimerSet.value = false
                playbackUseCases.sleepTimer.cancel()
            }
        }
    }



    private fun updateMetadata() {
        Log.d("NowPlaying", "Updating Static Info")
        val metaData = playbackUseCases.getCurrent.mediaMetaData()
        Log.d("NowPlayingViewModel", "Updating Metadata ${metaData.title}")
        state.static.value = StaticInfo(
            bookTitle = metaData.albumTitle.toString(),
            chapterTitle = metaData.title.toString(),
            chapterDuration = playbackUseCases.getCurrent.chapterDuration(), // Could be calc directly
            bookDuration = playbackUseCases.getCurrent.bookDuration()
        )
    }

    private fun resumeAllUpdates(onlyOnce: Boolean = false) {


        if (!isUpdating) {
            isUpdating = !onlyOnce
            resumePositionUpdates(onlyOnce)
            resumeChapterSliderUpdates(onlyOnce)
            resumeBookSliderUpdates(onlyOnce)

        }
    }

    private fun resumePositionUpdates(onlyOnce: Boolean = false) {

        // Show values before delay
        state.position.value = PositionInfo(
            bookPosition = playbackUseCases.getCurrent.positionInBook(),
            chapterPosition = playbackUseCases.getCurrent.positionInChapter()
        )
        if (!onlyOnce)
            keepPositionsUpdated()
    }

    private fun resumeChapterSliderUpdates(onlyOnce: Boolean = false) {
        var progress = playbackUseCases.getCurrent.positionInChapter()
        //val chapterDuration = playbackUseCases.getCurrent.chapterDuration()
        /*if (progress < 0)
            progress = 0
        else if (progress > chapterDuration)
            progress = chapterDuration*/
        state.chapterSliderValue.value = progress
        if (!onlyOnce)
            keepChapterSliderUpdated()
    }

    private fun resumeBookSliderUpdates(onlyOnce: Boolean = false) {
        state.bookProgressValue.value = playbackUseCases.getCurrent.bookProgressInBigPercent(
            state.static.value?.bookDuration ?: 1
        )
        if (!onlyOnce)
            keepBookProgressUpdated()
    }

    private fun stopAllUpdates() {
        isUpdating = false
        handler.removeCallbacksAndMessages(null)

        Log.d("LiveData-NowPlaying", "Stopping Updates")
    }

    private fun keepPositionsUpdated() {
        handler.postDelayed({
            state.position.value = PositionInfo(
                bookPosition = playbackUseCases.getCurrent.positionInBook(),
                chapterPosition = playbackUseCases.getCurrent.positionInChapter()
            )
            keepPositionsUpdated()
        }, 500)
    }

    private fun keepChapterSliderUpdated() {
        handler.postDelayed({
            if (!state.sliderIsBeingDragged) {
                state.chapterSliderValue.value = playbackUseCases.getCurrent.positionInChapter()
                keepChapterSliderUpdated()
            }
        }, 100)
    }

    private fun keepBookProgressUpdated() {
        handler.postDelayed({
            state.bookProgressValue.value = playbackUseCases.getCurrent.bookProgressInBigPercent(
                state.static.value?.bookDuration ?: 1
            )
            keepBookProgressUpdated()
        }, 1000)
    }
}