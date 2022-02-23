package de.erikspall.audiobookapp.data.source.local.disk.import

interface Importer<T> {
    fun getAll(): List<T>
}