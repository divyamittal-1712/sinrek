package com.app.appsinrek.main.profile;

import static com.app.appsinrek.utils.AppConstants.TAG_POST;
import static com.app.appsinrek.utils.AppConstants.TAG_USER_PROFILE;
import static com.app.appsinrek.utils.CommonUtils.decodeEmoji;
import static com.app.appsinrek.utils.CommonUtils.toCamelCase;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Intent;
import android.net.Uri;
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
import androidx.core.view.ViewCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.FragmentProfileBinding;
import com.app.appsinrek.intro.Welcome;
import com.app.appsinrek.main.MainActivity;
import com.app.appsinrek.main.dashboard.PostPreviewFragment;
import com.app.appsinrek.main.profile.adapter.UserPostAdapter;
import com.app.appsinrek.main.savepost.SavePostFragment;
import com.app.appsinrek.main.settings.SettingActivity;
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
import java.util.ArrayList;
import java.util.List;
public class ProfileFragment extends Fragment implements AuthListener {
    public ProfileFragment() {
        // Required empty public constructor
    }
    private AppCompatActivity mActivity;
    public PostModel model;
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setHasOptionsMenu(true);
    }
    FragmentProfileBinding bi;
    UserPostAdapter adapter;
    MainViewModel mainViewModel;
    private ArrayList<PostModel> postModelList = new ArrayList<>();
    @Override
    synchronized
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        bi = DataBindingUtil.inflate(inflater, R.layout.fragment_profile, container, false);
        mActivity = ((AppCompatActivity) getActivity());
//        mActivity.setSupportActionBar(bi.toolbar);
//        if (mActivity.getSupportActionBar() != null){
//            mActivity.getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            mActivity.getSupportActionBar().setDisplayShowHomeEnabled(true);
//            Drawable drawable= getResources().getDrawable(R.drawable.ic_back);
//            Bitmap bitmap = ((BitmapDrawable) drawable).getBitmap();
//            Drawable newDrawable = new BitmapDrawable(getResources(), Bitmap.createScaledBitmap(bitmap, 25, 40, true));
//            newDrawable.setColorFilter(Color.BLACK, PorterDuff.Mode.SRC_ATOP);
//            mActivity.getSupportActionBar().setHomeAsUpIndicator(newDrawable);
//        }
        bi.setMainViewModel(mainViewModel);
        mainViewModel.setMainListener(this);
        bi.instagramLink.setOnClickListener(view->{
            try {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("instagram://user?username=gk_everyday")));
            }catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("instagram://user?username=gk_everyday")));
            }
        });
        bi.twitterLink.setOnClickListener(view->{
            try {
//                elonmusk
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=SinrekApp")));
            }catch (Exception e) {
                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?screen_name=SinrekApp")));
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/#!/")));
            }
        });
        bi.mtbFollow.setVisibility(View.GONE);
        bi.followersCountBtn.setOnClickListener(view -> {
            Fragment fragment = new FollowersActivity();
            Bundle bu = new Bundle();
            bu.putString("userid",mainViewModel.getUser().getId());
            fragment.setArguments(bu);
            ((MainActivity)requireActivity()).setCurrentFragment(fragment,"");
        });
        bi.followingCountBtn.setOnClickListener(view -> {
            Fragment fragment = new FollowingActivity();
            Bundle bu = new Bundle();
            bu.putString("userid",mainViewModel.getUser().getId());
            fragment.setArguments(bu);
            ((MainActivity)requireActivity()).setCurrentFragment(fragment,"");
        });

        bi.more.setOnClickListener(view -> {
            PopupMenu menu = null;
            if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                menu = new PopupMenu(getActivity(), view, Gravity.END, 0, R.style.MyPostMoreIconMenu);
            }
            else
                menu = new PopupMenu(getActivity(), view);

            menu.getMenu().add("Saved");
            menu.getMenu().add("Settings");
            menu.getMenu().add("Language");
            menu.getMenu().add("Logout");

            //registering popup with OnMenuItemClickListener
            menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                @Override
                public boolean onMenuItemClick(MenuItem item) {
                    if(item.getTitle().equals("Settings")){
                        startActivity(new Intent(getActivity(), SettingActivity.class));
                    }else if (item.getTitle().equals("Saved")){
                        Fragment fragment = new SavePostFragment();
                        ((MainActivity)requireActivity()).setCurrentFragment(fragment,"");
                    }else if (item.getTitle().equals("Logout")){
                        new AlertDialog.Builder(requireActivity())
                                .setTitle("Logout Account...!")
                                .setMessage("Want to logout this account?")
                                .setPositiveButton("Yes", (dialogInterface, i) -> {
                                    mainViewModel.getStorage().clear();
                                    Intent intent = new Intent(requireActivity(), Welcome.class);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    startActivity(intent);
                                    requireActivity().finish();
                                    dialogInterface.dismiss();
                                })
                                .setNegativeButton("No",(dialogInterface, i) -> {
                                    dialogInterface.dismiss();
                                })
                                .create()
                                .show();

                    }
                    return true;
                }
            });
            menu.show();
        });
        bi.btnEditProfile.setOnClickListener(view -> {
            getActivity().startActivity(new Intent(getActivity(), EditProfileActivity.class));
        });
        bi.icBack.setOnClickListener(view -> {
            ((MainActivity)requireActivity()).setBottomSheet(0);
        });
        adapter = new UserPostAdapter(getActivity(), view->{
        if(view.getId() == R.id.card){
            int position = (int) view.getTag();
            Gson gson = new Gson();
            Type type = new TypeToken<List<PostModel>>(){}.getType();
            String json = gson.toJson(postModelList,type);
            Fragment fragment = new PostPreviewFragment();
            Bundle bu = new Bundle();
            bu.putString("user_id",mainViewModel.getUser().getId());
            bu.putString("post_array",json);
            bu.putInt("click_index",position);
            fragment.setArguments(bu);
            ((MainActivity)requireActivity()).setCurrentFragment(fragment,"");
        }
        },postModelList);
        bi.recycler.setHasFixedSize(false);
        bi.recycler.setAdapter(adapter);
        ViewCompat.setNestedScrollingEnabled(bi.recycler, false);
        return bi.getRoot();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            ((MainActivity)requireActivity()).setBottomSheet(0);
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onStart() {
        super.onStart();
        mainViewModel.getUserProfile(mainViewModel.getUser().getId());
        mainViewModel.getUserPosts(null);
    }

    private void setupUi() {
        RequestOptions reqOptions = new RequestOptions()
                .format(DecodeFormat.PREFER_RGB_565)
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.RESOURCE);

        if(!UtilityHelperKt.getText(mainViewModel.getUser().getImage()).isEmpty()) {
            Glide.with(requireActivity()).asBitmap()
                    .load(API_LINKS.BASE_IMAGE_URL+mainViewModel.getUser().getImage())
                    .apply(reqOptions)
                    .into(bi.profileImage);
        }
        UtilityHelperKt.visible(bi.name,!UtilityHelperKt.getText(mainViewModel.getUser().getFullName()).isEmpty());
        bi.profileName.setText(!(mainViewModel.getUser().getUsername()).isEmpty()?"@"+toCamelCase(mainViewModel.getUser().getUsername()):"Profile");
