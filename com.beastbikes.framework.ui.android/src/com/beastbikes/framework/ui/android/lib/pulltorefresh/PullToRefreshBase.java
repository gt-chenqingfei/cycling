package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import java.util.Date;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.AccelerateDecelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.FrameLayout;
import android.widget.LinearLayout;

import com.beastbikes.framework.ui.android.R;

@SuppressWarnings("deprecation")
public abstract class PullToRefreshBase<T extends View> extends LinearLayout {
    private static final String LOG = "mypull";
    public static final int ANIMATION_DURATION_MS = 150;

    final class SmoothScrollRunnable implements Runnable {

        static final int ANIMATION_FPS = 1000 / 60;

        private final Interpolator interpolator;
        private final int scrollToY;
        private final int scrollFromY;
        private final Handler handler;

        private boolean continueRunning = true;
        private long startTime = -1;
        private int currentY = -1;

        public SmoothScrollRunnable(Handler handler, int fromY, int toY) {
            this.handler = handler;
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
                 * calculations. We use 1000 as it gives us good accuracy and
                 * small rounding errors
                 */
                long normalizedTime = (1000 * (System.currentTimeMillis() - startTime))
                        / ANIMATION_DURATION_MS;
                normalizedTime = Math.max(Math.min(normalizedTime, 1000), 0);

                final int deltaY = Math
                        .round((scrollFromY - scrollToY)
                                * interpolator
                                .getInterpolation(normalizedTime / 1000f));
                this.currentY = scrollFromY - deltaY;
                setHeaderScroll(currentY);
            }

            // If we're not at the target Y, keep going...
            if (continueRunning && scrollToY != currentY) {
                handler.postDelayed(this, ANIMATION_FPS);
            }
        }

