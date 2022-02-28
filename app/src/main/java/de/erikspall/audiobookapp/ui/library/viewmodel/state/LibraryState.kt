package de.erikspall.audiobookapp.ui.library.viewmodel.state

import de.erikspall.audiobookapp.domain.const.Layout
import de.erikspall.audiobookapp.domain.model.AudiobookWithPersons
import de.erikspall.audiobookapp.domain.util.audiobook.order.AudiobookOrder
import de.erikspall.audiobookapp.domain.util.audiobook.order.OrderType

data class LibraryState (
    val books: List<AudiobookWithPersons> = emptyList(),
    val layout: Int = Layout.GRID,
    val bookOrder: AudiobookOrder = AudiobookOrder.Title(OrderType.Ascending),
    val isMiniPlayerVisible: Boolean = false
)