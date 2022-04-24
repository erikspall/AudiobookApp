package de.erikspall.audiobookapp.ui.now_playing.event

sealed class NowPlayingEvent {
    object WentToBackground: NowPlayingEvent()
    object WentToForeground: NowPlayingEvent()
    data class SliderDragged(val isBeingDragged: Boolean): NowPlayingEvent()
    object OnPlay: NowPlayingEvent()
    object OnPause: NowPlayingEvent()
    object TogglePlayPause: NowPlayingEvent()
    data class SeekTo(val position: Long): NowPlayingEvent()
    object SkipForward: NowPlayingEvent()
    object SkipBackward: NowPlayingEvent()
    object SeekForward: NowPlayingEvent()
    object SeekBackward: NowPlayingEvent()
    object StartedInPause: NowPlayingEvent()
    object SleepTimerSet: NowPlayingEvent()
    object SleepTimerCanceled: NowPlayingEvent()
}
