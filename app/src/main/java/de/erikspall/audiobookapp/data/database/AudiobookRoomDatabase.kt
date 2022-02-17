package de.erikspall.audiobookapp.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.sqlite.db.SupportSQLiteDatabase
import de.erikspall.audiobookapp.data.dao.*
import de.erikspall.audiobookapp.data.model.*
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@Database(entities = [Audiobook::class,  Genre::class, BelongsTo::class, Chapter::class, Person::class], version = 1, exportSchema = false)
abstract class AudiobookRoomDatabase: RoomDatabase() {
    abstract fun audiobookDao(): AudiobookDao
    abstract fun genreDao(): GenreDao
    abstract fun belongsToDao(): BelongsToDao
    abstract fun personDao(): PersonDao
    abstract fun chapterDao(): ChapterDao
    // Add more Dao's here

    companion object {
        @Volatile
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

                //context.applicationContext.deleteDatabase("audiobook_database") //TODO: REMOVE BEFORE RELEASE

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

        private class DatabaseCallback(
            private val scope: CoroutineScope
        ) : RoomDatabase.Callback() {
            /**
             * Override the onCreate method to populate the database.
             */
            override fun onCreate(db: SupportSQLiteDatabase) {
                super.onCreate(db)
                // If you want to keep the data through app restarts,
                // comment out the following line.
                INSTANCE?.let { database ->
                    scope.launch(Dispatchers.IO) {
                        populateDatabase(database.audiobookDao(), database.belongsToDao(), database.genreDao(), database.personDao())
                    }
                }
            }
        }

        /**
         * Populate the database in a new coroutine.
         * If you want to start with more words, just add them.
         * This is a dummy database!!!
         */
        suspend fun populateDatabase(audiobookDao: AudiobookDao, belongsToDao: BelongsToDao, genreDao: GenreDao, personDao: PersonDao) {
            // Start the app with a clean database every time.
            // Not needed if you only populate on creation.
            audiobookDao.deleteAll()
            belongsToDao.deleteAll()
            genreDao.deleteAll()
            personDao.deleteAll()

           /* val person = Person(firstName =  "Mark-Uwe", lastName = "Kling", isAuthor = true, isReader = true)
            val personId = personDao.insert(person)

            val audiobook = Audiobook(uri = "uri", title = "Die Känguru Chroniken", duration = 12000, authorId = personId, readerId = personId, position = 4500)
            val audiobookId = audiobookDao.insert(audiobook)

            val genre1 = Genre(name = "Comedy")
            val genre2 = Genre(name = "Satire")
            val genreId1 = genreDao.insert(genre1)
            val genreId2 = genreDao.insert(genre2)

            belongsToDao.addGenreToAudiobook(genreId1, audiobookId)
            belongsToDao.addGenreToAudiobook(genreId2, audiobookId)*/




        }
    }
}
