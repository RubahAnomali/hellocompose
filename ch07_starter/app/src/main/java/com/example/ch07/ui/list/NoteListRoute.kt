package com.example.ch07.ui.list

import androidx.compose.runtime.*
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import kotlinx.coroutines.delay

@Composable
fun NoteListRoute(viewModel: NoteListViewModel, onAdd: () -> Unit, onEdit: (Long) -> Unit) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val owner = LocalLifecycleOwner.current
    var now by remember { mutableLongStateOf(System.currentTimeMillis()) }
    LaunchedEffect(owner) {
        owner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            while (true) {
                now = System.currentTimeMillis()
                delay(30_000L)
            }
        }
    }
    NoteListScreen(state, now, viewModel::onQueryChange, viewModel::onTagSelected,
        onAdd, onEdit, viewModel::deleteNote, viewModel::togglePin,
        { id -> viewModel.undoDelete(id) }, { id -> viewModel.onUndoDismissed(id) }, viewModel::clearError)
}
