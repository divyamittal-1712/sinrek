package com.app.appsinrek.main.settings;

import static com.app.appsinrek.utils.UtilityHelperKt.toast;

import android.app.Activity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityChangePasswordBinding;
import com.app.appsinrek.databinding.ActivitySettingBinding;
import com.app.appsinrek.main.search.model.SearchModel;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.utils.AppConstants;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.fitness.modval.interfaces.AuthListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class ChangePasswordSettingActivity extends AppCompatActivity implements AuthListener {
    ActivityChangePasswordBinding bi;
    Activity mActivity = this;
    MainViewModel mainViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setMainListener(this);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_change_password);
        setSupportActionBar(bi.toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        bi.mtbSignIn.setOnClickListener(view -> {
            if(bi.passwrodEditText.getText().toString().isEmpty()){
                UtilityHelperKt.toast(this,"Please enter password");
            }else if(bi.passwrodEditText.getText().toString().length()<8 || bi.passwrodEditText.getText().toString().length()>15){
                UtilityHelperKt.toast(this,"Password must have at least 8 characters and maximum of 15 characters");
            }else if(!bi.passwrodEditText.getText().toString().equals(bi.confirmPasswrodEditText.getText().toString())){
                UtilityHelperKt.toast(this,"Password not match");
            }else{
                mainViewModel.changePassword(bi.passwrodEditText.getText().toString());
            }
        });
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
        bi.progressCircular.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if(tag.equals(AppConstants.TAG_CHANGE_PASS)){
            apiResponse.observe(this,responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    if (responseData.getCode() == 200) {
                        UtilityHelperKt.toast(mActivity,responseData.getMsg().toString());
                        finish();
                    } else {
                        UtilityHelperKt.toast(mActivity,responseData.getMsg().toString());
                    }
                }catch (Exception e){
//                    UtilityHelperKt.toast(mActivity,e.toString());
                }
            });
        }
    }

    @Override
    public void onFailure(@NonNull String message) {
        bi.progressCircular.setVisibility(View.GONE);
        toast(this,message);
    }
}