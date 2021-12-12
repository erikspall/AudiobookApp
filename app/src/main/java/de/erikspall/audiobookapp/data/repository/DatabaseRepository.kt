package de.erikspall.audiobookapp.data.repository

import androidx.annotation.WorkerThread
import de.erikspall.audiobookapp.data.dao.*
import de.erikspall.audiobookapp.data.model.*
import kotlinx.coroutines.flow.Flow

class DatabaseRepository(
    private val audiobookDao: AudiobookDao,
    private val belongsToDao: BelongsToDao,
    private val chapterDao: ChapterDao,
    private val genreDao: GenreDao,
    private val personDao: PersonDao
) {
    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    val allAudiobooks: Flow<List<Audiobook>> = audiobookDao.getAudiobooks()
    val allAudiobooksWithAuthor: Flow<List<AudiobookWithAuthor>> = audiobookDao.getAudiobooksWithAuthor()
    val allGenres: Flow<List<Genre>> = genreDao.getItems()

    // By default Room runs suspend queries off the main thread, therefore, we don't need to
    // implement anything else to ensure we're not doing long running database work
    // off the main thread.
    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAudiobook(audiobook:Audiobook) {
        audiobookDao.insert(audiobook)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertAudiobooks(audiobooks:List<Audiobook>) {
        audiobookDao.insert(audiobooks)
    }


    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun personExist(firstName: String, lastName: String): Boolean {
        return personDao.personExists(firstName, lastName)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    fun personExistsSync(firstName: String, lastName: String): Boolean {
        return personDao.personExistsSync(firstName, lastName)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertPerson(person: Person) {
        personDao.insert(person)
    }

    @Suppress("RedundantSuspendModifier")
    @WorkerThread
    suspend fun insertChapter(chapter: Chapter) {
        chapterDao.insert(chapter)
    }

}