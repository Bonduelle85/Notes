package com.gorokhov.notes.domain

import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    suspend fun addNote(
        title: String,
        content: String,
        updatedAt: Long,
        isPinned: Boolean
    )

    suspend fun editNote(note: Note)

    suspend fun getNote(noteId: Int): Note

    suspend fun deleteNote(noteId: Int)

    fun getAllNotes(): Flow<List<Note>>

    fun searchNotes(query: String): Flow<List<Note>>

    suspend fun switchPinnedStatus(noteId: Int)
}