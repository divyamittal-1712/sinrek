
package com.app.appsinrek.models.post;

import android.net.Uri;
import android.os.Parcel;
import android.os.Parcelable;

import com.app.appsinrek.network.API_LINKS;
import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import org.json.JSONArray;
import org.json.JSONException;

public class Post implements Parcelable
{

    @SerializedName("id")
    @Expose
    private String id;
    @SerializedName("post_id")
    @Expose
    private Object postId;
    @SerializedName("caption")
    @Expose
    private String caption;
    @SerializedName("attachment")
    @Expose
    private String attachment;
    @SerializedName("location_string")
    @Expose
    private String locationString;
    @SerializedName("lat")
    @Expose
    private String lat;
    @SerializedName("long")
    @Expose
    private String _long;
    @SerializedName("type")
    @Expose
    private String type;
    @SerializedName("post_type")
    @Expose
    private String postType;
    @SerializedName("active")
    @Expose
    private String active;
    @SerializedName("user_id")
    @Expose
    private String userId;
    @SerializedName("created")
    @Expose
    private String created;
    @SerializedName("updated")
    @Expose
    private String updated;
    @SerializedName("PostLike")
    @Expose
    private int postLike;
    @SerializedName("PostBookmark")
    @Expose
    private int postBookmark;
    @SerializedName("total_likes")
    @Expose
    private int totalLikes;
    @SerializedName("total_views")
    @Expose
    private int totalViews;
    @SerializedName("total_comments")
    @Expose
    private String totalComments;
    @SerializedName("last_comment")
    @Expose
    private LastComment lastComment;

    @SerializedName("who_can_comment_on_my_post")
    @Expose
    private String whoCanCommentOnMyPost;
    @SerializedName("who_can_like_my_post")
    @Expose
    private String whoCanLikeMyPost;
    @SerializedName("who_can_save_my_post")
    @Expose
    private String whoCanSaveMyPost;
    @SerializedName("who_can_download_my_post")
    @Expose
    private String whoCanDownloadMyPost;
    @SerializedName("who_can_see_my_profile")
    @Expose
    private String whoCanSeeMyProfile;
    @SerializedName("following")
    @Expose
    private String following;
    @SerializedName("follower")
    @Expose
    private String follower;
    boolean mute = false;

    public Post() {
    }

