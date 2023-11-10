package com.example.challange2.note;

import androidx.annotation.NonNull;

import java.io.Serializable;
import java.util.Date;

public class Note implements Serializable {
    private String title;
    private String content;
    private String Id;
    private Date date;

    public Note() {
    }

    public Note(String title, String content) {
        this.title = title;

        this.content = content;
    }

    public Note(String title, String content, String id) {
        this.title = title;
        this.content = content;
        Id = id;
    }

    public Note(String title, String content, String id, Date date) {
        this.title = title;
        this.content = content;
        Id = id;
        this.date = date;
    }

    public String getId() {
        return Id;
    }

    public Date getDate() {
        return date;
    }

    public void setId(String id) {
        Id = id;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public String getTitle() {
        return title;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    @NonNull
    @Override
    public String toString() {
        return "Title: " + title + " content: " + content;
    }
}

