package com.example.biometricnoteapp.services;

import java.io.*;
import java.nio.file.*;

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

    public void writeNote(Note note) throws IOException {
        Path filePath = path.resolve(note.getFilename());

        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            out.writeObject(note);
        }
    }
}