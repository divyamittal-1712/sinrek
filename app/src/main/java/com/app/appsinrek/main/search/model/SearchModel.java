package com.app.appsinrek.main.search.model;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.appsinrek.models.Other;
import com.app.appsinrek.models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class SearchModel implements Parcelable {

    @SerializedName("User")
    @Expose
    private User user;
    @SerializedName("Other")
    @Expose
    private Other other;

    public SearchModel() {
    }

    protected SearchModel(Parcel in) {
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

    public static final Creator<SearchModel> CREATOR = new Creator<SearchModel>() {
        @Override
        public SearchModel createFromParcel(Parcel in) {
            return new SearchModel(in);
        }

        @Override
        public SearchModel[] newArray(int size) {
            return new SearchModel[size];
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
