package com.example.ch04starter

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp

@Composable
fun SuhuScreen() {
    var celsiusText by rememberSaveable { mutableStateOf("") }

    val celsiusValue by remember {
        derivedStateOf { celsiusText.toFloatOrNull() ?: 0f }
    }

    val fahrenheit by remember {
        derivedStateOf { (celsiusValue * 9/5) + 32 }
    }

    val kelvin by remember {
        derivedStateOf { celsiusValue + 273.15f }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = celsiusText,
            onValueChange = { celsiusText = it },
            label = { Text("Celsius") },
            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = if (celsiusText.isBlank()) "" else fahrenheit.toString(),
            onValueChange = { },
            label = { Text("Fahrenheit") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = if (celsiusText.isBlank()) "" else kelvin.toString(),
            onValueChange = { },
            label = { Text("Kelvin") },
            readOnly = true,
            modifier = Modifier.fillMaxWidth()
        )
    }
}