package de.erikspall.audiobookapp.domain.use_case.playback

data class PlaybackUseCases (
    val togglePlayback: TogglePlayback,
    val playBook: PlayBook,
    val initizialize: Initizialize,
    val releaseController: ReleaseController,
    val addListener: AddListener,
    val getCurrent: GetCurrent,
    val state: State,
    val seekTo: SeekTo,
    val skip: Skip
)