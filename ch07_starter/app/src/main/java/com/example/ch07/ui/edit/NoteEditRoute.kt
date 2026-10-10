package com.example.ch07.ui.edit

import androidx.activity.compose.BackHandler
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.ch07.MainApplication

@Composable
fun NoteEditRoute(
    noteId: Long,
    onNavigateBack: () -> Unit
) {
    val app = LocalContext.current.applicationContext as MainApplication

    val editViewModel: NoteEditViewModel = viewModel(
        key = "note-edit-$noteId",
        factory = viewModelFactory {
            initializer {
                NoteEditViewModel(
                    repository = app.container.noteRepository,
                    noteId = noteId.takeIf { it != 0L }
                )
            }
        }
    )

    val uiState by editViewModel.uiState.collectAsStateWithLifecycle()

    LaunchedEffect(uiState.saved) {
        if (uiState.saved) {
            onNavigateBack()
        }
    }

    BackHandler(enabled = uiState.isSaving) {
        // Tunggu proses penyimpanan selesai.
    }

    NoteEditScreen(
        uiState = uiState,
        onTitleChange = editViewModel::onTitleChange,
        onContentChange = editViewModel::onContentChange,
        onTagsChange = editViewModel::onTagsChange,
        onSave = editViewModel::saveNote,
        onBack = onNavigateBack
    )
}