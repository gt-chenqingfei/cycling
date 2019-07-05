package com.beastbikes.android.widget.helper;

import android.support.v7.widget.RecyclerView;

/**
 * Created by icedan on 16/1/7.
 */
public interface ItemClickListener {

    public void OnItemClick(RecyclerView.ViewHolder viewHolder, int position);

    public void OnItemLongClick(RecyclerView.ViewHolder viewHolder, int position);
}
