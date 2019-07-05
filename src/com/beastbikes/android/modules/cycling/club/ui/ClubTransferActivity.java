package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.view.View;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

/**
 * Created by chenqingfei on 16/5/23.
 */

@LayoutResource(R.layout.club_transfer_activity)
public class ClubTransferActivity extends SessionFragmentActivity implements View.OnClickListener {

    @IdResource(R.id.club_transfer_activity_cancel_transfer)
    private TextView btnCancelTransfer;

    @IdResource(R.id.club_transfer_activity_tip_transfer)
    private TextView btnTipTransfer;

    private ClubManager clubManager;

    private LoadingDialog loadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        btnCancelTransfer.setOnClickListener(this);
        btnTipTransfer.setOnClickListener(this);
        clubManager = new ClubManager(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.club_transfer_activity_cancel_transfer:
                doCancelTransfer();
                break;
            case R.id.club_transfer_activity_tip_transfer:
                doTipTransfer();
                break;
        }
    }

    private void doCancelTransfer() {
        final ClubTransferActivity ctx = ClubTransferActivity.this;
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = new LoadingDialog(ctx, getString(R.string.club_info_waiting), true);
                loadingDialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return clubManager.cancelClubTrans();
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
                    Toasts.show(ctx, R.string.club_transfer_activity_cancel_transfer_ok);
                    ctx.finish();
                } else {
                    Toasts.show(ctx, R.string.club_transfer_activity_cancel_transfer_fail);
                }
            }
        });
    }

    private void doTipTransfer() {
        final ClubTransferActivity ctx = ClubTransferActivity.this;
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, Boolean>() {

            @Override
            protected void onPreExecute() {
                super.onPreExecute();
                loadingDialog = new LoadingDialog(ctx, getString(R.string.club_info_waiting), true);
                loadingDialog.show();
            }

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    return clubManager.sendClubTransNotify();
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
                    Toasts.show(ctx, R.string.club_transfer_activity_tip_transfer_ok);
                }
            }
        });
    }
}
