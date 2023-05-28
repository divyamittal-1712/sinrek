package com.app.appsinrek.intro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityResetPasswordBinding;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.phonenumberui.AppConstant;
import com.app.appsinrek.phonenumberui.utility.Utility;
import com.app.appsinrek.utils.AppConstants;
import com.app.appsinrek.utils.countrypicker.Country;
import com.app.appsinrek.viewmodels.AuthViewModel;
import com.fitness.modval.interfaces.AuthListener;

import in.aabhasjindal.otptextview.OTPListener;

public class ResetPassword extends AppCompatActivity implements AuthListener {
    ActivityResetPasswordBinding bi;
    private AuthViewModel authViewModel;
    private Activity mActivity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_reset_password);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.setAuthListener(this);
        bi.setLifecycleOwner(this);
        bi.setAuth(authViewModel);
        authViewModel.setCountry(Country.getCountryByISO(getIntent().getStringExtra(AppConstant.PhoneCode)));
        authViewModel.setMobile(getIntent().getStringExtra(AppConstant.PhoneNumber));
        bi.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
            }
            @Override
            public void onOTPComplete(String otp) {
                authViewModel.setOtp(otp);
                Utility.hideKeyBoardFromView(mActivity);
            }
        });
    }

    @Override
    public void onStarted(@NonNull String tag) {
        bi.progressCircular.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if(tag.equals(AppConstants.TAG_RESET)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    Toast.makeText(this, responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    if (responseData.getCode() == 200) {
                        Intent intent;
                        intent = new Intent(getApplicationContext(), Welcome.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        startActivity(intent);
                        finishAffinity();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
        if(tag.equals(AppConstants.TAG_FORGOT)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() != 200) {
                        Toast.makeText(this, responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @Override
    public void onFailure(@NonNull String message) {
        bi.progressCircular.setVisibility(View.GONE);
        Toast.makeText(this,message,Toast.LENGTH_LONG).show();
    }
}