package com.example.biometricnoteapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.navigation.compose.rememberNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.biometricnoteapp.views.NoteDetailPage
import java.net.URLEncoder
import java.net.URLDecoder
import com.example.biometricnoteapp.views.NotesPage
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val navController = rememberNavController()

            NavHost(navController, startDestination = "notes") {
                composable("notes") {
                    NotesPage(
                        onNoteClick = { title, body ->
                            val encodedTitle = URLEncoder.encode(title, "UTF-8")
                            val encodedBody = URLEncoder.encode(body, "UTF-8")
                            navController.navigate("detail/$encodedTitle/$encodedBody")
                        }
                    )
                }
                composable("detail/{title}/{body}") { backStackEntry ->
                    val title = URLDecoder.decode(backStackEntry.arguments?.getString("title") ?: "", "UTF-8")
                    val body = URLDecoder.decode(backStackEntry.arguments?.getString("body") ?: "", "UTF-8")
                    NoteDetailPage(
                        title = title,
                        body = body,
                        onBack = { navController.popBackStack() }
                    )
                }
            }
        }
    }
}