package com.example.ch07.ui.edit

data class NoteEditUiState(
    val title: String = "",
    val content: String = "",
    val tagsText: String = "",
    val isEditing: Boolean = false,
    val isLoading: Boolean = false,
    val isSaving: Boolean = false,
    val saved: Boolean = false,
    val canSave: Boolean = true,
    val error: String? = null
)
