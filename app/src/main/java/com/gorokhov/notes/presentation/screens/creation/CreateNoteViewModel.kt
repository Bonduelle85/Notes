package com.gorokhov.notes.presentation.screens.creation

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.gorokhov.notes.data.RoomNotesRepositoryImpl
import com.gorokhov.notes.data.TestNotesRepositoryImpl
import com.gorokhov.notes.domain.AddNoteUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class CreateNoteViewModel(context: Context) : ViewModel() {

    //private val repository = TestNotesRepositoryImpl
    private val repository = RoomNotesRepositoryImpl.getInstance(context)
    private val addNoteUseCase = AddNoteUseCase(repository)

    private val _state = MutableStateFlow<CreateNoteState>(CreateNoteState.Creation())
    val state = _state.asStateFlow()

    fun processCommand(command: CreateNoteCommand) {

        when (command) {
            CreateNoteCommand.ClickBack -> {
                _state.update { CreateNoteState.Finished }
            }

            CreateNoteCommand.ClickSave -> {
                viewModelScope.launch {
                    _state.update { previousState ->
                        if (previousState is CreateNoteState.Creation) {
                            val title = previousState.title
                            val content = previousState.content
                            addNoteUseCase(title = title, content = content)
                            CreateNoteState.Finished
                        } else {
                            previousState
                        }
                    }
                }
            }

            is CreateNoteCommand.InputContent -> {
                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) {
                        previousState.copy(
                            content = command.content,
                            isSaveEnable = previousState.title.isNotBlank()
                                    && command.content.isNotBlank()
                        )
                    } else CreateNoteState.Creation(content = command.content)
                }
            }

            is CreateNoteCommand.InputTitle -> {
                _state.update { previousState ->
                    if (previousState is CreateNoteState.Creation) {
                        previousState.copy(
                            title = command.title,
                            isSaveEnable = command.title.isNotBlank()
                                    && previousState.content.isNotBlank()
                        )
                    } else CreateNoteState.Creation(title = command.title)
                }
            }
        }
    }
}

sealed interface CreateNoteCommand {

    data class InputTitle(
        val title: String
    ) : CreateNoteCommand

    data class InputContent(
        val content: String
    ) : CreateNoteCommand

    data object ClickSave : CreateNoteCommand

    data object ClickBack : CreateNoteCommand
}

sealed interface CreateNoteState {

    data class Creation(
        val title: String = "",
        val content: String = "",
        val isSaveEnable: Boolean = false
    ) : CreateNoteState

    data object Finished : CreateNoteState
}