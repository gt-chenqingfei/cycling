package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.avos.avoscloud.AVAnalytics;
import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

@Alias("发布公告")
@MenuResource(R.menu.postnotice_menu)
@LayoutResource(R.layout.club_post_notice_activity)
public class ClubPostNoticeActivity extends SessionFragmentActivity {

    @IdResource(R.id.club_post_notice_edit)
    private EditText noticeEt;

    private ClubManager clubManager;

    public static String noticeBefore;
    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.clubManager = new ClubManager(this);
        Intent intent = getIntent();
        if (null == intent) {
            return;
        }

        this.noticeEt.setOnEditorActionListener(new OnEditorActionListener() {

            @Override
            public boolean onEditorAction(TextView v, int actionId,
                                          KeyEvent event) {
                return event.getKeyCode() == KeyEvent.KEYCODE_ENTER;
            }
        });
        if (TextUtils.isEmpty(noticeBefore)) {
            noticeBefore = intent
                    .getStringExtra(ClubMoreActivity.EXTRA_CLUB_NOTICE);
        }
        if (!TextUtils.isEmpty(noticeBefore)) {
            this.noticeEt.setText(noticeBefore);
        }
    }

    @Override
    public void finish() {
        String notice = this.noticeEt.getText().toString();
        setResult(RESULT_OK);
        if (TextUtils.isEmpty(notice)) {
            super.finish();
            super.overridePendingTransition(0, R.anim.activity_out_to_right);
            return;
        }
        if (!notice.equals(noticeBefore)) {
            showLastMemberDialog();
            return;
        }
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.friends_add_menu_item: {
                this.postUpdateClubNotice();
                SpeedxAnalytics.onEvent(this, "发布公告",null);
                break;
            }
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void showLastMemberDialog() {
        final MaterialDialog dialog = new MaterialDialog(this);
        dialog.setMessage(R.string.notice_change_notrealse);
        dialog.setPositiveButton(R.string.quit, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                noticeBefore = noticeEt.getText().toString();
                finish();
            }
        }).setNegativeButton(R.string.cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        }).show();
    }

    private void postUpdateClubNotice() {
        final String notice = this.noticeEt.getText().toString();
        if (TextUtils.isEmpty(notice) || TextUtils.isEmpty(notice.trim())) {
            Toasts.show(this, R.string.club_info_item_club_notice_cannot_null);
            return;
        }
        this.loadingDialog = new LoadingDialog(this, "", true);
        this.loadingDialog.show();
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    return clubManager.postUpdateClubNotice(params[0]);
                } catch (BusinessException e) {
                    return false;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (loadingDialog != null && loadingDialog.isShowing()) {
                    loadingDialog.dismiss();
                }
                if (result) {
                    Toasts.show(ClubPostNoticeActivity.this,
                            R.string.club_info_club_post_notice_success);
                    Intent intent = getIntent();
                    intent.putExtra(ClubMoreActivity.EXTRA_CLUB_NOTICE,
                            noticeEt.getText().toString());
                    setResult(RESULT_OK, intent);
                    noticeBefore = notice;
                    finish();
                } else {
                    Toasts.show(ClubPostNoticeActivity.this,
                            R.string.club_info_club_post_notice_error);
                }
            }

        }, notice);
    }

}
