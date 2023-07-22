package com.example.triptrack;

public class Document {
    private String name;
    private String path;

    public Document(String name, String path) {
        this.name = name;
        this.path = path;
    }

    public String getName() {
        return name;
    }

    public String getPath() {
        return path;
    }
}

