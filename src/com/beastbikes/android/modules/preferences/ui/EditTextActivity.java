package com.beastbikes.android.modules.preferences.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.activity.biz.ActivityManager;
import com.beastbikes.android.modules.cycling.activity.dao.entity.LocalActivity;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.BaseFragmentActivity;
import com.beastbikes.framework.ui.android.utils.Toasts;

@Alias("文本编辑(全部)")
@MenuResource(R.menu.edit_text_activity)
@LayoutResource(R.layout.edit_text_activity)
public class EditTextActivity extends BaseFragmentActivity {

    public static final String EXTRA_VALUE = "value";

    public static final String EXTRA_FROM_SETTING = "setting";

    public static final String EXTRA_FROM_FINISH = "finish";

    public static final String EXTRA_FROM_OFFLINE = "offline";

    private ActivityManager activityManager;

    private boolean isUpdateTitle = false;

    private String activityId;

    @IdResource(R.id.edit_text_activity_value)
    private EditText txtInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        int padding = dip2px(this, 8);
        this.txtInput.setPadding(padding, 0, padding, 0);

        final Intent intent = getIntent();
        if (null != intent) {
            final String value = getIntent().getStringExtra(EXTRA_VALUE);
            this.txtInput.setText(value);
        }

        if (intent.hasExtra(EXTRA_FROM_SETTING)) {
            this.txtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(8)});
        } else {
            this.txtInput.setFilters(new InputFilter[]{new InputFilter.LengthFilter(20)});
        }

        if (intent.hasExtra(EXTRA_FROM_FINISH) || intent.hasExtra(EXTRA_FROM_OFFLINE)) {
            this.activityManager = new ActivityManager(this);
            if (intent.hasExtra(EXTRA_FROM_FINISH)) {
                this.activityId = intent.getStringExtra(EXTRA_FROM_FINISH);
            } else {
                this.activityId = intent.getStringExtra(EXTRA_FROM_OFFLINE);
            }
            this.isUpdateTitle = true;
        }

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.edit_text_activity_action_button_save: {
                final Intent intent = new Intent();
                final Editable text = this.txtInput.getText();

                if (text != null && text.length() > 0) {
                    intent.putExtra(EXTRA_VALUE, String.valueOf(text));
                } else {
                    Toasts.show(this, R.string.edit_text_activity_can_not_be_null);
                    return false;
                }

                if (TextUtils.isEmpty(text.toString().trim())) {
                    Toasts.show(this, R.string.edit_text_activity_can_not_be_null);
                    return false;
                }

                final Intent i = getIntent();

                if (null != i && i.hasExtra(EXTRA_FROM_OFFLINE)) {
                    try {
                        final LocalActivity la = this.activityManager
                                .getLocalActivity(this.activityId);
                        la.setTitle(text.toString());
                        this.activityManager.updateLocalActivity(la);
                    } catch (BusinessException e) {
                        this.finish();
                    }
                }

                setResult(RESULT_OK, intent);
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }
}
