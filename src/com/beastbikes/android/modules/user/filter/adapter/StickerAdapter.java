package com.beastbikes.android.modules.user.filter.adapter;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.user.filter.models.StickerItem;
import com.beastbikes.framework.ui.android.utils.ViewHolder;

public class StickerAdapter extends BaseAdapter {

    private Context context;
    private List<StickerItem> list;

    public StickerAdapter(Context context, List<StickerItem> list) {
        this.context = context;
        this.list = list;
    }

    @Override
    public int getCount() {
        return this.list.size();
    }

    @Override
    public Object getItem(int position) {
        return this.list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final StickerViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.activity_watermark_gallery_item_sticker, null);
            holder = new StickerViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (StickerViewHolder) convertView.getTag();
        }

        StickerItem data = (StickerItem) getItem(position);
        holder.bind(data);

        return convertView;
    }

    class StickerViewHolder extends ViewHolder<StickerItem> {

        //		@IdResource(R.id.activity_watermark_gallery_lv_item_sticker_img)
        private ImageView image;

        protected StickerViewHolder(View convertView) {
            super(convertView);
        }

        @Override
        public void bind(StickerItem data) {
            this.image.setImageDrawable(data.getDrawable());
        }
    }
}
