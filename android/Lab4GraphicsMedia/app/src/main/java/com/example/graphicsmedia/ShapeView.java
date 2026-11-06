package com.example.graphicsmedia;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

/**
 * 程序1：自定义 View，在 onDraw() 中设置 Paint 属性并用 Canvas 绘制二维图形。
 *
 * 涉及的 Paint 属性：颜色 setColor、样式 setStyle(FILL/STROKE)、笔触宽度 setStrokeWidth、
 * 抗锯齿 setAntiAlias、线帽 setStrokeCap、文字大小 setTextSize、文字加粗 setFakeBoldText。
 * 涉及的 Canvas 方法：drawColor、drawRect、drawCircle、drawOval、drawArc、drawLine、
 * drawPoint、drawRoundRect、drawPath、drawText。
 */
public class ShapeView extends View {

    /** 主画笔：绘制图形 */
    private final Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
    /** 注释画笔：在图形下方写说明文字 */
    private final Paint labelPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private final RectF oval = new RectF();
    private final Path path = new Path();

    public ShapeView(Context context) {
        super(context);
    }

    public ShapeView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // 画布背景
        canvas.drawColor(Color.WHITE);

        // 以 1080px 宽为设计基准，按控件实际宽度等比缩放，保证不同屏幕比例一致
        float scale = getWidth() / 1080f;
        canvas.save();
        canvas.scale(scale, scale);

        paint.setAntiAlias(true);          // 抗锯齿，图形边缘平滑
        labelPaint.setAntiAlias(true);
        labelPaint.setTextSize(28);
        labelPaint.setColor(Color.DKGRAY);

        // ================= 1. 实心矩形 / 描边矩形 =================
        paint.setStyle(Paint.Style.FILL);                 // 实心
        paint.setColor(Color.rgb(0xFF, 0x57, 0x22));      // 橙色
        canvas.drawRect(60, 150, 480, 360, paint);        // 实心矩形

        paint.setStyle(Paint.Style.STROKE);               // 描边
        paint.setStrokeWidth(14);                         // 笔触宽度 14px
        paint.setColor(Color.rgb(0x21, 0x96, 0xF3));      // 蓝色
        canvas.drawRect(600, 150, 1020, 360, paint);      // 描边矩形

        canvas.drawText("实心矩形  FILL / #FF5722", 60, 410, labelPaint);
        canvas.drawText("描边矩形  STROKE / 笔触14", 600, 410, labelPaint);

        // ================= 2. 实心圆 / 圆环 =================
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(0x4C, 0xAF, 0x50));      // 绿色
        canvas.drawCircle(260, 560, 120, paint);          // 实心圆

        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(16);
        paint.setColor(Color.rgb(0x9C, 0x27, 0xB0));      // 紫色
        canvas.drawCircle(760, 560, 120, paint);          // 圆环

        canvas.drawText("实心圆  drawCircle / FILL", 60, 730, labelPaint);
        canvas.drawText("圆环  STROKE / strokeWidth=16", 600, 730, labelPaint);

        // ================= 3. 椭圆 / 圆弧 =================
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(0xE9, 0x1E, 0x63));      // 玫红
        oval.set(60, 780, 500, 980);
        canvas.drawOval(oval, paint);                     // 椭圆

        paint.setColor(Color.rgb(0x00, 0xAC, 0xC1));      // 青色
        oval.set(600, 780, 1020, 980);
        // 画弧：startAngle=30°，sweepAngle=270°，useCenter=true（扇形）
        canvas.drawArc(oval, 30, 270, true, paint);

        canvas.drawText("椭圆  drawOval / FILL", 60, 1025, labelPaint);
        canvas.drawText("扇形  drawArc(30°, 270°, true)", 600, 1025, labelPaint);

        // ================= 4. 直线 / 点 / 圆角矩形 =================
        paint.setStyle(Paint.Style.STROKE);
        paint.setStrokeWidth(10);
        paint.setStrokeCap(Paint.Cap.ROUND);              // 线帽为圆头
        paint.setColor(Color.rgb(0x3F, 0x51, 0xB5));      // 靛蓝
        canvas.drawLine(60, 1180, 480, 1080, paint);      // 直线

        paint.setStrokeWidth(46);                         // 笔触宽度决定点的大小
        paint.setColor(Color.rgb(0xF4, 0x43, 0x36));      // 红色
        canvas.drawPoint(700, 1120, paint);               // 点1
        canvas.drawPoint(860, 1160, paint);               // 点2

        paint.setStyle(Paint.Style.FILL);
        paint.setStrokeWidth(0);
        paint.setColor(Color.rgb(0x8B, 0xC3, 0x4A));      // 浅绿
        canvas.drawRoundRect(600, 1200, 1020, 1280, 40, 40, paint); // 圆角矩形

        canvas.drawText("直线 / 点  strokeCap=ROUND", 60, 1310, labelPaint);
        canvas.drawText("圆角矩形  drawRoundRect(rx=40)", 600, 1310, labelPaint);

        // ================= 5. 路径 Path =================
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(0xFF, 0xC1, 0x07));      // 琥珀色
        path.reset();
        path.moveTo(230, 1360);                           // 三角形路径
        path.lineTo(60, 1600);
        path.lineTo(400, 1600);
        path.close();
        canvas.drawPath(path, paint);

        paint.setColor(Color.rgb(0x79, 0x55, 0x48));      // 棕色 五角星路径
        path.reset();
        path.moveTo(700, 1360);
        path.lineTo(740, 1470);
        path.lineTo(860, 1470);
        path.lineTo(762, 1540);
        path.lineTo(800, 1650);
        path.lineTo(700, 1580);
        path.lineTo(600, 1650);
        path.lineTo(638, 1540);
        path.lineTo(540, 1470);
        path.lineTo(660, 1470);
        path.close();
        canvas.drawPath(path, paint);

        canvas.drawText("路径 Path 三角形", 60, 1695, labelPaint);
        canvas.drawText("路径 Path 五角星", 600, 1695, labelPaint);

        // ================= 6. 文字 =================
        paint.setStyle(Paint.Style.FILL);
        paint.setColor(Color.rgb(0x21, 0x21, 0x21));
        paint.setTextSize(56);                            // 文字大小 56px
        paint.setFakeBoldText(true);                      // 文字加粗
        canvas.drawText("Android 绘图：Paint + Canvas", 60, 1830, paint);
        labelPaint.setTextSize(28);
        canvas.drawText("文字 drawText / setTextSize(56)", 60, 1885, labelPaint);

        canvas.restore();
    }
}
