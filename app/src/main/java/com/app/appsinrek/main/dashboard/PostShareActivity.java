package com.app.appsinrek.main.dashboard;

import static com.app.appsinrek.utils.CommonUtils.decodeEmoji;
import static com.app.appsinrek.utils.CommonUtils.encodeEmoji;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityPostShareBinding;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.models.post.PostModel;
import com.app.appsinrek.network.API_LINKS;
import com.app.appsinrek.utils.UtilityHelperKt;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.fitness.modval.interfaces.AuthListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

import okhttp3.RequestBody;

public class PostShareActivity extends AppCompatActivity implements AuthListener {


    String attachment;
    String path;
    String postId, userId, created, username, fullname, caption, profileImage,full_name,type,postType;

    ActivityPostShareBinding bi;
    private MainViewModel mainViewModel;
    Activity mActivity = this;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mainViewModel.setMainListener(this);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_post_share);

        Intent intent = getIntent();
        if (intent != null) {
            postId = intent.getStringExtra("post_id");
            userId = intent.getStringExtra("userId");
            created = intent.getStringExtra("created");
            username = intent.getStringExtra("username");
            fullname = intent.getStringExtra("fullname");
            attachment = intent.getStringExtra("attachment");
            path = intent.getStringExtra("path");
            caption = intent.getStringExtra("caption");
            profileImage = intent.getStringExtra("profileImage");
            type = intent.getStringExtra("type");
            postType = intent.getStringExtra("postType");
            setUpScreenData();
        }

        bi.icBack.setOnClickListener(view -> finish());
        bi.tickId.setOnClickListener(view -> {
            hideKeyboard();
            hitApi();
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
    }
    @SuppressLint("CheckResult")
    private void setUpScreenData() {
        bi.usernameId.setText(fullname!=null && !fullname.isEmpty()?fullname:username);
        bi.usernameIdShared.setText(mainViewModel.getUser().getFullName().isEmpty()?mainViewModel.getUser().getUsername():mainViewModel.getUser().getFullName());

        bi.timeAgo.setText("" + (created));
        bi.timeAgoShared.setText("" + (created));
        String g = API_LINKS.BASE_IMAGE_URL+mainViewModel.getUser().getImage();
        String z = API_LINKS.BASE_IMAGE_URL+profileImage;
        if (mainViewModel.getUser().getImage() != null && !mainViewModel.getUser().getImage().equals("")) {
            Glide.with(mActivity)
                    .load(API_LINKS.BASE_IMAGE_URL+mainViewModel.getUser().getImage())
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .centerCrop()
                    .optionalFitCenter()
                    .placeholder(R.drawable.user2)
                    .into(bi.profPhotoIdShared);
        }
        if (profileImage != null && !profileImage.equals("")) {
            Glide.with(mActivity)
                    .load(API_LINKS.BASE_IMAGE_URL+profileImage)
                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                    .centerCrop()
                    .optionalFitCenter()
                    .placeholder(R.drawable.user2)
                    .into(bi.profPhotoId);
        }

        if (attachment != null && !attachment.equals("")) {
            try {
                ImageView imageView = new ImageView(getApplicationContext());
                Uri uri = Uri.parse(attachment);
                Glide.with(getApplicationContext())
                        .load(uri)
                        .centerCrop()
                        .into(imageView);
                bi.itemId.addView(imageView);
                Animation leftin = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_in);
                Animation leftout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.left_out);
                Animation rightin = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_in);
                Animation rightout = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.right_out);
                bi.itemId.setInAnimation(rightin);
                bi.itemId.setOutAnimation(leftout);
                bi.itemId.setFlipInterval(4000);
                bi.itemId.setAutoStart(bi.itemId.getChildCount() != 1);
            } catch (Exception v) {
                UtilityHelperKt.toast(this,v.toString());
            }
        }
        if (caption != null && !caption.equals("")) {
            bi.tvAddedCaption.setText(decodeEmoji(caption));
        }
    }

    public void hitApi(){
        HashMap<String, RequestBody> map = new HashMap<>();
        map.put("user_id",mainViewModel.toRequestBody(mainViewModel.getUser().getId()));
        map.put("post_type",mainViewModel.toRequestBody("1"));
        map.put("post_id",mainViewModel.toRequestBody(postId));
        map.put("caption",mainViewModel.toRequestBody(encodeEmoji(bi.tvCaption.getText().toString().trim())));
        map.put("type",mainViewModel.toRequestBody(type));
        map.put("location_string",mainViewModel.toRequestBody(""));
        map.put("lat",mainViewModel.toRequestBody(""));
        map.put("long",mainViewModel.toRequestBody(""));
        map.put("attachments",mainViewModel.toRequestBody(path));
        mainViewModel.mySharePost(bi.tickId,map);
    }


    public void hideKeyboard() {
        // Check if no view has focus:
        View view = getCurrentFocus();
        if (view != null) {
            InputMethodManager inputManager = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            inputManager.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    @Override
    public void onStarted(@NonNull String tag) {
        bi.progressCircular.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        apiResponse.observe(this,responseData -> {
            bi.progressCircular.setVisibility(View.GONE);
            try {
                if (responseData.getCode() == 200) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<PostModel>() {}.getType();
                    PostModel data = gson.fromJson(gson.toJsonTree(responseData.getMsg()),type);
                    Toast.makeText(this, "Successfully Post", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    PostShareActivity.this.setResult(RESULT_OK,intent);
//                    Bundle bu = new Bundle();
//                    bu.putParcelable("post",data);
//                    intent.putExtra("bundle",bu);
                    onBackPressed();
                } else {
                    Toast.makeText(this, responseData.getMsg().toString(), Toast.LENGTH_LONG).show();
                }
            } catch (Exception e) {
                Toast.makeText(this, e.toString(), Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onFailure(@NonNull String message) {
        bi.progressCircular.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}