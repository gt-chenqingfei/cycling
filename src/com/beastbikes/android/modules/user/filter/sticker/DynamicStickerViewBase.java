package com.beastbikes.android.modules.user.filter.sticker;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.DrawFilter;
import android.graphics.Paint;
import android.graphics.PaintFlagsDrawFilter;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.modules.user.dto.WaterMark;

/**
 * Created by caoxiao on 16/2/17.
 */
public class DynamicStickerViewBase extends View {

    private final Paint paint;

    private final DrawFilter filter;

    private Bitmap cover;

    private boolean isChineseVersion;

    private ActivityDTO dto;

    private boolean reverseMode;

    protected Context context;

    private WaterMark waterMark;

    public DynamicStickerViewBase(Context context) {
        this(context, null);
    }

    public DynamicStickerViewBase(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DynamicStickerViewBase(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.paint = new Paint();
        this.paint.setAntiAlias(true);
        this.filter = new PaintFlagsDrawFilter(0, Paint.ANTI_ALIAS_FLAG
                | Paint.FILTER_BITMAP_FLAG);
    }

    public void setReverseMode(boolean reverse) {
        this.reverseMode = reverse;
        this.invalidate();
    }

    public boolean isReverseMode() {
        return this.reverseMode;
    }

    public Paint getPaint() {
        return this.paint;
    }

    public void setCover(Bitmap cover) {
//        if (cover == null) {
//            this.cover = null;
//        } else {
        this.cover = cover;
//        }
        this.invalidate();
    }

    public void setCoverNull() {
        this.cover = null;
        this.invalidate();
    }

    public Bitmap getCover() {
        return this.cover;
    }

    public WaterMark getWaterMark() {
        return waterMark;
    }

    public void setWaterMark(WaterMark waterMark) {
        this.waterMark = waterMark;
        this.invalidate();
    }

    public void setActivityDto(ActivityDTO dto) {
        this.dto = dto;
    }

    public ActivityDTO getActivityDTO() {
        return this.dto;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        int widthSize = MeasureSpec.getSize(widthMeasureSpec);
        if (widthSize <= 0) {
            widthSize = Integer.MAX_VALUE;
        }

        int heightSize = MeasureSpec.getSize(heightMeasureSpec);
        if (heightSize <= 0) {
            heightSize = Integer.MAX_VALUE;
        }

        final int size = Math.min(widthSize, heightSize);
        setMeasuredDimension(size, size);
    }

    @Override
    @SuppressLint("DrawAllocation")
    protected void onDraw(Canvas canvas) {
        canvas.setDrawFilter(filter);
        super.onDraw(canvas);
        final float w = this.getWidth();
        final float h = this.getHeight();
        final float ratioDst = w / h;

        if (null != this.cover) {
            final float width = this.cover.getWidth();
            final float height = this.cover.getHeight();
            final float ratioSrc = width / height;
            final Rect dst = new Rect(0, 0, (int) w, (int) h);
            if (ratioSrc > ratioDst) {
                final float wDst = h * ratioSrc;
                dst.left = (int) ((w - wDst) / 2f);
                dst.right = (int) (dst.left + wDst);
            } else if (ratioSrc < ratioDst) {
                final float hDst = w / ratioSrc;
                dst.right = (int) w;
                dst.top = (int) ((h - hDst) / 2f);
                dst.bottom = (int) (dst.top + hDst);
            }
            canvas.drawBitmap(this.cover, null, dst, this.paint);
        }
    }

    @Override
    protected void onDetachedFromWindow() {
//        if (null != this.whiteSticker) {
//            this.whiteSticker.recycle();
//            this.whiteSticker = null;
//        }

        super.onDetachedFromWindow();
    }

    public boolean isChineseVersion() {
        return isChineseVersion;
    }

    public void setIsChineseVersion(boolean isChineseVersion) {
        this.isChineseVersion = isChineseVersion;
    }

}
