package com.example.biometricnoteapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biometricnoteapp.models.KotlinNote
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalContext
import com.example.biometricnoteapp.services.NoteAccess
import com.example.biometricnoteapp.models.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.IOException

@Composable
fun NoteDetailPage(noteId: String, onBack: () -> Unit) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current

    if (noteId == "") {Text("Note note found"); return;}

    // these are kinda like react useRef hook
    var editedTitle by remember { mutableStateOf("") }
    var editedText by remember { mutableStateOf("") }
    var isEditing by remember { mutableStateOf(false) }
    var isLoaded by remember { mutableStateOf(true) }
    LaunchedEffect(noteId) {
        try {
            val loaded = withContext(Dispatchers.IO) {
                NoteAccess.readNote(context, noteId)
            }

            editedTitle = loaded.title
            editedText = loaded.content
            isLoaded = true
        } catch (e: IOException) {
            e.printStackTrace()
        }
    }


    if (!isLoaded) {
        Text("Loading...")
        return
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            TextButton(onClick = onBack) { Text("← Back") }
            TextButton(onClick = {
                if (isEditing) {
                    scope.launch {
                        try {
                            val note = withContext(Dispatchers.IO) {
                                var note: Note = Note(editedTitle, editedText, noteId)
                                NoteAccess.writeNote(context, note)
                            }
                            // update UI here
                        } catch (e: IOException) {
                            e.printStackTrace()
                        }
                    }
                }
                isEditing = !isEditing
            }) {
                Text(if (isEditing) "Save" else "Edit")
            }

            TextButton(
                onClick = {
                    // DELETE STUFFF
                },
                colors = ButtonDefaults.textButtonColors(
                    contentColor = MaterialTheme.colorScheme.error
                )
            ) {
                Text("Delete")
            }
        }

        Spacer(modifier = Modifier.height(16.dp))
        if (isEditing) {
            OutlinedTextField(
                value = editedTitle,
                onValueChange = { editedTitle = it },
                label = { Text("Title") },
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(12.dp))
            OutlinedTextField(
                value = editedText,
                onValueChange = { editedText = it },
                label = { Text("Content") },
                modifier = Modifier.fillMaxWidth().heightIn(min = 150.dp)
            )
        } else {
            Text(text = editedTitle, fontSize = 28.sp, fontWeight = FontWeight.Bold)
            Spacer(modifier = Modifier.height(12.dp))
            Text(text = editedText, fontSize = 16.sp)
        }
    }
}