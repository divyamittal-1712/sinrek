package com.app.appsinrek.main.notification.adapter;

import static com.app.appsinrek.utils.CommonUtils.decodeEmoji;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Typeface;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.style.StyleSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.RowNotificationsBinding;
import com.app.appsinrek.main.notification.model.Following;
import com.app.appsinrek.network.API_LINKS;
import com.app.appsinrek.utils.TimeAgo;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.bumptech.glide.Glide;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private final LayoutInflater inflater;
    private Context mActivity;
    View.OnClickListener onClickListener;
    boolean viewType;
    List<Following> following;
    public NotificationsAdapter(Context mActivity, View.OnClickListener onClickListener, boolean viewType, List<Following> following) {
        inflater = LayoutInflater.from(mActivity);
        this.mActivity=mActivity;
        this.onClickListener=onClickListener;
        this.viewType = viewType;
        this.following = following;
       }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
            RowNotificationsBinding itemView = RowNotificationsBinding.inflate(inflater);
            RecyclerView.LayoutParams lp = new RecyclerView.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT
            );
            itemView.frame.setLayoutParams(lp);
            return new ItemViewHolder(itemView);
    }

    @SuppressLint("SetTextI18n")
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        Following model = following.get(position);
        RowNotificationsBinding bi = ((ItemViewHolder)holder).bi;

        holder.itemView.setTag(model);
        holder.itemView.setOnClickListener(onClickListener);
        bi.profileImage.setTag(model);
        bi.profileImage.setOnClickListener(onClickListener);
        String attachment = model.getPost().getAttachment();
        if (attachment != null && !UtilityHelperKt.getText(attachment).isEmpty()) {
            bi.image.setVisibility(View.VISIBLE);
            Glide.with(mActivity)
                    .load(attachment)
                    .centerCrop()
                    .optionalFitCenter()
                    .placeholder(R.drawable._1)
                    .into(bi.image);
        }
        else
            bi.image.setVisibility(View.GONE);
        bi.profileImage.setImageResource(R.drawable.user2);
        String image = model.getNotification().getSender_image();
        if (!UtilityHelperKt.getText(image).isEmpty()) {
            Glide.with(mActivity)
                    .load(API_LINKS.BASE_IMAGE_URL+image)
                    .centerCrop()
                    .optionalFitCenter()
                    .placeholder(R.drawable.user2)
                    .into(bi.profileImage);

        }
        SpannableStringBuilder builder=new SpannableStringBuilder();
        String txt1=UtilityHelperKt.getText(model.getNotification().getSender_username());
        String message=decodeEmoji(model.getNotification().getMsg());
        SpannableString txtSpannable= new SpannableString(message);
        StyleSpan boldSpan = new StyleSpan(Typeface.BOLD);
        txtSpannable.setSpan(boldSpan, 0, txt1.length(), Spannable.SPAN_EXCLUSIVE_INCLUSIVE);
        builder.append(txtSpannable);
        bi.msg.setText(txtSpannable);

        @SuppressLint("SimpleDateFormat") DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        try {
            Date date = formatter.parse(model.getNotification().getCreated());
            assert date != null;
            String time = TimeAgo.toDuration(date.getTime());
            bi.time.setText(time);
        } catch (ParseException e) {
            bi.time.setText("0 seconds ago");
            e.printStackTrace();
        }

    }


    @Override
    public int getItemCount() {
        return following.size();
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemViewType(int position) {
        return position;
    }

    @SuppressLint("NotifyDataSetChanged")
    public void setViewType(boolean b) {
        this.viewType = b;
        notifyDataSetChanged();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {
        RowNotificationsBinding bi;
        public ItemViewHolder(@NonNull RowNotificationsBinding bi) {
            super(bi.getRoot());
            this.bi = bi;
        }
    }

}
