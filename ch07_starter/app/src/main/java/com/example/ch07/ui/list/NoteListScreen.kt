package com.example.ch07.ui.list

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.ch07.data.NoteEntity

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteListScreen(
    uiState: NoteListUiState,
    now: Long,
    onQueryChange: (String) -> Unit,
    onTagSelected: (String?) -> Unit,
    onAdd: () -> Unit,
    onEdit: (Long) -> Unit,
    onDelete: (NoteEntity) -> Unit,
    onTogglePin: (NoteEntity) -> Unit,
    onUndoDelete: (Long) -> Unit,
    onUndoDismissed: (Long) -> Unit,
    onClearError: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    LaunchedEffect(uiState.recentlyDeleted) {
        val deleted = uiState.recentlyDeleted ?: return@LaunchedEffect
        val result = snackbarHostState.showSnackbar(
            message = "Catatan dihapus", actionLabel = "Batalkan",
            withDismissAction = true, duration = SnackbarDuration.Short
        )
        if (result == SnackbarResult.ActionPerformed) onUndoDelete(deleted.id)
        else onUndoDismissed(deleted.id)
    }
    Scaffold(
        topBar = { TopAppBar(title = { Text("CatatanKu") }) },
        snackbarHost = { SnackbarHost(snackbarHostState) },
        floatingActionButton = { FloatingActionButton(onClick = onAdd) { Text("+ Catatan", Modifier.padding(12.dp)) } }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp)) {
            OutlinedTextField(value = uiState.query, onValueChange = onQueryChange,
                label = { Text("Cari judul atau isi") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 8.dp)) {
                item {
                    FilterChip(selected = uiState.selectedTag == null,
                        onClick = { onTagSelected(null) }, label = { Text("Semua") })
                }
                items(uiState.availableTags, key = { it }) { tag ->
                    FilterChip(selected = uiState.selectedTag == tag,
                        onClick = { onTagSelected(tag) }, label = { Text("#$tag") })
                }
            }
            uiState.error?.let { message ->
                Text(message, color = MaterialTheme.colorScheme.error)
                TextButton(onClick = onClearError) { Text("Tutup pesan") }
            }
            when {
                uiState.isLoading -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    CircularProgressIndicator()
                }
                uiState.notes.isEmpty() -> Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Text(when {
                        uiState.selectedTag != null -> "Tidak ada catatan yang cocok dengan tag #${uiState.selectedTag} dan pencarian."
                        uiState.isFiltering -> "Tidak ada catatan yang cocok dengan pencarian."
                        else -> "Belum ada catatan. Tekan + Catatan untuk mulai."
                    })
                }
                else -> LazyColumn(verticalArrangement = Arrangement.spacedBy(12.dp),
                    contentPadding = PaddingValues(bottom = 96.dp)) {
                    items(uiState.notes, key = { it.id }) { note ->
                        NoteCard(note, now, onEdit = { onEdit(note.id) },
                            onTogglePin = { onTogglePin(note) }, onDelete = { onDelete(note) })
                    }
                }
            }
        }
    }
}
