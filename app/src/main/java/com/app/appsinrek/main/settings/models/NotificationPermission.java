
package com.app.appsinrek.main.settings.models;

import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class NotificationPermission implements Parcelable
{

    @SerializedName("like_on_my_post")
    @Expose
    private String likeOnMyPost;
    @SerializedName("comment_on_my_post")
    @Expose
    private String commentOnMyPost;
    @SerializedName("new_request")
    @Expose
    private String newRequest;
    @SerializedName("accept_request")
    @Expose
    private String acceptRequest;
    public final static Creator<NotificationPermission> CREATOR = new Creator<NotificationPermission>() {


        public NotificationPermission createFromParcel(android.os.Parcel in) {
            return new NotificationPermission(in);
        }

        public NotificationPermission[] newArray(int size) {
            return (new NotificationPermission[size]);
        }

    }
    ;

    protected NotificationPermission(android.os.Parcel in) {
        this.likeOnMyPost = ((String) in.readValue((String.class.getClassLoader())));
        this.commentOnMyPost = ((String) in.readValue((String.class.getClassLoader())));
        this.newRequest = ((String) in.readValue((String.class.getClassLoader())));
        this.acceptRequest = ((String) in.readValue((String.class.getClassLoader())));
    }

    public NotificationPermission() {
    }

    public String getLikeOnMyPost() {
        return likeOnMyPost;
    }

    public void setLikeOnMyPost(String likeOnMyPost) {
        this.likeOnMyPost = likeOnMyPost;
    }

    public String getCommentOnMyPost() {
        return commentOnMyPost;
    }

    public void setCommentOnMyPost(String commentOnMyPost) {
        this.commentOnMyPost = commentOnMyPost;
    }

    public String getNewRequest() {
        return newRequest;
    }

    public void setNewRequest(String newRequest) {
        this.newRequest = newRequest;
    }

    public String getAcceptRequest() {
        return acceptRequest;
    }

    public void setAcceptRequest(String acceptRequest) {
        this.acceptRequest = acceptRequest;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(likeOnMyPost);
        dest.writeValue(commentOnMyPost);
        dest.writeValue(newRequest);
        dest.writeValue(acceptRequest);
    }

    public int describeContents() {
        return  0;
    }

}
