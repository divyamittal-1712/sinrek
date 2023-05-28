package com.app.appsinrek.player_view.enums;

public enum PostType {
    TEXT("text"),
    IMAGE("image"),
    VIDEO("video");

    private final String value;

    PostType(String i) {
        this.value = i;
    }

    public String getValue() {
        return value;
    }
}
