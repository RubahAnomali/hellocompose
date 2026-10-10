package com.example.ch07.ui.list

import com.example.ch07.data.NoteEntity

data class NoteListUiState(
    val notes: List<NoteEntity> = emptyList(),
    val query: String = "",
    val recentlyDeleted: NoteEntity? = null,
    val availableTags: List<String> = emptyList(),
    val selectedTag: String? = null,
    val isFiltering: Boolean = false,
    val isLoading: Boolean = true,
    val error: String? = null
)
