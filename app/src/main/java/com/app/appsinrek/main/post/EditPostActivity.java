package com.app.appsinrek.main.post;

import static com.app.appsinrek.utils.CommonUtils.decodeEmoji;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityEditPostBinding;
import com.app.appsinrek.main.dashboard.VideoViewActivity;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.models.post.PostModel;
import com.app.appsinrek.player_view.enums.PostType;
import com.app.appsinrek.utils.CommonUtils;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.bumptech.glide.Glide;
import com.fitness.modval.interfaces.AuthListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;
import java.util.HashMap;

public class EditPostActivity extends AppCompatActivity implements AuthListener {
    ActivityEditPostBinding bi;
    MainViewModel mainViewModel;
    private MediaController mediaController;
    Activity mActivity;
    PostModel postmodel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_edit_post);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mediaController = new MediaController(this);
        mActivity = this;
        bi.setMainViewModel(mainViewModel);
        setSupportActionBar(bi.toolbar);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
        if (getIntent().hasExtra("post")) {
            postmodel = getIntent().getParcelableExtra("post");
        }
        String path = postmodel.getPost() != null ? postmodel.getPost().getAttachment() : null;
        bi.imagePost.setVisibility((path != null && !path.isEmpty() && (postmodel.getPost().getType().equals(PostType.IMAGE.getValue()) || postmodel.getPost().getType().equals(PostType.VIDEO.getValue())) ? View.VISIBLE : View.GONE));
        bi.playButton.setVisibility(postmodel.getPost().getType().equals(PostType.VIDEO.getValue()) ? View.VISIBLE : View.GONE);
        bi.etCaption.setText(decodeEmoji(postmodel.getPost().getCaption()));
        if (path != null && (postmodel.getPost().getType().equals(PostType.IMAGE.getValue()) || postmodel.getPost().getType().equals(PostType.VIDEO.getValue()))) {
            bi.imagePost.setTag(postmodel);
            bi.imagePost.setOnClickListener(view -> {
                startActivity(new Intent(this, VideoViewActivity.class)
                        .putExtra("post", postmodel.getPost())
                        .putExtra("user", postmodel.getUser())
                );
            });
            Glide.with(mActivity)
                    .load(path)
                    .centerCrop()
                    .optionalFitCenter()
                    .placeholder(R.drawable._1)
                    .into(bi.imagePost);
        }

        mainViewModel.setMainListener(this);
        bi.update.setOnClickListener(view -> {
            HashMap<Object, Object> map = new HashMap<>();
            map.put("post_id", postmodel.getPost().getId());
            map.put("caption", CommonUtils.encodeEmoji(bi.etCaption.getText().toString()));
            mainViewModel.updatePost(map);
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onStarted(@NonNull String tag) {
        bi.progressCircular.setVisibility(View.VISIBLE);
    }

    @Override
    public void onSuccess(@NonNull String tag, @NonNull LiveData<ResponseData> apiResponse) {
        apiResponse.observe(this, responseData -> {
            bi.progressCircular.setVisibility(View.GONE);
            try {
                if (responseData.getCode() == 200) {
                    Gson gson = new Gson();
                    Type type = new TypeToken<PostModel>() {
                    }.getType();
                    PostModel data = gson.fromJson(gson.toJsonTree(responseData.getMsg()), type);
                    Toast.makeText(this, "Successfully Update Post", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    Bundle bu = new Bundle();
                    bu.putParcelable("post", data);
                    intent.putExtra("bundle", bu);
                    intent.putExtra("tag", "update");
                    setResult(RESULT_OK, intent);
                    finish();
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