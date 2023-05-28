package com.app.appsinrek.player_view.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ColumnSuggestionsBinding;
import com.app.appsinrek.models.post.PostModel;
import com.app.appsinrek.models.post.Shared;
import com.app.appsinrek.models.post.UserSuggestionModel;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

import java.util.List;

public class SuggestionAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private final LayoutInflater inflater;
    private final List<UserSuggestionModel> userSuggestionModelList;
    private Context mActivity;
    View.OnClickListener onClickListener;
    Gson gson = new Gson();

    public SuggestionAdapter(Context mActivity, View.OnClickListener onClickListener, List<UserSuggestionModel> userSuggestionModelList) {
        setHasStableIds(true);
        this.userSuggestionModelList = userSuggestionModelList;
        inflater = LayoutInflater.from(mActivity);
        this.mActivity = mActivity;
        this.onClickListener = onClickListener;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        ColumnSuggestionsBinding itemView = ColumnSuggestionsBinding.inflate(inflater);
//        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.column_suggestions, parent, false);
        LayoutInflater inflater = LayoutInflater.from(parent.getContext());
        ColumnSuggestionsBinding innerView = ColumnSuggestionsBinding.inflate(inflater);
        return new ItemViewHolder(innerView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            ColumnSuggestionsBinding bi = itemViewHolder.bi;
            UserSuggestionModel model = userSuggestionModelList.get(position);
            if(position ==0){
                int padding = this.mActivity.getResources().getDimensionPixelOffset(com.intuit.sdp.R.dimen._10sdp);
                bi.framCl.setPadding(padding,0,padding,0);
            }
            String path = model.getUser().getProfile();
            bi.profileImage.setTag(model);
            bi.profileImage.setOnClickListener(onClickListener);
            Glide.with(mActivity)
                    .load(path)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .error(R.drawable.user2)
                    .placeholder(R.drawable.user2)
                    .into(bi.profileImage);
            String username  = UtilityHelperKt.getText(model.getUser().getUsername());
            bi.name.setText("@"+username);
            if (model.getUser().getFullName() == ""){
                bi.fullName.setVisibility(View.GONE);
            }else {
                bi.fullName.setVisibility(View.VISIBLE);
                String fullName = UtilityHelperKt.getText(model.getUser().getFullName());
                bi.fullName.setText(fullName);
            }
            bi.followersCount.setText(String.format("%s Followers", model.getUser().getFollowers()));
            Bundle bu = new Bundle();
            bu.putString("userid",model.getUser().getId());
            bu.putInt("position",position);
            bi.mtbFollow.setTag(bu);
            bi.mtbFollow.setOnClickListener(onClickListener);
            bi.closeBtn.setTag(position);
            bi.closeBtn.setOnClickListener(onClickListener);
            bi.profileImage.setTag(bu);
            bi.profileImage.setOnClickListener(onClickListener);
        }
    }

    private void setUi(ColumnSuggestionsBinding bi, PostModel postModel, String path, Shared shared) {

    }

    @Override
    public int getItemCount() {
        return userSuggestionModelList.size();
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
        ColumnSuggestionsBinding bi;

        public ItemViewHolder(@NonNull ColumnSuggestionsBinding bi) {
            super(bi.getRoot());
            this.bi = bi;
        }
    }
}


