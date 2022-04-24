package de.erikspall.audiobookapp.data.source.local.disk

import android.content.Context
import de.erikspall.audiobookapp.data.source.local.disk.import.M4bImporter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext

class AudiobookFileDataSource(
    private val context: Context,
    private val ioDispatcher: CoroutineDispatcher
) {

    /**
     * Responsible for getting all audiobook files on the device storage.
     */
    suspend fun getAll(): List<AudiobookMetadata> {
        return withContext(ioDispatcher) {
            M4bImporter(context).getAll()
        }
    }
}