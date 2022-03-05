package de.erikspall.audiobookapp.domain.use_case.audiobook

import de.erikspall.audiobookapp.domain.model.AudiobookWithInfo
import de.erikspall.audiobookapp.domain.repository.AppRepository
import de.erikspall.audiobookapp.domain.util.audiobook.order.AudiobookOrder
import de.erikspall.audiobookapp.domain.util.audiobook.order.OrderType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class GetBooksWithInfo(
    private val repository: AppRepository
) {
    operator fun invoke(
        audiobookOrder: AudiobookOrder = AudiobookOrder.Title(OrderType.Descending)
    ): Flow<List<AudiobookWithInfo>> {
        return repository.getAudiobooksWithInfo().map { books ->
            when(audiobookOrder.orderType) {
                is OrderType.Ascending -> {
                    when (audiobookOrder) {
                        is AudiobookOrder.Title -> books.sortedBy { it.audiobook.title.lowercase() }
                        is AudiobookOrder.Progress -> books.sortedBy { it.audiobook.position }
                        is AudiobookOrder.Author -> books.sortedBy { it.author.lastName.lowercase() }
                        is AudiobookOrder.Narrator -> books.sortedBy { it.narrator.lastName.lowercase() }
                        is AudiobookOrder.Duration -> books.sortedBy { it.audiobook.duration }
                    }
                }
                is OrderType.Descending -> {
                    when (audiobookOrder) {
                        is AudiobookOrder.Title -> books.sortedByDescending { it.audiobook.title.lowercase() }
                        is AudiobookOrder.Progress -> books.sortedByDescending { it.audiobook.position }
                        is AudiobookOrder.Author -> books.sortedByDescending { it.author.lastName.lowercase() }
                        is AudiobookOrder.Narrator -> books.sortedByDescending { it.narrator.lastName.lowercase() }
                        is AudiobookOrder.Duration -> books.sortedByDescending { it.audiobook.duration }
                    }
                }
            }

        }
    }
}