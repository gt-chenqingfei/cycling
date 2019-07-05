package com.beastbikes.framework.ui.android.utils;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.text.Html.ImageGetter;
import android.view.View;

import com.android.volley.RequestQueue;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.ImageLoader.ImageContainer;
import com.android.volley.toolbox.ImageLoader.ImageListener;
import com.beastbikes.framework.android.cache.CacheManager;
import com.beastbikes.framework.android.schedule.RequestQueueManager;

public class HtmlImageGetter implements ImageGetter, ImageListener {

    private final RequestQueueManager requestQueueManager;

    private final View view;

    public HtmlImageGetter(RequestQueueManager rqm, View v) {
        this.requestQueueManager = rqm;
        this.view = v;
    }

    @Override
    public Drawable getDrawable(String source) {
        final CacheManager cm = CacheManager.getInstance();
        final Resources res = this.view.getResources();
        final RequestQueue queue = this.requestQueueManager.getRequestQueue();
        final ImageLoader loader = new ImageLoader(queue, cm) {
            @Override
            protected void onGetImageSuccess(String cacheKey, Bitmap response) {
                super.onGetImageSuccess(cacheKey, response);
                view.requestLayout();
            }
        };
        final ImageContainer container = loader.get(source, this);
        final Bitmap bmp = container.getBitmap();
        if (null == bmp)
            return null;
        final BitmapDrawable drawable = new BitmapDrawable(res, bmp);
        drawable.setBounds(0, 0, bmp.getWidth(), bmp.getHeight());
        return drawable;
    }

    @Override
    public void onErrorResponse(VolleyError arg0) {

    }

    @Override
    public void onResponse(ImageContainer container, boolean b) {

    }

}
