package de.erikspall.audiobookapp.ui.library.event

import de.erikspall.audiobookapp.domain.util.audiobook.order.AudiobookOrder

/** Events that originate from user actions **/
sealed class LibraryEvent {
    data class Order(val audiobookOrder: AudiobookOrder): LibraryEvent()
    object SwitchLayout: LibraryEvent()
    object Import: LibraryEvent()
    /* search, delete, undo, ... */
}