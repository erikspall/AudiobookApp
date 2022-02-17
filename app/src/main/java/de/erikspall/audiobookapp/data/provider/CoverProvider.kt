package de.erikspall.audiobookapp.data.provider

import android.content.ContentProvider
import android.content.ContentValues
import android.content.UriMatcher
import android.database.Cursor
import android.database.MatrixCursor
import android.net.Uri
import android.os.Environment
import android.os.ParcelFileDescriptor
import android.util.Log
import androidx.core.net.toUri
import de.erikspall.audiobookapp.data.dao.AudiobookDao
import de.erikspall.audiobookapp.data.database.AudiobookRoomDatabase
import java.io.File
import java.io.FileNotFoundException

public const val AUTHORITY = "de.erikspall.audiobookapp.data.provider.CoverProvider"

private val sUriMatcher = UriMatcher(UriMatcher.NO_MATCH).apply {
    /*
     * The calls to addURI() go here
     */

    /*
     *
     */
    addURI(AUTHORITY, "cover/#", 1)
}

// Defines the database name
private const val DBNAME = "mydb"

class CoverProvider: ContentProvider() {
    // Defines a handle to the Room database
    private lateinit var appDatabase: AudiobookRoomDatabase

    // Defines a Data Access Object to perform the database operations
    private var audiobookDao: AudiobookDao? = null

    override fun onCreate(): Boolean {
        // Creates a new database object.
        appDatabase = AudiobookRoomDatabase.getDatabase(context!!)

        // Gets a Data Access Object to perform the database operations
        audiobookDao = appDatabase.audiobookDao()

        return true
    }

    override fun openFile(uri: Uri, mode: String): ParcelFileDescriptor? {
        when (sUriMatcher.match(uri)) {
            1 -> {
                val location = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
                Log.d("CoverProviderDebug", "Location: ${location.toString()}")
                val file = File(location, "${uri.lastPathSegment}.cover")
                val cover = ParcelFileDescriptor.open(file, ParcelFileDescriptor.MODE_READ_ONLY)
                Log.d("CoverProviderDebug", "File: ${file.toUri()}")
                return cover
            }
            else -> {
                throw FileNotFoundException("Unsupported Uri: $uri")
            }
        }
    }

    override fun query(
        uri: Uri,
        projection: Array<out String>?,
        selection: String?,
        selectionArgs: Array<out String>?,
        sortOrder: String?
    ): Cursor? {
        val cursor = MatrixCursor(arrayOf("_id", "_data", "mime_type"))
        val location = context!!.getExternalFilesDir(Environment.DIRECTORY_PICTURES)
        val f = File(location, "${uri.lastPathSegment}.cover")
        cursor.addRow(arrayOf(0,f, "image/jpeg"))
        return cursor
    }

    override fun getType(uri: Uri): String? {
        return null
    }

    override fun getStreamTypes(uri: Uri, mimeTypeFilter: String): Array<String>? {
        return arrayOf("image/jpeg", "image/png").filter { s -> s.contains("*$mimeTypeFilter") }.toTypedArray()
    }

    /**
     * Not allowed, CoverUri's are already in database and don't need to be added
     */
    override fun insert(uri: Uri, values: ContentValues?): Uri? {
        return null
    }

    /**
     * Not allowed
     */
    override fun delete(uri: Uri, selection: String?, selectionArgs: Array<out String>?): Int {
        return 0
    }

    /**
     * Not allowed
     */
    override fun update(
        uri: Uri,
        values: ContentValues?,
        selection: String?,
        selectionArgs: Array<out String>?
    ): Int {
        return 0
    }
}