package de.erikspall.audiobookapp.ui.library.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import de.erikspall.audiobookapp.domain.const.Layout
import de.erikspall.audiobookapp.domain.model.AudiobookWithPersons
import de.erikspall.audiobookapp.domain.use_case.AudiobookUseCases
import de.erikspall.audiobookapp.domain.util.audiobook.order.AudiobookOrder
import de.erikspall.audiobookapp.domain.util.audiobook.order.OrderType
import de.erikspall.audiobookapp.ui.library.event.LibraryEvent
import de.erikspall.audiobookapp.ui.library.viewmodel.state.LibraryState
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LibraryViewModel @Inject constructor(
    private val audiobookUseCases: AudiobookUseCases
) : ViewModel(){

    private val _state = MutableStateFlow(LibraryState())
    val state: StateFlow<LibraryState> = _state.asStateFlow()
    val books = getBooks(AudiobookOrder.Title(OrderType.Ascending)).asLiveData()

    /* fired by ui */
    fun onEvent(event: LibraryEvent) {
        when (event) {
            is LibraryEvent.Order -> {

            }
             is LibraryEvent.SwitchLayout ->  {
                 _state.value = state.value.copy(
                     layout = when(state.value.layout){
                         Layout.GRID -> Layout.LIST
                         Layout.LIST -> Layout.GRID
                         else -> Layout.GRID
                     }
                 )
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