package com.gorokhov.notes.presentation.screens.notes

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gorokhov.notes.domain.Note

@Composable
fun NotesScreen(
    modifier: Modifier,
    viewModel: NotesViewModel = viewModel()
) {

    val state by viewModel.state.collectAsState()

    LazyColumn {

        item {
            LazyRow(
                modifier = modifier
            ) {
                state.pinnedNotes.forEach { note ->
                    item(key = note.id) {
                        NoteCard(
                            note = note,
                            onClick = {
                                viewModel.processCommand(
                                    NotesCommand.SwitchPinStatus(note.id)
                                )
                            }
                        )
                    }
                }
            }
        }

        items( // extension fun
            items = state.otherNotes,
            key = { note -> note.id }
        ) { note ->
            NoteCard(
                note = note,
                onClick = {
                    viewModel.processCommand(
                        NotesCommand.SwitchPinStatus(note.id)
                    )
                }
            )
        }
    }
}

@Composable
private fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    onClick: (Note) -> Unit
) {
    Text(
        text = "${note.title} - ${note.content}",
        modifier = modifier
            .clickable { onClick(note) }
            .padding(16.dp)
    )
}