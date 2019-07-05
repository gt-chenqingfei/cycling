package com.beastbikes.framework.ui.android.lib.view.search;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.TextView;

import com.beastbikes.framework.ui.android.R;


@SuppressLint("InflateParams")
public class DefaultSearchAdapter extends BaseSearchBarAdapter {

    private Context context;

    public DefaultSearchAdapter(Context context, MySearchListener searchListener) {
        super(searchListener);
        this.context = context;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            LayoutInflater inflater = (LayoutInflater) context
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.search_history_item, null);
            holder.tv = (TextView) convertView
                    .findViewById(R.id.history_item_content);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        String string = getStringFromItem(getItem(position)).toString();
        if (isClear(position)) {
            LinearLayout.LayoutParams layoutParams = (LayoutParams) holder.tv
                    .getLayoutParams();
            layoutParams.gravity = Gravity.CENTER;
            holder.tv.setGravity(Gravity.CENTER);
            holder.tv.setLayoutParams(layoutParams);
        } else {
            LinearLayout.LayoutParams layoutParams = (LayoutParams) holder.tv
                    .getLayoutParams();
            layoutParams.gravity = Gravity.CENTER_VERTICAL;
            holder.tv.setLayoutParams(layoutParams);
            holder.tv.setGravity(Gravity.LEFT | Gravity.CENTER_VERTICAL);

        }
        holder.tv.setText(string);

        return convertView;
    }

    static class ViewHolder {
        TextView tv;
    }
}
