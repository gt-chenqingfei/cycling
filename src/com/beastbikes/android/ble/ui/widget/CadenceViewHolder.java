package com.beastbikes.android.ble.ui.widget;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.ble.dto.CadenceDTO;
import com.beastbikes.android.widget.convenientbanner.holder.Holder;

/**
 * Created by icedan on 16/10/10.
 */

public class CadenceViewHolder implements Holder<CadenceDTO>, View.OnClickListener {

    private TextView titleTv;
    private TextView descTv;

    @Override
    public void onClick(View v) {

    }

    @Override
    public View createView(Context context) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_cadence_viewpager_item, null);
        this.titleTv = (TextView) view.findViewById(R.id.cadence_item_title);
        this.descTv = (TextView) view.findViewById(R.id.cadence_item_desc);
        return view;
    }

    @Override
    public void UpdateUI(Context context, int position, CadenceDTO data) {
        if (null != data) {
            this.titleTv.setText(data.getTitle());
            this.descTv.setText(data.getDesc());
        }
    }
}
