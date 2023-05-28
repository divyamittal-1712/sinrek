
package com.app.appsinrek.models;

import android.os.Parcel;
import android.os.Parcelable;

import com.app.appsinrek.utils.UtilityHelperKt;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Sender implements Parcelable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("full_name")
    @Expose
    private String fullName;
    @SerializedName("email")
    @Expose
    private String email;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("password")
    @Expose
    private String password;
    @SerializedName("website")
    @Expose
    private String website;
    @SerializedName("bio")
    @Expose
    private String bio;
    @SerializedName("phone")
    @Expose
    private String phone;
    @SerializedName("gender")
    @Expose
    private String gender;
    @SerializedName("dob")
    @Expose
    private String dob;
    @SerializedName("education")
    @Expose
    private String education;
    @SerializedName("address")
    @Expose
    private String address;
    @SerializedName("device_token")
    @Expose
    private String deviceToken;
    @SerializedName("profile")
    @Expose
    private String profile;
    @SerializedName("otp_verify")
    @Expose
    private String otpVerify;
    @SerializedName("status")
    @Expose
    private String status;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("followers")
    @Expose
    private String followers;
    @SerializedName("following")
    @Expose
    private String following;
    @SerializedName("posts")
    @Expose
    private String posts;
    @SerializedName("isFollowing")
    @Expose
    private String isFollowing;
    @SerializedName("totalfollower")
    @Expose
    private Integer totalfollower;
    @SerializedName("totalfollowing")
    @Expose
    private Integer totalfollowing;
    private boolean isFriend = false;
//    @SerializedName("api_key")
//    @Expose
//    private String api_key;


    public Sender() {
    }

    protected Sender(Parcel in) {
        id = in.readString();
        username = in.readString();
        fullName = in.readString();
        email = in.readString();
        image = in.readString();
        password = in.readString();
        website = in.readString();
        bio = in.readString();
        phone = in.readString();
        gender = in.readString();
        dob = in.readString();
        education = in.readString();
        address = in.readString();
        deviceToken = in.readString();
        profile = in.readString();
        otpVerify = in.readString();
        status = in.readString();
        created = in.readString();
        followers = in.readString();
        following = in.readString();
        posts = in.readString();
        isFollowing = in.readString();
        if (in.readByte() == 0) {
            totalfollower = null;
        } else {
            totalfollower = in.readInt();
        }
        if (in.readByte() == 0) {
            totalfollowing = null;
        } else {
            totalfollowing = in.readInt();
        }
        isFriend = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(username);
        dest.writeString(fullName);
        dest.writeString(email);
        dest.writeString(image);
        dest.writeString(password);
        dest.writeString(website);
        dest.writeString(bio);
        dest.writeString(phone);
        dest.writeString(gender);
        dest.writeString(dob);
        dest.writeString(education);
        dest.writeString(address);
        dest.writeString(deviceToken);
        dest.writeString(profile);
        dest.writeString(otpVerify);
        dest.writeString(status);
        dest.writeString(created);
        dest.writeString(followers);
        dest.writeString(following);
        dest.writeString(posts);
        dest.writeString(isFollowing);
        if (totalfollower == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(totalfollower);
        }
        if (totalfollowing == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(totalfollowing);
        }
        dest.writeByte((byte) (isFriend ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Sender> CREATOR = new Creator<Sender>() {
        @Override
        public Sender createFromParcel(Parcel in) {
            return new Sender(in);
        }

        @Override
        public Sender[] newArray(int size) {
            return new Sender[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getFullName() {
        return UtilityHelperKt.getText(fullName);
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getEmail() {
        return UtilityHelperKt.getText(email);
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getImage() {
        return UtilityHelperKt.getText(image);
    }

    public void setImage(String image) {
        this.image = image;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getWebsite() {
        return website;
    }

    public void setWebsite(String website) {
        this.website = website;
    }

    public String getBio() {
        return bio;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getGender() {
        return gender;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getDob() {
        return dob;
    }

    public void setDob(String dob) {
        this.dob = dob;
    }

    public String getEducation() {
        return education;
    }

    public void setEducation(String education) {
        this.education = education;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public String getDeviceToken() {
        return deviceToken;
    }

    public void setDeviceToken(String deviceToken) {
        this.deviceToken = deviceToken;
    }

    public String getProfile() {
        return profile;
    }

    public void setProfile(String profile) {
        this.profile = profile;
    }

    public String getOtpVerify() {
        return otpVerify;
    }

    public void setOtpVerify(String otpVerify) {
        this.otpVerify = otpVerify;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public boolean isFriend() {
        return isFriend;
    }

    public void setFriend(boolean friend) {
        isFriend = friend;
    }

    public String getFollowers() {
        return followers;
    }

    public void setFollowers(String followers) {
        this.followers = followers;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getPosts() {
        return posts;
    }

    public void setPosts(String posts) {
        this.posts = posts;
    }

    public String getIsFollowing() {
        return isFollowing;
    }

    public void setIsFollowing(String isFollowing) {
        this.isFollowing = isFollowing;
    }

    public Integer getTotalfollower() {
        return totalfollower;
    }

    public void setTotalfollower(Integer totalfollower) {
        this.totalfollower = totalfollower;
    }

    public Integer getTotalfollowing() {
        return totalfollowing;
    }

    public void setTotalfollowing(Integer totalfollowing) {
        this.totalfollowing = totalfollowing;
    }
}
