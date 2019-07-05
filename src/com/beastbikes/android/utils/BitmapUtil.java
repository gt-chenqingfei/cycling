package com.beastbikes.android.utils;

import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.BitmapFactory.Options;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Rect;
import android.graphics.Shader;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.ScrollView;

import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.R;
import com.beastbikes.android.persistence.BeastStore.Caches;
import com.beastbikes.framework.android.cache.CacheManager;
import com.beastbikes.framework.android.utils.DimensionUtils;
import com.beastbikes.framework.ui.android.utils.Toasts;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class BitmapUtil {

    private static final String TAG = "BitmapUtil";

    private static final Logger logger = LoggerFactory
            .getLogger(BitmapUtil.class);

    public static final String SAVE_PATH = Environment
            .getExternalStorageDirectory().getAbsolutePath()
            + File.separator
            + "DCIM" + File.separator + "Camera" + File.separator;

    /**
     * 压缩图片
     *
     * @param image
     * @return
     */
    public static Bitmap compressImage(Bitmap image) {
        if (null == image)
            return null;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        // 质量压缩方法，这里100表示不压缩，把压缩后的数据存放到baos中
        image.compress(Bitmap.CompressFormat.JPEG, 90, baos);
        int options = 100;
        // 循环判断如果压缩后图片是否大于100kb,大于继续压缩
        logger.info("Save image compress start");
        int index = 0;
        while (baos.toByteArray().length > 220 * 1024) {
            if (options <= 10)
                continue;
            // 重置baos
            baos.reset();
            // 每次都减少10
            options -= 10;
            // 这里压缩options%，把压缩后的数据存放到baos中
            image.compress(Bitmap.CompressFormat.JPEG, options, baos);
            index = index + 1;
            logger.info("Save image compress baos size = "
                    + baos.toByteArray().length + "index" + index);
        }

        if (!image.isRecycled())
            image.recycle();

        // 把压缩后的数据baos存放到ByteArrayInputStream中
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());
        // 把ByteArrayInputStream数据生成图片
        Bitmap bitmap = BitmapFactory.decodeStream(isBm, null, null);
        logger.info("Save image compress success");
        return bitmap;
    }

    /**
     * 保存Map Image
     *
     * @param bitmap
     */
    public static void saveMapImage(Context context, Bitmap bitmap,
                                    String fileName) {
        final CacheManager cm = CacheManager.getInstance();
        final File activity = cm.lookup(context, Caches.ACTIVITIES, fileName);

        FileOutputStream fos = null;

        try {
            if (activity.exists()) {
                activity.createNewFile();
            }
            fos = new FileOutputStream(activity);
            bitmap.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            cm.putBitmap(activity.getAbsolutePath(), bitmap);
        } catch (IOException e) {
            logger.info("Cache avatar error");
            return;
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
                fos = null;
            }
        }

    }

    /**
     * 保存Map Image
     *
     * @param bitmap
     */
    public static String saveMapImageToPath(Context context, Bitmap bitmap,
                                            String fileName) {
        final CacheManager cm = CacheManager.getInstance();
        final File activity = cm.lookup(context, Caches.ACTIVITIES, fileName);

        FileOutputStream fos = null;

        try {
            if (activity.exists()) {
                activity.createNewFile();
            }
            fos = new FileOutputStream(activity);
            bitmap.compress(CompressFormat.JPEG, 100, fos);
            fos.flush();
            cm.putBitmap(activity.getAbsolutePath(), bitmap);
            return activity.getAbsolutePath();
        } catch (IOException e) {
            Log.e(TAG, "Cache avatar error", e);
            return null;
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                } catch (IOException e) {
                }
                fos = null;
            }

            if (null != bitmap && !bitmap.isRecycled()) {
                bitmap.recycle();
                System.gc();
            }
        }

    }

    /**
     * 保存到sdcard
     *
     * @param bmp
     * @return
     */
    public static String saveImage(Bitmap bmp) {
        logger.info("Save to sacard by iamge compress");
        bmp = compressImage(bmp);

        if (null == bmp)
            return null;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss",
                Locale.US);

        final File file = new File(SAVE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }

        File outfile = new File(SAVE_PATH, "Beast-" + sdf.format(new Date())
                + ".jpg");
        // 如果文件不存在，则创建一个新文件
        if (!outfile.exists()) {
            try {
                outfile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outfile);
            if (null != fos) {
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
            }
            logger.info("Save to sdcard by iamge success");
            return outfile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                    fos = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

//			if (null != b && !b.isRecycled()) {
//				b.recycle();
//			}

            if (null != bmp && !bmp.isRecycled()) {
                bmp.recycle();
                bmp = null;
            }

            System.gc();
        }
        return null;
    }

    /**
     * 保存到sdcard
     *
     * @param bmp
     * @return
     */
    public static String saveImage(Bitmap bmp, String fileName) {
        logger.info("Save to sacard by iamge compress");
        bmp = compressImage(bmp);

        if (null == bmp)
            return "";

        final File file = new File(SAVE_PATH);
        if (!file.exists()) {
            file.mkdirs();
        }

        final File outfile = new File(SAVE_PATH, "Beast-" + fileName + ".jpg");
        // 如果文件不存在，则创建一个新文件
        if (!outfile.exists()) {
            try {
                outfile.createNewFile();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(outfile);
            if (null != fos) {
                bmp.compress(Bitmap.CompressFormat.JPEG, 100, fos);
                fos.flush();
            }
            logger.info("Save to sdcard by iamge success");
            return outfile.getAbsolutePath();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (null != fos) {
                try {
                    fos.close();
                    fos = null;
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

//			if (null != b && !b.isRecycled()) {
//				b.recycle();
//				b = null;
//			}

            if (null != bmp && !bmp.isRecycled()) {
                bmp.recycle();
                bmp = null;
            }

            System.gc();
        }
        return "";
    }

    private static Bitmap createBitmap(Context context, Bitmap scroolBmp,
                                      Bitmap mapBmp) {
        if (scroolBmp == null) {
            return null;
        }

        int scroolW = scroolBmp.getWidth();
        int scroolH = scroolBmp.getHeight();

        Bitmap logo = BitmapFactory.decodeResource(context.getResources(),
                R.drawable.ic_actionbar_logo);

        Bitmap logoBmp = Bitmap.createBitmap(120, 120, Config.ARGB_4444);
        Bitmap bottomBmp = Bitmap.createBitmap(scroolW, 200, Config.ARGB_4444);
        Canvas canvas = new Canvas(bottomBmp);
        canvas.drawColor(context.getResources().getColor(R.color.activity_finished_activity_bg));
        Paint paint = new Paint();
        paint.setColor(context.getResources().getColor(
                R.color.activity_finished_activity_bg));

        canvas.drawRect(new Rect(0, 0, scroolW, 200), paint);

        Canvas cl = new Canvas(logoBmp);
        cl.drawBitmap(logo, null,
                new Rect(0, 0, logoBmp.getWidth(), logoBmp.getHeight()), null);

        canvas.drawBitmap(logoBmp, 40, 40, null);
        Paint textP = new Paint();
        textP.setColor(Color.WHITE);
        textP.setAntiAlias(true);
        textP.setTextSize(60);
        canvas.drawText(context.getString(R.string.app_name), 200, 120, textP);

        // create the new blank bitmap
        Bitmap shareBmp = Bitmap.createBitmap(scroolW, scroolH,
                Config.ARGB_4444);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(shareBmp);
        // draw src into
        cv.drawBitmap(scroolBmp, 0, 0, null);// 在 0，0坐标开始画入src
        // draw watermark into

        if (null != mapBmp) {
            Bitmap bmp = compressImage(mapBmp);
            cv.drawBitmap(bmp, 0, 200, null);// 在src的右下角画入水印
        }
        cv.drawBitmap(bottomBmp, 0, scroolH - 200, null);
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        // store
        cv.restore();// 存储
        return shareBmp;
    }

    private static Bitmap createShareBitmap(Context context, Bitmap scroolBmp,
                                           String path, int width) {
        float marginTop = 200;
        float size = 60;
        int logoSize = 120;
        float logoY = 40;
        float textX = 200;
        if (width <= 800) {
            logoSize = 80;
            logoY = (200 - logoSize) / 2;
            textX = logoSize + logoY;
            marginTop = 140;
            size = 40;
        }

        if (scroolBmp == null) {
            return null;
        }

        int scroolW = scroolBmp.getWidth();
        int scroolH = scroolBmp.getHeight();

        Bitmap logo = BitmapFactory.decodeResource(context.getResources()
                ,R.drawable.ic_actionbar_logo);

        Bitmap logoBmp = Bitmap.createBitmap(logoSize, logoSize,
                Config.RGB_565);
        Bitmap bottomBmp = Bitmap.createBitmap(scroolW, 200, Config.RGB_565);
        Canvas canvas = new Canvas(bottomBmp);
        canvas.drawColor(context.getResources().getColor(R.color.activity_finished_activity_bg));
        Paint paint = new Paint();
        paint.setColor(context.getResources().getColor(
                R.color.activity_finished_activity_bg));

        canvas.drawRect(new Rect(0, 0, scroolW, 200), paint);

        Canvas cl = new Canvas(logoBmp);
        cl.drawBitmap(logo, null,
                new Rect(0, 0, logoBmp.getWidth(), logoBmp.getHeight()), null);

        canvas.drawBitmap(logoBmp, 40, logoY, null);
        Paint textP = new Paint();
        textP.setColor(Color.WHITE);
        textP.setAntiAlias(true);
        textP.setTextSize(size);
        canvas.drawText(context.getString(R.string.app_name), textX, 120, textP);

        // create the new blank bitmap
        Bitmap shareBmp = Bitmap.createBitmap(scroolW, scroolH,
                Config.RGB_565);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(shareBmp);
        // draw src into
        cv.drawBitmap(scroolBmp, 0, 0, null);// 在 0，0坐标开始画入src
        // draw watermark into

        if (!TextUtils.isEmpty(path)) {
            BitmapFactory.Options bfOptions = new BitmapFactory.Options();
            bfOptions.inTempStorage = new byte[12 * 1024];
            Bitmap mapBmp = BitmapFactory.decodeFile(path, bfOptions);

            if (null != mapBmp) {
                cv.drawBitmap(compressImage(mapBmp), 0, marginTop, null);// 在src的右下角画入水印
                mapBmp.recycle();
                System.gc();
            }
        }

        cv.drawBitmap(bottomBmp, 0, scroolH - 200, null);
        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        // store
        cv.restore();// 存储
        return shareBmp;
    }

    /**
     * 截取scrollview的屏幕
     *
     * @param scrollView
     * @return
     */
    public static Bitmap getBitmapByView(ScrollView scrollView) {
        int h = 0;
        Bitmap bitmap = null;
        // 获取scrollview实际高度
        for (int i = 0; i < scrollView.getChildCount(); i++) {
            h += scrollView.getChildAt(i).getHeight();
        }
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(scrollView.getWidth(), h,
                Bitmap.Config.RGB_565);
        final Canvas canvas = new Canvas(bitmap);
        scrollView.draw(canvas);
        return bitmap;
    }

    public static Bitmap getBitmapByView(View view) {
        int h = view.getHeight();
        int w = view.getWidth();
        if (h == 0)
            h = 400;

        if (w == 0)
            w = 400;

        Bitmap bitmap = null;
        // 获取scrollview实际高度
        // 创建对应大小的bitmap
        bitmap = Bitmap.createBitmap(w, h, Config.ARGB_4444);
        final Canvas canvas = new Canvas(bitmap);
        canvas.drawColor(Color.TRANSPARENT);
        view.draw(canvas);
        return bitmap;
    }

    //
    public static Bitmap convertViewToBitmap(View view) {
        view.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED), View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));
        view.layout(0, 0, view.getMeasuredWidth(), view.getMeasuredHeight());
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();

        return bitmap;
    }

    public static Bitmap getViewBitmap(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (cacheBitmap == null) {
            return null;
        }

        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    public static Bitmap createShareBitmap(Context context, Bitmap mapBmp,
                                           Bitmap dataBmp, Bitmap imageBmp, String fileName, long startTime) {
        DisplayMetrics dm = new DisplayMetrics();
        WindowManager vm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        vm.getDefaultDisplay().getMetrics(dm);
        int windowWidth = dm.widthPixels;
        int marginLeft = 250 * windowWidth / 1080;

        int margin = DimensionUtils.dip2px(context, 17);
        int textMargin = DimensionUtils.dip2px(context, 15);
        int timeSize = DimensionUtils.dip2px(context, 16);
        int dateSize = DimensionUtils.dip2px(context, 12);

        Resources resources = context.getResources();
        Bitmap topBmp = BitmapFactory.decodeResource(resources,
                R.drawable.ic_activity_complete_share_top);
        Bitmap bottomBmp = BitmapFactory.decodeResource(context.getResources()
                ,R.drawable.ic_activity_complete_share_bottom);

        Bitmap logoBmp = Bitmap.createBitmap(windowWidth, margin * 2 + topBmp.getHeight(), Config.RGB_565);
        Canvas canvasTop = new Canvas(logoBmp);
        canvasTop.drawColor(Color.parseColor("#333436"));
        Paint textP = new Paint();
        textP.setColor(Color.WHITE);
        textP.setAntiAlias(true);
        textP.setTextSize(timeSize);

        canvasTop.drawBitmap(topBmp, margin, margin, null);
        if (null != topBmp && !topBmp.isRecycled()) {
            topBmp.recycle();
            topBmp = null;
        }

        SimpleDateFormat sdf = new SimpleDateFormat("HH:mm:ss");
        Date startDate = new Date(startTime);
        String time = sdf.format(startDate);
        canvasTop.drawText(time, windowWidth - marginLeft, textMargin * 2, textP);

        sdf = new SimpleDateFormat("yyyy-MM-dd");
        String date = sdf.format(startDate);
        textP.setColor(Color.parseColor("#6c6c6c"));
        textP.setTextSize(dateSize);
        canvasTop.drawText(date, windowWidth - marginLeft, textMargin * 2 + margin, textP);
        canvasTop.save(Canvas.ALL_SAVE_FLAG);
        canvasTop.restore();

        int topHeight = logoBmp.getHeight();
        int dataHeight = dataBmp.getHeight();
        int imageHeight = imageBmp.getHeight();

        Bitmap shareBmp = Bitmap.createBitmap(windowWidth, topHeight
                + dataHeight + windowWidth + bottomBmp.getHeight() + margin * 2, Config.RGB_565);

        Bitmap stencilBmp = Bitmap.createBitmap(windowWidth, topHeight
                + windowWidth + imageHeight, Config.RGB_565);
        Canvas stencilCanvas = new Canvas(stencilBmp);
        Canvas canvas = new Canvas(shareBmp);
        canvas.drawColor(Color.parseColor("#333436"));
        canvas.drawBitmap(logoBmp, 0, 0, null);
        stencilCanvas.drawBitmap(logoBmp, 0, 0, null);
        if (null != logoBmp && !logoBmp.isRecycled()) {
            logoBmp.recycle();
            logoBmp = null;
        }

        canvas.drawBitmap(mapBmp, 0, topHeight, null);
        stencilCanvas.drawBitmap(mapBmp, 0, topHeight, null);
        if (null != mapBmp && !mapBmp.isRecycled()) {
            mapBmp.recycle();
            mapBmp = null;
        }

        stencilCanvas.drawBitmap(imageBmp, 0, topHeight + windowWidth, null);
        if (null != imageBmp && !imageBmp.isRecycled()) {
            imageBmp.recycle();
            imageBmp = null;
        }

        canvas.drawBitmap(dataBmp, 0, topHeight + windowWidth, null);
        if (null != dataBmp && !dataBmp.isRecycled()) {
            dataBmp.recycle();
            dataBmp = null;
        }

        canvas.drawBitmap(bottomBmp, (windowWidth - bottomBmp.getWidth()) / 2,
                topHeight + windowWidth + dataHeight + margin, null);
        if (null != bottomBmp && !bottomBmp.isRecycled()) {
            bottomBmp.recycle();
            bottomBmp = null;
        }

        stencilCanvas.save(Canvas.ALL_SAVE_FLAG);
        stencilCanvas.restore();
        saveImage(stencilBmp, fileName);

        // save all clip
        canvas.save(Canvas.ALL_SAVE_FLAG);// 保存
        // store
        canvas.restore();// 存储

        return shareBmp;
    }

    public static Bitmap createShareBitmap(Context context, Bitmap scroolBmp,
                                           Bitmap chatBmp, String path, String title, int totalHeight) {
        if (scroolBmp == null) {
            return null;
        }

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager vm = (WindowManager) context
                .getSystemService(Context.WINDOW_SERVICE);
        vm.getDefaultDisplay().getMetrics(dm);
        int windowWidth = dm.widthPixels;
        int windowHeight = dm.heightPixels;

        int scroolW = scroolBmp.getWidth();
        int scroolH = scroolBmp.getHeight() - (200 * windowHeight / 1920);

        int topHeight = 160 * windowHeight / 1920;
        int marginTop = 20 * windowHeight / 1920;
        int logoX = 40 * windowHeight / 1920;
        int textMarginTop = 100 * windowHeight / 1920;

        int size = 50 * windowWidth / 1080;
        int logoSize = 100 * windowWidth / 1080;
        float logoY = 40 * windowWidth / 1080;
        float textX = 180 * windowWidth / 1080;

        Bitmap logo = BitmapFactory.decodeResource(context.getResources(),R.drawable.ic_actionbar_logo);

        Bitmap logoBmp = Bitmap.createBitmap(logoSize, logoSize,
                Config.RGB_565);
        Bitmap topBmp = Bitmap.createBitmap(scroolW, topHeight,
                Config.RGB_565);
        Canvas canvas = new Canvas(topBmp);
        canvas.drawColor(context.getResources().getColor(R.color.activity_finished_activity_bg));
        Paint paint = new Paint();
        paint.setColor(context.getResources().getColor(
                R.color.activity_finished_activity_bg));

        canvas.drawRect(new Rect(0, 0, scroolW, topHeight), paint);

        Canvas cl = new Canvas(logoBmp);
        cl.drawBitmap(logo, null,
                new Rect(0, 0, logoBmp.getWidth(), logoBmp.getHeight()), null);

        canvas.drawBitmap(logoBmp, logoX, (topHeight - logoSize) / 2, null);
        Paint textP = new Paint();
        textP.setColor(Color.WHITE);
        textP.setAntiAlias(true);
        textP.setTextSize(size);
        canvas.drawText(context.getString(R.string.app_name) + "—" + title,
                textX, textMarginTop, textP);

        logo.recycle();
        logoBmp.recycle();
        System.gc();

        // create the new blank bitmap
        Bitmap shareBmp = Bitmap.createBitmap(scroolW, scroolH,
                Config.RGB_565);// 创建一个新的和SRC长度宽度一样的位图
        Canvas cv = new Canvas(shareBmp);
        cv.drawRect(new Rect(0, 0, scroolW, scroolH), paint);
        // draw src into
        cv.drawBitmap(scroolBmp, 0, 0, null);// 在 0，0坐标开始画入src

        scroolBmp.recycle();
        System.gc();

        cv.drawBitmap(topBmp, 0, marginTop, null);

        topBmp.recycle();
        System.gc();

        // draw watermark into
        int chatHeight = (totalHeight - chatBmp.getHeight()) * windowHeight / 3
                / 1920;
        cv.drawBitmap(chatBmp, logoX, scroolH - totalHeight + chatHeight, null);

        chatBmp.recycle();
        System.gc();

        if (!TextUtils.isEmpty(path)) {
            BitmapFactory.Options bfOptions = new BitmapFactory.Options();
            bfOptions.inTempStorage = new byte[12 * 1024];
            Bitmap mapBmp = BitmapFactory.decodeFile(path, bfOptions);

            int margenLeft = (windowWidth - mapBmp.getWidth()) / 2
                    * windowWidth / 1080;

            if (null != mapBmp) {
                cv.drawBitmap(compressImage(mapBmp), margenLeft, topHeight
                        + logoY + marginTop, null);// 在src的右下角画入水印
                mapBmp.recycle();
                System.gc();
            }
        }

        // save all clip
        cv.save(Canvas.ALL_SAVE_FLAG);// 保存
        // store
        cv.restore();// 存储
        return shareBmp;
    }

    /**
     * 获取缩略图
     *
     * @param context
     * @param path
     * @return
     */
    public static Bitmap getImageThumbnail(Context context, String path) {
        ContentResolver cr = context.getContentResolver();
        Cursor cursor = null;
        String[] projection = {MediaStore.Images.Media.DATA,
                MediaStore.Images.Media._ID,};
        String whereClause = MediaStore.Images.Media.DATA + "=?";
        try {
            cursor = cr.query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                    projection, whereClause, new String[]{path}, null);
            if (cursor != null && cursor.moveToFirst()) {
                int _id = cursor.getColumnIndex(MediaStore.Images.Media._ID);
                int id = cursor.getInt(_id);
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inDither = false;
                options.inPreferredConfig = Bitmap.Config.RGB_565;
                return MediaStore.Images.Thumbnails.getThumbnail(cr, id,
                        MediaStore.Images.Thumbnails.MINI_KIND, options);

            }
        } finally {
            if (cursor != null && !cursor.isClosed()) {
                cursor.close();
            }
        }
        return null;
    }

    public static List<Integer> getColors(int width) {
        Bitmap bitmap = Bitmap.createBitmap(width + 1, 3, Config.RGB_565);
        Canvas canvas = new Canvas(bitmap);
        Paint paint = new Paint();
        paint.setAntiAlias(true);
        int[] SECTION_COLORS = {0xffff5d00, 0xffd62424};
        LinearGradient gradient = new LinearGradient(0, 0, width + 1, 3,
                SECTION_COLORS, null, Shader.TileMode.MIRROR);
        paint.setShader(gradient);
        canvas.drawRect(new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight()),
                paint);
        List<Integer> colors = new ArrayList<Integer>();
        for (int i = 0; i <= width; i++) {
            int color = bitmap.getPixel(i, 1);
            if (color == 0)
                continue;

            colors.add(color);
        }

        if (!bitmap.isRecycled()) {
            bitmap.recycle();
            bitmap = null;
        }

        return colors;
    }

    /**
     * 保存到本地，刷新到图库
     *
     * @param sharePath
     */
    public static void saveToSdCard(String sharePath) {
        Context context = BeastBikes.getInstance().getApplicationContext();
        if (TextUtils.isEmpty(sharePath)) {
            Toasts.show(context, R.string.activity_finished_share_sdcard_err);
        } else {
            Toasts.show(context,
                    R.string.activity_finished_share_sdcard_success);
            Uri url = Uri.parse(sharePath);
            context.sendBroadcast(new Intent(
                    android.hardware.Camera.ACTION_NEW_PICTURE, url));
            context.sendBroadcast(new Intent("com.android.camera.NEW_PICTURE",
                    url));
            context.sendBroadcast(new Intent(
                    Intent.ACTION_MEDIA_SCANNER_SCAN_FILE, Uri.parse("file://"
                    + sharePath)));
        }
    }

    public static Bitmap createBitmapByUrl(String url) {
        try {
            URL bitmapUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) bitmapUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream in = connection.getInputStream();
            Options opts = new Options();
            opts.inSampleSize = 2;
            return BitmapFactory.decodeStream(in, new Rect(), opts);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static String saveBitmapByUrl(String url, String fileName) {
        try {
            URL bitmapUrl = new URL(url);
            HttpURLConnection connection = (HttpURLConnection) bitmapUrl.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream in = connection.getInputStream();
            Options opts = new Options();
            opts.inSampleSize = 2;
            return saveImage(BitmapFactory.decodeStream(in, new Rect(), opts), fileName);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Uri getResourceUri(int resId, String packageName) {
        return Uri.parse("android.resource://" + packageName + "/" + resId);
    }

}
