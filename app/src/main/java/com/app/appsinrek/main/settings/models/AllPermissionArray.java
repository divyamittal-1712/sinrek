
package com.app.appsinrek.main.settings.models;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class AllPermissionArray {

    @SerializedName("PostPermission")
    @Expose
    private PostPermission postPermission;

    public PostPermission getPostPermission() {
        return postPermission;
    }

    public void setPostPermission(PostPermission postPermission) {
        this.postPermission = postPermission;
    }

}
