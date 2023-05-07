package com.example.facadeservice;

import java.util.UUID;

public class Message {
    private UUID id;
    private String txt;

    public UUID getId() {
        return id;
    }

    public String getTxt() {
        return txt;
    }

    public Message(UUID id, String txt) {
        this.id = id;
        this.txt = txt;
    }
}
