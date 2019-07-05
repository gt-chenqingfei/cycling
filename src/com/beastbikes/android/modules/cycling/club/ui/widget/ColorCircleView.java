package com.beastbikes.android.modules.cycling.club.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import com.beastbikes.android.R;

/**
 * Created by zhangyao on 2016/3/25.
 */
public class ColorCircleView extends View{
    private Paint paint;

    public ColorCircleView(Context context) {
        super(context);
    }

    public ColorCircleView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs, 0);
    }

    public ColorCircleView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,0);
    }

    private void init(Context context, AttributeSet attrs,int defStyleAttr){
        TypedArray a = context.getTheme().
                obtainStyledAttributes(attrs, R.styleable.ColorCircleView, defStyleAttr, 0);
        paint =new Paint();
        for (int i =0 ; i<a.getIndexCount();i++){
            int attr = a.getIndex(i);
            switch (attr){
                case R.styleable.ColorCircleView_circle_color:
                    int color = a.getColor(attr, Color.BLACK);
                    paint.setColor(color);
                    break;
            }
            this.paint.setAntiAlias(true); //消除锯齿
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        int center = getWidth()/2;
        canvas.drawCircle(center,center, center, this.paint);
        super.onDraw(canvas);
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
