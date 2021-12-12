package de.erikspall.audiobookapp.data.handling.import

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import androidx.core.net.toUri
import de.erikspall.audiobookapp.data.database.AudiobookRoomDatabase
import de.erikspall.audiobookapp.data.model.Audiobook
import de.erikspall.audiobookapp.data.model.BelongsTo
import de.erikspall.audiobookapp.data.model.Genre
import de.erikspall.audiobookapp.data.model.Person
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.withContext
import java.io.File

class LocalImporter(val context: Context) : Importer<Audiobook> {

    val database: AudiobookRoomDatabase by lazy { AudiobookRoomDatabase.getDatabase(context,  CoroutineScope(
        SupervisorJob()
    )
    )}



    override fun getAll(): List<Audiobook> {
        return searchForAudiobooks()
    }

    suspend fun getAllAsync(): List<Audiobook>{
        return withContext(Dispatchers.IO){
            searchForAudiobooks()
        }
    }

    private fun searchForAudiobooks(): List<Audiobook> {
        val audioList = mutableListOf<Audiobook>()

        val collection =
                MediaStore.Audio.Media.EXTERNAL_CONTENT_URI


        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.BOOKMARK,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.COMPOSER,
            MediaStore.Audio.Media.GENRE,
            MediaStore.Audio.Media.ALBUM_ARTIST
        )

        // Show only videos that are at least 5 minutes in duration.
        val selection = "${MediaStore.Audio.Media.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf(
            "%.m4b"
        )

        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        val query = context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )

        query?.use { cursor ->
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Video.Media._ID)
            val titleColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val bookmarkColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BOOKMARK)
            val composerColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER)
            val genreColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
            val albumArtistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ARTIST)

            while (cursor.moveToNext()) {
                // Get info of audio
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val duration = cursor.getLong(durationColumn)
                val artist = cursor.getString(artistColumn)
                val bookmark = cursor.getLong(bookmarkColumn)
                val composer = cursor.getString(composerColumn)
                val genre = cursor.getString(genreColumn)
                val albumArtist = cursor.getString(albumArtistColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )


                Log.d("Imported", "\n " +
                        "URI:" + contentUri.toString() + "\n" +
                        "TITLE: " + title + "\n" +
                        "DURATION: " + duration + "\n" +
                        "NARRATOR: " + artist + "\n" +
                        "BOOKMARK: " + bookmark + "\n" +
                        "AUTHOR: " + composer
                )

                var authorId: Long?
                var narratorId: Long?
                if (composer == null) {
                    if (albumArtistColumn == null){
                        // Fallback to album artist
                        authorId = getOrAddPerson("No author")
                    } else {
                        authorId = getOrAddPerson(albumArtist)
                    }

                } else {
                    authorId = getOrAddPerson(composer)
                }
                if (artist == null) {
                    narratorId = getOrAddPerson("No narrator")
                } else {
                    narratorId = getOrAddPerson(artist)
                }
                // Check if author/narrator is already in database, if not add them
                // you can call stuff in sync here, because the importer itself is in seperate thread

                val coverUri = extractAndGetOrSaveCover(contentUri, ContentUris.parseId(contentUri))
                //TODO: Dont use placeholders and reconsider using long
                audioList += Audiobook(
                    uri = contentUri.toString(),
                    coverUri = coverUri.toString(),
                    title = title,
                    duration = duration,
                    authorId = authorId,
                    readerId = narratorId,
                    position = bookmark
                )

                val genreId: Long = getOrAddGenre(genre)
                val audiobookId: Long = getOrAddAudiobook(audioList[audioList.size-1])

                database.belongsToDao().insertSync(BelongsTo(audiobookId, genreId))

                ChapterImporter(context, contentUri, audiobookId).getAll()

            }
        }
        return audioList
    }

    private fun getOrAddPerson(raw: String): Long{
        val firstName = raw.substringBeforeLast(" ")
        val lastName = raw.substringAfterLast(" ")

        if (database.personDao().personExistsSync(firstName, lastName)){
            return database.personDao().getPersonSync(firstName, lastName).personId
        } else {
            return database.personDao().insertSync(Person(firstName = firstName, lastName = lastName))
        }
    }

    private fun getOrAddGenre(genre: String): Long{
        if (database.genreDao().genreExistsSync(genre)){
            return database.genreDao().getGenreSync(genre).genreId
        } else {
            return database.genreDao().insertSync(Genre(name = genre))
        }
    }

    private fun getOrAddAudiobook(audiobook: Audiobook): Long{
        if (database.audiobookDao().audiobookExistsSync(audiobook.title, audiobook.uri)){
            return database.audiobookDao().getAudiobookSync(audiobook.title, audiobook.uri).audiobookId
        } else {
            return database.audiobookDao().insertSync(audiobook)
        }
    }

    private fun extractAndGetOrSaveCover(audiobookUri: Uri, id: Long): Uri {
        val file = File(context.filesDir, id.toString())
        if (file.exists()) {
            return file.toUri()
        }
        val mmr = MediaMetadataRetriever()
        mmr.setDataSource(context, audiobookUri)
        /*context.openFileOutput(id.toString(), Context.MODE_PRIVATE).use {
            it.write(mmr.embeddedPicture)
        }*/
        val cover = mmr.embeddedPicture
        if (cover != null) {
            file.writeBytes(mmr.embeddedPicture!!)
            return file.toUri()
        }
        return "".toUri()
    }
}