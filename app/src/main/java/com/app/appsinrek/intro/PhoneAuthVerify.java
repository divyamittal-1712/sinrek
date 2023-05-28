package com.app.appsinrek.intro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityPhoneAuthVerifyBinding;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.phonenumberui.AppConstant;
import com.app.appsinrek.phonenumberui.utility.Utility;
import com.app.appsinrek.utils.AppConstants;
import com.app.appsinrek.utils.countrypicker.Country;
import com.app.appsinrek.viewmodels.AuthViewModel;
import com.fitness.modval.interfaces.AuthListener;

import in.aabhasjindal.otptextview.OTPListener;

public class PhoneAuthVerify extends AppCompatActivity implements AuthListener {
    ActivityPhoneAuthVerifyBinding bi;
    private Activity mActivity = this;
    private AuthViewModel authViewModel;
    boolean resend = false;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_phone_auth_verify);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.setAuthListener(this);
        bi.setLifecycleOwner(this);
        bi.setAuth(authViewModel);
        authViewModel.setOtp(getIntent().getStringExtra(AppConstant.PhoneOtp));
        authViewModel.setCountry(Country.getCountryByISO(getIntent().getStringExtra(AppConstant.PhoneCode)));
        authViewModel.setMobile(getIntent().getStringExtra(AppConstant.PhoneNumber));
        bi.mtbReset.setOnClickListener(view->{
            if(resend){
                resend = false;
                authViewModel.sendOtp(bi.mtbReset);
            }
        });
        bi.mtbSignIn.setOnClickListener(view->{
            if(bi.otpView.getOTP().length()<3){
                Toast.makeText(mActivity,"Please enter otp",Toast.LENGTH_LONG).show();
                return;
            }
            if(!bi.otpView.getOTP().equals(authViewModel.getOtp())){
                Toast.makeText(mActivity,"Wrong otp",Toast.LENGTH_LONG).show();
                return;
            }
            Intent intent = new Intent();
            intent.putExtra(AppConstant.PhoneOtp,authViewModel.getOtp());
            setResult(Activity.RESULT_OK,intent);
            finish();
        });
        bi.otpView.setOtpListener(new OTPListener() {
            @Override
            public void onInteractionListener() {
            }
            @Override
            public void onOTPComplete(String otp) {
                Utility.hideKeyBoardFromView(mActivity);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        startTimer();
    }

    @Override
    public void onStarted(@NonNull String tag) {
        bi.progressCircular.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if(tag.equals(AppConstants.TAG_SEND_OTP)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200) {
//                        Toast.makeText(this, responseData.getOtp().toString(), Toast.LENGTH_LONG).show();
                        authViewModel.setOtp(responseData.getOtp().toString());
                        startTimer();
                    } else {
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
    CountDownTimer cTimer = null;

    //start timer function
    void startTimer() {
        cTimer = new CountDownTimer(30000, 1000) {
            public void onTick(long millisUntilFinished) {
                bi.mtbReset.setText(String.format("%s 00: %d", getText(R.string.otp_resend), millisUntilFinished / 1000));
            }
            public void onFinish() {
                resend = true;
                bi.mtbReset.setText(getText(R.string.otp_resend));
            }
        };
        cTimer.start();
    }


    //cancel timer
    void cancelTimer() {
        if(cTimer!=null)
            cTimer.cancel();
    }

    @Override
    protected void onDestroy() {
        cancelTimer();
        super.onDestroy();
    }
}