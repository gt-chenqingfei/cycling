package com.beastbikes.android.modules.user.ui.binding;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

@LayoutResource(R.layout.activity_bind_mail_box)
public class BindMailBoxActivity extends SessionFragmentActivity {
    @IdResource(R.id.activity_bind_mail_box)
    private EditText mail;

    @IdResource(R.id.activity_bind_mail_box_commit)
    private TextView commit;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(getString(R.string.activity_bind_mailbox_title));
        }
        init_view();
    }

    private void init_view() {
        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

}
