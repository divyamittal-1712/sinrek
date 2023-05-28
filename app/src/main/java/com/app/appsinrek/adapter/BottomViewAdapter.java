package com.app.appsinrek.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.RowBottomViewBinding;
import com.bumptech.glide.Glide;

import java.util.ArrayList;

public class BottomViewAdapter extends RecyclerView.Adapter<BottomViewAdapter.ItemViewHolder> {

    private final LayoutInflater inflater;
    private final String[] post = {"Image","Video"};
    private final Integer[] postImage = {R.drawable.photo_camera,R.drawable.video_camera};
    private Context mActivity;
    View.OnClickListener onClickListener;
    public BottomViewAdapter(Context mActivity, View.OnClickListener onClickListener) {
        inflater = LayoutInflater.from(mActivity);
        this.mActivity=mActivity;
        this.onClickListener=onClickListener;
       }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowBottomViewBinding itemView = RowBottomViewBinding.inflate(inflater);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        RowBottomViewBinding bi = holder.bi;
        bi.imagePost.setImageResource(postImage[position]);
        bi.title.setText(post[position]);
        bi.getRoot().setTag(position);
        bi.getRoot().setOnClickListener(onClickListener);
    }

    @Override
    public int getItemCount() {
        return post.length;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        RowBottomViewBinding bi;
        public ItemViewHolder(@NonNull RowBottomViewBinding bi) {
            super(bi.getRoot());
            this.bi = bi;
        }
    }

}