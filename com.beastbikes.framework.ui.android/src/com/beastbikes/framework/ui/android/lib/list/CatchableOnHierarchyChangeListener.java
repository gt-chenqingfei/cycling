/**
 *
 */
package com.beastbikes.framework.ui.android.lib.list;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup.OnHierarchyChangeListener;

import com.beastbikes.framework.ui.android.BuildConfig;


/**
 *
 *
 */
public abstract class CatchableOnHierarchyChangeListener implements
        OnHierarchyChangeListener {
    public abstract void intlOnChildViewAdded(View parent, View child);

    public abstract void intlOnChildViewRemoved(View parent, View child);

    @Override
    public void onChildViewAdded(View parent, View child) {
        try {
            intlOnChildViewAdded(parent, child);
        } catch (final Error t) {
            if (BuildConfig.DEBUG) Log.e("CatchableOnHierarchyChangeListener", t.getMessage(), t);

            throw (Error) t.fillInStackTrace();
        }
    }

    @Override
    public void onChildViewRemoved(View parent, View child) {
        try {
            intlOnChildViewRemoved(parent, child);
        } catch (final Error t) {
            if (BuildConfig.DEBUG) Log.e("CatchableOnHierarchyChangeListener", t.getMessage(), t);

            throw (Error) t.fillInStackTrace();
        }
    }
}
