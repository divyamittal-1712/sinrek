
package com.app.appsinrek.main.comment.model;

import android.os.Parcelable;
import com.app.appsinrek.models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import java.util.List;

public class CommentModel implements Parcelable
{
    @SerializedName("PostComment")
    @Expose
    private PostComment postComment;

    @SerializedName("User")
    @Expose
    private User user;

    @SerializedName("ChildPostComment")
    @Expose
    public List<ChildPostComment> childPostComment = null;

    public final static Creator<CommentModel> CREATOR = new Creator<CommentModel>() {
        public CommentModel createFromParcel(android.os.Parcel in) {
            return new CommentModel(in);
        }
        public CommentModel[] newArray(int size) {
            return (new CommentModel[size]);
        }
    };

    protected CommentModel(android.os.Parcel in) {
        this.postComment = ((PostComment) in.readValue((PostComment.class.getClassLoader())));
        this.user = ((User) in.readValue((User.class.getClassLoader())));
        in.readList(this.childPostComment, (ChildPostComment.class.getClassLoader()));
    }

    public CommentModel() {}

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

    public List<ChildPostComment> getChildPostComment() {
        return childPostComment;
    }

    public void setChildPostComment(List<ChildPostComment> childrenPost) {
        this.childPostComment = childrenPost;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(postComment);
        dest.writeValue(user);
        dest.writeList(childPostComment);
    }

    public int describeContents() {
        return  0;
    }

}