//        bi.toolbar.setTitle(!(mainViewModel.getUser().getUsername()).isEmpty()?"@"+toCamelCase(mainViewModel.getUser().getUsername()):"Profile");
        bi.name.setText(UtilityHelperKt.getText(mainViewModel.getUser().getFullName()).isEmpty()?mainViewModel.getUser().getUsername():mainViewModel.getUser().getFullName());
//        bi.username.setText("@"+mainViewModel.getUser().getUsername());



        if (mainViewModel.getUser().getDob().equals("")) {
            bi.dob.setVisibility(View.GONE);
        }else {
            bi.dob.setVisibility(View.VISIBLE);
            bi.dob.setText(UtilityHelperKt.getText(mainViewModel.getUser().getDob()).isEmpty()?"":UtilityHelperKt.convertDateTime(mainViewModel.getUser().getDob(),"yyyy-MM-dd","dd MMM,yyyy"));

        }

        if (mainViewModel.getUser().getBio().equals("")) {
            bi.bio.setVisibility(View.GONE);
        }else {
            bi.bio.setVisibility(View.VISIBLE);
            bi.bio.setText(decodeEmoji(mainViewModel.getUser().getBio()));

        }

        if (mainViewModel.getUser().getWebsite().equals("")) {
            bi.link.setVisibility(View.GONE);
        }else {
            bi.link.setVisibility(View.VISIBLE);
            bi.link.setText(mainViewModel.getUser().getWebsite());

        }
        bi.followersCountBtn.setText(mainViewModel.getUser().getFollowers()+ " Followers");
        bi.followingCountBtn.setText(mainViewModel.getUser().getFollowing()+ " Following");

    }

    @Override
    public void onStarted(@NonNull String tag) {
        bi.progressCircular.setVisibility(View.VISIBLE);
    }

    @SuppressLint({"NotifyDataSetChanged", "SetTextI18n"})
    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if(tag.equals(TAG_POST)) {
        apiResponse.observe(this, responseData->{
            bi.progressCircular.setVisibility(View.GONE);
            try {
                postModelList.clear();
                if (responseData.getCode() == 200) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<List<PostModel>>(){}.getType();
                    JsonElement js = gson.toJsonTree(responseData.getMsg());
                    postModelList.addAll(gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type));
                    bi.postCount.setText(toCamelCase("Posts "+postModelList.size()));
                    adapter.notifyDataSetChanged();
                } else {
//                    Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
            }

        });
    }
        else if(tag.equals(TAG_USER_PROFILE)){
            apiResponse.observe(this,responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<User>() {
                        }.getType();
                        JsonElement js = gson.toJsonTree(responseData.getMsg());
                        User model = gson.fromJson(gson.toJsonTree(responseData.getMsg()).getAsJsonObject().getAsJsonObject("User").toString(), type);
                        mainViewModel.setUser(model);
                        setupUi();
                    } else {
//                    Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
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