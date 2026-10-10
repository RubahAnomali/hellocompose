package com.example.ch07.testutil

import com.example.ch07.data.NoteEntity
import com.example.ch07.data.NoteRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update

class FakeNoteRepository(
    initial: List<NoteEntity> = emptyList()
) : NoteRepository {

    private val notes = MutableStateFlow(initial)
    private var nextId = (initial.maxOfOrNull { it.id } ?: 0L) + 1L

    val current: List<NoteEntity>
        get() = notes.value

    private fun ordered(list: List<NoteEntity>): List<NoteEntity> =
        list.sortedWith(
            compareByDescending<NoteEntity> { it.isPinned }
                .thenByDescending { it.createdAt }
                .thenByDescending { it.id }
        )

    override fun observeNotes(): Flow<List<NoteEntity>> =
        notes.map { ordered(it) }

    override suspend fun getNote(id: Long): NoteEntity? =
        notes.value.firstOrNull { it.id == id }

    override suspend fun insert(note: NoteEntity): Long {
        val id = if (note.id == 0L) nextId++ else note.id

        check(notes.value.none { it.id == id }) {
            "Catatan dengan ID $id sudah ada"
        }

        nextId = maxOf(nextId, id + 1L)

        notes.update { list ->
            list + note.copy(id = id)
        }

        return id
    }

    override suspend fun update(note: NoteEntity) {
        notes.update { list ->
            list.map {
                if (it.id == note.id) note else it
            }
        }
    }

    override suspend fun delete(note: NoteEntity) {
        notes.update { list ->
            list.filterNot { it.id == note.id }
        }
    }

    override suspend fun togglePin(id: Long) {
        notes.update { list ->
            list.map {
                if (it.id == id) {
                    it.copy(isPinned = !it.isPinned)
                } else {
                    it
                }
            }
        }
    }
}