        public void stop() {
            this.continueRunning = false;
            this.handler.removeCallbacks(this);
        }
    }

    ;

    // ===========================================================
    // Constants
    // ===========================================================

    static final float FRICTION = 2.0f;
    private static final int DP_HORIZONTAL_SCROLL = 55;

    static final int PULL_DOWN_TO_REFRESH = 0x1;
    static final int PULL_UP_TO_REFRESH = 0x2;
    static final int RELEASE_TO_REFRESH = 0x3;
    static final int REFRESHING = 0x4;
    static final int MANUAL_REFRESHING = 0x5;

    public static final int MODE_NONE = 0x0;
    public static final int MODE_PULL_DOWN_TO_REFRESH = 0x1;
    public static final int MODE_PULL_UP_TO_REFRESH = 0x2;
    public static final int MODE_BOTH = 0x3;

    // ===========================================================
    // Fields
    // ===========================================================

    private int touchSlop;
    // scroll large than this distance, think user is scrolling horizontal
    private int horizontalSlop;
    /**
     * indicate whether on fling was invoked, avoiding invoking multi times.
     */
    private boolean isFlingInvoked = false;
    /**
     * indicate whether the fling was handled, if true, next touch event will
     * invoke onTouchedAfterFlinged
     */
    private boolean isFlingHandled = false;

    private float initialMotionX;
    private float initialMotionY;
    private float lastMotionX;
    private float lastMotionY;
    private boolean isBeingDragged = false;

    private int state = PULL_DOWN_TO_REFRESH;
    private int mode = MODE_BOTH;
    private int currentMode;

    private boolean disableScrollingWhileRefreshing = true;

    FrameLayout refreshContainer;
    View emptyView;
    T refreshableView;
    private boolean isPullToRefreshEnabled = true;

    private PullableView headerLayout;
    private PullableView footerLayout;
    private View pullHeaderView;
    private View pullFooterView;
    private int headerHeight, headerOffset;
    private int footerHeight;

    private final Handler handler = new Handler();

    private OnRefreshListener onRefreshListener;
    private OnFlingListener onFlingListener;

    private SmoothScrollRunnable currentSmoothScrollRunnable;

    // ===========================================================
    // Constructors
    // ===========================================================

    public PullToRefreshBase(Context context) {
        super(context);
        init(context, null);
    }

    public PullToRefreshBase(Context context, int mode) {
        super(context);
        this.mode = mode;
        init(context, null);
    }

    public PullToRefreshBase(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    // ===========================================================
    // Getter & Setter
    // ===========================================================

    /**
     * Get the Wrapped Refreshable View. Anything returned here has already been
     * added to the content view.
     *
     * @return The View which is currently wrapped
     */
    public final T getRefreshableView() {
        return refreshableView;
    }

    /**
     * Whether Pull-to-Refresh is enabled
     *
     * @return enabled
     */
    public final boolean isPullToRefreshEnabled() {
        return isPullToRefreshEnabled;
    }

    /**
     * Returns whether the widget has disabled scrolling on the Refreshable View
     * while refreshing.
     *
     * @param true if the widget has disabled scrolling while refreshing
     */
    public final boolean isDisableScrollingWhileRefreshing() {
        return disableScrollingWhileRefreshing;
    }

    /**
     * Returns whether the Widget is currently in the Refreshing state
     *
     * @return true if the Widget is currently refreshing
     */
    public final boolean isRefreshing() {
        return state == REFRESHING || state == MANUAL_REFRESHING;
    }

    /**
     * By default the Widget disabled scrolling on the Refreshable View while
     * refreshing. This method can change this behaviour.
     *
     * @param disableScrollingWhileRefreshing - true if you want to disable scrolling while refreshing
     */
    public final void setDisableScrollingWhileRefreshing(
            boolean disableScrollingWhileRefreshing) {
        this.disableScrollingWhileRefreshing = disableScrollingWhileRefreshing;
    }

    /**
     * Mark the current Refresh as complete. Will Reset the UI and hide the
     * Refreshing View
     */
    public final void onRefreshComplete() {
        if (state != PULL_DOWN_TO_REFRESH) {
            resetHeader();
        }
    }

    /**
     * Set OnRefreshListener for the Widget
     *
     * @param listener - Listener to be used when the Widget is set to Refresh
     */
    public final void setOnRefreshListener(OnRefreshListener listener) {
        onRefreshListener = listener;
    }

    public final void setOnFlingListener(OnFlingListener listener) {
        onFlingListener = listener;
    }

    /**
     * A mutator to enable/disable Pull-to-Refresh for the current View
     *
     * @param enable Whether Pull-To-Refresh should be used
     */
    public final void setPullToRefreshEnabled(boolean enable) {
        this.isPullToRefreshEnabled = enable;
    }

    /**
     * Set Text to show when the Widget is being pulled, and will refresh when
     * released
     *
     * @param releaseLabel - String to display
     */
    public void setReleaseLabel(String releaseLabel) {
        if (null != headerLayout) {
            headerLayout.setReleaseLabel(releaseLabel);
        }
        if (null != footerLayout) {
            footerLayout.setReleaseLabel(releaseLabel);
        }
    }

    /**
     * Set Text to show when the Widget is being Pulled
     *
     * @param pullLabel - String to display
     */
    public void setPullLabel(String pullLabel) {
        if (null != headerLayout) {
            headerLayout.setPullLabel(pullLabel);
        }
        if (null != footerLayout) {
            footerLayout.setPullLabel(pullLabel);
        }
    }

    /**
     * Set Text to show when the Widget is refreshing
     *
     * @param refreshingLabel - String to display
     */
    public void setRefreshingLabel(String refreshingLabel) {
        if (null != headerLayout) {
            headerLayout.setRefreshingLabel(refreshingLabel);
        }
        if (null != footerLayout) {
            footerLayout.setRefreshingLabel(refreshingLabel);
        }
    }

    /**
     * Set background color to header
     *
     * @param color - color to setting
     */
    public void setPullHeaderBackgroundColor(int color) {
        if (null != headerLayout) {
            headerLayout.getView().setBackgroundColor(color);
        }
        if (null != footerLayout) {
            footerLayout.getView().setBackgroundColor(color);
        }
    }

    /**
     * Sets the Widget to be in the refresh state. The UI will be updated to
     * show the 'Refreshing' view.
     *
     * @param doScroll - true if you want to force a scroll to the Refreshing view.
     */
    public final void setRefreshing(boolean doScroll) {
        if (!isRefreshing()) {
            this.headerLayout.updateTimeLabel();
            this.headerLayout.setUpdateTime(getTime());
            this.currentMode = MODE_PULL_DOWN_TO_REFRESH;
            setRefreshingInternal(doScroll);
            state = MANUAL_REFRESHING;
            this.onRefreshListener.onPullDownRefresh();
        }
    }

    public final boolean hasPullFromTop() {
        return currentMode != MODE_PULL_UP_TO_REFRESH;
    }

    // ===========================================================
    // Methods for/from SuperClass/Interfaces
    // ===========================================================

    @Override
    public void setVisibility(int visibility) {
        super.setVisibility(visibility);
        this.getRefreshableView().setVisibility(visibility);
    }

    @Override
    public final boolean onTouchEvent(MotionEvent event) {

        if (isTouchAfterFling(event)) {
            return true;
        }

        if (!isPullToRefreshEnabled) {
            return false;
        }

        if (isRefreshing() && disableScrollingWhileRefreshing) {
            return true;
        }

        if (event.getAction() == MotionEvent.ACTION_DOWN
                && event.getEdgeFlags() != 0) {
            return false;
        }

        switch (event.getAction()) {

            case MotionEvent.ACTION_MOVE: {
                if (isBeingDragged) {
                    lastMotionY = event.getY();
                    this.pullEvent();
                    return true;
                }
                break;
            }

            case MotionEvent.ACTION_DOWN: {
                // if (isReadyForPull()) {
                lastMotionY = initialMotionY = event.getY();
                isFlingInvoked = false;
                return true;
                // }
            }

            case MotionEvent.ACTION_CANCEL:
            case MotionEvent.ACTION_UP: {
                if (isBeingDragged) {
                    isBeingDragged = false;

                    if (state == RELEASE_TO_REFRESH && null != onRefreshListener) {

                        if (currentMode == MODE_PULL_DOWN_TO_REFRESH
                                && canPullDownRefresh) {
                            setRefreshingInternal(true);
                            this.headerLayout.setUpdateTime(getTime());
                            onRefreshListener.onPullDownRefresh();
                        } else if (currentMode == MODE_PULL_UP_TO_REFRESH
                                && canPullUpRefresh) {
                            setRefreshingInternal(true);
                            this.footerLayout.setUpdateTime(getTime());
                            onRefreshListener.onPullUpRefresh();
                        } else {
                            // MLog.d(LOG, "onTouchEvent RELEASE_TO_REFRESH "
                            // + canPullDownRefresh + " " + canPullUpRefresh
                            // + " " + currentMode);
                            smoothScrollTo(0);
                        }
                    } else if (state == REFRESHING || state == MANUAL_REFRESHING) {
                        int y = currentMode == MODE_PULL_DOWN_TO_REFRESH ? -headerHeight
                                : footerHeight;
                        smoothScrollTo(y);
                    } else {
                        smoothScrollTo(0);
                    }
                    return true;
                }
                break;
            }
        }

        return false;
    }

    @Override
    public final boolean onInterceptTouchEvent(MotionEvent event) {
        // MLog.d(LOG, event.toString());

        if (isTouchAfterFling(event)) {
            return true;
        }

        if (!isPullToRefreshEnabled) {
            return false;
        }

        if (isRefreshing() && disableScrollingWhileRefreshing) {
            return true;
        }

        final int action = event.getAction();

        if (action == MotionEvent.ACTION_CANCEL
                || action == MotionEvent.ACTION_UP) {

            isBeingDragged = false;
            return false;
        }

        if (action != MotionEvent.ACTION_DOWN && isBeingDragged) {
            return true;
        }

        switch (action) {
            case MotionEvent.ACTION_MOVE: {
                if (!isFlingInvoked) {
                    final float dy = event.getY() - initialMotionY;
                    final float yDiff = Math.abs(dy);
                    final float xDiffValue = event.getX() - initialMotionX;
                    final float xDiff = Math.abs(xDiffValue);
                    Log.d(LOG, "x1: " + initialMotionX + " y1:" + initialMotionY
                            + "xd: " + xDiff + " yd: " + yDiff);
                    if (xDiff > horizontalSlop && xDiff > yDiff
                            && onFlingListener != null) {
                        if (xDiffValue < horizontalSlop) {
                            isFlingInvoked = true;
                            isFlingHandled = onFlingListener.onFlingToLeft(
                                    initialMotionX, initialMotionY, event.getX(),
                                    event.getY());

                        } else {
                            isFlingInvoked = true;
                            isFlingHandled = onFlingListener.onFlingToRight(
                                    initialMotionX, initialMotionY, event.getX(),
                                    event.getY());
                        }
                    }
                }
                if (isReadyForPull() && !isFlingHandled) {

                    final float y = event.getY();
                    final float dy = y - lastMotionY;
                    final float yDiff = Math.abs(dy);
                    final float xDiffValue = event.getX() - lastMotionX;
                    final float xDiff = Math.abs(xDiffValue);

                    if (yDiff > touchSlop && yDiff > xDiff) {
                        if ((mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH)
                                && dy >= 0.0001f && isReadyForPullDown()) {
                            lastMotionY = y;
                            isBeingDragged = true;
                            if (mode == MODE_BOTH
                                    || mode == MODE_PULL_DOWN_TO_REFRESH) {
                                this.headerLayout.updateTimeLabel();
                                currentMode = MODE_PULL_DOWN_TO_REFRESH;
                                Log.d(LOG, "current mode is set to: "
                                        + currentMode);
                            }
                        } else if ((mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH)
                                && dy <= 0.0001f && isReadyForPullUp()) {
                            lastMotionY = y;
                            isBeingDragged = true;
                            if (mode == MODE_BOTH
                                    || mode == MODE_PULL_UP_TO_REFRESH) {
                                this.footerLayout.updateTimeLabel();
                                currentMode = MODE_PULL_UP_TO_REFRESH;
                                Log.d(LOG, "current mode is set to: "
                                        + currentMode);
                            }
                        }
                    }

                }
                break;
            }
            case MotionEvent.ACTION_DOWN: {
                // if (isReadyForPull()) {
                lastMotionY = initialMotionY = event.getY();
                lastMotionX = initialMotionX = event.getX();
                isBeingDragged = false;
                isFlingInvoked = false;
                // }
                break;
            }
        }

        return isBeingDragged || isFlingHandled;
    }

    /**
     * check whether after OnFling handled, is true, invoke
     * onTouchedAfterFlinged, and return true.
     */
    private boolean isTouchAfterFling(MotionEvent event) {
        if (isFlingHandled && onFlingListener != null
                && event.getAction() == MotionEvent.ACTION_DOWN) {
            onFlingListener.onTouchedAfterFlinged(event.getX(), event.getY());
            isFlingHandled = false;
            return true;
        }
        return false;
    }

    protected void addRefreshableView(Context context, T refreshableView) {
        refreshContainer.addView(refreshableView,
                android.widget.FrameLayout.LayoutParams.MATCH_PARENT,
                FrameLayout.LayoutParams.MATCH_PARENT);
    }

    /**
     * This is implemented by derived classes to return the created View. If you
     * need to use a custom View (such as a custom ListView), override this
     * method and return an instance of your custom class.
     * <p/>
     * Be sure to set the ID of the view in this method, especially if you're
     * using a ListActivity or ListFragment.
     *
     * @param context
     * @param attrs   AttributeSet from wrapped class. Means that anything you
     *                include in the XML layout declaration will be routed to the
     *                created View
     * @return New instance of the Refreshable View
     */
    protected abstract T createRefreshableView(Context context,
                                               AttributeSet attrs);

    protected final int getCurrentMode() {
        return currentMode;
    }

    protected final int getMode() {
        return mode;
    }

    /**
     * Implemented by derived class to return whether the View is in a state
     * where the user can Pull to Refresh by scrolling down.
     *
     * @return true if the View is currently the correct state (for example, top
     * of a ListView)
     */
    protected abstract boolean isReadyForPullDown();

    /**
     * Implemented by derived class to return whether the View is in a state
     * where the user can Pull to Refresh by scrolling up.
     *
     * @return true if the View is currently in the correct state (for example,
     * bottom of a ListView)
     */
    protected abstract boolean isReadyForPullUp();

    // ===========================================================
    // Methods
    // ===========================================================

    protected void resetHeader() {
        state = PULL_DOWN_TO_REFRESH;
        isBeingDragged = false;

        if (null != headerLayout) {
            headerLayout.reset();
        }
        if (null != footerLayout) {
            footerLayout.reset();
        }

        smoothScrollTo(0);
    }

    protected void setRefreshingInternal(boolean doScroll) {

        state = REFRESHING;

        if (null != headerLayout) {
            headerLayout.refreshing();
        }
        if (null != footerLayout) {
            footerLayout.refreshing();
        }

        if (doScroll) {

            int y = currentMode == MODE_PULL_DOWN_TO_REFRESH ? -(headerHeight + headerOffset)
                    : footerHeight;
            smoothScrollTo(y);
        }
    }

    protected final void setHeaderScroll(int y) {
        scrollTo(0, y);
    }

    protected final void smoothScrollTo(int y) {

        if (null != currentSmoothScrollRunnable) {
            currentSmoothScrollRunnable.stop();
        }

        if (this.getScrollY() != y) {
            this.currentSmoothScrollRunnable = new SmoothScrollRunnable(
                    handler, getScrollY(), y);
            handler.post(currentSmoothScrollRunnable);
        }
    }

    private void init(Context context, AttributeSet attrs) {

        setOrientation(LinearLayout.VERTICAL);

        touchSlop = ViewConfiguration.getTouchSlop();
        horizontalSlop = DensityUtil.dip2px(DP_HORIZONTAL_SCROLL, this.getContext());

        // Styleables from XML
        TypedArray a = context.obtainStyledAttributes(attrs,
                R.styleable.PullToRefresh);
        if (a.hasValue(R.styleable.PullToRefresh_ptrMode)) {
            mode = a.getInteger(R.styleable.PullToRefresh_ptrMode, MODE_BOTH);
        }

        refreshContainer = new FrameLayout(context);
        addView(refreshContainer, new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 0, 1.0f));
        // Refreshable View
        // By passing the attrs, we can add ListView/GridView params via XML
        refreshableView = this.createRefreshableView(context, attrs);
        this.addRefreshableView(context, refreshableView);

        // Loading View Strings
        String pullDownLabel = context
                .getString(R.string.pull_down_to_refresh_pull_label);

        String pullUpLabel = context
                .getString(R.string.pull_up_to_refresh_pull_label);

        String refreshingLabel = context
                .getString(R.string.pull_to_refresh_refreshing_label);
        String releaseLabel = context
                .getString(R.string.pull_to_refresh_release_label);

        // Add Loading Views
        if (mode == MODE_PULL_DOWN_TO_REFRESH || mode == MODE_BOTH) {
            LoadingLayout header = new LoadingLayout(context,
                    MODE_PULL_DOWN_TO_REFRESH, releaseLabel, pullDownLabel,
                    refreshingLabel);
            this.setPullHeaderView(header);
        }
        if (mode == MODE_PULL_UP_TO_REFRESH || mode == MODE_BOTH) {
            LoadingLayout footer = new LoadingLayout(context,
                    MODE_PULL_UP_TO_REFRESH, releaseLabel, pullUpLabel,
                    refreshingLabel);
            this.setPullFooterView(footer);
        }

        if (a.hasValue(R.styleable.PullToRefresh_ptrHeaderBackground)) {
            this.setBackgroundResource(a.getResourceId(
                    R.styleable.PullToRefresh_ptrHeaderBackground, Color.WHITE));
        }
        if (a.hasValue(R.styleable.PullToRefresh_adapterViewBackground)) {
            refreshableView.setBackgroundResource(a.getResourceId(
                    R.styleable.PullToRefresh_adapterViewBackground,
                    Color.WHITE));
        }
        a.recycle();

        // Hide Loading Views
        refreshPadding();

        // If we're not using MODE_BOTH, then just set currentMode to current
        // mode
        if (mode != MODE_BOTH) {
            currentMode = mode;
        }
    }

    private void refreshPadding() {
        switch (mode) {
            case MODE_BOTH:
                setPadding(0, -headerHeight, 0, -footerHeight);
                break;
            case MODE_PULL_UP_TO_REFRESH:
                setPadding(0, 0, 0, -footerHeight);
                break;
            case MODE_PULL_DOWN_TO_REFRESH:
            default:
                setPadding(0, -headerHeight, 0, 0);
                break;
        }
    }

    private void measureView(View child) {
        ViewGroup.LayoutParams p = child.getLayoutParams();
        if (p == null) {
            p = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
        }

        int childWidthSpec = ViewGroup.getChildMeasureSpec(0, 0 + 0, p.width);
        int lpHeight = p.height;
        int childHeightSpec;
        if (lpHeight > 0) {
            childHeightSpec = MeasureSpec.makeMeasureSpec(lpHeight,
                    MeasureSpec.EXACTLY);
        } else {
            childHeightSpec = MeasureSpec.makeMeasureSpec(0,
                    MeasureSpec.UNSPECIFIED);
        }
        child.measure(childWidthSpec, childHeightSpec);
    }

    /**
     * Actions a Pull Event
     *
     * @return true if the Event has been handled, false if there has been no
     * change
     */
    private boolean pullEvent() {

        int newHeight;
        final int oldHeight = this.getScrollY();

        switch (currentMode) {
            case MODE_PULL_UP_TO_REFRESH:
                newHeight = Math.round(Math.max(initialMotionY - lastMotionY, 0)
                        / FRICTION);
                newHeight = state == REFRESHING ? newHeight + footerHeight
                        : newHeight;
                break;
            case MODE_PULL_DOWN_TO_REFRESH:
            default:
                newHeight = Math.round(Math.min(initialMotionY - lastMotionY, 0)
                        / FRICTION);
                newHeight = state == REFRESHING || state == MANUAL_REFRESHING ? newHeight
                        - headerHeight
                        : newHeight;
                break;
        }

        setHeaderScroll(newHeight);

        if (newHeight != 0) {

            if (state < REFRESHING) {
                switch (currentMode) {
                    case MODE_PULL_UP_TO_REFRESH:
                        footerLayout.updateRefresh(newHeight, footerHeight);
                        break;
                    case MODE_PULL_DOWN_TO_REFRESH:
                        headerLayout.updateRefresh(newHeight, headerHeight);
                        break;
                }
            }

            if (state == PULL_DOWN_TO_REFRESH
                    && (headerHeight < Math.abs(newHeight) || footerHeight < Math
                    .abs(newHeight))) {
                state = RELEASE_TO_REFRESH;

                switch (currentMode) {
                    case MODE_PULL_UP_TO_REFRESH:
                        footerLayout.releaseToRefresh();
                        break;
                    case MODE_PULL_DOWN_TO_REFRESH:
                        headerLayout.releaseToRefresh();
                        break;
                }

                return true;

            } else if (state == RELEASE_TO_REFRESH
                    && headerHeight >= Math.abs(newHeight)) {
                state = PULL_DOWN_TO_REFRESH;

                switch (currentMode) {
                    case MODE_PULL_UP_TO_REFRESH:
                        footerLayout.pullToRefresh();
                        break;
                    case MODE_PULL_DOWN_TO_REFRESH:
                        headerLayout.pullToRefresh();
                        break;
                }

                return true;
            }
        }

        return oldHeight != newHeight;
    }

    private boolean isReadyForPull() {
        // switch (mode) {
        // case MODE_PULL_DOWN_TO_REFRESH:
        // return isReadyForPullDown();
        // case MODE_PULL_UP_TO_REFRESH:
        // return isReadyForPullUp();
        // case MODE_BOTH:
        return isReadyForPullUp() || isReadyForPullDown();
        // }
        // return false;
    }

    // ===========================================================
    // Inner and Anonymous Classes
    // ===========================================================

    public static interface OnRefreshListener {

        public void onPullDownRefresh();

        public void onPullUpRefresh();

    }

    public static interface OnLastItemVisibleListener {

        public void onLastItemVisible();

    }

    @Override
    public void setLongClickable(boolean longClickable) {
        getRefreshableView().setLongClickable(longClickable);
    }

    private boolean canPullDownRefresh = true;
    private boolean canPullUpRefresh = true;

    public void disablePull() {
        pullFooterView.setVisibility(View.INVISIBLE);
        pullHeaderView.setVisibility(View.INVISIBLE);
        this.canPullDownRefresh = false;
        this.canPullUpRefresh = false;
    }

    public void enablePull() {
        pullFooterView.setVisibility(View.VISIBLE);
        pullHeaderView.setVisibility(View.VISIBLE);
        this.canPullDownRefresh = true;
        this.canPullUpRefresh = true;
    }

    public void disablePullUp() {
        pullFooterView.setVisibility(View.INVISIBLE);
        this.canPullUpRefresh = false;
    }

    public void enablePullUp() {
        pullFooterView.setVisibility(View.VISIBLE);
        this.canPullUpRefresh = true;
    }

    public void disablePullDown() {
        pullHeaderView.setVisibility(View.INVISIBLE);
        this.canPullDownRefresh = false;
    }

    public long getUpdateTime() {
        return this.headerLayout.getUpdateTime();
    }

    public void setUpdateTime(long ut) {
        this.headerLayout.setUpdateTime(ut);
    }

    private long getTime() {
        Date now = new Date();
        return now.getTime();
    }

    /**
     * 设置数据为空的提示
     *
     * @param emptyView
     */
    public void setEmptyView(View emptyView) {
        if (this.emptyView != emptyView) {
            refreshContainer.removeView(this.emptyView);
        }
        if (emptyView != null) {
            FrameLayout.LayoutParams param = new FrameLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            param.gravity = Gravity.CENTER;
            param.topMargin = -DensityUtil.dip2px(40, this.getContext());
            refreshContainer.addView(emptyView, 0, param);
            this.emptyView = emptyView;
            this.emptyView.setVisibility(View.GONE);
        }
    }

    public void addEmptyView(View emptyView){
        if(emptyView != null){
            refreshContainer.removeAllViews();
            refreshContainer.addView(emptyView);
        }
    }

    /**
     * 显示emptyview
     */
    public void showEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(View.VISIBLE);
            emptyView.bringToFront();
            refreshContainer.bringChildToFront(emptyView);
        }
    }

    /**
     * 隐藏emptyview
     */
    public void hideEmptyView() {
        if (emptyView != null) {
            emptyView.setVisibility(View.GONE);
        }
    }


    public void setPullHeaderView(PullableView header) {
        if (header == null) {
            return;
        }

        if (pullHeaderView != null) {
            this.removeView(pullHeaderView);
        }

        this.headerLayout = header;
        pullHeaderView = headerLayout.getView();

        addView(pullHeaderView, 0, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        measureView(pullHeaderView);

        headerHeight = pullHeaderView.getMeasuredHeight();
        refreshPadding();
    }

    public void setPullHeaderTextColor(int color) {
        if (this.headerLayout != null) {
            this.headerLayout.setTextColor(color);
        }
        if (this.footerLayout != null) {
            this.footerLayout.setTextColor(color);
        }
    }

    public void setPullFooterView(PullableView footer) {
        if (footer == null) {
            return;
        }

        if (pullFooterView != null) {
            this.removeView(pullFooterView);
        }

        this.footerLayout = footer;
        pullFooterView = footerLayout.getView();

        addView(pullFooterView, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.FILL_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT));
        measureView(pullFooterView);

        footerHeight = pullFooterView.getMeasuredHeight();
        refreshPadding();
    }

    /**
     * next pull down refresh, header view will stop on offset+headerheight
     * position
     *
     * @param headerOffset
     */
    public void setHeaderOffset(int headerOffset) {
        this.headerOffset = headerOffset;
    }

}
