package de.erikspall.audiobookapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import de.erikspall.audiobookapp.data.dao.AudiobookDao
import de.erikspall.audiobookapp.data.dao.BelongsToDao
import de.erikspall.audiobookapp.data.dao.GenreDao
import de.erikspall.audiobookapp.data.dao.PersonDao
import de.erikspall.audiobookapp.data.model.Audiobook
import de.erikspall.audiobookapp.data.model.BelongsTo
import de.erikspall.audiobookapp.data.model.Genre
import de.erikspall.audiobookapp.data.model.Person

@Database(entities = [Audiobook::class, Person::class, Genre::class, BelongsTo::class], version = 1, exportSchema = false)
abstract class AudiobookRoomDatabase: RoomDatabase() {
    abstract fun audiobookDao(): AudiobookDao
    abstract fun personDao(): PersonDao
    abstract fun genreDao(): GenreDao
    abstract fun belongsToDao(): BelongsToDao

    // Add more Dao's here

    companion object {
        private var INSTANCE: AudiobookRoomDatabase? = null
        /**
         * Make sure only one database exists (Singleton). synchronized creation of instance
         * means, only one thread can enter that block, so only one database will be created
         */
        fun getDatabase(context: Context): AudiobookRoomDatabase {
            /**
             * Make sure only one database exists (Singleton). synchronized creation of instance
             * means, only one thread can enter that block, so only one database will be created
             */
            return INSTANCE ?: synchronized(this) {
                context.applicationContext.deleteDatabase("audiobook_database") //TODO: REMOVE BEFORE RELEASE
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AudiobookRoomDatabase::class.java,
                    "audiobook_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()

                INSTANCE = instance
                return instance
            }
        }
    }
}