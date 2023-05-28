
package com.app.appsinrek.main.savepost.models;

import com.app.appsinrek.models.User;
import com.app.appsinrek.models.post.Post;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Bookmark {

    @SerializedName("PostAction")
    @Expose
    private PostAction postAction;
    @SerializedName("Post")
    @Expose
    private Post post;
    @SerializedName("User")
    @Expose
    private User user;
    @SerializedName("shared")
    @Expose
    private Object shared = null;

    public PostAction getPostAction() {
        return postAction;
    }

    public void setPostAction(PostAction postAction) {
        this.postAction = postAction;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Object getShared() {
        return shared;
    }

    public void setShared(Object shared) {
        this.shared = shared;
    }

}
