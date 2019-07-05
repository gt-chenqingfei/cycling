package com.beastbikes.android.modules.cycling.ranking.ui.widget;

import android.app.Activity;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.ui.GeoCodeMessage;
import com.beastbikes.android.home.HomeActivity;
import com.beastbikes.android.utils.RxBus;


/**
 * Created by zhangyao on 2016/3/9.
 */
public class RankMenuPopupWindow extends PopupWindow implements View.OnClickListener {

    private TextView drop_down_tv;
    private String geoCode;
    private Activity activity;

    public static RankMenuPopupWindow createPopupWindow(Activity context,
                                                        TextView drop_down_tv, String geoCode) {
        return new RankMenuPopupWindow(context, drop_down_tv, geoCode);
    }

    private RankMenuPopupWindow(Activity context, TextView drop_down_tv, String geoCode) {
        this.drop_down_tv = drop_down_tv;
        this.geoCode = geoCode;
        this.activity = context;

        View view = LayoutInflater.from(context).
                inflate(R.layout.rank_fragment_view, null);
        DisplayMetrics dm = new DisplayMetrics();
        context.getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;    //得到宽度
        int height = dm.heightPixels;  //得到高度
        this.setWidth(width / 4);
        this.setHeight(height / 6);

        view.findViewById(R.id.rank_fragment_view_net).setOnClickListener(this);
        view.findViewById(R.id.rank_fragment_view_country).setOnClickListener(this);
        view.findViewById(R.id.rank_fragment_view_province).setOnClickListener(this);

        this.setContentView(view);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        // 设置SelectPicPopupWindow弹出窗体动画效果
        this.setAnimationStyle(R.style.WindowAnim);
        // 实例化一个ColorDrawable颜色为半透明
        ColorDrawable dw = new ColorDrawable(Color.TRANSPARENT);
        // 设置SelectPicPopupWindow弹出窗体的背景
        this.setBackgroundDrawable(dw);
        this.setOutsideTouchable(true);
        this.setBackgroundAlpha(0.5f);
    }

    @Override
    public void onClick(View v) {
        String[] geoCodes = geoCode.split("\\.");
        RxBus rxBus = RxBus.getDefault();
        switch (v.getId()) {
            case R.id.rank_fragment_view_net:
                HomeActivity.REGION_RANK_CODE = Constants.RANK_REGION_NET_CODE;
                drop_down_tv.setText(R.string.ranking_fragment_menu_safety_net);
                rxBus.post(new GeoCodeMessage(""));
                break;
            case R.id.rank_fragment_view_country:
                HomeActivity.REGION_RANK_CODE = Constants.RANK_REGION_COUNTRY;
                drop_down_tv.setText(R.string.ranking_fragment_menu_whole_country);
                if (geoCodes.length >= 1) {
                    rxBus.post(new GeoCodeMessage(geoCode.split("\\.")[0]));
                }
                break;
            case R.id.rank_fragment_view_province:
                HomeActivity.REGION_RANK_CODE = Constants.RANK_REGION_AREA_CODE;
                drop_down_tv.setText(R.string.ranking_fragment_menu_whole_area);
                if (geoCodes.length >= 2) {
                    rxBus.post(new GeoCodeMessage(geoCode.split("\\.")[0] + "."
                            + geoCode.split("\\.")[1]));
                }
                break;
        }
        this.dismiss();
    }

    @Override
    public void dismiss() {
        super.dismiss();
        this.setBackgroundAlpha(1f);
    }

    private void setBackgroundAlpha(float alpha) {
        WindowManager.LayoutParams lp = activity.getWindow().getAttributes();
        lp.alpha = alpha; //0.0-1.0
        activity.getWindow().setAttributes(lp);
    }
}
