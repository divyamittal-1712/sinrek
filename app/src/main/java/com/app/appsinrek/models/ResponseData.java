package com.app.appsinrek.models;

import android.os.Parcel;
import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.jetbrains.annotations.NotNull;

public class ResponseData implements Parcelable
{

    @SerializedName("code")
    @Expose
    private Integer code;
    @SerializedName("otp")
    @Expose
    private Integer otp;
    @SerializedName("msg")
    @Expose
    private Object msg;

    @SerializedName("permission")
    @Expose
    private Object permission;

    public ResponseData() {
    }

    //For Error in repository class
    public ResponseData(int code, @NotNull String msg) {
        this.code = code;
        this.msg = msg;
    }

    protected ResponseData(Parcel in) {
        if (in.readByte() == 0) {
            code = null;
        } else {
            code = in.readInt();
        }
        if (in.readByte() == 0) {
            otp = null;
        } else {
            otp = in.readInt();
        }
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        if (code == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(code);
        }
        if (otp == null) {
            dest.writeByte((byte) 0);
        } else {
            dest.writeByte((byte) 1);
            dest.writeInt(otp);
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<ResponseData> CREATOR = new Creator<ResponseData>() {
        @Override
        public ResponseData createFromParcel(Parcel in) {
            return new ResponseData(in);
        }

        @Override
        public ResponseData[] newArray(int size) {
            return new ResponseData[size];
        }
    };

    public Integer getCode() {
        return code;
    }

    public void setCode(Integer code) {
        this.code = code;
    }

    public Integer getOtp() {
        return otp;
    }

    public void setOtp(Integer otp) {
        this.otp = otp;
    }

    public Object getMsg() {
        return msg;
    }

    public void setMsg(Object msg) {
        this.msg = msg;
    }

    public Object getPermission() {
        return permission;
    }

    public void setPermission(Object permission) {
        this.permission = permission;
    }
}