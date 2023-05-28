package com.app.appsinrek.main.settings.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.RowBlockeduserBinding;
import com.app.appsinrek.main.settings.models.BlockedUsers;
import com.app.appsinrek.models.User;
import com.app.appsinrek.network.API_LINKS;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BlockedUsersAdapter extends RecyclerView.Adapter<BlockedUsersAdapter.ItemViewHolder> {

    private final LayoutInflater inflater;
    private Context mActivity;
    View.OnClickListener onClickListener;
    ArrayList<BlockedUsers> listData;
    MainViewModel mainViewModel;
    public BlockedUsersAdapter(Context mActivity, View.OnClickListener onClickListener, ArrayList<BlockedUsers> listData, MainViewModel mainViewModel) {
        inflater = LayoutInflater.from(mActivity);
        this.mActivity=mActivity;
        this.onClickListener=onClickListener;
        this.listData = listData;
        this.mainViewModel = mainViewModel;
    }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowBlockeduserBinding itemView = RowBlockeduserBinding.inflate(inflater);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        itemView.fram.setLayoutParams(lp);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        RowBlockeduserBinding bi = holder.bi;
        User user = listData.get(position).getUser();
        bi.unblockUser.setTag(user);
        bi.unblockUser.setOnClickListener(onClickListener);
        bi.profileImage.setTag(user);
        bi.profileImage.setOnClickListener(onClickListener);
        bi.linearLayoutCompat.setTag(user);
        bi.linearLayoutCompat.setOnClickListener(onClickListener);
        bi.username.setText(user.getFullName()!= null && user.getFullName().isEmpty()?user.getUsername():user.getFullName());
//        bi.countFollowers.setText(String.format("%d Followers", user.getTotalfollower()));
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
        RowBlockeduserBinding bi;
        public ItemViewHolder(@NonNull RowBlockeduserBinding bi) {
            super(bi.getRoot());
            this.bi = bi;
        }
    }
}
