
package com.app.appsinrek.main.comment.model;

import android.os.Parcelable;

import com.app.appsinrek.models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ChildPostComment implements Parcelable {
    @SerializedName("PostComment")
    @Expose
    private PostComment postComment;
    @SerializedName("User")
    @Expose
    private User user;

    public final static Creator<ChildPostComment> CREATOR = new Creator<ChildPostComment>() {
        public ChildPostComment createFromParcel(android.os.Parcel in) {
            return new ChildPostComment(in);
        }

        public ChildPostComment[] newArray(int size) {
            return (new ChildPostComment[size]);
        }

    };

    protected ChildPostComment(android.os.Parcel in) {
        this.postComment = ((PostComment) in.readValue((PostComment.class.getClassLoader())));
        this.user = ((User) in.readValue((User.class.getClassLoader())));
    }

    public ChildPostComment() {
    }

    public PostComment getPostComment() {
        return postComment;
    }

    public void setPostComment(PostComment postComment) {
        this.postComment = postComment;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(postComment);
        dest.writeValue(user);
    }

    public int describeContents() {
        return 0;
    }

}
