package com.beastbikes.android.ble.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.WindowManager;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.ble.dto.FirstMaintenanceDTO;

public class FirstMaintenanceDialog extends Dialog {

    private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7;
    private FirstMaintenanceDTO firstMaintenanceDTO;

    public FirstMaintenanceDialog(Context context, FirstMaintenanceDTO dto) {
        super(context, R.style.dialog_ble);
        getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
//        getWindow().setLayout(context.getFrameLayout.LayoutParams.WRAP_CONTENT);
        this.firstMaintenanceDTO = dto;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_first_maintenance);
        this.initView();
        this.fillView();
    }

    private void initView() {

        tv1 = (TextView) findViewById(R.id.first_maintenance_vale1);
        tv2 = (TextView) findViewById(R.id.first_maintenance_vale2);
        tv3 = (TextView) findViewById(R.id.first_maintenance_vale3);
        tv4 = (TextView) findViewById(R.id.first_maintenance_vale4);
        tv5 = (TextView) findViewById(R.id.first_maintenance_vale5);
        tv6 = (TextView) findViewById(R.id.first_maintenance_vale6);
        tv7 = (TextView) findViewById(R.id.first_maintenance_vale7);
    }


    private void fillView() {
        if (firstMaintenanceDTO != null) {
            this.tv1.setText(firstMaintenanceDTO.getName());
            this.tv2.setText(firstMaintenanceDTO.getStatus() == FirstMaintenanceDTO.STATUS_UN_USE ?
                    R.string.route_self_activity_un_used : R.string.route_self_activity_used);
            this.tv3.setText(firstMaintenanceDTO.getRedeemCode());
            this.tv4.setText(firstMaintenanceDTO.getDescription());
            this.tv5.setText(getContext().getString(R.string.expire_at) + firstMaintenanceDTO.getExpireAt());
            this.tv6.setText(firstMaintenanceDTO.getTitle());
            this.tv7.setText(firstMaintenanceDTO.getDesc());
            if (firstMaintenanceDTO.getDuration() > 0) {
                this.tv7.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        FirstMaintenanceDialog.this.dismiss();
                    }
                }, firstMaintenanceDTO.getDuration() * 1000);
            }
        }
    }
}
