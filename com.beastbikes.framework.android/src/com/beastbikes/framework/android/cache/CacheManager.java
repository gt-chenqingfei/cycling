package com.beastbikes.framework.android.cache;

import java.io.File;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.LruCache;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageCache;
import com.beastbikes.framework.android.schedule.RequestQueueManager;
import com.beastbikes.framework.android.utils.FileUtils;

/**
 * The cache manager
 *
 * @author johnson
 */
@SuppressWarnings(value = {"unchecked", "deprecation"})
public class CacheManager implements ImageCache {

    private static CacheManager instance = null;

    public static synchronized final CacheManager getInstance() {
        if (null == instance) {
            instance = new CacheManager();
        }

        return instance;
    }

    private final LruCache<String, Bitmap> images;
    private final LruCache<String, String> values;

    private CacheManager() {
        final int cacheSize = (int) (Runtime.getRuntime().maxMemory() / 8);
        this.images = new LruCache<String, Bitmap>(cacheSize) {

            @Override
            protected int sizeOf(String key, Bitmap bmp) {
                return bmp.getByteCount();
            }

        };
        this.values = new LruCache<String, String>(cacheSize) {

            @Override
            protected int sizeOf(String key, String value) {
                return value.getBytes().length;
            }

        };
    }

    public File lookup(Context ctx, String... segment) {
        File file = ctx.getExternalCacheDir();

        for (int i = 0; i < segment.length; i++) {
            file = new File(file, segment[i]);
        }

        return file;
    }

    public String getString(String key) {
        if (null == key)
            return null;
        return this.values.get(key);
    }

    public void putString(String key, String value) {
        if (null == key || null == value)
            return;
        this.values.put(key, value);
    }

    @Override
    public Bitmap getBitmap(String key) {
        if (null == key)
            return null;
        return this.images.get(key);
    }

    @Override
    public void putBitmap(String key, Bitmap bmp) {
        if (null == key || null == bmp)
            return;
        this.images.put(key, bmp);
    }

    /**
     * Returns an image loader
     *
     * @param rqm The request queue manager
     * @return an image loader
     */
    public ImageLoader getImageLoader(RequestQueueManager rqm) {
        return new ImageLoader(rqm.getRequestQueue(), this);
    }

    public Bitmap loadBitmapFromFile(String path) {
        return loadBitmapFromFile(path, 4);
    }

    public Bitmap loadBitmapFromFile(String path, int sampleSize) {
        final BitmapFactory.Options opts = new BitmapFactory.Options();
        opts.inJustDecodeBounds = false;
        opts.inPreferredConfig = Bitmap.Config.ARGB_8888;
        opts.inPurgeable = true;
        opts.inInputShareable = true;
        opts.inSampleSize = Math.max(2, sampleSize);
        final Bitmap bmp = BitmapFactory.decodeFile(path, opts);

        try {
            return bmp;
        } finally {
            this.putBitmap(path, bmp);
        }
    }

    /**
     * Clear all cache data
     *
     * @param context
     */
    public void clear(Context context) {
        this.clearExternalCache(context);
        this.clearInternalCache(context);
    }

    /**
     * Clear external cache data
     *
     * @param context
     */
    public void clearExternalCache(Context context) {
        final File root = context.getExternalCacheDir();
        final File[] files = root.listFiles();
        if (files == null || files.length == 0)
            return;
        for (int i = 0; i < files.length; i++) {
            FileUtils.delete(files[i], true);
        }
    }

    /**
     * Clear internal cache data
     *
     * @param context
     */
    public void clearInternalCache(Context context) {
        final File root = context.getCacheDir();
        final File[] files = root.listFiles();
        if (files == null || files.length == 0)
            return;
        for (int i = 0; i < files.length; i++) {
            FileUtils.delete(files[i], true);
        }
    }

}
