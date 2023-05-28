package com.app.appsinrek.main.profile_other;

import static com.app.appsinrek.utils.AppConstants.TAG_BLOCKED_USERS;
import static com.app.appsinrek.utils.AppConstants.TAG_CANCEL_REQUEST;
import static com.app.appsinrek.utils.AppConstants.TAG_FOLLOW;
import static com.app.appsinrek.utils.AppConstants.TAG_POST;
import static com.app.appsinrek.utils.AppConstants.TAG_UNFOLLOW;
import static com.app.appsinrek.utils.AppConstants.TAG_USER_BLOCK;
import static com.app.appsinrek.utils.AppConstants.TAG_USER_PROFILE;
import static com.app.appsinrek.utils.CommonUtils.decodeEmoji;
import static com.app.appsinrek.utils.CommonUtils.toCamelCase;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.FragmentProfileOtherUserBinding;
import com.app.appsinrek.main.MainActivity;
import com.app.appsinrek.main.dashboard.PostPreviewFragment;
import com.app.appsinrek.main.profile.FollowersActivity;
import com.app.appsinrek.main.profile.FollowingActivity;
import com.app.appsinrek.main.profile.adapter.UserPostAdapter;
import com.app.appsinrek.main.profile_other.models.BlockedUser;
import com.app.appsinrek.main.report.ReportActivity;
import com.app.appsinrek.models.Other;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.models.User;
import com.app.appsinrek.models.post.PostModel;
import com.app.appsinrek.network.API_LINKS;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DecodeFormat;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;
import com.fitness.modval.interfaces.AuthListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import kotlin.collections.CollectionsKt;

public class ProfileOtherFragment extends Fragment implements AuthListener {
    private AppCompatActivity mActivity;

    public ProfileOtherFragment() {
    }

    boolean isUserBlocked = false;
    int page = 1;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    FragmentProfileOtherUserBinding bi;
    UserPostAdapter adapter;
    MainViewModel mainViewModel;
    public PostModel model;
    private ArrayList<PostModel> postModelList = new ArrayList<>();
    String userid;

    @Override
    synchronized
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        bi = DataBindingUtil.inflate(inflater, R.layout.fragment_profile_other_user, container, false);
        mActivity = ((AppCompatActivity) getActivity());

