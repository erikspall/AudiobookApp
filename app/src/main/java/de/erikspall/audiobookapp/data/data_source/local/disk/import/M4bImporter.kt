package de.erikspall.audiobookapp.data.data_source.local.disk.import

import android.content.ContentUris
import android.content.Context
import android.database.Cursor
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.os.Environment
import android.provider.MediaStore
import androidx.core.net.toUri
import de.erikspall.audiobookapp.data.data_source.local.disk.AudiobookMetadata
import de.erikspall.audiobookapp.domain.provider.AUTHORITY
import java.io.File

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
            val genreColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.GENRE)
            val albumArtistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ALBUM_ARTIST)

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
               // val coverUri: Uri = "content://$AUTHORITY/cover/$id".toUri()

                val chapterImporter = ChapterImporter(context, contentUri)
                audiobooks += AudiobookMetadata(
                    uri = contentUri,
                    coverUri = getOrSaveCover(contentUri, id),
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
            MediaStore.Audio.Media.GENRE, // TODO: Check if it is working
            MediaStore.Audio.Media.ALBUM_ARTIST
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

    /**
     * Will return the Uri of this audiobooks cover bitmap file.
     * Returns an empty Uri if cover for the audiobook was not found
     */
    private fun getOrSaveCover(audiobookUri: Uri, id: Long): Uri{
        val file = File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "$id.cover")
        if (!file.exists()){
            val mmr = MediaMetadataRetriever()
            mmr.setDataSource(context, audiobookUri)
            val cover = mmr.embeddedPicture
            if (cover != null) {
                file.writeBytes(cover)
                file.setReadOnly()
                return "content://$AUTHORITY/cover/$id".toUri()
            }
            return "".toUri()
        }
        return "content://$AUTHORITY/cover/$id".toUri()
    }

}