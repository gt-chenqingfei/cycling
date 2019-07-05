package com.beastbikes.framework.ui.android;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import com.android.volley.RequestQueue;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.android.schedule.AsyncTaskQueue;
import com.beastbikes.framework.android.schedule.AsyncTaskQueueFactory;
import com.beastbikes.framework.android.schedule.AsyncTaskQueueManager;
import com.beastbikes.framework.android.schedule.RequestQueueFactory;
import com.beastbikes.framework.android.schedule.RequestQueueManager;
import com.beastbikes.framework.ui.android.utils.ViewIntrospector;

/**
 * The base fragment
 *
 * @author johnson
 */
public abstract class BaseFragment extends Fragment implements
        RequestQueueManager, AsyncTaskQueueManager {

    private RequestQueue requestQueue;
    private AsyncTaskQueue asyncTaskQueue;

    @Override
    public RequestQueue getRequestQueue() {
        return this.requestQueue;
    }

    @Override
    public AsyncTaskQueue getAsyncTaskQueue() {
        return this.asyncTaskQueue;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final Class<?> clazz = getClass();
        final LayoutResource layout = clazz.getAnnotation(LayoutResource.class);
        final MenuResource menu = clazz.getAnnotation(MenuResource.class);
        final View v;

        if (layout != null) {
            v = inflater.inflate(layout.value(), null);
        } else {
            v = super.onCreateView(inflater, container, savedInstanceState);
        }

        ViewIntrospector.introspect(v, this);
        setHasOptionsMenu(null != menu);

        return v;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);

        final Class<?> clazz = getClass();
        final MenuResource mr = clazz.getAnnotation(MenuResource.class);

        if (mr != null) {
            inflater.inflate(mr.value(), menu);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                if(getActivity() != null) {
                    getActivity().finish();
                }
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAttach(Activity activity) {
        this.requestQueue = RequestQueueFactory.newRequestQueue(activity);
        this.asyncTaskQueue = AsyncTaskQueueFactory.newTaskQueue(activity);
        super.onAttach(activity);
    }

    @Override
    public void onStop() {
        this.requestQueue.cancelAll(this);
        this.asyncTaskQueue.cancelAll(this);
        super.onStop();
    }

    @Override
    public void onDetach() {
        this.requestQueue.cancelAll(this);
        this.requestQueue.stop();
        this.asyncTaskQueue.cancelAll(this);
        this.asyncTaskQueue.stop();
        super.onDestroy();
    }

}
