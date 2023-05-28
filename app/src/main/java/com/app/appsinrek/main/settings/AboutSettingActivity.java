package com.app.appsinrek.main.settings;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.LinearLayoutCompat;
import androidx.databinding.DataBindingUtil;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityAboutSettingBinding;
import com.app.appsinrek.databinding.ActivitySettingBinding;
import com.app.appsinrek.utils.AppWebView;

public class AboutSettingActivity extends AppCompatActivity {
    ActivityAboutSettingBinding bi;
    Activity mActivity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_about_setting);
        setSupportActionBar(bi.toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        LinearLayoutCompat privacyPolicy = findViewById(R.id.privacyPolicy);
        privacyPolicy.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AppWebView.class);
                intent.putExtra("url","http://sinrek.in/page/privacy-policy");
                intent.putExtra("title","Privacy Policy");
                startActivity(intent);
            }
        });
        LinearLayoutCompat termConditions = findViewById(R.id.termConditions);
        termConditions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AppWebView.class);
                intent.putExtra("url","http://sinrek.in/page/terms-services");
                intent.putExtra("title","Terms and Conditions");
                startActivity(intent);
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
}