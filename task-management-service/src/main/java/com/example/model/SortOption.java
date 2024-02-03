package com.example.model;

public enum SortOption {
    IMPORTANCE("isImportant"),
    TITLE("title"),
    DUE_DATE("dueDate"),
    CREATION_DATE("creationDate");

    private final String value;
    SortOption(String value) {
        this.value = value;
    }

    public String getValue() {
        return value;
    }

}
