package com.example.ch07.ui.edit

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NoteEditScreen(uiState: NoteEditUiState, onTitleChange: (String) -> Unit,
    onContentChange: (String) -> Unit, onTagsChange: (String) -> Unit,
    onSave: () -> Unit, onBack: () -> Unit) {
    val editable = !uiState.isLoading && !uiState.isSaving && uiState.canSave
    Scaffold(topBar = {
        TopAppBar(title = { Text(if (uiState.isEditing) "Edit catatan" else "Catatan baru") },
            navigationIcon = { TextButton(onClick = onBack, enabled = !uiState.isSaving) { Text("Kembali") } })
    }) { padding ->
        if (uiState.isLoading) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) { CircularProgressIndicator() }
        } else {
            Column(Modifier.fillMaxSize().padding(padding).imePadding()
                .verticalScroll(rememberScrollState()).padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)) {
                OutlinedTextField(value = uiState.title, onValueChange = onTitleChange,
                    label = { Text("Judul") }, singleLine = true, enabled = editable,
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = uiState.content, onValueChange = onContentChange,
                    label = { Text("Isi catatan") }, minLines = 6, enabled = editable,
                    modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = uiState.tagsText, onValueChange = onTagsChange,
                    label = { Text("Tag") }, placeholder = { Text("Kuliah, #Ide") },
                    supportingText = { Text("Pisahkan tag dengan koma. Duplikat dihapus otomatis.") },
                    enabled = editable, modifier = Modifier.fillMaxWidth())
                uiState.error?.let { Text(it, color = MaterialTheme.colorScheme.error) }
                Button(onClick = onSave, enabled = editable && !uiState.saved, modifier = Modifier.fillMaxWidth()) {
                    Text(if (uiState.isSaving) "Menyimpan…" else "Simpan catatan")
                }
            }
        }
    }
}
