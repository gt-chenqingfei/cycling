package com.beastbikes.android.modules.user.filter.adapter;

import java.util.List;

import jp.co.cyberagent.android.gpuimage.GPUImageView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.user.filter.models.FilterItem;
import com.beastbikes.android.modules.user.filter.utils.FilterTools;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.ui.android.utils.ViewHolder;

public class FilterAdapter extends BaseAdapter {

    private Context context;
    private List<FilterItem> filters;
    private Uri uri;

    public FilterAdapter(Context context, List<FilterItem> filters, Uri uri) {
        this.context = context;
        this.filters = filters;
        this.uri = uri;
    }

    @Override
    public int getCount() {
        return this.filters.size();
    }

    @Override
    public Object getItem(int position) {
        return this.filters.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint({"InflateParams"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        final WatermarkViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.activity_watermark_gallery_item_filter, null);
            holder = new WatermarkViewHolder(convertView);
            convertView.setTag(holder);
        } else {
            holder = (WatermarkViewHolder) convertView.getTag();
        }

        FilterItem data = (FilterItem) getItem(position);
        holder.bind(data);

        return convertView;

    }

    class WatermarkViewHolder extends ViewHolder<FilterItem> {

        @IdResource(R.id.activity_watermark_gallery_lv_item_filter_img)
        private GPUImageView image;

        @IdResource(R.id.activity_watermark_gallery_lv_item_filter_name)
        private TextView name;


        public WatermarkViewHolder(View convertView) {
            super(convertView);
        }

        @Override
        public void bind(FilterItem data) {
            this.image.deleteImage();
            this.image.setImage(uri);
            this.image.setFilter(FilterTools.createFilterForType(context, data.getType()));
            this.image.requestRender();
            this.name.setText(data.getFilterName());
        }

    }

}
