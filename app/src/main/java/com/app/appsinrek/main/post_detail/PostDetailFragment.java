package com.app.appsinrek.main.post_detail;

import static com.app.appsinrek.main.downloader.KEY_FILE_NAMEKt.KEY_FILE_NAME;
import static com.app.appsinrek.main.downloader.KEY_FILE_NAMEKt.KEY_FILE_TYPE;
import static com.app.appsinrek.main.downloader.KEY_FILE_NAMEKt.KEY_FILE_URI;
import static com.app.appsinrek.main.downloader.KEY_FILE_NAMEKt.KEY_FILE_URL;
import static com.app.appsinrek.utils.AppConstants.TAG_BOOKMARK;
import static com.app.appsinrek.utils.AppConstants.TAG_DELETE_POST;
import static com.app.appsinrek.utils.AppConstants.TAG_LIKE;
import static com.app.appsinrek.utils.AppConstants.TAG_POST;
import static com.app.appsinrek.utils.AppConstants.TAG_POST_DETAIL;
import static com.app.appsinrek.utils.AppConstants.TAG_POST_FOR_UPDATE;
import static com.app.appsinrek.utils.AppConstants.TAG_SHOW_ALL_BOOKMARK;
import static com.app.appsinrek.utils.AppConstants.TAG_USER_SUGGESTION;
import static com.app.appsinrek.utils.AppConstants.TAG_VIEW_COUNT;
import static com.app.appsinrek.utils.CommonUtils.decodeEmoji;
import static com.app.appsinrek.utils.CommonUtils.print;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;
import androidx.work.Constraints;
import androidx.work.Data;
import androidx.work.NetworkType;
import androidx.work.OneTimeWorkRequest;
import androidx.work.WorkInfo;
import androidx.work.WorkManager;

import com.ablanco.zoomy.Zoomy;
import com.app.appsinrek.R;
import com.app.appsinrek.databinding.LayoutPostDetailBinding;
import com.app.appsinrek.main.MainActivity;
import com.app.appsinrek.main.downloader.FileDownloadWorker;
import com.app.appsinrek.main.post_detail.model.PostDetailModel;
import com.app.appsinrek.main.profile.ProfileFragment;
import com.app.appsinrek.main.profile_other.ProfileOtherFragment;
import com.app.appsinrek.main.savepost.models.Bookmark;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.models.User;
import com.app.appsinrek.models.post.Post;
import com.app.appsinrek.models.post.PostModel;
import com.app.appsinrek.models.post.Shared;
import com.app.appsinrek.models.post.UserSuggestionModel;
import com.app.appsinrek.network.API_LINKS;
import com.app.appsinrek.player_view.enums.PostType;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fitness.modval.interfaces.AuthListener;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import org.json.JSONObject;

import java.lang.reflect.Type;
import java.util.List;

public class PostDetailFragment extends Fragment implements AuthListener {
    public PostDetailFragment() {
    }

