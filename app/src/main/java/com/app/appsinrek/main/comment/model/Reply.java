
package com.app.appsinrek.main.comment.model;
import android.os.Parcelable;
import com.app.appsinrek.models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Reply implements Parcelable
{

    @SerializedName("PostCommentReply")
    @Expose
    private PostCommentReply postCommentReply;
    @SerializedName("User")
    @Expose
    private User user;
    public final static Creator<Reply> CREATOR = new Creator<Reply>() {


        public Reply createFromParcel(android.os.Parcel in) {
            return new Reply(in);
        }

        public Reply[] newArray(int size) {
            return (new Reply[size]);
        }

    }
    ;

    protected Reply(android.os.Parcel in) {
        this.postCommentReply = ((PostCommentReply) in.readValue((PostCommentReply.class.getClassLoader())));
        this.user = ((User) in.readValue((User.class.getClassLoader())));
    }

    public Reply() {
    }

    public PostCommentReply getPostCommentReply() {
        return postCommentReply;
    }

    public void setPostCommentReply(PostCommentReply postCommentReply) {
        this.postCommentReply = postCommentReply;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(postCommentReply);
        dest.writeValue(user);
    }

    public int describeContents() {
        return  0;
    }

}
