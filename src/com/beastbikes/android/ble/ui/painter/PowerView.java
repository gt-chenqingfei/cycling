package com.beastbikes.android.ble.ui.painter;

import android.animation.ValueAnimator;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

import com.beastbikes.android.R;
import com.beastbikes.android.ble.ui.painter.digital.Digital;
import com.beastbikes.android.ble.ui.painter.digital.DigitalImp;
import com.beastbikes.android.ble.ui.painter.progress.ProgressPowerPainter;
import com.beastbikes.android.ble.ui.painter.progress.ProgressPowerPainterImp;
import com.beastbikes.android.ble.ui.painter.velocimeter.InternalPowerPainter;
import com.beastbikes.android.ble.ui.painter.velocimeter.InternalPowerPainterImp;
import com.beastbikes.android.ble.ui.utils.DimensionUtils;

/**
 * @author Icedan
 */
public class PowerView extends View {

    private ValueAnimator progressValueAnimator;
    private ValueAnimator powerValueAnimator;
    private Interpolator interpolator = new AccelerateDecelerateInterpolator();
    private InternalPowerPainter internalPowerPainter;
    private ProgressPowerPainter progressPowerPainter;
    private Digital digitalPainter;
    private int min = 0;
    private float progressLastValue = min;
    private float powerLastValue = min;
    private int max = 100;
    private float value;
    private int duration = 1000;
    private long progressDelay = 350;
    private int margin = 15;
    private int insideProgressColor = Color.parseColor("#222222");
    private int externalProgressColor = Color.parseColor("#18ae6a");
    private int internalPowerColor = Color.WHITE;
    private int digitalNumberColor = Color.WHITE;
    private int digitalNumberBlurColor = Color.parseColor("#bbbbbb");
    private String units = "%";
    private boolean changeColor = true;
    private int strokeWidth;
    private int digitalSize;
    private int digitalUnitSize;
    private int digitalMarginTop;

    public PowerView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public PowerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        int size;
        int width = getMeasuredWidth();
        int height = getMeasuredHeight();

