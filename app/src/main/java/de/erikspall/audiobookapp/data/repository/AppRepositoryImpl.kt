package de.erikspall.audiobookapp.data.repository

import android.util.Log
import de.erikspall.audiobookapp.data.data_source.local.disk.AudiobookFileDataSource
import de.erikspall.audiobookapp.data.data_source.local.disk.AudiobookMetadata
import de.erikspall.audiobookapp.data.data_source.local.disk.ChapterMetadata
import de.erikspall.audiobookapp.domain.model.*
import de.erikspall.audiobookapp.domain.repository.*
import kotlinx.coroutines.flow.Flow

class AppRepositoryImpl(
    /* DatabaseDataSource */
    private val audiobookRepo: AudiobookRepository,
    private val belongsToRepo: BelongsToRepository,
    private val chapterRepo: ChapterRepository,
    private val genreRepo: GenreRepository,
    private val personRepo: PersonRepository,
    /* AudiobookFileDataSource */
    private val audiobookFileDataSource: AudiobookFileDataSource
) : AppRepository{

    // Room executes all queries on a separate thread.
    // Observed Flow will notify the observer when the data has changed.
    //val allAudiobooks: Flow<List<Audiobook>> = audiobookRepo.getAudiobooks()
    val allAudiobooksWithPersons: Flow<List<AudiobookWithPersons>> =
        audiobookRepo.getAudiobooksWithPersons()
    val allAudiobooksWithInfo: Flow<List<AudiobookWithInfo>> = audiobookRepo.getAudiobooksWithInfo()
    //val allGenres: Flow<List<Genre>> = genreDao.getItems()

    /*
        Add needed Data access functions here
     */

    /**
     * Imports all audiobooks from local storage into database
     */
    override suspend fun importAudiobooksFromLocalStorage() {
        val rawAudiobooks = audiobookFileDataSource.getAll()
        for (book in rawAudiobooks) {
            val genreId = getOrInsertGenre(book.genre)
            val audiobookId = getOrInsertAudiobook(book)
            addChapters(audiobookId, book.chapters)
            belongsToRepo.insert(BelongsTo(
                genreId = genreId,
                audiobookId = audiobookId
            ))

        }
    }

    override fun getAudiobooksWithPersons(): Flow<List<AudiobookWithPersons>> {
        return allAudiobooksWithPersons
    }

    override fun getAudiobooksWithInfo(): Flow<List<AudiobookWithInfo>> {
        return allAudiobooksWithInfo
    }

    override suspend fun setPosition(audiobookId: Long, position: Long, isPlaying: Boolean) {
        audiobookRepo.setPosition(audiobookId, position, isPlaying)
    }

    override suspend fun setChapterIsPlaying(
        audiobookId: Long,
        chapterId: Long,
        isPlaying: Boolean
    ) {
        chapterRepo.setIsPlaying(audiobookId, chapterId, isPlaying)
    }


    private suspend fun getOrInsertPerson(person: String): Long {
        val firstName = person.substringBeforeLast(" ")
        val lastName = person.substringAfterLast(" ")
        //if (firstName != person) { //Checks if Person has first and last name
        Log.d("Importing","Checking Person: $person")
        return if (personRepo.personExists(firstName, lastName)){
            Log.d("Importing","$person does exist, retrieving id ...")
            personRepo.getPerson(firstName, lastName)!!.personId
        }
        else {
            Log.d("Importing", "$person does not exist, adding to database...")
            personRepo.insert(
                Person(
                    firstName = firstName,
                    lastName = lastName
                )
            )
        }
    }

    private suspend fun getOrInsertGenre(genre: String): Long {
        return if (genreRepo.genreExists(genre))
            genreRepo.getGenre(genre)!!.genreId
        else
            genreRepo.insert(Genre(name = genre))
    }

    private suspend fun getOrInsertAudiobook(audiobook: AudiobookMetadata): Long {
        return if (audiobookRepo.audiobookExists(audiobook.uri.toString())) {
            audiobookRepo.getAudiobookByUri(audiobook.uri.toString())!!.audiobookId
        } else {
            audiobookRepo.insert(Audiobook(
                uri = audiobook.uri.toString(),
                coverUri = audiobook.coverUri.toString(),
                title = audiobook.title,
                duration = audiobook.duration,
                authorId = getOrInsertPerson(audiobook.author ?: "Unknown author"),
                narratorId = getOrInsertPerson(audiobook.narrator ?: "Unknown narrator"),
                position = audiobook.position
            ))
        }
    }

    private suspend fun addChapters(bookId: Long, chapters: List<ChapterMetadata>) {
        for (chapter in chapters)
            chapterRepo.insert(
                Chapter(
                    audiobookId = bookId,
                    time_base = chapter.time_base,
                    start = chapter.start,
                    start_time = chapter.start_time,
                    end = chapter.end,
                    end_time = chapter.end_time,
                    title = chapter.title
            ))
    }
}