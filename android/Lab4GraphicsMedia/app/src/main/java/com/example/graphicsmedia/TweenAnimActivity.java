package com.example.graphicsmedia;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * 程序4：补间动画（Tween Animation）。
 * 四个基本动画的 XML 资源：
 *   res/anim/alpha.xml     透明度动画 fromAlpha 1.0 → toAlpha 0.2
 *   res/anim/scale.xml     缩放动画 fromX/YScale 1.0 → 2.0，中心为轴
 *   res/anim/translate.xml 平移动画 (0,0) → (300,300)
 *   res/anim/rotate.xml    旋转动画 0° → 360°，中心为轴
 *   res/anim/anim_set.xml  组合动画（alpha 5000ms + scale 9000ms + translate 10000ms + rotate 10000ms）
 */
public class TweenAnimActivity extends Activity implements View.OnClickListener {

    private ImageView ivAndroid;
    private TextView tvState;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tween);

        ivAndroid = (ImageView) findViewById(R.id.ivTween);
        tvState = (TextView) findViewById(R.id.tvTweenState);

        findViewById(R.id.btnAlpha).setOnClickListener(this);
        findViewById(R.id.btnScale).setOnClickListener(this);
        findViewById(R.id.btnTranslate).setOnClickListener(this);
        findViewById(R.id.btnRotate).setOnClickListener(this);
        findViewById(R.id.btnCombo).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.btnAlpha) {
            start(R.anim.alpha, "透明度动画 alpha：1.0 → 0.2，持续 2000ms");
        } else if (id == R.id.btnScale) {
            start(R.anim.scale, "缩放动画 scale：1.0 → 2.0 倍，以图片中心为轴");
        } else if (id == R.id.btnTranslate) {
            start(R.anim.translate, "平移动画 translate：(0,0) → (300,300)");
        } else if (id == R.id.btnRotate) {
            start(R.anim.rotate, "旋转动画 rotate：0° → 360°，以图片中心为轴");
        } else if (id == R.id.btnCombo) {
            start(R.anim.anim_set, "组合动画 set：透明度+缩放+平移+旋转同时进行（5000/9000/10000/10000ms）");
        }
    }

    /**
     * 加载并启动补间动画。
     * AnimationUtils.loadAnimation() 解析 XML 动画资源，startAnimation() 作用到 View 上。
     */
    private void start(int resId, final String desc) {
        Animation animation = AnimationUtils.loadAnimation(this, resId);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                tvState.setText("开始播放：" + desc);
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                tvState.setText("播放结束：" + desc + "（可再次点击重播）");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }
        });
        ivAndroid.clearAnimation();   // 清除上一次动画，避免叠加
        ivAndroid.startAnimation(animation);
    }
}
