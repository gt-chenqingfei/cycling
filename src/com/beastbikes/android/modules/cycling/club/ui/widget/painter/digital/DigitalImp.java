package com.beastbikes.android.modules.cycling.club.ui.widget.painter.digital;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import com.beastbikes.framework.android.utils.DimensionUtils;

import java.text.DecimalFormat;

/**
 * @author Adrián García Lomas
 */
public class DigitalImp implements Digital {

    private float value;
    protected Paint digitPaint;
    private Context context;
    private float textSize;
    private int marginTop;
    private int color;
    private float centerX;
    private float centerY;
    private float correction;

    public DigitalImp(int color, Context context, int marginTop, int textSize) {
        this.context = context;
        this.color = color;
        this.marginTop = marginTop;
        this.textSize = textSize;
        initPainter();
        initValues();
    }

    private void initPainter() {
        digitPaint = new Paint();
        digitPaint.setAntiAlias(true);
        digitPaint.setTextSize(textSize);
        digitPaint.setColor(color);
        digitPaint.setTextAlign(Paint.Align.CENTER);
    }

    private void initValues() {
        correction = DimensionUtils.getSizeInPixels(10, context);
    }

    @Override
    public void setValue(float value) {
        this.value = value;
    }

    @Override
    public void draw(Canvas canvas) {
        DecimalFormat format = new DecimalFormat("#%");
        canvas.drawText(format.format(value / 100), centerX, centerY + marginTop / 2,
                digitPaint);
    }

    @Override
    public void setColor(int color) {
        this.color = color;
    }

    @Override
    public int getColor() {
        return this.color;
    }

    @Override
    public void onSizeChanged(int height, int width) {
        this.centerX = width / 2;
        this.centerY = height / 2;
    }
}
