package com.beastbikes.android.ble.ui.painter.velocimeter;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.RectF;
import android.graphics.Shader;

import com.beastbikes.android.R;
import com.beastbikes.android.ble.ui.utils.DimensionUtils;

/**
 * @author Adrián García Lomas
 */
public class InternalPowerPainterImp implements InternalPowerPainter {

    private Paint paint;
    private RectF circle;
    private Context context;
    private int width;
    private int height;
    private float startAngle = 0;
    private float finishAngle = 360;
    private int strokeWidth;
    private int blurMargin;
    private int lineWidth;
    private int lineSpace;
    private int color;
    private float max = 100;

    public InternalPowerPainterImp(int color, int margin, Context context, int strokeWidth) {
        this.blurMargin = margin;
        this.context = context;
        this.color = color;
        this.strokeWidth = strokeWidth;
        initSize();
        initPainter();
    }

    private void initSize() {
        this.lineWidth = DimensionUtils.getSizeInPixels(2, context);
        this.lineSpace = -DimensionUtils.getSizeInPixels(4, context);
    }

    private void initPainter() {
        paint = new Paint();
        paint.setAntiAlias(true);
        paint.setStrokeWidth(strokeWidth);
        paint.setColor(color);
        paint.setStyle(Paint.Style.STROKE);
        paint.setPathEffect(new DashPathEffect(new float[]{lineWidth, lineSpace}, 0));
    }

    private void initCircle() {
        int padding = strokeWidth / 2 + blurMargin;
        circle = new RectF();
        circle.set(padding, padding, width - padding, height - padding);
    }

    @Override
    public void draw(Canvas canvas) {
        canvas.drawArc(circle, startAngle, finishAngle, false, paint);
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int getColor() {
        return 0;
    }

    @Override
    public void onSizeChanged(int height, int width) {
        this.width = width;
        this.height = height;
        initCircle();
    }

    public void setValue(float value) {
        float angle = (360f * value) / max;
        this.startAngle = angle + 93;
        this.finishAngle = 354 - angle;
    }

    public float getMax() {
        return max;
    }

    public void setMax(float max) {
        this.max = max;
    }
}
