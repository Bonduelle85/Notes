@file:Suppress("OPT_IN_USAGE")

package com.gorokhov.notes.presentation.screens.notes

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorokhov.notes.domain.GetAllNotesUseCase
import com.gorokhov.notes.domain.Note
import com.gorokhov.notes.domain.SearchNotesUseCase
import com.gorokhov.notes.domain.SwitchPinnedStatusUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NotesViewModel @Inject constructor(
    getAllNotesUseCase: GetAllNotesUseCase,
    searchNotesUseCase: SearchNotesUseCase,
    private val switchPinnedStatusUseCase: SwitchPinnedStatusUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(NotesScreenState())
    val state = _state.asStateFlow()

    private val query = MutableStateFlow("")

    init {
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

    fun processCommand(command: NotesCommand) {
        viewModelScope.launch {
            when (command) {
                is NotesCommand.SwitchPinStatus -> {
                    switchPinnedStatusUseCase(command.noteId)
                }

                is NotesCommand.InputSearchQuery -> {
                    query.update { command.query.trim() }
                }
            }
        }
    }
}

sealed interface NotesCommand {

    data class InputSearchQuery(val query: String) : NotesCommand

    data class SwitchPinStatus(val noteId: Int) : NotesCommand
}

data class NotesScreenState(
    val query: String = "",
    val pinnedNotes: List<Note> = listOf(),
    val otherNotes: List<Note> = listOf(),
)

