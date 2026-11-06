package com.example.graphicsmedia;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

/**
 * 实验四 图形图像和多媒体技术 —— 七个程序的总入口。
 * 胡俊峰  202402323  计算机249
 */
public class MainActivity extends Activity implements View.OnClickListener {

    /** 七个程序对应的 Activity */
    private final Class<?>[] targets = {
            PaintCanvasActivity.class,   // 程序1 Paint 与 Canvas 绘图
            MatrixActivity.class,        // 程序2 Matrix 图像变换
            FrameAnimActivity.class,     // 程序3 帧动画
            TweenAnimActivity.class,     // 程序4 补间动画
            SoundPoolActivity.class,     // 程序5 SoundPool 音效播放
            AudioPlayerActivity.class,   // 程序6 MediaPlayer 音频播放
            VideoPlayerActivity.class    // 程序7 MediaPlayer + SurfaceView 视频播放
    };

    /** 七个按钮的 id，与 targets 一一对应 */
    private final int[] buttonIds = {
            R.id.btnPaint, R.id.btnMatrix, R.id.btnFrame, R.id.btnTween,
            R.id.btnSound, R.id.btnAudio, R.id.btnVideo
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        for (int id : buttonIds) {
            findViewById(id).setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        for (int i = 0; i < buttonIds.length; i++) {
            if (v.getId() == buttonIds[i]) {
                startActivity(new Intent(this, targets[i]));
                return;
            }
        }
    }
}
