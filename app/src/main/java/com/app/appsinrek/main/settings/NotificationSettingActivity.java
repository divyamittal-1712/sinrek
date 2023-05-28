package com.app.appsinrek.main.settings;

import static com.app.appsinrek.utils.AppConstants.TAG_GET_NOTIFICATION_SETTINGS;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityNotificationSettingBinding;
import com.app.appsinrek.databinding.ActivitySettingBinding;
import com.app.appsinrek.main.search.model.SearchModel;
import com.app.appsinrek.main.settings.models.NotificationPermission;
import com.app.appsinrek.main.settings.models.ResponseNotificationSettings;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.phonenumberui.AppConstant;
import com.app.appsinrek.utils.AppConstants;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.fitness.modval.interfaces.AuthListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;

public class NotificationSettingActivity extends AppCompatActivity implements AuthListener {
    ActivityNotificationSettingBinding bi;
    Activity mActivity = this;
    private MainViewModel mainViewModel;
    NotificationPermission notificationPermission;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setMainListener(this);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_notification_setting);
        setSupportActionBar(bi.toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        bi.newPost.radioGroup.setTag(1);
        bi.postLike.radioGroup.setTag(2);
        bi.postComment.radioGroup.setTag(3);
        bi.newRequest.radioGroup.setTag(4);
//        setListener();
    }

    private void setListener() {
        bi.newPost.radioGroup.setOnCheckedChangeListener(chnageListener);
        bi.postLike.radioGroup.setOnCheckedChangeListener(chnageListener);
        bi.postComment.radioGroup.setOnCheckedChangeListener(chnageListener);
        bi.newRequest.radioGroup.setOnCheckedChangeListener(chnageListener);
    }
    private void removeListener(){
        bi.newPost.radioGroup.setOnCheckedChangeListener(null);
        bi.postLike.radioGroup.setOnCheckedChangeListener(null);
        bi.postComment.radioGroup.setOnCheckedChangeListener(null);
        bi.newRequest.radioGroup.setOnCheckedChangeListener(null);
    }

    RadioGroup.OnCheckedChangeListener chnageListener = new RadioGroup.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(RadioGroup radioGroup, int i) {
            int index = (int) radioGroup.getTag();
            int pos = radioGroup.indexOfChild(radioGroup.findViewById(i));
            HashMap<Object, Object> body;
            removeListener();
            switch (index){
                case 1:
                    body = new HashMap();
                    body.put("userid", mainViewModel.getUser().getId());
                    body.put("name", "new_request");
                    body.put("value", pos==1?0:1);
                    mainViewModel.updateNotificationSettings(body);
                    break;
                case 2:
                    body = new HashMap();
                    body.put("userid",mainViewModel.getUser().getId());
                    body.put("name","like_on_my_post");
                    body.put("value",pos==1?0:1);
                    mainViewModel.updateNotificationSettings(body);
                    break;
                case 3:
                    body = new HashMap();
                    body.put("userid",mainViewModel.getUser().getId());
                    body.put("name","comment_on_my_post");
                    body.put("value",pos==1?0:1);
                    mainViewModel.updateNotificationSettings(body);
                    break;
                case 4:
                    body = new HashMap();
                    body.put("userid",mainViewModel.getUser().getId());
                    body.put("name","accept_request");
                    body.put("value",pos==1?0:1);
                    mainViewModel.updateNotificationSettings(body);
                    break;
            }

        }
    };
    @Override
    protected void onStart() {
        super.onStart();
        mainViewModel.getNotificationSettings();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStarted(@NonNull String tag) {
        UtilityHelperKt.show(bi.progressCircular);
    }

    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if(tag.equals(AppConstants.TAG_SET_NOTIFICATION_SETTINGS)){
            apiResponse.observe(this,responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try{
                    mainViewModel.getNotificationSettings();
                }catch (Exception e){
                    UtilityHelperKt.toast(mActivity,e.toString());
                }
            });
        }else if(tag.equals(TAG_GET_NOTIFICATION_SETTINGS)){
            apiResponse.observe(this,responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try{
                    Gson gson = new Gson();
                    Type type = new TypeToken<NotificationPermission>(){}.getType();
                    notificationPermission = gson.fromJson(gson.toJsonTree(responseData.getPermission()).toString(), type);
                    if(notificationPermission.getNewRequest().equals("1"))bi.newPost.rbOn.setChecked(true); else bi.newPost.rbOff.setChecked(true);
                    if(notificationPermission.getLikeOnMyPost().equals("1"))bi.postLike.rbOn.setChecked(true); else bi.postLike.rbOff.setChecked(true);
                    if(notificationPermission.getCommentOnMyPost().equals("1"))bi.postComment.rbOn.setChecked(true); else bi.postComment.rbOff.setChecked(true);
                    if(notificationPermission.getAcceptRequest().equals("1"))bi.newRequest.rbOn.setChecked(true); else bi.newRequest.rbOff.setChecked(true);
                    setListener();
                }catch (Exception e){
//                    UtilityHelperKt.toast(mActivity,e.toString());
                }
            });
        }
    }

    @Override
    public void onFailure(@NonNull String message) {
        UtilityHelperKt.hide(bi.progressCircular);
        UtilityHelperKt.toast(mActivity,message);
    }
}