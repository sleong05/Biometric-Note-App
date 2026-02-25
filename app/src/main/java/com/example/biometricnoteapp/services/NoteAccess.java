package com.example.biometricnoteapp.services;

import android.content.Context;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NoteAccess {

    private static Path getPath(Context context) {
        return new File(context.getFilesDir(), "data").toPath();
    }


    public static Note createNote(Context context) throws IOException {
        Path path = getPath(context);
        Files.createDirectories(path);

        String title = "";
        String content = "";
        String id = UUID.randomUUID().toString();

        Note note = new Note(content, content, id);

        writeNote(context, note);
        return note;
    }

    public static Note readNote(Context context, UUID id) throws IOException {
        Path path = getPath(context);
        Path filePath = path.resolve(id.toString());

        try (ObjectInputStream in = new ObjectInputStream(Files.newInputStream(filePath))) {
            return (Note) in.readObject();
        }
        catch (ClassNotFoundException e) {
            throw new IOException("Note class not found", e);
        }
    }

    public List<Note> readAllNotes(Context context) throws IOException {
        Path path = getPath(context);
        Files.createDirectories(path);
        List<Note> notes = new ArrayList<>();

        try (DirectoryStream<Path> stream = Files.newDirectoryStream(path)) {
            for (Path filePath : stream) {
                if (Files.isRegularFile(filePath)) {
                    UUID id = UUID.fromString(filePath.getFileName().toString());
                    notes.add(readNote(context, id));
                }
            }
        }

        return notes;
    }

    public static void writeNote(Context context, Note note) throws IOException {
        Path path = getPath(context);
        Path filePath = path.resolve(note.getFilename());

        try (ObjectOutputStream out = new ObjectOutputStream(Files.newOutputStream(filePath))) {
            out.writeObject(note);
        }
    }
}