/**
 * @file XListViewHeader.java
 * @create Apr 18, 2012 5:22:27 PM
 * @author Maxwin
 * @description XListView's header
 */
package com.beastbikes.android.widget;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.beastbikes.android.R;

public class ListViewHeader extends LinearLayout {
    private LinearLayout container;
    private ImageView refreshView;
    private int state = STATE_NORMAL;

    private AnimationDrawable refreshAnim;

    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_REFRESHING = 2;

    private boolean isWhiteHead;

    public ListViewHeader(Context context, boolean isWhiteHead) {
        super(context);
        this.isWhiteHead = isWhiteHead;
        initView(context);
    }

    /**
     * @param context
     * @param attrs
     */
    public ListViewHeader(Context context, AttributeSet attrs) {
        super(context, attrs);
        initView(context);
    }

    private void initView(Context context) {
        // 初始情况，设置下拉刷新view高度为0
        LinearLayout.LayoutParams lp = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, 0);
        this.container = (LinearLayout) LayoutInflater.from(context).inflate(
                R.layout.list_view_head_refresh, null);
        this.addView(container, lp);
        this.setGravity(Gravity.BOTTOM);

        refreshView = (ImageView) findViewById(R.id.list_view_head_refresh);
        if (!isWhiteHead) {
            this.refreshView.setImageResource(R.drawable.frame_refresh);
        } else {
            this.refreshView.setImageResource(R.drawable.frame_refresh_white_anim);
        }
        this.refreshAnim = (AnimationDrawable) this.refreshView.getDrawable();

    }

    public void setRefreshAnim(int resId) {
        this.refreshView.setImageResource(resId);
        this.refreshAnim = (AnimationDrawable) this.refreshView.getDrawable();
    }

    public void setViewBackgroundColor(int resId) {
        this.container.setBackgroundColor(getResources().getColor(resId));
    }

    public void setState(int state) {
        if (state == this.state)
            return;

        if (state == STATE_REFRESHING) { // 显示进度
            refreshAnim.start();
        } else { // 显示箭头图片
            refreshAnim.selectDrawable(0);
            refreshAnim.stop();
        }

        switch (state) {
            case STATE_NORMAL:
                break;
            case STATE_READY:
                break;
            case STATE_REFRESHING:
                break;
            default:
        }

        this.state = state;
    }

    public void setVisiableHeight(int height) {
        if (height < 0)
            height = 0;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) container
                .getLayoutParams();
        lp.height = height;
        this.container.setLayoutParams(lp);
    }


    public int getVisiableHeight() {
        return this.container.getHeight();
    }

}
