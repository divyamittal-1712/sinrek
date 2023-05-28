package com.app.appsinrek.main.report;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.databinding.ViewDataBinding;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityReportBinding;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.fitness.modval.interfaces.AuthListener;

public class ReportActivity extends AppCompatActivity implements  View.OnClickListener {

    ActivityReportBinding bi;
    MainViewModel mainViewModel;
    Activity mActivity;
    String userid;
    String postid;
    String report_on;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mActivity = this;
        bi = DataBindingUtil.setContentView(this, R.layout.activity_report);
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

        bi.list1.setVisibility(report_on.equals("user")? View.VISIBLE:View.GONE);
        bi.list2.setVisibility(report_on.equals("post")? View.VISIBLE:View.GONE);
        for (int i = 0; i < bi.list1.getChildCount(); i++) {
            bi.list1.getChildAt(i).setOnClickListener(this);
        }
        for (int i = 0; i < bi.list2.getChildCount(); i++) {
            bi.list2.getChildAt(i).setOnClickListener(this);
        }
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onClick(View view) {
        startActivity(new Intent(mActivity,ReportFormActivity.class)
                .putExtra("report_on",report_on)
                .putExtra("subject",((TextView)view).getText().toString())
                .putExtra("userid",userid)
                .putExtra("postid",postid)
        );
        finish();
    }
}