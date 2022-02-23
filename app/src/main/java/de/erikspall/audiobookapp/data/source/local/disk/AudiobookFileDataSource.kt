package de.erikspall.audiobookapp.data.source.local.disk

import android.content.Context
import dagger.hilt.android.qualifiers.ApplicationContext
import de.erikspall.audiobookapp.data.source.local.disk.import.M4bImporter
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class AudiobookFileDataSource @Inject constructor(
    @ApplicationContext private val context : Context,
    private val ioDispatcher: CoroutineDispatcher
) {
    suspend fun getAll(): List<AudiobookMetadata> {
        return withContext(ioDispatcher) {
            M4bImporter(context).getAll()
        }
    }
}