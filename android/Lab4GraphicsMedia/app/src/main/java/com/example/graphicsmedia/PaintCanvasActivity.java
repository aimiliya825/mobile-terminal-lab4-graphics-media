package com.example.graphicsmedia;

import android.app.Activity;
import android.os.Bundle;

/**
 * 程序1：Paint 与 Canvas 绘图。
 * 布局中使用自定义 View（ShapeView），其 onDraw() 完成全部二维图形的绘制。
 */
public class PaintCanvasActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_paint_canvas);
    }
}
