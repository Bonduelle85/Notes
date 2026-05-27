package com.gorokhov.notes.presentation.screens.notes

import android.content.Context
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldColors
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gorokhov.notes.domain.Note
import com.gorokhov.notes.presentation.ui.theme.OtherNotesColorsDark
import com.gorokhov.notes.presentation.ui.theme.OtherNotesColorsLight
import com.gorokhov.notes.presentation.ui.theme.PinnedNotesColorsDark
import com.gorokhov.notes.presentation.ui.theme.PinnedNotesColorsLight
import com.gorokhov.notes.presentation.utils.DateFormatter

@Composable
fun NotesScreen(
    modifier: Modifier = Modifier,
    context: Context = LocalContext.current.applicationContext,
    viewModel: NotesViewModel = viewModel {
        NotesViewModel(context = context)
    },
    onNoteClick: (Note) -> Unit,
    onAddNoteClick: () -> Unit,
) {

    val state by viewModel.state.collectAsState()

    Scaffold(
        modifier = modifier.background(MaterialTheme.colorScheme.background),
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAddNoteClick,
                contentColor = MaterialTheme.colorScheme.onPrimary,
                containerColor = MaterialTheme.colorScheme.primary,
                shape = CircleShape
            ) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = "Add Note Button"
                )
            }
        }
    ) { innerPadding ->

        LazyColumn(
            contentPadding = innerPadding,
        ) {

            item {
                Title(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = "All Notes"
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                SearchBar(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    query = state.query,
                    onInputChanged = {
                        viewModel.processCommand(NotesCommand.InputSearchQuery(it))
                    }
                )
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Subtitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = "Pinned"
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            item {
                LazyRow(
                    modifier = Modifier
                        .fillMaxWidth(),
                    horizontalArrangement =  Arrangement.spacedBy(8.dp),
                    contentPadding = PaddingValues(horizontal = 24.dp)
                ) {

                    state.pinnedNotes.forEachIndexed { index, note ->
                        item(key = note.id) {
                            NoteCard(
                                modifier = modifier.widthIn(max = 160.dp),
                                note = note,
                                backgroundColor = if (!isSystemInDarkTheme())
                                    PinnedNotesColorsLight[index % PinnedNotesColorsLight.size]
                                else PinnedNotesColorsDark[index % PinnedNotesColorsDark.size],
                                onClick = onNoteClick,
                                onLongClick = {
                                    viewModel.processCommand(
                                        NotesCommand.SwitchPinStatus(note.id)
                                    )
                                }
                            )
                        }
                    }
                }
            }
            item {
                Spacer(modifier = Modifier.height(24.dp))
            }
            item {
                Subtitle(
                    modifier = Modifier.padding(horizontal = 24.dp),
                    text = "Others"
                )
            }
            item {
                Spacer(modifier = Modifier.height(16.dp))
            }
            itemsIndexed( // extension fun
                items = state.otherNotes,
                key = { index, note -> note.id }
            ) { index,note ->
                NoteCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 24.dp),
                    note = note,
                    backgroundColor = if (!isSystemInDarkTheme())
                        OtherNotesColorsLight[index % OtherNotesColorsLight.size]
                    else OtherNotesColorsDark[index % OtherNotesColorsDark.size],
                    onClick = onNoteClick,
                    onLongClick = {
                        viewModel.processCommand(
                            NotesCommand.SwitchPinStatus(note.id)
                        )
                    }
                )
                Spacer(modifier = Modifier.height(8.dp))
            }
        }
    }
}

@Composable
private fun Title(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 24.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onBackground

    )
}

@Composable
private fun SearchBar(
    modifier: Modifier = Modifier,
    query: String,
    onInputChanged: (String) -> Unit,
) {
    TextField(
        modifier = modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                shape = RoundedCornerShape(10.dp)
            ),
        value = query,
        onValueChange = onInputChanged,
        placeholder = {
            Text(
                text = "Search...",
                fontSize = 14.sp,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        },
        leadingIcon = {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = "Search notes",
                tint = MaterialTheme.colorScheme.onSurface
            )
        },
        shape = RoundedCornerShape(10.dp),
        colors = TextFieldDefaults.colors(
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            unfocusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
        )
    )
}

@Composable
private fun Subtitle(
    modifier: Modifier = Modifier,
    text: String,
) {
    Text(
        modifier = modifier,
        text = text,
        fontSize = 14.sp,
        fontWeight = FontWeight.Bold,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun NoteCard(
    modifier: Modifier = Modifier,
    note: Note,
    backgroundColor: Color,
    onClick: (Note) -> Unit,
    onLongClick: (Note) -> Unit
) {
    Column(
        modifier = modifier
            .clip(RoundedCornerShape(16.dp))
            .background(color = backgroundColor)
            .combinedClickable(
                onClick = { onClick(note) },
                onLongClick = { onLongClick(note) }
            )
            .padding(16.dp)
    ) {
        Text(
            text = note.title,
            fontSize = 14.sp,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface,
        )
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = DateFormatter.formatDateToString(note.updatedAt),
            fontSize = 12.sp,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(modifier = Modifier.height(24.dp))
        Text(
            text = note.content,
            fontSize = 16.sp,
            maxLines = 3,
            overflow = TextOverflow.Ellipsis,
            color = MaterialTheme.colorScheme.onSurface,
            fontWeight = FontWeight.Medium
        )
    }
}