package com.example.biometricnoteapp.services;

import com.example.biometricnoteapp.models.Note;
import android.content.Context;

import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class NoteAccess {

    private static EncryptionManager encryptionManager;
    private static EncryptionManager getEncryptionManager() throws Exception {
        if (encryptionManager == null) encryptionManager = new EncryptionManager();
        return encryptionManager;
    }
    private static Path getPath(Context context) {
        return new File(context.getFilesDir(), "data").toPath();
    }


    public static Note createNote(Context context) throws Exception {
        Path path = getPath(context);
        Files.createDirectories(path);

        String title = "Untitled";
        String content = "";
        String id = UUID.randomUUID().toString();

        Note note = new Note(title, content, id);

        writeNote(context, note);
        return note;
    }

    public static Note readNote(Context context, String id) throws Exception {
        Path path = getPath(context);
        Path filePath = path.resolve(id);

        byte[] encrypted = Files.readAllBytes(filePath);
        byte[] decrypted = getEncryptionManager().decrypt(encrypted);

        ByteArrayInputStream bis = new ByteArrayInputStream(decrypted);
        ObjectInputStream ois = new ObjectInputStream(bis);
        return (Note) ois.readObject();

    }

    public static List<Note> readAllNotes(Context context) throws Exception {
        Path path = getPath(context);
        Files.createDirectories(path);
        List<Note> notes = new ArrayList<>();

        DirectoryStream<Path> stream = Files.newDirectoryStream(path);

        for (Path filePath : stream) {
            if (Files.isRegularFile(filePath)) {
                String id = filePath.getFileName().toString();
                notes.add(readNote(context, id));
            }
        }


        return notes;
    }

    public static void writeNote(Context context, Note note) throws Exception {
        Path path = getPath(context);
        Path filePath = path.resolve(note.getId());

        ByteArrayOutputStream bos = new ByteArrayOutputStream(); // stores bytes
        ObjectOutputStream oos = new ObjectOutputStream(bos); // writes to bos after serializing the object

        oos.writeObject(note);
        byte[] bytes = bos.toByteArray();

        byte[] encrypted = getEncryptionManager().encrypt(bytes);
        Files.write(filePath, encrypted);
    }

    public static void deleteNote(Context context, String id) throws IOException {
        Path path = getPath(context);
        Path filePath = path.resolve(id);

        Files.deleteIfExists(filePath);

    }
}