package com.beastbikes.android.modules.user.ui.binding;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.user.biz.AccountBindManager;
import com.beastbikes.android.modules.user.dto.AccountDTO;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import java.util.List;

@LayoutResource(R.layout.activity_set_password)
@MenuResource(R.menu.activity_set_passward)
public class SetPasswordActivity extends SessionFragmentActivity {
    @IdResource(R.id.activity_set_password_prompt)
    private TextView prompt;

    @IdResource(R.id.activity_set_password_content)
    private TextView content;

    @IdResource(R.id.activity_set_password_first)
    private EditText first;

    @IdResource(R.id.activity_set_password_second)
    private EditText second;

    private int type;

    //邮箱设置密码模式
    public static final int EXTRA_MAIL =0x123;

    //手机设置密码模式
    public static final int EXTRA_PHONE =0x124;

    //类型
    public static final String TYPE = "type";

    public static final String AUTHKEY = "authKey";

    private String authKey;

    private AccountBindManager accountBindManager;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(getString(R.string.activity_bound_phone_password_title));
        }
        accountBindManager = new AccountBindManager(this);
        init_Intent();
        init_view();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        switch (id){
            case R.id.activity_set_passward_commit:
                if (getPassWard()==null)
                    break;
                final LoadingDialog dialog = new LoadingDialog(this,
                        getString(R.string.loading_msg), false);
                if (dialog != null)
                    dialog.show();
                getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<AccountDTO>>() {
                    @Override
                    protected List<AccountDTO> doInBackground(Void... params) {
                        try {
                            return accountBindManager.bindResetPassword(authKey,getPassWard(),2);
                        } catch (BusinessException e) {
                            if (dialog!=null&&dialog.isShowing())
                                dialog.dismiss();
                            e.printStackTrace();
                        }
                        return null;
                    }

                    @Override
                    protected void onPostExecute(List<AccountDTO> accountDTOs) {
                        super.onPostExecute(accountDTOs);
                        if (dialog !=null&& dialog.isShowing())
                            dialog.dismiss();
                        if (accountDTOs==null)
                            return;
                       finish();
                    }
                });

                break;
        }
        return super.onOptionsItemSelected(item);
    }

    private void init_Intent() {
        Intent intent=getIntent();
        type = intent.getIntExtra(TYPE,0);
        authKey = intent.getStringExtra(AUTHKEY);
    }

    private void init_view(){
        switch (type){
            case EXTRA_MAIL :
                prompt.setText(getString(R.string.activity_bound_mail_password_str));
                break;
            case EXTRA_PHONE :
                prompt.setText(getString(R.string.activity_bound_phone_password_str));
                break;
        }
        setPhone(authKey);
    }
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }



    private void setPhone(String contentStr){
        content.setText(contentStr);
    }

    private String getPassWard(){
        String firstStr = first.getText().toString();
        String secondStr = second.getText().toString();
        if ((firstStr.length()>16&&firstStr.length()<6)||
                (secondStr.length()>16&&secondStr.length()<6)|| TextUtils.isEmpty(firstStr)||
                TextUtils.isEmpty(secondStr)){
            Toasts.showOnUiThread(this,
                    getString(R.string.activity_bound_phone_password_illegal_length_str));
            return null;
        }else if (!firstStr.equals(secondStr)){
            Toasts.showOnUiThread(this,
                    getString(R.string.activity_bound_phone_password_illegal_change_str));
            return null;
        }

        return firstStr;
    }


}
