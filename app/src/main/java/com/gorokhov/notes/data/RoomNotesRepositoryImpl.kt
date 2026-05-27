package com.gorokhov.notes.data

import android.content.Context
import com.gorokhov.notes.domain.Note
import com.gorokhov.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class RoomNotesRepositoryImpl private constructor(
    context: Context
) : NotesRepository {

    private val noteDatabase = AppDatabase.getInstance(context)
    private val noteDao = noteDatabase.noteDao()

    override suspend fun addNote(
        title: String,
        content: String,
        updatedAt: Long,
        isPinned: Boolean
    ) {
        val noteDbModel = NoteDbModel(
            title = title,
            content = content,
            updatedAt = updatedAt,
            isPinned = isPinned
        )
        noteDao.insertNote(noteDbModel)
    }

    override suspend fun editNote(note: Note) {
        noteDao.updateNote(note.toDbModel())
    }

    override suspend fun getNote(noteId: Int): Note {
        return noteDao.getNoteById(noteId).toEntity()
    }

    override suspend fun deleteNote(noteId: Int) {
        noteDao.deleteNoteById(noteId)
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return noteDao.getAllNotes().map {
            it.toEntities()
        }
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return noteDao.searchNotes(query).map {
            it.toEntities()
        }
    }

    override suspend fun switchPinnedStatus(noteId: Int) {
        noteDao.togglePin(noteId)
    }

    companion object {
        @Volatile
        private var INSTANCE: RoomNotesRepositoryImpl? = null

        fun getInstance(context: Context): RoomNotesRepositoryImpl {
            // Первая проверка (без блокировки)
            INSTANCE?.let { return it }

            // Синхронизация только при необходимости
            synchronized(this) {
                // Вторая проверка (double-check)
                INSTANCE?.let { return it }

                return RoomNotesRepositoryImpl(context).also {
                    INSTANCE = it
                }
            }
        }
    }
}