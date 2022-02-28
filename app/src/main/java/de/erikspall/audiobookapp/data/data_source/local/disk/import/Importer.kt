package de.erikspall.audiobookapp.data.data_source.local.disk.import

interface Importer<T> {
    fun getAll(): List<T>
}