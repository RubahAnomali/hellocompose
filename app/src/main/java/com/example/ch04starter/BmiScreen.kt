package com.example.ch04starter

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun BmiScreen() {
    var berat by rememberSaveable { mutableFloatStateOf(60f) }
    var tinggi by rememberSaveable { mutableFloatStateOf(165f) }
    var isDihitung by rememberSaveable { mutableStateOf(false) }

    val snackbarHostState = remember { SnackbarHostState() }

    val bmi by remember {
        derivedStateOf { berat / ((tinggi / 100f) * (tinggi / 100f)) }
    }

    val kategori by remember {
        derivedStateOf {
            when {
                bmi < 18.5 -> "Kurus" to Color(0xFF2196F3)
                bmi < 25.0 -> "Normal" to Color(0xFF4CAF50)
                bmi < 30.0 -> "Gemuk" to Color(0xFFFF9800)
                else -> "Obesitas" to Color(0xFFF44336)
            }
        }
    }

    LaunchedEffect(isDihitung, kategori.first) {
        if (isDihitung) {
            snackbarHostState.showSnackbar("Kategori Anda: ${kategori.first}")
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(hostState = snackbarHostState) }
    ) { paddingValues ->
        Column(
            modifier = Modifier.padding(paddingValues).padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(text = "Berat: ${berat.toInt()} kg")
            Slider(value = berat, onValueChange = { berat = it; isDihitung = false }, valueRange = 30f..150f)

            Text(text = "Tinggi: ${tinggi.toInt()} cm")
            Slider(value = tinggi, onValueChange = { tinggi = it; isDihitung = false }, valueRange = 100f..220f)

            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                Button(onClick = { isDihitung = true }) {
                    Text("Hitung BMI")
                }

                OutlinedButton(
                    onClick = {
                        berat = 60f
                        tinggi = 165f
                        isDihitung = false
                    }
                ) {
                    Text("Reset")
                }
            }

            AnimatedVisibility(visible = isDihitung) {
                Column {
                    Text(
                        text = "BMI Anda: %.1f".format(bmi),
                        style = MaterialTheme.typography.headlineMedium,
                        color = kategori.second
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    InterpretasiBmiTable()
                }
            }
        }
    }
}

@Composable
fun InterpretasiBmiTable() {
    Column {
        Text("Tabel Interpretasi BMI:", fontWeight = FontWeight.Bold)
        Text("- Kurus: < 18.5")
        Text("- Normal: 18.5 - 24.9")
        Text("- Gemuk: 25.0 - 29.9")
        Text("- Obesitas: >= 30.0")
    }
}