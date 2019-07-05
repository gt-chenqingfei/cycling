package com.beastbikes.android.ble.ui;

import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.baidu.mapapi.map.BaiduMap;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

@LayoutResource(R.layout.activity_start_navigation)
public class StartNavigationActivity extends SessionFragmentActivity implements View.OnClickListener{
    @IdResource(R.id.activity_start_navigation_unselected)
    private View unselected;

    @IdResource(R.id.activity_start_navigation_selected)
    private View selected;

    @IdResource(R.id.activity_start_navigation_search)
    private View search;

    @IdResource(R.id.activity_start_navigation_delete)
    private View delete;

    @IdResource(R.id.activity_start_navigation_start)
    private View start;

    @IdResource(R.id.activity_start_navigation_time)
    private TextView time;

    @IdResource(R.id.activity_start_navigation_distance)
    private TextView distance;

    @IdResource(R.id.activity_start_navigation_map)
    private BaiduMap baiduMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        changetounselected();
        search.setOnClickListener(this);
        delete.setOnClickListener(this);
        start.setOnClickListener(this);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
    }
    public void finish() {
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
        super.finish();
    }

    //没有选择路线的时候
    private void changetounselected(){
        unselected.setVisibility(View.VISIBLE);
        selected.setVisibility(View.GONE);
    }

    //选择路线的时候
    private void changetoselected(){
        unselected.setVisibility(View.GONE);
        selected.setVisibility(View.VISIBLE);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.activity_start_navigation_search:
                //点击后开始搜索
                changetoselected();

                break;

            case R.id.activity_start_navigation_delete:
                //点击清理
                changetoselected();
                break;

            case R.id.activity_start_navigation_start:
                //点击开始导航
                changetounselected();
                break;
        }
    }
}
