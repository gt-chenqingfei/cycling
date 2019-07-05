package com.beastbikes.android.ble.ui.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.util.AttributeSet;
import android.widget.TextView;

import com.beastbikes.android.R;

/**
 * Created by secret on 16/8/23.
 * 含有边框和圆角的TextView
 */
public class TextViewWithBoardAndCorners extends TextView {

    private int mBoardColor;
    private float radius;
    private int solidColor;

    public TextViewWithBoardAndCorners(Context context) {
        super(context);
        this.initView(context, null, 0);
    }

    public TextViewWithBoardAndCorners(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context, attrs, 0);
    }

    public TextViewWithBoardAndCorners(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView(context, attrs, defStyleAttr);
    }

    private void initView(Context context, AttributeSet attrs, int defStyleAttr) {

        TypedArray typedArray = context.obtainStyledAttributes(attrs, R.styleable.TextViewWithBoardAndCorners, defStyleAttr, 0);
        mBoardColor = typedArray.getColor(R.styleable.TextViewWithBoardAndCorners_drawableColor, Color.WHITE);
        radius = typedArray.getDimension(R.styleable.TextViewWithBoardAndCorners_radius, 0);
        solidColor = typedArray.getColor(R.styleable.TextViewWithBoardAndCorners_solidColor, Color.TRANSPARENT);
        typedArray.recycle();

        setBackgroundDrawable(getResources().getDrawable(R.drawable.bg_white_with_black_board_and_corners));
        GradientDrawable gradientDrawable = (GradientDrawable) getBackground();
        gradientDrawable.setStroke(1, mBoardColor);
        gradientDrawable.setCornerRadius(radius);
        gradientDrawable.setColor(solidColor);

    }

    /**
     * set stroke color
     * @param color
     */
    public void setBoardColor(int color) {
        this.mBoardColor = color;
        GradientDrawable gradientDrawable = (GradientDrawable) getBackground();
        gradientDrawable.setStroke(1, mBoardColor);
    }

    /**
     * set solid color
     * @param color
     */
    public void setSolidColor(int color) {
        this.solidColor = color;
        GradientDrawable gradientDrawable = (GradientDrawable) getBackground();
        gradientDrawable.setColor(solidColor);
    }

    /**
     * set radius
     * @param radius
     */
    public void setRadius(float radius) {
        this.radius = radius;
        GradientDrawable gradientDrawable = (GradientDrawable) getBackground();
        gradientDrawable.setColor(solidColor);
    }
}
