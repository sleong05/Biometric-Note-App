package com.example.biometricnoteapp

import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.biometricnoteapp.views.NoteDetailPage
import com.example.biometricnoteapp.views.NotesPage
class MainActivity : ComponentActivity() {

    private val loginLauncher = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == RESULT_OK) {
            loadAppUI()
        } else {
            finish() // Auth failed or cancelled — close the app
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val intent = Intent(this, LoginActivity::class.java)
        loginLauncher.launch(intent)
    }
    private fun loadAppUI() {
        setContent {
            val navController = rememberNavController()

            // NEED A READ ALL NOTES THING HERE
            NavHost(navController, startDestination = "notes") {
                composable("notes") {
                    NotesPage(
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