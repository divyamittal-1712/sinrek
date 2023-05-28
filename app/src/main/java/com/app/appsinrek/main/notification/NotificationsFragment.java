package com.app.appsinrek.main.notification;

import static com.app.appsinrek.utils.AppConstants.TAG_POST_FOR_UPDATE;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.FragmentNotificationsBinding;
import com.app.appsinrek.main.MainActivity;
import com.app.appsinrek.main.comment.CommentActivity;
import com.app.appsinrek.main.dashboard.PostPreviewFragment;
import com.app.appsinrek.main.notification.adapter.NotificationsAdapter;
import com.app.appsinrek.main.notification.model.Following;
import com.app.appsinrek.main.notification.model.Notifications;
import com.app.appsinrek.main.profile.ProfileFragment;
import com.app.appsinrek.main.profile_other.ProfileOtherFragment;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.models.post.PostModel;
import com.app.appsinrek.utils.AppConstants;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.fitness.modval.interfaces.AuthListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import io.ak1.pix.helpers.SystemUiHelperKt;

public class NotificationsFragment extends Fragment implements AuthListener {
    public NotificationsFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SystemUiHelperKt.getScreenSize(requireActivity());
    }

    FragmentNotificationsBinding bi;
    private MainViewModel mainViewModel;
    boolean viewType = true;
    NotificationsAdapter adapter;
    private List<Following> following = new ArrayList<>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setMainListener(this);
        bi = DataBindingUtil.inflate(inflater, R.layout.fragment_notifications, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar(bi.toolbar);
        setupRecyclerView();
        return bi.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        mainViewModel.getAllNotifications();
    }

    private void setupRecyclerView() {
        adapter = new NotificationsAdapter(requireActivity(), view -> {
            if (view.getId() == R.id.profile_image) {
                Following model = (Following) view.getTag();
                if (model.getUser().getId().isEmpty()) {
                    UtilityHelperKt.toast(requireActivity(), "User Data Empty");
                    return;
                }
                if (mainViewModel.getUser().getId().equals(model.getUser().getId())) {
                    ((MainActivity) requireActivity()).setCurrentFragment(new ProfileFragment(), "");
                    return;
                }
                Fragment fragment = new ProfileOtherFragment();
                Bundle bu = new Bundle();
                bu.putString("userid", model.getUser().getId());
                fragment.setArguments(bu);
                ((MainActivity) requireActivity()).setCurrentFragment(fragment, "");
            } else if (view.getId() == R.id.frame) {
                Following model = (Following) view.getTag();
                Intent intent;
                if (model.getNotification().getComment() != null && model.getPost().getId() != null) {
                    intent = new Intent(getActivity(), CommentActivity.class);
                    intent.putExtra("post_id", model.getPost().getId());
                    intent.putExtra("post_user_id", model.getPost().getUserId());
                    startActivity(intent);
                } else if (model.getNotification().getLike() != null && model.getPost().getId() != null) {
                    mainViewModel.getPostsForUpdate(model.getPost().getId());
                }
            }
        }, viewType, following);
        bi.recycler2.setAdapter(adapter);
    }

    @Override
    public void onStarted(@NonNull String tag) {
        UtilityHelperKt.show(bi.progressCircular);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if (tag.equals(AppConstants.TAG_SHOW_ALL_NOTIFICATIONS)) {
            apiResponse.observe(this, responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    following.clear();
                    if (responseData.getCode() == 200) {
                        Intent myIntent = new Intent("FBR-IMAGE");
                        myIntent.putExtra("action","foreground_notificator");
                        getActivity().getApplicationContext().sendBroadcast(myIntent);
                        Gson gson = new Gson();
                        Type type = new TypeToken<Notifications>() {
                        }.getType();
                        JsonObject js = gson.toJsonTree(responseData.getMsg()).getAsJsonObject();
                        Notifications notifications = gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type);
                        gson.fromJson(js.toString(), type);
                        following.addAll(notifications.getYou());
                        following.addAll(notifications.getFollowing());
//                        Collections.sort(following, new Comparator<Following>() {
//                            @SuppressLint("SimpleDateFormat")
//                            DateFormat f = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
//                            @Override
//                            public int compare(Following lhs, Following rhs) {
//                                try {
//                                    return f.parse(rhs.getNotification().getCreated()).compareTo(f.parse(lhs.getNotification().getCreated()));
//                                } catch (ParseException e) {
//                                    throw new IllegalArgumentException(e);
//                                }
//                            }
//                        });
                        adapter.notifyDataSetChanged();
                    } else {
//                        UtilityHelperKt.toast(requireActivity(), responseData.getMsg().toString());
                    }
                } catch (Exception e) {
                    UtilityHelperKt.toast(requireActivity(), e.toString());
                }
            });
        }
        else if (tag.equals(TAG_POST_FOR_UPDATE)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<PostModel>() {
                        }.getType();
                        PostModel model = gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type);
//                        requireActivity().startActivity(new Intent(requireActivity(), PostPreviewActivity.class)
//                                .putExtra("user_id", model.getPost().getUserId()));
                        Fragment fragment = new PostPreviewFragment();
                        Bundle bu = new Bundle();
                        bu.putString("user_id",model.getPost().getUserId());
                        fragment.setArguments(bu);
                        ((MainActivity)requireActivity()).setCurrentFragment(fragment,"");

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
    }
}