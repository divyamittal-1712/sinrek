package com.app.appsinrek.main.profile;

import static com.app.appsinrek.utils.AppConstants.TAG_CANCEL_REQUEST;
import static com.app.appsinrek.utils.AppConstants.TAG_FOLLOW;
import static com.app.appsinrek.utils.AppConstants.TAG_UNFOLLOW;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityFollowingBinding;
import com.app.appsinrek.main.MainActivity;
import com.app.appsinrek.main.profile.adapter.FollowingAdapter;
import com.app.appsinrek.main.profile.model.Following;
import com.app.appsinrek.main.profile_other.ProfileOtherFragment;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.models.User;
import com.app.appsinrek.utils.AppConstants;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.fitness.modval.interfaces.AuthListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import kotlin.collections.CollectionsKt;

public class FollowingActivity extends Fragment implements AuthListener {

    ActivityFollowingBinding bi;
    AppCompatActivity mActivity;
    ArrayList<Following> listData = new ArrayList();
    List<Following> mainData = new ArrayList();
    MainViewModel mainViewModel;
    private FollowingAdapter adapter;
    private String userid;

    @SuppressLint("NonConstantResourceId")
    @Override
    synchronized
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setMainListener(this);
        mActivity = ((AppCompatActivity) requireActivity());
        bi = DataBindingUtil.inflate(inflater, R.layout.activity_following, container, false);
        mActivity.setSupportActionBar(bi.toolbar);
        if (mActivity.getSupportActionBar() != null){
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        bi.tilSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {}

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                if(bi.tilSearch.getText().toString().isEmpty()){
                    bi.ivSearch.setImageResource(R.drawable.search);
                }else{
                    bi.ivSearch.setImageResource(R.drawable.ic_baseline_close_24);
                }
                filter(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });
        userid = getArguments().getString("userid");
        adapter = new FollowingAdapter(mActivity, view -> {
            int id = view.getId();
            User user = (User) view.getTag();
            switch (id){
                case R.id.materialButton:
                    if(user.getIsFollowing().equals(mActivity.getResources().getString(R.string.following))) {
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
                    }else if(user.getIsFollowing().equals(mActivity.getResources().getString(R.string.requested))) {
                        new AlertDialog.Builder(requireActivity())
                                .setTitle("Cancel Request")
                                .setMessage("Do you want to  cancel request?")
                                .setPositiveButton("Yes", (dialogInterface, i) -> {
                                    mainViewModel.cancelRequest(user.getId());
                                    dialogInterface.dismiss();
                                })
                                .setNegativeButton("No", (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                })
                                .create()
                                .show();
                    }else{
                        mainViewModel.sendFollowRequest(user.getId());
                    }
                    break;
                case R.id.linearLayoutCompat:
                case R.id.profile_image:
                case R.id.username:
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

        },listData,mainViewModel);
        bi.recycler.setAdapter(adapter);
        return bi.getRoot();
    }
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            getParentFragmentManager().popBackStack();
        }
        return super.onOptionsItemSelected(item);
    }
    @Override
    public void onStart() {
        super.onStart();
        mainViewModel.getFollowingList(userid);
    }
    @Override
    public void onStarted(@NonNull String tag) {
        UtilityHelperKt.show(bi.progressCircular);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if(tag.equals(AppConstants.TAG_SHOW_ALL_FOLLOWING)){
            apiResponse.observe(this,responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    listData.clear();
                    mainData.clear();
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<Following>>(){}.getType();
                        List<Following> list = gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type);
                        List<Following> newList = CollectionsKt.filter(list, s -> (s.getUser().getId() != null));
                        listData.addAll(newList);
                        mainData.addAll(newList);
                        adapter.notifyDataSetChanged();
                    } else {
//                        UtilityHelperKt.toast(mActivity,responseData.getMsg().toString());
                    }
                }catch (Exception e){
                    UtilityHelperKt.toast(mActivity,e.toString());
                }
            });
        }else if(tag.equals(TAG_FOLLOW)){
            apiResponse.observe(this,responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    if (responseData.getCode() == 200) {
                        Toast.makeText(getActivity(), "Request Send", Toast.LENGTH_LONG).show();
                        mainViewModel.getFollowingList(userid);
                    } else {
                        Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }else if(tag.equals(TAG_UNFOLLOW)){
            apiResponse.observe(this,responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    if (responseData.getCode() == 200) {
                        mainViewModel.getFollowingList(userid);
                    } else {
                        Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }else if(tag.equals(TAG_CANCEL_REQUEST)) {
            apiResponse.observe(this, responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    if (responseData.getCode() == 200) {
                        mainViewModel.getFollowingList(userid);
                    } else {
                        Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }

    }

    @Override
    public void onFailure(@NonNull String message) {
        UtilityHelperKt.hide(bi.progressCircular);
        UtilityHelperKt.toast(mActivity,message);
    }
    void filter(String text){
        listData.clear();
        if(text.isEmpty()){
            listData.addAll(mainData);
            adapter.notifyDataSetChanged();
            return;
        }
        List<Following> temp = new ArrayList();
        for(Following d : mainData){
            if(d.getUser().getUsername().toLowerCase(Locale.ROOT).contains(text.toLowerCase(Locale.ROOT))){
                temp.add(d);
            }
        }
        listData.addAll(temp);
        adapter.notifyDataSetChanged();
    }
}