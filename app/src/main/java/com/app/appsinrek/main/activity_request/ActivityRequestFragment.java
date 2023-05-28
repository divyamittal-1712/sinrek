package com.app.appsinrek.main.activity_request;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.FragmentActivityRequestBinding;
import com.app.appsinrek.main.MainActivity;
import com.app.appsinrek.main.activity_request.adapter.ActivityRequestAdapter;
import com.app.appsinrek.main.activity_request.adapter.FollowingActivityAdapter;
import com.app.appsinrek.main.activity_request.models.Following;
import com.app.appsinrek.main.profile_other.ProfileOtherFragment;
import com.app.appsinrek.models.ResponseData;
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

import io.ak1.pix.helpers.SystemUiHelperKt;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link ActivityRequestFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ActivityRequestFragment extends Fragment implements AuthListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public ActivityRequestFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DashboardFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static ActivityRequestFragment newInstance(String param1, String param2) {
        ActivityRequestFragment fragment = new ActivityRequestFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

//    private List<Requests> requestsList = new ArrayList<>();
    private List<Following> followingActivityList = new ArrayList<>();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUiHelperKt.getScreenSize(requireActivity());
    }

    FragmentActivityRequestBinding bi;
    private MainViewModel mainViewModel;
    boolean viewType = false;
    ActivityRequestAdapter adapter;
    FollowingActivityAdapter fa_adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setMainListener(this);
        bi = DataBindingUtil.inflate(inflater, R.layout.fragment_activity_request, container, false);
        setFollowingActivity();
//        bi.mtTabs.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
//            @SuppressLint("NotifyDataSetChanged")
//            @Override
//            public void onTabSelected(TabLayout.Tab tab) {
//                switch (tab.getPosition()) {
//                    case 0:
//                        bi.recycler.setVisibility(View.GONE);
//                        bi.recycler2.setVisibility(View.VISIBLE);
//                        setFollowingActivity();
//                        adapter.setViewType(false);
//                        break;
//                    case 1:
//                        bi.recycler.setVisibility(View.VISIBLE);
//                        bi.recycler2.setVisibility(View.GONE);
//                        setRequestList();
//                        fa_adapter.setViewType(false);
//                        break;
//                }
//            }
//
//            @Override
//            public void onTabUnselected(TabLayout.Tab tab) {
//
//            }
//
//            @Override
//            public void onTabReselected(TabLayout.Tab tab) {
//
//            }
//        });
        return bi.getRoot();
    }

    private void setFollowingActivity(){
        mainViewModel.getAllFollowingActivity();
        setupFATabRecyclerView();/**FollowingActivityAdapter RecycleView**/
        fa_adapter.setViewType(true);
    }

//    private void setRequestList(){
//        mainViewModel.getAllRequests();
//        setupRTabRecyclerView();/**ActivityRequestAdapter RecycleView**/
//        adapter.setViewType(true);
//    }

    private void setupFATabRecyclerView() {
        fa_adapter = new FollowingActivityAdapter(requireActivity(), view -> {
            if (view.getId() == R.id.profile_image || view.getId() == R.id.username) {
                Following model = (Following) view.getTag();
                Fragment fragment = new ProfileOtherFragment();
                Bundle bu = new Bundle();
                bu.putString("userid", model.getFollowingActivity().getUserId());
                fragment.setArguments(bu);
                ((MainActivity) requireActivity()).setCurrentFragment(fragment, "");
            }
        }, viewType, followingActivityList);
        bi.recycler2.setAdapter(fa_adapter);
    }

    private void setupRTabRecyclerView() {
//        adapter = new ActivityRequestAdapter(requireActivity(), view -> {
//            if (view.getId() == R.id.profile_image || view.getId() == R.id.username) {
//                Requests model = (Requests) view.getTag();
//                Fragment fragment = new ProfileOtherFragment();
//                Bundle bu = new Bundle();
//                bu.putString("userid", model.getSender().getId());
//                fragment.setArguments(bu);
//                ((MainActivity) requireActivity()).setCurrentFragment(fragment, "");
//            }
//            if (view.getId() == R.id.accept) {
//                Requests model = (Requests) view.getTag();
//                mainViewModel.accept(model.getFollowRequest().getSenderId(), model.getFollowRequest().getReceiverId());
//            } else if (view.getId() == R.id.decline) {
//                Requests model = (Requests) view.getTag();
//                mainViewModel.reject(model.getFollowRequest().getSenderId(), model.getFollowRequest().getReceiverId());
//            }
//        }, viewType, requestsList);
//        bi.recycler.setAdapter(adapter);
    }

    @Override
    public void onStarted(@NonNull String tag) {
        UtilityHelperKt.show(bi.progressCircular);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {

        if (tag.equals(AppConstants.TAG_SHOW_ALL_FOLLOWING_ACTIVITY)) {
            apiResponse.observe(this, responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    followingActivityList.clear();
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<Following>>() {}.getType();
                        JsonElement js = gson.toJsonTree(responseData.getMsg());
                        followingActivityList.addAll(gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type));
                        fa_adapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    UtilityHelperKt.toast(requireActivity(), e.toString());
                }
            });
        }
//        else if (tag.equals(AppConstants.TAG_SHOW_ALL_REQUESTS)) {
//            apiResponse.observe(this, responseData -> {
//                UtilityHelperKt.hide(bi.progressCircular);
//                try {
//                    requestsList.clear();
//                    if (responseData.getCode() == 200) {
//                        Gson gson = new Gson();
//                        Type type = new TypeToken<List<Requests>>() {
//                        }.getType();
//                        JsonElement js = gson.toJsonTree(responseData.getMsg());
//                        requestsList.addAll(gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type));
//                        adapter.notifyDataSetChanged();
//                    } else {
////                        UtilityHelperKt.toast(requireActivity(), responseData.getMsg().toString());
//                    }
//                } catch (Exception e) {
//                    UtilityHelperKt.toast(requireActivity(), e.toString());
//                }
//            });
//        }
//        else if (tag.equals(AppConstants.TAG_ACCEPT_REJECT)) {
//            apiResponse.observe(this, responseData -> {
//                UtilityHelperKt.hide(bi.progressCircular);
//
//                mainViewModel.getAllRequests();
//            });
//        }
    }

    @Override
    public void onFailure(@NonNull String message) {
        UtilityHelperKt.hide(bi.progressCircular);
    }
}