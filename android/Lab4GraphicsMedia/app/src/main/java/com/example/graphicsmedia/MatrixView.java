package com.example.graphicsmedia;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

/**
 * 程序2：利用 Matrix 对同一张图片做平移、旋转、缩放、扭曲变换，
 * 并通过 Canvas.drawBitmap(bitmap, matrix, paint) 显示在同一画布上。
 */
public class MatrixView extends View {

    private static final int PANEL_COUNT = 5;
    private static final float PANEL_HEIGHT = 360f;

    private final String[] titles = {
            "原图 rabbit.png（150×247）",
            "平移 postTranslate(160, 20)",
            "旋转 postRotate(30°, cx, cy)",
            "缩放 postScale(1.6, 1.6, cx, cy)",
            "扭曲 postSkew(0.35, 0.05, cx, cy)"
    };

    private final Paint bitmapPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint titlePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Paint borderPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final Matrix matrix = new Matrix();
    private Bitmap bitmap;

    public MatrixView(Context context) {
        super(context);
        init();
    }

    public MatrixView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    private void init() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.rabbit);
        bitmapPaint.setFilterBitmap(true);          // 变换后开启图像过滤，避免锯齿

        titlePaint.setTextSize(32);
        titlePaint.setColor(Color.rgb(0x21, 0x21, 0x21));
        titlePaint.setFakeBoldText(true);

        borderPaint.setStyle(Paint.Style.STROKE);
        borderPaint.setStrokeWidth(3);
        borderPaint.setColor(Color.rgb(0xCC, 0xCC, 0xCC));
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        canvas.drawColor(Color.WHITE);

        // 以 1080px 宽为设计基准等比缩放
        float scale = getWidth() / 1080f;
        canvas.save();
        canvas.scale(scale, scale);

        for (int i = 0; i < PANEL_COUNT; i++) {
            float top = i * PANEL_HEIGHT;
            canvas.save();
            canvas.clipRect(0, top, 1080, top + PANEL_HEIGHT);
            canvas.translate(0, top);

            // 面板边框 + 标题
            canvas.drawRect(10, 10, 1070, PANEL_HEIGHT - 10, borderPaint);
            canvas.drawText(titles[i], 30, 50, titlePaint);

            // 面板中心点
            float cx = 540f;
            float cy = (PANEL_HEIGHT - 60f) / 2f + 70f;
            drawTransformedBitmap(canvas, i, cx, cy);

            canvas.restore();
        }
        canvas.restore();
    }

    /**
     * 按面板序号 index 构造不同的 Matrix 并绘制图片。
     * index=0 原图；1 平移；2 旋转；3 缩放；4 扭曲。
     */
    private void drawTransformedBitmap(Canvas canvas, int index, float cx, float cy) {
        float bw = bitmap.getWidth();
        float bh = bitmap.getHeight();
        // 基础缩放：把原图缩到面板内合适大小
        float baseScale = Math.min(560f / bw, 230f / bh);

        matrix.reset();
        matrix.postScale(baseScale, baseScale);                          // 1) 基础缩放
        matrix.postTranslate(cx - bw * baseScale / 2f,                     // 2) 平移到面板中心
                cy - bh * baseScale / 2f);

        switch (index) {
            case 1:  // 平移：向右 160px、向下 20px
                matrix.postTranslate(160, 20);
                break;
            case 2:  // 旋转：绕面板中心点旋转 30°
                matrix.postRotate(30, cx, cy);
                break;
            case 3:  // 缩放：以面板中心为轴放大 1.6 倍
                matrix.postScale(1.6f, 1.6f, cx, cy);
                break;
            case 4:  // 扭曲：x 方向斜切 0.35，y 方向斜切 0.05
                matrix.postSkew(0.35f, 0.05f, cx, cy);
                break;
            default: // 原图，不叠加变换
                break;
        }
        canvas.drawBitmap(bitmap, matrix, bitmapPaint);
    }
}
