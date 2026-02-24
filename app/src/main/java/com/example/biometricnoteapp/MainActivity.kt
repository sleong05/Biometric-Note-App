package com.example.biometricnoteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.biometricnoteapp.views.NoteDetailPage
import com.example.biometricnoteapp.views.NotesPage
import com.example.biometricnoteapp.data.sampleNotes
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            // NEED A READ ALL NOTES THING HERE
            NavHost(navController, startDestination = "notes") {
                composable("notes") {
                    NotesPage(
                        notes = sampleNotes,
                        onNoteClick = { noteId ->
                            navController.navigate("detail/$noteId")
                        }
                    )
                }
                composable("detail/{noteId}") { backStackEntry ->
                    val noteId = backStackEntry.arguments?.getString("noteId") ?: "";

                    NoteDetailPage(
                        noteId = noteId,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}