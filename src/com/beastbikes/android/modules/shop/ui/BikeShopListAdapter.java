package com.beastbikes.android.modules.shop.ui;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.shop.dto.BikeShopListDTO;
import com.beastbikes.framework.ui.android.lib.list.BaseListAdapter;

@SuppressLint("InflateParams")
public class BikeShopListAdapter extends BaseListAdapter<BikeShopListDTO> {
    private String type ;
    public BikeShopListAdapter(Handler handler, AbsListView listView,String type) {
        super(handler, listView);
        this.type = type;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final BikeShopListViewHolder vh;
        if (convertView == null) {
            convertView = LayoutInflater.from(parent.getContext()).inflate(
                    R.layout.bike_shop_listitem, null);
            vh = new BikeShopListViewHolder(convertView,type);
        } else {
            vh = (BikeShopListViewHolder) convertView.getTag();
        }
        vh.bind(getItem(position));
        return convertView;
    }

    @Override
    protected void recycleView(View view) {

    }
}
