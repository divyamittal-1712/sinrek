package com.app.appsinrek.main.activity_request.adapter;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.RowActivitiesBinding;
import com.app.appsinrek.databinding.RowNewRequestBinding;
import com.app.appsinrek.main.activity_request.models.Requests;
import com.app.appsinrek.network.API_LINKS;
import com.bumptech.glide.Glide;

import java.util.List;

public class ActivityRequestAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private Context mActivity;
    View.OnClickListener onClickListener;
    List<Requests> requestsList;
    boolean viewType;
    public ActivityRequestAdapter(Context mActivity, View.OnClickListener onClickListener, boolean viewType, List<Requests> requestsList) {
        inflater = LayoutInflater.from(mActivity);
        this.mActivity=mActivity;
        this.onClickListener=onClickListener;
        this.viewType = viewType;
        this.requestsList = requestsList;
       }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            RowNewRequestBinding itemView = RowNewRequestBinding.inflate(inflater);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            itemView.fram.setLayoutParams(lp);
            return new ItemViewHolder(itemView);
        } else {
            RowActivitiesBinding itemView2 = RowActivitiesBinding.inflate(inflater);
            RecyclerView.LayoutParams lp2 = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            itemView2.fram.setLayoutParams(lp2);
            return new ItemViewHolder2(itemView2);
        }
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof ItemViewHolder){
            RowNewRequestBinding bi = ((ItemViewHolder) holder).bi;
            Requests model = requestsList.get(position);
            bi.profileImage.setTag(model);
            bi.profileImage.setOnClickListener(onClickListener);
            bi.name.setTag(model);
            bi.name.setOnClickListener(onClickListener);
            bi.name.setText(model.getSender().getUsername());
            if(model.getSender().getFullName() != null && !model.getSender().getFullName().isEmpty())
                bi.username.setVisibility(View.VISIBLE);
            bi.username.setText(model.getSender().getFullName());

            bi.followers.setText(model.getSender().getFollowers() +" Followers");
            bi.profileImage.setImageResource(R.drawable.user2);
                Glide.with(mActivity)
                        .load(API_LINKS.BASE_IMAGE_URL+model.getSender().getImage())
                        .centerCrop()
                        .optionalFitCenter()
                        .placeholder(R.drawable.user2)
                        .error(R.drawable.user2)
                        .into(bi.profileImage);
            bi.accept.setTag(model);
            bi.accept.setOnClickListener(onClickListener);
            bi.decline.setTag(model);
            bi.decline.setOnClickListener(onClickListener);
        }
    }
    @Override
    public int getItemViewType(int position) {
        // Just as an example, return 0 or 2 depending on position
        // Note that unlike in ListView adapters, types don't have to be contiguous
        return viewType?0:1;
    }

    @Override
    public int getItemCount() {
        return viewType?requestsList.size():0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setViewType(boolean b) {
        this.viewType = b;
        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        RowNewRequestBinding bi;
        public ItemViewHolder(@NonNull RowNewRequestBinding bi) {
            super(bi.getRoot());
            this.bi = bi;
        }
    }
    public static class ItemViewHolder2 extends RecyclerView.ViewHolder {
        RowActivitiesBinding bi;
        public ItemViewHolder2(@NonNull RowActivitiesBinding bi) {
            super(bi.getRoot());
            this.bi = bi;
        }
    }
}
