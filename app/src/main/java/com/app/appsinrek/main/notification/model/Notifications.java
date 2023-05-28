
package com.app.appsinrek.main.notification.model;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.List;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Notifications implements Parcelable {

    @SerializedName("You")
    @Expose
    private List<Following> you = null;
    @SerializedName("Following")
    @Expose
    private List<Following> following = null;
    @SerializedName("Request")
    @Expose
    private Integer request;

    protected Notifications(Parcel in) {
        if (in.readByte() == 0) {
            request = null;
        } else {
            request = in.readInt();
        }
    }

    public static final Creator<Notifications> CREATOR = new Creator<Notifications>() {
        @Override
        public Notifications createFromParcel(Parcel in) {
            return new Notifications(in);
        }

        @Override
        public Notifications[] newArray(int size) {
            return new Notifications[size];
        }
    };

    public List<Following> getYou() {
        return you;
    }

    public void setYou(List<Following> you) {
        this.you = you;
    }

    public List<Following> getFollowing() {
        return following;
    }

    public void setFollowing(List<Following> following) {
        this.following = following;
    }

    public Integer getRequest() {
        return request;
    }

    public void setRequest(Integer request) {
        this.request = request;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        if (request == null) {
            parcel.writeByte((byte) 0);
        } else {
            parcel.writeByte((byte) 1);
            parcel.writeInt(request);
        }
    }
}
