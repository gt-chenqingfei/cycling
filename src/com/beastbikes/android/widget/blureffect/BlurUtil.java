package com.beastbikes.android.widget.blureffect;

import android.app.Activity;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;
import android.widget.ImageView;

import java.io.File;

/**
 * Created by chenqingfei on 16/1/27.
 */
public class BlurUtil {

    private static final String BLURRED_IMG_PATH = "blurred_image.png";

    static LruCache<ImageView, Bitmap> lruCache  = new LruCache<>(1);

    public static void blurOn(final Activity context, final ImageView targetImage, final int blurImageRes) {
        blurSwitch(context, targetImage, blurImageRes, true);
    }

    public static void blurOff(final ImageView targetImage, final int blurImageRes) {
        blurSwitch(null, targetImage, blurImageRes, false);
    }

    public static void blurBitmapFree(ImageView targetImage) {
        if (targetImage == null)
            return;

        targetImage.setImageBitmap(null);
        targetImage.setImageDrawable(null);
        Bitmap bm = lruCache.get(targetImage);
        if (bm != null && !bm.isRecycled()) {
            bm.recycle();
        }
        lruCache.remove(targetImage);
    }

    public static void blurSwitch(final Activity context, final ImageView targetImage, final int blurImageRes, boolean isBlur) {
        if (targetImage == null || blurImageRes <= 0)
            return;

        boolean hasBlur = false;
        if (targetImage.getTag() != null) {
            hasBlur = (boolean) targetImage.getTag();
        }

        if (isBlur) {

            if (context == null || hasBlur)
                return;

            final File blurredImage = new File(context.getFilesDir() + BLURRED_IMG_PATH);
            final int screenWidth = ImageUtils.getScreenWidth(context);
            if (!blurredImage.exists()) {

                new Thread(new Runnable() {

                    @Override
                    public void run() {

                        // No image found => let's generate it!
                        BitmapFactory.Options options = new BitmapFactory.Options();
                        options.inSampleSize = 2;
                        Bitmap image = BitmapFactory.decodeResource(context.getResources(),
                                blurImageRes, options);

                        final Bitmap newImg = Blur.fastblur(context, image, 25);
                        ImageUtils.storeImage(newImg, blurredImage);
                        context.runOnUiThread(new Runnable() {

                            @Override
                            public void run() {
                                lruCache.put(targetImage, newImg);
                                updateView(screenWidth, context, targetImage);
                            }
                        });

                    }
                }).start();
            } else {
                updateView(screenWidth, context, targetImage);
            }

        } else {
            if (hasBlur)
                targetImage.setImageResource(blurImageRes);
        }

        targetImage.setTag(isBlur);
    }

    private static void updateView(final int screenWidth, Activity context, ImageView target) {
        Bitmap bmpBlurred = lruCache.get(target);
        if (lruCache.get(target) == null) {
            bmpBlurred = BitmapFactory.decodeFile(context.getFilesDir() + BLURRED_IMG_PATH);
        }

        if (null == bmpBlurred) {
            return;
        }

        bmpBlurred = Bitmap.createScaledBitmap(bmpBlurred, screenWidth, (int) (bmpBlurred.getHeight()
                * ((float) screenWidth) / (float) bmpBlurred.getWidth()), false);

        target.setImageBitmap(bmpBlurred);
    }
}
