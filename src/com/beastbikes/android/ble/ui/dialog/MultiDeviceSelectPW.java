package com.beastbikes.android.ble.ui.dialog;

import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Rect;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBar;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager.LayoutParams;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.ble.biz.BleManager;
import com.beastbikes.android.ble.dao.entity.BleDevice;
import com.beastbikes.android.modules.SessionFragmentActivity;

import java.util.ArrayList;
import java.util.List;

public class MultiDeviceSelectPW extends PopupWindow implements AdapterView.OnItemClickListener {

    private ListView listView;
    private LayoutInflater inflater;
    private SessionFragmentActivity context;
    private MenuAdapter adapter;
    private List<BleDevice> bleDevices = new ArrayList<>();
    private OnItemClickListener itemClickListener;
    private String connectedMacAddress = null;

    public MultiDeviceSelectPW(SessionFragmentActivity context,
                               OnItemClickListener itemClickListener) {

        this.context = context;
        this.itemClickListener = itemClickListener;

        this.inflater = context.getLayoutInflater();
        ViewGroup menuView = (ViewGroup) inflater.inflate(R.layout.speedforce_popup_menu, null);
        this.listView = (ListView) menuView.findViewById(R.id.speedforce_popup_menu_lv);

        this.setContentView(menuView);
        Resources r = context.getResources();
        float pxWidth = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 200, r.getDisplayMetrics());
        this.setWidth((int) pxWidth);
        this.setHeight(LayoutParams.WRAP_CONTENT);
        this.setFocusable(true);
//        this.setAnimationStyle(R.style.animationPopup);
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        this.setBackgroundDrawable(dw);

        this.setOutsideTouchable(true);

    }

    public void show(View view, String connectedMacAddress, List<BleDevice> bleDevices) {
        this.bleDevices = bleDevices;
        this.connectedMacAddress = connectedMacAddress;
        this.adapter = new MenuAdapter();
        this.listView.setAdapter(adapter);
        this.listView.setOnItemClickListener(this);

        Rect rect = locateView(view);
        this.showAtLocation((View) listView.getParent(), Gravity.CENTER_HORIZONTAL | Gravity.TOP, 0,
                (rect.bottom - 10));
    }

    public static Rect locateView(View v) {
        int[] loc_int = new int[2];
        if (v == null) return null;
        try {
            v.getLocationOnScreen(loc_int);
        } catch (NullPointerException npe) {
            //Happens when the view doesn't exist on screen anymore.
            return null;
        }
        Rect location = new Rect();
        location.left = loc_int[0];
        location.top = loc_int[1];
        location.right = location.left + v.getWidth();
        location.bottom = location.top + v.getHeight();
        return location;
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (this.itemClickListener != null) {
            BleDevice item = (BleDevice) adapter.getItem(position);
            if (item != null) {
                this.itemClickListener.onItemClick(item);
            }
        }
        dismiss();
    }

    class MenuAdapter extends BaseAdapter {

        @Override
        public int getCount() {
            return bleDevices.size();
        }

        @Override
        public Object getItem(int position) {
            return bleDevices.get(position);
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.speedforce_popup_menu_item, null);
                TextView tvTitle = (TextView) convertView.findViewById(R.id.menu_item_title);
                ImageView icon = (ImageView) convertView.findViewById(R.id.menu_item_icon_right);
                BleDevice device = (BleDevice) getItem(position);
                if (device != null) {
                    tvTitle.setText(device.getDeviceName());
                    icon.setVisibility((TextUtils.equals(device.getMacAddress(), connectedMacAddress)
                    ) ? View.VISIBLE : View.INVISIBLE);
                }
            }

            return convertView;
        }
    }

    public interface OnItemClickListener {
        void onItemClick(BleDevice item);
    }
}
