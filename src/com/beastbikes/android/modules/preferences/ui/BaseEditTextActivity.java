package com.beastbikes.android.modules.preferences.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.ui.android.utils.Toasts;

@Alias("文本编辑(全部)")
@MenuResource(R.menu.edit_text_activity)
@LayoutResource(R.layout.edit_text_activity)
public class BaseEditTextActivity extends SessionFragmentActivity {

    public static final String EXTRA_VALUE = "value";

    public static final String EXTRA_MAX_LENGTH = "length";

    @IdResource(R.id.edit_text_activity_value)
    private EditText txtInput;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        Intent intent = getIntent();
        if (null == intent)
            return;

        String value = intent.getStringExtra(EXTRA_VALUE);

        if (intent.hasExtra(EXTRA_MAX_LENGTH)) {
            this.txtInput
                    .setFilters(new InputFilter[]{new InputFilter.LengthFilter(
                            intent.getIntExtra(EXTRA_MAX_LENGTH, 12))});
        }
        this.txtInput.setText(value);
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
                final Intent intent = getIntent();
                if (null == intent)
                    return false;

                final String text = this.txtInput.getText().toString();

                if (TextUtils.isEmpty(text.trim())) {
                    Toasts.show(this, R.string.edit_text_activity_can_not_be_null);
                    return false;
                }

                intent.putExtra(EXTRA_VALUE, text);

                setResult(RESULT_OK, intent);
                finish();
                return true;
            }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

}
