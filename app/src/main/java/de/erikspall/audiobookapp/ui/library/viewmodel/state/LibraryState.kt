package de.erikspall.audiobookapp.ui.library.viewmodel.state

import androidx.lifecycle.MutableLiveData
import de.erikspall.audiobookapp.domain.const.Layout
import de.erikspall.audiobookapp.domain.util.audiobook.order.AudiobookOrder
import de.erikspall.audiobookapp.domain.util.audiobook.order.OrderType

data class LibraryState (
    //val books: MutableLiveDataList<AudiobookWithPersons> = emptyList(),
    val layout: MutableLiveData<Int> = MutableLiveData(Layout.GRID),
    val bookOrder: MutableLiveData<AudiobookOrder> = MutableLiveData(AudiobookOrder.Title(OrderType.Ascending)),

) {
    fun reset() {

    }
}