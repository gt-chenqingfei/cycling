package com.beastbikes.android.ble.ui;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.search.core.PoiInfo;
import com.beastbikes.android.R;

import java.util.List;

/**
 * Created by zhangyao on 2016/3/2.
 */
public class SearchAdpter extends RecyclerView.Adapter<SearchAdpter.MyViewHolder>{

    private List<PoiInfo> poiInfos;
    private Activity context;

    public SearchAdpter(List<PoiInfo> poiInfos,Activity context){
        this.poiInfos = poiInfos;
        this.context = context;
    }
    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater
                .from(parent.getContext())
                .inflate(R.layout.activity_search_location_item, parent, false);
        return new MyViewHolder(v);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        final PoiInfo poiInfo = poiInfos.get(position);
        holder.setAddress(poiInfo.address);
        holder.setName(poiInfo.name);
        if (poiInfo.type == PoiInfo.POITYPE.BUS_LINE|| poiInfo.type == PoiInfo.POITYPE.BUS_STATION){
            holder.setType(context.getString(R.string.activity_search_location_item_type_bus));
        }
        String typeStr = "";
        switch (poiInfo.type){
            case BUS_STATION:
                typeStr = context.getString(R.string.activity_search_location_item_type_bus);
                break;
            case BUS_LINE:
                typeStr = context.getString(R.string.activity_search_location_item_type_bus_line);
                break;
            case SUBWAY_STATION:
                typeStr = context.getString(R.string.activity_search_location_item_type_subway);
                break;
            case SUBWAY_LINE:
                typeStr = context.getString(R.string.activity_search_location_item_type_subway_line);
                break;
        }
        holder.setType(typeStr);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                context.finish();
            }
        });
    }

    @Override
    public int getItemCount() {
        return poiInfos.size();
    }

    public class MyViewHolder extends RecyclerView.ViewHolder {
        private TextView name;
        private TextView address;
        private TextView type;
        public MyViewHolder(View itemView) {
            super(itemView);
            name = (TextView) itemView.findViewById(R.id.activity_search_location_item_name);
            address = (TextView) itemView.findViewById(R.id.activity_search_location_item_address);
            type = (TextView) itemView.findViewById(R.id.activity_search_location_item_type);
        }
        public void setName(String nameStr){
            if (TextUtils.isEmpty(nameStr)) return;
            name.setText(nameStr);

        }
        public void setType(String typeStr){
            type.setText(typeStr);
        }

        public void setAddress(String addressStr){
            if (TextUtils.isEmpty(addressStr)) return;
            address.setText(addressStr);
        }
    }
}
