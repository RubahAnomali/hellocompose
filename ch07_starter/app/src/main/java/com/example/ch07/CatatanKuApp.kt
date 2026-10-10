package com.example.ch07

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.ch07.ui.edit.NoteEditRoute
import com.example.ch07.ui.list.NoteListRoute
import com.example.ch07.ui.list.NoteListViewModel

@Composable
fun CatatanKuApp() {
    val navController = rememberNavController()
    val app = LocalContext.current.applicationContext as MainApplication

    NavHost(
        navController = navController,
        startDestination = "notes"
    ) {
        composable("notes") {
            val listViewModel: NoteListViewModel = viewModel(
                factory = viewModelFactory {
                    initializer {
                        NoteListViewModel(app.container.noteRepository)
                    }
                }
            )

            NoteListRoute(
                viewModel = listViewModel,
                onAdd = {
                    navController.navigate("edit/0")
                },
                onEdit = { id ->
                    navController.navigate("edit/$id")
                }
            )
        }

        composable(
            route = "edit/{noteId}",
            arguments = listOf(
                navArgument("noteId") {
                    type = NavType.LongType
                }
            )
        ) { entry ->
            val noteId = entry.arguments?.getLong("noteId") ?: 0L

            NoteEditRoute(
                noteId = noteId,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}