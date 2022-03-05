package de.erikspall.audiobookapp.ui.global.viewmodels.state

import android.annotation.SuppressLint
import androidx.lifecycle.MutableLiveData
import androidx.media3.common.MediaMetadata
import de.erikspall.audiobookapp.domain.const.Player

@SuppressLint("UnsafeOptInUsageError")
data class PlayerState (
    var isPlaying: Boolean = false,
   // val isPrepared: MutableLiveData<Boolean> = MutableLiveData(false),
    @SuppressLint("UnsafeOptInUsageError")
    var mediaMetadata: MutableLiveData<MediaMetadata> = MutableLiveData(MediaMetadata.EMPTY),
    val controllerCreated: MutableLiveData<Boolean> = MutableLiveData(false),
    val sliderProgress: MutableLiveData<Int> = MutableLiveData(0),
    var currentlyPlayingBookId: Long = -1,
    val playbackState: MutableLiveData<Int> = MutableLiveData(Player.STATE_NONE)
) {
    fun reset() {
        isPlaying = false
       // isPrepared.postValue(false)
        mediaMetadata.postValue( MediaMetadata.EMPTY)
        controllerCreated.postValue(false)
        sliderProgress.postValue(0)
        currentlyPlayingBookId = -1
    }
}