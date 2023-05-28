package com.app.appsinrek.intro;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivitySplashBinding;
import com.app.appsinrek.fcm.services.MyFcmListenerService;
import com.app.appsinrek.main.MainActivity;
import com.app.appsinrek.viewmodels.AuthViewModel;

public class Splash extends AppCompatActivity {
    ActivitySplashBinding bi;
    private AuthViewModel authViewModel;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bi = DataBindingUtil.setContentView(this, R.layout.activity_splash);
        authViewModel = new ViewModelProvider(this).get(AuthViewModel.class);
        MyFcmListenerService.getCurrentToken();
        bi.clickWelcome.setOnClickListener(view -> {
         Intent intent = new Intent(this,Welcome.class);
         startActivity(intent);
        });
        new Handler().postDelayed(() -> {
            if(authViewModel.getStorage().readUserInfo()!= null){
                Intent intent;
                intent = new Intent(getApplicationContext(), MainActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }else {
                Intent intent;
                intent = new Intent(getApplicationContext(), Welcome.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(intent);
                finish();
            }
        }, 2000);
    }
}