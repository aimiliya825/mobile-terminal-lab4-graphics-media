package com.example.graphicsmedia;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

/**
 * 程序7：MediaPlayer + SurfaceView 播放视频。
 * 视频文件 res/raw/video.mp4（实验素材“实验7”目录）。
 * SurfaceView 负责提供显示表面，SurfaceHolder.Callback 在表面创建后
 * 把 MediaPlayer 与该表面关联（setDisplay），再由 MediaPlayer 控制播放。
 */
public class VideoPlayerActivity extends Activity
        implements View.OnClickListener, SurfaceHolder.Callback {

    private SurfaceView surfaceView;
    private SurfaceHolder surfaceHolder;
    private MediaPlayer mediaPlayer;
    private TextView tvState;
    private TextView tvProgress;

    private final Handler handler = new Handler(Looper.getMainLooper());
    private final Runnable progressTask = new Runnable() {
        @Override
        public void run() {
            updateProgress();
            handler.postDelayed(this, 500);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        surfaceView = (SurfaceView) findViewById(R.id.svVideo);
        tvState = (TextView) findViewById(R.id.tvVideoState);
        tvProgress = (TextView) findViewById(R.id.tvVideoProgress);

        // 1) 获取 SurfaceHolder 并注册回调（surfaceCreated / surfaceChanged / surfaceDestroyed）
        surfaceHolder = surfaceView.getHolder();
        surfaceHolder.addCallback(this);

        // 2) 设置视频源：res/raw/video.mp4
        mediaPlayer = MediaPlayer.create(this, R.raw.video);
        mediaPlayer.setLooping(false);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                tvState.setText("播放完成（onCompletion 回调）");
            }
        });
        tvState.setText("已加载 res/raw/video.mp4，点击“播放”开始播放");

        findViewById(R.id.btnVideoPlay).setOnClickListener(this);
        findViewById(R.id.btnVideoPause).setOnClickListener(this);
        findViewById(R.id.btnVideoStop).setOnClickListener(this);

        handler.post(progressTask);
    }

    // ---------------- SurfaceHolder.Callback ----------------

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        // 3) 显示表面创建完成后，把 MediaPlayer 与 SurfaceView 关联
        if (mediaPlayer != null) {
            mediaPlayer.setDisplay(holder);
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        if (mediaPlayer != null) {
            mediaPlayer.setDisplay(holder);
        }
        tvProgress.setText("显示表面：" + width + " × " + height
                + "    视频：" + mediaPlayer.getVideoWidth() + " × " + mediaPlayer.getVideoHeight()
                + "    时长 " + format(mediaPlayer.getDuration()));
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        // 4) 显示表面销毁时取消关联，避免泄漏
        if (mediaPlayer != null) {
            mediaPlayer.setDisplay(null);
        }
    }

    // ---------------- 播放控制 ----------------

    @Override
    public void onClick(View v) {
        if (mediaPlayer == null) {
            return;
        }
        if (v.getId() == R.id.btnVideoPlay) {
            mediaPlayer.start();                       // 播放
            tvState.setText("正在播放视频（isPlaying = true）");
        } else if (v.getId() == R.id.btnVideoPause) {
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();                   // 暂停
                tvState.setText("已暂停（isPlaying = false），再次点击“播放”继续");
            }
        } else if (v.getId() == R.id.btnVideoStop) {
            mediaPlayer.pause();
            mediaPlayer.seekTo(0);                     // 停止：暂停并回到开头
            tvState.setText("已停止，画面回到第 0 帧");
            updateProgress();
        }
    }

    private void updateProgress() {
        if (mediaPlayer == null) {
            return;
        }
        try {
            int position = mediaPlayer.getCurrentPosition();
            int duration = mediaPlayer.getDuration();
            tvProgress.setText("播放位置：" + format(position) + " / " + format(duration)
                    + "    视频：" + mediaPlayer.getVideoWidth() + " × " + mediaPlayer.getVideoHeight());
        } catch (IllegalStateException ignored) {
        }
    }

    private String format(int millis) {
        int totalSeconds = millis / 1000;
        return String.format("%02d:%02d", totalSeconds / 60, totalSeconds % 60);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacks(progressTask);
        // 5) 释放 MediaPlayer 资源
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }
}
