package com.example.biometricnoteapp.views

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biometricnoteapp.models.KotlinNote
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import com.example.biometricnoteapp.data.sampleKotlinNotes
import com.example.biometricnoteapp.services.NoteAccess
import com.example.biometricnoteapp.models.Note

@Composable
fun NoteDetailPage(noteId: String, onBack: () -> Unit) {
    if (noteId == "") {Text("Note note found"); return;}
    // READ STUFF
    val kotlinNote : KotlinNote = sampleKotlinNotes.find { it?.id == noteId } ?: return

    // these are kinda like react useRef hook
    var editedTitle by remember { mutableStateOf(kotlinNote.title) }
    var editedText by remember { mutableStateOf(kotlinNote.text) }
    var isEditing by remember { mutableStateOf(false) }
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
                    val note = Note(editedTitle, editedText, noteId);
                    NoteAccess.writeNote(note);
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