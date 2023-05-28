package com.app.appsinrek.player_view.utils;


import com.app.appsinrek.player_view.enums.PostType;

public class PostModel {
    PostType type;
    String url;
    boolean mute = false;


    public PostModel(PostType type, String url) {
        this.type = type;
        this.url = url;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

    public PostType getType() {
        return type;
    }

    public String getUrl() {
        return url;
    }

}
