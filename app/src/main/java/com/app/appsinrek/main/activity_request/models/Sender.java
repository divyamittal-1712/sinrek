
package com.app.appsinrek.main.activity_request.models;

import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sender implements Parcelable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("followers")
    @Expose
    private String followers;
    public final static Creator<Sender> CREATOR = new Creator<Sender>() {


        public Sender createFromParcel(android.os.Parcel in) {
            return new Sender(in);
        }

        public Sender[] newArray(int size) {
            return (new Sender[size]);
        }

    }
    ;

    protected Sender(android.os.Parcel in) {
        this.id = ((String) in.readValue((String.class.getClassLoader())));
        this.email = ((String) in.readValue((String.class.getClassLoader())));
        this.username = ((String) in.readValue((String.class.getClassLoader())));
        this.image = ((String) in.readValue((String.class.getClassLoader())));
    }

    public Sender() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getImage() {
        return image;
    }

    public void setImage(String image) {
        this.image = image;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFollowers() {
        return followers;
    }


    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(id);
        dest.writeValue(email);
        dest.writeValue(username);
        dest.writeValue(image);
    }

    public int describeContents() {
        return  0;
    }

}
