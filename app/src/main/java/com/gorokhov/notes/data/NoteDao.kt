package com.gorokhov.notes.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface NoteDao {

    @Query("SELECT * FROM notes ORDER BY isPinned DESC, updatedAt DESC")
    fun getAllNotes(): Flow<List<NoteDbModel>>

    @Query("SELECT * FROM notes WHERE id = :id")
    suspend fun getNoteById(id: Int): NoteDbModel

    @Query(
        """
        SELECT * FROM notes 
        WHERE title LIKE '%' || :query || '%' 
           OR content LIKE '%' || :query || '%' 
        ORDER BY updatedAt DESC
        """
    )
    fun searchNotes(query: String): Flow<List<NoteDbModel>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNote(note: NoteDbModel)

    @Update
    suspend fun updateNote(note: NoteDbModel)

    @Query("DELETE FROM notes WHERE id = :id")
    suspend fun deleteNoteById(id: Int)

    @Query("UPDATE notes SET isPinned = CASE WHEN isPinned = 1 THEN 0 ELSE 1 END WHERE id = :id")
    suspend fun togglePin(id: Int)
}