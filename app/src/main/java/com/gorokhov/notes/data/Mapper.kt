package com.gorokhov.notes.data

import com.gorokhov.notes.domain.Note

fun NoteDbModel.toEntity() = Note(
    id = id,
    title = title,
    content = content,
    updatedAt = updatedAt,
    isPinned = isPinned
)

fun Note.toDbModel() = NoteDbModel(
    id = id,
    title = title,
    content = content,
    updatedAt = updatedAt,
    isPinned = isPinned
)

fun List<NoteDbModel>.toEntities(): List<Note> {
    return map {
        it.toEntity()
    }
}

