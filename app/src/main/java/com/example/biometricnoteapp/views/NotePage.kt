package com.example.biometricnoteapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biometricnoteapp.components.NoteCard
import com.example.biometricnoteapp.models.Note
import com.example.biometricnoteapp.services.NoteAccess
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
@Composable
fun NotesPage(onNoteClick: (String) -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    var notes by remember { mutableStateOf<List<Note>>(emptyList()) }

    // Load notes on first composition
    LaunchedEffect(Unit) {
        notes = withContext(Dispatchers.IO) {
            NoteAccess.readAllNotes(context)
        }
    }


    Scaffold(
        topBar = {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    "My Notes",
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.primary
                )
            }
        },
        floatingActionButton = {
            FloatingActionButton(onClick = {
                scope.launch {
                    try {
                        withContext(Dispatchers.IO) {
                            NoteAccess.createNote(context)
                        }

                        notes = withContext(Dispatchers.IO) {
                            NoteAccess.readAllNotes(context)
                        }
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                }
            }) {
                Icon(Icons.Default.Add, contentDescription = "Add Note")
            }
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            notes.forEach { note ->
                NoteCard(note.title, note.content) {
                    onNoteClick(note.id)
                }
            }
        }
    }
}