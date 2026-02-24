package com.example.biometricnoteapp.services;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;

public class NoteAccess {

    private final Path path;

    public NoteAccess() {
        this.path = Paths.get("app/data/notes");
    }

    public Note createNote(String title,
                           String content,
                           String filename) throws IOException {
        Note note = new Note(title, content, filename);

        Path filePath = path.resolve(filename);

        if (!Files.exists(filePath)) {
            Files.createFile(filePath);
        }
        writeNote(note);
        return note;
    }

    public Note readNote(String filename) throws IOException {
        Path filePath = path.resolve(filename);

        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(filePath))) {
            return (Note) in.readObject();
        }
        catch (ClassNotFoundException e) {
            throw new IOException("Note class not found", e);
        }
    }

    public List<Note> readAllNotes() throws IOException {
        List<Note> notes = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path filePath : stream) {
                if (Files.isRegularFile(filePath)) {
                    String filename = filePath.getFileName().toString();
                    notes.add(readNote(filename));
                }
            }
        }

        return notes;
    }

    public void writeNote(Note note) throws IOException {
        Path filePath = path.resolve(note.getFilename());

        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            out.writeObject(note);
        }
    }
}