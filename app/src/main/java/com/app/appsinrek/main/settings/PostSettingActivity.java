package com.app.appsinrek.main.settings;

import static com.app.appsinrek.utils.AppConstants.TAG_GET_All_SETTINGS;
import static com.app.appsinrek.utils.AppConstants.TAG_UPDATE_SETTINGS;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityPostSettingBinding;
import com.app.appsinrek.main.settings.models.AllPermissionArray;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.fitness.modval.interfaces.AuthListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kotlin.collections.CollectionsKt;

public class PostSettingActivity extends AppCompatActivity implements AuthListener {
    ActivityPostSettingBinding bi;
    Activity mActivity = this;
    private MainViewModel mainViewModel;
    List<AllPermissionArray> allPermissionArrays = new ArrayList<>();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setMainListener(this);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_post_setting);
        setSupportActionBar(bi.toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        bi.rb.radioGroup.setTag(1);
        bi.rb1.radioGroup.setTag(2);
        bi.rb2.radioGroup.setTag(3);
        bi.rb3.radioGroup.setTag(4);
        bi.rb4.radioGroup.setTag(5);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mainViewModel.getAllPermissionSettings();
    }

    @Override
    public void onStarted(@NonNull String tag) {
        UtilityHelperKt.show(bi.progressCircular);
    }

    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if(tag.equals(TAG_UPDATE_SETTINGS)) {
            apiResponse.observe(this, responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try{
                    mainViewModel.getAllPermissionSettings();
                    UtilityHelperKt.toast(mActivity,responseData.getMsg().toString());
                }catch (Exception e){
                    UtilityHelperKt.toast(mActivity,e.toString());
                }
            });
        }else if(tag.equals(TAG_GET_All_SETTINGS)){
            apiResponse.observe(this,responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try{
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<AllPermissionArray>>(){}.getType();
                    allPermissionArrays = gson.fromJson(gson.toJsonTree(responseData.getPermission()).toString(), type);
                    int data1 = CollectionsKt.indexOfFirst(allPermissionArrays, s -> s.getPostPermission().getName().equals("who_can_see_my_post"));
                    int data2 = CollectionsKt.indexOfFirst(allPermissionArrays, s -> s.getPostPermission().getName().equals("who_can_comment_on_my_post"));
                    int data3 = CollectionsKt.indexOfFirst(allPermissionArrays, s -> s.getPostPermission().getName().equals("who_can_like_my_post"));
                    int data4 = CollectionsKt.indexOfFirst(allPermissionArrays, s -> s.getPostPermission().getName().equals("who_can_save_my_post"));
                    int data5 = CollectionsKt.indexOfFirst(allPermissionArrays, s -> s.getPostPermission().getName().equals("who_can_download_my_post"));

                    RadioButton radioButton;
                    radioButton = (RadioButton) bi.rb.radioGroup.getChildAt(Integer.parseInt(data1 > -1 ? allPermissionArrays.get(data1).getPostPermission().getValue() : "0"));
                    radioButton.setChecked(true);
                    radioButton = (RadioButton) bi.rb1.radioGroup.getChildAt(Integer.parseInt(data2 > -1 ? allPermissionArrays.get(data2).getPostPermission().getValue() : "0"));
                    radioButton.setChecked(true);
                    radioButton = (RadioButton) bi.rb2.radioGroup.getChildAt(Integer.parseInt(data3 > -1 ? allPermissionArrays.get(data3).getPostPermission().getValue() : "0"));
                    radioButton.setChecked(true);
                    radioButton = (RadioButton) bi.rb3.radioGroup.getChildAt(Integer.parseInt(data4 > -1 ? allPermissionArrays.get(data4).getPostPermission().getValue() : "0"));
                    radioButton.setChecked(true);
                    radioButton = (RadioButton) bi.rb4.radioGroup.getChildAt(Integer.parseInt(data5 > -1 ? allPermissionArrays.get(data5).getPostPermission().getValue() : "0"));
                    radioButton.setChecked(true);

                    setListener();
                }catch (Exception e){
                    //UtilityHelperKt.toast(mActivity,e.toString());
                }
            });
        }
    }

    @Override
    public void onFailure(@NonNull String message) {
        UtilityHelperKt.hide(bi.progressCircular);
        UtilityHelperKt.toast(mActivity,message);
    }
    private void setListener() {
        bi.rb.radioGroup.setOnCheckedChangeListener(chnageListener);
        bi.rb1.radioGroup.setOnCheckedChangeListener(chnageListener);
        bi.rb2.radioGroup.setOnCheckedChangeListener(chnageListener);
        bi.rb3.radioGroup.setOnCheckedChangeListener(chnageListener);
        bi.rb4.radioGroup.setOnCheckedChangeListener(chnageListener);
    }
    private void removeListener(){
        bi.rb.radioGroup.setOnCheckedChangeListener(null);
        bi.rb1.radioGroup.setOnCheckedChangeListener(null);
        bi.rb2.radioGroup.setOnCheckedChangeListener(null);
        bi.rb3.radioGroup.setOnCheckedChangeListener(null);
        bi.rb4.radioGroup.setOnCheckedChangeListener(null);
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
                    body.put("userid",mainViewModel.getUser().getId());
                    body.put("name","who_can_see_my_post");
                    body.put("value",""+pos);
                    mainViewModel.updateSettings(body);
                    break;
                case 2:
                    body = new HashMap();
                    body.put("userid",mainViewModel.getUser().getId());
                    body.put("name","who_can_comment_on_my_post");
                    body.put("value",""+pos);
                    mainViewModel.updateSettings(body);
                    break;
                case 3:
                    body = new HashMap();
                    body.put("userid",mainViewModel.getUser().getId());
                    body.put("name","who_can_like_my_post");
                    body.put("value",""+pos);
                    mainViewModel.updateSettings(body);
                    break;
                case 4:
                    body = new HashMap();
                    body.put("userid",mainViewModel.getUser().getId());
                    body.put("name","who_can_save_my_post");
                    body.put("value",""+pos);
                    mainViewModel.updateSettings(body);
                    break;
                case 5:
                    body = new HashMap();
                    body.put("userid",mainViewModel.getUser().getId());
                    body.put("name","who_can_download_my_post");
                    body.put("value",""+pos);
                    mainViewModel.updateSettings(body);
                    break;
            }

        }
    };
}