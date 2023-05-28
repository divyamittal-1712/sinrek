package com.app.appsinrek.main.dashboard;

import static com.google.android.exoplayer2.ui.StyledPlayerView.SHOW_BUFFERING_NEVER;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.app.appsinrek.R;
import com.app.appsinrek.databinding.ActivityVideoViewBinding;
import com.app.appsinrek.models.User;
import com.app.appsinrek.models.post.Post;
import com.app.appsinrek.player_view.enums.PostType;
import com.bumptech.glide.Glide;
import com.google.android.exoplayer2.ExoPlayer;
import com.google.android.exoplayer2.MediaItem;
import com.google.android.exoplayer2.Player;
import com.google.android.exoplayer2.source.DefaultMediaSourceFactory;
import com.google.android.exoplayer2.source.ProgressiveMediaSource;
import com.google.android.exoplayer2.upstream.DataSource;
import com.google.android.exoplayer2.upstream.DefaultDataSource;
import com.google.android.exoplayer2.util.Util;

public class VideoViewActivity extends AppCompatActivity {

    private ActivityVideoViewBinding bi;
    ExoPlayer simpleExoPlayer;
    private DefaultMediaSourceFactory mediaSourceFactory;
    private ProgressiveMediaSource mediaSource;

    Post post;
    User user;
    private String path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bi = ActivityVideoViewBinding.inflate(getLayoutInflater());
        setContentView(bi.getRoot());
        if(getIntent().hasExtra("post")){
            post = getIntent().getParcelableExtra("post");
        }
        if(getIntent().hasExtra("user")){
            user = getIntent().getParcelableExtra("user");
        }
        bi.icBack.setOnClickListener(view -> {
            finish();
        });
    }
    private void initializePlayer() {
        path = post.getAttachment();
        if(path!= null && post.getType().equals(PostType.VIDEO.getValue())){
            bi.videoPost.setVisibility(View.VISIBLE);
            DataSource.Factory mediaDataSourceFactory = new DefaultDataSource.Factory(this);

            mediaSource = new ProgressiveMediaSource.Factory(mediaDataSourceFactory)
                    .createMediaSource(MediaItem.fromUri(path));

            mediaSourceFactory = new
                    DefaultMediaSourceFactory(mediaDataSourceFactory);

            simpleExoPlayer = new ExoPlayer.Builder(this)
                    .setMediaSourceFactory(mediaSourceFactory)
                    .build();

            simpleExoPlayer.addMediaSource(mediaSource);

            simpleExoPlayer.setPlayWhenReady(true);
            bi.playerView.setPlayer(simpleExoPlayer);
            bi.playerView.setShowBuffering(SHOW_BUFFERING_NEVER);
            bi.playerView.setControllerShowTimeoutMs(0);
            bi.playerView.requestFocus();
            bi.playerView.setUseController(true);
            simpleExoPlayer.setRepeatMode(Player.REPEAT_MODE_ALL);
            simpleExoPlayer.prepare();
            bi.ivVolume.setOnClickListener(view -> {

                if (simpleExoPlayer.getVolume() == 0F) {
                    bi.ivVolume.setImageResource(R.drawable.ic_volume_on);
                    setTwoStateVolume(false);
                } else {
                    bi.ivVolume.setImageResource(R.drawable.ic_volume_off);
                    setTwoStateVolume(true);
                }
            });
        }
        else{
            bi.imagePost.setVisibility(View.VISIBLE);
            Glide.with(this)
                    .load(path)
                    .centerCrop()
                    .optionalFitCenter()
                    .placeholder(R.drawable._1)
                    .into(bi.imagePost);
        }
    }
    void setTwoStateVolume(Boolean mute) {
        simpleExoPlayer.setVolume(mute?0F:1F);
    }
    @SuppressLint("SuspiciousIndentation")
    private void releasePlayer() {
        if (path!= null && post.getType().equals(PostType.VIDEO.getValue()))
        simpleExoPlayer.release();
    }
    @Override
    public void onStart() {
        super.onStart();

        if (Util.SDK_INT > 23) initializePlayer();
    }
    @Override
    public void onResume() {
        super.onResume();
        if (Util.SDK_INT <= 23 && path!= null && post.getType().equals(PostType.VIDEO.getValue())) initializePlayer();
    }
    @Override
    public void onPause() {
        super.onPause();
        if (Util.SDK_INT <= 23 && path!= null && post.getType().equals(PostType.VIDEO.getValue())) releasePlayer();
    }
    @Override
    public void onStop() {
        super.onStop();
        if (Util.SDK_INT > 23 && path!= null && post.getType().equals(PostType.VIDEO.getValue())) releasePlayer();
    }
}