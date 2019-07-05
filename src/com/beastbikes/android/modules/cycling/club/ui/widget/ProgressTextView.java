package com.beastbikes.android.modules.cycling.club.ui.widget;

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
import com.beastbikes.android.modules.cycling.club.ui.widget.painter.digital.DigitalImp;
import com.beastbikes.android.modules.cycling.club.ui.widget.painter.progress.ProgressTextPainter;
import com.beastbikes.android.modules.cycling.club.ui.widget.painter.progress.ProgressTextPainterImp;
import com.beastbikes.android.modules.cycling.club.ui.widget.painter.velocimeter.InternalProgressPainter;
import com.beastbikes.android.modules.cycling.club.ui.widget.painter.velocimeter.InternalProgressPainterImp;
import com.beastbikes.framework.android.utils.DimensionUtils;

/**
 * @author Icedan
 */
public class ProgressTextView extends View {

    private ValueAnimator progressValueAnimator;
    private ValueAnimator powerValueAnimator;
    private Interpolator interpolator = new AccelerateDecelerateInterpolator();
    private InternalProgressPainter internalPowerPainter;
    private ProgressTextPainter progressPowerPainter;
    private DigitalImp digitalPainter;
    private int min = 0;
    private float progressLastValue = min;
    private float powerLastValue = min;
    private int max = 100;
    private float value;
    private int duration = 1000;
    private long progressDelay = 350;
    private int margin = 5;
    private int insideProgressColor = Color.parseColor("#094e35");
    private int externalProgressColor = Color.parseColor("#9cfa1d");
    private int digitalNumberColor = Color.WHITE;
    private String units = "%";

    public ProgressTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public ProgressTextView(Context context, AttributeSet attrs, int defStyleAttr) {
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
        TypedArray attributes =
                context.obtainStyledAttributes(attributeSet, R.styleable.PowerView);
        initAttributes(attributes);

        int marginPixels = DimensionUtils.getSizeInPixels(margin, getContext());
        setLayerType(LAYER_TYPE_SOFTWARE, null);
        internalPowerPainter =
                new InternalProgressPainterImp(insideProgressColor, marginPixels, getContext());
        progressPowerPainter =
                new ProgressTextPainterImp(externalProgressColor, max, marginPixels, getContext());
        initValueAnimator();

        digitalPainter = new DigitalImp(digitalNumberColor, getContext(),
                DimensionUtils.getSizeInPixels(6, context), DimensionUtils.getSizeInPixels(8, context));
    }

    private void initAttributes(TypedArray attributes) {
        insideProgressColor =
                attributes.getColor(R.styleable.PowerView_inside_progress_color, insideProgressColor);
        externalProgressColor = attributes.getColor(R.styleable.PowerView_external_progress_color,
                externalProgressColor);
        digitalNumberColor =
                attributes.getColor(R.styleable.PowerView_digital_number_color, digitalNumberColor);
        max = attributes.getInt(R.styleable.PowerView_max, max);
        units = attributes.getString(R.styleable.PowerView_units);
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
        }
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
