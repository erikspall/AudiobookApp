package de.erikspall.audiobookapp.domain.use_case

data class AudiobookUseCases (
    val getBooksWithPersons: GetBooksWithPersons,
    val importBooksLocal: ImportBooksLocal
    /* Ad more here when app grows */
)