package com.beastbikes.android.modules.cycling.club.ui.widget.richeditor;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Display;
import android.view.WindowManager;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

/**
 * Created by zhangyao on 2016/3/14.
 */
public class RichEditorRelativeLayout extends RelativeLayout
{

    public static String ACTION_ON_VIEW_RESIZE="action_on_view_resize";
    public static String EXTRA_IS_SOFTKEYBOARD_SHOWN="Extra_isSoftKeyboardShown";
    private WindowManager manager = null;

    public RichEditorRelativeLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    private OnResizeListener mListener;




    public interface OnResizeListener
    {
        void OnSoftKeyboardChanged(boolean isSoftKeyboardShown);
    }

    public void setOnResizeListener(OnResizeListener l)
    {
        mListener = l;
        manager = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
    }

    @SuppressWarnings("deprecation")
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh)
    {
        super.onSizeChanged(w, h, oldw, oldh);
        int orientation = 0;
        if (manager != null)
        {
            Display d = manager.getDefaultDisplay();
            orientation = d.getOrientation();
        }
        Log.i("m", String.format("currentHeight =[%d], oldHeight=[%d],current -  old=[%d] orientation=[%d]", h, oldh, Math.abs(h - oldh), orientation));
        boolean isShown = (h < oldh && orientation == 0);
        if (Math.abs(h - oldh) > 100)
        {
            if(mListener != null)
            {
                mListener.OnSoftKeyboardChanged(isShown);
            }
        }

    }

    Handler handler = new Handler()
    {
        @Override
        public void handleMessage(Message msg)
        {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            mListener.OnSoftKeyboardChanged(msg.arg1 == 0);
        }

    };

    private void notifyOnSizeChange(boolean isShow){
        final Intent intent = new Intent();
        intent.setAction(ACTION_ON_VIEW_RESIZE);
        intent.putExtra(EXTRA_IS_SOFTKEYBOARD_SHOWN, isShow);

        getContext().sendBroadcast(intent);
    }

}

