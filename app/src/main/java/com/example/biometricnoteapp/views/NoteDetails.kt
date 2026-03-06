package com.example.biometricnoteapp.views

import android.content.Context
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
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
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.FileProvider
import com.example.biometricnoteapp.services.NoteAccess
import com.example.biometricnoteapp.models.Note
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
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
    var imageBytes by remember { mutableStateOf<ByteArray?>(null) }

    val photoUri = remember {
        val photoDir = File(context.cacheDir, "photos").apply { mkdirs() }
        val file = File.createTempFile("note_photo", ".jpg", photoDir)
        FileProvider.getUriForFile(context, "${context.packageName}.fileprovider", file)
    }

    val cameraLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.TakePicture()
    ) { success ->
        if (success) {
            val bytes = context.contentResolver.openInputStream(photoUri)?.readBytes()
            imageBytes = bytes
            // call a save
            scope.launch {
                saveNote(editedTitle, editedText, noteId, imageBytes, context)
            }

            // make sure the files dont sit temporarily in the storage
            val file = File(File(context.cacheDir, "photos"), photoUri.lastPathSegment ?: "")
            file.delete()
        }
    }

    val permissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestPermission()
    ) { granted ->
        if (granted) {
            cameraLauncher.launch(photoUri)
        }
    }

    // load notes from storage on launch
    LaunchedEffect(noteId) {
        try {
            val loaded = withContext(Dispatchers.IO) {
                NoteAccess.readNote(context, noteId)
            }

            editedTitle = loaded.title
            editedText = loaded.content
            imageBytes = loaded.image
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
                        saveNote(editedTitle, editedText, noteId, imageBytes, context)
                    }
                }
                isEditing = !isEditing
            }) {
                Text(if (isEditing) "Save" else "Edit")
            }

            TextButton(
                onClick = {
                    NoteAccess.deleteNote(context, noteId)
                    onBack();
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

            Spacer(modifier = Modifier.height(16.dp))

            imageBytes?.let { bytes ->
                val bitmap = android.graphics.BitmapFactory.decodeByteArray(bytes, 0, bytes.size)
                Box(
                    modifier = Modifier.fillMaxWidth(),
                    contentAlignment = androidx.compose.ui.Alignment.Center
                ) {
                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "Note photo",
                        modifier = Modifier.heightIn(max = 200.dp)
                    )
                }
            }

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.Center
            ) {
                Button(onClick = {
                    permissionLauncher.launch(android.Manifest.permission.CAMERA)
                }) {
                    Text(if (imageBytes == null) "Take Photo" else "Change Photo")
                }
            }
        }
    }
}

private suspend fun saveNote(
    editedTitle: String,
    editedText: String,
    noteId: String,
    imageBytes: ByteArray?,
    context: Context
) {
    try {
        withContext(Dispatchers.IO) {
            var note: Note = Note(editedTitle, editedText, noteId, imageBytes)
            NoteAccess.writeNote(context, note)
        }
    } catch (e: IOException) {
        e.printStackTrace()
    }
}