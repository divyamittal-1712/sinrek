package com.app.appsinrek.intro;

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
import com.app.appsinrek.databinding.ActivityLoginBinding;
import com.app.appsinrek.main.MainActivity;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.models.User;
import com.app.appsinrek.phonenumberui.utility.Utility;
import com.app.appsinrek.utils.AppConstants;
import com.app.appsinrek.utils.countrypicker.Country;
import com.app.appsinrek.utils.countrypicker.CountryPicker;
import com.app.appsinrek.viewmodels.AuthViewModel;
import com.fitness.modval.interfaces.AuthListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.Objects;

import io.michaelrocks.libphonenumber.android.PhoneNumberUtil;


public class Login extends AppCompatActivity implements AuthListener {
    ActivityLoginBinding bi;
    private AuthViewModel authViewModel;
    private PhoneNumberUtil mPhoneUtil;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_login);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        authViewModel.setAuthListener(this);
        bi.setLifecycleOwner(this);
        bi.setAuth(authViewModel);
        mPhoneUtil = PhoneNumberUtil.createInstance(this);
        bi.llCc.setOnClickListener(view -> {
            Utility.hideKeyBoardFromView(this);
            CountryPicker picker = CountryPicker.newInstance(this.getString(R.string.select_country));
            picker.setListener((name, code, dialCode, flagDrawableResID) -> {
                authViewModel.setCountry(Objects.requireNonNull(Country.getCountryByISO(code)));
                bi.etCountryCode.setText(dialCode);
                bi.flagImv.setText(flagDrawableResID);
                picker.dismiss();
            });
            picker.show(getSupportFragmentManager(), "Country Picker");
        });
        bi.btnForgot.setOnClickListener(view->{
            Intent intent = new Intent(this,PhoneAuth.class);
            intent.putExtra("flag","forgot");
            startActivity(intent);
        });
    }

    @Override
    public void onStarted(@NonNull String tag) {
        bi.progressCircular.setVisibility(View.VISIBLE);
    }
    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if(tag.equals(AppConstants.TAG_LOGIN)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<User>() {
                        }.getType();
                        JsonObject js = gson.toJsonTree(responseData.getMsg()).getAsJsonObject();
                        JSONObject user_js = new JSONObject(js.toString());
                        String api_key = user_js.getString("api_key");
                        User model = gson.fromJson(gson.toJsonTree(responseData.getMsg()).getAsJsonObject().getAsJsonObject("User").toString(), type);
                        authViewModel.getStorage().writeUserInfo(model);
                        authViewModel.getStorage().writeUserAuthToken(api_key);
                        Toast.makeText(this, "Login Success", Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                        startActivity(intent);
                        finishAffinity();

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