package com.app.appsinrek.main;

import static com.app.appsinrek.utils.AppConstants.NOTIFICATIONS_COUNT;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityMainBinding;
import com.app.appsinrek.main.activity_request.ActivityRequestFragment;
import com.app.appsinrek.main.dashboard.DashboardFragment;
import com.app.appsinrek.main.notification.NotificationsFragment;
import com.app.appsinrek.main.post.AddPhotosFragment;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.fitness.modval.interfaces.AuthListener;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationMenuView;
import com.google.gson.Gson;
import com.google.gson.JsonObject;

import org.json.JSONObject;


public class MainActivity extends AppCompatActivity implements AuthListener {
    ActivityMainBinding bi;
    private MainViewModel mainViewModel;

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setMainListener(this);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_main);
        setCurrentFragment(new DashboardFragment(), "home");
        bi.bottomNavigationMain.setOnItemSelectedListener(item -> {
//            item.setChecked(true);
            switch (item.getItemId()) {
                case R.id.nav_dashboard:
                    setCurrentFragment(new DashboardFragment(), "home");
                    break;
                case R.id.nav_add:
                    setCurrentFragment(new AddPhotosFragment(), "home");
                    break;
                case R.id.nav_profile:
                    setCurrentFragment(new ActivityRequestFragment(), "home");
                    break;
                case R.id.nav_notification:
                    setCurrentFragment(new NotificationsFragment(), "home");
                    break;
            }
            return true;
        });
        mainViewModel.getNotificationCount();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        return false;
    }

    @Override
    public void onBackPressed() {
        FragmentManager fm = getSupportFragmentManager();
        if (fm.getBackStackEntryCount() == 1) {
            finish();
            return;
        }
        super.onBackPressed();

    }

    public void setCurrentFragment(Fragment fragment, String tag) {
        FragmentManager fm = getSupportFragmentManager();
        FragmentTransaction ft = fm.beginTransaction();
        if (tag.equals("home")) {
            ft.add(R.id.content, fragment);
            fm.popBackStack("home", FragmentManager.POP_BACK_STACK_INCLUSIVE);
            ft.addToBackStack("home");
        } else {
            ft.replace(R.id.content, fragment);
            ft.addToBackStack(null);
        }

        ft.commit();
    }

    public void setBottomSheet(int i) {
        bi.bottomNavigationMain.setSelectedItemId(R.id.nav_dashboard);
    }

    private void addBadgeView(int count) {
        try {
            BottomNavigationMenuView menuView = (BottomNavigationMenuView) bi.bottomNavigationMain.getChildAt(0);
            BottomNavigationItemView itemView = (BottomNavigationItemView) menuView.getChildAt(3); //set this to 0, 1, 2, or 3.. accordingly which menu item of the bottom bar you want to show badge
            View notificationBadge = LayoutInflater.from(MainActivity.this).inflate(R.layout.notification_badge, menuView, false);
            @SuppressLint({"MissingInflatedId", "LocalSuppress"}) TextView tv = (TextView) notificationBadge.findViewById(R.id.notifications_badge);
            if (count > 99) {
                tv.setText("99+");
            } else
                tv.setText(String.valueOf(count));
            itemView.addView(notificationBadge);
            if (count > 0)
                notificationBadge.setVisibility(View.VISIBLE);
            else
                notificationBadge.setVisibility(View.GONE);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public BroadcastReceiver myReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getStringExtra("action");
            if (action.contentEquals("foreground_notificator"))
                mainViewModel.getNotificationCount();
        }
    };

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(myReceiver, new IntentFilter("FBR-IMAGE"));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(myReceiver);
    }

    @Override
    public void onStarted(@NonNull String tag) {

    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if (tag.equals(NOTIFICATIONS_COUNT)) {
            apiResponse.observe(this, responseData -> {
                try {
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        JsonObject js = gson.toJsonTree(responseData.getMsg()).getAsJsonObject();
                        JSONObject countJson = new JSONObject(js.toString());
                        int count = countJson.getInt("notificationcount");
                        addBadgeView(count);
                    }
                } catch (Exception e) {
//                    Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onFailure(@NonNull String message) {

    }
}