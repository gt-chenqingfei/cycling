package com.beastbikes.android.ble.ui.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.View;

import com.beastbikes.android.R;

/**
 * Created by secret on 16/8/23.
 */
public class BatteryView extends View {

    //电池周围
    private Paint mPaintMainStroke;
    //电量
    private Paint mPaintMainSolid;
    //电池头部
    private Paint mPaintHeader;
    //充电中
    private Paint mPaintCharging;

    private final int strokeWidth = 3;
    private final int insideMargin = 3;
    private final int solidColor = Color.BLACK;
    private final int batteryWidth = 36;
    private final int batteryHeight = 72;
    private final int headerWidth = 20;
    private final int headerHeight = 8;
    private int mPower;
    private boolean isCharging;

    private BitmapFactory.Options options;
    private Bitmap bitmap;

    public BatteryView(Context context) {
        super(context);
        this.initView();
    }

    public BatteryView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView();
    }

    public BatteryView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.initView();
    }

    private void initView() {
        mPaintMainStroke = new Paint();
        mPaintMainStroke.setColor(Color.BLACK);
        mPaintMainStroke.setStyle(Paint.Style.STROKE);
        mPaintMainStroke.setAntiAlias(true);
        mPaintMainStroke.setStrokeWidth(strokeWidth);

        mPaintMainSolid = new Paint();
        mPaintMainSolid.setStyle(Paint.Style.FILL);
        mPaintMainSolid.setColor(solidColor);

        mPaintHeader = new Paint();
        mPaintHeader.setStyle(Paint.Style.FILL);
        mPaintHeader.setColor(Color.BLACK);

        mPaintCharging = new Paint();
        options = new BitmapFactory.Options();

        this.initBitmap();
    }

    private void initBitmap() {
        bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ic_charging, options);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        //边框
        int strokeLeft = strokeWidth;
        int strokeTop = strokeWidth + headerHeight;
        int strokeRight = strokeLeft + batteryWidth + strokeWidth;
        int strokeBottom = strokeTop + batteryHeight + strokeWidth;
        RectF rectF = new RectF(strokeLeft, strokeTop, strokeRight, strokeBottom);
        canvas.drawRoundRect(rectF, 5, 5, mPaintMainStroke);

        //电量
        float powerPercent = (float) mPower / 100;
        if (powerPercent > 0) {
            if (isCharging) {
                mPaintMainSolid.setColor(Color.parseColor("#5bec81"));
            } else {
                mPaintMainSolid.setColor(Color.BLACK);
            }
            int solidLeft = strokeLeft + insideMargin + strokeWidth;
            int solidRight = batteryWidth + strokeWidth - insideMargin;
            int solidBottom = strokeTop + batteryHeight - insideMargin;
            int solidTop = solidBottom + strokeWidth - (int)((batteryHeight - insideMargin * 2) * powerPercent);
            Rect rect = new Rect(solidLeft, solidTop, solidRight, solidBottom);
            canvas.drawRect(rect, mPaintMainSolid);
        }

        //头部
        int headerLeft = strokeLeft + batteryWidth / 2 - headerWidth / 2 +  + strokeWidth;
        int headerTop = strokeTop - headerHeight;
        int headerRight = headerLeft + headerWidth;
        int headerBottom = headerTop + headerHeight;
        Rect rect3 = new Rect(headerLeft, headerTop, headerRight, headerBottom);
        canvas.drawRect(rect3, mPaintHeader);

        //充电状态
        if (isCharging) {
            int chargingLeft = strokeWidth + batteryWidth / 2 - options.outWidth / 2;
            int chargingTop = strokeWidth + batteryHeight / 2 + headerHeight - options.outHeight / 2;
            if (bitmap == null) {
                this.initBitmap();
            }
            canvas.drawBitmap(bitmap, chargingLeft, chargingTop, mPaintCharging);
        }

    }

    /**
     * set power
     * @param power
     */
    public void setPower(int power) {
        this.mPower = power;
        if (this.mPower < 0) {
            this.mPower = 0;
        } else if (this.mPower > 100) {
            this.mPower = 100;
        }

        invalidate();
    }

    /**
     * if is charging
     * @param isCharging
     */
    public void setCharging(boolean isCharging) {
        if (this.isCharging == isCharging) {
            //相同
            return;
        }
        this.isCharging = isCharging;
        invalidate();
    }
}
