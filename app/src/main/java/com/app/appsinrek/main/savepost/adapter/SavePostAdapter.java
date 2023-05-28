package com.app.appsinrek.main.savepost.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.RowSavepostBinding;
import com.app.appsinrek.main.savepost.models.Bookmark;
import com.app.appsinrek.player_view.enums.PostType;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class SavePostAdapter extends RecyclerView.Adapter<SavePostAdapter.ItemViewHolder> {

    private final LayoutInflater inflater;
    private List<Bookmark> postModelList;
    private Context mActivity;
    private View.OnClickListener onClickListener;
    public SavePostAdapter(Context mActivity, View.OnClickListener onClickListener, ArrayList<Bookmark> postModelList) {
        this.postModelList = postModelList;
        inflater = LayoutInflater.from(mActivity);
        this.mActivity=mActivity;
        this.onClickListener=onClickListener;
       }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowSavepostBinding itemView = RowSavepostBinding.inflate(inflater);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {
        RowSavepostBinding bi = holder.bi;
        Bookmark model = postModelList.get(position);
        holder.itemView.setTag(position);
//        holder.itemView.setTag(model);
        holder.itemView.setOnClickListener(onClickListener);
//        bi.delete.setTag(model);
//        bi.delete.setOnClickListener(onClickListener);
        bi.imagePost.setVisibility(!model.getPost().getType().equals(PostType.TEXT.getValue()) ?View.VISIBLE:View.GONE);
        bi.textView.setVisibility(model.getPost().getType().equals(PostType.TEXT.getValue()) ?View.VISIBLE:View.GONE);
        bi.typeImage.setVisibility(model.getPost().getType().equals(PostType.IMAGE.getValue()) ?View.VISIBLE:View.GONE);
        bi.typeVideo.setVisibility(model.getPost().getType().equals(PostType.VIDEO.getValue()) ?View.VISIBLE:View.GONE);
        bi.textView.setText(model.getPost().getCaption());
        try {
            String path = model.getPost().getAttachment();
            if(path != null){
                Glide.with(mActivity)
                        .load(path)
                        .centerCrop()
                        .optionalFitCenter()
                        .placeholder(R.drawable._1)
                        .into(bi.imagePost);
            }

        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        RowSavepostBinding bi;
        public ItemViewHolder(@NonNull RowSavepostBinding bi) {
            super(bi.getRoot());
            this.bi = bi;
        }
    }

}