        userid = getArguments().getString("userid");
        bi.setMainViewModel(mainViewModel);
        mainViewModel.setMainListener(this);
        bi.countFollowers.setOnClickListener(view -> {
            Fragment fragment = new FollowersActivity();
            Bundle bu = new Bundle();
            bu.putString("userid", userid);
            fragment.setArguments(bu);
            getParentFragmentManager().beginTransaction().add(R.id.content, fragment).addToBackStack("home").commit();
        });
        bi.countFollowing.setOnClickListener(view -> {
            Fragment fragment = new FollowingActivity();
            Bundle bu = new Bundle();
            bu.putString("userid", userid);
            fragment.setArguments(bu);
            getParentFragmentManager().beginTransaction().add(R.id.content, fragment).addToBackStack("home").commit();
        });
        bi.icBack.setOnClickListener(view -> {
            ((MainActivity) requireActivity()).setBottomSheet(0);
        });
        bi.more.setOnClickListener(view -> {
            PopupMenu menu = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                menu = new PopupMenu(getActivity(), view, Gravity.END, 0, R.style.MyPostMoreIconMenu);
            } else
                menu = new PopupMenu(getActivity(), view);

            if (isUserBlocked)
                menu.getMenu().add("Unblock");
            else
                menu.getMenu().add("Block");
            menu.getMenu().add("Report");
            menu.getMenu().add("Share this Profile");

            menu.setOnMenuItemClickListener(item -> {
                if (item.getTitle().equals("Block")) {
                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Block Account...!")
                            .setMessage("Want to block this account?")
                            .setPositiveButton("Yes", (dialogInterface, i) -> {
                                HashMap<Object, Object> body = new HashMap();
                                body.put("userid", mainViewModel.getUser().getId());
                                body.put("blockuserid", userid);
                                mainViewModel.userBlock(body);
                                dialogInterface.dismiss();
                            })
                            .setNegativeButton("No", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            })
                            .create()
                            .show();

                }
                if (item.getTitle().equals("Unblock")) {
                    new AlertDialog.Builder(requireActivity())
                            .setTitle("Unblock Account...!")
                            .setMessage("Want to Unblock this account?")
                            .setPositiveButton("Yes", (dialogInterface, i) -> {
                                HashMap<Object, Object> body = new HashMap();
                                body.put("userid", mainViewModel.getUser().getId());
                                body.put("blockeduserid", userid);
                                mainViewModel.userUnBlock(body);
                                dialogInterface.dismiss();
                            })
                            .setNegativeButton("No", (dialogInterface, i) -> {
                                dialogInterface.dismiss();
                            })
                            .create()
                            .show();

                }
                if (item.getTitle().equals("Report")) {
                    startActivity(new Intent(requireActivity(), ReportActivity.class).putExtra("report_on", "user").putExtra("userid", userid));
                }
                item.getTitle();
                return true;
            });
            menu.show();
        });
        bi.btnEditProfile.setVisibility(View.GONE);
        adapter = new UserPostAdapter(getActivity(), view -> {
            if (view.getId() == R.id.card) {
                int position = (int) view.getTag();
                Gson gson = new Gson();
                Type type = new TypeToken<List<PostModel>>() {
                }.getType();
                String json = gson.toJson(postModelList, type);
                Fragment fragment = new PostPreviewFragment();
                Bundle bu = new Bundle();
                bu.putString("user_id", mainViewModel.getUser().getId());
                bu.putString("post_array", json);
                bu.putInt("click_index", position);
                fragment.setArguments(bu);
                ((MainActivity) requireActivity()).setCurrentFragment(fragment, "");
            }
        }, postModelList);
        bi.recycler.setHasFixedSize(false);
        bi.recycler.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(bi.recycler, false);
        return bi.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ((MainActivity) requireActivity()).setBottomSheet(0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mainViewModel.getUserProfile(userid);
//        mainViewModel.getBlockedUserList();
    }

    private void setupUi(User user, Other other) {
        RequestOptions reqOptions = new RequestOptions()
                .format(DecodeFormat.PREFER_RGB_565)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);
        if (!user.getImage().isEmpty()) {
            Glide.with(requireActivity()).asBitmap()
                    .load(API_LINKS.BASE_IMAGE_URL + user.getImage())
                    .apply(reqOptions)
                    .into(bi.profileImage);
        }
        isUserBlocked = false;
        UtilityHelperKt.visible(bi.name, !UtilityHelperKt.getText(user.getFullName()).isEmpty());
        bi.profileName.setText(!(user.getUsername()).isEmpty() ? "@" + toCamelCase(user.getUsername()) : "Profile");
        bi.name.setText(UtilityHelperKt.getText(user.getFullName()).isEmpty() ? user.getUsername() : user.getFullName());
        bi.dob.setText(UtilityHelperKt.getText(user.getDob()).isEmpty() ? "" : UtilityHelperKt.convertDateTime(user.getDob(), "yyyy-MM-dd", "dd MMM,yyyy"));
        bi.bio.setText(decodeEmoji(user.getBio()));
        bi.link.setText(user.getWebsite());

            if (other.getButton().equals(mActivity.getResources().getString(R.string.follow))) {
            bi.mtbFollow.setBackgroundTintList(ContextCompat.getColorStateList(mActivity, R.color.blue));
            bi.mtbFollow.setBackground(ContextCompat.getDrawable(mActivity,R.drawable.bg_blue));
            bi.mtbFollow.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
            bi.mtbFollow.setText(mActivity.getResources().getString(R.string.follow));
            bi.mtbFollow.setOnClickListener(view -> {
                mainViewModel.sendFollowRequest(userid);
            });
        }
        else if (other.getButton().equals(mActivity.getResources().getString(R.string.following))) {
            bi.mtbFollow.setBackgroundTintList(ContextCompat.getColorStateList(mActivity, R.color.greyLiteToo));
            bi.mtbFollow.setBackground(ContextCompat.getDrawable(mActivity,R.drawable.bg_grey5));
            bi.mtbFollow.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            bi.mtbFollow.setText(mActivity.getResources().getString(R.string.following));
            bi.mtbFollow.setOnClickListener(view -> {
                new AlertDialog.Builder(requireActivity())
                        .setTitle("Cancel Request")
                        .setMessage("Do you want to  unfollow this account?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            mainViewModel.unfollow(userid);
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .create()
                        .show();
            });
        } else {
            bi.mtbFollow.setBackground(ContextCompat.getDrawable(mActivity, R.drawable.bg_white));
            bi.mtbFollow.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            bi.mtbFollow.setText(mActivity.getResources().getString(R.string.following));
            bi.mtbFollow.setOnClickListener(view -> {
                new AlertDialog.Builder(requireActivity())
                        .setTitle("Unfollow")
                        .setMessage("Do you want to  unfollow this account?")
                        .setPositiveButton("Yes", (dialogInterface, i) -> {
                            mainViewModel.unfollow(userid);
                            dialogInterface.dismiss();
                        })
                        .setNegativeButton("No", (dialogInterface, i) -> {
                            dialogInterface.dismiss();
                        })
                        .create()
                        .show();
            });
        }
        bi.countFollowers.setText(MessageFormat.format("{0} Followers", user.getFollowers()));
        bi.countFollowing.setText(MessageFormat.format("{0} Following", user.getFollowing()));
        assert getArguments() != null;

        mainViewModel.getUserPosts(userid);
    }

    @Override
    public void onStarted(@NonNull String tag) {
        bi.progressCircular.setVisibility(View.VISIBLE);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if (tag.equals(TAG_POST)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    Gson gson1 = new Gson();
                    JsonElement e = gson1.toJsonTree(responseData).getAsJsonObject();
                    postModelList.clear();
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<PostModel>>() {
                        }.getType();
                        postModelList.addAll(gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type));
                        bi.postCount.setText("Posts  " + postModelList.size());
                        adapter.notifyDataSetChanged();
                    } else {
//                        Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }

            });
        }
        else if (tag.equals(TAG_FOLLOW)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200) {
                        mainViewModel.getUserProfile(userid);
//                        Toast.makeText(getActivity(), "Request Send", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else if (tag.equals(TAG_UNFOLLOW)) {
            apiResponse.observe(this, responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    if (responseData.getCode() == 200) {
                        mainViewModel.getUserProfile(userid);
                        Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else if (tag.equals(TAG_CANCEL_REQUEST)) {
            apiResponse.observe(this, responseData -> {
                UtilityHelperKt.hide(bi.progressCircular);
                try {
                    if (responseData.getCode() == 200) {
                        mainViewModel.getUserProfile(userid);
                        Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else if (tag.equals(TAG_BLOCKED_USERS)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<BlockedUser>>() {
                        }.getType();
                        List<BlockedUser> list = gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type);
                        List<BlockedUser> newList = CollectionsKt.filter(list, s -> (s.getUser().getId() != null && s.getUser().getId().equals(userid)));
                        if (newList.size() > 0) {
                            isUserBlocked = true;
                        } else {
                            isUserBlocked = false;
                        }
                    } else {
//                            Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }

            });
        }
        else if (tag.equals(TAG_USER_BLOCK)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200) {
                        isUserBlocked = !isUserBlocked;
                        if(!isUserBlocked){
                            mainViewModel.getUserProfile(userid);
                        }
//                        mainViewModel.getBlockedUserList();
                        Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else if (tag.equals(TAG_USER_PROFILE)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    Gson gson1 = new Gson();
                    JsonElement e = gson1.toJsonTree(responseData.getMsg()).getAsJsonObject();
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<User>() {
                        }.getType();
                        Type otherType = new TypeToken<Other>() {
                        }.getType();
                        User model = gson.fromJson(gson.toJsonTree(responseData.getMsg()).getAsJsonObject().getAsJsonObject("User").toString(), type);
                        Other other = gson.fromJson(gson.toJsonTree(responseData.getMsg()).getAsJsonObject().getAsJsonObject("Other").toString(), otherType);
                        setupUi(model, other);
                    } else {
//                        Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    @Override
    public void onFailure(@NonNull String message) {
        bi.progressCircular.setVisibility(View.GONE);
        Toast.makeText(requireActivity(), message, Toast.LENGTH_SHORT).show();
    }
}