package com.app.appsinrek.main.post;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.view.View;
import android.widget.MediaController;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModelProvider;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityFinalStepBinding;
import com.app.appsinrek.models.ResponseData;
import com.app.appsinrek.models.post.PostModel;
import com.app.appsinrek.player_view.enums.PostType;
import com.app.appsinrek.viewmodels.MainViewModel;
import com.fitness.modval.interfaces.AuthListener;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.gowtham.library.utils.CompressOption;
import com.gowtham.library.utils.TrimVideo;
import com.yalantis.ucrop.UCrop;
import com.yalantis.ucrop.model.AspectRatio;

import java.io.File;
import java.lang.reflect.Type;

public class FinalStepActivity extends AppCompatActivity implements AuthListener {
    ActivityFinalStepBinding bi;
    MainViewModel mainViewModel;
    private MediaController mediaController;
    Activity mActivity;

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        bi = DataBindingUtil.setContentView(this, R.layout.activity_final_step);
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
        String type = getIntent().getStringExtra("type");
        mainViewModel.setPostType(type);
        String path = getIntent().getStringExtra("path");
        bi.etText.setVisibility(type.equals(PostType.TEXT.getValue()) ? View.VISIBLE : View.GONE);
        bi.imagePost.setVisibility(type.equals(PostType.IMAGE.getValue()) ? View.VISIBLE : View.GONE);
        bi.crop.setVisibility(type.equals(PostType.IMAGE.getValue()) ? View.VISIBLE : View.GONE);
        bi.trim.setVisibility(type.equals(PostType.VIDEO.getValue()) ? View.VISIBLE : View.GONE);
        bi.videoView.setVisibility(type.equals(PostType.VIDEO.getValue()) ? View.VISIBLE : View.GONE);
        mainViewModel.setPath(path);
        if (type.equals(PostType.VIDEO.getValue())) {
            bi.autoPlayer.setVideoURI(Uri.parse(path)); //the string of the URL mentioned above
            bi.autoPlayer.setMediaController(mediaController);
            bi.autoPlayer.requestFocus();
            bi.autoPlayer.start();
            bi.trim.setOnClickListener(view -> {
                TrimVideo.activity(path)
                        .setCompressOption(new CompressOption()) //pass empty constructor for default compress option
                        .start(this, activityResultVideo);
            });

        } else if (type.equals(PostType.IMAGE.getValue())) {
            bi.imagePost.setImageURI(Uri.parse(path));
            bi.crop.setOnClickListener(view -> {
                UCrop.Options options = new UCrop.Options();
                options.setCompressionQuality(70);
                options.setToolbarCancelDrawable(R.drawable.back_arrow);
                options.setToolbarTitle("Crop Photo");
                options.setToolbarColor(ContextCompat.getColor(this, R.color.white));
                options.setToolbarWidgetColor(ContextCompat.getColor(this, R.color.black));
                options.setStatusBarColor(ContextCompat.getColor(this, R.color.lite_blue));
                options.setRootViewBackgroundColor(R.color.white);
                options.setFreeStyleCropEnabled(true);
                options.setActiveControlsWidgetColor(ContextCompat.getColor(this, R.color.lite_blue));
                options.setAspectRatioOptions(1,
                        new AspectRatio("1:1", 1, 1),
                        new AspectRatio("3:4", 3, 4));

                File file = new File(path);
                Uri uri = Uri.fromFile(file);
                Uri destinationUri = Uri.fromFile(new File(getFilesDir(), "cropped_image.jpg"));
                mainViewModel.setPath(destinationUri.getPath());
                Intent intent = UCrop.of(uri, destinationUri).withOptions(options).getIntent(this);
                activityResultImage.launch(intent);
            });
        } else {
            bi.fram.setBackgroundColor(ContextCompat.getColor(this, R.color.white));
        }
        bi.mtPost.setOnClickListener(view -> {
            Intent intent = new Intent(mActivity, FinalStepActivity2.class);
            intent.putExtra("path", mainViewModel.getPath());
            intent.putExtra("type", mainViewModel.getPostType());
            intent.setFlags(Intent.FLAG_ACTIVITY_FORWARD_RESULT);
            startActivity(intent);
            finish();
        });
        mainViewModel.setMainListener(this);
    }

    private final ActivityResultLauncher<Intent> activityResultImage = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == RESULT_OK) {
            assert result.getData() != null;
            final Uri resultUri = UCrop.getOutput(result.getData());
//            mainViewModel.setPath(resultUri.getPath());
            bi.imagePost.setImageURI(resultUri);
        } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
            final Throwable cropError = UCrop.getError(result.getData());
        }
    });
    private final ActivityResultLauncher<Intent> activityResultVideo = registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), result -> {

        if (result.getResultCode() == RESULT_OK) {
            assert result.getData() != null;
            final Uri resultUri = Uri.parse(TrimVideo.getTrimmedVideoPath(result.getData()));
            mainViewModel.setPath(resultUri.getPath());
            bi.autoPlayer.setVideoURI(resultUri); //the string of the URL mentioned above
            bi.autoPlayer.setMediaController(mediaController);
            bi.autoPlayer.requestFocus();
            bi.autoPlayer.start();
        } else if (result.getResultCode() == UCrop.RESULT_ERROR) {
        }
    });

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // close this activity and return to preview activity (if there is any)
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
                    Toast.makeText(this, "Successfully Post", Toast.LENGTH_LONG).show();
                    Intent intent = new Intent();
                    Bundle bu = new Bundle();
                    bu.putParcelable("post", data);
                    intent.putExtra("bundle", bu);
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

    public String getPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }
}