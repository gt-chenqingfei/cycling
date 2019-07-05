package com.beastbikes.android.home.view;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.view.GestureDetector;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.home.HomeManager;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.DensityUtil;

/**
 * 把tab相关的东西都归到TabViewHolder类中
 * <p/>
 * 方便集中管理
 */
public class TabViewHolder implements View.OnTouchListener, View.OnClickListener {

    private View convertView;
    private ImageView ivIcon;
    private TextView tvTitle;
    private TextView dot;
    private String tag;
    private boolean reset = false;
    private GestureDetector gd;

    //tab
    private HomeManager.OnTabChangeListener mOnTabChangeListener;

    public TabViewHolder(int title, int icon, String tag, Context context,
                         ViewGroup tabParent, int tabCount, final OnDoubleClickListener onDoubleClickListener) {
        this.convertView = LayoutInflater.from(context).inflate(R.layout.tab_item, null);
        if (this.convertView != null) {

            this.ivIcon = (ImageView) this.convertView.findViewById(R.id.tab_item_icon);
            this.tvTitle = (TextView) this.convertView.findViewById(R.id.tab_item_title);
            this.dot = (TextView) this.convertView.findViewById(R.id.tab_item_dot);

            tabParent.addView(this.convertView);
            LinearLayout.LayoutParams params = (LinearLayout.LayoutParams)
                    this.convertView.getLayoutParams();
            params.width = DensityUtil.getWidth(context) / tabCount;
            this.convertView.setLayoutParams(params);
            this.convertView.setTag(tag);
        }
        this.tag = tag;
        this.ivIcon.setImageResource(icon);
        this.tvTitle.setText(context.getString(title));
        this.convertView.setOnClickListener(this);
        gd = new GestureDetector(context, new GestureDetector.SimpleOnGestureListener() {
            @Override
            public boolean onDoubleTapEvent(MotionEvent e) {
                if (onDoubleClickListener != null) {
                    onDoubleClickListener.onDoubleClick();
                }
                return super.onDoubleTapEvent(e);
            }
        });
        this.convertView.setOnTouchListener(this);

    }

    public void setDotText(int msgCount) {
        if (this.dot != null) {
            if (msgCount > 0) {
                this.dot.setVisibility(View.VISIBLE);
                this.dot.setText(msgCount > 100 ? "99+" : msgCount + "");
            } else {
                this.dot.setText("");
                this.dot.setVisibility(View.GONE);
            }
        }
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return this.tag;
    }


    public boolean isReset() {
        return reset;
    }

    public void setReset(boolean reset) {
        this.reset = reset;
    }

    public void setSelected(boolean isSelected) {
        if (this.convertView != null) {
            this.convertView.setSelected(isSelected);
        }
    }

    @Override
    public boolean onTouch(View v, MotionEvent event) {
        return gd.onTouchEvent(event);
    }

    @Override
    public void onClick(View view) {
        if (view == convertView && null != this.mOnTabChangeListener) {
            this.mOnTabChangeListener.onTabChange(getTag());
        }
    }

    public void setOnTabChangeListener(HomeManager.OnTabChangeListener onTabChangeListener) {
        this.mOnTabChangeListener = onTabChangeListener;
    }

    public interface OnDoubleClickListener {
        public void onDoubleClick();
    }
}