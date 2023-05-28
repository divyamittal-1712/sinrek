
package com.app.appsinrek.main.notification.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.appsinrek.models.Sender;
import com.app.appsinrek.models.post.Post;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Following implements Parcelable {

    @SerializedName("Notification")
    @Expose
    private Notification notification;
    @SerializedName("Sender")
    @Expose
    private Sender sender;
    @SerializedName("Post")
    @Expose
    private Post post;

    protected Following(Parcel in) {
        notification = in.readParcelable(Notification.class.getClassLoader());
        sender = in.readParcelable(Sender.class.getClassLoader());
        post = in.readParcelable(Post.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(notification, flags);
        dest.writeParcelable(sender, flags);
        dest.writeParcelable(post, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Following> CREATOR = new Creator<Following>() {
        @Override
        public Following createFromParcel(Parcel in) {
            return new Following(in);
        }

        @Override
        public Following[] newArray(int size) {
            return new Following[size];
        }
    };

    public Notification getNotification() {
        return notification;
    }

    public void setNotification(Notification notification) {
        this.notification = notification;
    }

    public Sender getUser() {
        return sender;
    }

    public void setUser(Sender sender) {
        this.sender = sender;
    }

    public Post getPost() {
        return post;
    }

    public void setPost(Post post) {
        this.post = post;
    }

}
