package com.example.graphicsmedia;

import android.app.Activity;
import android.os.Bundle;

/**
 * 程序2：Matrix 图像变换。
 * 自定义 View（MatrixView）在 onDraw() 中用 Matrix + Canvas.drawBitmap() 显示
 * 原图以及平移、旋转、缩放、扭曲四种变换后的图像。
 */
public class MatrixActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_matrix);
    }
}
