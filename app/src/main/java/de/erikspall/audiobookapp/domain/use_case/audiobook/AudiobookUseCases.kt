package de.erikspall.audiobookapp.domain.use_case.audiobook

data class AudiobookUseCases (
    val getBooksWithPersons: GetBooksWithPersons,
    val getBooksWithInfo: GetBooksWithInfo,
    val importBooksLocal: ImportBooksLocal,
    val savePosition: SavePosition
    /* Ad more here when app grows */
)