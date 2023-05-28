package com.app.appsinrek.player_view.adapter;

import static com.app.appsinrek.utils.CommonUtils.decodeEmoji;
import static com.app.appsinrek.utils.CommonUtils.print;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Html;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ablanco.zoomy.Zoomy;
import com.app.appsinrek.R;
import com.app.appsinrek.databinding.RowDashboardBinding;
import com.app.appsinrek.models.User;
import com.app.appsinrek.models.post.Post;
import com.app.appsinrek.models.post.PostModel;
import com.app.appsinrek.models.post.Shared;
import com.app.appsinrek.models.post.UserSuggestionModel;
import com.app.appsinrek.network.API_LINKS;
import com.app.appsinrek.player_view.enums.PostType;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_VIEW_TYPE_WITH_INNER_RECYCLER = 1;
    private final LayoutInflater inflater;
    private final List<PostModel> postModelList;
    private final List<UserSuggestionModel> userSuggestionModelList;
    private Context mActivity;
    static Handler.Callback callback;
    SuggestionAdapter suggestionAdapter;
    //    private InnerRecyclerViewAdapter suggestionUserAdapter;
    String user_id;
    View.OnClickListener onClickListener;
    Gson gson = new Gson();
    ViewGroup parentViewGroup;

    public PostAdapter(Context mActivity, View.OnClickListener onClickListener,
                       List<PostModel> postModelList, List<UserSuggestionModel> userSuggestionModelList, String id,
                       Handler.Callback callback) {
        setHasStableIds(true);
        this.postModelList = postModelList;
        this.userSuggestionModelList = userSuggestionModelList;
        inflater = LayoutInflater.from(mActivity);
        this.mActivity = mActivity;
        this.onClickListener = onClickListener;
        user_id = id;
        this.callback = callback;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        parentViewGroup = parent;
        RowDashboardBinding itemView = RowDashboardBinding.inflate(inflater);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        itemView.fram.setLayoutParams(lp);
        return new ItemViewHolder(itemView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            RowDashboardBinding bi = itemViewHolder.bi;
            PostModel model = postModelList.get(position);
            model.setPosition(position);
            if (position == 1 && userSuggestionModelList != null) {
                bi.suggestionsRow.setVisibility(View.VISIBLE);
                suggestionAdapter = new SuggestionAdapter(parentViewGroup.getContext(), view -> {
                    if (view.getId() == R.id.mtbFollow) {
                        Bundle bu = (Bundle) view.getTag();
                        Message replyMessage = Message.obtain();
                        replyMessage.setData(bu);
                        replyMessage.what = 1;
                        if (this.callback != null) {
                            this.callback.handleMessage(replyMessage);
                            this.userSuggestionModelList.remove(bu.getInt("position"));
                            suggestionAdapter.notifyDataSetChanged();
                        }
                    }
                    if (view.getId() == R.id.close_btn) {
                        int r_position = (int) view.getTag();
                        this.userSuggestionModelList.remove(r_position);
                        suggestionAdapter.notifyDataSetChanged();
                    }
                    if (view.getId() == R.id.profile_image) {
                        Bundle bu = (Bundle) view.getTag();
                        Message replyMessage = Message.obtain();
                        replyMessage.setData(bu);
                        replyMessage.what = 2;
                        if (this.callback != null) {
                            this.callback.handleMessage(replyMessage);
                        }
                    }
                }, userSuggestionModelList);
                bi.innerRecyclerView.setAdapter(suggestionAdapter);
            } else {
                bi.suggestionsRow.setVisibility(View.GONE);
            }
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
            holder.itemView.setTag(model);
            holder.itemView.setOnClickListener(onClickListener);
            String path = model.getPost().getAttachment();
            bi.id.setText(model.getPost().getId());
            bi.more.setTag(model);
            bi.more.setOnClickListener(onClickListener);
            bi.profileImage.setTag(model);
            if (isAllowedProfile) {
                bi.profileImage.setOnClickListener(onClickListener);
            }
            bi.username.setTag(model);
            if (isAllowedProfile) {
                bi.username.setOnClickListener(onClickListener);
            }
            bi.share.setTag(model);
            bi.share.setOnClickListener(onClickListener);
            bi.llComment.setTag(model);
            if (isAllowedComment) {
                bi.llComment.setOnClickListener(onClickListener);
            }
            bi.like.setText(model.getPost().getTotalLikes() > 0 ? String.format("%d", model.getPost().getTotalLikes()) : "");
            bi.comments.setText(!model.getPost().getTotalComments().equals("0") ? String.format("%s", model.getPost().getTotalComments()) : "");
            bi.views.setText(model.getPost().getTotalViews() > 0 ? String.format("%d", model.getPost().getTotalViews()) : "");
            bi.date.setText(model.getPost().getCreated());
            String username = UtilityHelperKt.getText(model.getUser().getUsername());
            bi.username.setText(username);
            bi.likeIcon.setTag(postModelList.get(position));
            bi.likeIcon.setOnClickListener(onClickListener);
            bi.like.setTag(postModelList.get(position));
            if (isAllowedLike) {
                bi.like.setOnClickListener(onClickListener);
            }
            bi.bookmark.setTag(postModelList.get(position));
            if (isAllowedSave) {
                bi.bookmark.setOnClickListener(onClickListener);
            }
            if (model.getUser().getStatus() == "Online")
                bi.onlineStatus.setVisibility(View.VISIBLE);
            bi.profileImage.setImageResource(R.drawable.user2);
            if (!UtilityHelperKt.getText(model.getUser().getImage()).isEmpty()) {
                Glide.with(mActivity)
                        .load(API_LINKS.BASE_IMAGE_URL + model.getUser().getImage())
                        .centerCrop()
                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                        .optionalFitCenter()
                        .placeholder(R.drawable.user2)
                        .into(bi.profileImage);
            }
            if (shared == null && path != null && (model.getPost().getType().equals(PostType.IMAGE.getValue()) || model.getPost().getType().equals(PostType.VIDEO.getValue()))) {
                String caption = decodeEmoji(model.getPost().getCaption());
                bi.tvCaptionBottom.setVisibility(View.VISIBLE);
                String moreBtnHTML = "<font color='#ea4335'><b>more</b></font>";
                String lessBtnHTML = "<font color='#ea4335'> <b>less</b></font>";
                String usernameHTML = "<font color='#222222'><b>"+username+" </b></font>";

                Spanned fullText = Html.fromHtml(usernameHTML + caption);
                if (caption.length() > 100) {
                    Spanned abbreviationText = Html.fromHtml(usernameHTML + (caption.substring(0, 100)) + "..." + moreBtnHTML);
                    bi.tvCaptionBottom.setText(abbreviationText);
                } else {
                    bi.tvCaptionBottom.setText(fullText);
                }
                bi.tvCaptionBottom.setOnClickListener(view -> {
                    if (bi.tvCaptionBottom.getText().toString().endsWith("more")) {
                        Spanned fullTextWithMore = Html.fromHtml(usernameHTML + caption + lessBtnHTML);
                        bi.tvCaptionBottom.setText(fullTextWithMore);
                    } else {
                        if (caption.length() > 100) {
                            Spanned abbreviationText = Html.fromHtml(usernameHTML + (caption.substring(0, 100)) + "..." + moreBtnHTML);
                            bi.tvCaptionBottom.setText(abbreviationText);
                        } else {
                            bi.tvCaptionBottom.setText(fullText);
                        }
                    }
                });
                if (model.getPost().getType().equals(PostType.VIDEO.getValue())) {
                    bi.videoPost.setTag(model);
                    bi.videoPost.setOnClickListener(onClickListener);
                    bi.playButton.setTag(model);
                    bi.playButton.setOnClickListener(onClickListener);
                    Glide.with(mActivity)
                            .load(path)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .error(R.drawable._1)
                            .placeholder(R.drawable._1)
                            .into(bi.videoPost);
                } else {
                    bi.imagePost.setTag(model);
                    bi.imagePost.setOnClickListener(onClickListener);
                    Glide.with(mActivity)
                            .load(path)
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .error(R.drawable._1)
                            .placeholder(R.drawable._1)
                            .into(bi.imagePost);
                }
            } else {
                bi.tvCaption.setVisibility(View.VISIBLE);
                String caption = decodeEmoji(model.getPost().getCaption());
                String moreBtnHTML = "<font color='#ea4335'><b>more</b></font>";
                String lessBtnHTML = "<font color='#ea4335'> <b>less</b></font>";
                Spanned fullText = Html.fromHtml( caption);
                if (caption.length() > 100) {
                    Spanned abbreviationText = Html.fromHtml((caption.substring(0, 100)) + "..." + moreBtnHTML);
                    bi.tvCaption.setText(abbreviationText);
                } else {
                    bi.tvCaption.setText(fullText);
                }
                bi.tvCaption.setOnClickListener(view -> {
                    if (bi.tvCaption.getText().toString().endsWith("more")) {
                        Spanned fullTextWithMore = Html.fromHtml(caption + lessBtnHTML);
                        bi.tvCaption.setText(fullTextWithMore);
                    } else {
                        if (caption.length() > 100) {
                            Spanned abbreviationText = Html.fromHtml((caption.substring(0, 100)) + "..." + moreBtnHTML);
                            bi.tvCaption.setText(abbreviationText);
                        } else {
                            bi.tvCaption.setText(fullText);
                        }
                    }
                });
//                if (caption.length() > 100) {
//                    Spanned moreTxt = Html.fromHtml(caption.substring(0, 100) + "..." + "<font color='blue'><u>more</u></font>");
//                    bi.tvCaption.setText(moreTxt);
//                } else {
//                    bi.tvCaption.setText(caption);
//                }
//                bi.tvCaption.setOnClickListener(view -> {
//                    if (bi.tvCaption.getText().toString().endsWith("more")) {
//                        bi.tvCaption.setText(caption);
//                    } else {
//                        if (caption.length() > 100) {
//                            Spanned moreTxt = Html.fromHtml(caption.substring(0, 100) + "..." + "<font color='blue'><u>more</u></font>");
//                            bi.tvCaption.setText(moreTxt);
//                        } else {
//                            bi.tvCaption.setText(caption);
//                        }
//                    }
//                });
                holder.setIsRecyclable(true);
            }
            setUi(bi, postModelList.get(position), path, shared);
//            bi.tvCaption.setShowingLine(2);
//            bi.tvCaption.addShowMoreText("More");
//            bi.tvCaptionBottom.setShowingLine(2);
//            bi.tvCaptionBottom.addShowMoreText("More");
        }
    }

    private void setUi(RowDashboardBinding bi, PostModel postModel, String path, Shared shared) {

        if (bi.tvCaption.getVisibility() == View.VISIBLE)
            bi.tvCaptionBlock.setVisibility(UtilityHelperKt.getText(postModel.getPost().getCaption()).isEmpty() ? View.GONE : View.VISIBLE);
        if (bi.tvCaptionBottom.getVisibility() == View.VISIBLE)
            bi.tvCaptionBlockBottom.setVisibility(UtilityHelperKt.getText(postModel.getPost().getCaption()).isEmpty() ? View.GONE : View.VISIBLE);

        bi.likeIcon.setImageResource(postModel.getPost().getPostLike() == 0 ? R.drawable.like : R.drawable.liked);
        bi.bookmark.setVisibility(postModel.getPost().getPostBookmark() == 1 ? View.VISIBLE : View.GONE);
        bi.imagePost.setVisibility((shared == null && path != null && !path.isEmpty() && postModel.getPost().getType().equals(PostType.IMAGE.getValue())) ? View.VISIBLE : View.GONE);
        bi.videoPost.setVisibility((shared == null && path != null && !path.isEmpty() && postModel.getPost().getType().equals(PostType.VIDEO.getValue())) ? View.VISIBLE : View.GONE);
        bi.playButton.setVisibility(postModel.getPost().getType().equals(PostType.VIDEO.getValue()) ? View.VISIBLE : View.GONE);
        bi.share.setVisibility((postModel.getPost().getUserId().equals(user_id) || shared != null) ? View.GONE : View.VISIBLE);
        bi.rlShare.setVisibility(shared != null ? View.VISIBLE : View.GONE);
        if (bi.imagePost.getVisibility() == View.GONE && bi.playButton.getVisibility() == View.GONE) {
            bi.fr.setVisibility(View.GONE);
        } else {
            Zoomy.Builder builder = new Zoomy.Builder((Activity) mActivity).target(bi.imagePost);
            builder.register();
        }

        if (shared != null) {
            setSharedUi(bi, shared, postModel);
        }
    }

    private void setSharedUi(RowDashboardBinding bi, Shared shared, PostModel model) {
        Post post = shared.getPost();
        User user = shared.getUser();
        PostModel postModel = new PostModel();
        postModel.setPost(post);
        postModel.setUser(user);
        String path = post.getAttachment();
        bi.rlShare.setTag(model);
        bi.rlShare.setOnClickListener(onClickListener);
        bi.profPhotoId.setTag(postModel);
        bi.profPhotoId.setOnClickListener(onClickListener);
        bi.shareUsernameId.setTag(postModel);
        bi.shareUsernameId.setOnClickListener(onClickListener);
        bi.shareTimeAgo.setText(UtilityHelperKt.convertDateTime(post.getCreated(), "yyyy-MM-dd hh:mm:ss", "dd MMM, hh:mm aa"));
        bi.shareUsernameId.setText(UtilityHelperKt.getText(user.getUsername()));
        bi.shareCaption.setText(decodeEmoji(post.getCaption()));
        bi.profPhotoId.setImageResource(R.drawable.user2);
        if (!UtilityHelperKt.getText(user.getImage()).isEmpty()) {
            Glide.with(mActivity)
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
        bi.playButtonShare.setVisibility(post.getType().equals(PostType.VIDEO.getValue()) ? View.VISIBLE : View.GONE);
        if (path != null && post.getType().equals(PostType.VIDEO.getValue())) {

        }

        if (bi.imagePostShare.getVisibility() == View.GONE && bi.playButton.getVisibility() == View.GONE) {
            bi.frShare.setVisibility(View.GONE);
        }
        bi.share.setVisibility(View.GONE);
        if (path != null && (post.getType().equals(PostType.IMAGE.getValue()) /**|| post.getType().equals(PostType.VIDEO.getValue())**/)) {
            Glide.with(mActivity)
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

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return super.getItemViewType(position);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        RowDashboardBinding bi;

        public ItemViewHolder(@NonNull RowDashboardBinding bi) {
            super(bi.getRoot());
            this.bi = bi;
        }
    }

    private String getFileTypeFromURL(String url) {
        String[] splitedArray = url.split("\\.");
        String lastValueOfArray = splitedArray[splitedArray.length - 1];
        if (lastValueOfArray.equals("mp4") || lastValueOfArray.equals("flv") || lastValueOfArray.equals("m4a") || lastValueOfArray.equals("3gp") || lastValueOfArray.equals("mkv")) {
            return "video";
        } else if (lastValueOfArray.equals("mp3") || lastValueOfArray.equals("ogg")) {
            return "audio";
        } else if (lastValueOfArray.equals("jpg") || lastValueOfArray.equals("png") || lastValueOfArray.equals("gif")) {
            return "photo";
        } else {
            return "";
        }
    }

    public static void makeTextViewResizable(final TextView tv, final int maxLine, final String expandText, final boolean viewMore) {
        if (tv.getTag() == null) {
            tv.setTag(tv.getText());
        }
        ViewTreeObserver vto = tv.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @SuppressWarnings("deprecation")
            @Override
            public void onGlobalLayout() {

                ViewTreeObserver obs = tv.getViewTreeObserver();
                obs.removeGlobalOnLayoutListener(this);
                if (maxLine == 0) {
                    int lineEndIndex = tv.getLayout().getLineEnd(0);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else if (maxLine > 0 && tv.getLineCount() >= maxLine) {
                    int lineEndIndex = tv.getLayout().getLineEnd(maxLine - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex - expandText.length() + 1) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, maxLine, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                } else {
                    int lineEndIndex = tv.getLayout().getLineEnd(tv.getLayout().getLineCount() - 1);
                    String text = tv.getText().subSequence(0, lineEndIndex) + " " + expandText;
                    tv.setText(text);
                    tv.setMovementMethod(LinkMovementMethod.getInstance());
                    tv.setText(
                            addClickablePartTextViewResizable(Html.fromHtml(tv.getText().toString()), tv, lineEndIndex, expandText,
                                    viewMore), TextView.BufferType.SPANNABLE);
                }
            }
        });
    }

    private static CharSequence addClickablePartTextViewResizable(final Spanned strSpanned, final TextView tv, final int maxLine, final String spanableText, final boolean viewMore) {
        String str = strSpanned.toString();
        SpannableStringBuilder ssb = new SpannableStringBuilder(strSpanned);

        if (str.contains(spanableText)) {


            ssb.setSpan(new MySpannable(false) {
                @Override
                public void onClick(View widget) {
                    if (viewMore) {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, -1, "See Less", false);
                    } else {
                        tv.setLayoutParams(tv.getLayoutParams());
                        tv.setText(tv.getTag().toString(), TextView.BufferType.SPANNABLE);
                        tv.invalidate();
                        makeTextViewResizable(tv, 3, ".. See More", true);
                    }
                }
            }, str.indexOf(spanableText), str.indexOf(spanableText) + spanableText.length(), 0);

        }
        return ssb;

    }
}


