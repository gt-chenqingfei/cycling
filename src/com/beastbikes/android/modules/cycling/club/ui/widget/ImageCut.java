package com.beastbikes.android.modules.cycling.club.ui.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.Xfermode;
import android.util.AttributeSet;
import android.view.MotionEvent;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.preferences.ui.CutImageView;

public class ImageCut extends CutImageView {

    private Paint paint_rect = new Paint();
    private Xfermode cur_xfermode;
    private Rect r;
    private RectF rf;
    private boolean isToCutImage = false;

    public ImageCut(Context context, AttributeSet attrs) {
        super(context, attrs);
        paint_rect.setColor(getResources().getColor(
                R.color.activity_user_setting_img_cut));
        paint_rect.setAntiAlias(true);
        cur_xfermode = new PorterDuffXfermode(PorterDuff.Mode.DST_OUT);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {

        return super.onTouchEvent(event);
    }

    @SuppressLint("DrawAllocation")
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        if (isToCutImage)
            return;

        if (rf == null || rf.isEmpty()) {
            r = new Rect(0, 0, getWidth(), getHeight());
            rf = new RectF(r);
        }
        int sc = canvas.saveLayer(rf, null, Canvas.MATRIX_SAVE_FLAG
                | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                | Canvas.FULL_COLOR_LAYER_SAVE_FLAG
                | Canvas.CLIP_TO_LAYER_SAVE_FLAG | Canvas.ALL_SAVE_FLAG);
        paint_rect.setColor(Color.parseColor("#99000000"));
        canvas.drawRect(r, paint_rect);
        paint_rect.setXfermode(cur_xfermode);
        paint_rect.setColor(Color.parseColor("#ffffff"));
        float bitmapWidth = getWidth();
        float left = (getWidth() - bitmapWidth) / 2;
        float top = (getHeight() - (bitmapWidth * 326) / 640) / 2;
        float right = left + bitmapWidth;
        float botton = top + (bitmapWidth * 326) / 640;

        canvas.drawRect(left, top, right, botton, paint_rect);
        canvas.restoreToCount(sc);
        paint_rect.setXfermode(null);
    }

    public Bitmap onClip() {
        Paint paint = new Paint();
        isToCutImage = true;
        invalidate();
        setDrawingCacheEnabled(true);
        Bitmap bitmap = getDrawingCache().copy(getDrawingCache().getConfig(),
                false);
        setDrawingCacheEnabled(false);
        Bitmap tempBmp = Bitmap.createBitmap(getWidth(), (getWidth() * 326) / 640,
                Config.ARGB_8888);
        Canvas canvas = new Canvas(tempBmp);

        Rect dst = new Rect();// 屏幕
        int bitmapWidth = 0;
        if (bitmap != null) {
            bitmapWidth = bitmap.getWidth();
        }
        int left = (getWidth() - bitmapWidth) / 2;
        int top = (getHeight() - (bitmapWidth * 326) / 640) / 2;
        int right = left + bitmapWidth;
        int botton = top + (bitmapWidth * 326) / 640;
        dst.left = -left;
        dst.top = -top;
        dst.right = right;
        dst.bottom = botton;
        canvas.drawBitmap(bitmap, null, dst, paint);
        if (null != bitmap && !bitmap.isRecycled()) {
            bitmap.recycle();
        }
        isToCutImage = false;

        return Bitmap.createScaledBitmap(tempBmp, getWidth(), (getWidth() * 326) / 640, false);
    }

}
