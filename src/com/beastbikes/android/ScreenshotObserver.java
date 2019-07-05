package com.beastbikes.android;

import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ScreenshotObserver extends ContentObserver {
    protected static final Logger logger = LoggerFactory.getLogger("ScreenshotObserver");
    private static final String EXTERNAL_CONTENT_URI_MATCHER =
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI.toString();
    private static final String[] PROJECTION = new String[]{
            MediaStore.Images.Media.DISPLAY_NAME, MediaStore.Images.Media.DATA,
            MediaStore.Images.Media.DATE_ADDED
    };
    private static final String SORT_ORDER = MediaStore.Images.Media.DATE_ADDED + " DESC";
    private static final long DEFAULT_DETECT_WINDOW_SECONDS = 10;

    private Context mContext;

    public interface OnScreenshotListener {
        void onScreenshot(String path);
    }

    private OnScreenshotListener listener;

    /**
     * Creates a content observer.
     *
     * @param handler The handler to run {@link #onChange} on, or null if none.
     */
    public ScreenshotObserver(@Nullable Handler handler, @NonNull Context context) {
        super(handler);
        this.mContext = context;
    }

    /**
     * Screen event subscription
     *
     * @param listener
     */
    public void subscript(@NonNull OnScreenshotListener listener) {
        this.listener = listener;
        mContext.getContentResolver().registerContentObserver(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI, true, this);
    }

    /**
     * Unsubscribe screen screenshot event
     */
    public void unSubscript() {
        mContext.getContentResolver().unregisterContentObserver(this);
    }

    @Override
    public void onChange(boolean selfChange, Uri uri) {
        logger.debug("onChange: " + selfChange + ", " + uri.toString());
        if (uri.toString().matches(EXTERNAL_CONTENT_URI_MATCHER)) {
            Cursor cursor = null;
            try {
                cursor = mContext.getContentResolver().query(uri, PROJECTION, null, null, SORT_ORDER);
                if (cursor != null && cursor.moveToFirst()) {
                    String path = cursor.getString(
                            cursor.getColumnIndex(MediaStore.Images.Media.DATA));
                    long dateAdded = cursor.getLong(cursor.getColumnIndex(
                            MediaStore.Images.Media.DATE_ADDED));
                    long currentTime = System.currentTimeMillis() / 1000;
                    logger.debug("path: " + path + ", dateAdded: " + dateAdded +
                            ", currentTime: " + currentTime);
                    if (matchPath(path) && matchTime(currentTime, dateAdded)) {
                        if (listener != null) {
                            listener.onScreenshot(path);
                        }
                    }
                }
            } catch (Exception e) {
                logger.debug("open cursor fail");
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
        super.onChange(selfChange, uri);
    }

    private boolean matchPath(String path) {
        return path.toLowerCase().contains("screenshots")
                || path.toLowerCase().contains("screenshot")
                || path.contains("截屏")
                || path.contains("截图");
    }

    private boolean matchTime(long currentTime, long dateAdded) {
        return Math.abs(currentTime - dateAdded) <= DEFAULT_DETECT_WINDOW_SECONDS;
    }
}