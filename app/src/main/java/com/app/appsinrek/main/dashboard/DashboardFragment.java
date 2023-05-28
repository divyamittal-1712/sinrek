package com.app.appsinrek.main.dashboard;

import static com.app.appsinrek.main.downloader.KEY_FILE_NAMEKt.KEY_FILE_NAME;
import static com.app.appsinrek.main.downloader.KEY_FILE_NAMEKt.KEY_FILE_TYPE;
import static com.app.appsinrek.main.downloader.KEY_FILE_NAMEKt.KEY_FILE_URI;
import static com.app.appsinrek.main.downloader.KEY_FILE_NAMEKt.KEY_FILE_URL;
import static com.app.appsinrek.utils.AppConstants.TAG_BOOKMARK;
import static com.app.appsinrek.utils.AppConstants.TAG_DELETE_POST;
import static com.app.appsinrek.utils.AppConstants.TAG_FOLLOW;
import static com.app.appsinrek.utils.AppConstants.TAG_LIKE;
import static com.app.appsinrek.utils.AppConstants.TAG_POST;
import static com.app.appsinrek.utils.AppConstants.TAG_POST_FOR_UPDATE;
import static com.app.appsinrek.utils.AppConstants.TAG_SHOW_ALL_BOOKMARK;
import static com.app.appsinrek.utils.AppConstants.TAG_USER_SUGGESTION;
import static com.app.appsinrek.utils.AppConstants.TAG_VIEW_COUNT;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.LinearSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.FragmentDashboardBinding;
import com.app.appsinrek.main.MainActivity;
import com.app.appsinrek.main.comment.CommentActivity;
import com.app.appsinrek.main.downloader.FileDownloadWorker;
import com.app.appsinrek.main.post.EditPostActivity;
import com.app.appsinrek.main.profile.ProfileFragment;
import com.app.appsinrek.main.profile_other.ProfileOtherFragment;
import com.app.appsinrek.main.savepost.models.Bookmark;
import com.app.appsinrek.main.search.SearchFragment;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.models.User;
import com.app.appsinrek.models.post.Post;
import com.app.appsinrek.models.post.PostModel;
import com.app.appsinrek.models.post.UserSuggestionModel;
import com.app.appsinrek.network.API_LINKS;
import com.app.appsinrek.player_view.adapter.PostAdapter;
import com.app.appsinrek.player_view.enums.PostType;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.bumptech.glide.Glide;
import com.fitness.modval.interfaces.AuthListener;
import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;

import kotlin.collections.CollectionsKt;

public class DashboardFragment extends Fragment implements AuthListener {
    public DashboardFragment() {
    }

