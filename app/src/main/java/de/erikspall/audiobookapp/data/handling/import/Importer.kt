package de.erikspall.audiobookapp.data.handling.import

import android.content.Context

interface Importer<T> {
   companion object Factory {
      fun createLocalImporter(context: Context): LocalImporter {
         return LocalImporter(context)
      }
   }

   fun getAll(): List<T>
}