package com.example.biometricnoteapp.views

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.biometricnoteapp.components.NoteCard

@Composable
fun NotesPage(onNoteClick: (String, String) -> Unit) {
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
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding),
            verticalArrangement = Arrangement.spacedBy(16.dp),
        ) {
            NoteCard("Grocery List", "Milk, eggs, bread, butter") {
                onNoteClick("Grocery List", "Milk, eggs, bread, butters")
            }
            NoteCard("Meeting Notes", "Discuss Q2 goals with team") {
                onNoteClick("Meeting Notes", "Discuss Q2 goals with team")
            }
            NoteCard("Ideas", "App for tracking daily habits") {
                onNoteClick("Ideas", "App for tracking daily habits")
            }
        }
    }
}