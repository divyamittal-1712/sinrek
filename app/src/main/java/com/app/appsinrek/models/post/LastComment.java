
package com.app.appsinrek.models.post;

import android.os.Parcelable;

public class LastComment implements Parcelable
{

    public final static Creator<LastComment> CREATOR = new Creator<LastComment>() {


        public LastComment createFromParcel(android.os.Parcel in) {
            return new LastComment(in);
        }

        public LastComment[] newArray(int size) {
            return (new LastComment[size]);
        }

    }
    ;

    protected LastComment(android.os.Parcel in) {
    }

    public LastComment() {
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
    }

    public int describeContents() {
        return  0;
    }

}