    WorkManager workManager;
    private boolean isLoading = false;
    private boolean isScrolling = false;
    public PostModel model;
    private LinearSnapHelper snapHelper;
    private ActivityResultLauncher<Intent> requestLauncher;
    private ActivityResultLauncher<Intent> requestCommentLauncher;
    FragmentDashboardBinding bi;
    private List<PostModel> postModelList = new ArrayList<>();
    private List<UserSuggestionModel> userSuggestionModelList = new ArrayList<>();
    private ArrayList<Bookmark> savedPostList = new ArrayList<>();
    private PostAdapter dashboardAdapter;
    private boolean isMute = false;
    private MainViewModel mainViewModel;
    int page = 1;
    int suggestion_page = 1;
    int currentItem = 0;
    String lastPostId = "0";
    Handler handler = new Handler();
    PostModel viewModel;

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        page = 1;
                        DashboardFragment.this.setupRecyclerView();
                        mainViewModel.getPosts("" + page);
                    }
                });
        requestCommentLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {
                        Intent data = result.getData();
                        String tag = data.getStringExtra("tag");
                        if (tag != null && tag.equals("from_comment")) {
                            mainViewModel.getPostsForUpdate(data.getStringExtra("post_id"));
                        }
                        if (tag != null && tag.equals("update")) {
                            Bundle b = (Bundle) data.getParcelableExtra("bundle");
                            PostModel model_ = (PostModel) b.getParcelable("post");
                            mainViewModel.getPostsForUpdate(model_.getPost().getId());
                        } else {
                            String userId = data.getStringExtra("user_id");
                            if (mainViewModel.getUser().getId().equals(UtilityHelperKt.getText(userId))) {
                                ((MainActivity) requireActivity()).setCurrentFragment(new ProfileFragment(), "");
                                return;
                            }
                            if (UtilityHelperKt.getText(userId).isEmpty()) {
//                                UtilityHelperKt.toast(requireActivity(),"User Data Empty");
                                return;
                            }
                            Fragment fragment = new ProfileOtherFragment();
                            Bundle bu = new Bundle();
                            bu.putString("userid", userId);
                            fragment.setArguments(bu);
                            ((MainActivity) requireActivity()).setCurrentFragment(fragment, "");
                        }
                    }
                });
        workManager = WorkManager.getInstance(requireActivity());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setMainListener(this);
        bi = DataBindingUtil.inflate(inflater, R.layout.fragment_dashboard, container, false);
        ((AppCompatActivity) getActivity()).setSupportActionBar(bi.toolbar);

        setupRecyclerView();
        page = 1;
        mainViewModel.getSuggestionsUsers("" + suggestion_page);
        mainViewModel.getPosts("" + page);
        bi.search.setOnClickListener(view -> {
            ((MainActivity) requireActivity()).setCurrentFragment(new SearchFragment(), "");
        });
        bi.profile.setOnClickListener(view -> {
            ((MainActivity) requireActivity()).setCurrentFragment(new ProfileFragment(), "");
        });
        if (!UtilityHelperKt.getText(mainViewModel.getUser().getImage()).isEmpty()) {
            Glide.with(requireActivity())
                    .load(API_LINKS.BASE_IMAGE_URL + mainViewModel.getUser().getImage())
                    .centerCrop()
                    .optionalFitCenter()
                    .placeholder(R.drawable.user2)
                    .into(bi.profile);
        }
        return bi.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        //mainViewModel.showBookmarkedPosts();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @SuppressLint("NonConstantResourceId")
    private void setupRecyclerView() {

        postModelList.clear();
        final LinearLayoutManager layoutManager = new LinearLayoutManager(getActivity());
        bi.recycler.setLayoutManager(layoutManager);
        dashboardAdapter = new PostAdapter(getActivity(),
                view -> {
                    if (view.getId() == R.id.imagePost || view.getId() == R.id.playButton) {
                        PostModel model = (PostModel) view.getTag();
                        requireActivity().startActivity(new Intent(requireActivity(), VideoViewActivity.class)
                                .putExtra("post", model.getPost())
                        );
                    }
                    if (view.getId() == R.id.rl_share) {
                        PostModel model = (PostModel) view.getTag();
                        requireActivity().startActivity(new Intent(requireActivity(), VideoViewActivity.class)
                                .putExtra("post", model.getPost())
                        );
                    }
                    if (view.getId() == R.id.profile_image || view.getId() == R.id.username) {
                        PostModel model = (PostModel) view.getTag();
                        if (mainViewModel.getUser().getId().equals(UtilityHelperKt.getText(model.getUser().getId()))) {
                            ((MainActivity) requireActivity()).setCurrentFragment(new ProfileFragment(), "");
                            return;
                        }
                        if (UtilityHelperKt.getText(model.getUser().getId()).isEmpty()) {
                            UtilityHelperKt.toast(requireActivity(), "User Data Empty");
                            return;
                        }
                        Fragment fragment = new ProfileOtherFragment();
                        Bundle bu = new Bundle();
                        bu.putString("userid", model.getUser().getId());
                        fragment.setArguments(bu);
                        ((MainActivity) requireActivity()).setCurrentFragment(fragment, "");
                    }
                    if (view.getId() == R.id.prof_photo_id || view.getId() == R.id.share_username_id) {

                        PostModel model = (PostModel) view.getTag();
                        if (mainViewModel.getUser().getId().equals(UtilityHelperKt.getText(model.getUser().getId()))) {
                            ((MainActivity) requireActivity()).setCurrentFragment(new ProfileFragment(), "");
                            return;
                        }
                        if (UtilityHelperKt.getText(model.getUser().getId()).isEmpty()) {
                            UtilityHelperKt.toast(requireActivity(), "User Data Empty");
                            return;
                        }
                        Fragment fragment = new ProfileOtherFragment();
                        Bundle bu = new Bundle();
                        bu.putString("userid", model.getUser().getId());
                        fragment.setArguments(bu);
                        ((MainActivity) requireActivity()).setCurrentFragment(fragment, "");
                    }
                    if (view.getId() == R.id.more) {
//                        model = (PostModel) view.getTag();
//                        Fragment fragment = new PostDetailFragment();
//                        Bundle bu = new Bundle();
//                        bu.putString("post_id", model.getPost().getId());
//                        fragment.setArguments(bu);
//                        ((MainActivity) requireActivity()).setCurrentFragment(fragment, "");
                        model = (PostModel) view.getTag();
                        boolean isSaved = false;
                        List<Bookmark> newList = CollectionsKt.filter(savedPostList, s -> s.getPost().getId().equals(model.getPost().getId()));
                        if (newList.size() > 0) {
                            isSaved = true;
                        }
                        PopupMenu menu = null;
                        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP_MR1) {
                            menu = new PopupMenu(getActivity(), view, Gravity.END, 0, R.style.PostMoreIconMenu);
                        } else
                            menu = new PopupMenu(getActivity(), view);
                        if (mainViewModel.getUser().getId().equals(UtilityHelperKt.getText(model.getPost().getUserId()))) {
                            //My Profile
                            menu.inflate(R.menu.popup_menu2);
                        } else {
                            //Other Profile
                            menu.inflate(R.menu.popup_menu);
                            MenuItem menuCopy = menu.getMenu().findItem(R.id.download);
                            if (model.getPost().getType().equals(PostType.TEXT.getValue())) {
                                menuCopy.setVisible(false);
                            }
                            MenuItem menuLink = menu.getMenu().findItem(R.id.saved);
                            if (isSaved) {
                                menuLink.setTitle("Unsaved");
                            }
                            MenuItem menuReport = menu.getMenu().findItem(R.id.report);
                            if (model.getPost().getType().equals(PostType.TEXT.getValue())) {
                                menuReport.setVisible(false);
//                                startActivity(new Intent(requireActivity(), ReportActivity.class).putExtra("report_on", "user").putExtra("userid", userid));

                            }
                        }
                        MenuItem menuLink = menu.getMenu().findItem(R.id.link);
                        if (model.getPost().getType().equals(PostType.TEXT.getValue())) {
                            menuLink.setTitle("Copy");
                        }

                        boolean isAllowedDownload = model.getUser().getId().equals(mainViewModel.getUser().getId()) || model.getPost().getWhoCanDownloadMyPost().equals("0") || (model.getPost().getWhoCanDownloadMyPost().equals("1") && model.getPost().getFollowing().equals("1")) || (model.getPost().getWhoCanDownloadMyPost().equals("3") && model.getPost().getFollower().equals("1"));
                        boolean isAllowedSave = model.getUser().getId().equals(mainViewModel.getUser().getId()) || model.getPost().getWhoCanSaveMyPost().equals("0") || (model.getPost().getWhoCanSaveMyPost().equals("1") && model.getPost().getFollowing().equals("1")) || (model.getPost().getWhoCanSaveMyPost().equals("3") && model.getPost().getFollower().equals("1"));
                        boolean isAllowedProfile = model.getUser().getId().equals(mainViewModel.getUser().getId()) || model.getPost().getWhoCanSeeMyProfile().equals("0") || (model.getPost().getWhoCanSeeMyProfile().equals("1") && model.getPost().getFollowing().equals("1")) || (model.getPost().getWhoCanSeeMyProfile().equals("3") && model.getPost().getFollower().equals("1"));
                        //registering popup with OnMenuItemClickListener
                        menu.setOnMenuItemClickListener(item -> {
                            int pos = item.getItemId();
                            switch (pos) {
                                case R.id.view:
                                    if (isAllowedProfile) {
                                        if (UtilityHelperKt.getText(model.getUser().getId()).isEmpty()) {
                                            UtilityHelperKt.toast(requireActivity(), "User Data Empty");
                                            break;
                                        }
                                        Fragment fragment = new ProfileOtherFragment();
                                        Bundle bu = new Bundle();
                                        bu.putString("userid", model.getUser().getId());
                                        fragment.setArguments(bu);
                                        ((MainActivity) requireActivity()).setCurrentFragment(fragment, "");
                                    }
                                    model = null;
                                    break;
                                case R.id.edit:
                                    requestCommentLauncher.launch(new Intent(requireActivity(), EditPostActivity.class)
                                            .putExtra("post", model));
                                    break;
                                case R.id.delete:
                                    /**Customization title text view**/
                                    TextView titleView = new TextView(getContext());
                                    titleView.setText("DELETE POST!");
                                    titleView.setBackgroundColor(ContextCompat.getColor(getContext(), R.color.smooth_red));
                                    titleView.setTextColor(ContextCompat.getColor(getContext(), R.color.white));
                                    titleView.setGravity(Gravity.CENTER);
                                    titleView.setPadding(20, 20, 20, 20);
                                    titleView.setTextSize(15f);
                                    titleView.setTypeface(null, Typeface.BOLD);
//                            ,R.style.AppCompatAlertDialogStyle
                                    /**Code to show Confirmation alter for delete event**/
                                    new AlertDialog.Builder(getContext())
                                            .setCustomTitle(titleView)
                                            .setMessage("Do you want to delete this post?")
                                            .setIcon(android.R.drawable.ic_dialog_alert)
                                            .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                                                public void onClick(DialogInterface dialog, int whichButton) {
                                                    /**Event fire for delete operation on post**/
                                                    mainViewModel.deletePosts(model.getPost().getId());
                                                }
                                            })
                                            .setNegativeButton(android.R.string.no, null).show();
                                    break;
                                case R.id.saved:
                                    if (isAllowedSave) {
                                        Post post = model.getPost();
                                        mainViewModel.bookmarkPosts(post.getId(), post.getPostBookmark() == 0 ? "1" : "0");
                                    }
                                    break;
                                case R.id.download:
                                    String path = model.getPost().getAttachment();
                                    if (isAllowedDownload) {
                                        if (!model.getPost().getType().equals(PostType.TEXT.getValue()) && path != null) {
                                            if (!checkPermission(Manifest.permission.READ_EXTERNAL_STORAGE) && !checkPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                                                ActivityCompat.requestPermissions(
                                                        requireActivity(),
                                                        new String[]{
                                                                Manifest.permission.READ_EXTERNAL_STORAGE,
                                                                Manifest.permission.WRITE_EXTERNAL_STORAGE
                                                        },
                                                        201
                                                );
                                                model = null;
                                                break;
                                            } else {
                                                startDownloadingFile(path, model);
                                            }
                                        } else if (model.getPost().getType().equals(PostType.TEXT.getValue())) {
                                            UtilityHelperKt.toast(requireActivity(), "Not allow on text type");
                                        } else {
                                            UtilityHelperKt.toast(requireActivity(), "Url is Empty");
                                            model = null;
                                            break;
                                        }
                                    }
                                    model = null;
                                    break;
                                case R.id.link:
                                    path = model.getPost() != null ? model.getPost().getAttachment() : null;
                                    /**@Code for open other app to share post path easily **/
                                    Intent sendIntent = new Intent();
                                    sendIntent.setAction(Intent.ACTION_SEND);
                                    sendIntent.putExtra(Intent.EXTRA_TEXT, path);
                                    sendIntent.setType("text/plain");
                                    Intent shareIntent = Intent.createChooser(sendIntent, null);
                                    startActivity(shareIntent);
//                                    if (!model.getPost().getType().equals(PostType.TEXT.getValue()) && path != null) {
//                                        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(CLIPBOARD_SERVICE);
//                                        ClipData clip = ClipData.newPlainText("label", path);
//                                        clipboard.setPrimaryClip(clip);
//                                        UtilityHelperKt.toast(requireActivity(), "Link Copied");
////                                clipboard.addPrimaryClipChangedListener(() -> );
//                                    } else if (model.getPost().getType().equals(PostType.TEXT.getValue())) {
//                                        String text = decodeEmoji(model.getPost().getCaption());
//                                        ClipboardManager clipboard = (ClipboardManager) requireActivity().getSystemService(CLIPBOARD_SERVICE);
//                                        ClipData clip = ClipData.newPlainText("label", text);
//                                        clipboard.setPrimaryClip(clip);
//                                        UtilityHelperKt.toast(requireActivity(), "Text Copied");
////                                clipboard.addPrimaryClipChangedListener(() -> );
//                                    } else {
//                                        UtilityHelperKt.toast(requireActivity(), "Url is Empty");
//                                    }
//                                    model = null;
//                                    break;
//                                case R.id.report:
//                                    startActivity(new Intent(requireActivity(), ReportActivity.class)
//                                            .putExtra("report_on", "post")
//                                            .putExtra("userid", model.getPost().getUserId())
//                                            .putExtra("postid", model.getPost().getId())
//                                    );
//                                    model = null;
//                                    break;

                            }
                            return true;
                        });
                        menu.show();
                    } else if (view.getId() == R.id.llComment) {
                        model = (PostModel) view.getTag();
                        Intent intent = new Intent(getActivity(), CommentActivity.class);
                        intent.putExtra("post_id", model.getPost().getId());
                        intent.putExtra("post_user_id", model.getPost().getUserId());
                        requestCommentLauncher.launch(intent);
                    } else if (view.getId() == R.id.like_icon) {
                        model = (PostModel) view.getTag();
                        if (UtilityHelperKt.getText(model.getUser().getId()).isEmpty()) {
                            UtilityHelperKt.toast(requireActivity(), "User Data Empty");
                            return;
                        }
                        int status = model.getPost().getPostLike();
                        model.getPost().setPostLike(status == 0 ? 1 : 0);
                        dashboardAdapter.notifyItemChanged(model.getPosition());
                        mainViewModel.likePosts(model.getPost().getId(), status == 0 ? "1" : "0");
                    } else if (view.getId() == R.id.like) {
                        model = (PostModel) view.getTag();
                        if (UtilityHelperKt.getText(model.getUser().getId()).isEmpty()) {
                            UtilityHelperKt.toast(requireActivity(), "User Data Empty");
                            return;
                        }
                        Fragment fragment = new LikedUsersFragment();
                        Bundle bu = new Bundle();
                        bu.putString("post_id", model.getPost().getId());
                        fragment.setArguments(bu);
                        ((MainActivity) requireActivity()).setCurrentFragment(fragment, "");
                    } else if (view.getId() == R.id.bookmark) {
                        model = (PostModel) view.getTag();
                        if (UtilityHelperKt.getText(model.getUser().getId()).isEmpty()) {
                            UtilityHelperKt.toast(requireActivity(), "User Data Empty");
                            return;
                        }
                        mainViewModel.bookmarkPosts(model.getPost().getId(), model.getPost().getPostBookmark() == 0 ? "1" : "0");
                    } else if (view.getId() == R.id.share) {
                        PostModel model = (PostModel) view.getTag();
                        Post post = model.getPost();
                        User user = model.getUser();
                        if (UtilityHelperKt.getText(model.getUser().getId()).isEmpty()) {
                            UtilityHelperKt.toast(requireActivity(), "User Data Empty");
                            return;
                        }
                        String profileImage = user.getImage();
                        String path = model.getPost().getAttachment();
                        requestLauncher.launch(new Intent(requireActivity(), PostShareActivity.class)
                                .putExtra("post_id", post.getId())
                                .putExtra("user_id", post.getUserId())
                                .putExtra("type", post.getType())
                                .putExtra("caption", post.getCaption())
                                .putExtra("created", post.getCreated())
                                .putExtra("username", user.getUsername())
                                .putExtra("fullname", user.getFullName())
                                .putExtra("attachment", path)
                                .putExtra("path", model.getPost().getAttachmentRaw())
                                .putExtra("postType", post.getPostType())
                                .putExtra("profileImage", profileImage));
                    }
                }, postModelList, userSuggestionModelList, mainViewModel.getUser().getId(),
                new Handler.Callback() {
                    @Override
                    public boolean handleMessage(@NonNull Message message) {
                        switch (message.what) {
                            case 1: {
                                Bundle bu = message.getData();
                                String userid = bu.getString("userid");
                                mainViewModel.sendFollowRequest(userid);
                            }
                            case 2:
                            {
                                Bundle bu = message.getData();
                                String userid = bu.getString("userid");
                                if (mainViewModel.getUser().getId().equals(UtilityHelperKt.getText(userid))) {
                                    ((MainActivity) requireActivity()).setCurrentFragment(new ProfileFragment(), "");
                                }
                                if (UtilityHelperKt.getText(userid).isEmpty()) {
                                    UtilityHelperKt.toast(requireActivity(), "User Data Empty");
                                }
                                Fragment fragment = new ProfileOtherFragment();
                                Bundle fbu = new Bundle();
                                fbu.putString("userid", userid);
                                fragment.setArguments(fbu);
                                ((MainActivity) requireActivity()).setCurrentFragment(fragment, "");
                            }
                        }
                        return true;
                    }
                }
        );
        bi.recycler.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);

                if (newState == RecyclerView.SCROLL_STATE_IDLE) {
                    int firstVisible = layoutManager.findFirstVisibleItemPosition();
                    int lastVisible = layoutManager.findLastVisibleItemPosition();
                    int itemsCount = lastVisible - firstVisible + 1;
                    int screenCenter = requireActivity().getResources().getDisplayMetrics().widthPixels / 2;
                    int minCenterOffset = Integer.MAX_VALUE;
                    int middleItemIndex = 0;
                    for (int i = 0; i < itemsCount; i++) {
                        if (layoutManager.getChildAt(i) == null) return;
                        View listItem = layoutManager.getChildAt(i);
                        int topOffset = listItem.getTop();
                        int bottomOffset = listItem.getBottom();
                        int centerOffset = Math.abs(topOffset - screenCenter) + Math.abs(bottomOffset - screenCenter);
                        if (minCenterOffset > centerOffset) {
                            minCenterOffset = centerOffset;
                            middleItemIndex = i + firstVisible;
                        }
                    }
//                    for (int i = 0; i < postModelList.size(); i++) {
//                        if (lastVisible != i)
//                            postModelList.get(i).getPost().setMute(false);
//                        else
//                            postModelList.get(i).getPost().setMute(true);
//                    }

                    currentItem = middleItemIndex;

                    handler.postDelayed(myRunnable, 3000);
                    if (!isLoading) {
                        if (lastVisible == postModelList.size() - 1) {
                            try {
                                handler.removeCallbacks(myRunnable);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                            //bottom of list!
                            page += 1;
                            mainViewModel.getPosts("" + (page));
                            isLoading = true;
                        }
                    }
                } else {
                    try {
                        handler.removeCallbacks(myRunnable);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }

            @Override
            public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
            }
        });
        bi.recycler.setAdapter(dashboardAdapter);
    }

    private Boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(
                requireActivity(),
                permission
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void startDownloadingFile(String uri, PostModel model) {
        String ext = uri.substring(uri.lastIndexOf(".") + 1);
        String type = null;
        switch (ext) {
            case "png":
                type = "PNG";
                break;
            case "jpg":
                type = "JPG";
                break;
            case "mp4":
                type = "MP4";
                break;
            case "pdf":
                type = "PDF";
                break;
            default:
                UtilityHelperKt.toast(requireActivity(), "File Not Supported");
                return;
        }
        if (model.getPost().getType().equals(PostType.VIDEO.getValue())) {
            type = "MP4";
            ext = "mp4";
        }
        Constraints constraints = new Constraints.Builder()
                .setRequiredNetworkType(NetworkType.CONNECTED)
                .setRequiresStorageNotLow(true)
                .setRequiresBatteryNotLow(true)
                .build();
        Data.Builder data = new Data.Builder();
        data.putString(KEY_FILE_NAME, System.currentTimeMillis() + "." + ext);
        data.putString(KEY_FILE_URL, uri);
        data.putString(KEY_FILE_TYPE, type);

        OneTimeWorkRequest oneTimeWorkRequest = new OneTimeWorkRequest.Builder(FileDownloadWorker.class)
                .setConstraints(constraints)
                .setInputData(data.build())
                .build();

        workManager.enqueue(oneTimeWorkRequest);

        workManager.getWorkInfoByIdLiveData(oneTimeWorkRequest.getId())
                .observe(getViewLifecycleOwner(), new Observer<WorkInfo>() {
                            @Override
                            public void onChanged(WorkInfo info) {
                                if (info.getState() == WorkInfo.State.SUCCEEDED) {
                                    String uri = info.getOutputData().getString(KEY_FILE_URI);
                                    if (uri != null) {
                                        UtilityHelperKt.toast(DashboardFragment.this.requireActivity(), "File saved to : " + uri);
                                    }
                                } else if (info.getState() == WorkInfo.State.FAILED) {
                                    UtilityHelperKt.toast(DashboardFragment.this.requireActivity(), "Download in failed");
                                } else if (info.getState() == WorkInfo.State.RUNNING) {
                                    UtilityHelperKt.toast(DashboardFragment.this.requireActivity(), "Download in progress...");
                                }

                            }
                        }
                );
    }

    @Override
    public void onStarted(@NonNull String tag) {
        if (tag.equals(TAG_POST)) {
            bi.progressCircular.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if (tag.equals(TAG_POST)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<PostModel>>() {
                        }.getType();

                        List<PostModel> list = gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type);
                        if (list.size() > 0) {
                            isLoading = false;
                        }
                        for (int i = 0; i < list.size(); i++) {
                            if (list.get(i).getPost().getType().equals(PostType.VIDEO.getValue())) {
                                list.get(i).getPost().setMute(true);
                            }
                            postModelList.add(list.get(i));
                        }
                        dashboardAdapter.notifyDataSetChanged();
                        handler.postDelayed(myRunnable, 3000);
                    } else {
//                        Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
        if (tag.equals(TAG_USER_SUGGESTION)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<UserSuggestionModel>>() {
                        }.getType();

                        List<UserSuggestionModel> list = gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type);
                        if (list.size() > 0) {
                            isLoading = false;
                        }
                        for (int i = 0; i < list.size(); i++) {
                            userSuggestionModelList.add(list.get(i));
                        }
                        dashboardAdapter.notifyDataSetChanged();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        } else if (tag.equals(TAG_POST_FOR_UPDATE)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<PostModel>() {
                        }.getType();
                        PostModel new_model = gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type);
                        if (model != null) {
                            new_model.setPosition(model.getPosition());
                            postModelList.set(new_model.getPosition(), new_model);
                            dashboardAdapter.notifyItemChanged(new_model.getPosition());
                            model = null;
                        }
                        if (viewModel != null) {
                            new_model.setPosition(viewModel.getPosition());
                            postModelList.set(new_model.getPosition(), new_model);
                            dashboardAdapter.notifyItemChanged(new_model.getPosition());
                            viewModel = null;
                        }

                    } else {
                        Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        } else if (tag.equals(TAG_DELETE_POST)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (model != null)
                        if (responseData.getCode() == 200) {
                            bi.progressCircular.setVisibility(View.GONE);
                            if (model != null) {
                                Toast.makeText(getActivity(), "Post Deleted Successfully!", Toast.LENGTH_LONG).show();
                                postModelList.remove(model.getPosition());
                                model = null;
                                dashboardAdapter.notifyDataSetChanged();
                            }
                        } else {
                            Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                        }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });

        } else if (tag.equals(TAG_BOOKMARK)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (model != null)
                        if (responseData.getCode() == 200) {
                            if (model != null) {
                                mainViewModel.getUserProfile(model.getUser().getId());
                                int bookmark = model.getPost().getPostBookmark() > 0 ? 0 : 1;
                                model.getPost().setPostBookmark(bookmark);
                                postModelList.set(model.getPosition(), model);
                                dashboardAdapter.notifyItemChanged(model.getPosition());
                                model = null;
                            }
                            mainViewModel.showBookmarkedPosts();
                        } else {
                            Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                        }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });

        } else if (tag.equals(TAG_LIKE)) {
            apiResponse.observe(this, responseData -> {
                try {
                    if (model != null)
                        if (responseData.getCode() == 200) {
                            Gson gson = new Gson();
                            JsonObject js = gson.toJsonTree(responseData.getMsg()).getAsJsonObject();
                            JSONObject likeResponse = new JSONObject(js.toString());
                            int likeCount = likeResponse.getInt("likeCounts");
                            model.getPost().setTotalLikes(likeCount);
                            dashboardAdapter.notifyItemChanged(model.getPosition());
                        } else {
                            int status = model.getPost().getPostLike();
                            model.getPost().setPostLike(status == 0 ? 1 : 0);
                            dashboardAdapter.notifyItemChanged(model.getPosition());
                            Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                        }
                } catch (Exception e) {
                    int status = model.getPost().getPostLike();
                    model.getPost().setPostLike(status == 0 ? 1 : 0);
                    dashboardAdapter.notifyItemChanged(model.getPosition());
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        } else if (tag.equals(TAG_VIEW_COUNT)) {
            apiResponse.observe(this, responseData -> {
                try {
                    if (viewModel != null)
                        if (responseData.getCode() == 200) {
                            Gson gson = new Gson();
                            JsonObject js = gson.toJsonTree(responseData.getMsg()).getAsJsonObject();
                            JSONObject viewCountResponse = new JSONObject(js.toString());
                            int viewCount = viewCountResponse.getInt("viewCounts");
                            viewModel.getPost().setTotalViews(viewCount);
                            dashboardAdapter.notifyItemChanged(viewModel.getPosition());
                        } else {
                            Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                        }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        } else if (tag.equals(TAG_SHOW_ALL_BOOKMARK)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    savedPostList.clear();
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<Bookmark>>() {
                        }.getType();
                        savedPostList.addAll(gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type));
                    } else {
//                        Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }

            });
        }
        else if(tag.equals(TAG_FOLLOW)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
//                    if (responseData.getCode() == 200) {
//                        Toast.makeText(getActivity(), "Request Send", Toast.LENGTH_LONG).show();
//                    } else {
                    if (responseData.getCode() != 200){
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
        bi.progressCircular.setVisibility(View.VISIBLE);
        Toast.makeText(getActivity(), message, Toast.LENGTH_SHORT).show();
    }

//    Runnable payVideo = () ->{
//        try {
//            if (currentItem > 0) {
//                PostModel prevViewModel = postModelList.get(currentItem - 1);
//                prevViewModel.getPost().setMute(false);
//                dashboardAdapter.notifyItemChanged(prevViewModel.getPosition());
//            }
//            if (!viewModel.getPost().isMute()) {
//                viewModel.getPost().setMute(true);
//                dashboardAdapter.notifyItemChanged(viewModel.getPosition());
//            }
//            if (currentItem < postModelList.size()-1) {
//                PostModel nextViewModel = postModelList.get(currentItem - 1);
//                nextViewModel.getPost().setMute(false);
//                dashboardAdapter.notifyItemChanged(nextViewModel.getPosition());
//            }
//        } catch (Exception e) {
//            e.printStackTrace();
//        }
//    };

    Runnable myRunnable = () -> {
        try {
            viewModel = postModelList.get(currentItem);
            if (!lastPostId.equals(viewModel.getPost().getId())) {
                lastPostId = viewModel.getPost().getId();
                mainViewModel.viewCountPosts(viewModel.getPost().getId());
            } else {
                viewModel = null;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    };
}
