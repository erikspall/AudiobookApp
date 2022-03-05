package de.erikspall.audiobookapp.ui.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.erikspall.audiobookapp.domain.const.Layout
import de.erikspall.audiobookapp.domain.model.AudiobookWithPersons
import de.erikspall.audiobookapp.domain.use_case.audiobook.AudiobookUseCases
import de.erikspall.audiobookapp.domain.util.audiobook.order.AudiobookOrder
import de.erikspall.audiobookapp.domain.util.audiobook.order.OrderType
import de.erikspall.audiobookapp.ui.library.event.LibraryEvent
import de.erikspall.audiobookapp.ui.library.viewmodel.state.LibraryState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val audiobookUseCases: AudiobookUseCases
) : ViewModel(){

    val state = LibraryState()
    val books = getBooks(AudiobookOrder.Title(OrderType.Ascending)).asLiveData()

    /* fired by ui */
    fun onEvent(event: LibraryEvent) {
        when (event) {
            is LibraryEvent.Order -> {

            }
             is LibraryEvent.SwitchLayout ->  {
                 state.layout.postValue(when(state.layout.value){
                     Layout.GRID -> Layout.LIST
                     Layout.LIST -> Layout.GRID
                     else -> Layout.GRID
                 })
             }
            is LibraryEvent.Import -> {
                viewModelScope.launch {
                    audiobookUseCases.importBooksLocal()
                }
            }
        }
    }

    private fun getBooks(bookOrder: AudiobookOrder): Flow<List<AudiobookWithPersons>> {
        return audiobookUseCases.getBooksWithPersons.invoke(bookOrder)
    }
}