package com.app.appsinrek.main.settings;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivitySettingBinding;
import com.app.appsinrek.intro.Welcome;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.utils.AppConstants;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.fitness.modval.interfaces.AuthListener;

public class SettingActivity extends AppCompatActivity implements View.OnClickListener, AuthListener {
    ActivitySettingBinding bi;
    Activity mActivity = this;
    MainViewModel mainViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setMainListener(this);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_setting);
        setSupportActionBar(bi.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        clickListener(new View[]{bi.llPost, bi.llProfilePrivacy, bi.llNotification, bi.llBlockedUsers, bi.llAbout, bi.llReset, bi.llDelete});
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    void clickListener(View[] arrView) {
        for (View view : arrView) {
            view.setOnClickListener(this);
        }
    }

    @SuppressLint("NonConstantResourceId")
    @Override
    public void onClick(View view) {
        Intent intent;
        switch (view.getId()) {
            case R.id.llPost:
                intent = new Intent(mActivity, PostSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.llProfilePrivacy:
                intent = new Intent(mActivity, PrivacySettingActivity.class);
                startActivity(intent);
                break;
            case R.id.llNotification:
                intent = new Intent(mActivity, NotificationSettingActivity.class);
                startActivity(intent);
                break;
            case R.id.llBlockedUsers:
                intent = new Intent(mActivity, BlockedActivity.class);
                startActivity(intent);
                break;
//            case R.id.llChangePassword:
//                intent = new Intent(mActivity, ChangePasswordSettingActivity.class);
//                startActivity(intent);
//                break;
            case R.id.llAbout:
                intent = new Intent(mActivity, AboutSettingActivity.class);
                startActivity(intent);
                break;
//            case R.id.llInvite:
//                break;
//            case R.id.llReset:
//                break;
//            case R.id.llOnline:
//                break;
            case R.id.llDelete:
                new AlertDialog.Builder(this)
                        .setTitle("Delete Account...!")
                        .setMessage("Do you want delete this post?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            mainViewModel.deleteAccount();
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .create()
                        .show();
                break;
        }
    }

    @Override
    public void onStarted(@NonNull String tag) {
        UtilityHelperKt.show(bi.progressCircular);
    }

    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if (tag.equals(AppConstants.TAG_DELETE_ACCOUNT)) {
            apiResponse.observe(this, responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    if (responseData.getCode() == 200) {
                        mainViewModel.getStorage().clear();
                        UtilityHelperKt.toast(mActivity, responseData.getMsg().toString());
                        startActivity(new Intent(this, Welcome.class).setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK));
                        finishAffinity();
                    } else {
                        UtilityHelperKt.toast(mActivity, responseData.getMsg().toString());
                    }
                } catch (Exception e) {
                    UtilityHelperKt.toast(mActivity, e.toString());
                }
            });
        }
    }

    @Override
    public void onFailure(@NonNull String message) {
        UtilityHelperKt.hide(bi.progressCircular);
        UtilityHelperKt.toast(this, message);
    }
}