package com.app.appsinrek.main.profile.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.RowFollowersBinding;
import com.app.appsinrek.main.profile.model.Followers;
import com.app.appsinrek.models.User;
import com.app.appsinrek.network.API_LINKS;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.bumptech.glide.Glide;

import java.util.List;

public class FollowersAdapter extends RecyclerView.Adapter<FollowersAdapter.ItemViewHolder> {

    private final LayoutInflater inflater;
    private Context mActivity;
    View.OnClickListener onClickListener;
    List<Followers> listData;
    MainViewModel mainViewModel;
    public  FollowersAdapter(Context mActivity, View.OnClickListener onClickListener, List<Followers> listData, MainViewModel mainViewModel) {
        inflater = LayoutInflater.from(mActivity);
        this.mActivity=mActivity;
        this.onClickListener=onClickListener;
        this.listData = listData;
        this.mainViewModel = mainViewModel;
       }

    public void setListData(List<Followers> listData) {
        this.listData.addAll(listData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowFollowersBinding itemView = RowFollowersBinding.inflate(inflater);
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
        RowFollowersBinding bi = holder.bi;
        User user = listData.get(position).getUser();
        bi.materialButton.setTag(user);
        bi.materialButton.setOnClickListener(onClickListener);
        bi.materialButton.setVisibility(mainViewModel.getUser().getId().equals(UtilityHelperKt.getText(user.getId()))?View.GONE:View.VISIBLE);
        if(user.getIsFollowing().equals(mActivity.getResources().getString(R.string.following))) {
            bi.materialButton.setBackgroundTintList(ContextCompat.getColorStateList(mActivity, R.color.white));
            bi.materialButton.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            bi.materialButton.setText(mActivity.getResources().getString(R.string.following));
        }else if(user.getIsFollowing().equals(mActivity.getResources().getString(R.string.requested))){
            bi.materialButton.setBackgroundTintList(ContextCompat.getColorStateList(mActivity, R.color.white));
            bi.materialButton.setTextColor(ContextCompat.getColor(mActivity, R.color.black));
            bi.materialButton.setText(mActivity.getResources().getString(R.string.requeted));
        }else{
            bi.materialButton.setBackground(ContextCompat.getDrawable(mActivity,R.drawable.bg_black));
            bi.materialButton.setTextColor(ContextCompat.getColor(mActivity, R.color.white));
            bi.materialButton.setText(mActivity.getResources().getString(R.string.follow));
        }
        bi.profileImage.setTag(user);
        bi.profileImage.setOnClickListener(onClickListener);
        bi.linearLayoutCompat.setTag(user);
        bi.linearLayoutCompat.setOnClickListener(onClickListener);
        bi.username.setText(user.getFullName().isEmpty()?user.getUsername():user.getFullName());
        bi.countFollowers.setText(String.format("%d Followers", user.getTotalfollower()));
        bi.userId.setText("@"+user.getUsername());

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
        return listData.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        RowFollowersBinding bi;
        public ItemViewHolder(@NonNull RowFollowersBinding bi) {
            super(bi.getRoot());
            this.bi = bi;
        }
    }
}