        if (width > height) {
            size = height;
        } else {
            size = width;
        }
        super.setMeasuredDimension(size, size);
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        internalPowerPainter.onSizeChanged(h, w);
        progressPowerPainter.onSizeChanged(h, w);
        digitalPainter.onSizeChanged(h, w);
    }

    private void init(Context context, AttributeSet attributeSet) {
        this.strokeWidth = DimensionUtils.getSizeInPixels(10, context);
        this.digitalSize = DimensionUtils.getSizeInPixels(54, context);
        this.digitalUnitSize = DimensionUtils.getSizeInPixels(18, context);
        this.digitalMarginTop = DimensionUtils.getSizeInPixels(25, context);
        this.margin = DimensionUtils.getSizeInPixels(15, getContext());
        TypedArray attributes = context.obtainStyledAttributes(attributeSet, R.styleable.PowerView);
        initAttributes(attributes);

        setLayerType(LAYER_TYPE_SOFTWARE, null);
        internalPowerPainter =
                new InternalPowerPainterImp(insideProgressColor, margin, getContext(), strokeWidth);
        progressPowerPainter =
                new ProgressPowerPainterImp(externalProgressColor, max, margin, getContext(), strokeWidth);
        initValueAnimator();

        digitalPainter = new DigitalImp(digitalNumberColor, digitalNumberBlurColor, getContext(),
                digitalMarginTop, digitalSize, units, this.digitalUnitSize);
    }

    private void initAttributes(TypedArray attributes) {
        insideProgressColor =
                attributes.getColor(R.styleable.PowerView_inside_progress_color, insideProgressColor);
        externalProgressColor = attributes.getColor(R.styleable.PowerView_external_progress_color,
                externalProgressColor);
        internalPowerColor =
                attributes.getColor(R.styleable.PowerView_internal_velocimeter_color, internalPowerColor);
        digitalNumberColor = attributes.getColor(R.styleable.PowerView_digital_number_color, digitalNumberColor);
        digitalNumberBlurColor = attributes.getColor(R.styleable.PowerView_digital_number_blur_color, digitalNumberBlurColor);
        max = attributes.getInt(R.styleable.PowerView_max, max);
        units = attributes.getString(R.styleable.PowerView_units);
        this.digitalSize = (int) attributes.getDimension(R.styleable.PowerView_digital_number_size, digitalSize);
        this.digitalUnitSize = (int) attributes.getDimension(R.styleable.PowerView_digital_unit_size, digitalUnitSize);
        this.strokeWidth = (int) attributes.getDimension(R.styleable.PowerView_stroke_width, strokeWidth);
        this.margin = (int) attributes.getDimension(R.styleable.PowerView_progress_margin, margin);
        if (units == null) {
            units = "%";
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        digitalPainter.draw(canvas);
        internalPowerPainter.draw(canvas);
        progressPowerPainter.draw(canvas);
        invalidate();
    }

    public void setValue(float value) {
        this.value = value;
        if (value <= max && value >= min) {
            animateProgressValue();
            internalPowerPainter.setValue(value);
        }
    }

    public void setValue(float value, boolean animate) {
        this.value = value;
        if (value <= max && value >= min) {
            if (!animate) {
                updateValueProgress(value);
                updateValueNeedle(value);
            } else {
                animateProgressValue();
            }

            int progressColor = externalProgressColor;
            if (value > 0 && value <= 20 && changeColor) {
                progressColor = Color.parseColor("#d62424");
            }

            progressPowerPainter.setColor(progressColor);
        }
    }

    public float getValue() {
        return this.value;
    }

    public void setChangeColor(boolean changeColor) {
        this.changeColor = changeColor;
    }

    public void setVersion(String version) {
        this.digitalPainter.setDesc(version);
    }

    public void setTitle(int resId) {
        this.digitalPainter.setTitle(getContext().getString(resId));
    }

    public void setTitle(String title) {
        this.digitalPainter.setTitle(title);
    }

    private void initValueAnimator() {
        progressValueAnimator = new ValueAnimator();
        progressValueAnimator.setInterpolator(interpolator);
        progressValueAnimator.addUpdateListener(new ProgressAnimatorListenerImp());
        powerValueAnimator = new ValueAnimator();
        powerValueAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        powerValueAnimator.addUpdateListener(new NeedleAnimatorListenerImp());
    }

    private void animateProgressValue() {
        if (progressValueAnimator != null) {
            progressValueAnimator.setFloatValues(progressLastValue, value);
            progressValueAnimator.setDuration(duration + progressDelay);
            progressValueAnimator.start();
            powerValueAnimator.setFloatValues(powerLastValue, value);
            powerValueAnimator.setDuration(duration);
            powerValueAnimator.start();
        }
    }

    public void setProgress(Interpolator interpolator) {
        this.interpolator = interpolator;

        if (progressValueAnimator != null) {
            progressValueAnimator.setInterpolator(interpolator);
        }
    }

    public float getMax() {
        return max;
    }

    public void setMax(int max) {
        this.max = max;
    }

    private void updateValueProgress(float value) {
        progressPowerPainter.setValue(value);
        internalPowerPainter.setValue(value);
    }

    private void updateValueNeedle(float value) {
        digitalPainter.setValue(value);
    }

    private class ProgressAnimatorListenerImp implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            Float value = (Float) valueAnimator.getAnimatedValue();
            updateValueProgress(value);
            progressLastValue = value;
        }
    }

    private class NeedleAnimatorListenerImp implements ValueAnimator.AnimatorUpdateListener {
        @Override
        public void onAnimationUpdate(ValueAnimator valueAnimator) {
            Float value = (Float) valueAnimator.getAnimatedValue();
            updateValueNeedle(value);
            powerLastValue = value;
        }
    }

}
