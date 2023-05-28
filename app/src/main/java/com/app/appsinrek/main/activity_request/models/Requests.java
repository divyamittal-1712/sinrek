
package com.app.appsinrek.main.activity_request.models;

import android.os.Parcelable;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Requests implements Parcelable
{

    @SerializedName("FollowRequest")
    @Expose
    private FollowRequest followRequest;
    @SerializedName("Sender")
    @Expose
    private Sender sender;
    @SerializedName("Receiver")
    @Expose
    private Receiver receiver;
    public final static Creator<Requests> CREATOR = new Creator<Requests>() {


        public Requests createFromParcel(android.os.Parcel in) {
            return new Requests(in);
        }

        public Requests[] newArray(int size) {
            return (new Requests[size]);
        }

    }
    ;

    protected Requests(android.os.Parcel in) {
        this.followRequest = ((FollowRequest) in.readValue((FollowRequest.class.getClassLoader())));
        this.sender = ((Sender) in.readValue((Sender.class.getClassLoader())));
        this.receiver = ((Receiver) in.readValue((Receiver.class.getClassLoader())));
    }

    public Requests() {
    }

    public FollowRequest getFollowRequest() {
        return followRequest;
    }

    public void setFollowRequest(FollowRequest followRequest) {
        this.followRequest = followRequest;
    }

    public Sender getSender() {
        return sender;
    }

    public void setSender(Sender sender) {
        this.sender = sender;
    }

    public Receiver getReceiver() {
        return receiver;
    }

    public void setReceiver(Receiver receiver) {
        this.receiver = receiver;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(followRequest);
        dest.writeValue(sender);
        dest.writeValue(receiver);
    }

    public int describeContents() {
        return  0;
    }

}
