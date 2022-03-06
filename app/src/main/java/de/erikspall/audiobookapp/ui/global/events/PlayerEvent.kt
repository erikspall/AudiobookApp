package de.erikspall.audiobookapp.ui.global.events

import de.erikspall.audiobookapp.domain.model.Audiobook

sealed class PlayerEvent {
    object TogglePlayPause: PlayerEvent()
    data class StartPlayback(val audiobook: Audiobook): PlayerEvent()
    object LibraryWentToBackground: PlayerEvent()
    object LibraryWentToForeground: PlayerEvent()
    object SavePosition: PlayerEvent()
}