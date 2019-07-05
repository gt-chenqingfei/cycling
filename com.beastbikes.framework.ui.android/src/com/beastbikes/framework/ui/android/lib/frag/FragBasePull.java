package com.beastbikes.framework.ui.android.lib.frag;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView.OnItemClickListener;

import com.beastbikes.framework.ui.android.BaseFragment;
import com.beastbikes.framework.ui.android.BuildConfig;
import com.beastbikes.framework.ui.android.R;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.PullRefeshListener;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.PullToRefreshBase;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.PullToRefreshProxy;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.PullableView;

public abstract class FragBasePull<K, V extends View> extends BaseFragment
        implements OnItemClickListener, PullRefeshListener<K> {

    private static final String TAG = "fragment";

    protected PullToRefreshProxy<K, V> pullProxy;
    protected PullToRefreshBase<V> pullView;
    protected V internalView;

    private PullConfigable<V> pullConfigableListener;
    private boolean configured = false;

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        if (BuildConfig.DEBUG) Log.d(TAG, this.getClass().getSimpleName() + " onattach");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (BuildConfig.DEBUG) Log.d(TAG, this.getClass().getSimpleName() + " onCreate");
    }

    @SuppressWarnings("unchecked")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        if (BuildConfig.DEBUG) Log.d(TAG, this.getClass().getSimpleName() + " onCreateView");
        View view = inflater.inflate(this.layoutResource(), container, false);

        pullView = (PullToRefreshBase<V>) view
                .findViewById(R.id.pullRefreshAbsListView);
        internalView = pullView.getRefreshableView();

        pullProxy = this.getPullProxy();
        long minInterval = this.getMinInterval();
        pullProxy.setMinInterval(minInterval);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        if (BuildConfig.DEBUG) Log.d(TAG, this.getClass().getSimpleName() + " onActivityCreated");
        pullProxy.onCreate();
        if (pullConfigableListener != null && !configured) {
            configured = true;
            pullConfigableListener.configView(pullView, internalView);
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        if (BuildConfig.DEBUG) Log.d(TAG, this.getClass().getSimpleName() + " onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        if (BuildConfig.DEBUG) Log.d(TAG, this.getClass().getSimpleName() + " onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        if (BuildConfig.DEBUG) Log.d(TAG, this.getClass().getSimpleName() + " onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, this.getClass().getSimpleName() + " onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, this.getClass().getSimpleName() + " onDestroyView");
    }

    @Override
    public void onDestroy() {
        if (pullProxy != null) {
            pullProxy.onDestroy();
        }
        super.onDestroy();
        Log.d(TAG, this.getClass().getSimpleName() + " onDestroy");

    }

    ;

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, this.getClass().getSimpleName() + " onDetach");
    }

    public long getMinInterval() {
        return 0;
    }

    protected void showListContextMenu() {
        pullProxy.showContextMenu();
    }

    // ===============================================
    // abstract method or methods children should override
    // ================================================

    protected String cacheKey() {
        return null;
    }

    protected PullableView getPullHeader() {
        return null;
    }

    /**
     * override this to use your custom layout
     */
    protected abstract int layoutResource();

    protected PullToRefreshProxy<K, V> getPullProxy() {
        PullToRefreshProxy<K, V> proxy = new PullToRefreshProxy<K, V>(pullView,
                this.cacheKey(), this);
        proxy.setPullHeader(getPullHeader());
        return proxy;
    }

    @Override
    public boolean shouldRefreshingHeaderOnStart() {
        return true;
    }

    public void setPullConfiguableListener(
            PullConfigable<V> pullConfiguableListener) {
        this.pullConfigableListener = pullConfiguableListener;
        if (pullProxy != null && !configured) {
            configured = true;
            pullConfigableListener.configView(pullView, internalView);
        }
    }

    public void pullToRefresh() {
        pullProxy.pullDownToRefresh();
    }

    public void pullToRefresh(boolean showRefreshingHeader) {
        pullProxy.pullDownToRefresh(showRefreshingHeader);
    }

}
