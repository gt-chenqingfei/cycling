package com.beastbikes.android.ble.ui.painter.digital;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.TextUtils;

import com.beastbikes.android.R;
import com.beastbikes.android.ble.ui.utils.DimensionUtils;

/**
 * @author Adrián García Lomas
 */
public class DigitalImp implements Digital {

    private float value;
    protected Paint digitPaint;
    protected Paint textPaint;
    protected Paint paint;
    private Context context;
    private float textSize;
    private float unitSize;
    private int marginTop;
    private int color;
    private float centerX;
    private float centerY;
    private float correction;
    private String units;
    private String title;
    private String desc;
    private int descColor = Color.parseColor("#bbbbbb");

    public DigitalImp(int color, int descColor, Context context, int marginTop, int textSize,
                      String units, int unitSize) {
        this.context = context;
        this.color = color;
        this.descColor = descColor;
        this.marginTop = marginTop;
        this.textSize = textSize;
        this.unitSize = unitSize;
        this.units = units;
        initPainter();
        initValues();
    }

    private void initPainter() {
        digitPaint = new Paint();
        digitPaint.setAntiAlias(true);
        digitPaint.setTextSize(textSize);
        digitPaint.setColor(color);
        Typeface typeface = Typeface.createFromAsset(context.getAssets(),
                    "fonts/BebasNeue.otf");
        if (null == typeface) {
            typeface = Typeface.DEFAULT;
        }
        digitPaint.setTypeface(typeface);
        digitPaint.setTextAlign(Paint.Align.CENTER);
        textPaint = new TextPaint();
        textPaint.setAntiAlias(true);
        textPaint.setTextSize(this.unitSize);
        textPaint.setColor(descColor);
        textPaint.setTypeface(typeface);
        textPaint.setTextAlign(Paint.Align.CENTER);

        paint = new TextPaint();
        paint.setAntiAlias(true);
        paint.setTextSize(DimensionUtils.getSizeInPixels(12, context));
        paint.setColor(descColor);
        paint.setTypeface(Typeface.DEFAULT);
        paint.setTextAlign(Paint.Align.CENTER);
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
        if (!TextUtils.isEmpty(title)) {
            canvas.drawText(title, centerX, centerY - marginTop - 20,
                    paint);
        }

        String digitText = String.format("%.0f", value);
        canvas.drawText(digitText, centerX, centerY + marginTop / 2 + 10, digitPaint);

        float digitWidth = digitPaint.measureText(digitText);
        float unitX = centerX + digitWidth * 9 / 10;
        if (value < 10) {
            unitX = unitX + unitX / 10;
        } else if (value > 99){
            unitX = unitX - unitX / 10;
        }
        canvas.drawText(units, unitX, centerY + marginTop / 2 + 5, textPaint);

        if (!TextUtils.isEmpty(desc)) {
            canvas.drawText(desc, centerX, centerY + marginTop + 50, textPaint);
        }
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

    public void setDesc(String desc) {
        this.desc = desc;
    }

    @Override
    public void setTitle(String title) {
        this.title = title;
    }
}
