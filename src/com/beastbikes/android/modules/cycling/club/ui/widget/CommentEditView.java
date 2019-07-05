package com.beastbikes.android.modules.cycling.club.ui.widget;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;

/**
 * Created by chenqingfei on 15/12/2.
 */
public class CommentEditView extends LinearLayout implements View.OnClickListener, TextWatcher,TextView.OnEditorActionListener {


    @Override
    public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
       // if(replyId != 0) {
            return (event.getKeyCode() == KeyEvent.KEYCODE_ENTER);
//        }
//        return false;
    }

    public interface CommentPostListener {
        public void doPost(final String content, final int replyId, final int feedId);
    }

    private CommentPostListener listener;
    private EditText etComment;
    private Button btnSend;
    private View clubFeedPost;
    private Activity context;
    private SoftInputChangedListener receiver;
    private int replyId;
    private int feedId;
    private String hint;
    // VISIBLE
    private boolean inVisible;

    public CommentEditView(Activity context) {
        super(context);
        this.context = context;
        LayoutInflater.from(context).inflate(R.layout.clubfeed_post, this);
        initView();
        receiver = new SoftInputChangedListener();

    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();
        context.registerReceiver(receiver, new IntentFilter(MyFrameLayout.ACTION_ON_VIEW_RESIZE));
    }

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (receiver != null && !receiver.isOrderedBroadcast())
            this.context.unregisterReceiver(receiver);
    }

    public void setListener(CommentPostListener l) {
        this.listener = l;
    }

    private void initView() {
        clubFeedPost = findViewById(R.id.clubfeed_post);
        etComment = (EditText) findViewById(R.id.et_clubfeed_comment);
        btnSend = (Button) findViewById(R.id.btn_clubfeed_send);
        btnSend.setOnClickListener(this);
        etComment.addTextChangedListener(this);
        etComment.setOnEditorActionListener(this);
        setCLubFeedGone();
    }

    @Override
    public void onClick(View v) {
        if (v == btnSend) {
            if (!TextUtils.isEmpty(etComment.getText()) && listener != null) {
                this.listener.doPost(etComment.getText().toString(), replyId, feedId);
                this.etComment.setText("");
            }
            if (!inVisible) {
                toggleSoftInput();
            } else {
                togglePhotoSoftInput();
            }
        }
    }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        btnSend.setEnabled(count > 0);
    }

    @Override
    public void afterTextChanged(Editable s) {

    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        Log.d("m", "w" + w);
    }

    public void toggleSoftInput() {

        this.etComment.setFocusable(true);
        this.etComment.setEnabled(true);
        this.etComment.setFocusableInTouchMode(true);
        this.etComment.requestFocus();
        this.etComment.setSelection(0);

        InputMethodManager imm = (InputMethodManager) getContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);

        imm.toggleSoftInputFromWindow(etComment.getWindowToken(),
                InputMethodManager.SHOW_IMPLICIT, InputMethodManager.RESULT_SHOWN);

    }

    public void togglePhotoSoftInput() {
        this.etComment.setFocusable(true);
        this.etComment.setEnabled(true);
        this.etComment.setSelection(0);
        InputMethodManager imm = (InputMethodManager) getContext().
                getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.toggleSoftInputFromWindow(etComment.getWindowToken(),
                InputMethodManager.SHOW_IMPLICIT, InputMethodManager.RESULT_SHOWN);
    }

    public void setClubFeedVisibility() {
        LayoutParams params = (LayoutParams) clubFeedPost.getLayoutParams();
        params.height = LayoutParams.WRAP_CONTENT;
        clubFeedPost.setMinimumHeight((int) context.getResources().getDimension(R.dimen.clubfeed_comment_et_height));
        clubFeedPost.setLayoutParams(params);
        this.inVisible = true;
    }

    public void setCLubFeedGone() {
        LayoutParams params = (LayoutParams) clubFeedPost.getLayoutParams();
        params.height = 0;
        clubFeedPost.setLayoutParams(params);
    }

    public void setParams(int feedId, int replyId) {
        if (this.feedId != feedId) {
            etComment.setText("");
        } else if (this.replyId != replyId) {
            etComment.setText("");
        }
        this.feedId = feedId;
        this.replyId = replyId;
    }

    public void setTextHint(String hint) {
        this.hint = hint;
        this.etComment.setHint(hint);
    }

    public int getFeedId() {
        return feedId;
    }

    public int getReplyId() {
        return replyId;
    }


    class SoftInputChangedListener extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent != null && intent.getAction().equals(MyFrameLayout.ACTION_ON_VIEW_RESIZE)) {
                boolean isShow = intent.getBooleanExtra(MyFrameLayout.EXTRA_IS_SOFTKEYBOARD_SHOWN, false);

                LayoutParams params = (LayoutParams) clubFeedPost.getLayoutParams();
                if (isShow) {
                    params.height = LayoutParams.WRAP_CONTENT;
                    clubFeedPost.setMinimumHeight((int) context.getResources().getDimension(R.dimen.clubfeed_comment_et_height));
                }
                else {
                    params.height = 0;
                    clubFeedPost.setMinimumHeight(0);
                }
                clubFeedPost.setLayoutParams(params);
            }
        }
    }

}
