package com.gorokhov.notes.presentation.screens.editing

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorokhov.notes.data.TestNotesRepositoryImpl
import com.gorokhov.notes.domain.DeleteNoteUseCase
import com.gorokhov.notes.domain.EditNoteUseCase
import com.gorokhov.notes.domain.GetNoteUseCase
import com.gorokhov.notes.domain.Note
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class EditNoteViewModel(private val noteId: Int) : ViewModel() {

    private val repository = TestNotesRepositoryImpl
    private val editNoteUseCase = EditNoteUseCase(repository)
    private val getNoteUseCase = GetNoteUseCase(repository)
    private val deleteNoteUseCase = DeleteNoteUseCase(repository)

    private val _state = MutableStateFlow<EditNoteState>(EditNoteState.Initial)
    val state = _state.asStateFlow()

    init {
        viewModelScope.launch {
            _state.update {
                val note = getNoteUseCase(noteId = noteId)
                EditNoteState.Editing(note = note)
            }
        }
    }

    fun processCommand(command: EditNoteCommand) {

        when (command) {
            EditNoteCommand.ClickBack -> {
                _state.update { EditNoteState.Finished }
            }

            EditNoteCommand.ClickSave -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is EditNoteState.Editing) {
                            val note = previousState.note
                            editNoteUseCase(note = note)
                            EditNoteState.Finished
                        } else {
                            previousState
                        }
                    }
                }
            }

            is EditNoteCommand.InputContent -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newNote = previousState.note.copy(
                            content = command.content
                        )
                        previousState.copy(note = newNote)
                    } else previousState
                }
            }

            is EditNoteCommand.InputTitle -> {
                _state.update { previousState ->
                    if (previousState is EditNoteState.Editing) {
                        val newNote = previousState.note.copy(
                            title = command.title
                        )
                        previousState.copy(note = newNote)
                    } else previousState
                }
            }

            EditNoteCommand.ClickDelete -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is EditNoteState.Editing) {
                            val note = previousState.note
                            deleteNoteUseCase(noteId = note.id)
                            EditNoteState.Finished
                        } else {
                            previousState
                        }
                    }
                }
            }
        }
    }
}

sealed interface EditNoteCommand {

    data class InputTitle(
        val title: String
    ) : EditNoteCommand

    data class InputContent(
        val content: String
    ) : EditNoteCommand

    data object ClickSave : EditNoteCommand

    data object ClickDelete : EditNoteCommand

    data object ClickBack : EditNoteCommand
}

sealed interface EditNoteState {

    data object Initial : EditNoteState

    data class Editing(
        val note: Note
    ) : EditNoteState {

        val isSaveEnable: Boolean
            get() = note.title.isNotBlank() && note.content.isNotBlank()
    }

    data object Finished : EditNoteState
}