package com.app.appsinrek.main.dashboard.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.appsinrek.models.Other;
import com.app.appsinrek.models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class LikedUsersModel implements Parcelable {

    @SerializedName("User")
    @Expose
    private User user;
    @SerializedName("Other")
    @Expose
    private Other other;

    public LikedUsersModel() {
    }

    protected LikedUsersModel(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
        other = in.readParcelable(Other.class.getClassLoader());
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user, flags);
        dest.writeParcelable(other, flags);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<LikedUsersModel> CREATOR = new Creator<LikedUsersModel>() {
        @Override
        public LikedUsersModel createFromParcel(Parcel in) {
            return new LikedUsersModel(in);
        }

        @Override
        public LikedUsersModel[] newArray(int size) {
            return new LikedUsersModel[size];
        }
    };

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Other getOther() {
        return other;
    }

    public void setOther(Other other) {
        this.other = other;
    }

}
