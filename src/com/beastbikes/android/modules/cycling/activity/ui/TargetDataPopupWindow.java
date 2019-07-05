package com.beastbikes.android.modules.cycling.activity.ui;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;

import com.beastbikes.android.R;
import com.beastbikes.android.widget.NumberTextView;
import com.beastbikes.framework.android.utils.DimensionUtils;
import com.beastbikes.framework.ui.android.utils.ViewHolder;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by icedan on 16/1/11.
 */
public class TargetDataPopupWindow extends PopupWindow implements AdapterView.OnItemClickListener {

    private Activity context;
    private LayoutInflater inflater;
    private ListView listView;
    private OnSelectListener selectListener;
    private DataAdapter adapter;

    public interface OnSelectListener {
        void selected(int value);
    }

    public TargetDataPopupWindow(Context context, AttributeSet attribute) {
        super(context, attribute);
    }

    public TargetDataPopupWindow(Activity context, OnSelectListener listener) {
        super(context);
        this.selectListener = listener;
        this.context = context;
        inflater = context.getLayoutInflater();
        ViewGroup menuView = (ViewGroup) inflater.inflate(R.layout.target_data_popup_window, null);
        this.listView = (ListView) menuView.findViewById(R.id.target_data_popup_list);

        // 设置SelectPicPopupWindow的View
        this.setContentView(menuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(DimensionUtils.dip2px(context, 160));
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(DimensionUtils.dip2px(context, 200));
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.WindowAnim);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);

        this.setOutsideTouchable(true);
        this.adapter = new DataAdapter();
        this.listView.setAdapter(this.adapter);
        this.listView.setOnItemClickListener(this);

    }

    @Override
    public void dismiss() {
        super.dismiss();
        this.setBackgroundAlpha(1.0f);
    }

    @Override
    public void showAtLocation(View parent, int gravity, int x, int y) {
        this.setBackgroundAlpha(0.5f);
        super.showAtLocation(parent, gravity, x, y);
    }

    @Override
    public void showAsDropDown(View anchor, int xoff, int yoff, int gravity) {
        super.showAsDropDown(anchor, xoff, yoff, gravity);
        this.setBackgroundAlpha(0.5f);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != this.selectListener) {
            selectListener.selected((Integer) this.adapter.getItem(position));
        }
        dismiss();
    }

    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = this.context.getWindow().getAttributes();
        lp.alpha = alpha; //0.0-1.0
        this.context.getWindow().setAttributes(lp);
    }

    class DataAdapter extends BaseAdapter {

        private List<Integer> list = new ArrayList<>();

        public DataAdapter() {
            for (int i = 1; i <= 1000 / 50; i++) {
                list.add(i * 50);
            }
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
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final DataViewHolder vh;
            if (convertView == null) {
                convertView = inflater.inflate(
                        R.layout.target_data_popup_list_item, null);
                vh = new DataViewHolder(convertView);
            } else {
                vh = (DataViewHolder) convertView.getTag();
            }

            vh.bind(this.list.get(position));
            return convertView;
        }
    }

    private final class DataViewHolder extends ViewHolder<Integer> {

        private NumberTextView valueTv;

        public DataViewHolder(View v) {
            super(v);
            this.valueTv = (NumberTextView) v.findViewById(R.id.target_data_popup_item_value);
        }

        @Override
        public void bind(Integer integer) {
            this.valueTv.setText(String.valueOf(integer));
        }
    }
}
