package de.erikspall.audiobookapp.data.dao

import androidx.room.*
import de.erikspall.audiobookapp.data.model.ReadBy
import kotlinx.coroutines.flow.Flow

interface ReadByDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(readBy: ReadBy)

    @Update
    suspend fun update(readBy: ReadBy)

    @Delete
    suspend fun delete(readBy: ReadBy)


}