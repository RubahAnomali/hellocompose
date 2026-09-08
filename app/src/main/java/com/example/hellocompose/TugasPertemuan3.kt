package com.example.hellocompose

import android.content.res.Configuration
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.hellocompose.ui.theme.HelloComposeTheme

data class Student(val nim: String, val nama: String, val ipk: Double)

@Composable
fun ProfileScreen3(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.fillMaxWidth().padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Box(
            modifier = Modifier.size(100.dp).clip(CircleShape).background(MaterialTheme.colorScheme.primary),
            contentAlignment = Alignment.Center
        ) {
            Text(text = "FF", style = MaterialTheme.typography.displayMedium, color = MaterialTheme.colorScheme.onPrimary)
        }
        Spacer(modifier = Modifier.height(16.dp))
        Text(text = "Farrell Fyelo Hollans Tamaela", style = MaterialTheme.typography.titleLarge)
        Text(text = "NIM: 22001", style = MaterialTheme.typography.bodyLarge)
        Text(text = "Prodi: Informatika", style = MaterialTheme.typography.bodyMedium)
    }
}

@Composable
fun StudentListScreen(modifier: Modifier = Modifier) {
    val students = remember {
        listOf(
            Student("22001", "Farrell Fyelo", 3.8),
            Student("22002", "Andi", 3.4),
            Student("22003", "Budi", 3.9),
            Student("22004", "Caca", 2.8),
            Student("22005", "Deni", 3.2),
            Student("22006", "Eka", 3.6),
            Student("22007", "Fina", 2.5),
            Student("22008", "Gani", 3.1),
            Student("22009", "Hani", 3.7),
            Student("22010", "Indra", 2.9),
            Student("22011", "Joko", 3.5)
        )
    }

    var selectedFilter by remember { mutableStateOf("Semua") }
    val filters = listOf("Semua", "Cumlaude (>=3.5)", "Baik", "Kurang (<3.0)")

    val filteredStudents = when (selectedFilter) {
        "Cumlaude (>=3.5)" -> students.filter { it.ipk >= 3.5 }
        "Baik" -> students.filter { it.ipk in 3.0..3.49 }
        "Kurang (<3.0)" -> students.filter { it.ipk < 3.0 }
        else -> students
    }

    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        item {
            Column {
                Text(text = "Daftar Mahasiswa", style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
                Spacer(modifier = Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    items(filters) { filter ->
                        FilterChip(
                            selected = selectedFilter == filter,
                            onClick = { selectedFilter = filter },
                            label = { Text(filter) }
                        )
                    }
                }
                HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
            }
        }

        items(
            items = filteredStudents,
            key = { student -> student.nim }
        ) { student ->
            StudentCard(student)
        }

        item {
            Box(modifier = Modifier.fillMaxWidth().padding(vertical = 16.dp), contentAlignment = Alignment.Center) {
                Text(text = "Total Data: ${filteredStudents.size} Mahasiswa", style = MaterialTheme.typography.labelLarge, color = MaterialTheme.colorScheme.secondary)
            }
        }
    }
}

@Composable
fun StudentCard(student: Student) {
    val gpaColor = when {
        student.ipk >= 3.5 -> Color(0xFF4CAF50)
        student.ipk >= 3.0 -> Color(0xFFFFA000)
        else -> Color(0xFFE53935)
    }

    Card(modifier = Modifier.fillMaxWidth(), elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(text = student.nama, fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                Text(text = "NIM: ${student.nim}", style = MaterialTheme.typography.bodyMedium)
            }
            Surface(color = gpaColor.copy(alpha = 0.2f), shape = RoundedCornerShape(8.dp)) {
                Text(text = "IPK: ${student.ipk}", color = gpaColor, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
            }
        }
    }
}

@Preview(name = "List Light", showBackground = true)
@Preview(name = "List Dark", uiMode = Configuration.UI_MODE_NIGHT_YES, showBackground = true)
@Composable
fun StudentListScreenPreview() {
    HelloComposeTheme {
        Surface(color = MaterialTheme.colorScheme.background) { StudentListScreen() }
    }
}