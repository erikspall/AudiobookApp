package de.erikspall.audiobookapp.data.handling.import

import android.content.ContentUris
import android.content.Context
import android.media.MediaMetadataRetriever
import android.net.Uri
import android.provider.MediaStore
import android.util.Log
import de.erikspall.audiobookapp.data.model.Audiobook2
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class LocalImporter(val context: Context) : Importer<Audiobook2> {

    override fun getAll(): List<Audiobook2> {
        return searchForAudiobooks()
    }

    suspend fun getAllAsync(): List<Audiobook2>{
        return withContext(Dispatchers.IO){
            searchForAudiobooks()
        }
    }

    private fun searchForAudiobooks(): List<Audiobook2> {
        val audioList = mutableListOf<Audiobook2>()

        val collection =
            MediaStore.Audio.Media.getContentUri(
                MediaStore.VOLUME_EXTERNAL
            )

        val projection = arrayOf(
            MediaStore.Audio.Media._ID,
            MediaStore.Audio.Media.TITLE,
            MediaStore.Audio.Media.DURATION,
            MediaStore.Audio.Media.ARTIST,
            MediaStore.Audio.Media.BOOKMARK,
            MediaStore.Audio.Media.TRACK,
            MediaStore.Audio.Media.DISPLAY_NAME,
            MediaStore.Audio.Media.RELATIVE_PATH,
            MediaStore.Audio.Media.DATA
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
            val idColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)
            val titleColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)
            val durationColumn =
                cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)
            val artistColumn = cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)

            while (cursor.moveToNext()) {
                // Get info of audio
                val id = cursor.getLong(idColumn)
                val title = cursor.getString(titleColumn)
                val duration = cursor.getInt(durationColumn)
                val artist = cursor.getString(artistColumn)

                val contentUri: Uri = ContentUris.withAppendedId(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    id
                )

                val retriever = MediaMetadataRetriever()
                retriever.setDataSource(context, contentUri)


                val path = cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.RELATIVE_PATH)).toString() + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DISPLAY_NAME)).toString()
                Log.d("Imported", "\n " + "ID: " + cursor.getLong(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media._ID)).toString() + "\n" +
                        "TITLE: " + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.TITLE)).toString() + "\n" +
                        "DURATION: " + cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.DURATION)).toString() + "\n" +
                        "ARTIST: " + cursor.getString(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.ARTIST)).toString() + "\n" +
                        "BOOKMARK: " + cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Audio.Media.BOOKMARK)).toString() + "\n")
                audioList += Audiobook2(id, contentUri, title, duration, artist)


                Log.d("[CHAPTERS:]", ChapterImporter(context, contentUri).getAll().toString())



            }
        }
        return audioList
    }


}