package com.example.graphicsmedia;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.TextView;

import java.io.IOException;

/**
 * 程序6：MediaPlayer 播放音频。
 * 音频文件 res/raw/xiaoge.mp3；使用 MediaPlayer.create() 加载，
 * 通过 start()/pause()/stop()+prepare() 控制播放，onDestroy 中 release() 释放。
 */
public class AudioPlayerActivity extends Activity implements View.OnClickListener {

    private MediaPlayer mediaPlayer;
    private TextView tvState;
    private TextView tvProgress;
    private final Handler handler = new Handler(Looper.getMainLooper());

    /** 每 500ms 刷新一次播放进度 */
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
        setContentView(R.layout.activity_audio);

        tvState = (TextView) findViewById(R.id.tvAudioState);
        tvProgress = (TextView) findViewById(R.id.tvAudioProgress);

        // 1) 加载音频：MediaPlayer.create() 直接根据 res/raw 下的资源创建并准备播放器
        mediaPlayer = MediaPlayer.create(this, R.raw.xiaoge);
        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                tvState.setText("播放完成（onCompletion 回调），可点击“播放”重新播放");
            }
        });
        tvState.setText("已加载 res/raw/xiaoge.mp3，时长 " + format(mediaPlayer.getDuration()));

        findViewById(R.id.btnAudioPlay).setOnClickListener(this);
        findViewById(R.id.btnAudioPause).setOnClickListener(this);
        findViewById(R.id.btnAudioStop).setOnClickListener(this);

        handler.post(progressTask);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnAudioPlay) {
            // 2) 播放
            if (!mediaPlayer.isPlaying()) {
                mediaPlayer.start();
                tvState.setText("正在播放（isPlaying = true）");
            }
        } else if (v.getId() == R.id.btnAudioPause) {
            // 3) 暂停
            if (mediaPlayer.isPlaying()) {
                mediaPlayer.pause();
                tvState.setText("已暂停（isPlaying = false），再次点击“播放”继续");
            }
        } else if (v.getId() == R.id.btnAudioStop) {
            // 4) 停止：调用 stop() 后必须 prepare() 才能再次播放
            mediaPlayer.stop();
            try {
                mediaPlayer.prepare();
            } catch (IOException e) {
                e.printStackTrace();
            }
            mediaPlayer.seekTo(0);
            tvState.setText("已停止，并重新 prepare() 就绪");
            updateProgress();
        }
    }

    private void updateProgress() {
        if (mediaPlayer == null) {
            return;
        }
        int position = 0;
        int duration = 0;
        try {
            position = mediaPlayer.getCurrentPosition();
            duration = mediaPlayer.getDuration();
        } catch (IllegalStateException ignored) {
        }
        tvProgress.setText("播放进度：" + format(position) + " / " + format(duration));
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
