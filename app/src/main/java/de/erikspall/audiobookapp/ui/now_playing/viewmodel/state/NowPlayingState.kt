package de.erikspall.audiobookapp.ui.now_playing.viewmodel.state

import android.net.Uri
import androidx.lifecycle.MutableLiveData
import de.erikspall.audiobookapp.domain.util.Conversion

data class NowPlayingState (
    val static: MutableLiveData<StaticInfo> = MutableLiveData(StaticInfo()),
    val position: MutableLiveData<PositionInfo> = MutableLiveData(PositionInfo()),
    val currentCover: MutableLiveData<Uri> = MutableLiveData(Uri.EMPTY),
    val chapterSliderValue: MutableLiveData<Long> = MutableLiveData(0),
    val bookProgressValue: MutableLiveData<Int> = MutableLiveData(0),
    var sliderIsBeingDragged: Boolean = false,
    val isSleepTimerSet: MutableLiveData<Boolean> = MutableLiveData(false)
)

data class StaticInfo(
    val bookTitle: String = "-",
    val chapterTitle: String = "-",
    val chapterDuration: Long = 1,
    val bookDuration: Long = 1
) {
    fun chapterDurationAsString(): String {
        return Conversion.millisToStr(chapterDuration)
    }

    fun bookDurationAsString(): String {
        return Conversion.millisToStr(bookDuration)
    }
}

data class PositionInfo(
    val chapterPosition: Long = 0,
    val bookPosition: Long = 0
) {
    fun chapterPositionAsString(): String {
        return Conversion.millisToStr(chapterPosition)
    }

    fun bookPositionAsString(): String {
        return Conversion.millisToStr(bookPosition)
    }
}

