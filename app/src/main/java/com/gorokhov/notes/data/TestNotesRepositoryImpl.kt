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

    override suspend  fun addNote(
        title: String,
        content: String,
        updatedAt: Long,
        isPinned: Boolean,
    ) {
        notesListFlow.update { oldList ->
            val note = Note(
                id = oldList.size,
                title = title,
                content = content,
                updatedAt = updatedAt,
                isPinned = isPinned
            )
            oldList + note
        }
    }

    override suspend  fun editNote(note: Note) {
        notesListFlow.update { list ->
            list.map { currentNote ->
                if (currentNote.id == note.id) note else currentNote
            }
        }
    }

    override suspend  fun getNote(noteId: Int): Note {
        return notesListFlow.value.first { it.id == noteId }
    }

    override suspend  fun deleteNote(noteId: Int) {
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

    override suspend  fun switchPinnedStatus(noteId: Int) {
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