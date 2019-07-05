package com.beastbikes.android.modules.cycling.club.ui.widget;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.utils.FileUtil;
import com.beastbikes.framework.ui.android.utils.Toasts;

/**
 * Created by icedan on 15/12/16.
 */
public class SaveImagePopupWindow extends PopupWindow implements AdapterView.OnItemClickListener {

    private ListView listView;
    private LayoutInflater inflater;
    private String imageUrl;
    private Uri uri;
    private SelectSaveListener listener;
    private Activity context;

    public SaveImagePopupWindow(Activity context, String imageUrl) {
        this.context = context;
        this.imageUrl = imageUrl;
        inflater = context.getLayoutInflater();
        ViewGroup menuView = (ViewGroup) inflater.inflate(R.layout.save_image_popup_window, null);
        this.listView = (ListView) menuView.findViewById(R.id.save_image_list_view);
        this.listView.setOnItemClickListener(this);

        // 设置SelectPicPopupWindow的View
        this.setContentView(menuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.WindowAnim);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        this.setOutsideTouchable(true);
        this.listView.setAdapter(new MenuAdapter());
        this.listView.setOnItemClickListener(this);

        this.setBackgroundAlpha(0.5f);
    }

    public SaveImagePopupWindow(Activity context, Uri uri,SelectSaveListener listener) {
        this.listener = listener;
        this.context = context;
        this.uri = uri;
        inflater = context.getLayoutInflater();
        ViewGroup menuView = (ViewGroup) inflater.inflate(R.layout.save_image_popup_window, null);
        this.listView = (ListView) menuView.findViewById(R.id.save_image_list_view);
        this.listView.setOnItemClickListener(this);

        // 设置SelectPicPopupWindow的View
        this.setContentView(menuView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(WindowManager.LayoutParams.WRAP_CONTENT);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.WindowAnim);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        this.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_RESIZE);

        this.setOutsideTouchable(true);
        this.listView.setAdapter(new MenuAdapter());
        this.listView.setOnItemClickListener(this);

        this.setBackgroundAlpha(0.5f);
    }

    public void setUri(Uri uri){
        this.uri = uri;
    }

    @Override
    public void dismiss() {
        super.dismiss();
        this.setBackgroundAlpha(1.0f);
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        switch (position) {
            case 0:
                if(listener == null) {
                    this.saveImage(imageUrl);
                }
                else{
                    this.listener.onSelected(uri);
                }
                break;
        }
        dismiss();
    }

    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = this.context.getWindow().getAttributes();
        lp.alpha = alpha; //0.0-1.0
        this.context.getWindow().setAttributes(lp);
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    class MenuAdapter extends BaseAdapter {
        private int[] menuContent = new int[]{R.string.save_image_label, R.string.cancel};

        @Override
        public int getCount() {
            return menuContent.length;
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            if (convertView == null) {
                convertView = inflater.inflate(
                        R.layout.save_image_popup_window_item, null);
                TextView textView = (TextView) convertView.findViewById(R.id.save_image_list_view_item);
                int content = menuContent[position];
                textView.setText(content);

            }
            return convertView;
        }
    }

    private void saveImage(final String imageUrl) {
        new AsyncTask<String, Void, String>() {

            @Override
            protected String doInBackground(String... params) {
                return FileUtil.saveImage(imageUrl, context);
            }

            @Override
            protected void onPostExecute(String s) {
                if (!TextUtils.isEmpty(s)) {
                    Toasts.show(context, s);
                }
            }
        }.execute();
    }

    public interface SelectSaveListener{
        public void onSelected(Uri uri);
    }

}
