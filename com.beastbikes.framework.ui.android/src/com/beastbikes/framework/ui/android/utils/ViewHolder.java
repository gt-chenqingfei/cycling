package com.beastbikes.framework.ui.android.utils;

import android.content.Context;
import android.view.View;

/**
 * The {@link ViewHolder} is used by view adapter
 *
 * @param <T>
 * @author johnson
 */
public abstract class ViewHolder<T> {

    @SuppressWarnings("unchecked")
    public static <T extends ViewHolder<?>> T as(View v) {
        return (T) v.getTag();
    }

    private final View view;

    /**
     * Create an instance with the specified view
     *
     * @param v The root view to hold
     */
    protected ViewHolder(View v) {
        this.view = v;
        v.setTag(this);
        ViewIntrospector.introspect(v, this);
    }

    /**
     * Returns the context
     *
     * @return the context
     */
    public Context getContext() {
        return this.view.getContext();
    }

    /**
     * Bind the specified view
     *
     * @param t The object to bound
     */
    public abstract void bind(T t);

}
