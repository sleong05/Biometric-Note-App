package com.example.biometricnoteapp.services;

import java.io.Serializable;
public class Note implements Serializable {
    private static final long serialVersionUID = 1L;
    private final String title;
    private final String content;
    private final String id;

    public Note(String title, String content, String id) {
        this.title = title;
        this.content = content;
        this.id = id;
    }

    public String getTitle() { return title; }
    public String getContent() { return content; }
    public String getFilename() { return id; }

}