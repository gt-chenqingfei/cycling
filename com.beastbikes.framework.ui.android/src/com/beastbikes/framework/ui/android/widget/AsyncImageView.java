package com.beastbikes.framework.ui.android.widget;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.beastbikes.framework.android.cache.CacheManager;

import java.io.File;

/**
 * The asynchronously loaded image view
 *
 * @author johnson
 */
public class AsyncImageView extends NetworkImageView {

    private Bitmap local;

    public AsyncImageView(Context context) {
        this(context, null);
    }

    public AsyncImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public AsyncImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public void setImageBitmap(File file) {
        this.setImageBitmap(file.getAbsolutePath());
    }

    public void setImageBitmap(String path) {
        final CacheManager cm = CacheManager.getInstance();
        this.local = cm.getBitmap(path);
        if (null == this.local) {
            this.local = cm.loadBitmapFromFile(path);
        }

        this.requestLayout();
    }

    @Override
    public void setImageUrl(String url, ImageLoader imageLoader) {
        this.local = null;
        super.setImageUrl(url, imageLoader);
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right,
                            int bottom) {
        super.onLayout(changed, left, top, right, bottom);

        if (null != this.local) {
            super.setImageBitmap(this.local);
        }
    }

}
