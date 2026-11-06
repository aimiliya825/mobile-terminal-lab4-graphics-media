package com.example.graphicsmedia;

import android.app.Activity;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 程序3：帧动画（Frame Animation）。
 * 帧动画由 res/anim/album_list.xml（animation-list）定义：
 * 10 帧图片 p01.png~p10.png，每帧 1000ms，oneshot=false 即循环播放。
 */
public class FrameAnimActivity extends Activity implements View.OnClickListener {

    private ImageView imageView;
    private AnimationDrawable frameAnim;
    private TextView tvState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_frame);

        imageView = (ImageView) findViewById(R.id.ivFrame);
        tvState = (TextView) findViewById(R.id.tvFrameState);

        // 1) 把 animation-list 资源设置为 ImageView 的背景
        imageView.setBackgroundResource(R.anim.album_list);
        // 2) 从背景中取出 AnimationDrawable 对象
        frameAnim = (AnimationDrawable) imageView.getBackground();

        findViewById(R.id.btnFrameStart).setOnClickListener(this);
        findViewById(R.id.btnFrameStop).setOnClickListener(this);

        tvState.setText("已加载 animation-list：共 " + frameAnim.getNumberOfFrames()
                + " 帧，每帧 1000ms，循环播放");
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.btnFrameStart) {
            // 3) 启动帧动画（必须在窗口可见后调用，否则不播放）
            if (!frameAnim.isRunning()) {
                frameAnim.start();
                tvState.setText("正在播放：p01→p10 逐帧循环，每帧 1000ms");
            }
        } else if (v.getId() == R.id.btnFrameStop) {
            // 4) 停止帧动画
            if (frameAnim.isRunning()) {
                frameAnim.stop();
                tvState.setText("帧动画已停止（当前停留在停止时的那一帧）");
            }
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // 界面不可见时停止动画，避免后台空耗 CPU
        if (frameAnim != null && frameAnim.isRunning()) {
            frameAnim.stop();
        }
    }
}
