package com.app.appsinrek.main.dashboard.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.RowSearchBinding;
import com.app.appsinrek.main.dashboard.model.LikedUsersModel;
import com.app.appsinrek.models.Other;
import com.app.appsinrek.models.User;
import com.app.appsinrek.network.API_LINKS;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class LikedUsersAdapter extends RecyclerView.Adapter<LikedUsersAdapter.ItemViewHolder> {

    private final LayoutInflater inflater;
    private Context mActivity;
    View.OnClickListener onClickListener;
    ArrayList<LikedUsersModel> searchData;
    String userId;
    public LikedUsersAdapter(Context mActivity, View.OnClickListener onClickListener, ArrayList<LikedUsersModel> searchData, String userId) {
        inflater = LayoutInflater.from(mActivity);
        this.mActivity=mActivity;
        this.onClickListener=onClickListener;
        this.searchData = searchData;
        this.userId = userId;
       }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowSearchBinding itemView = RowSearchBinding.inflate(inflater);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        itemView.fram.setLayoutParams(lp);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        RowSearchBinding bi = holder.bi;
        bi.btnFollow.setTag(position);
        bi.btnFollow.setOnClickListener(onClickListener);
        User user = searchData.get(position).getUser();
        Other other = searchData.get(position).getOther();

        bi.profileImage.setTag(user);
        bi.profileImage.setOnClickListener(onClickListener);
        bi.username.setTag(user);
        bi.username.setOnClickListener(onClickListener);
        bi.username.setText(UtilityHelperKt.getText(user.getFullName()).isEmpty()?user.getUsername():UtilityHelperKt.getText(user.getFullName()));
        bi.followers.setText(user.getFollowers() +" Followers");
        bi.userId.setText(user.getUsername());
        if(userId.equals(user.getId()))bi.btnFollow.setVisibility(View.GONE);
        else bi.btnFollow.setVisibility(View.VISIBLE);
        if(other.getButton().equals("Follow")){
            bi.btnFollow.setBackground(ContextCompat.getDrawable(mActivity,R.drawable.bg_black));
            ((AppCompatButton)bi.btnFollow).setTextColor(ContextCompat.getColor(mActivity,R.color.white));
            ((AppCompatButton)bi.btnFollow).setText(mActivity.getResources().getString(R.string.follow));
        }else{
            bi.btnFollow.setBackground(ContextCompat.getDrawable(mActivity,R.drawable.bg_white));
            ((AppCompatButton)bi.btnFollow).setTextColor(ContextCompat.getColor(mActivity,R.color.black));
            ((AppCompatButton)bi.btnFollow).setText(mActivity.getResources().getString(R.string.following));
        }
        if(!user.getImage().isEmpty())
        Glide.with(mActivity)
                .load(API_LINKS.BASE_IMAGE_URL+user.getImage())
                .centerCrop()
                .optionalFitCenter()
                .placeholder(R.drawable.user2)
                .error(R.drawable.user2)
                .into(bi.profileImage);
    }
    @Override
    public int getItemCount() {
        return searchData.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        RowSearchBinding bi;
        public ItemViewHolder(@NonNull RowSearchBinding bi) {
            super(bi.getRoot());
            this.bi = bi;
        }
    }
}
