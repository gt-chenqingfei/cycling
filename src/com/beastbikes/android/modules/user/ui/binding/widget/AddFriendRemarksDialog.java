package com.beastbikes.android.modules.user.ui.binding.widget;

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

import com.beastbikes.android.R;

/**
 * Created by zhangyao on 2016/3/8.
 */
public class AddFriendRemarksDialog extends Dialog implements
        android.view.View.OnClickListener, TextWatcher {

    private TextView tvContent;
    private EditText editText;
    private Button cancle, sure;
    //
    private String content, hint;

    private boolean canClickEnter = true;

    //
    private OnMessageDialogClickListener listener;

    private int maxLength = 20;


    private String cancleStr ;
    private String sureStr;


    public interface OnMessageDialogClickListener {
        void onMessageDialogClickOk(String text);
    }

    public AddFriendRemarksDialog(Context context, String content, String hint,
                       OnMessageDialogClickListener listener) {
        super(context, R.style.message_dialog);
        this.content = content;
        this.hint = hint;
        this.listener = listener;
        this.setCancelable(false);

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_add_friendre_marks);
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
                    .setOnEditorActionListener(new TextView.OnEditorActionListener() {

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
                this.listener.onMessageDialogClickOk(editText.getText().toString());
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
