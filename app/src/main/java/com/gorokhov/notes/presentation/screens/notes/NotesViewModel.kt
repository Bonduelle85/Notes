@file:Suppress("OPT_IN_USAGE")

package com.gorokhov.notes.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorokhov.notes.data.TestNotesRepositoryImpl
import com.gorokhov.notes.domain.AddNoteUseCase
import com.gorokhov.notes.domain.DeleteNoteUseCase
import com.gorokhov.notes.domain.EditNoteUseCase
import com.gorokhov.notes.domain.GetAllNotesUseCase
import com.gorokhov.notes.domain.GetNoteUseCase
import com.gorokhov.notes.domain.Note
import com.gorokhov.notes.domain.SearchNotesUseCase
import com.gorokhov.notes.domain.SwitchPinnedStatusUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update

class NotesViewModel : ViewModel() {

    private val repository = TestNotesRepositoryImpl

    private val addNoteUseCase = AddNoteUseCase(repository)
    private val deleteNoteUseCase = DeleteNoteUseCase(repository)
    private val editNoteUseCase = EditNoteUseCase(repository)
    private val getAllNotesUseCase = GetAllNotesUseCase(repository)
    private val getNoteUseCase = GetNoteUseCase(repository)
    private val searchNotesUseCase = SearchNotesUseCase(repository)
    private val switchPinnedStatusUseCase = SwitchPinnedStatusUseCase(repository)

    private val _state = MutableStateFlow(NotesScreenState())
    val state = _state.asStateFlow()

    private val query = MutableStateFlow("")

    init {
        addSomeNotes()

        query
            .onEach { input ->
                _state.update {
                    it.copy(query = input)
                }
            }
            .flatMapLatest { input ->
                if (input.isBlank()) {
                    getAllNotesUseCase()
                } else {
                    searchNotesUseCase(input)
                }
            }
            .onEach { notes ->
                val pinnedNotes = notes.filter { note ->
                    note.isPinned
                }
                val otherNotes = notes.filter { note ->
                    !note.isPinned
                }
                _state.update {
                    it.copy(pinnedNotes = pinnedNotes, otherNotes = otherNotes)
                }
            }
            .launchIn(viewModelScope)
    }

    // Temp method
    private fun addSomeNotes() {
        repeat(50) {
            addNoteUseCase(
                title = "Title$it",
                content = "Content$it"
            )
        }
    }

    fun processCommand(command: NotesCommand) {
        when (command) {
            is NotesCommand.SwitchPinStatus -> {
                switchPinnedStatusUseCase(command.noteId)
            }

            is NotesCommand.DeleteNote -> {
                deleteNoteUseCase(command.noteId)
            }

            is NotesCommand.EditNote -> {
                val note = getNoteUseCase(command.note.id)
                val title = note.title
                editNoteUseCase(note.copy(title = "$title Edited"))
            }

            is NotesCommand.InputSearchQuery -> {
                query.update { command.query.trim() }
            }
        }
    }
}

sealed interface NotesCommand {

    data class InputSearchQuery(val query: String) : NotesCommand

    data class SwitchPinStatus(val noteId: Int) : NotesCommand

    // Test Commands
    data class DeleteNote(val noteId: Int) : NotesCommand

    data class EditNote(val note: Note) : NotesCommand
}

data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes: List<Note> = listOf(),
)

