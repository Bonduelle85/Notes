package com.gorokhov.notes.data

import com.gorokhov.notes.domain.Note
import com.gorokhov.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class RoomNotesRepositoryImpl @Inject constructor(
    private val noteDao: NoteDao
) : NotesRepository {

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
}