package com.beastbikes.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.beastbikes.android.R;

public class AlertDialog extends Dialog implements
        android.view.View.OnClickListener {

    private TextView tvTitle;
    private TextView tvContent;
    private Button cancle, sure;
    //
    private String title, content;
    private String textcancle = getContext().getResources().getString(R.string.activity_alert_dialog_text_cancel);
    private String textSure = getContext().getResources().getString(R.string.activity_alert_dialog_text_ok);
    private int id;
    private int tvContentSize = 16;
    //
    private DialogListener listener;

    public interface DialogListener {
        void onClickOk(int id);

        void onClickCancel(int id);
    }

    public AlertDialog(Context context, String title, String content,
                       DialogListener listener, int id) {
        super(context, R.style.alert_dialog);
        this.title = title;
        this.content = content;
        this.listener = listener;
        this.id = id;
    }

    public AlertDialog(Context context, String title, String content,
                       String textSure, DialogListener listener, int id) {
        super(context, R.style.alert_dialog);
        this.title = title;
        this.content = content;
        this.listener = listener;
        this.id = id;
        this.textSure = textSure;
    }

    public AlertDialog(Context context, String title, String content,
                       String textcancel, String textSure, DialogListener listener, int id) {
        super(context, R.style.alert_dialog);
        this.title = title;
        this.content = content;
        this.textcancle = textcancel;

        this.textSure = textSure;
        this.listener = listener;
        this.id = id;
    }

    public AlertDialog(Context context, String title, String url,
                       String content, String textcancle, String textSure,
                       DialogListener listener, int id) {
        super(context, R.style.alert_dialog);
        this.title = title;
        this.content = content;
        this.textcancle = textcancle;

        this.textSure = textSure;
        this.listener = listener;
        this.id = id;
    }

    public AlertDialog(Context context, String title, String url,
                       String content, String textcancle, String textSure,
                       DialogListener listener, boolean cancelable, int id) {
        super(context, R.style.alert_dialog);
        this.title = title;
        this.content = content;
        this.textcancle = textcancle;

        this.textSure = textSure;
        this.listener = listener;
        this.id = id;
        this.setCancelable(cancelable);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.dialog_alert_layout);
        initView();
        fillView();
    }

    private void initView() {

        tvTitle = (TextView) findViewById(R.id.tv_title);
        tvContent = (TextView) findViewById(R.id.tv_content);

        cancle = (Button) findViewById(R.id.cancle);
        sure = (Button) findViewById(R.id.sure);

        cancle.setOnClickListener(this);
        sure.setOnClickListener(this);
    }

    private void fillView() {

        if (TextUtils.isEmpty(title)) {
            tvTitle.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(content)) {
            tvContent.setVisibility(View.GONE);
        }

        if (TextUtils.isEmpty(textSure)) {
            sure.setVisibility(View.GONE);
        }
        tvTitle.setText(title);
        tvContent.setText(content);
        tvContent.setTextSize(tvContentSize);
        cancle.setText(textcancle);
        sure.setText(textSure);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure: {
                this.dismiss();
                if (null != listener)
                    listener.onClickOk(this.id);
                break;
            }
            case R.id.cancle: {
                this.dismiss();
                if (null != listener)
                    listener.onClickCancel(id);
                break;
            }
        }

    }

}