    WorkManager workManager;
    private boolean isLoading = false;
    public PostDetailModel model;
    private ActivityResultLauncher<Intent> requestLauncher;
    private ActivityResultLauncher<Intent> requestCommentLauncher;
    LayoutPostDetailBinding bi;
    private MainViewModel mainViewModel;
    PostDetailModel viewModel;
    String user_id;
    Gson gson = new Gson();
    View.OnClickListener onClickMore, onClickProfile,onClickUsername, onClickShare, onClickComment,
            onClickLikeIcon,onClickLike, onClickAllowSaved, onClickImagePost, onClickPlayButton;
    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestLauncher = registerForActivityResult(
                new ActivityResultContracts.StartActivityForResult(),
                result -> {
                    if (result.getResultCode() == Activity.RESULT_OK) {


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
                            PostDetailModel model_ = (PostDetailModel) b.getParcelable("post");
                            mainViewModel.getPostsForUpdate(model_.getPost().getId());
                        } else {
                            String userId = data.getStringExtra("user_id");
                            if (mainViewModel.getUser().getId().equals(UtilityHelperKt.getText(userId))) {
                                ((MainActivity) requireActivity()).setCurrentFragment(new ProfileFragment(), "");
                                return;
                            }
                            if (UtilityHelperKt.getText(userId).isEmpty()) {
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
        bi = DataBindingUtil.inflate(inflater, R.layout.layout_post_detail, container, false);
        user_id = mainViewModel.getUser().getId();
        onClickMore = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {

            }
        };
        onClickProfile = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {

            }
        };
        onClickUsername = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {

            }
        };
        onClickShare = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {

            }
        };
        onClickLikeIcon = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {

            }
        };
        onClickLike = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {

            }
        };
        onClickAllowSaved = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {

            }
        };
        onClickComment = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {

            }
        };
        onClickImagePost = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {

            }
        };
        onClickPlayButton = new View.OnClickListener() {
            @Override
            public void onClick(android.view.View view) {

            }
        };
        return bi.getRoot();
    }

    @Override
    public void onStart() {
        super.onStart();
        /**Load post detail fro api**/
        String post_id = getArguments().getString("post_id");
        mainViewModel.getPostsDetail(post_id );
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    private void setupUi() {
        boolean isAllowedProfile = model.getUser().getId().equals(user_id) || model.getPost().getWhoCanSeeMyProfile().equals("0") || (model.getPost().getWhoCanSeeMyProfile().equals("1") && model.getPost().getFollowing().equals("1")) || (model.getPost().getWhoCanSeeMyProfile().equals("3") && model.getPost().getFollower().equals("1"));
        boolean isAllowedComment = model.getUser().getId().equals(user_id) || model.getPost().getWhoCanCommentOnMyPost().equals("0") || (model.getPost().getWhoCanCommentOnMyPost().equals("1") && model.getPost().getFollowing().equals("1")) || (model.getPost().getWhoCanCommentOnMyPost().equals("3") && model.getPost().getFollower().equals("1"));
        boolean isAllowedDownload = model.getUser().getId().equals(user_id) || model.getPost().getWhoCanDownloadMyPost().equals("0") || (model.getPost().getWhoCanDownloadMyPost().equals("1") && model.getPost().getFollowing().equals("1")) || (model.getPost().getWhoCanDownloadMyPost().equals("3") && model.getPost().getFollower().equals("1"));
        boolean isAllowedSave = model.getUser().getId().equals(user_id) || model.getPost().getWhoCanSaveMyPost().equals("0") || (model.getPost().getWhoCanSaveMyPost().equals("1") && model.getPost().getFollowing().equals("1")) || (model.getPost().getWhoCanSaveMyPost().equals("3") && model.getPost().getFollower().equals("1"));
        boolean isAllowedLike = model.getUser().getId().equals(user_id) || model.getPost().getWhoCanLikeMyPost().equals("0") || (model.getPost().getWhoCanLikeMyPost().equals("1") && model.getPost().getFollowing().equals("1")) || (model.getPost().getWhoCanLikeMyPost().equals("3") && model.getPost().getFollower().equals("1"));

        Type type1 = new TypeToken<Shared>() {
        }.getType();
        JsonElement jsonElement = gson.toJsonTree(model.getShared());
        Shared shared = null;
        if (jsonElement instanceof JsonObject) {
            shared = gson.fromJson(gson.toJsonTree(model.getShared()).toString(), type1);
            if (shared != null) {
                print("Yes");
            }
        }

        String path = model.getPost().getAttachment();
        bi.id.setText(model.getPost().getId());
        bi.more.setOnClickListener(onClickMore);
        if (isAllowedProfile) {
            bi.profileImage.setOnClickListener(onClickProfile);
        }
        if (isAllowedProfile) {
            bi.username.setOnClickListener(onClickUsername);
        }
        bi.share.setOnClickListener(onClickShare);
        if (isAllowedComment) {
            bi.llComment.setOnClickListener(onClickComment);
        }
        bi.like.setText(model.getPost().getTotalLikes() > 0 ? String.format("%d", model.getPost().getTotalLikes()) : "");
        bi.comments.setText(!model.getPost().getTotalComments().equals("0") ? String.format("%s", model.getPost().getTotalComments()) : "");
        bi.views.setText(model.getPost().getTotalViews() > 0 ? String.format("%d", model.getPost().getTotalViews()) : "");
        bi.date.setText(model.getPost().getCreated());
        bi.username.setText(UtilityHelperKt.getText(model.getUser().getUsername()));
        bi.likeIcon.setOnClickListener(onClickLikeIcon);
        if (isAllowedLike) {
            bi.like.setOnClickListener(onClickLike);
        }
        if (isAllowedSave) {
            bi.bookmark.setOnClickListener(onClickAllowSaved);
        }
        if (model.getUser().getStatus() == "Online")
            bi.onlineStatus.setVisibility(View.VISIBLE);
        bi.profileImage.setImageResource(R.drawable.user2);
        if (!UtilityHelperKt.getText(model.getUser().getImage()).isEmpty()) {
            Glide.with(getActivity())
                    .load(API_LINKS.BASE_IMAGE_URL + model.getUser().getImage())
                    .centerCrop()
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .optionalFitCenter()
                    .placeholder(R.drawable.user2)
                    .into(bi.profileImage);
        }
        if (shared == null && path != null && (model.getPost().getType().equals(PostType.IMAGE.getValue()) || model.getPost().getType().equals(PostType.VIDEO.getValue()))) {
            bi.tvCaptionBottom.setVisibility(View.VISIBLE);
            String caption = decodeEmoji(model.getPost().getCaption());
            bi.tvCaptionBottom.setText(caption);
            bi.usernamePrefix.setText(UtilityHelperKt.getText(model.getUser().getUsername()));
            bi.imagePost.setOnClickListener(onClickImagePost);
            if (model.getPost().getType().equals(PostType.VIDEO.getValue())){
                bi.playButton.setOnClickListener(onClickPlayButton);
                bi.playButton.setVisibility(View.VISIBLE);
            }
            Glide.with(getActivity())
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .error(R.drawable._1)
                    .placeholder(R.drawable._1)
                    .into(bi.imagePost);
        } else {
            bi.tvCaption.setVisibility(View.VISIBLE);
            bi.tvCaption.setText(decodeEmoji(model.getPost().getCaption()));
        }
        setUi(bi, model, path, shared);
        bi.tvCaption.setShowingLine(2);
        bi.tvCaption.addShowMoreText("More");
        bi.tvCaptionBottom.setShowingLine(2);
        bi.tvCaptionBottom.addShowMoreText("More");
    }

    private void setUi(LayoutPostDetailBinding bi, PostDetailModel postModel, String path, Shared shared) {

        if (bi.tvCaption.getVisibility() == View.VISIBLE)
            bi.tvCaptionBlock.setVisibility(UtilityHelperKt.getText(postModel.getPost().getCaption()).isEmpty() ? View.GONE : View.VISIBLE);
        if (bi.tvCaptionBottom.getVisibility() == View.VISIBLE)
            bi.tvCaptionBlockBottom.setVisibility(UtilityHelperKt.getText(postModel.getPost().getCaption()).isEmpty() ? View.GONE : View.VISIBLE);

        bi.likeIcon.setImageResource(postModel.getPost().getPostLike() == 0 ? R.drawable.like : R.drawable.liked);
        bi.bookmark.setVisibility(postModel.getPost().getPostBookmark() == 1 ? View.VISIBLE : View.GONE);
        bi.imagePost.setVisibility((shared == null && path != null && !path.isEmpty() && (postModel.getPost().getType().equals(PostType.IMAGE.getValue()) || postModel.getPost().getType().equals(PostType.VIDEO.getValue()))) ? View.VISIBLE : View.GONE);
//        bi.playButton.setVisibility(shared == null && postModel.getPost().getType().equals(PostType.VIDEO.getValue()) ? View.VISIBLE : View.GONE);
        bi.share.setVisibility((postModel.getPost().getUserId().equals(user_id) || shared != null) ? View.GONE : View.VISIBLE);
        bi.rlShare.setVisibility(shared != null ? View.VISIBLE : View.GONE);
        if (bi.imagePost.getVisibility() == View.GONE /**&& bi.playButton.getVisibility() == View.GONE**/) {
            bi.fr.setVisibility(View.GONE);
        } else {
            Zoomy.Builder builder = new Zoomy.Builder((Activity) getActivity()).target(bi.imagePost);
            builder.register();
        }

        if (shared != null) {
            setSharedUi(bi, shared, postModel);
        }
    }

    private void setSharedUi(LayoutPostDetailBinding bi, Shared shared, PostDetailModel model) {
        Post post = shared.getPost();
        User user = shared.getUser();
        PostModel postModel = new PostModel();
        postModel.setPost(post);
        postModel.setUser(user);
        String path = post.getAttachment();
//        bi.rlShare.setOnClickListener(onClickListener);
//        bi.profPhotoId.setOnClickListener(onClickListener);
//        bi.shareUsernameId.setOnClickListener(onClickListener);
        bi.shareTimeAgo.setText(UtilityHelperKt.convertDateTime(post.getCreated(), "yyyy-MM-dd hh:mm:ss", "dd MMM, hh:mm aa"));
        bi.shareUsernameId.setText(UtilityHelperKt.getText(user.getUsername()));
        bi.shareCaption.setText(decodeEmoji(post.getCaption()));
        bi.profPhotoId.setImageResource(R.drawable.user2);
        if (!UtilityHelperKt.getText(user.getImage()).isEmpty()) {
            Glide.with(getActivity())
                    .load(API_LINKS.BASE_IMAGE_URL + user.getImage())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .centerCrop()
                    .optionalFitCenter()
                    .placeholder(R.drawable.user2)
                    .into(bi.profPhotoId);
        }
        bi.shareCaption.setVisibility(UtilityHelperKt.getText(post.getCaption()).isEmpty() ? View.GONE : View.VISIBLE);
        // bi.shareCaption.setVisibility(postModel.getPost().getType().equals(PostType.TEXT.getValue()) ? View.GONE : View.VISIBLE);
        //bi.tvShareTextPost.setVisibility(postModel.getPost().getType().equals(PostType.TEXT.getValue()) ? View.VISIBLE : View.GONE);
        bi.imagePostShare.setVisibility((path != null && !path.isEmpty() && (post.getType().equals(PostType.IMAGE.getValue()) || post.getType().equals(PostType.VIDEO.getValue()))) ? View.VISIBLE : View.GONE);
//        bi.playButtonShare.setVisibility(post.getType().equals(PostType.VIDEO.getValue()) ? View.VISIBLE : View.GONE);
        if (path != null && post.getType().equals(PostType.VIDEO.getValue())) {

        }

        if (bi.imagePostShare.getVisibility() == View.GONE && bi.playButton.getVisibility() == View.GONE) {
            bi.frShare.setVisibility(View.GONE);
        }
        bi.share.setVisibility(View.GONE);
        if (path != null && (post.getType().equals(PostType.IMAGE.getValue()) /**|| post.getType().equals(PostType.VIDEO.getValue())**/)) {
            Glide.with(getActivity())
                    .load(path)
                    .centerCrop()
//                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .optionalFitCenter()
                    .placeholder(R.drawable._1)
                    .into(bi.imagePostShare);
        } else {
//            bi.tvShareTextPost.setText(decodeEmoji(post.getCaption()));
            // TextView text =  bi.tvShareTextPost;
            //NoUnderlineClickSpanKt.setResizableText(text,decodeEmoji(post.getCaption()), 4, false,null);
        }
        bi.shareCaption.setShowingLine(2);
        bi.shareCaption.addShowMoreText("More");

    }

    private Boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(
                requireActivity(),
                permission
        ) == PackageManager.PERMISSION_GRANTED;
    }

    private void startDownloadingFile(String uri, PostDetailModel model) {
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
                                        UtilityHelperKt.toast(PostDetailFragment.this.requireActivity(), "File saved to : " + uri);
                                    }
                                } else if (info.getState() == WorkInfo.State.FAILED) {
                                    UtilityHelperKt.toast(PostDetailFragment.this.requireActivity(), "Download in failed");
                                } else if (info.getState() == WorkInfo.State.RUNNING) {
                                    UtilityHelperKt.toast(PostDetailFragment.this.requireActivity(), "Download in progress...");
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
        if (tag.equals(TAG_POST_DETAIL)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<PostDetailModel>() {
                        }.getType();
                        model = gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type);
                        setupUi();
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
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else if (tag.equals(TAG_POST_FOR_UPDATE)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<PostDetailModel>() {
                        }.getType();
                        PostDetailModel new_model = gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type);

                    } else {
                        Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                    }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else if (tag.equals(TAG_DELETE_POST)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (model != null)
                        if (responseData.getCode() == 200) {
                            bi.progressCircular.setVisibility(View.GONE);
                            if (model != null) {
                                Toast.makeText(getActivity(), "Post Deleted Successfully!", Toast.LENGTH_LONG).show();
                               //Back to previoust page
                                model = null;
                            }
                        } else {
                            Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                        }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });

        }
        else if (tag.equals(TAG_BOOKMARK)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (model != null)
                        if (responseData.getCode() == 200) {
                            if (model != null) {
                                mainViewModel.getUserProfile(model.getUser().getId());
                                int bookmark = model.getPost().getPostBookmark() > 0 ? 0 : 1;
                                model.getPost().setPostBookmark(bookmark);
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

        }
        else if (tag.equals(TAG_LIKE)) {
            apiResponse.observe(this, responseData -> {
                try {
                    if (model != null)
                        if (responseData.getCode() == 200) {
                            Gson gson = new Gson();
                            JsonObject js = gson.toJsonTree(responseData.getMsg()).getAsJsonObject();
                            JSONObject likeResponse = new JSONObject(js.toString());
                            int likeCount = likeResponse.getInt("likeCounts");
                            model.getPost().setTotalLikes(likeCount);
                        } else {
                            int status = model.getPost().getPostLike();
                            model.getPost().setPostLike(status == 0 ? 1 : 0);
                            Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                        }
                } catch (Exception e) {
                    int status = model.getPost().getPostLike();
                    model.getPost().setPostLike(status == 0 ? 1 : 0);
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else if (tag.equals(TAG_VIEW_COUNT)) {
            apiResponse.observe(this, responseData -> {
                try {
                    if (viewModel != null)
                        if (responseData.getCode() == 200) {
                            Gson gson = new Gson();
                            JsonObject js = gson.toJsonTree(responseData.getMsg()).getAsJsonObject();
                            JSONObject viewCountResponse = new JSONObject(js.toString());
                            int viewCount = viewCountResponse.getInt("viewCounts");
                            model.getPost().setTotalViews(viewCount);
                        } else {
                            Toast.makeText(getActivity(), responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                        }
                } catch (Exception e) {
                    Toast.makeText(getActivity(), e.toString(), Toast.LENGTH_LONG).show();
                }
            });
        }
        else if (tag.equals(TAG_SHOW_ALL_BOOKMARK)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
//                    savedPostList.clear();
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<Bookmark>>() {
                        }.getType();
//                        savedPostList.addAll(gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type));
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
}
