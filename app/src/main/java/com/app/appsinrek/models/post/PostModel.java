
package com.app.appsinrek.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.appsinrek.models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class PostModel implements Parcelable
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

    private int position = -1;

    public PostModel() {
    }

    protected PostModel(Parcel in) {
        post = in.readParcelable(Post.class.getClassLoader());
        user = in.readParcelable(User.class.getClassLoader());
        position = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(post, flags);
        dest.writeParcelable(user, flags);
        dest.writeInt(position);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<PostModel> CREATOR = new Creator<PostModel>() {
        @Override
        public PostModel createFromParcel(Parcel in) {
            return new PostModel(in);
        }

        @Override
        public PostModel[] newArray(int size) {
            return new PostModel[size];
        }
    };

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

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
