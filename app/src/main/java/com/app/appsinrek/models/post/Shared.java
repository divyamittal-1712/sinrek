package com.app.appsinrek.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.appsinrek.models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Shared implements Parcelable {

    @SerializedName("Post")
    @Expose
    private Post post;
    @SerializedName("User")
    @Expose
    private User user;

    protected Shared(Parcel in) {
        post = in.readParcelable(Post.class.getClassLoader());
        user = in.readParcelable(User.class.getClassLoader());
    }

    public static final Creator<Shared> CREATOR = new Creator<Shared>() {
        @Override
        public Shared createFromParcel(Parcel in) {
            return new Shared(in);
        }

        @Override
        public Shared[] newArray(int size) {
            return new Shared[size];
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeParcelable(post, i);
        parcel.writeParcelable(user, i);
    }
}
