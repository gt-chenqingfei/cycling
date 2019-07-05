package com.beastbikes.android.modules.preferences.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.database.DataSetObserver;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupClickListener;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.baidu.mapapi.map.offline.MKOLSearchRecord;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.baidu.mapapi.map.offline.MKOfflineMapListener;
import com.beastbikes.android.R;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.utils.ConnectivityUtils;
import com.beastbikes.framework.ui.android.BaseFragmentActivity;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.framework.ui.android.utils.ViewHolder;

import java.util.ArrayList;
import java.util.List;

@Alias("离线地图")
@LayoutResource(R.layout.offline_map_setting_activity)
public class OfflineMapSettingActivity extends BaseFragmentActivity implements
        MKOfflineMapListener, OnChildClickListener, OnGroupClickListener {

    private final MKOfflineMap offlineMap = new MKOfflineMap();

    private final DataSetObserver observer = new DataSetObserver() {

        @Override
        public void onChanged() {
            if (null == adapter || null == offlineList)
                return;

            final int n = adapter.getGroupCount();
            for (int i = 0; i < n; i++) {
                offlineList.expandGroup(i, false);
            }
        }

    };

    @IdResource(R.id.offline_map_setting_activity_items)
    private ExpandableListView offlineList;

    private LocalMapAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.offlineMap.init(this);
        this.adapter = new LocalMapAdapter(this);
        this.adapter.registerDataSetObserver(this.observer);
        this.offlineList.setOnChildClickListener(this);
        this.offlineList.setOnGroupClickListener(this);
        this.offlineList.setAdapter(this.adapter);
    }

    protected void onResume() {
        super.onResume();
        this.adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        this.offlineMap.destroy();
        this.adapter.unregisterDataSetObserver(this.observer);
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        if (isAirplaneModeOn(this)) {
            return false;
        }
        final ExpandableListAdapter ela = parent.getExpandableListAdapter();
        final LocalMapItem lmi = (LocalMapItem) ela.getChild(groupPosition,
                childPosition);
        if (null != lmi) {
            lmi.onClick(this.offlineMap, ela);
        }

        AVAnalytics
                .onEvent(
                        this,
                        getString(R.string.offline_map_setting_activity_event_download));
        return true;
    }

    @Override
    public void onGetOfflineMapState(int type, int cityId) {
        switch (type) {
            case MKOfflineMap.TYPE_DOWNLOAD_UPDATE:
            case MKOfflineMap.TYPE_NEW_OFFLINE:
                // 处理下载进度更新提示
                this.adapter.update(this, cityId);
                break;
            case MKOfflineMap.TYPE_VER_UPDATE:
                // 版本更新提示
                this.offlineMap.remove(cityId);
                this.adapter.update(this, cityId);
                break;
        }
    }

    private static final class LocalMapAdapter extends
            BaseExpandableListAdapter {

        private final List<LocalMapCategory> items = new ArrayList<LocalMapCategory>();

        public LocalMapAdapter(OfflineMapSettingActivity activity) {
            this.items.add(new HotCityCategory(activity));
            this.items.add(new OfflineCityCategory(activity));
        }

        public void update(OfflineMapSettingActivity activity, int cityId) {
            final int gn = getGroupCount();

            try {
                for (int i = 0; i < gn; i++) {
                    final int cn = getChildrenCount(i);

                    for (int j = 0; j < cn; j++) {
                        final LocalMapItem lmi = (LocalMapItem) getChild(i, j);

                        if (lmi.isProvince()) {
                            final MKOLSearchRecord sr = lmi.getCityById(cityId);
                            if (null != sr) {
                                lmi.updateProvince();
                                return;
                            }
                        } else if (lmi.record.cityID == cityId) {
                            lmi.updateCity(activity.offlineMap
                                    .getUpdateInfo(cityId));
                            return;
                        }
                    }
                }
            } finally {
                this.notifyDataSetChanged();
            }
        }

        @Override
        public int getGroupCount() {
            return this.items.size();
        }

        @Override
        public int getChildrenCount(int groupPosition) {
            return this.items.get(groupPosition).items.size();
        }

        @Override
        public Object getGroup(int groupPosition) {
            return this.items.get(groupPosition);
        }

        @Override
        public Object getChild(int groupPosition, int childPosition) {
            return this.items.get(groupPosition).items.get(childPosition);
        }

        @Override
        public long getGroupId(int groupPosition) {
            return groupPosition;
        }

        @Override
        public long getChildId(int groupPosition, int childPosition) {
            return childPosition;
        }

        @Override
        public boolean hasStableIds() {
            return false;
        }

        @Override
        public View getGroupView(int groupPosition, boolean isExpanded,
                                 View convertView, ViewGroup parent) {
            final ViewHolder<LocalMapCategory> gvh;

            if (convertView == null) {
                convertView = View
                        .inflate(
                                parent.getContext(),
                                R.layout.offline_map_setting_activity_list_category_item,
                                null);
                gvh = new GroupViewHolder(convertView);
            } else {
                gvh = ViewHolder.as(convertView);
            }

            gvh.bind(this.items.get(groupPosition));

            return convertView;
        }

        @Override
        public View getChildView(int groupPosition, int childPosition,
                                 boolean isLastChild, View convertView, ViewGroup parent) {
            final ViewHolder<LocalMapItem> cvh;

            if (convertView == null) {
                convertView = View.inflate(parent.getContext(),
                        R.layout.offline_map_setting_activity_list_item, null);
                cvh = new ChildViewHolder(convertView);
            } else {
                cvh = ChildViewHolder.as(convertView);
            }

            cvh.bind(this.items.get(groupPosition).items.get(childPosition));

            return convertView;
        }

        @Override
        public boolean isChildSelectable(int groupPosition, int childPosition) {
            return true;
        }

    }

    private static final class GroupViewHolder extends
            ViewHolder<LocalMapCategory> {

        @IdResource(R.id.offline_map_setting_activity_list_category_item_title)
        private TextView title;

        public GroupViewHolder(View v) {
            super(v);
        }

        @Override
        public void bind(LocalMapCategory lmc) {
            this.title.setText(lmc.name);
        }

    }

    private static final class ChildViewHolder extends ViewHolder<LocalMapItem> {

        @SuppressLint("DefaultLocale")
        static String formatName(MKOLSearchRecord sr) {
            if (sr.size < (1024 * 1024)) {
                return String.format("%s (%dK)", sr.cityName, sr.size / 1024);
            } else {
                return String.format("%s (%.1fM)", sr.cityName, sr.size
                        / (1024 * 1024.0));
            }
        }

        @SuppressLint("DefaultLocale")
        static String formatState(MKOLUpdateElement ue) {
            // return String.format("%d%%", ue.size * 100 / ue.serversize);
            return String.valueOf(ue.ratio) + "%";
        }

        @IdResource(R.id.offline_map_setting_activity_list_item_name)
        private TextView name;

        @IdResource(R.id.offline_map_setting_activity_list_item_state)
        private TextView state;

        protected ChildViewHolder(View v) {
            super(v);
        }

        @Override
        public void bind(LocalMapItem lmi) {
            this.name.setText(formatName(lmi.record));
            this.state.setText(lmi.status);
        }

    }

    private static class LocalMapCategory {

        private final String name;
        private final ArrayList<LocalMapItem> items;

        public LocalMapCategory(OfflineMapSettingActivity activity, int resId,
                                ArrayList<MKOLSearchRecord> items) {
            this.name = activity.getString(resId);
            this.items = new ArrayList<LocalMapItem>();

            for (MKOLSearchRecord sr : items) {
                this.items.add(new LocalMapItem(activity, sr));
            }
        }

    }

    private static final class LocalMapItem {
        private final MKOLSearchRecord record;
        private final OfflineMapSettingActivity activity;
        String status;

        public LocalMapItem(OfflineMapSettingActivity activity,
                            MKOLSearchRecord record) {
            this.record = record;
            this.activity = activity;

            int nFinished = 0;
            float totalSize = 0;
            float downloadedSize = 0;

            switch (record.cityType) {
                case 1:
                    for (final MKOLSearchRecord sr : this.record.childCities) {
                        totalSize += sr.size;

                        final MKOLUpdateElement ele = activity.offlineMap
                                .getUpdateInfo(sr.cityID);
                        if (null == ele)
                            continue;

                        downloadedSize += ele.ratio / 100 * sr.size;
                        if (ele.ratio >= 100) {
                            nFinished++;
                        }
                    }

                    if (nFinished >= this.record.childCities.size()) {
                        this.status = activity
                                .getString(R.string.offline_map_setting_activity_already_downloaded);
                    } else if (downloadedSize > 0) {
                        this.status = String.format("%d%%",
                                (int) (downloadedSize * 100 / totalSize));
                    } else {
                        this.status = activity
                                .getString(R.string.offline_map_setting_activity_download);
                    }

                    final MKOLUpdateElement mue = activity.offlineMap
                            .getUpdateInfo(record.cityID);
                    if (null != mue && mue.update) {
                        this.status = activity
                                .getString(R.string.offline_map_setting_activity_already_downloaded);
                    }
                    break;
                default: {
                    if(activity == null)
                        return;
                    final MKOLUpdateElement ele = activity.offlineMap
                            .getUpdateInfo(record.cityID);
                    if (null != ele) {
                        if (ele.ratio >= 100) {
                            this.status = activity
                                    .getString(R.string.offline_map_setting_activity_already_downloaded);
                        } else if (ele.ratio <= 0) {
                            this.status = activity
                                    .getString(R.string.offline_map_setting_activity_download);
                        } else {
                            this.status = String.format("%d%%", ele.ratio);
                        }

                        if (ele.update) {
                            this.status = activity
                                    .getString(R.string.offline_map_setting_activity_already_downloaded);
                        }
                    } else {
                        this.status = activity
                                .getString(R.string.offline_map_setting_activity_download);
                    }
                    break;
                }
            }
        }

        public boolean isProvince() {
            return 1 == this.record.cityType;
        }

        public MKOLSearchRecord getCityById(int cityId) {
            if (!this.isProvince())
                return null;
            for (final MKOLSearchRecord sr : this.record.childCities) {
                if (sr.cityID == cityId)
                    return sr;
            }

            return null;
        }

        public void onClick(MKOfflineMap offlineMap,
                            ExpandableListAdapter adapter) {

            if (this.isProvince()) {
                int nDownloading = 0;
                int nFinished = 0;

                for (final MKOLSearchRecord sr : this.record.childCities) {
                    final MKOLUpdateElement ue = activity.offlineMap
                            .getUpdateInfo(sr.cityID);
                    if (null == ue)
                        continue;

                    if (ue.ratio >= 100)
                        nFinished++;

                    switch (ue.status) {
                        case MKOLUpdateElement.DOWNLOADING:
                        case MKOLUpdateElement.WAITING:
                            nDownloading++;
                            break;
                    }
                }

                if (nFinished >= this.record.childCities.size()) {
                    Toasts.show(
                            activity,
                            R.string.offline_map_setting_activity_already_downloaded);
                    return;
                }

                if (nDownloading > 0) { // 如果有下载的，则暂停
                    Toasts.show(
                            activity,
                            R.string.offline_map_setting_activity_toast_pause_downloading);
                    for (final MKOLSearchRecord sr : this.record.childCities) {
                        this.activity.offlineMap.pause(sr.cityID);
                    }
                } else { // 没有下载，则开始下载
                    final List<Integer> cities = new ArrayList<Integer>();
                    for (final MKOLSearchRecord sr : this.record.childCities) {
                        final MKOLUpdateElement ue = activity.offlineMap
                                .getUpdateInfo(sr.cityID);
                        if (ue == null || ue.ratio < 100) {
                            cities.add(sr.cityID);
                        }
                    }
                    if (cities.isEmpty()) {
                        return;
                    }

                    final int[] ids = new int[cities.size()];
                    for (int i = 0; i < ids.length; i++) {
                        ids[i] = cities.get(i);
                    }
                    this.activity.startDownload(this, ids);
                }
            } else {
                final MKOLUpdateElement ele = offlineMap
                        .getUpdateInfo(this.record.cityID);
                if (null == ele) {
                    this.activity
                            .startDownload(this, new int[]{this.record.cityID});
                    return;
                }

                switch (ele.status) {
                    case MKOLUpdateElement.DOWNLOADING:
                        offlineMap.pause(this.record.cityID);
                        Toasts.show(
                                activity,
                                R.string.offline_map_setting_activity_toast_pause_downloading);
                        break;
                    case MKOLUpdateElement.FINISHED:
                        return;
                    case MKOLUpdateElement.SUSPENDED:
                        this.activity
                                .startDownload(this, new int[]{this.record.cityID});
                        break;
                    case MKOLUpdateElement.UNDEFINED:
                        this.activity
                                .startDownload(this, new int[]{this.record.cityID});
                        break;
                }
            }

            ((LocalMapAdapter) adapter).notifyDataSetChanged();
        }

        public void updateProvince() {
            float totalSize = 0;
            float downloadedSize = 0;

            for (final MKOLSearchRecord sr : this.record.childCities) {
                totalSize += sr.size;

                final MKOLUpdateElement ue = activity.offlineMap
                        .getUpdateInfo(sr.cityID);
                if (null == ue)
                    continue;

                downloadedSize += ue.ratio / 100f * sr.size;
            }

            if (downloadedSize >= totalSize) {
                this.status = activity
                        .getString(R.string.offline_map_setting_activity_already_downloaded);
            } else if (downloadedSize > 0) {
                this.status = String.format("%d%%", (int) (downloadedSize
                        / totalSize * 100));
            } else {
                this.status = activity
                        .getString(R.string.offline_map_setting_activity_download);
            }
        }

        public void updateCity(MKOLUpdateElement ue) {
            if (ue.ratio >= 100) {
                this.status = activity
                        .getString(R.string.offline_map_setting_activity_already_downloaded);
            } else if (ue.ratio <= 0) {
                this.status = activity
                        .getString(R.string.offline_map_setting_activity_download);
            } else {
                this.status = String.format("%d%%", ue.ratio);
            }
        }
    }

    private static final class HotCityCategory extends LocalMapCategory {

        public HotCityCategory(OfflineMapSettingActivity activity) {
            super(activity,
                    R.string.offline_map_setting_activity_category_hot_cities,
                    activity.offlineMap.getHotCityList());
        }

    }

    private static final class OfflineCityCategory extends LocalMapCategory {

        public OfflineCityCategory(OfflineMapSettingActivity activity) {
            super(activity,
                    R.string.offline_map_setting_activity_category_all_cities,
                    activity.offlineMap.getOfflineCityList());
        }

    }

    @Override
    public boolean onGroupClick(ExpandableListView parent, View v,
                                int groupPosition, long id) {
        if (parent.isGroupExpanded(groupPosition)) {
            parent.collapseGroup(groupPosition);
        } else {
            // 第二个参数false表示展开时是否触发默认滚动动画
            parent.expandGroup(groupPosition, false);
        }
        // telling the listView we have handled the group click, and don't want
        // the default actions.
        return true;
    }

    /**
     * 判断手机是否是飞行模式
     *
     * @param context
     * @return
     */
    @SuppressWarnings("deprecation")
    public static boolean isAirplaneModeOn(Context context) {
        int isAirplaneMode = Settings.System.getInt(
                context.getContentResolver(), Settings.System.AIRPLANE_MODE_ON,
                0);
        return (isAirplaneMode == 1) ? true : false;
    }

    private void startDownload(final LocalMapItem lmi, final int[] cityIds) {
        switch (ConnectivityUtils.getActiveNetworkType(this)) {
            case ConnectivityManager.TYPE_WIFI:
            case ConnectivityManager.TYPE_ETHERNET:
                for (int i = 0; i < cityIds.length; i++) {
                    this.offlineMap.start(cityIds[i]);
                }
                Toasts.show(
                        OfflineMapSettingActivity.this,
                        R.string.offline_map_setting_activity_toast_start_downloading);
                lmi.status = getString(R.string.offline_map_setting_activity_downloading);
                break;
            default:
                final MaterialDialog dialog = new MaterialDialog(this);
                dialog.setMessage(R.string.offline_map_setting_activity_wifi_mesage);
                dialog.setPositiveButton(R.string.offline_map_setting_activity_wifi_continue, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                        for (int i = 0; i < cityIds.length; i++) {
                            offlineMap.start(cityIds[i]);
                        }
                        Toasts.show(
                                OfflineMapSettingActivity.this,
                                R.string.offline_map_setting_activity_toast_start_downloading);
                        lmi.status = getString(R.string.offline_map_setting_activity_downloading);
                    }
                }).setNegativeButton(R.string.offline_map_setting_activity_wifi_cancle, new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                }).show();
        }
    }
}
