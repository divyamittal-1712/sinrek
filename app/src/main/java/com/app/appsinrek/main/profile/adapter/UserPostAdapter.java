package com.app.appsinrek.main.profile.adapter;

import static com.app.appsinrek.utils.CommonUtils.decodeEmoji;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.RowProfilePostBinding;
import com.app.appsinrek.models.post.PostModel;
import com.app.appsinrek.player_view.enums.PostType;
import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

public class UserPostAdapter extends RecyclerView.Adapter<UserPostAdapter.ItemViewHolder> {

    private final LayoutInflater inflater;
    private List<PostModel> postModelList;
    private Context mActivity;
    private View.OnClickListener onClickListener;
    public UserPostAdapter(Context mActivity, View.OnClickListener onClickListener, ArrayList<PostModel> postModelList) {
        this.postModelList = postModelList;
        inflater = LayoutInflater.from(mActivity);
        this.mActivity=mActivity;
        this.onClickListener=onClickListener;
       }

    @NonNull
    @Override
    public ItemViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        RowProfilePostBinding itemView = RowProfilePostBinding.inflate(inflater);
        return new ItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final ItemViewHolder holder, final int position) {

        RowProfilePostBinding bi = holder.bi;
        PostModel model = postModelList.get(position);
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(onClickListener);
        bi.imagePost.setVisibility(!model.getPost().getType().equals(PostType.TEXT.getValue()) ?View.VISIBLE:View.GONE);
        bi.typeImage.setVisibility(model.getPost().getType().equals(PostType.IMAGE.getValue()) ?View.VISIBLE:View.GONE);
        bi.typeVideo.setVisibility(model.getPost().getType().equals(PostType.VIDEO.getValue()) ?View.VISIBLE:View.GONE);
        bi.textView.setVisibility(model.getPost().getType().equals(PostType.TEXT.getValue()) ?View.VISIBLE:View.GONE);
        bi.textView.setText(decodeEmoji(model.getPost().getCaption()));
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


//            if (model.getPost().getType().equals(PostType.VIDEO.getValue())) {
//                bi.typeVideo.setTag(model);
//                bi.typeVideo.setOnClickListener(onClickListener);
////                bi.playButton.setTag(model);
////                bi.playButton.setOnClickListener(onClickListener);
//                Glide.with(mActivity)
//                        .load(path)
//                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                        .error(R.drawable._1)
//                        .placeholder(R.drawable._1)
//                        .into(bi.typeVideo);
//            } else {
//                bi.imagePost.setTag(model);
//                bi.imagePost.setOnClickListener(onClickListener);
//                Glide.with(mActivity)
//                        .load(path)
//                        .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
//                        .error(R.drawable._1)
//                        .placeholder(R.drawable._1)
//                        .into(bi.imagePost);
//            }


        }catch(Exception e){
            e.printStackTrace();
        }
    }

    @Override
    public int getItemCount() {
        return postModelList.size();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        RowProfilePostBinding bi;
        public ItemViewHolder(@NonNull RowProfilePostBinding bi) {
            super(bi.getRoot());
            this.bi = bi;
        }
    }

}