    protected Post(Parcel in) {
        id = in.readString();
        caption = in.readString();
        attachment = in.readString();
        locationString = in.readString();
        lat = in.readString();
        _long = in.readString();
        type = in.readString();
        postType = in.readString();
        active = in.readString();
        userId = in.readString();
        created = in.readString();
        updated = in.readString();
        postLike = in.readInt();
        postBookmark = in.readInt();
        totalLikes = in.readInt();
        totalViews = in.readInt();
        totalComments = in.readString();
        lastComment = in.readParcelable(LastComment.class.getClassLoader());
        whoCanCommentOnMyPost = in.readString();
        whoCanLikeMyPost = in.readString();
        whoCanSaveMyPost = in.readString();
        whoCanDownloadMyPost = in.readString();
        whoCanSeeMyProfile = in.readString();
        following = in.readString();
        follower = in.readString();
        mute = in.readByte() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(caption);
        dest.writeString(attachment);
        dest.writeString(locationString);
        dest.writeString(lat);
        dest.writeString(_long);
        dest.writeString(type);
        dest.writeString(postType);
        dest.writeString(active);
        dest.writeString(userId);
        dest.writeString(created);
        dest.writeString(updated);
        dest.writeInt(postLike);
        dest.writeInt(postBookmark);
        dest.writeInt(totalLikes);
        dest.writeInt(totalViews);
        dest.writeString(totalComments);
        dest.writeParcelable(lastComment, flags);
        dest.writeString(whoCanCommentOnMyPost);
        dest.writeString(whoCanLikeMyPost);
        dest.writeString(whoCanSaveMyPost);
        dest.writeString(whoCanDownloadMyPost);
        dest.writeString(whoCanSeeMyProfile);
        dest.writeString(following);
        dest.writeString(follower);
        dest.writeByte((byte) (mute ? 1 : 0));
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<Post> CREATOR = new Creator<Post>() {
        @Override
        public Post createFromParcel(Parcel in) {
            return new Post(in);
        }

        @Override
        public Post[] newArray(int size) {
            return new Post[size];
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

    public String getCaption() {
        return caption;
    }

    public void setCaption(String caption) {
        this.caption = caption;
    }

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

    public String getLocationString() {
        return locationString;
    }

    public void setLocationString(String locationString) {
        this.locationString = locationString;
    }

    public String getLat() {
        return lat;
    }

    public void setLat(String lat) {
        this.lat = lat;
    }

    public String getLong() {
        return _long;
    }

    public void setLong(String _long) {
        this._long = _long;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getPostType() {
        return postType;
    }

    public void setPostType(String postType) {
        this.postType = postType;
    }

    public String getActive() {
        return active;
    }

    public void setActive(String active) {
        this.active = active;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getCreated() {
        return created;
    }

    public void setCreated(String created) {
        this.created = created;
    }

    public String getUpdated() {
        return updated;
    }

    public void setUpdated(String updated) {
        this.updated = updated;
    }

    public int getPostLike() {
        return postLike;
    }

    public void setPostLike(int postLike) {
        this.postLike = postLike;
    }

    public int getPostBookmark() {
        return postBookmark;
    }

    public void setPostBookmark(int postBookmark) {
        this.postBookmark = postBookmark;
    }

    public int getTotalLikes() {
        return totalLikes;
    }

    public void setTotalLikes(int totalLikes) {
        this.totalLikes = totalLikes;
    }

    public int getTotalViews() {
        return totalViews;
    }

    public void setTotalViews(int totalViews) {
        this.totalViews = totalViews;
    }

    public String getTotalComments() {
        return totalComments;
    }

    public void setTotalComments(String totalComments) {
        this.totalComments = totalComments;
    }

    public LastComment getLastComment() {
        return lastComment;
    }

    public void setLastComment(LastComment lastComment) {
        this.lastComment = lastComment;
    }

    public String getWhoCanCommentOnMyPost() {
        return whoCanCommentOnMyPost;
    }

    public void setWhoCanCommentOnMyPost(String whoCanCommentOnMyPost) {
        this.whoCanCommentOnMyPost = whoCanCommentOnMyPost;
    }

    public String getWhoCanLikeMyPost() {
        return whoCanLikeMyPost;
    }

    public void setWhoCanLikeMyPost(String whoCanLikeMyPost) {
        this.whoCanLikeMyPost = whoCanLikeMyPost;
    }

    public String getWhoCanSaveMyPost() {
        return whoCanSaveMyPost;
    }

    public void setWhoCanSaveMyPost(String whoCanSaveMyPost) {
        this.whoCanSaveMyPost = whoCanSaveMyPost;
    }

    public String getWhoCanDownloadMyPost() {
        return whoCanDownloadMyPost;
    }

    public void setWhoCanDownloadMyPost(String whoCanDownloadMyPost) {
        this.whoCanDownloadMyPost = whoCanDownloadMyPost;
    }

    public String getWhoCanSeeMyProfile() {
        return whoCanSeeMyProfile;
    }

    public void setWhoCanSeeMyProfile(String whoCanSeeMyProfile) {
        this.whoCanSeeMyProfile = whoCanSeeMyProfile;
    }

    public String getFollowing() {
        return following;
    }

    public void setFollowing(String following) {
        this.following = following;
    }

    public String getFollower() {
        return follower;
    }

    public void setFollower(String follower) {
        this.follower = follower;
    }

    public boolean isMute() {
        return mute;
    }

    public void setMute(boolean mute) {
        this.mute = mute;
    }

}
