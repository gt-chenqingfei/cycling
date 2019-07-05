package com.beastbikes.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.framework.ui.android.utils.Toasts;

public class LoadingDialog extends Dialog {

    private TextView tipTextView;
    private String msg;

    private AnimationDrawable animationDrawable;

    public LoadingDialog(Context context, String msg, boolean cancelable) {
        super(context, R.style.loading_dialog);
        this.msg = msg;
        setCancelable(cancelable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.dialog_loading_layout);

        this.tipTextView = (TextView) findViewById(R.id.tipTextView);
        ImageView loadingIv = (ImageView) findViewById(R.id.loading_img);
        this.animationDrawable = (AnimationDrawable) loadingIv.getDrawable();

        if (TextUtils.isEmpty(msg)) {
            this.tipTextView.setVisibility(View.GONE);
        } else {
            this.tipTextView.setText(msg);
        }

    }

    public void setMessage(String msg) {
        if (TextUtils.isEmpty(msg)) {
            this.tipTextView.setVisibility(View.GONE);
        } else {
            this.tipTextView.setVisibility(View.VISIBLE);
            this.tipTextView.setText(msg);
        }
    }

    @Override
    public void show() {
        super.show();
        if (null != this.animationDrawable) {
            this.animationDrawable.start();
        }
    }

    public void show(long delayMillis, final String delayErrorMsg) {
        show();
        this.tipTextView.postDelayed(new Runnable() {
            @Override
            public void run() {
                if (LoadingDialog.this.isShowing()) {
                    LoadingDialog.this.dismiss();
                    Toasts.show(getContext(), delayErrorMsg);
                }
            }
        }, delayMillis);
    }


    @Override
    public void dismiss() {
        super.dismiss();
        if (null != this.animationDrawable) {
            this.animationDrawable.stop();
        }
    }
}
