package com.gorokhov.notes.domain

import kotlinx.coroutines.flow.Flow

interface NotesRepository {

    fun addNote(
        title: String,
        content: String
    )

    fun editNote(note: Note)

    fun getNote(noteId: Int): Note

    fun deleteNote(noteId: Int)

    fun getAllNotes(): Flow<List<Note>>

    fun searchNotes(query: String): Flow<List<Note>>

    fun switchPinnedStatus(noteId: Int)
}