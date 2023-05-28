
package com.app.appsinrek.main.activity_request.models;

import android.net.Uri;
import android.os.Parcelable;

import com.app.appsinrek.network.API_LINKS;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;

public class FollowingActivity implements Parcelable
{

    @SerializedName("receiver_id")
    @Expose
    private String receiverId;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("username")
    @Expose
    private String username;
    @SerializedName("image")
    @Expose
    private String image;
    @SerializedName("msg")
    @Expose
    private String msg;
    @SerializedName("attachment")
    @Expose
    private String attachment;
    public final static Creator<FollowingActivity> CREATOR = new Creator<FollowingActivity>() {

        public FollowingActivity createFromParcel(android.os.Parcel in) {
            return new FollowingActivity(in);
        }

        public FollowingActivity[] newArray(int size) {
            return (new FollowingActivity[size]);
        }

    }
            ;

    protected FollowingActivity(android.os.Parcel in) {
        this.receiverId = ((String) in.readValue((String.class.getClassLoader())));
        this.created = ((String) in.readValue((String.class.getClassLoader())));
        this.userId = ((String) in.readValue((String.class.getClassLoader())));
        this.username = ((String) in.readValue((String.class.getClassLoader())));
        this.image = ((String) in.readValue((String.class.getClassLoader())));
        this.msg = ((String) in.readValue((String.class.getClassLoader())));
        this.attachment = ((String) in.readValue((String.class.getClassLoader())));
    }

    public FollowingActivity() {
    }


    public String getReceiverId() {
        return receiverId;
    }
    public void setReceiverId(String receiverId) {
        this.receiverId = receiverId;
    }

    public String getCreated() {
        return created;
    }
    public void setCreated(String created) {
        this.created = created;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {return username;}
    public void setUsername(String username) {
        this.username = username;
    }

    public String getImage() {return image;}
    public void setImage(String image) {this.image = image;}

    public String getMsg() {return msg;}
    public void setMsg(String msg) {this.msg = msg;}


    public String getAttachment() {
        Uri url = null;
        if (attachment != null && !attachment.isEmpty()) {
            try {
                JSONArray arr = new JSONArray(attachment);
                if(arr.length()>0) {
                    url = Uri.parse(new JSONArray(attachment).getString(0));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        String path = null;
        if (url != null) {
            if (!url.isAbsolute()) {
                path = API_LINKS.BASE_IMAGE_URL+url.getPath();
            } else {
                path = url.getPath();
            }
        }
        return path;
    }
    public String getAttachmentRaw() {
        return attachment;
    }

    public void setAttachment(String attachment) {
        this.attachment = attachment;
    }


    public void writeToParcel(android.os.Parcel dest, int flags) {
        dest.writeValue(receiverId);
        dest.writeValue(created);
    }

    public int describeContents() {
        return  0;
    }

}
