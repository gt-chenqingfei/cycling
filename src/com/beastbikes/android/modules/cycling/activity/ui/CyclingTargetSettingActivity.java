package com.beastbikes.android.modules.cycling.activity.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.activity.biz.CyclingManager;
import com.beastbikes.android.modules.cycling.activity.dto.GoalConfigDTO;
import com.beastbikes.android.modules.cycling.activity.dto.MyGoalInfoDTO;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.NumberTextView;
import com.beastbikes.android.widget.helper.ItemClickListener;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by icedan on 16/1/7.
 */
@LayoutResource(R.layout.cycling_target_setting_activity)
public class CyclingTargetSettingActivity extends SessionFragmentActivity implements View.OnClickListener,
        ItemClickListener, TargetDataPopupWindow.OnSelectListener {

    public static final String EXTRA_TARGET_DISTANCE = "target_distance";

    public static final String ACTION_TARGET_DISTANCE = "action.target.distance";

    @IdResource(R.id.cycling_target_setting_close)
    private ImageView closeIv;
    @IdResource(R.id.cycling_target_setting_save)
    private TextView saveTv;
    @IdResource(R.id.cycling_target_config_view)
    private RecyclerView recyclerView;
    @IdResource(R.id.cycling_target_setting_target_value)
    private NumberTextView targetDistanceTv;
    @IdResource(R.id.cycling_target_setting_target_unit)
    private NumberTextView targetUnitTv;
    @IdResource(R.id.cycling_target_setting_select)
    private ImageView selectIv;

    private CyclingManager cyclingManager;
    private RecyclerAdapter adapter;
    private List<GoalConfigDTO> list;
    private TargetDataPopupWindow popupWindow;
    private SharedPreferences userSp;
    private double targetDistance;
    private String unit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        SpeedxAnalytics.onEvent(this, "目标设置","save_ridding_goal");
        this.cyclingManager = new CyclingManager(this);

        this.selectIv.setOnClickListener(this);
        this.closeIv.setOnClickListener(this);
        this.saveTv.setOnClickListener(this);
        this.list = new ArrayList<>();
        this.adapter = new RecyclerAdapter(this, list);
        this.recyclerView.setHasFixedSize(true);
        this.recyclerView.setAdapter(adapter);
        this.recyclerView.setLayoutManager(new LinearLayoutManager(this));

        this.targetDistance = getIntent().getDoubleExtra(EXTRA_TARGET_DISTANCE, 0);
        if (LocaleManager.isDisplayKM(CyclingTargetSettingActivity.this)) {
            this.targetDistanceTv.setText(String.format("%.0f", targetDistance));
            this.unit = "KM";
            this.targetUnitTv.setText(this.unit);
        } else {
            this.targetDistanceTv.setText(String.format("%.0f", LocaleManager.kilometreToMile(targetDistance)));
            this.unit = "MI";
            this.targetUnitTv.setText(this.unit);
        }
        this.userSp = getSharedPreferences(getUserId(), 0);
        this.initTargetSetting();
        this.getGoalConfigs();

        if (userSp.contains(Constants.PREF_CYCLING_MY_GOAL_KEY)) {
            String data = this.userSp.getString(Constants.PREF_CYCLING_MY_GOAL_KEY, "");
            if (!TextUtils.isEmpty(data)) {
                try {
                    JSONObject object = new JSONObject(data);
                    MyGoalInfoDTO result = new MyGoalInfoDTO(object);
                    if (LocaleManager.isDisplayKM(CyclingTargetSettingActivity.this)) {
                        this.targetDistanceTv.setText(String.format("%.0f", result.getMyGoal() / 1000));
                    } else {
                        String myGoal = String.format("%.0f", LocaleManager.kilometreToMile(result.getMyGoal() / 1000));
                        this.targetDistanceTv.setText(myGoal);
                    }
                } catch (Exception e) {
                }
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.cycling_target_setting_select:
                SpeedxAnalytics.onEvent(this, "设定目标","save_ridding_goal");
                if (null == this.popupWindow) {
                    this.popupWindow = new TargetDataPopupWindow(this, this);
                }
                this.popupWindow.showAtLocation(recyclerView, Gravity.CENTER_HORIZONTAL, 0, saveTv.getHeight());
                break;
            case R.id.cycling_target_setting_close:
                finish();
                break;
            case R.id.cycling_target_setting_save:
                SpeedxAnalytics.onEvent(this, "setting_ridding_goal", "setting_ridding_goal");
                for (GoalConfigDTO config : list) {
                    if (config.isChecked()) {
                        this.targetDistance = config.getDistance() / 1000;
                    }
                }
                if (!LocaleManager.isDisplayKM(this)) {
                    this.targetDistance = LocaleManager.mileToKilometre(targetDistance);
                }
                this.setMyGoal(targetDistance);
                break;
        }
    }

    @Override
    public void OnItemClick(RecyclerView.ViewHolder viewHolder, int position) {
        if (null == this.list || this.list.size() <= 0) {
            return;
        }

        switch(position){
            case 0:
                SpeedxAnalytics.onEvent(this, "俱乐部月平均里程", "month_rank_avg_distance");
                break;
            case 1:
                SpeedxAnalytics.onEvent(this, "月榜冠军里程", "setting_speedx_goal_distance");
                break;
            case 2:
                SpeedxAnalytics.onEvent(this, "野兽月平均里程", "setting_speedx_avg_distance");
                break;
        }

        double distance = 0;
        for (int i = 0; i < this.list.size(); i++) {
            GoalConfigDTO dto = this.list.get(i);
            if (i == position) {
                dto.setChecked(true);
                distance = dto.getDistance() / 1000;
            } else {
                dto.setChecked(false);
            }
        }
        this.adapter.notifyDataSetChanged();
        if (!LocaleManager.isDisplayKM(CyclingTargetSettingActivity.this)) {
            targetDistanceTv.setText(String.format("%.0f", LocaleManager.kilometreToMile(distance)));
        } else {
            targetDistanceTv.setText(String.format("%.0f", distance));
        }
    }

    @Override
    public void OnItemLongClick(RecyclerView.ViewHolder viewHolder, int position) {
    }

    @Override
    public void selected(int value) {
        this.targetDistanceTv.setText(String.valueOf(value));
        for (GoalConfigDTO config : list) {
            config.setChecked(false);
        }
        this.adapter.notifyDataSetChanged();
        this.targetDistance = value;
    }

    private void initTargetSetting() {
        if (!userSp.contains(Constants.PREF_CYCLING_TARGET_SETTING_KEY)) {
            userSp.edit().putString(Constants.PREF_CYCLING_TARGET_SETTING_KEY,
                    Constants.SPEEDX_MONTHLY_SVG_DISTANCE).commit();
        }
    }

    /**
     * 获取目标配置列表
     */
    private void getGoalConfigs() {
        this.getAsyncTaskQueue().add(new AsyncTask<Void, Void, List<GoalConfigDTO>>() {
            @Override
            protected List<GoalConfigDTO> doInBackground(Void... params) {
                return cyclingManager.getGoalConfigs();
            }

            @Override
            protected void onPostExecute(List<GoalConfigDTO> goalConfigDTOs) {
                if (null == goalConfigDTOs || goalConfigDTOs.size() <= 0) {
                    return;
                }

                if (null == list) {
                    list = new ArrayList<>();
                }
                list.clear();
                list.addAll(goalConfigDTOs);
                adapter.notifyDataSetChanged();
            }
        });
    }

    /**
     * 设置目标
     *
     * @param distance 目标里程
     */
    private void setMyGoal(final double distance) {
        this.getAsyncTaskQueue().add(new AsyncTask<Double, Void, Boolean>() {
            @Override
            protected Boolean doInBackground(Double... params) {
                return cyclingManager.setMyGoal(distance * 1000);
            }

            @Override
            protected void onPostExecute(Boolean result) {
                if (result) {
                    String key = String.valueOf(distance);
                    if (null != list && list.size() > 0) {
                        for (GoalConfigDTO dto : list) {
                            if (dto.isChecked()) {
                                key = dto.getKey();
                            }
                        }
                    }
                    userSp.edit().putString(Constants.PREF_CYCLING_TARGET_SETTING_KEY,
                            key).commit();
                    String muGoal = userSp.getString(Constants.PREF_CYCLING_MY_GOAL_KEY, "");
                    try {
                        JSONObject json = new JSONObject(muGoal);
                        json.put("myGoal", distance * 1000);
                        userSp.edit().putString(Constants.PREF_CYCLING_MY_GOAL_KEY, json.toString()).commit();
                    } catch (Exception e) {
                    }
                    Intent intent = new Intent(ACTION_TARGET_DISTANCE);
                    sendBroadcast(intent);
                    finish();
                }
            }
        });
    }

    private final class RecyclerAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        private final List<GoalConfigDTO> configs;
        private final ItemClickListener itemClickListener;


        public RecyclerAdapter(ItemClickListener listener, List<GoalConfigDTO> configs) {
            this.configs = configs;
            this.itemClickListener = listener;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.cycling_target_config_item,
                    parent, false);
            final ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            return itemViewHolder;
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder holder, final int position) {
            final GoalConfigDTO config = this.configs.get(position);
            if (null == config) {
                return;
            }

            holder.titleTv.setText(config.getTitle());
            String subTitle = config.getSubTitle();
            if (!TextUtils.isEmpty(subTitle)) {
                holder.subTitleTv.setVisibility(View.VISIBLE);
                holder.subTitleTv.setText(config.getSubTitle());
            } else {
                holder.subTitleTv.setVisibility(View.GONE);
            }


            final double distance = config.getDistance() / 1000;
            if (!LocaleManager.isDisplayKM(CyclingTargetSettingActivity.this)) {
                holder.distanceTv.setText(String.format("%.0f", LocaleManager.kilometreToMile(distance))
                        + unit);
            } else {
                holder.distanceTv.setText(String.format("%.0f", distance) + unit);
            }

            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.OnItemClick(holder, position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    itemClickListener.OnItemLongClick(holder, position);
                    return true;
                }
            });

            if (config.isChecked()) {
                holder.checkBox.setVisibility(View.VISIBLE);
            } else {
                holder.checkBox.setVisibility(View.INVISIBLE);
            }
        }

        @Override
        public int getItemCount() {
            return this.configs.size();
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public final TextView titleTv;
        public final TextView subTitleTv;
        public final ImageView checkBox;
        public final NumberTextView distanceTv;
        public final View itemView;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.titleTv = (TextView) itemView.findViewById(R.id.cycling_target_config_item_title);
            this.subTitleTv = (TextView) itemView.findViewById(R.id.cycling_target_config_item_subtitle);
            this.checkBox = (ImageView) itemView.findViewById(R.id.cycling_target_config_item_check);
            this.distanceTv = (NumberTextView) itemView.findViewById(R.id.cycling_target_config_item_distance);
        }
    }
}
