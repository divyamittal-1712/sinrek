package com.app.appsinrek.main.comment.adapter;

import static com.app.appsinrek.utils.CommonUtils.decodeEmoji;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.RowComment2Binding;
import com.app.appsinrek.main.comment.model.ChildPostComment;
import com.app.appsinrek.main.comment.model.CommentModel;
import com.app.appsinrek.network.API_LINKS;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.bumptech.glide.Glide;

import java.util.HashMap;

public class Comment2Adapter extends RecyclerView.Adapter<Comment2Adapter.ItemViewHolder> {

    private final LayoutInflater inflater;
    private Context mActivity;
//    RowComment2Binding bi;
    View.OnClickListener onClickListener;
    View.OnLongClickListener onLongClickListener;
    CommentModel commentModel;
    String post_user_id;
    String loginUserId;
    public Comment2Adapter(Context mActivity, View.OnClickListener onClickListener,
                           View.OnLongClickListener onLongClickListener, CommentModel commentModel, String post_user_id, String loginUserId) {
        inflater = LayoutInflater.from(mActivity);
        this.mActivity=mActivity;
        this.onClickListener=onClickListener;
        this.onLongClickListener = onLongClickListener;
        this.commentModel = commentModel;
        this.post_user_id = post_user_id;
        this.loginUserId = loginUserId;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowComment2Binding itemView = RowComment2Binding.inflate(inflater);
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
        RowComment2Binding  bi = holder.bi;
        ChildPostComment model = commentModel.getChildPostComment().get(position);
        Glide.with(mActivity)
                .load(API_LINKS.BASE_IMAGE_URL+UtilityHelperKt.getText(model.getUser().getImage()))
                .centerCrop()
                .optionalFitCenter()
                .placeholder(R.drawable.user2)
                .error(R.drawable.user2)
                .into(bi.profileImage);
        holder.itemView.setTag(bi.llDelete);
        holder.itemView.setOnLongClickListener(view -> {
            if(post_user_id.equals(loginUserId))
                UtilityHelperKt.show(bi.llDelete);
            else if(model.getPostComment().getUserId().equals(loginUserId))
                UtilityHelperKt.show(bi.llDelete);
            return false;
        });

        HashMap<Object,Object> holdItemTagMap = new HashMap();
        holdItemTagMap.put("show",(post_user_id.equals(loginUserId) || model.getPostComment().getUserId().equals(loginUserId))?true:false);
        holdItemTagMap.put("comment_id",model.getPostComment().getId());
        holdItemTagMap.put("bi",bi);
        holder.itemView.setTag(holdItemTagMap);
        holder.itemView.setOnLongClickListener(onLongClickListener);
        bi.profileImage.setTag(model.getUser().getId());
        bi.profileImage.setOnClickListener(onClickListener);
        bi.username.setTag(model.getUser().getId());
        bi.username.setOnClickListener(onClickListener);
        bi.username.setText(UtilityHelperKt.getText(model.getUser().getUsername()).isEmpty()?UtilityHelperKt.getText(model.getUser().getFullName()):UtilityHelperKt.getText(model.getUser().getUsername()));
        bi.comments.setText(decodeEmoji(model.getPostComment().getComment()));
        HashMap<Object,Object> map = new HashMap();
        map.put("comment_id",commentModel.getPostComment().getId());
        map.put("comment_id",commentModel.getPostComment().getId());
        map.put("comment_reply_id",model.getPostComment().getId());
        map.put("like",model.getPostComment().getIsLike()==0?"1":"0");
        bi.likeIcon.setTag(map);
        bi.likeIcon.setOnClickListener(onClickListener);
        bi.likeIcon.setImageResource(model.getPostComment().getIsLike()==0 ? R.drawable.like : R.drawable.liked);
        bi.likeCount.setText(String.format("%d", model.getPostComment().getTotalLike()));
        bi.childReply.setTag(model);
        bi.childReply.setOnClickListener(onClickListener);

    }

    @Override
    public int getItemCount() {
        return commentModel.getChildPostComment().size();
    }

    public void hideDeleteAlert(RowComment2Binding  bi){
        UtilityHelperKt.hide(bi.llDelete);
    }
    public void showDeleteAlert(RowComment2Binding bi){
        UtilityHelperKt.show(bi.llDelete);
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        RowComment2Binding bi;
        public ItemViewHolder(@NonNull RowComment2Binding bi) {
            super(bi.getRoot());
            this.bi = bi;
        }
    }
}
