package com.example.ch04starter

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp

@Composable
fun RegistrasiScreen() {
    var nama by rememberSaveable { mutableStateOf("") }
    var email by rememberSaveable { mutableStateOf("") }
    var password by rememberSaveable { mutableStateOf("") }
    var konfirmasiPassword by rememberSaveable { mutableStateOf("") }

    val isFormValid by remember {
        derivedStateOf {
            nama.isNotBlank() &&
                    email.contains("@") &&
                    password.length >= 8 &&
                    konfirmasiPassword == password
        }
    }

    Column(
        modifier = Modifier.padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        OutlinedTextField(
            value = nama,
            onValueChange = { nama = it },
            label = { Text("Nama Lengkap") },
            isError = nama.isBlank(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            isError = !email.contains("@") && email.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = password,
            onValueChange = { password = it },
            label = { Text("Password (Min. 8 Karakter)") },
            visualTransformation = PasswordVisualTransformation(),
            isError = password.length < 8 && password.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )

        OutlinedTextField(
            value = konfirmasiPassword,
            onValueChange = { konfirmasiPassword = it },
            label = { Text("Konfirmasi Password") },
            visualTransformation = PasswordVisualTransformation(),
            isError = konfirmasiPassword != password && konfirmasiPassword.isNotBlank(),
            modifier = Modifier.fillMaxWidth()
        )

        Button(
            onClick = { /* Tindakan submit */ },
            enabled = isFormValid,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text("Daftar")
        }
    }
}