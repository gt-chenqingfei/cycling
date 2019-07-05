package com.beastbikes.framework.android.update;

import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;

public abstract class UpdateManager {

    /**
     * The callback of check update
     *
     * @author johnson
     */
    public interface CheckUpdateCallback {

        public void onUpdateAvailable(ReleasedPackage pkg);

    }

    public interface InstallationCallback {

        public void done(Throwable t);

    }

    private final Context context;

    private final Handler handler = new Handler(Looper.getMainLooper());

    public UpdateManager(Context context) {
        this.context = context;
    }

    public Context getContext() {
        return this.context;
    }

    protected Handler getHandler() {
        return this.handler;
    }

    /**
     * Returns the latest released package of the specified platform
     *
     * @param platform The platform which the package runs
     * @return the latest released package or null if no package released
     */
    public abstract ReleasedPackage getLatestReleasedPackage(String platform);

    /**
     * Returns all released packages
     *
     * @return all released packages
     */
    public abstract List<ReleasedPackage> getAllReleasedPackages();

    /**
     * Returns the released package of the specified platform with the spcified
     * version
     *
     * @param platform The platform which the package runs
     * @param version  the package version
     * @return a released package or null if not exists
     */
    public abstract ReleasedPackage getReleasedPackage(String platform,
                                                       String version);

    /**
     * Install the specified package
     *
     * @param pkg A released package
     */
    public abstract void install(ReleasedPackage pkg, InstallationCallback callback);

    /**
     * Check if this application is up to date
     *
     * @param callback
     */
    public void checkUpdate(final CheckUpdateCallback callback) {
        if (null == callback)
            return;

        final Thread thread = new Thread(new Runnable() {

            @Override
            public void run() {
                final ReleasedPackage latest = getLatestReleasedPackage("android");
                if (null == latest)
                    return;

                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        callback.onUpdateAvailable(latest);
                    }
                });
            }

        });
        thread.setDaemon(true);
        thread.setPriority(Thread.MIN_PRIORITY);
        thread.start();
    }

    protected void runOnUiThread(Runnable runnable) {
        if (null != runnable) {
            getHandler().post(runnable);
        }
    }

}
