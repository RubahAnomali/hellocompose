package com.example.hellocompose

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun DownloaderScreen() {
    val coroutineScope = rememberCoroutineScope()
    var downloadJob by remember { mutableStateOf<Job?>(null) }
    var status by remember { mutableStateOf("Siap") }
    var progress by remember { mutableIntStateOf(0) }

    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Status: $status")
        if (downloadJob?.isActive == true) {
            Text(text = "Progres: $progress%")
        }
        Spacer(modifier = Modifier.height(16.dp))
        Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Button(
                onClick = {
                    status = "Mengunduh..."
                    progress = 0
                    downloadJob = coroutineScope.launch {
                        for (i in 1..10) {
                            delay(500)
                            progress = i * 10
                        }
                        status = "Selesai"
                    }
                },
                enabled = downloadJob?.isActive != true
            ) { Text("Start") }

            Button(
                onClick = {
                    downloadJob?.cancel()
                    status = "Dibatalkan"
                },
                enabled = downloadJob?.isActive == true
            ) { Text("Cancel") }
        }
    }
}