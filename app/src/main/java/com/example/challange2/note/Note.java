package com.example.challange2.note;

import java.util.Date;

public class Note {
    private String title;
    private String content;
    private String username;
    private Date date;

    public Note(String title, String content) {
        this.title = title;
        this.content = content;
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
}

