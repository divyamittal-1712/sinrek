
package com.app.appsinrek.main.post_detail.model;

import com.app.appsinrek.models.User;
import com.app.appsinrek.models.post.Post;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostDetailModel
{
    @SerializedName("Post")
    @Expose
    private Post post;

    @SerializedName("User")
    @Expose
    private User user;

    @SerializedName("shared")
    @Expose
    private Object shared;

    public User getUser() {
        return user;
    }
    public void setUser(User user) {
        this.user = user;
    }


    public Post getPost() {
        return post;
    }
    public void setPost(Post post) {
        this.post = post;
    }

    public Object getShared() {
        return shared;
    }
    public void setShared(Object shared) {
        this.shared = shared;
    }
}
