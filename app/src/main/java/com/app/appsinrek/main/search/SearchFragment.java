package com.app.appsinrek.main.search;

import static com.app.appsinrek.utils.AppConstants.TAG_CANCEL_REQUEST;

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
import com.app.appsinrek.databinding.ActivitySearchBinding;
import com.app.appsinrek.main.MainActivity;
import com.app.appsinrek.main.profile.ProfileFragment;
import com.app.appsinrek.main.profile_other.ProfileOtherFragment;
import com.app.appsinrek.main.search.adapter.SearchAdapter;
import com.app.appsinrek.main.search.model.SearchModel;
import com.app.appsinrek.models.Other;
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

public class SearchFragment extends Fragment implements AuthListener {

    ActivitySearchBinding bi;
    AppCompatActivity mActivity;
    MainViewModel mainViewModel;
    SearchAdapter adapter;
    ArrayList<SearchModel> searchData = new ArrayList();
    int position;

    @Override
    synchronized
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setMainListener(this);
        bi = DataBindingUtil.inflate(inflater, R.layout.activity_search, container, false);
//        bi = DataBindingUtil.setContentView(this, R.layout.activity_search);
        mActivity = ((AppCompatActivity) getActivity());
        mActivity.setSupportActionBar(bi.toolbar);
        if (mActivity.getSupportActionBar() != null) {
            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            mActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
        }
        adapter = new SearchAdapter(requireActivity(), view -> {
            int id = view.getId();
            User user;
            switch (id) {
                case R.id.btnFollow:
                    position = (int) view.getTag();
                    if (UtilityHelperKt.getText(searchData.get(position).getUser().getId()).isEmpty()) {
                        UtilityHelperKt.toast(requireActivity(), "User Data Empty");
                        break;
                    }
                    user = searchData.get(position).getUser();
                    Other other = searchData.get(position).getOther();
                    if (other.getButton().equals("Following")) {
                        new AlertDialog.Builder(requireActivity())
                                .setTitle("Unfollow")
                                .setMessage("Do you want to  unfollow this account?")
                                .setPositiveButton("Yes", (dialogInterface, i) -> {
                                    mainViewModel.unfollow(user.getId());
                                    dialogInterface.dismiss();
                                })
                                .setNegativeButton("No", (dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                })
                                .create()
                                .show();

                    }
                    else if (other.getButton().equals(mActivity.getResources().getString(R.string.requested))) {
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

                    } else {
                        mainViewModel.sendFollowRequest(user.getId());
                    }
                    break;
                case R.id.profile_image:
                case R.id.username:
                    user = (User) view.getTag();
                    if (mainViewModel.getUser().getId().equals(UtilityHelperKt.getText(user.getId()))) {
                        ((MainActivity) requireActivity()).setCurrentFragment(new ProfileFragment(), "");
                        return;
                    }
                    if (UtilityHelperKt.getText(user.getId()).isEmpty()) {
                        UtilityHelperKt.toast(requireActivity(), "User Data Empty");
                        break;
                    }
                    Fragment fragment = new ProfileOtherFragment();
                    Bundle bu = new Bundle();
                    bu.putString("userid", user.getId());
                    fragment.setArguments(bu);
                    ((MainActivity) requireActivity()).setCurrentFragment(fragment, "");
                    break;

            }

        }, searchData, mainViewModel.getUser().getId());
        bi.recycler.setAdapter(adapter);


        bi.ivSearch.setOnClickListener(view -> {
            if (!bi.tilSearch.getText().toString().isEmpty()) {
                mainViewModel.searchUser(bi.tilSearch.getText().toString());
            }

        });

        bi.tilSearch.addTextChangedListener(new TextWatcher() {

            public void afterTextChanged(Editable s) {
                if (!s.toString().isEmpty()) {
                    mainViewModel.searchUser(bi.tilSearch.getText().toString());
                }
            }

            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                searchData.clear();
                adapter.notifyDataSetChanged();
            }

            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

        });
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
//            ((MainActivity)getActivity()).setCurrentFragment(new DashboardFragment(),"home");
            ((MainActivity) requireActivity()).setBottomSheet(0);
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
        if (tag.equals(AppConstants.TAG_SEARCH)) {
            apiResponse.observe(this, responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    searchData.clear();

                    if (responseData.getCode() == 200 && !bi.tilSearch.getText().toString().isEmpty()) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<SearchModel>>() {
                        }.getType();

                        List<SearchModel> list = gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type);
                        searchData.addAll(list);
                        adapter.notifyDataSetChanged();
                    } else if (responseData.getCode() != 200) {
                        adapter.notifyDataSetChanged();
                        UtilityHelperKt.toast(mActivity, responseData.getMsg().toString());
                    } else {
                        adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    UtilityHelperKt.toast(mActivity, e.toString());
                }
            });
        }
        else if (tag.equals(AppConstants.TAG_FOLLOW)) {
            apiResponse.observe(this, responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    if (responseData.getCode() == 200) {
                        if (!bi.tilSearch.getText().toString().isEmpty()) {
                            mainViewModel.searchUser(bi.tilSearch.getText().toString());
                        }
                    } else {
                        UtilityHelperKt.toast(mActivity, responseData.getMsg().toString());
                    }
                } catch (Exception e) {
                    UtilityHelperKt.toast(mActivity, e.toString());
                }
            });
        }
        else if (tag.equals(AppConstants.TAG_UNFOLLOW)) {
            apiResponse.observe(this, responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    if (responseData.getCode() == 200) {
                        if (!bi.tilSearch.getText().toString().isEmpty()) {
                            mainViewModel.searchUser(bi.tilSearch.getText().toString());
                        }
                    } else {
                        UtilityHelperKt.toast(mActivity, responseData.getMsg().toString());
                    }
                } catch (Exception e) {
                    UtilityHelperKt.toast(mActivity, e.toString());
                }
            });
        }
        else if (tag.equals(TAG_CANCEL_REQUEST)) {
            apiResponse.observe(this, responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    if (responseData.getCode() == 200) {
                        if (!bi.tilSearch.getText().toString().isEmpty()) {
                            mainViewModel.searchUser(bi.tilSearch.getText().toString());
                        }
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
        UtilityHelperKt.toast(mActivity, message);
    }
}