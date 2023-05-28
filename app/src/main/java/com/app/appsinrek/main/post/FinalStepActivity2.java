package com.app.appsinrek.main.post;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityFinalStep2Binding;
import com.app.appsinrek.main.MainActivity;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.models.post.PostModel;
import com.app.appsinrek.player_view.enums.PostType;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.fitness.modval.interfaces.AuthListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.lang.reflect.Type;

public class FinalStepActivity2 extends AppCompatActivity implements AuthListener {
    ActivityFinalStep2Binding bi;
    MainViewModel mainViewModel;
    private MediaController mediaController;
    Activity mActivity;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this,R.layout.activity_final_step2);
        mainViewModel = new ViewModelProvider(this).get(MainViewModel.class);
        mediaController = new MediaController(this);
        mActivity = this;
        bi.setMainViewModel(mainViewModel);
        setSupportActionBar(bi.toolbar);
        if (getSupportActionBar() != null){
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            getSupportActionBar().setTitle("");
        }
        String type = getIntent().getStringExtra("type");
        mainViewModel.setPostType(type);
        String path = getIntent().getStringExtra("path");
        bi.svText.setVisibility(type.equals(PostType.TEXT.getValue())? View.VISIBLE:View.GONE);
        bi.etCaption.setVisibility(type.equals(PostType.TEXT.getValue())? View.GONE:View.VISIBLE);
        bi.rlImagePost.setVisibility(type.equals(PostType.IMAGE.getValue())? View.VISIBLE:View.GONE);
        bi.videoView.setVisibility(type.equals(PostType.VIDEO.getValue())? View.VISIBLE:View.GONE);
        mainViewModel.setPath(path);
        if(type.equals(PostType.VIDEO.getValue()))  {
            bi.autoPlayer.setVideoURI(Uri.parse(path)); //the string of the URL mentioned above
            bi.autoPlayer.setMediaController(mediaController);
            bi.autoPlayer.requestFocus();
            bi.autoPlayer.start();
        }
        else if(type.equals(PostType.IMAGE.getValue())){
            bi.imagePost.setImageURI(Uri.parse(path));
        }else{
            bi.fram.setBackgroundColor(ContextCompat.getColor(this,R.color.white));
        }
//        rotateSendIcon();

        mainViewModel.setMainListener(this);
    }

//    private void rotateSendIcon(){
//        Matrix matrix = new Matrix();
//        matrix.postRotate(-20);
//        Bitmap sendIcon = BitmapFactory.decodeResource(this.getResources(), R.drawable.send);
//        Bitmap rotatedBitmap = Bitmap.createBitmap(sendIcon , 0, 0, sendIcon .getWidth(), sendIcon .getHeight(), matrix, true);
//        Drawable d = new BitmapDrawable(getResources(), rotatedBitmap);
//        bi.postSend.setIcon(d);
//    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if(mainViewModel.getPostType().equals(PostType.IMAGE.getValue())) {
                Intent intent = new Intent(mActivity, PhotoCropActivity.class);
                intent.putExtra("path", mainViewModel.getPath());
                intent.putExtra("type", mainViewModel.getPostType());
                startActivity(intent);
            }
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if(!mainViewModel.getPostType().equals(PostType.IMAGE.getValue())) {
            Intent intent = new Intent(mActivity, PhotoCropActivity.class);
            intent.putExtra("path", mainViewModel.getPath());
            intent.putExtra("type", mainViewModel.getPostType());
            startActivity(intent);
        }
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
                    Bundle bu = new Bundle();
                    bu.putParcelable("post",data);
                    intent.putExtra("bundle",bu);
                    setResult(RESULT_OK,intent);
                    finish();
                    Intent intent_ = new Intent(this, MainActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP|Intent.FLAG_ACTIVITY_CLEAR_TASK);
                    startActivity(intent_);
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