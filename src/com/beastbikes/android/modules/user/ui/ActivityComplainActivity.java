package com.beastbikes.android.modules.user.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityManager;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

@LayoutResource(R.layout.activity_complain_activity)
@MenuResource(R.menu.activity_complain_menu)
public class ActivityComplainActivity extends SessionFragmentActivity {

    public static final String EXTRA_ACTIVITY_ID = "activity_id";

    @IdResource(R.id.activity_complain_contact_et)
    private EditText contactEt;

    @IdResource(R.id.activity_complain_question_et)
    private EditText questionEt;

    private ActivityManager activityManager;
    private String activityId;
    private boolean isCommit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.activityManager = new ActivityManager(this);

        Intent intent = getIntent();
        if (null == intent)
            return;
        this.activityId = intent.getStringExtra(EXTRA_ACTIVITY_ID);

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.activity_complain_menu_commit:
                if (isCommit)
                    break;

                this.isCommit = true;
                this.commit();
                break;

            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    /**
     * 提交申诉
     */
    private void commit() {
        String contact = this.contactEt.getText().toString();
        if (TextUtils.isEmpty(contact)) {
            this.isCommit = false;
            Toasts.show(this, R.string.activity_complain_contact_empty);
            return;
        }

        String question = this.questionEt.getText().toString();
        if (TextUtils.isEmpty(question)) {
            this.isCommit = false;
            Toasts.show(this, R.string.activity_complain_question_empty);
            return;
        }

        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
            }

            @Override
            protected Boolean doInBackground(String... params) {
                try {
                    return activityManager.postComplainForActivity(params[0],
                            params[1], activityId);
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
//                    String message = getString(R.string.activity_complain_commit_success);
//                    Toasts.show(ActivityComplainActivity.this, message);
                    finish();
                } else {
                    isCommit = false;
                }
            }

        }, contact, question);
    }

}
