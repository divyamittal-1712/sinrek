
package com.app.appsinrek.main.comment.model;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostCommentReply implements Parcelable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("comment_id")
    @Expose
    private String commentId;
    @SerializedName("post_id")
    @Expose
    private String postId;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("reply")
    @Expose
    private String reply;
    @SerializedName("totalLike")
    @Expose
    private int totalLike;
    @SerializedName("isLike")
    @Expose
    private int isLike;
    @SerializedName("created")
    @Expose
    private String created;

    public PostCommentReply() {
    }

    protected PostCommentReply(Parcel in) {
        id = in.readString();
        commentId = in.readString();
        postId = in.readString();
        userId = in.readString();
        reply = in.readString();
        totalLike = in.readInt();
        isLike = in.readInt();
        created = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(commentId);
        dest.writeString(postId);
        dest.writeString(userId);
        dest.writeString(reply);
        dest.writeInt(totalLike);
        dest.writeInt(isLike);
        dest.writeString(created);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PostCommentReply> CREATOR = new Creator<PostCommentReply>() {
        @Override
        public PostCommentReply createFromParcel(Parcel in) {
            return new PostCommentReply(in);
        }

        @Override
        public PostCommentReply[] newArray(int size) {
            return new PostCommentReply[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCommentId() {
        return commentId;
    }

    public void setCommentId(String commentId) {
        this.commentId = commentId;
    }

    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getReply() {
        return reply;
    }

    public void setReply(String reply) {
        this.reply = reply;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public int getTotalLike() {
        return totalLike;
    }

    public void setTotalLike(int totalLike) {
        this.totalLike = totalLike;
    }

    public int getIsLike() {
        return isLike;
    }

    public void setIsLike(int isLike) {
        this.isLike = isLike;
    }
}
