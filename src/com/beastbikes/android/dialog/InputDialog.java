package com.beastbikes.android.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.beastbikes.android.R;

public class InputDialog extends Dialog implements
        android.view.View.OnClickListener, TextWatcher {

    private TextView tvContent;
    private EditText editText;
    private Button cancle, sure;
    //
    private String content, hint;

    private boolean canClickEnter = true;

    //
    private OnInputDialogClickListener listener;

    private int maxLength = 20;


    private String cancleStr ;
    private String sureStr;


    public interface OnInputDialogClickListener {
        void onInputDialogClickOk(String text);
    }

    public InputDialog(Context context, String content, String hint,
                       OnInputDialogClickListener listener) {
        super(context, R.style.message_dialog);
        this.content = content;
        this.hint = hint;
        this.listener = listener;
        this.setCancelable(false);

    }

    public InputDialog(Context context, String content, String hint,
                       OnInputDialogClickListener listener, int maxLength,
                       boolean cancelable, boolean canClickEnter) {
        super(context, R.style.message_dialog);
        this.content = content;
        this.hint = hint;
        this.listener = listener;
        this.maxLength = maxLength;
        this.setCancelable(cancelable);
        this.canClickEnter = canClickEnter;
    }

    public InputDialog(Context context, String content, String hint,
                       OnInputDialogClickListener listener, int maxLength,
                       boolean cancelable, boolean canClickEnter, String cancleStr, String sureStr) {
        super(context, R.style.message_dialog);
        this.content = content;
        this.hint = hint;
        this.listener = listener;
        this.maxLength = maxLength;
        this.setCancelable(cancelable);
        this.canClickEnter = canClickEnter;
        this.cancleStr = cancleStr;
        this.sureStr = sureStr;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.message_dialog);
        this.initView();
        this.fillView();

    }

    private void initView() {
        this.tvContent = (TextView) findViewById(R.id.tv_hint);
        this.editText = (EditText) findViewById(R.id.edit);

        this.cancle = (Button) findViewById(R.id.cancle);
        this.sure = (Button) findViewById(R.id.sure);

        this.cancle.setOnClickListener(this);
        sure.setOnClickListener(this);

        this.editText.requestFocus();
        // editText.addTextChangedListener(this);
    }

    private void fillView() {
        if (!TextUtils.isEmpty(content)) {
            this.tvContent.setText(content);
            this.tvContent.setVisibility(View.VISIBLE);
        } else {
            this.tvContent.setVisibility(View.GONE);
        }

        if (!TextUtils.isEmpty(hint))
            this.editText.setHint(hint);

        if (!TextUtils.isEmpty(cancleStr))
            this.cancle.setText(cancleStr);

        if (!TextUtils.isEmpty(sureStr))
            this.sure.setText(sureStr);

        this.editText
                .setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                        this.maxLength)});
        if (!canClickEnter) {
            this.editText
                    .setOnEditorActionListener(new OnEditorActionListener() {

                        @Override
                        public boolean onEditorAction(TextView v, int actionId,
                                                      KeyEvent event) {
                            return event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
                        }
                    });
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.sure: {
                this.listener.onInputDialogClickOk(editText.getText().toString());
                break;
            }
            case R.id.cancle: {
                this.dismiss();
                break;
            }
        }
    }

    @Override
    public void afterTextChanged(Editable arg0) {
        this.sure.setEnabled(editText.getText().length() > 0);
    }

    @Override
    public void beforeTextChanged(CharSequence arg0, int arg1, int arg2,
                                  int arg3) {

    }

    @Override
    public void onTextChanged(CharSequence arg0, int arg1, int arg2, int arg3) {
    }

}
