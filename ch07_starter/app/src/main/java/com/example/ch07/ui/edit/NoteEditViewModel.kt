package com.example.ch07.ui.edit

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch07.data.NoteEntity
import com.example.ch07.data.NoteRepository
import com.example.ch07.util.formatTags
import com.example.ch07.util.parseTags
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class NoteEditViewModel(
    private val repository: NoteRepository,
    noteId: Long? = null,
    private val clock: () -> Long = System::currentTimeMillis
) : ViewModel() {
    private val editingId = noteId?.takeIf { it != 0L }
    private var original: NoteEntity? = null
    private val state = MutableStateFlow(
        NoteEditUiState(isEditing = editingId != null, isLoading = editingId != null)
    )
    val uiState = state.asStateFlow()

    init {
        if (editingId != null) {
            viewModelScope.launch {
                try {
                    val note = repository.getNote(editingId)
                    original = note
                    state.update {
                        if (note == null) it.copy(
                            isLoading = false, canSave = false, error = "Catatan tidak ditemukan"
                        ) else it.copy(
                            title = note.title, content = note.content,
                            tagsText = formatTags(note.tags), isLoading = false
                        )
                    }
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    state.update { it.copy(isLoading = false, canSave = false, error = "Gagal membaca catatan") }
                }
            }
        }
    }

    private fun edit(transform: (NoteEditUiState) -> NoteEditUiState) {
        state.update {
            if (it.isLoading || it.isSaving || it.saved || !it.canSave) it
            else transform(it).copy(error = null)
        }
    }

    fun onTitleChange(value: String) = edit { it.copy(title = value) }
    fun onContentChange(value: String) = edit { it.copy(content = value) }
    fun onTagsChange(value: String) = edit { it.copy(tagsText = value) }

    fun saveNote() {
        val input = state.value
        if (input.isLoading || input.isSaving || input.saved || !input.canSave) return
        if (input.title.isBlank()) {
            state.update { it.copy(error = "Judul tidak boleh kosong") }
            return
        }
        state.update { it.copy(isSaving = true, error = null) }
        viewModelScope.launch {
            try {
                val now = clock()
                val note = (original ?: NoteEntity(createdAt = now)).copy(
                    title = input.title.trim(), content = input.content.trim(),
                    tags = parseTags(input.tagsText), updatedAt = now
                )
                if (editingId == null) repository.insert(note) else repository.update(note)
                state.update { it.copy(isSaving = false, saved = true) }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                state.update { it.copy(isSaving = false, error = "Gagal menyimpan catatan. Silakan coba lagi.") }
            }
        }
    }
}
