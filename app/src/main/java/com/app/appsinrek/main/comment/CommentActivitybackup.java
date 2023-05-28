package com.app.appsinrek.main.comment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityCommentBinding;
import com.app.appsinrek.main.comment.adapter.CommentAdapter;
import com.app.appsinrek.main.comment.model.CommentModel;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.network.API_LINKS;
import com.app.appsinrek.utils.AppConstants;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.bumptech.glide.Glide;
import com.fitness.modval.interfaces.AuthListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentActivitybackup extends AppCompatActivity implements AuthListener {

    ActivityCommentBinding bi;
    Activity mActivity = this;
    private List<CommentModel> commentList = new ArrayList<>();
    private CommentAdapter adapter;
    private MainViewModel mainViewModel;
    private String post_id;
    private String post_user_id;
    private CommentModel commentModelReply;
    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setMainListener(this);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_comment);
//        setSupportActionBar(bi.toolbar);
//        if (getSupportActionBar() != null){
//            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//            getSupportActionBar().setDisplayShowHomeEnabled(true);
//        }
        post_id = getIntent().getStringExtra("post_id");
        post_user_id = getIntent().getStringExtra("post_user_id");
        if (!mainViewModel.getUser().getImage().isEmpty()) {
            Glide.with(this)
                    .load(API_LINKS.BASE_IMAGE_URL+mainViewModel.getUser().getImage())
                    .centerCrop()
                    .optionalFitCenter()
                    .placeholder(R.drawable.user2)
                    .into(bi.profileImage);
        }
        adapter = new CommentAdapter(this,view -> {
            switch(view.getId()){
                case R.id.reply:
                    commentModelReply = (CommentModel) view.getTag();
                    UtilityHelperKt.show(bi.llReplay);
                    bi.replyTo.setText(String.format("Reply to %s.", commentModelReply.getUser().getFullName()));
                    break;
//                case R.id.ivDelete:
//                    String id = (String) view.getTag();
//                    mainViewModel.deleteComments(id);
//                    break;
//                case R.id.ivDeleteReply:
//                    String id = (String) view.getTag();
//                    mainViewModel.deleteCommentsReply(id);
//                    break;
                case R.id.like_icon:
                    HashMap<Object,Object> map = (HashMap<Object,Object>) view.getTag();
                    map.put("post_id",post_id);
                    map.put("user_id",mainViewModel.getUser().getId());
                    mainViewModel.likeComment(map);
                    break;
            }
        },view->{return false;}, new Handler.Callback() {
            @Override
            public boolean handleMessage(@NonNull Message message) {
                // Your code logic goes here.
                 return true;  }  } ,
                commentList,post_user_id,mainViewModel.getUser().getId());
        bi.cancelAction.setOnClickListener(view -> {
            commentModelReply = null;
            UtilityHelperKt.hide(bi.llReplay);
        });
        bi.recycler.setAdapter(adapter);
        bi.send.setOnClickListener(view -> {
            if(!bi.tvComment.getText().toString().isEmpty()){
                if(commentModelReply != null){
//                    mainViewModel.addCommentsReply(post_id,commentModelReply.getPostComment().getId(), encodeEmoji(bi.tvComment.getText().toString()));
                }else {
//                    mainViewModel.addComments(post_id, encodeEmoji(bi.tvComment.getText().toString()));
                }
            }
        });
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
        setResult(RESULT_OK,null);
        finish();
    }

    @Override
    public void onStarted(@NonNull String tag) {
        bi.progressCircular.setVisibility(View.VISIBLE);
    }

    @SuppressLint("NotifyDataSetChanged")
    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        if(tag.equals(AppConstants.TAG_COMMENT)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    commentList.clear();
                    if (responseData.getCode() == 200) {
                        Gson gson = new Gson();
                        Type type = new TypeToken<List<CommentModel>>() {
                        }.getType();
                        commentList.addAll(gson.fromJson(gson.toJsonTree(responseData.getMsg()).toString(), type));
                    } else {
                        UtilityHelperKt.toast(mActivity, responseData.getMsg().toString());
                    }
                    adapter.notifyDataSetChanged();
                } catch (Exception e) {
                    UtilityHelperKt.toast(mActivity, e.toString());
                }
            });
        }else if(tag.equals(AppConstants.TAG_ADD_COMMENT) || tag.equals(AppConstants.TAG_ADD_COMMENT_REPLY)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200) {
                        UtilityHelperKt.hide(bi.llReplay);
                        bi.tvComment.setText("");
                       getAllComments();
                    } else {
                        UtilityHelperKt.toast(mActivity, "Error");
                    }
                } catch (Exception e) {
                    UtilityHelperKt.toast(mActivity, getResources().getString(R.string.some_error_occurred));
                }
            });
        }else if(tag.equals(AppConstants.TAG_DELETE_COMMENT) || tag.equals(AppConstants.TAG_LIKE_COMMENT)) {
            apiResponse.observe(this, responseData -> {
                bi.progressCircular.setVisibility(View.GONE);
                try {
                    if (responseData.getCode() == 200) {
                        getAllComments();
                        if(tag.equals(AppConstants.TAG_DELETE_COMMENT))
                        UtilityHelperKt.toast(mActivity, responseData.getMsg().toString());
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
        UtilityHelperKt.toast(mActivity,message);
    }
}