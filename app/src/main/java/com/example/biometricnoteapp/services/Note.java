package com.example.biometricnoteapp.services;

import java.io.Serializable;
public class Note implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String title;
    private final String content;
    private final String filename;

    public Note(String title, String content, String filename) {
        this.title = title;
        this.content = content;
        this.filename = filename;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getFilename() { return filename; }

}