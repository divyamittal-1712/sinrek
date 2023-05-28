package com.app.appsinrek.main.dashboard;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityLikedUsersBinding;
import com.app.appsinrek.main.MainActivity;
import com.app.appsinrek.main.dashboard.adapter.LikedUsersAdapter;
import com.app.appsinrek.main.dashboard.model.LikedUsersModel;
import com.app.appsinrek.main.profile.ProfileFragment;
import com.app.appsinrek.main.profile_other.ProfileOtherFragment;
import com.app.appsinrek.models.Other;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.models.User;
import com.app.appsinrek.utils.AppConstants;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.fitness.modval.interfaces.AuthListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

public class LikedUsersFragment extends Fragment implements AuthListener {

    ActivityLikedUsersBinding bi;
    AppCompatActivity mActivity;
    MainViewModel mainViewModel;
    LikedUsersAdapter adapter;
    ArrayList<LikedUsersModel> likedUsersData = new ArrayList();
    int position;
    private String postid;

    @Override
    synchronized
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setMainListener(this);
        bi = DataBindingUtil.inflate(inflater, R.layout.activity_liked_users, container, false);
        mActivity = ((AppCompatActivity) getActivity());
        mActivity.setSupportActionBar(bi.toolbar);

        if (mActivity.getSupportActionBar() != null){
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        postid = getArguments().getString("post_id");
        adapter = new LikedUsersAdapter(requireActivity(), view -> {
            int id = view.getId();
            User user;
            switch (id){
                case R.id.btnFollow:
                    position = (int) view.getTag();
                    if(UtilityHelperKt.getText(likedUsersData.get(position).getUser().getId()).isEmpty()){
                        UtilityHelperKt.toast(requireActivity(),"User Data Empty");
                        break;
                    }
                    user = likedUsersData.get(position).getUser();
                    Other other = likedUsersData.get(position).getOther();
                    if(other.getButton().equals("Following")) {
                        new AlertDialog.Builder(requireActivity())
                                .setTitle("Unfollow")
                                .setMessage("Do you want to  unfollow this account?")
                                .setPositiveButton("Yes", (dialogInterface, i) -> {
                                    mainViewModel.unfollow(user.getId());
                                    dialogInterface.dismiss();
                                })
                                .setNegativeButton("No",(dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                })
                                .create()
                                .show();

                    }else if(other.getButton().equals("Requested")){
                    }else{
                        mainViewModel.sendFollowRequest(user.getId());
                    }
                    break;
                case R.id.profile_image:
                case R.id.username:
                    user = (User) view.getTag();
                    if (mainViewModel.getUser().getId().equals(UtilityHelperKt.getText(user.getId()))) {
                        ((MainActivity)requireActivity()).setCurrentFragment(new ProfileFragment(),"");
                        return;
                    }
                    if(UtilityHelperKt.getText(user.getId()).isEmpty()){
                        UtilityHelperKt.toast(requireActivity(),"User Data Empty");
                        break;
                    }
                    Fragment fragment = new ProfileOtherFragment();
                    Bundle bu = new Bundle();
                    bu.putString("userid",user.getId());
                    fragment.setArguments(bu);
                    ((MainActivity)requireActivity()).setCurrentFragment(fragment,"");
                    break;
            }
        }, likedUsersData,mainViewModel.getUser().getId());
        bi.recycler.setAdapter(adapter);
        return bi.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        mainViewModel.postLikedUsers(postid);
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
//            ((MainActivity)getActivity()).setCurrentFragment(new DashboardFragment(),"home");
            ((MainActivity)requireActivity()).setBottomSheet(0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStarted(@NonNull String tag) {
        UtilityHelperKt.show(bi.progressCircular);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if(tag.equals(AppConstants.TAG_LIKE_USERS)){
            apiResponse.observe(this,responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    likedUsersData.clear();
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<LikedUsersModel>>(){}.getType();

                        List<LikedUsersModel> list = gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type);
                        likedUsersData.addAll(list);
                        adapter.notifyDataSetChanged();
                    } else {
                        UtilityHelperKt.toast(mActivity,responseData.getMsg().toString());
                    }
                }catch (Exception e){
                    UtilityHelperKt.toast(mActivity,e.toString());
                }
            });
        }else if(tag.equals(AppConstants.TAG_FOLLOW)){
            apiResponse.observe(this,responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    if (responseData.getCode() == 200) {
                        mainViewModel.postLikedUsers(postid);
                    } else {
                        UtilityHelperKt.toast(mActivity,responseData.getMsg().toString());
                    }
                }catch (Exception e){
                    UtilityHelperKt.toast(mActivity,e.toString());
                }
            });
        }else if(tag.equals(AppConstants.TAG_UNFOLLOW)){
            apiResponse.observe(this,responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    if (responseData.getCode() == 200) {
                        mainViewModel.postLikedUsers(postid);
                    } else {
                        UtilityHelperKt.toast(mActivity,responseData.getMsg().toString());
                    }
                }catch (Exception e){
                    UtilityHelperKt.toast(mActivity,e.toString());
                }
            });
        }

    }

    @Override
    public void onFailure(@NonNull String message) {
        UtilityHelperKt.hide(bi.progressCircular);
        UtilityHelperKt.toast(mActivity,message);
    }
}