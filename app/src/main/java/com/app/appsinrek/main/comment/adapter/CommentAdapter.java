package com.app.appsinrek.main.comment.adapter;

import static com.app.appsinrek.utils.CommonUtils.decodeEmoji;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.RowComment2Binding;
import com.app.appsinrek.databinding.RowCommentBinding;
import com.app.appsinrek.main.comment.model.ChildPostComment;
import com.app.appsinrek.main.comment.model.CommentModel;
import com.app.appsinrek.network.API_LINKS;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ItemViewHolder> {

    private final LayoutInflater inflater;
    private Comment2Adapter adapter;
    private Context mActivity;
//    RowCommentBinding bi;
    View.OnClickListener onClickListener;
    View.OnLongClickListener onLongClickListener;
    static Handler.Callback callback;
    List<CommentModel> commentList;
    private ChildPostComment childPostComment;
    String post_user_id;
    String loginUserId;
    public CommentAdapter(Context mActivity, View.OnClickListener onClickListener, View.OnLongClickListener onLongClickListener,
                          Handler.Callback callback, List<CommentModel> commentList, String post_user_id, String loginUserId) {
        inflater = LayoutInflater.from(mActivity);
        this.mActivity=mActivity;
        this.onClickListener=onClickListener;
        this.onLongClickListener = onLongClickListener;
        this.commentList = commentList;
        this.post_user_id = post_user_id;
        this.loginUserId = loginUserId;
        this.callback = callback;
       }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowCommentBinding itemView = RowCommentBinding.inflate(inflater);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        itemView.fram.setLayoutParams(lp);
        return new ItemViewHolder(itemView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        RowCommentBinding bi = holder.bi;
        CommentModel model = commentList.get(position);
        Glide.with(mActivity)
                .load(API_LINKS.BASE_IMAGE_URL+UtilityHelperKt.getText(model.getUser().getImage()))
                .centerCrop()
                .optionalFitCenter()
                .placeholder(R.drawable.user2)
                .error(R.drawable.user2)
                .into(bi.profileImage);
        HashMap<Object,Object> holdItemTagMap = new HashMap();
        holdItemTagMap.put("show",(post_user_id.equals(loginUserId) || model.getPostComment().getUserId().equals(loginUserId))?true:false);
        holdItemTagMap.put("comment_id",model.getPostComment().getId());
        holdItemTagMap.put("bi",bi);
        holder.itemView.setTag(holdItemTagMap);
        holder.itemView.setOnLongClickListener(onLongClickListener);
//        view -> {
//            if(post_user_id.equals(loginUserId))
//                UtilityHelperKt.show(bi.llDelete);
//            else if(model.getPostComment().getUserId().equals(loginUserId))
//                UtilityHelperKt.show(bi.llDelete);
//            return false;
//        }
        bi.profileImage.setTag(model.getUser().getId());
        bi.profileImage.setOnClickListener(onClickListener);
        bi.username.setTag(model.getUser().getId());
        bi.username.setOnClickListener(onClickListener);
        HashMap<Object,Object> deleteBtnTagMap = new HashMap();
        deleteBtnTagMap.put("comment_id",model.getPostComment().getId());
        deleteBtnTagMap.put("bi",bi);
//        bi.ivDelete.setTag(deleteBtnTagMap);
//        bi.ivDelete.setOnClickListener(onClickListener);
//        bi.ivCancel.setOnClickListener(view -> UtilityHelperKt.hide(bi.llDelete));
        bi.reply.setTag(model);
        bi.reply.setOnClickListener(onClickListener);
        HashMap<Object,Object> map = new HashMap();
        map.put("comment_id",model.getPostComment().getId());
        map.put("comment_reply_id","");
        map.put("like",model.getPostComment().getIsLike()==0?"1":"0");
        bi.likeIcon.setTag(map);
        bi.likeIcon.setOnClickListener(onClickListener);
        bi.likeIcon.setImageResource(model.getPostComment().getIsLike()==0 ? R.drawable.like : R.drawable.liked);
        bi.likeCount.setText(String.format("%d", model.getPostComment().getTotalLike()));
        bi.username.setText(UtilityHelperKt.getText(model.getUser().getUsername()).isEmpty()?UtilityHelperKt.getText(model.getUser().getFullName()):UtilityHelperKt.getText(model.getUser().getUsername()));
        bi.date.setText(UtilityHelperKt.convertDateTime(model.getPostComment().getCreated(),"yyyy-MM-dd hh:mm:ss","dd MMM, hh:mm aa"));
        bi.comments.setText(decodeEmoji(model.getPostComment().getComment()));

        adapter = new Comment2Adapter(this.mActivity, view -> {
            switch (view.getId()) {
                case R.id.child_reply:
                    childPostComment = (ChildPostComment) view.getTag();
                    Message replyMessage = Message.obtain();
                    Bundle bu = new Bundle();
                    bu.putString("username",childPostComment.getUser().getFullName());
                    bu.putString("parent_id",childPostComment.getPostComment().getId());
                    replyMessage.setData(bu);
                    replyMessage.what = 10;
                      this.callback.handleMessage(replyMessage);
                    break;
                case R.id.profile_image:
                case R.id.username:
//                    String userId = (String) view.getTag();
//                    Intent intent = new Intent();
//                    intent.putExtra("tag", "profile");
//                    intent.putExtra("user_id", userId);
//                    setResult(RESULT_OK, intent);
//                    finish();
                    break;
                case R.id.like_icon:
//                    HashMap<Object, Object> like_map = (HashMap<Object, Object>) view.getTag();
//                    map.put("user_id", mainViewModel.getUser().getId());
//
//                    Message likeMessage = Message.obtain();
//                    Bundle bu12 = new Bundle();
//                    bu12.putString("post_id",childPostComment.getPostComment().getId());
//                    likeMessage.setData(bu12);
//                    likeMessage.what = 12;
//                    this.callback.handleMessage(likeMessage);
                    break;
            }
        },view->{
            HashMap<Object, Object> deleteItemTagMap = (HashMap<Object, Object>) view.getTag();
                HashMap<Object,Object> replyDeleteMap = new HashMap();
                replyDeleteMap.put("adapter",adapter);
                replyDeleteMap.put("comment_id",(String) deleteItemTagMap.get("comment_id"));
                replyDeleteMap.put("child_bi",(RowComment2Binding) deleteItemTagMap.get("bi"));
                replyDeleteMap.put("show",(boolean) deleteItemTagMap.get("show"));
                bi.replyDelete.setTag(replyDeleteMap);
                bi.replyDelete.setOnClickListener(onClickListener);
                bi.replyDelete.performClick();

            return false;
        },model, post_user_id, loginUserId);
        bi.recycler.setAdapter(adapter);
    }
    @Override
    public int getItemCount() {
        return commentList.size();
    }

    public void hideDeleteAlert(RowCommentBinding bi){
        UtilityHelperKt.hide(bi.llDelete);
    }
    public void showDeleteAlert(RowCommentBinding bi){
        UtilityHelperKt.show(bi.llDelete);
    }


    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        RowCommentBinding bi;
        public ItemViewHolder(@NonNull RowCommentBinding bi) {
            super(bi.getRoot());
            this.bi = bi;
        }
    }
}
