package de.erikspall.audiobookapp.data.source.local.disk.import

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.net.Uri
import android.provider.MediaStore
import androidx.core.net.toUri
import de.erikspall.audiobookapp.data.source.local.disk.AudiobookMetadata
import de.erikspall.audiobookapp.provider.AUTHORITY

/**
 * Retrieves all needed metadata information for given m4b file.
 * Information has yet to be added to database
 */
class M4bImporter constructor(
    private val context: Context
) : Importer<AudiobookMetadata> {

    /**
     * Returns a list of all Audiobooks with their metadata
     * that are on the users disk and end with .m4b
     */
    override fun getAll(): List<AudiobookMetadata> {
        return searchForAudiobooks()
    }


    private fun searchForAudiobooks(): List<AudiobookMetadata> {
        val audiobooks = mutableListOf<AudiobookMetadata>()
        val cursor = makeM4bQuery() ?: return emptyList()
        cursor.use {
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)
            val bookmarkColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BOOKMARK)
            val composerColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.COMPOSER)
            val genreColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Genres.NAME)
            val albumArtistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Albums.ARTIST)

            while (it.moveToNext()) {
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
                val coverUri: Uri = "content://$AUTHORITY/cover/$id".toUri()



                val chapterImporter = ChapterImporter(context, contentUri)
                audiobooks += AudiobookMetadata(
                    uri = contentUri,
                    coverUri = coverUri,
                    title = title,
                    duration = duration,
                    author = composer ?: albumArtist ?: "Unknown author", //TODO: Use String resource
                    narrator = artist ?: "Unknown narrator",
                    chapters = chapterImporter.getAll(),
                    position = bookmark,
                    genre = genre
                )
            }
        }
        return audiobooks
    }

    private fun makeM4bQuery(): Cursor? {
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
            MediaStore.Audio.Genres.NAME, // TODO: Check if it is working
            MediaStore.Audio.Albums.ARTIST
        )

        val selection = "${MediaStore.Audio.Media.DISPLAY_NAME} LIKE ?"
        val selectionArgs = arrayOf(
            "%.m4b"
        )

        val sortOrder = "${MediaStore.Audio.Media.DISPLAY_NAME} ASC"

        return context.contentResolver.query(
            collection,
            projection,
            selection,
            selectionArgs,
            sortOrder
        )
    }
}