package com.beastbikes.android.modules.cycling.activity.ui.record.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.github.mikephil.charting.utils.Utils;

/**
 *
 * Created by secret on 16/10/20.
 */

public class CustomVerticalLineView extends View {

    private float startX;

    private float startY;

    private float stopX;

    private float stopY;

    private Paint p;

    public CustomVerticalLineView(Context context) {
        super(context);
        this.initView(context);
    }

    public CustomVerticalLineView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context);
    }

    public CustomVerticalLineView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context);
    }

    private void initView(Context context) {
        WindowManager wm = (WindowManager) getContext().getSystemService(
                Context.WINDOW_SERVICE);

        float width = wm.getDefaultDisplay().getWidth();

        float height = wm.getDefaultDisplay().getHeight();

//        startX = width / 2;
//        stopX = width / 2;
        startX = 0;
        stopX = 0;

        startY = 0;
        stopY = height;

        p = new Paint();

        p.setColor(0xffe6e6e6);

    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
//        Log.d("TAG", "onDraw is called!" + Utils.convertDpToPixel(15.f));
        canvas.drawLine(startX, startY, stopX, stopY, p);
    }

    public void update(float x) {
        Log.d("TAG", "x: " + x);
        startX = x;
        stopX = x;
        invalidate();
    }
}
