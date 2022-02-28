package de.erikspall.audiobookapp.di

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.components.ViewModelComponent
import dagger.hilt.android.scopes.ViewModelScoped
import de.erikspall.audiobookapp.domain.repository.AppRepository
import de.erikspall.audiobookapp.domain.use_case.AudiobookUseCases
import de.erikspall.audiobookapp.domain.use_case.GetBooksWithPersons
import de.erikspall.audiobookapp.domain.use_case.ImportBooksLocal

@Module
@InstallIn(ViewModelComponent::class)
object UseCaseModule {

    @Provides
    @ViewModelScoped
    fun provideAudiobookUseCases(repository: AppRepository): AudiobookUseCases {
        return AudiobookUseCases(
            getBooksWithPersons = GetBooksWithPersons(repository),
            importBooksLocal = ImportBooksLocal(repository)
        )
    }
}