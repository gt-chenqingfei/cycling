package com.beastbikes.android.ble.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beastbikes.android.R;

public class BleConnectTipDialog extends Dialog implements View.OnClickListener {
    private TextView tvTitle;
    private TextView tvMessage;
    private ImageView ivImage;
    private boolean isWholeBike;

    public BleConnectTipDialog(Context context, boolean isWholeBike) {
        super(context, R.style.dialog_ble);
        this.isWholeBike = isWholeBike;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.setContentView(R.layout.dialog_ble_connect_tip);
        this.setCanceledOnTouchOutside(false);
        this.initView();
    }

    private void initView() {
        findViewById(R.id.dialog_ble_connect_ok).setOnClickListener(this);
        this.tvTitle = (TextView) findViewById(R.id.dialog_ble_connect_title);
        this.tvMessage = (TextView) findViewById(R.id.dialog_ble_connect_message);
        this.ivImage = (ImageView) findViewById(R.id.dialog_ble_connect_image);

        if(isWholeBike){
            this.tvTitle.setText(R.string.dialog_ble_connect_speedx_title);
            this.tvMessage.setText(R.string.dialog_ble_connect_speedx_message);
            this.ivImage.setImageResource(R.drawable.ic_ble_speedx);
        }
        else{
            this.tvTitle.setText(R.string.dialog_ble_connect_speedforce_title);
            this.tvMessage.setText(R.string.dialog_ble_connect_speedforce_message);
            this.ivImage.setImageResource(R.drawable.ic_ble_speedforce);
        }
    }

    @Override
    public void onClick(View v) {
        this.dismiss();
    }

}
