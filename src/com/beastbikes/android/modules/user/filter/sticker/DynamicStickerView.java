package com.beastbikes.android.modules.user.filter.sticker;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Typeface;
import android.text.TextUtils;
import android.util.AttributeSet;

import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.user.dto.ActivityDTO;
import com.beastbikes.android.modules.user.dto.WaterMark;
import com.beastbikes.android.modules.user.dto.WaterMarkImage;
import com.beastbikes.android.modules.user.dto.WaterMarkSepLine;
import com.beastbikes.android.modules.user.dto.WaterMarkText;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by caoxiao on 16/2/17.
 */
public class DynamicStickerView extends DynamicStickerViewBase {

    private static final SimpleDateFormat sdf = new SimpleDateFormat(
            "yyyy/MM/dd", Locale.getDefault());

    float viewWidth;
    float viewHeight;

    private final Rect src = new Rect();

    private final Rect dst = new Rect();

    private WaterMark waterMark;

    private Date startDate = null;

    private Canvas canvas;

    private Paint paint;

    private ActivityDTO dto;

    private List<WaterMarkImage> waterMarkImageList = new ArrayList<>();//图片列表

    public DynamicStickerView(Context context) {
        this(context, null);
    }

    public DynamicStickerView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public DynamicStickerView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void setActivityDto(ActivityDTO dto) {
        super.setActivityDto(dto);
        if (null == dto) {
            this.startDate = new Date();
        } else {
            this.startDate = new Date(dto.getStartTime());
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        this.canvas = canvas;
        this.paint = getPaint();

        viewWidth = this.getWidth();
        viewHeight = this.getHeight();

        if (waterMarkImageList.size() > 0) {
            for (int i = 0; i < waterMarkImageList.size(); i++) {
                Bitmap sticker = getSticker(i);
                if (sticker == null)
                    continue;
                drawStickerImage(sticker, waterMarkImageList.get(i));
            }
        }
        dto = this.getActivityDTO();
        this.waterMark = getWaterMark();
        if (this.waterMark != null) {
            List<WaterMarkText> waterMarkTextList = this.waterMark.getWaterMarkTexts();
            if (waterMarkTextList != null && waterMarkTextList.size() > 0) {
                for (int i = 0; i < waterMarkTextList.size(); i++) {
                    drawStickerText(waterMarkTextList.get(i));
                }
            }

            List<WaterMarkSepLine> waterMarkSepLineList = this.waterMark.getWaterMarkSepLines();
            if (waterMarkSepLineList != null && waterMarkSepLineList.size() > 0) {
                for (int i = 0; i < waterMarkSepLineList.size(); i++) {
                    drawLine(waterMarkSepLineList.get(i));
                }
            }
        }
    }

    private void drawLine(WaterMarkSepLine waterMarkSepLine) {
        float widhtScale = viewWidth / this.waterMark.getCanvasWidth();
        float heightScale = viewHeight / this.waterMark.getCanvasHeight();
        paint.setColor(isReverseMode() ? Color.BLACK : Color.WHITE);
        paint.setStrokeWidth(3);
        switch (waterMarkSepLine.getPosition()) {
            case 1:
                canvas.drawLine(waterMarkSepLine.getLeft() * widhtScale, waterMarkSepLine.getTop() * heightScale, (waterMarkSepLine.getLeft() + waterMarkSepLine.getWidth()) * widhtScale, waterMarkSepLine.getTop() * heightScale, paint);
                break;
            case 2:

                break;
            case 3:
                canvas.drawLine(this.getWidth() - waterMarkSepLine.getRight() * widhtScale - waterMarkSepLine.getWidth() * widhtScale, waterMarkSepLine.getTop() * heightScale, this.getWidth() - waterMarkSepLine.getRight() * widhtScale, waterMarkSepLine.getTop() * heightScale, paint);
                break;
            case 4:

                break;
            case 5:

                break;
            case 6:

                break;
            case 7:
                canvas.drawLine(waterMarkSepLine.getLeft() * widhtScale, this.getHeight() - waterMarkSepLine.getBottom() * heightScale, (waterMarkSepLine.getLeft() + waterMarkSepLine.getWidth()) * widhtScale, this.getHeight() - waterMarkSepLine.getBottom() * heightScale, paint);
                break;
            case 8:

                break;
            case 9:
                canvas.drawLine(this.getWidth() - waterMarkSepLine.getRight() * widhtScale - waterMarkSepLine.getWidth() * widhtScale, this.getHeight() - waterMarkSepLine.getBottom() * heightScale, this.getWidth() - waterMarkSepLine.getRight() * widhtScale, this.getHeight() - waterMarkSepLine.getBottom() * heightScale, paint);

                break;
        }
    }

    //画图片
    private void drawStickerImage(Bitmap sticker, WaterMarkImage waterMarkImage) {
        final float sw = sticker.getWidth();
        final float sh = sticker.getHeight();

        this.src.left = 0;
        this.src.top = 0;
        this.src.right = (int) sw;
        this.src.bottom = (int) sh;

        Rect rect = getImageRect(sticker, waterMarkImage);
        canvas.drawBitmap(sticker, this.src, rect, paint);

    }

    public void drawStickerText(WaterMarkText waterMarkText) {
        float widhtScale = viewWidth / this.waterMark.getCanvasWidth();
        float heightScale = viewHeight / this.waterMark.getCanvasHeight();
        paint.setColor(isReverseMode() ? Color.BLACK : Color.WHITE);
        paint.setTextSize(waterMarkText.getFontSize() * widhtScale);
        if (waterMarkText.getFontName().equals(WaterMarkText.TEXTFONTBOLD)) {
            paint.setTypeface(Typeface.DEFAULT_BOLD);
        } else {
            paint.setTypeface(Typeface.DEFAULT);
        }
        int descent = getTextXY();//处理baseline
        int x = 0;
        int y = 0;
        //计算base与x坐标
        String string = getTextString(waterMarkText);
        if (!TextUtils.isEmpty(waterMarkText.getTitle()) && !LocaleManager.isDisplayKM(context)) {
            if (waterMarkText.getTitle().equals("DISTANCE(KM)")) {
                waterMarkText.setTitle("DISTANCE(mi)");
            } else if (waterMarkText.getTitle().equals("SPEED(KM/H)")) {
                waterMarkText.setTitle("SPEED(MPH)");
            } else if (waterMarkText.getTitle().equals("里程(km)")) {
                waterMarkText.setTitle("里程(mi)");
            } else if (waterMarkText.getTitle().equals("速度(km/h)")) {
                waterMarkText.setTitle("速度(MPH)");
            }
        }
        switch (waterMarkText.getPosition()) {
            case 1:
                paint.setTextAlign(Paint.Align.LEFT);
                x = (int) (waterMarkText.getLeft() * widhtScale);
                y = (int) (heightScale * (waterMarkText.getHeight() + waterMarkText.getTop()));
                canvas.drawText(string, x, y - descent, paint);
                if (!TextUtils.isEmpty(waterMarkText.getTitle())) {
                    int x1 = getTextWidth(paint, string);
                    paint.setTextSize(20 * widhtScale);
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(waterMarkText.getTitle(), x + x1 / 2, y - descent + 10 + getTextHeight(), paint);
                }
                break;
            case 2:
                paint.setTextAlign(Paint.Align.CENTER);
                x = (this.getWidth() / 2);
                y = (int) (heightScale * (waterMarkText.getHeight() + waterMarkText.getTop()));
                canvas.drawText(string, x, y - descent, paint);
                if (!TextUtils.isEmpty(waterMarkText.getTitle())) {
                    paint.setTextSize(20 * widhtScale);
                    canvas.drawText(waterMarkText.getTitle(), x, y - descent + 10 + getTextHeight(), paint);
                }
                break;
            case 3:
                paint.setTextAlign(Paint.Align.RIGHT);
                x = (int) (this.getWidth() - waterMarkText.getRight() * widhtScale);
                y = (int) (heightScale * (waterMarkText.getHeight() + waterMarkText.getTop()));
                canvas.drawText(string, x, y - descent, paint);
                paint.setTextAlign(Paint.Align.CENTER);
                if (!TextUtils.isEmpty(waterMarkText.getTitle())) {
                    int x1 = getTextWidth(paint, string);
                    paint.setTextSize(20 * widhtScale);
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(waterMarkText.getTitle(), x - x1 / 2, y - descent + 10 + getTextHeight(), paint);
                }
                break;
            case 4:
                paint.setTextAlign(Paint.Align.LEFT);
                x = (int) (waterMarkText.getLeft() * widhtScale);
                y = (this.getHeight() / 2 + (paint.getFontMetricsInt().bottom - paint.getFontMetricsInt().top) / 2);
                canvas.drawText(string, x, y - descent, paint);
                break;
            case 5:
                paint.setTextAlign(Paint.Align.CENTER);
                x = (this.getWidth() / 2);
                y = (this.getHeight() / 2 + (paint.getFontMetricsInt().bottom - paint.getFontMetricsInt().top) / 2);
                canvas.drawText(string, x, y - descent, paint);
                break;
            case 6:
                paint.setTextAlign(Paint.Align.RIGHT);
                x = (int) (this.getWidth() - waterMarkText.getRight() * widhtScale);
                y = (this.getHeight() / 2 + (paint.getFontMetricsInt().bottom - paint.getFontMetricsInt().top) / 2);
                canvas.drawText(string, x, y - descent, paint);
                break;
            case 7:
                paint.setTextAlign(Paint.Align.LEFT);
                x = (int) (waterMarkText.getLeft() * widhtScale);
                y = (int) (this.getHeight() - heightScale * waterMarkText.getBottom());
                canvas.drawText(string, x, y - descent, paint);
                if (!TextUtils.isEmpty(waterMarkText.getTitle())) {
                    int x1 = getTextWidth(paint, string);
                    paint.setTextSize(20 * widhtScale);
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(waterMarkText.getTitle(), x + x1 / 2, y - descent + 10 + getTextHeight(), paint);
                }
                break;
            case 8:
                paint.setTextAlign(Paint.Align.CENTER);
                x = (this.getWidth() / 2);
                y = (int) (this.getHeight() - heightScale * waterMarkText.getBottom());
                canvas.drawText(string, x, y - descent, paint);
                if (!TextUtils.isEmpty(waterMarkText.getTitle())) {
                    paint.setTextSize(20 * widhtScale);
                    canvas.drawText(waterMarkText.getTitle(), x, y - descent + 10 + getTextHeight(), paint);
                }
                break;
            case 9:
                paint.setTextAlign(Paint.Align.RIGHT);
                x = (int) (this.getWidth() - waterMarkText.getRight() * widhtScale);
                y = (int) (this.getHeight() - heightScale * waterMarkText.getBottom());
                canvas.drawText(string, x, y - descent, paint);
                if (!TextUtils.isEmpty(waterMarkText.getTitle())) {
                    int x1 = getTextWidth(paint, string);
                    paint.setTextSize(20 * widhtScale);
                    paint.setTextAlign(Paint.Align.CENTER);
                    canvas.drawText(waterMarkText.getTitle(), x - x1 / 2, y - descent + 10 + getTextHeight(), paint);
                }
                break;
        }
    }

    private String getTextString(WaterMarkText waterMarkText) {//1里程 2时间 3时间 4日期 5地点 6昵称
        int type = waterMarkText.getType();
        String string = "";
        if (dto == null)
            return string;
        switch (type) {
            case 1:
                if (LocaleManager.isDisplayKM(context)) {
                    string = String.format(Locale.getDefault(), "%.2f",
                            dto.getTotalDistance() / 1000) + waterMarkText.getUnit();
                } else {
                    if (waterMarkText.getUnit().equals("KM") || waterMarkText.getUnit().equals("km")) {
                        string = String.format(Locale.getDefault(), "%.2f",
                                LocaleManager.kilometreToMile(dto.getTotalDistance() / 1000)) + "mi";
                    } else {
                        string = String.format(Locale.getDefault(), "%.2f",
                                LocaleManager.kilometreToMile(dto.getTotalDistance() / 1000)) + waterMarkText.getUnit();
                    }
                }
                break;
            case 2:
                final long hour, m, s;
                final long et = (long) dto.getElapsedTime();
                if (et > 0) {
                    hour = et / 3600;
                    m = et % 3600 / 60;
                    s = et % 3600 % 60;
                } else {
                    hour = 0;
                    m = 0;
                    s = 0;
                }
                string = String.format(Locale.getDefault(),
                        "%02d:%02d:%02d", hour, m, s);
                break;
            case 3:
                if (LocaleManager.isDisplayKM(context)) {
                    string = String.format(Locale.getDefault(), "%.2f",
                            dto.getTotalDistance() / dto.getElapsedTime() * 3.6) + waterMarkText.getUnit();
                } else {
                    if (waterMarkText.getUnit().equals("KM") || waterMarkText.getUnit().equals("km")) {
                        string = String.format(Locale.getDefault(), "%.2f",
                                LocaleManager.kilometreToMile(dto.getTotalDistance() / dto.getElapsedTime() * 3.6)) + "mi";
                    } else {
                        string = String.format(Locale.getDefault(), "%.2f",
                                LocaleManager.kilometreToMile(dto.getTotalDistance() / dto.getElapsedTime() * 3.6)) + waterMarkText.getUnit();
                    }
                }
                break;
            case 4:
                string = sdf.format(this.startDate);
                break;
            case 5:
                string = dto.getCityName();
                if (TextUtils.isEmpty(string)) {
                    string = "";
                }
                break;
            case 6:
                string = dto.getNickname();
                break;
        }
        return string;
    }

    private int getTextXY() {
// FontMetrics对象
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int descent = fontMetrics.descent;
        return descent;
    }

    private int getTextHeight() {
        Paint.FontMetricsInt fontMetrics = paint.getFontMetricsInt();
        int descent = fontMetrics.descent;
        int ascent = fontMetrics.ascent;
        return descent - ascent;
    }

    private Rect getImageRect(Bitmap sticker, WaterMarkImage waterMarkImage) {
        final float viewWidth = this.getWidth();
        final float viewHeight = this.getHeight();
        int width = waterMarkImage.getWidth();
        int height = waterMarkImage.getHeight();

        float widhtScale = viewWidth / waterMarkImage.getCanvasWidth();
        float heightScale = viewHeight / waterMarkImage.getCanvasHeight();

        Rect imageDst = new Rect();
        switch (waterMarkImage.getPosition()) {
            case 1:
                imageDst.left = (int) (waterMarkImage.getLeft() * widhtScale);
                imageDst.right = imageDst.left + (int) (width * widhtScale);
                imageDst.top = (int) (waterMarkImage.getTop() * heightScale);
                imageDst.bottom = imageDst.top + (int) (waterMarkImage.getHeight() * widhtScale);
                break;
            case 2:
                imageDst.left = (int) (this.getWidth() - waterMarkImage.getWidth() * widhtScale) / 2;
                imageDst.right = (int) (this.getWidth() + waterMarkImage.getWidth() * widhtScale) / 2;
                imageDst.top = (int) (waterMarkImage.getTop() * heightScale);
                imageDst.bottom = imageDst.top + (int) (waterMarkImage.getHeight() * widhtScale);
                break;
            case 3:
                imageDst.right = (int) (this.getWidth() - waterMarkImage.getRight() * widhtScale);
                imageDst.left = imageDst.right - (int) (waterMarkImage.getWidth() * widhtScale);
                imageDst.top = (int) (waterMarkImage.getTop() * heightScale);
                imageDst.bottom = imageDst.top + (int) (waterMarkImage.getHeight() * widhtScale);
                break;
            case 4:
                imageDst.left = (int) (waterMarkImage.getLeft() * widhtScale);
                imageDst.right = imageDst.left + (int) (width * widhtScale);
                imageDst.top = (int) (height * heightScale);
                imageDst.bottom = imageDst.top + (int) (waterMarkImage.getHeight() * widhtScale);
                break;
            case 5:
                imageDst.left = (int) (this.getWidth() - waterMarkImage.getWidth() * widhtScale) / 2;
                imageDst.right = (int) (this.getWidth() + waterMarkImage.getWidth() * widhtScale) / 2;
                imageDst.top = (int) (this.getHeight() - waterMarkImage.getHeight() * heightScale) / 2;
                imageDst.bottom = (int) (this.getHeight() + waterMarkImage.getHeight() * heightScale) / 2;
                break;
            case 6:
                imageDst.right = (int) (this.getWidth() - waterMarkImage.getRight() * widhtScale);
                imageDst.left = imageDst.right - (int) (waterMarkImage.getWidth() * widhtScale);
                imageDst.top = (int) (this.getHeight() - waterMarkImage.getHeight() * heightScale) / 2;
                imageDst.bottom = (int) (this.getHeight() + waterMarkImage.getHeight() * heightScale) / 2;
                break;
            case 7:
                imageDst.left = (int) (waterMarkImage.getLeft() * widhtScale);
                imageDst.bottom = (int) (this.getHeight() - waterMarkImage.getBottom() * heightScale);
                imageDst.top = imageDst.bottom - (int) (height * heightScale);
                imageDst.right = imageDst.left + (int) (width * widhtScale);
                break;
            case 8:
                imageDst.left = (int) (this.getWidth() - waterMarkImage.getWidth() * widhtScale) / 2;
                imageDst.right = (int) (this.getWidth() + waterMarkImage.getWidth() * widhtScale) / 2;
                imageDst.bottom = (int) (this.getHeight() - waterMarkImage.getBottom() * heightScale);
                imageDst.top = imageDst.bottom - (int) (height * heightScale);
                break;
            case 9:
                imageDst.right = (int) (this.getWidth() - waterMarkImage.getRight() * widhtScale);
                imageDst.left = imageDst.right - (int) (waterMarkImage.getWidth() * widhtScale);
                imageDst.bottom = (int) (this.getHeight() - waterMarkImage.getBottom() * heightScale);
                imageDst.top = imageDst.bottom - (int) (height * heightScale);
                break;
        }

        return imageDst;
    }

    private Bitmap getSticker(int index) {
        if (isReverseMode()) {
            return waterMarkImageList.get(index).getBlackBitmap();
        } else {
            return waterMarkImageList.get(index).getWhiteBitmap();
        }
    }


    public void addImages(WaterMarkImage waterMarkImage) {
        waterMarkImageList.add(waterMarkImage);
        this.invalidate();
    }

    private int getTextWidth(Paint paint, String str) {
        int iRet = 0;
        if (str != null && str.length() > 0) {
            int len = str.length();
            float[] widths = new float[len];
            paint.getTextWidths(str, widths);
            for (int j = 0; j < len; j++) {
                iRet += (int) Math.ceil(widths[j]);
            }
        }
        return iRet;
    }

}
