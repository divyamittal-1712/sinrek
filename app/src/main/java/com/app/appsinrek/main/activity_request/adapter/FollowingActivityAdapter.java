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
import com.app.appsinrek.main.activity_request.models.Following;
import com.app.appsinrek.network.API_LINKS;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.bumptech.glide.Glide;

import java.util.List;

public class FollowingActivityAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private Context mActivity;
    View.OnClickListener onClickListener;
    List<Following> list;
    boolean viewType;

    public FollowingActivityAdapter(Context mActivity, View.OnClickListener onClickListener, boolean viewType, List<Following> list) {
        inflater = LayoutInflater.from(mActivity);
        this.mActivity = mActivity;
        this.onClickListener = onClickListener;
        this.viewType = viewType;
        this.list = list;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        if (viewType == 0) {
            RowActivitiesBinding itemView = RowActivitiesBinding.inflate(inflater);
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
        if (holder instanceof ItemViewHolder) {
            RowActivitiesBinding bi = ((ItemViewHolder) holder).bi;
            Following model = list.get(position);
            bi.profileImage.setTag(model);
            bi.profileImage.setOnClickListener(onClickListener);
            bi.profileImage.setImageResource(R.drawable.user2);
            Glide.with(mActivity)
                    .load(API_LINKS.BASE_IMAGE_URL + model.getFollowingActivity().getImage())
                    .centerCrop()
                    .optionalFitCenter()
                    .placeholder(R.drawable.user2)
                    .error(R.drawable.user2)
                    .into(bi.profileImage);
            bi.msg.setText(model.getFollowingActivity().getMsg());
            String attachment = model.getFollowingActivity().getAttachment();
            if (attachment != null && !UtilityHelperKt.getText(attachment).isEmpty()) {
                bi.image.setVisibility(View.VISIBLE);
                Glide.with(mActivity)
                        .load(attachment)
                        .centerCrop()
                        .optionalFitCenter()
                        .placeholder(R.drawable._1)
                        .into(bi.image);
            }
            else{
//                int leftMarginInDp = (int) TypedValue.applyDimension(
//                        TypedValue.COMPLEX_UNIT_DIP, 65, mActivity.getResources()
//                                .getDisplayMetrics());
//
//                int rightMarginInDp = (int) TypedValue.applyDimension(
//                        TypedValue.COMPLEX_UNIT_DIP, 10, mActivity.getResources()
//                                .getDisplayMetrics());
//
//                LinearLayout.LayoutParams params =
//                        new LinearLayout.LayoutParams( LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT);
//                params.setMargins(leftMarginInDp, 0, rightMarginInDp, 0);
//                bi.linearLayoutCompat.setLayoutParams(params);
                bi.image.setVisibility(View.GONE);
            }
            bi.time.setText(UtilityHelperKt.convertDateTime(model.getFollowingActivity().getCreated(),"yyyy-MM-dd hh:mm:ss","dd MMM, hh:mm aa"));
        }
    }

    @Override
    public int getItemViewType(int position) {
        return viewType ? 0 : 1;
    }

    @Override
    public int getItemCount() {
        return viewType ? list.size() : 0;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setViewType(boolean b) {
        this.viewType = b;
        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        RowActivitiesBinding bi;

        public ItemViewHolder(@NonNull RowActivitiesBinding bi) {
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
