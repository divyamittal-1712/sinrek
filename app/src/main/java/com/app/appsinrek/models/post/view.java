
package com.app.appsinrek.models.post;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class view implements Parcelable
{

    private String id;
    private Object postId;
    public view() {
    }

    protected view(Parcel in) {
        id = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<view> CREATOR = new Creator<view>() {
        @Override
        public view createFromParcel(Parcel in) {
            return new view(in);
        }

        @Override
        public view[] newArray(int size) {
            return new view[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Object getPostId() {
        return postId;
    }

    public void setPostId(Object postId) {
        this.postId = postId;
    }
}
