/**
 * @file XFooterView.java
 * @create Mar 31, 2012 9:33:43 PM
 * @author Maxwin
 * @description XListView's footer
 */
package com.beastbikes.android.widget;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;

public class ListViewFooter extends LinearLayout {
    public final static int STATE_NORMAL = 0;
    public final static int STATE_READY = 1;
    public final static int STATE_LOADING = 2;
    public final static int STATE_NO_MORE = 3;

    private View contentView;
    private View progressBar;
    private TextView hintView;
    private LinearLayout moreView;

    private int state;

    public ListViewFooter(Context context) {
        super(context);
        this.initView(context);
    }

    public ListViewFooter(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.initView(context);
    }

    public void setState(int state) {
        this.state = state;
        this.hintView.setVisibility(View.INVISIBLE);
        this.progressBar.setVisibility(View.INVISIBLE);
        this.hintView.setVisibility(View.INVISIBLE);
        if (state == STATE_READY) {
            this.hintView.setVisibility(View.VISIBLE);
            this.hintView.setText(R.string.list_view_footer_hint_ready);
        } else if (state == STATE_LOADING) {
            this.progressBar.setVisibility(View.VISIBLE);
        } else if (state == STATE_NORMAL) {
            this.hintView.setVisibility(View.VISIBLE);
            this.hintView.setText(R.string.list_view_footer_hint_normal);
        } else {
            this.hintView.setVisibility(View.VISIBLE);
            this.hintView.setText(R.string.list_view_footer_hint_no_more);
            this.moreView.setVisibility(View.GONE);
        }
    }

    public int getState() {
        return this.state;
    }

    public void setBottomMargin(int height) {
        if (height < 0)
            return;
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) contentView
                .getLayoutParams();
        lp.bottomMargin = height;
        this.contentView.setLayoutParams(lp);
    }

    public int getBottomMargin() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) contentView
                .getLayoutParams();
        return lp.bottomMargin;
    }

    /**
     * normal status
     */
    public void normal() {
        this.hintView.setVisibility(View.VISIBLE);
        this.progressBar.setVisibility(View.GONE);
    }

    /**
     * loading status
     */
    public void loading() {
        this.hintView.setVisibility(View.GONE);
        this.progressBar.setVisibility(View.VISIBLE);
    }

    /**
     * hide footer when disable pull load more
     */
    public void hide() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) contentView
                .getLayoutParams();
        lp.height = 0;
        this.contentView.setLayoutParams(lp);
    }

    /**
     * show footer
     */
    public void show() {
        LinearLayout.LayoutParams lp = (LinearLayout.LayoutParams) contentView
                .getLayoutParams();
        lp.height = LayoutParams.WRAP_CONTENT;
        this.contentView.setLayoutParams(lp);
    }

    private void initView(Context context) {
        this.moreView = (LinearLayout) LayoutInflater.from(context)
                .inflate(R.layout.list_view_footer_more, null);
        this.addView(moreView);
        moreView.setLayoutParams(new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT));

        this.contentView = moreView.findViewById(R.id.list_view_footer_content);
        this.progressBar = moreView
                .findViewById(R.id.list_view_footer_progressbar);
        this.hintView = (TextView) moreView
                .findViewById(R.id.list_view_footer_hint_textview);
    }

}
