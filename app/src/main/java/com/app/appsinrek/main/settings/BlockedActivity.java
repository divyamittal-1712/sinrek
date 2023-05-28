package com.app.appsinrek.main.settings;

import static com.app.appsinrek.utils.AppConstants.TAG_USER_BLOCK;

import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityBlockedusersBinding;
import com.app.appsinrek.main.settings.adapter.BlockedUsersAdapter;
import com.app.appsinrek.main.settings.models.BlockedUsers;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.models.User;
import com.app.appsinrek.utils.AppConstants;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.fitness.modval.interfaces.AuthListener;
import com.google.common.reflect.TypeToken;
import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kotlin.collections.CollectionsKt;

public class BlockedActivity extends AppCompatActivity implements AuthListener {
    ActivityBlockedusersBinding bi;
    AppCompatActivity mActivity;
    ArrayList<BlockedUsers> listData = new ArrayList();
    List<BlockedUsers> mainData = new ArrayList();
    MainViewModel mainViewModel;
    private BlockedUsersAdapter adapter;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setMainListener(this);
        mActivity = this;
        bi = DataBindingUtil.setContentView(this, R.layout.activity_blockedusers);
        mActivity.setSupportActionBar(bi.toolbar);
        if (mActivity.getSupportActionBar() != null) {
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        bi.tilSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if (bi.tilSearch.getText().toString().isEmpty()) {
                    bi.ivSearch.setImageResource(R.drawable.search);
                } else {
                    bi.ivSearch.setImageResource(R.drawable.ic_baseline_close_24);
                }
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        adapter = new BlockedUsersAdapter(mActivity, view -> {
            int id = view.getId();
            User user = (User) view.getTag();
            switch (id) {
                case R.id.unblock_user:
                    new AlertDialog.Builder(this)
                            .setTitle("Unblock Account...!")
                            .setMessage("Want to Unblock this account?")
                            .setPositiveButton("Yes", (dialogInterface, i) -> {
                                HashMap<Object, Object> body = new HashMap();
                                body.put("userid", mainViewModel.getUser().getId());
                                body.put("blockeduserid", user.getId());
                                mainViewModel.userUnBlock(body);
                                dialogInterface.dismiss();
                            })
                            .setNegativeButton("No", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            })
                            .create()
                            .show();
                    break;
                case R.id.profile_image:
                case R.id.username:
//                    if (mainViewModel.getUser().getId().equals(UtilityHelperKt.getText(user.getId()))) {
//                        ((MainActivity)requireActivity()).setCurrentFragment(new ProfileFragment(),"");
//                        return;
//                    }
//                    if(UtilityHelperKt.getText(user.getId()).isEmpty()){
//                        UtilityHelperKt.toast(requireActivity(),"User Data Empty");
//                        break;
//                    }
//                    Fragment fragment = new ProfileOtherFragment();
//                    Bundle bu = new Bundle();
//                    bu.putString("userid",user.getId());
//                    fragment.setArguments(bu);
//                    ((MainActivity)requireActivity()).setCurrentFragment(fragment,"");
                    break;

            }

        }, listData, mainViewModel);
        bi.recycler.setAdapter(adapter);
        bi.progressCircular.setVisibility(View.VISIBLE);
        mainViewModel.getBlockedUserList();
    }

    @Override
    public void onStarted(@NonNull String tag) {

    }

    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if (tag.equals(AppConstants.TAG_BLOCKED_USERS)) {
            apiResponse.observe(this, responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    listData.clear();
                    mainData.clear();
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<BlockedUsers>>() {
                        }.getType();
                        List<BlockedUsers> list = gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type);
                        List<BlockedUsers> newList = CollectionsKt.filter(list, s -> (s.getUser().getId() != null));
                        listData.addAll(newList);
                        mainData.addAll(newList);
                        adapter.notifyDataSetChanged();
                    } else {
//                        UtilityHelperKt.toast(mActivity,responseData.getMsg().toString());
                    }
                } catch (Exception e) {
                    UtilityHelperKt.toast(mActivity, e.toString());
                }
            });
        }
        else if (tag.equals(TAG_USER_BLOCK)) {
            apiResponse.observe(this, responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    if (responseData.getCode() == 200) {
                        bi.progressCircular.setVisibility(View.VISIBLE);
                        mainViewModel.getBlockedUserList();
                        Toast.makeText(this, responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
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
        UtilityHelperKt.hide(bi.progressCircular);
    }

    void filter(String text) {
        listData.clear();
        if (text.isEmpty()) {
            listData.addAll(mainData);
            adapter.notifyDataSetChanged();
            return;
        }
        List<BlockedUsers> temp = new ArrayList();
        for (BlockedUsers d : mainData) {
            if (d.getUser().getUsername().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))) {
                temp.add(d);
            }
        }
        listData.addAll(temp);
        adapter.notifyDataSetChanged();
    }
}