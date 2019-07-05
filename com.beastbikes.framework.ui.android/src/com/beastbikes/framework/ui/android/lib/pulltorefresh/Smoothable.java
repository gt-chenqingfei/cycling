package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import android.os.Handler;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;

public abstract class Smoothable implements Runnable {

    static final int ANIMATION_FPS = 1000 / 60;

    private final long duration;
    private final Interpolator interpolator;
    private final int scrollToY;
    private final int scrollFromY;
    private final Handler handler;

    private boolean continueRunning = true;
    private long startTime = -1;
    private int currentY = -1;

    public Smoothable(Handler handler, long duration, int fromY, int toY) {
        this.handler = handler;
        this.duration = duration;
        this.scrollFromY = fromY;
        this.scrollToY = toY;
        this.interpolator = new AccelerateDecelerateInterpolator();
    }

    @Override
    public void run() {

        /**
         * Only set startTime if this is the first time we're starting, else
         * actually calculate the Y delta
         */
        if (startTime == -1) {
            startTime = System.currentTimeMillis();
        } else {

            /**
             * We do do all calculations in long to reduce software float
             * calculations. We use 1000 as it gives us good accuracy and small
             * rounding errors
             */
            long normalizedTime = (1000 * (System.currentTimeMillis() - startTime))
                    / duration;
            normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

            final int deltaY = Math.round((scrollFromY - scrollToY)
                    * interpolator.getInterpolation(normalizedTime / 1000f));
            this.currentY = scrollFromY - deltaY;
            doSmooth(currentY);
        }

        // If we're not at the target Y, keep going...
        if (continueRunning && scrollToY != currentY) {
            handler.postDelayed(this, ANIMATION_FPS);
        }
    }

    /**
     * 更新你的进程
     *
     * @param currentDiff
     */
    public abstract void doSmooth(int currentDiff);

    public void stop() {
        this.continueRunning = false;
        this.handler.removeCallbacks(this);
    }

}
