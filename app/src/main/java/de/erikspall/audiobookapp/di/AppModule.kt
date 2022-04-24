package de.erikspall.audiobookapp.di

import android.app.Application
import android.content.Context
import android.content.SharedPreferences
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import de.erikspall.audiobookapp.R
import de.erikspall.audiobookapp.data.source.local.database.AudiobookDatabase
import de.erikspall.audiobookapp.data.source.local.disk.AudiobookFileDataSource
import de.erikspall.audiobookapp.data.source.local.player_controller.ControllerDataSource
import de.erikspall.audiobookapp.data.source.local.player_controller.MediaItemTreeDataSource
import de.erikspall.audiobookapp.data.repository.AppRepositoryImpl
import de.erikspall.audiobookapp.data.repository.database.*
import de.erikspall.audiobookapp.data.repository.player_controller.PlayerControllerRepositoryImpl
import de.erikspall.audiobookapp.domain.repository.*
import kotlinx.coroutines.CoroutineDispatcher
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {
    @Provides
    @Singleton
    fun provideBookDatabase(app: Application): AudiobookDatabase {
        return AudiobookDatabase.getDatabase(app)
    }

    @Provides
    @Singleton
    fun provideAudiobookRepository(db: AudiobookDatabase): AudiobookRepository {
        return AudiobookRepositoryImpl(db.audiobookDao())
    }

    @Provides
    @Singleton
    fun provideBelongsToRepository(db: AudiobookDatabase): BelongsToRepository {
        return BelongsToRepositoryImpl(db.belongsToDao())
    }

    @Provides
    @Singleton
    fun provideChapterRepository(db: AudiobookDatabase): ChapterRepository {
        return ChapterRepositoryImpl(db.chapterDao())
    }

    @Provides
    @Singleton
    fun provideGenreRepository(db: AudiobookDatabase): GenreRepository {
        return GenreRepositoryImpl(db.genreDao())
    }

    @Provides
    @Singleton
    fun providePersonRepository(db: AudiobookDatabase): PersonRepository {
        return PersonRepositoryImpl(db.personDao())
    }

    @Provides
    @Singleton
    fun provideAudiobookFileDataSource(
        @ApplicationContext appContext: Context,
        @IoDispatcher ioDispatcher: CoroutineDispatcher
    ): AudiobookFileDataSource {
        return AudiobookFileDataSource(appContext, ioDispatcher)
    }

    @Provides
    @Singleton
    fun provideControllerDataSource(
        @ApplicationContext appContext: Context
    ): ControllerDataSource {
        return ControllerDataSource(appContext) { /* Don't do anything */ }
    }

    @Provides
    @Singleton
    fun provideMediaItemTreeDataSource(
    ): MediaItemTreeDataSource {
        return MediaItemTreeDataSource()
    }

    @Provides
    @Singleton
    fun providePlayerControllerRepository(
        mediaItemTreeDataSource: MediaItemTreeDataSource,
        controllerDataSource: ControllerDataSource
    ) : PlayerControllerRepository {
        return PlayerControllerRepositoryImpl(
            mediaItemTreeDataSource,
            controllerDataSource
        )
    }

    @Provides
    @Singleton
    fun provideAppRepository(
        audiobookRepo: AudiobookRepository,
        belongsToRepo: BelongsToRepository,
        chapterRepo: ChapterRepository,
        genreRepo: GenreRepository,
         personRepo: PersonRepository,
        /* AudiobookFileDataSource */
        audiobookFileDataSource: AudiobookFileDataSource): AppRepository {
        return AppRepositoryImpl(
            audiobookRepo,
            belongsToRepo,
            chapterRepo,
            genreRepo,
            personRepo,
            audiobookFileDataSource
        )
    }

    @Provides
    @Singleton
    fun provideSleepTimerSharedPref(
        @ApplicationContext appContext: Context
    ) : SharedPreferences {
        return appContext.getSharedPreferences(appContext.getString(R.string.sleep_timer_shared_pref_name),Context.MODE_PRIVATE)
    }
}