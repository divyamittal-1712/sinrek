
package com.app.appsinrek.main.settings.models;

import android.os.Parcelable;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class ResponseNotificationSettings implements Parcelable
{

    @SerializedName("permission")
    @Expose
    private NotificationPermission notificationPermission;
    @SerializedName("code")
    @Expose
    private String code;
    public final static Creator<ResponseNotificationSettings> CREATOR = new Creator<ResponseNotificationSettings>() {


        public ResponseNotificationSettings createFromParcel(android.os.Parcel in) {
            return new ResponseNotificationSettings(in);
        }

        public ResponseNotificationSettings[] newArray(int size) {
            return (new ResponseNotificationSettings[size]);
        }

    }
    ;

    protected ResponseNotificationSettings(android.os.Parcel in) {
        this.notificationPermission = ((NotificationPermission) in.readValue((NotificationPermission.class.getClassLoader())));
        this.code = ((String) in.readValue((String.class.getClassLoader())));
    }

    public ResponseNotificationSettings() {
    }

    public NotificationPermission getPermission() {
        return notificationPermission;
    }

    public void setPermission(NotificationPermission notificationPermission) {
        this.notificationPermission = notificationPermission;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(notificationPermission);
        dest.writeValue(code);
    }

    public int describeContents() {
        return  0;
    }

}
