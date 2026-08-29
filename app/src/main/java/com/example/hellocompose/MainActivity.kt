import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
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
                            delay(500) // Simulasi waktu proses unduh
                            progress = i * 10
                        }
                        status = "Selesai"
                    }
                },
                enabled = downloadJob?.isActive != true
            ) {
                Text("Start")
            }

            Button(
                onClick = {
                    downloadJob?.cancel()
                    status = "Dibatalkan"
                },
                enabled = downloadJob?.isActive == true
            ) {
                Text("Cancel")
            }
        }
    }
}