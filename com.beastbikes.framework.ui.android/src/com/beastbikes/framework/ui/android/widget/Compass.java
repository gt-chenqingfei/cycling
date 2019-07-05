package com.beastbikes.framework.ui.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.util.AttributeSet;
import android.view.View;

import com.beastbikes.framework.ui.android.R;

public class Compass extends View implements SensorEventListener {

    public static enum Direction {

        NORTH,

        NORTH_EAST,

        EAST,

        SOUTH_EAST,

        SOUTH,

        SOUTH_WEST,

        WEST,

        NORTH_WEST,

    }

    static Direction getDirectionFromDegrees(float degree) {
        if (degree >= -22.5 && degree < 22.5) {
            return Direction.NORTH;
        }

        if (degree >= 22.5 && degree < 67.5) {
            return Direction.NORTH_EAST;
        }

        if (degree >= 67.5 && degree < 112.5) {
            return Direction.EAST;
        }

        if (degree >= 112.5 && degree < 157.5) {
            return Direction.SOUTH_EAST;
        }

        if (degree >= 157.5 || degree < -157.5) {
            return Direction.SOUTH;
        }

        if (degree >= -157.5 && degree < -112.5) {
            return Direction.SOUTH_WEST;
        }

        if (degree >= -112.5 && degree < -67.5) {
            return Direction.WEST;
        }

        if (degree >= -67.5 && degree < -22.5) {
            return Direction.NORTH_WEST;
        }

        return null;
    }

    private final Drawable dialFace;
    private final Drawable dialNeedle;
    private final float[] gravity = new float[3];
    private final float[] magnetic = new float[3];

    private float azimuth = 0f;

    @SuppressWarnings("unused")
    private float pitch = 0f;

    @SuppressWarnings("unused")
    private float roll = 0f;

    private Sensor aSensor;
    private Sensor mSensor;
    private SensorManager sensorManager;

    public Compass(Context context) {
        this(context, null);
    }

    public Compass(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public Compass(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        final TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.Compass);

        this.dialFace = ta.getDrawable(R.styleable.Compass_dialFace);
        this.dialNeedle = ta.getDrawable(R.styleable.Compass_dialNeedle);

        ta.recycle();
    }

    @Override
    protected void onAttachedToWindow() {
        try {
            this.sensorManager = (SensorManager) getContext().getSystemService(
                    Context.SENSOR_SERVICE);
            this.aSensor = this.sensorManager
                    .getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
            this.mSensor = this.sensorManager
                    .getDefaultSensor(Sensor.TYPE_MAGNETIC_FIELD);
            this.sensorManager.registerListener(this, this.aSensor,
                    SensorManager.SENSOR_DELAY_UI);
            this.sensorManager.registerListener(this, this.mSensor,
                    SensorManager.SENSOR_DELAY_UI);
        } catch (UnsupportedOperationException e) {
            // ignore
        }

        super.onAttachedToWindow();

        if (this.dialFace != null) {
            this.dialFace.setVisible(getVisibility() == VISIBLE, false);
        }

        if (this.dialNeedle != null) {
            this.dialNeedle.setVisible(getVisibility() == VISIBLE, false);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
        if (this.sensorManager != null) {
            this.sensorManager.unregisterListener(this, this.aSensor);
            this.sensorManager.unregisterListener(this, this.mSensor);
        }

        super.onDetachedFromWindow();

        if (this.dialFace != null) {
            this.dialFace.setVisible(false, false);
        }

        if (this.dialNeedle != null) {
            this.dialNeedle.setVisible(false, false);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        final int d;
        final int w = getWidth();
        final int h = getHeight();
        final int x = w >> 1;
        final int y = h >> 1;

        if (w > h) {
            d = h;
        } else {
            d = w;
        }

        final int r = d >> 1;

        canvas.save();

        if (this.dialFace != null) {
            this.dialFace.setBounds(x - r, y - r, x + r, y + r);
            this.dialFace.draw(canvas);
        }

        canvas.rotate(-this.azimuth, x, y);

        if (this.dialNeedle != null) {
            this.dialNeedle.setBounds(x - r, y - r, x + r, y + r);
            this.dialNeedle.draw(canvas);
        }

        canvas.restore();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        final int w;
        final int h;

        if (this.dialFace != null) {
            w = this.dialFace.getIntrinsicWidth();
            h = this.dialFace.getIntrinsicHeight();
        } else if (this.dialNeedle != null) {
            w = this.dialNeedle.getIntrinsicWidth();
            h = this.dialNeedle.getIntrinsicHeight();
        } else {
            w = 0;
            h = 0;
        }

        final int width = resolveAdjustedSize(w, Integer.MAX_VALUE,
                widthMeasureSpec);
        final int height = resolveAdjustedSize(h, Integer.MAX_VALUE,
                heightMeasureSpec);
        this.setMeasuredDimension(width, height);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        switch (event.sensor.getType()) {
            case Sensor.TYPE_ACCELEROMETER:
                System.arraycopy(event.values, 0, this.gravity, 0, 3);
                break;
            case Sensor.TYPE_MAGNETIC_FIELD:
                System.arraycopy(event.values, 0, this.magnetic, 0, 3);
                break;
            default:
                return;
        }

        final float[] R = new float[9];
        final float[] I = new float[9];
        final float[] values = new float[3];

        if (!SensorManager.getRotationMatrix(R, I, this.gravity, this.magnetic))
            return;

        SensorManager.getOrientation(R, values);

        for (int i = 0; i < values.length; i++) {
            values[i] = (float) Math.toDegrees(values[i]);
        }

        this.azimuth = values[0];
        this.pitch = values[1];
        this.roll = values[2];

        invalidate();
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    private int resolveAdjustedSize(int desiredSize, int maxSize,
                                    int measureSpec) {
        final int specMode = MeasureSpec.getMode(measureSpec);
        final int specSize = MeasureSpec.getSize(measureSpec);

        switch (specMode) {
            case MeasureSpec.UNSPECIFIED:
                return Math.min(desiredSize, maxSize);
            case MeasureSpec.AT_MOST:
                return Math.min(Math.min(desiredSize, specSize), maxSize);
            case MeasureSpec.EXACTLY:
                return specSize;
        }

        return specSize;
    }

}
