package com.beastbikes.android.modules.cycling.activity.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Point;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.beastbikes.android.R;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by chenqingfei on 16/3/9.
 */
public class ActivityBlurLayer extends View {

    private List<Point> points = new ArrayList<Point>();
    private Paint p;
    private PaintFlagsDrawFilter pfd;
    private Bitmap startBitmap, endBitmap;
    private DrawFilter filter;


    public ActivityBlurLayer(Context context) {

        super(context);
        init();
    }

    public ActivityBlurLayer(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public ActivityBlurLayer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    public void refheshView(List<Point> points) {
        if (points != null) {
            this.points.clear();
            this.points.addAll(points);
        }
        invalidate();
    }

    private void init() {
        p = new Paint();
        p.setColor(0xffff102d);// 设置红色
        p.setAntiAlias(true);
        this.filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG);

        p.setStrokeWidth((float) 5.0);
        p.setAntiAlias(true);
        pfd = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG);
        startBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_activity_detail_start);
        endBitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_activity_finish_end);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(filter);
        super.onDraw(canvas);
        if (points == null || points.size() <= 0) {
            return;
        }

        Point start = null;
        Point end = null;

        if (points.size() > 1) {
            start = points.get(0);
        }

        if (points.size() > 2) {
            end = points.get(points.size() - 1);
        }

        for (int i = 0; i < points.size(); i++) {
            if (i < points.size() - 1) {


                Point from = points.get(i);
                Point to = points.get(i + 1);
                p.setAntiAlias(true);
                if (from != null && to != null) {
                    canvas.drawLine(from.x, from.y, to.x, to.y, p);
                }

            }
        }
        canvas.setDrawFilter(pfd);
        if (startBitmap != null && start != null) {
            canvas.drawBitmap(startBitmap, start.x, start.y, p);
        }
        if (endBitmap != null && end != null) {
            canvas.drawBitmap(endBitmap, end.x, end.y, p);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (startBitmap != null && !startBitmap.isRecycled()) {
            startBitmap.isRecycled();
        }

        if (endBitmap != null && !endBitmap.isRecycled()) {
            endBitmap.isRecycled();
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return true;
    }
}
