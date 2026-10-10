package com.example.ch07.ui.list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ch07.data.NoteEntity
import com.example.ch07.data.NoteRepository
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock

class NoteListViewModel(
    private val repository: NoteRepository
) : ViewModel() {

    private val allNotes =
        MutableStateFlow<List<NoteEntity>>(emptyList())

    private val query = MutableStateFlow("")

    private val recentlyDeleted =
        MutableStateFlow<NoteEntity?>(null)

    private val selectedTag =
        MutableStateFlow<String?>(null)

    private val loading = MutableStateFlow(true)

    private val error = MutableStateFlow<String?>(null)

    private val mutationMutex = Mutex()

    // Pencarian menunggu 300 ms setelah input terakhir.
    // Mengosongkan pencarian langsung menampilkan semua catatan.
    @OptIn(FlowPreview::class)
    private val searchQuery = query.debounce { text ->
        if (text.isBlank()) 0L else 300L
    }

    private val filtered = combine(
        allNotes,
        query,
        searchQuery,
        recentlyDeleted,
        selectedTag
    ) { notes, input, appliedQuery, deleted, selected ->

        val tags = notes
            .flatMap { it.tags }
            .distinct()
            .sorted()

        val activeTag = selected?.takeIf { it in tags }

        val keyword = appliedQuery.trim()

        val visibleNotes = notes.filter { note ->
            val matchesSearch =
                keyword.isEmpty() ||
                        note.title.contains(
                            keyword,
                            ignoreCase = true
                        ) ||
                        note.content.contains(
                            keyword,
                            ignoreCase = true
                        )

            val matchesTag =
                activeTag == null || activeTag in note.tags

            matchesSearch && matchesTag
        }

        NoteListUiState(
            notes = visibleNotes,
            query = input,
            recentlyDeleted = deleted,
            availableTags = tags,
            selectedTag = activeTag,
            isFiltering = input.isNotBlank() || activeTag != null
        )
    }

    val uiState: StateFlow<NoteListUiState> = combine(
        filtered,
        loading,
        error
    ) { state, isLoading, message ->
        state.copy(
            isLoading = isLoading,
            error = message
        )
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.Eagerly,
        initialValue = NoteListUiState()
    )

    init {
        viewModelScope.launch {
            try {
                repository.observeNotes().collect { notes ->
                    allNotes.value = notes

                    val currentTag = selectedTag.value

                    // Lepaskan filter jika tag tidak dipakai lagi.
                    if (
                        currentTag != null &&
                        notes.none { currentTag in it.tags }
                    ) {
                        selectedTag.value = null
                    }

                    loading.value = false
                }
            } catch (e: CancellationException) {
                throw e
            } catch (e: Exception) {
                loading.value = false
                error.value =
                    "Gagal membaca catatan. Tutup dan buka kembali aplikasi."
            }
        }
    }

    fun onQueryChange(value: String) {
        query.value = value
    }

    fun onTagSelected(tag: String?) {
        if (
            tag != null &&
            allNotes.value.none { tag in it.tags }
        ) {
            return
        }

        selectedTag.value =
            if (selectedTag.value == tag) null else tag
    }

    fun deleteNote(note: NoteEntity) {
        mutate {
            // Gunakan data terbaru, termasuk pin dan tag.
            val latest = repository.getNote(note.id)
                ?: return@mutate

            repository.delete(latest)

            // Salinan baru disimpan setelah penghapusan berhasil.
            recentlyDeleted.value = latest
        }
    }

    fun undoDelete(
        expectedId: Long? = recentlyDeleted.value?.id
    ) {
        mutate {
            val note = recentlyDeleted.value
                ?: return@mutate

            // Callback Snackbar lama tidak boleh mengubah
            // penghapusan catatan yang lebih baru.
            if (note.id != expectedId) {
                return@mutate
            }

            // Kembalikan objek asli dengan ID dan data yang sama.
            repository.insert(note)

            recentlyDeleted.value = null
        }
    }

    fun onUndoDismissed(
        expectedId: Long? = recentlyDeleted.value?.id
    ) {
        if (recentlyDeleted.value?.id == expectedId) {
            recentlyDeleted.value = null
        }
    }

    fun togglePin(note: NoteEntity) {
        mutate {
            repository.togglePin(note.id)
        }
    }

    fun clearError() {
        error.value = null
    }

    private fun mutate(block: suspend () -> Unit) {
        viewModelScope.launch {
            mutationMutex.withLock {
                try {
                    block()
                } catch (e: CancellationException) {
                    throw e
                } catch (e: Exception) {
                    error.value =
                        "Perubahan gagal disimpan. Silakan coba lagi."
                }
            }
        }
    }
}