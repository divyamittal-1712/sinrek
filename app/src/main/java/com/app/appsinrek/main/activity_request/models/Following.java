
package com.app.appsinrek.main.activity_request.models;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Following implements Parcelable
{

    @SerializedName("followingActivity")
    @Expose
    private FollowingActivity followingActivity;
    public final static Creator<Following> CREATOR = new Creator<Following>() {


        public Following createFromParcel(android.os.Parcel in) {
            return new Following(in);
        }

        public Following[] newArray(int size) {
            return (new Following[size]);
        }

    }
    ;

    protected Following(android.os.Parcel in) {
        this.followingActivity = ((FollowingActivity) in.readValue((FollowingActivity.class.getClassLoader())));
    }

    public Following() {
    }

    public FollowingActivity getFollowingActivity() {
        return followingActivity;
    }

    public void setFollowingActivity(FollowingActivity followingActivity) {
        this.followingActivity = followingActivity;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(followingActivity);
    }

    public int describeContents() {
        return  0;
    }

}
