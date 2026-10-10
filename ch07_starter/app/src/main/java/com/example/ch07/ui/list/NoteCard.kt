package com.example.ch07.ui.list

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.example.ch07.data.NoteEntity
import com.example.ch07.util.formatRelativeTime

@Composable
fun NoteCard(note: NoteEntity, now: Long, onEdit: () -> Unit,
             onTogglePin: () -> Unit, onDelete: () -> Unit) {
    Card(modifier = Modifier.fillMaxWidth()) {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            if (note.isPinned) Text("DISEMATKAN", style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary)
            Text(note.title, style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold, maxLines = 2, overflow = TextOverflow.Ellipsis)
            if (note.content.isNotBlank()) Text(note.content, maxLines = 4, overflow = TextOverflow.Ellipsis)
            if (note.tags.isNotEmpty()) Text(note.tags.joinToString("  ") { "#$it" },
                color = MaterialTheme.colorScheme.primary, style = MaterialTheme.typography.bodySmall)
            Text("Diubah ${formatRelativeTime(now, note.updatedAt)}",
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant)
            Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                TextButton(onClick = onEdit) { Text("Edit") }
                TextButton(onClick = onTogglePin) { Text(if (note.isPinned) "Lepas pin" else "Pin") }
                TextButton(onClick = onDelete) { Text("Hapus", color = MaterialTheme.colorScheme.error) }
            }
        }
    }
}
