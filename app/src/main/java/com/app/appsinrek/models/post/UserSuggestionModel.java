
package com.app.appsinrek.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.appsinrek.models.User;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class UserSuggestionModel implements Parcelable
{

    @SerializedName("User")
    @Expose
    private User user;
    private int position = -1;

    public UserSuggestionModel() {
    }

    protected UserSuggestionModel(Parcel in) {
        user = in.readParcelable(User.class.getClassLoader());
        position = in.readInt();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(user, flags);
        dest.writeInt(position);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<UserSuggestionModel> CREATOR = new Creator<UserSuggestionModel>() {
        @Override
        public UserSuggestionModel createFromParcel(Parcel in) {
            return new UserSuggestionModel(in);
        }

        @Override
        public UserSuggestionModel[] newArray(int size) {
            return new UserSuggestionModel[size];
        }
    };

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public int getPosition() {
        return position;
    }

    public void setPosition(int position) {
        this.position = position;
    }
}
