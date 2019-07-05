package com.beastbikes.android.modules.user.ui.binding;

import android.content.Intent;
import android.graphics.Paint;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.view.Gravity;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.user.biz.AccountBindManager;
import com.beastbikes.android.modules.user.dto.AccountDTO;
import com.beastbikes.android.utils.RxBus;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;

import java.util.List;

@LayoutResource(R.layout.actvivity_set_password_success)
public class BandingSuccessActivity extends SessionFragmentActivity {
    @IdResource(R.id.actvivity_set_password_success_account_number)
    private TextView accountNumber;

    @IdResource(R.id.actvivity_set_password_success_ms)
    private TextView ms;

    @IdResource(R.id.actvivity_set_password_success_remove)
    private TextView remove_binding;

    @IdResource(R.id.actvivity_set_password_success_icon)
    private ImageView icon;

    public static final int EXTRA_MAIL = 0x123;

    public static final int EXTRA_PHONE = 0x124;

    public static final String EXTRA_TYPE = "type";

    public static final int MAIL_ICON = R.drawable.actvivity_set_password_success_icon_mail;

    public static final int PHONE_ICON =R.drawable.actvivity_set_password_success_icon_phone ;

    public static final String AUTHKEY = "authKey";

    private String authKey;

    private int type ;

    private AccountBindManager accountBindManager;

    private android.support.v7.app.ActionBar bar;

    private static final int AYTHTYPE=2;

    private SelectPopupWindow popupWindow;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        this.bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }
        accountBindManager = new AccountBindManager(this);
        init_intent();
        init_view();
    }

    private void init_intent() {
        Intent intent = getIntent();
        this.type = intent.getIntExtra(EXTRA_TYPE,0);
        this.authKey = intent.getStringExtra(AUTHKEY);
    }

    private void init_view() {
        accountNumber.setText(authKey);
        remove_binding.getPaint().setFlags(Paint.UNDERLINE_TEXT_FLAG );
        switch (type){
            case EXTRA_MAIL:
                bar.setTitle(getString(R.string.activity_bind_mailbox_title));
                icon.setImageResource(MAIL_ICON);
                ms.setText(Html.fromHtml("<u>" + getString(R.string.actvivity_set_password_success_mail_str) + "</u>"));
                break;

            case EXTRA_PHONE:
                bar.setTitle(getString(R.string.activity_bind_phone_title));
                icon.setImageResource(PHONE_ICON);
                ms.setText(getString(R.string.actvivity_set_password_success_phone_str));
                break;
        }

    }

    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    public void onClick(View view){
        int type = view.getId();
        switch (type){
            case R.id.actvivity_set_password_success_commit:
                Intent intent = new Intent(this,SetPasswordActivity.class);
                intent.putExtra(SetPasswordActivity.AUTHKEY,authKey);
                intent.putExtra(SetPasswordActivity.TYPE,SetPasswordActivity.EXTRA_PHONE);
                startActivity(intent);
                break;
            case R.id.actvivity_set_password_success_remove:
                showPopupWindow();
                break;
        }
    }
    //显示对话框再次确认解绑
    private void showRemoveBindingDialog() {
            final MaterialDialog  dialog = new MaterialDialog(this);
            dialog.setMessage(getString(R.string.activity_account_management_dialog_ms_head) + getString(R.string.
                    activity_account_management_phone_str) +
                    getString(R.string.activity_account_management_dialog_ms_tail));
            dialog.setPositiveButton(R.string.club_release_activites_dialog_binding, new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    removeBinding();
                    dialog.dismiss();
                }
            });
        dialog.setNegativeButton(R.string.activity_alert_dialog_text_cancel, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });
        dialog.show();

    }

    private void removeBinding(){
        final LoadingDialog dialog = new LoadingDialog(this,
                getString(R.string.loading_msg), false);
        dialog.show();
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<AccountDTO>>() {
            @Override
            protected List<AccountDTO> doInBackground(Void... params) {
                try {
                    return accountBindManager.unbindUser(authKey, AYTHTYPE);
                } catch (BusinessException e) {
                    if (dialog.isShowing())
                        dialog.dismiss();
                    e.printStackTrace();
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<AccountDTO> accountDTOs) {
                if (accountDTOs == null)
                    return;
                if (dialog.isShowing()) {
                    dialog.dismiss();
                }
                RxBus.getDefault().post(new Accounts(accountDTOs));
                finish();
                super.onPostExecute(accountDTOs);
            }
        });
    }

    private void showPopupWindow(){
        popupWindow = new SelectPopupWindow(this, new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popupWindow.dismiss();
                if (v.getId()==R.id.popup_window_account_slelect_banding)
                    showRemoveBindingDialog();
            }
        });
        popupWindow.showAtLocation(findViewById(R.id.actvivity_set_password_success),
                Gravity.BOTTOM | Gravity.CENTER_HORIZONTAL, 0, 0);

    }

    public static class Accounts {
        private List<AccountDTO> list;

        public Accounts(List<AccountDTO> list) {
            this.list = list;
        }

        public List<AccountDTO> getList() {
            return list;
        }

        public void setList(List<AccountDTO> list) {
            this.list = list;
        }
    }

}
