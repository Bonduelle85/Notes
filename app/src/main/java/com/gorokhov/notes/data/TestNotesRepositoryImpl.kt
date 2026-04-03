package com.gorokhov.notes.data

import com.gorokhov.notes.domain.Note
import com.gorokhov.notes.domain.NotesRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

object TestNotesRepositoryImpl : NotesRepository {

    private val notesListFlow = MutableStateFlow<List<Note>>(emptyList())

    override fun addNote(note: Note) {
        notesListFlow.update { list ->
            list + note
        }
    }

    override fun editNote(note: Note) {
        notesListFlow.update { list ->
            list.map { currentNote ->
                if (currentNote.id == note.id) note else currentNote
            }
        }
    }

    override fun getNote(noteId: Int): Note {
        return notesListFlow.value.first { it.id == noteId }
    }

    override fun deleteNote(noteId: Int) {
        notesListFlow.update { list ->
            list.filter { it.id != noteId }
        }
    }

    override fun getAllNotes(): Flow<List<Note>> {
        return notesListFlow.asStateFlow()
    }

    override fun searchNotes(query: String): Flow<List<Note>> {
        return notesListFlow.map { list ->
            list.filter {
                it.content.contains(query, ignoreCase = true) ||
                        it.title.contains(query, ignoreCase = true)
            }
        }
    }

    override fun switchPinnedStatus(noteId: Int) {
        notesListFlow.update { list ->
            list.map { currentNote ->
                if (currentNote.id == noteId) {
                    currentNote.copy(isPinned = !currentNote.isPinned)
                } else {
                    currentNote
                }
            }
        }
    }
}