package de.erikspall.audiobookapp.ui.now_playing.viewmodel

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
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

    fun onEvent(event: NowPlayingEvent) {
        when (event) {
            is NowPlayingEvent.WentToForeground -> {
                resumeAllUpdates()
            }
            is NowPlayingEvent.WentToBackground -> {
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
        }
    }

    private fun updateMetadata(){
        Log.d("LiveData-NowPlaying", "Updating Static Info")
        val metaData = playbackUseCases.getCurrent.mediaMetaData()
        state.static.value = StaticInfo(
            bookTitle = metaData.albumTitle.toString(),
            chapterTitle = metaData.title.toString(),
            chapterDuration = playbackUseCases.getCurrent.chapterDuration(), // Could be calc directly
            bookDuration = playbackUseCases.getCurrent.bookDuration()
        )
    }

    private fun resumeAllUpdates() {
        if (!isUpdating) {
            isUpdating = true
            resumePositionUpdates()
            resumeChapterSliderUpdates()
            resumeBookSliderUpdates()

        }
    }

    private fun resumePositionUpdates() {
        Log.d("LiveData-NowPlaying", "Resuming Position Updates")
        // Show values before delay
        state.position.value = PositionInfo(
            bookPosition = playbackUseCases.getCurrent.positionInBook(),
            chapterPosition = playbackUseCases.getCurrent.positionInChapter()
        )
        keepPositionsUpdated()
    }

    private fun resumeChapterSliderUpdates() {
        Log.d("LiveData-NowPlaying", "Resuming chapter-slider Updates")
        var progress = playbackUseCases.getCurrent.positionInChapter()
        //val chapterDuration = playbackUseCases.getCurrent.chapterDuration()
        /*if (progress < 0)
            progress = 0
        else if (progress > chapterDuration)
            progress = chapterDuration*/
        state.chapterSliderValue.value = progress
        keepChapterSliderUpdated()
    }

    private fun resumeBookSliderUpdates(){
        Log.d("LiveData-NowPlaying", "Resuming book-slider Updates")
        state.bookProgressValue.value = playbackUseCases.getCurrent.bookProgressInBigPercent(state.static.value?.bookDuration ?: 1)
        keepBookProgressUpdated()
    }

    private fun stopAllUpdates(){
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
            state.bookProgressValue.value = playbackUseCases.getCurrent.bookProgressInBigPercent(state.static.value?.bookDuration ?: 1)
            keepBookProgressUpdated()
        }, 1000)
    }
}