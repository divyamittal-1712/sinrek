package com.app.appsinrek.main.comment;

import static com.app.appsinrek.utils.CommonUtils.encodeEmoji;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityCommentBinding;
import com.app.appsinrek.databinding.RowComment2Binding;
import com.app.appsinrek.databinding.RowCommentBinding;
import com.app.appsinrek.main.comment.adapter.Comment2Adapter;
import com.app.appsinrek.main.comment.adapter.CommentAdapter;
import com.app.appsinrek.main.comment.model.ChildPostComment;
import com.app.appsinrek.main.comment.model.CommentModel;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.network.API_LINKS;
import com.app.appsinrek.utils.AppConstants;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.bumptech.glide.Glide;
import com.fitness.modval.interfaces.AuthListener;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity implements AuthListener {

    ActivityCommentBinding bi;
    Activity mActivity = this;
    private List<CommentModel> commentList = new ArrayList<>();
    private CommentAdapter adapter;
    private MainViewModel mainViewModel;
    private String post_id;
    private String post_user_id;
    private CommentModel commentModelReply;
    private ChildPostComment childPostCommentReply;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setMainListener(this);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_comment);
        post_id = getIntent().getStringExtra("post_id");
        post_user_id = getIntent().getStringExtra("post_user_id");
        if (!mainViewModel.getUser().getImage().isEmpty()) {
            Glide.with(this)
                    .load(API_LINKS.BASE_IMAGE_URL + mainViewModel.getUser().getImage())
                    .centerCrop()
                    .optionalFitCenter()
                    .placeholder(R.drawable.user2)
                    .into(bi.profileImage);
        }
        adapter = new CommentAdapter(this, view -> {
            switch (view.getId()) {
                case R.id.reply:
                    commentModelReply = (CommentModel) view.getTag();
                    bi.parentId.setText(commentModelReply.getPostComment().getId());
                    UtilityHelperKt.show(bi.llReplay);
                    bi.replyTo.setText(String.format("Reply to %s.", commentModelReply.getUser().getFullName()));
                    break;
                case R.id.reply_delete:
                    HashMap<Object, Object> replyDeleteMap = (HashMap<Object, Object>) view.getTag();
                    Comment2Adapter replyAdapter = (Comment2Adapter) replyDeleteMap.get("adapter");
                    String comment_id = (String) replyDeleteMap.get("comment_id");
                    RowComment2Binding reply_Bi = (RowComment2Binding) replyDeleteMap.get("child_bi");
                    boolean show = (boolean) replyDeleteMap.get("show");
                    if(show){
                        replyAdapter.showDeleteAlert(reply_Bi);
                        replyDeletePopup(comment_id, replyAdapter, reply_Bi);
                    }
                    else
                        replyAdapter.hideDeleteAlert(reply_Bi);
                    break;
                case R.id.profile_image:
                case R.id.username:
                    String userId = (String) view.getTag();
                    Intent intent = new Intent();
                    intent.putExtra("tag", "profile");
                    intent.putExtra("user_id", userId);
                    setResult(RESULT_OK, intent);
                    finish();
                    break;
                case R.id.like_icon:
                    HashMap<Object, Object> map = (HashMap<Object, Object>) view.getTag();
                    map.put("post_id", post_id);
                    map.put("user_id", mainViewModel.getUser().getId());
                    mainViewModel.likeComment(map);
                    break;
            }
        },view->{
            HashMap<Object, Object> deleteBtnTagMap = (HashMap<Object, Object>) view.getTag();
            String comment_id = (String) deleteBtnTagMap.get("comment_id");
            boolean show = (boolean) deleteBtnTagMap.get("show");
            RowCommentBinding bi = (RowCommentBinding) deleteBtnTagMap.get("bi");
            if(show){
                adapter.showDeleteAlert(bi);
                deletePopup(comment_id, bi);
            }
            else
                adapter.hideDeleteAlert(bi);
            return false;
        },  new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                switch (message.what){
                    case 10:
                        Bundle bu10 = message.getData();
                        String username = bu10.getString("username");
                        bi.parentId.setText(bu10.getString("parent_id"));
                        UtilityHelperKt.show(bi.llReplay);
                        bi.replyTo.setText(String.format("Reply to %s.",username ));
                        break;
                }
                return true;
            }
        }, commentList, post_user_id, mainViewModel.getUser().getId());

        bi.cancelAction.setOnClickListener(view -> {
            commentModelReply = null;
            UtilityHelperKt.hide(bi.llReplay);
        });
        bi.recycler.setAdapter(adapter);
        bi.send.setOnClickListener(view -> {
            if (!bi.tvComment.getText().toString().isEmpty()) {
                String parent_id = (String) bi.parentId.getText();
                if(parent_id == null)
                    parent_id = "";
                mainViewModel.addComments(post_id, encodeEmoji(bi.tvComment.getText().toString()), parent_id);
            }
        });
        bi.icBack.setOnClickListener(view -> {
            onBackPressed();
        });
    }

    private void deletePopup(String comment_id, RowCommentBinding bi){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
//                .setTitle("Delete This Comment?")
                .setCustomTitle(customTitle())
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setCancelable(false)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /**Event fire for delete operation on comment**/
                        mainViewModel.deleteComments(comment_id);
                    }})
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        adapter.hideDeleteAlert(bi);
                    }
                });
        setDialogPosition(builder);
    }


    public void replyDeletePopup(String comment_id, Comment2Adapter adapter , RowComment2Binding bi){
        AlertDialog.Builder builder = new AlertDialog.Builder(this)
//                .setTitle("Delete This Comment?")
                .setCustomTitle(customTitle())
                .setCancelable(false)
                .setIcon(android.R.drawable.ic_dialog_alert)
                .setPositiveButton("YES", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        /**Event fire for delete operation on comment**/
                        mainViewModel.deleteComments(comment_id);
                    }})
                .setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int whichButton) {
                        adapter.hideDeleteAlert(bi);
                    }
                });
        setDialogPosition(builder);
    }

   private TextView customTitle(){
        TextView titleView = new TextView(this);
        titleView.setText("Delete This Comment?");
        titleView.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        titleView.setTextColor(ContextCompat.getColor(this, R.color.black));
        titleView.setGravity(Gravity.CENTER);
        titleView.setPadding(20, 20, 20, 20);
        titleView.setTextSize(15f);
        titleView.setTypeface(null, Typeface.BOLD);
        return  titleView;
    }

    private void setDialogPosition(AlertDialog.Builder builder){
        AlertDialog dialog = builder.create();
        dialog.getWindow().setBackgroundDrawableResource(R.drawable.dialog_rounded_background);
        dialog.show();
        dialog.getWindow().setGravity(Gravity.BOTTOM);
        WindowManager.LayoutParams layoutParams = dialog.getWindow().getAttributes();
        layoutParams.y = 150; // bottom margin
        layoutParams.width = 400;
        dialog.getWindow().setAttributes(layoutParams);
        Button negativeBtn = dialog.getButton(DialogInterface.BUTTON_NEGATIVE);
        negativeBtn.setTextSize(15);
        negativeBtn.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        negativeBtn.setTextColor(ContextCompat.getColor(this, R.color.themeBlue));
        Button positiveBtn = dialog.getButton(DialogInterface.BUTTON_POSITIVE);
        positiveBtn.setTextSize(15);
        positiveBtn.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
        negativeBtn.setTextColor(ContextCompat.getColor(this, R.color.themeBlue));
    }

    @Override
    protected void onStart() {
        super.onStart();
        getAllComments();
    }

    private void getAllComments() {
        mainViewModel.getAllComments(post_id);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed(); // close this activity and return to preview activity (if there is any)
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent();
        intent.putExtra("post_id", post_id);
        intent.putExtra("tag", "from_comment");
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onStarted(@NonNull String tag) {
        bi.progressCircular.setVisibility(View.VISIBLE);
    }

    @SuppressLint({"NotifyDataSetChanged", "SuspiciousIndentation"})
    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if (tag.equals(AppConstants.TAG_COMMENT)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    commentList.clear();
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<CommentModel>>() {
                        }.getType();
                        JsonArray js = gson.toJsonTree(responseData.getMsg()).getAsJsonArray();
                        commentList.addAll(gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type));
                    } else {
//                        UtilityHelperKt.toast(mActivity, responseData.getMsg().toString());
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    UtilityHelperKt.toast(mActivity, e.toString());
                }
            });
        }
        else if (tag.equals(AppConstants.TAG_ADD_COMMENT) || tag.equals(AppConstants.TAG_ADD_COMMENT_REPLY)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200) {
                        UtilityHelperKt.hide(bi.llReplay);
                        bi.parentId.setText("");
                        bi.tvComment.setText("");
                        getAllComments();
                    } else {
                        UtilityHelperKt.toast(mActivity, "Error");
                    }
                } catch (Exception e) {
                    UtilityHelperKt.toast(mActivity, getResources().getString(R.string.some_error_occurred));
                }
            });
        }
        else if (tag.equals(AppConstants.TAG_DELETE_COMMENT) || tag.equals(AppConstants.TAG_LIKE_COMMENT)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200) {
                        getAllComments();
                        if (tag.equals(AppConstants.TAG_DELETE_COMMENT)) {
                            UtilityHelperKt.toast(mActivity, responseData.getMsg().toString());
                        }
                    } else {
                        UtilityHelperKt.toast(mActivity, responseData.getMsg().toString());
                    }
                } catch (Exception e) {
                    UtilityHelperKt.toast(mActivity, e.toString());
                }
            });
        }
    }

    @Override
    public void onFailure(@NonNull String message) {
        bi.progressCircular.setVisibility(View.GONE);
        UtilityHelperKt.toast(mActivity, message);
    }
}