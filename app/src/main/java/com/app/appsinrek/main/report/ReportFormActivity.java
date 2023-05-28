package com.app.appsinrek.main.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityReportBinding;
import com.app.appsinrek.databinding.ActivityReportFormBinding;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.fitness.modval.interfaces.AuthListener;

import java.util.HashMap;

public class ReportFormActivity extends AppCompatActivity implements AuthListener {

    ActivityReportFormBinding bi;
    MainViewModel mainViewModel;
    Activity mActivity;
    String subject;
    String userid;
    String postid;
    String report_on;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setMainListener(this);
        mActivity = this;
        bi = DataBindingUtil.setContentView(this, R.layout.activity_report_form);
        setSupportActionBar(bi.toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        Intent intent = getIntent();
        if(intent.hasExtra("report_on"))
            report_on = getIntent().getStringExtra("report_on");
        if(intent.hasExtra("userid"))
            userid = getIntent().getStringExtra("userid");
        if(intent.hasExtra("postid"))
            postid = getIntent().getStringExtra("postid");
        if(intent.hasExtra("subject"))
            subject = getIntent().getStringExtra("subject");

        bi.sendReport.setOnClickListener(view -> {
            if(bi.tilReasion.getText().toString().isEmpty()){
                UtilityHelperKt.toast(mActivity, "Message empty");
                return;
            }
            if(report_on.equals("user")){
                HashMap<Object,Object> body = new HashMap();
                body.put("user_id",userid);
                body.put("subject",subject);
                body.put("message",bi.tilReasion.getText().toString());
                mainViewModel.userReport(body);
            }else{
                HashMap<Object,Object> body = new HashMap();
                body.put("post_user_id",userid);
                body.put("post_id",postid);
                body.put("subject",subject);
                body.put("message",bi.tilReasion.getText().toString());
                mainViewModel.postReport(body);
            }
        });
        bi.sendReportDirect.setOnClickListener(view -> {
            if(report_on.equals("user")){
                HashMap<Object,Object> body = new HashMap();
                body.put("user_id",userid);
                body.put("subject",subject);
                body.put("message",bi.tilReasion.getText().toString());
                mainViewModel.userReport(body);
            }else{
                HashMap<Object,Object> body = new HashMap();
                body.put("post_user_id",userid);
                body.put("post_id",postid);
                body.put("subject",subject);
                body.put("message",bi.tilReasion.getText().toString());
                mainViewModel.postReport(body);
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
        apiResponse.observe(this,responseData -> {
            bi.progressCircular.setVisibility(View.GONE);
            Toast.makeText(mActivity, responseData.getMsg().toString(), Toast.LENGTH_SHORT).show();
            finish();
        });
    }

    @Override
    public void onFailure(@NonNull String message) {
        bi.progressCircular.setVisibility(View.GONE);
        Toast.makeText(mActivity, message, Toast.LENGTH_SHORT).show();
    }
}