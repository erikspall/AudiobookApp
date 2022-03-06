package de.erikspall.audiobookapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import de.erikspall.audiobookapp.domain.repository.AppRepository
import de.erikspall.audiobookapp.domain.repository.PlayerControllerRepository
import de.erikspall.audiobookapp.domain.use_case.audiobook.*
import de.erikspall.audiobookapp.domain.use_case.playback.*
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object UseCaseModule {

    @Provides
    @Singleton
    fun provideAudiobookUseCases(repository: AppRepository): AudiobookUseCases {
        return AudiobookUseCases(
            getBooksWithPersons = GetBooksWithPersons(repository),
            importBooksLocal = ImportBooksLocal(repository),
            getBooksWithInfo = GetBooksWithInfo(repository),
            savePosition = SavePosition(repository)
        )
    }

    @Provides
    @Singleton
    fun providePlaybackUseCases(repository: PlayerControllerRepository): PlaybackUseCases {
        return PlaybackUseCases(
            togglePlayback = TogglePlayback(repository),
            playBook = PlayBook(repository),
            initizialize = Initizialize(repository),
            releaseController = ReleaseController(repository),
            addListener = AddListener(repository),
            getCurrent = GetCurrent(repository),
            state = State(repository),
            seekTo = SeekTo(repository),
            skip = Skip(repository)
        )
    }
}