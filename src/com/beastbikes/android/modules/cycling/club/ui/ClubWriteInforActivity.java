package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;

@MenuResource(R.menu.club_write_infor_menu)
@LayoutResource(R.layout.activity_club_write_infor)
public class ClubWriteInforActivity extends SessionFragmentActivity {
    @IdResource(R.id.activity_club_write_infor_phonenumber)
    private EditText phonenumber;

    @IdResource(R.id.activity_club_write_infor_detailed)
    private EditText detailed;

    @IdResource(R.id.activity_club_write_infor_ms)
    private EditText et_ms;
    private boolean ishonenumber = true;

    private String status;

    public static final String PHONENUMBER ="phonenumber";
    public static final String DESCRIPTION = "description";
    public static final String MS= "ms";

    public static final String TYPE = "type";
    public static final String NEAR = "ms";

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(getString(R.string.club_write_infor_title));
        }
        initView();
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId()==R.id.club_write_info_ok) {
            Intent intent = new Intent(this,ClubActivityReleaseActivity.class);
            if (status.equals(PHONENUMBER)){
                intent.putExtra(NEAR,phonenumber.getText().toString());
            }else if (status.equals(DESCRIPTION)){
                intent.putExtra(NEAR,detailed.getText().toString());
            }else {
                intent.putExtra(NEAR,et_ms.getText().toString());
            }
            setResult(RESULT_OK, intent);
            finish();
        }
        if (item.getItemId() == android.R.id.home) {
            showAlertDialog();
        }

        return false;
    }
    private void initView(){
        Intent intent = getIntent();
        String type = intent.getStringExtra(TYPE);
        String ms = intent.getStringExtra(NEAR);
        if (type.equals(PHONENUMBER)){
            phonenumber.setVisibility(View.VISIBLE);
            detailed.setVisibility(View.GONE);
            et_ms.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(ms)&&!ms.equals(
                    getResources().
                            getString(
                                    R.string.activity_club_release_activities_activity_Phone_Number))){
                phonenumber.setText(ms);
            }
            status = PHONENUMBER;

        }else if (type.equals(DESCRIPTION)){
            phonenumber.setVisibility(View.GONE);
            detailed.setVisibility(View.VISIBLE);
            et_ms.setVisibility(View.GONE);
            if (!TextUtils.isEmpty(ms)&&!ms.equals(
                    getResources().
                            getString(
                    R.string.activity_club_release_activities_activity_Phone_activity_description))) {
                detailed.setText(ms);
            }
            status = DESCRIPTION;
        }
        else if(type.equals(NEAR)){
            phonenumber.setVisibility(View.GONE);
            detailed.setVisibility(View.GONE);
            et_ms.setVisibility(View.VISIBLE);
            et_ms.setText(ms);
            status = MS;
        }
    }
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }
    private boolean isfinsh() {
        boolean isfinsh = true;
        if (!TextUtils.isEmpty(phonenumber.getText().toString())) {
            isfinsh = false;
        } else if (!TextUtils.isEmpty(detailed.getText().toString())) {
            isfinsh = false;
        } else if (!TextUtils.isEmpty(et_ms.getText().toString())) {
            isfinsh = false;
        }
        return isfinsh;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event)
    {
        if (keyCode == KeyEvent.KEYCODE_BACK )
        {
            showAlertDialog();
        }
        return false;
    }

    private void showAlertDialog(){
        if (isfinsh())
            finish();
        else {
            final MaterialDialog dialog = new MaterialDialog(this);
            dialog.setMessage(R.string.club_release_activites_dialog_ms);
            dialog.setPositiveButton(R.string.club_release_activites_dialog_ok, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                    finish();
                }
            });
            dialog.setNegativeButton(R.string.club_release_activites_dialog_cencle, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    dialog.dismiss();
                }
            });
            dialog.show();
        }
    }
}
