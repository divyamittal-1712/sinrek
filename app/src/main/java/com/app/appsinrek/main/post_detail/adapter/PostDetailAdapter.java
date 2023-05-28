package com.app.appsinrek.main.post_detail.adapter;

import static com.app.appsinrek.utils.CommonUtils.decodeEmoji;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ablanco.zoomy.Zoomy;
import com.app.appsinrek.R;
import com.app.appsinrek.databinding.LayoutPostDetailBinding;
import com.app.appsinrek.main.post_detail.model.PostDetailModel;
import com.app.appsinrek.models.User;
import com.app.appsinrek.models.post.Post;
import com.app.appsinrek.models.post.PostModel;
import com.app.appsinrek.models.post.Shared;
import com.app.appsinrek.network.API_LINKS;
import com.app.appsinrek.player_view.enums.PostType;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.gson.Gson;

public class PostDetailAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private static final int ITEM_VIEW_TYPE_WITH_INNER_RECYCLER = 1;
    private final LayoutInflater inflater;
    private final PostDetailModel postModel;
    private Context mActivity;
    String user_id;
    View.OnClickListener onClickListener;
    Gson gson = new Gson();

    public PostDetailAdapter(Context mActivity, View.OnClickListener onClickListener, PostDetailModel postModel , String id) {
        setHasStableIds(true);
        this.postModel = postModel;
        inflater = LayoutInflater.from(mActivity);
        this.mActivity = mActivity;
        this.onClickListener = onClickListener;
        user_id = id;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LayoutPostDetailBinding itemView = LayoutPostDetailBinding.inflate(inflater);
        RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT
        );
        itemView.fram.setLayoutParams(lp);
        return new ItemViewHolder(itemView);
    }

    @SuppressLint("DefaultLocale")
    @Override
    public void onBindViewHolder(@NonNull final RecyclerView.ViewHolder holder, final int position) {
        if (holder instanceof ItemViewHolder) {
            ItemViewHolder itemViewHolder = (ItemViewHolder) holder;
            LayoutPostDetailBinding bi = itemViewHolder.bi;
            PostDetailModel model = postModel;

        }
    }


    @Override
    public int getItemViewType(int position) {
        if (position == 2) {
            return ITEM_VIEW_TYPE_WITH_INNER_RECYCLER;
        }
        return super.getItemViewType(position);
    }

    /**
     * Returns the total number of items in the data set held by the adapter.
     *
     * @return The total number of items in this adapter.
     */
    @Override
    public int getItemCount() {
        return 0;
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        LayoutPostDetailBinding bi;
        public ItemViewHolder(@NonNull LayoutPostDetailBinding bi) {
            super(bi.getRoot());
            this.bi = bi;
        }
    }
}


