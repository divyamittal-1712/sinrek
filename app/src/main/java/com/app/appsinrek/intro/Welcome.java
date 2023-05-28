package com.app.appsinrek.intro;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.os.Build;
import android.os.Bundle;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;

import com.app.appsinrek.databinding.ActivityWelcomeBinding;

import java.util.Locale;

public class Welcome extends AppCompatActivity {
    ActivityWelcomeBinding bi;
    private ActivityResultLauncher<Intent> selectLanguageLauncher;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        String pref = getSavedLanguage();
        if(!pref.isEmpty()){
            Locale locale = new Locale(pref);
            Locale.setDefault(locale);
            Configuration config = new Configuration();
            config.locale = locale;
            getBaseContext().getResources().updateConfiguration(config,
                    getBaseContext().getResources().getDisplayMetrics());
        }
        bi = ActivityWelcomeBinding.inflate(getLayoutInflater());
        setContentView(bi.getRoot());
        bi.mtbSignIn.setOnClickListener(view->{
            Intent intent = new Intent(this,Login.class);
            startActivity(intent);
        });
        bi.mtbSignUp.setOnClickListener(view->{
            Intent intent = new Intent(this,PhoneAuth.class);
            intent.putExtra("flag","register");
            startActivity(intent);
        });
        selectLanguageLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        String pref_1 = getSavedLanguage();
                        Locale locale = new Locale(pref_1);
                        Locale.setDefault(locale);
                        Configuration config = new Configuration();
                        if (Build.VERSION.SDK_INT >= 24) {
                            config.setLocale(locale);
                            getBaseContext().getResources().updateConfiguration(config, getBaseContext().getResources().getDisplayMetrics());
                        } else {
                            config.locale = locale;
                            getBaseContext().getApplicationContext().createConfigurationContext(config);
                        }
                        Intent intent = getIntent();
                        finish();
                        startActivity(intent);
                    }
                }
        );
        bi.selectLanguage.setOnClickListener(view -> {
            selectLanguageLauncher.launch(new Intent(this, Languages.class));
        });
    }

    public String getSavedLanguage() {
        SharedPreferences sharedPreferences = getSharedPreferences("Settings", MODE_PRIVATE);
        return sharedPreferences.getString("lang", "en");
    }

}