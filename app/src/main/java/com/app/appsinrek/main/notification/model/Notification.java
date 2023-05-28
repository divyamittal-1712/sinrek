
package com.app.appsinrek.main.notification.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notification implements Parcelable {

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("post_id")
    @Expose
    private String postId;
    @SerializedName("post_user_id")
    @Expose
    private String postUserId;
    @SerializedName("like")
    @Expose
    private String like;
    @SerializedName("comment")
    @Expose
    private String comment;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("is_read")
    @Expose
    private String isRead;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("sender_image")
    @Expose
    private String sender_image;
    @SerializedName("sender_username")
    @Expose
    private String sender_username;


    protected Notification(Parcel in) {
        id = in.readString();
        userId = in.readString();
        postId = in.readString();
        postUserId = in.readString();
        like = in.readString();
        comment = in.readString();
        msg = in.readString();
        isRead = in.readString();
        created = in.readString();
    }

    public static final Creator<Notification> CREATOR = new Creator<Notification>() {
        @Override
        public Notification createFromParcel(Parcel in) {
            return new Notification(in);
        }

        @Override
        public Notification[] newArray(int size) {
            return new Notification[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getSender_image() {
        return sender_image;
    }

    public void setSender_image(String imagePath) {
        this.sender_image = imagePath;
    }


    public String getSender_username() {
        return sender_username;
    }

    public void setSender_username(String username) {
        this.sender_username = username;
    }


    public String getPostId() {
        return postId;
    }

    public void setPostId(String postId) {
        this.postId = postId;
    }

    public String getPostUserId() {
        return postUserId;
    }

    public void setPostUserId(String postUserId) {
        this.postUserId = postUserId;
    }

    public String getLike() {
        return like;
    }

    public void setLike(String like) {
        this.like = like;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getIsRead() {
        return isRead;
    }

    public void setIsRead(String isRead) {
        this.isRead = isRead;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(id);
        parcel.writeString(userId);
        parcel.writeString(postId);
        parcel.writeString(postUserId);
        parcel.writeString(like);
        parcel.writeString(comment);
        parcel.writeString(msg);
        parcel.writeString(isRead);
        parcel.writeString(created);
    }
}
