package com.example.ch07.data

import kotlinx.coroutines.flow.Flow

interface NoteRepository {
    fun observeNotes(): Flow<List<NoteEntity>>
    suspend fun getNote(id: Long): NoteEntity?
    suspend fun insert(note: NoteEntity): Long
    suspend fun update(note: NoteEntity)
    suspend fun delete(note: NoteEntity)
    suspend fun togglePin(id: Long)
}

class RoomNoteRepository(private val dao: NoteDao) : NoteRepository {
    override fun observeNotes() = dao.observeNotes()
    override suspend fun getNote(id: Long) = dao.getNote(id)
    override suspend fun insert(note: NoteEntity) = dao.insert(note)
    override suspend fun update(note: NoteEntity) = dao.update(note)
    override suspend fun delete(note: NoteEntity) = dao.delete(note)
    override suspend fun togglePin(id: Long) = dao.togglePin(id)
}
