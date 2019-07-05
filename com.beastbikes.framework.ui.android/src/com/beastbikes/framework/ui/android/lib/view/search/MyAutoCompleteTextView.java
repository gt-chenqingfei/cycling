package com.beastbikes.framework.ui.android.lib.view.search;

import android.content.Context;
import android.os.Handler;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.widget.AutoCompleteTextView;
import android.widget.PopupWindow;

import com.beastbikes.framework.ui.android.BuildConfig;

import java.lang.reflect.Field;

public class MyAutoCompleteTextView extends AutoCompleteTextView {

    static final String tag = "dropdown";
    protected Handler handler = new Handler();
    private boolean isShowing = false;


    public MyAutoCompleteTextView(Context context, AttributeSet attrs,
                                  int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public MyAutoCompleteTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MyAutoCompleteTextView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        try {
            // 通过反射拿到POPUP，以方便监听dismiss事件，另isPopupShowing方法总是返回false，所以走到现在这一步
            Class<?> cls = this.getClass().getSuperclass();
            Field f = cls.getDeclaredField("mPopup");
            f.setAccessible(true);
            Object obj = f.get(this);

            cls = obj.getClass();
            f = cls.getDeclaredField("mPopup");
            f.setAccessible(true);
            Object objPop = f.get(obj);
            if (objPop instanceof PopupWindow) {
                PopupWindow popup = (PopupWindow) objPop;
                popup.setOnDismissListener(new PopupWindow.OnDismissListener() {

                    @Override
                    public void onDismiss() {
                        handler.postDelayed(new Runnable() {

                            @Override
                            public void run() {
                                Log.d(tag, "false");
                                isShowing = false;
                            }
                        }, 100);

                    }
                });
            }

        } catch (Throwable e) {
            System.err.println(e);
        }

    }

    @Override
    public boolean enoughToFilter() {
        return true;
    }

    @Override
    public void showDropDown() {
        if (BuildConfig.DEBUG) Log.d(tag, "true");
        isShowing = true;
        super.showDropDown();
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN: {
                if (!isShowing) {
                    handler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            showDropDown();
                            if (getFilter() != null) {
                                performFiltering(getEditableText(), -1);
                            }

                        }
                    }, 400);
                }
                break;
            }
        }
        return super.onTouchEvent(event);
    }

}
