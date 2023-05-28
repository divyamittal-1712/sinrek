package com.app.appsinrek.intro;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityPhoneAuthBinding;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.phonenumberui.AppConstant;
import com.app.appsinrek.phonenumberui.utility.Utility;
import com.app.appsinrek.utils.AppConstants;
import com.app.appsinrek.utils.CommonUtils;
import com.app.appsinrek.utils.countrypicker.Country;
import com.app.appsinrek.utils.countrypicker.CountryPicker;
import com.app.appsinrek.viewmodels.AuthViewModel;
import com.fitness.modval.interfaces.AuthListener;

import java.util.Objects;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;

public class PhoneAuth extends AppCompatActivity implements AuthListener {
    ActivityPhoneAuthBinding bi;
    private PhoneNumberUtil mPhoneUtil;
    private static final int COUNTRYCODE_ACTION = 1;
    private static final int VERIFICATION_ACTION = 2;
    private final Activity mActivity = this;
    private String flag;
    private String otp;
    private AuthViewModel authViewModel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_phone_auth);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.setAuthListener(this);
        bi.setLifecycleOwner(this);
        bi.setAuth(authViewModel);
        flag = getIntent().getStringExtra("flag");
        if(flag.equals("register")){
            bi.title.setText(R.string.phone_auth_register_title);
        }else{
            bi.title.setText(R.string.phone_auth_forgot_title);
        }
        bi.mtbSignIn.setOnClickListener(view -> {
            if(flag.equals("register")){
                authViewModel.sendOtp(bi.mtbSignIn);
            }else{
                authViewModel.forgotPassword(bi.mtbSignIn);
            }
        });
        setUpUI();
    }
    ActivityResultLauncher<Intent> activityResultLauncher = registerForActivityResult(
                        new ActivityResultContracts.StartActivityForResult(),
    result -> {
        if (result.getResultCode() == Activity.RESULT_OK) {
            assert result.getData() != null;
            authViewModel.setOtp(result.getData().getStringExtra(AppConstant.PhoneOtp));
            Intent intent;
            if(flag.equals("register")) {
                intent = new Intent(mActivity, Register.class);
                intent.putExtra(AppConstant.PhoneCode, authViewModel.getCountry().getCode());
                intent.putExtra(AppConstant.PhoneOtp, authViewModel.getOtp());
                intent.putExtra(AppConstant.PhoneNumber, bi.etPhoneNumber.getText().toString());
                startActivity(intent);
            }
        }
    });
    private void setUpUI() {

        mPhoneUtil = PhoneNumberUtil.createInstance(mActivity);

//        bi.etPhoneNumber.setHint(setPhoneNumberHint(authViewModel.getCountry(),mPhoneUtil));

        bi.etCountryCode.setOnClickListener(view -> {
            Utility.hideKeyBoardFromView(mActivity);
            CountryPicker picker = CountryPicker.newInstance(mActivity.getString(R.string.select_country));
            picker.setListener((name, code, dialCode, flagDrawableResID) -> {
                authViewModel.setCountry(Objects.requireNonNull(Country.getCountryByISO(code)));
                bi.etCountryCode.setText(dialCode);
                bi.flagImv.setText(flagDrawableResID);
//                bi.etPhoneNumber.setHint(setPhoneNumberHint(authViewModel.getCountry(),mPhoneUtil));
                picker.dismiss();
            });
            picker.show(getSupportFragmentManager(), "Country Picker");
        });
        bi.mtbSignIn.setOnClickListener(view->{
            bi.etPhoneNumber.setError(null);
            if (!validate()) return;
            if(flag.equals("register")) {
                authViewModel.sendOtp(view);
            }else {
                authViewModel.forgotPassword(view);
            }

        });

        if (getIntent().getExtras() != null) {
            if (getIntent().hasExtra("PHONE_NUMBER")) {
                bi.etPhoneNumber.setText(getIntent().getStringExtra("PHONE_NUMBER"));
                bi.etPhoneNumber.setSelection(bi.etPhoneNumber.getText().toString().trim().length());
            }
        }
    }
    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return true;
    }


    private boolean validate() {
        if (TextUtils.isEmpty(bi.etPhoneNumber.getText().toString().trim())) {
            bi.etPhoneNumber.setError("Please enter phone number");
            bi.etPhoneNumber.requestFocus();
            return false;
        } else if (!CommonUtils.isValid(bi.etPhoneNumber.getText().toString(),authViewModel.getCountry(),mPhoneUtil)) {
            bi.etPhoneNumber.setError("Please enter valid phone number");
            bi.etPhoneNumber.requestFocus();
            return false;
        }
        return true;
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
                        otp = responseData.getOtp().toString();
//                        Toast.makeText(this, responseData.getOtp().toString(), Toast.LENGTH_LONG).show();
                        Intent intent;
                        intent = new Intent(mActivity, PhoneAuthVerify.class);
                        intent.putExtra(AppConstant.PhoneNumber, bi.etPhoneNumber.getText().toString().trim());
                        intent.putExtra(AppConstant.PhoneCode, authViewModel.getCountry().getCode());
                        intent.putExtra(AppConstant.PhoneOtp, otp);
                        activityResultLauncher.launch(intent);

                    } else {
                        Toast.makeText(this, responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
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
                    if (responseData.getCode() == 200) {
                        Intent intent;
                        intent = new Intent(mActivity, ResetPassword.class);
                        intent.putExtra(AppConstant.PhoneCode,authViewModel.getCountry().getCode());
                        intent.putExtra(AppConstant.PhoneNumber,bi.etPhoneNumber.getText().toString());
                        startActivity(intent);

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
}