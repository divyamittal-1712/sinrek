package com.app.appsinrek.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Other implements Parcelable {

@SerializedName("button")
@Expose
private String button;

    protected Other(Parcel in) {
        button = in.readString();
    }

    public static final Creator<Other> CREATOR = new Creator<Other>() {
        @Override
        public Other createFromParcel(Parcel in) {
            return new Other(in);
        }

        @Override
        public Other[] newArray(int size) {
            return new Other[size];
        }
    };

    public String getButton() {
return button;
}

public void setButton(String button) {
this.button = button;
}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeString(button);
    }
}