package com.beastbikes.framework.ui.android;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.android.schedule.AsyncTaskQueue;
import com.beastbikes.framework.android.schedule.AsyncTaskQueueFactory;
import com.beastbikes.framework.android.schedule.AsyncTaskQueueManager;
import com.beastbikes.framework.ui.android.utils.ViewIntrospector;
import com.umeng.analytics.MobclickAgent;

/**
 * The base activity
 *
 * @author johnson
 */
public abstract class BaseActivity extends Activity implements
        AsyncTaskQueueManager {

    private AsyncTaskQueue asyncTaskQueue;

    @Override
    public AsyncTaskQueue getAsyncTaskQueue() {
        return this.asyncTaskQueue;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.asyncTaskQueue = AsyncTaskQueueFactory.newTaskQueue(this);

        final Class<?> clazz = getClass();
        final LayoutResource layout = clazz.getAnnotation(LayoutResource.class);

        if (layout != null) {
            super.setContentView(layout.value());
        }

        ViewIntrospector.introspect(this, this);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        final Class<?> clazz = getClass();
        final MenuResource mr = clazz.getAnnotation(MenuResource.class);

        if (mr != null) {
            final MenuInflater inflater = getMenuInflater();
            inflater.inflate(mr.value(), menu);
        }

        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onStop() {
        this.asyncTaskQueue.cancelAll(this);
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        this.asyncTaskQueue.cancelAll(this);
        this.asyncTaskQueue.stop();
        super.onDestroy();
    }

    @Override
    protected void onResume() {
        MobclickAgent.onResume(this);
        super.onResume();
    }

    @Override
    protected void onPause() {
        MobclickAgent.onPause(this);
        super.onPause();
    }
}
