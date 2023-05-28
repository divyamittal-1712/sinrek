
package com.app.appsinrek.main.activity_request.models;

import android.os.Parcelable;
import android.os.Parcelable.Creator;
import android.os.Parcelable.Creator;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class FollowRequest implements Parcelable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("sender_id")
    @Expose
    private String senderId;
    @SerializedName("receiver_id")
    @Expose
    private String receiverId;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("read")
    @Expose
    private String read;
    @SerializedName("created")
    @Expose
    private String created;
    public final static Creator<FollowRequest> CREATOR = new Creator<FollowRequest>() {

        public FollowRequest createFromParcel(android.os.Parcel in) {
            return new FollowRequest(in);
        }

        public FollowRequest[] newArray(int size) {
            return (new FollowRequest[size]);
        }

    }
    ;

    protected FollowRequest(android.os.Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.senderId = ((String) in.readValue((String.class.getClassLoader())));
        this.receiverId = ((String) in.readValue((String.class.getClassLoader())));
        this.status = ((String) in.readValue((String.class.getClassLoader())));
        this.read = ((String) in.readValue((String.class.getClassLoader())));
        this.created = ((String) in.readValue((String.class.getClassLoader())));
    }

    public FollowRequest() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getSenderId() {
        return senderId;
    }

    public void setSenderId(String senderId) {
        this.senderId = senderId;
    }

    public String getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getRead() {
        return read;
    }

    public void setRead(String read) {
        this.read = read;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(senderId);
        dest.writeValue(receiverId);
        dest.writeValue(status);
        dest.writeValue(read);
        dest.writeValue(created);
    }

    public int describeContents() {
        return  0;
    }

}
