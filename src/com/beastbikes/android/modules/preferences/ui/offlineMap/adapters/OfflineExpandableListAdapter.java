package com.beastbikes.android.modules.preferences.ui.offlineMap.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseExpandableListAdapter;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.preferences.ui.offlineMap.OfflineMapActivity;
import com.beastbikes.android.modules.preferences.ui.offlineMap.interfaces.OnOfflineItemStatusChangeListener;
import com.beastbikes.android.modules.preferences.ui.offlineMap.models.OfflineMapItem;
import com.beastbikes.android.modules.preferences.ui.offlineMap.utils.OfflineMapUtil;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.utils.DimensionUtils;
import com.beastbikes.framework.ui.android.utils.ViewHolder;

import java.util.List;

public class OfflineExpandableListAdapter extends BaseExpandableListAdapter {

    private Context context;
    private MKOfflineMap offline;
    protected LayoutInflater inflater;
    private OnOfflineItemStatusChangeListener listener;

    private List<OfflineMapItem> itemsProvince;
    private List<List<OfflineMapItem>> itemsProvinceCity;

    public OfflineExpandableListAdapter(Context context, MKOfflineMap offline,
                                        OnOfflineItemStatusChangeListener listener) {
        this.context = context;
        this.offline = offline;
        this.listener = listener;
        inflater = LayoutInflater.from(context);
    }

    @SuppressLint("InflateParams")
    @Override
    public View getGroupView(int groupPosition, boolean isExpanded,
                             View convertView, ViewGroup parent) {
        final ProvinceViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(R.layout.activity_offline_map_item_province,
                    null);
            holder = new ProvinceViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (ProvinceViewHolder) convertView.getTag();
        }

        if (isExpanded) {
            holder.tvProvincename.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    context.getResources().getDrawable(R.drawable.ic_arrow_up),
                    null);
        } else {
            holder.tvProvincename.setCompoundDrawablesWithIntrinsicBounds(null, null,
                    context.getResources()
                            .getDrawable(R.drawable.ic_arrow_down), null);
        }

        final OfflineMapItem item = (OfflineMapItem) getGroup(groupPosition);
        if(holder != null && null != item) {
            holder.bind(item);
        }
        return convertView;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getChildView(int groupPosition, int childPosition,
                             boolean isLastChild, View convertView, ViewGroup parent) {
        final CityViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.activity_offline_map_item_province_child, null);
            convertView.setPadding(DimensionUtils.dip2px(context, 12), 0, 0, 0);
            holder = new CityViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (CityViewHolder) convertView.getTag();
        }

        final OfflineMapItem data = (OfflineMapItem) getChild(groupPosition,
                childPosition);
        holder.bind(data);

        return convertView;
    }

    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

    @Override
    public int getGroupCount() {
        if (itemsProvince != null) {
            return itemsProvince.size();
        }
        return 0;
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        if (itemsProvinceCity != null && groupPosition >= 0
                && groupPosition < itemsProvinceCity.size()) {
            List<OfflineMapItem> c = itemsProvinceCity.get(groupPosition);
            if (c != null) {
                return c.size();
            }
        }
        return 0;
    }

    @Override
    public Object getGroup(int groupPosition) {
        if (itemsProvince != null && groupPosition >= 0
                && groupPosition < itemsProvince.size()) {
            return itemsProvince.get(groupPosition);
        }
        return null;
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        List<OfflineMapItem> pList = itemsProvinceCity.get(groupPosition);
        if (pList != null && childPosition >= 0 && childPosition < pList.size()) {
            return pList.get(childPosition);
        }
        return null;
    }

    @Override
    public long getGroupId(int groupPosition) {
        return 0;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return 0;
    }

    @Override
    public boolean hasStableIds() {
        return true;
    }

    public void setDatas(List<OfflineMapItem> itemsProvince,
                         List<List<OfflineMapItem>> itemsProvinceCity) {
        this.itemsProvince = itemsProvince;
        this.itemsProvinceCity = itemsProvinceCity;
        notifyDataSetChanged();
    }

    class ProvinceViewHolder extends ViewHolder<OfflineMapItem> {

        @IdResource(R.id.offlinemap_fragment_city_list_tvProvincename)
        private TextView tvProvincename;

        protected ProvinceViewHolder(View convertView) {
            super(convertView);
        }

        @Override
        public void bind(OfflineMapItem data) {
            tvProvincename.setText(data.getCityName().toString() + "("
                    + OfflineMapUtil.getSizeStr(data.getSize()) + ")");
        }

    }

    class CityViewHolder extends ViewHolder<OfflineMapItem> implements
            OnClickListener {

        @IdResource(R.id.offlinemap_fragment_city_list_tvCityname)
        private TextView tvCityname;

        @IdResource(R.id.offlinemap_fragment_city_list_tvSize)
        private TextView tvSize;

        @IdResource(R.id.offlinemap_fragment_city_list_tvStatus)
        private TextView tvStatus;

        @IdResource(R.id.offlinemap_fragment_city_list_tvDownload)
        private TextView tvDownload;

//		@IdResource(R.id.offlinemap_fragment_city_list_pbDownloadStatus)
//		private ProgressBar pbDownload;

        private OfflineMapItem data;

        public CityViewHolder(View convertView) {
            super(convertView);
            tvDownload.setOnClickListener(this);
        }

        @Override
        public void bind(OfflineMapItem data) {
            this.data = data;

            tvCityname.setText(data.getCityName());
            tvSize.setText("(" + OfflineMapUtil.getSizeStr(data.getSize()) + ")");

            if (data.getStatus() == MKOLUpdateElement.UNDEFINED) {
                tvDownload.setVisibility(View.VISIBLE);
            } else {
                tvDownload.setVisibility(View.INVISIBLE);
            }

            switch (data.getStatus()) {
                case MKOLUpdateElement.UNDEFINED:
                    tvStatus.setVisibility(View.INVISIBLE);
//				pbDownload.setVisibility(View.INVISIBLE);
                    tvDownload.setVisibility(View.VISIBLE);
                    tvDownload.setText(context.getResources().getText(R.string.offline_map_setting_activity_download));
                    break;
                case MKOLUpdateElement.DOWNLOADING:
                    tvStatus.setVisibility(View.VISIBLE);
//				pbDownload.setVisibility(View.VISIBLE);
                    tvDownload.setVisibility(View.VISIBLE);
                    tvStatus.setText(data.getProgress() + "%");
//				pbDownload.setProgress(data.getProgress());
                    tvDownload.setText(context.getResources().getText(R.string.activity_state_label_pause));
                    break;
                case MKOLUpdateElement.FINISHED:
                    if (data.isHavaUpdate()) {
                        tvStatus.setVisibility(View.INVISIBLE);
//					pbDownload.setVisibility(View.INVISIBLE);
                        tvDownload.setVisibility(View.VISIBLE);
                        tvDownload.setText(context.getResources().getText(R.string.update_dialog_button_update_immediately));
                    } else {
                        tvStatus.setVisibility(View.INVISIBLE);
//					pbDownload.setVisibility(View.INVISIBLE);
                        tvDownload.setVisibility(View.VISIBLE);
                        tvDownload.setText(context.getResources().getText(R.string.offline_map_setting_activity_already_downloaded));
                    }
                    break;
                case MKOLUpdateElement.SUSPENDED:
                case MKOLUpdateElement.eOLDSMd5Error:
                case MKOLUpdateElement.eOLDSIOError:
                case MKOLUpdateElement.eOLDSNetError:
                case MKOLUpdateElement.eOLDSWifiError:
                    // 暂停、错误，都当作暂停，都是可以继续下载
                    tvStatus.setVisibility(View.VISIBLE);
//				pbDownload.setVisibility(View.VISIBLE);
                    tvDownload.setVisibility(View.VISIBLE);
                    tvStatus.setText(context.getResources().getText(R.string.offline_map_setting_activity_download_pausing));
//				pbDownload.setProgress(data.getProgress());
                    tvDownload.setText(context.getResources().getText(R.string.offline_map_setting_activity_download_start));
                    break;
                case MKOLUpdateElement.WAITING:
                    tvStatus.setVisibility(View.VISIBLE);
//				pbDownload.setVisibility(View.VISIBLE);
                    tvDownload.setVisibility(View.VISIBLE);
                    tvStatus.setText(context.getResources().getText(R.string.offline_map_setting_activity_wait));
//				pbDownload.setProgress(data.getProgress());
                    tvDownload.setText(context.getResources().getText(R.string.activity_state_label_pause));
                    break;
                default:
                    tvStatus.setVisibility(View.INVISIBLE);
//				pbDownload.setVisibility(View.INVISIBLE);
                    break;
            }

        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.offlinemap_fragment_city_list_tvDownload:
                    if (data.getStatus() == MKOLUpdateElement.UNDEFINED
                            || data.getStatus() == MKOLUpdateElement.SUSPENDED) {
                        int id = data.getCityId();
                        if (id > 0) {
                            offline.start(id);
                            if (null == data.getDownInfo()) {
                                data.setDownInfo(offline.getUpdateInfo(id));
                            }
                            data.setStatus(MKOLUpdateElement.WAITING);
                            if (listener != null) {
                                listener.statusChanged(data, false);
                            }
                        }

                    } else if (data.getStatus() == MKOLUpdateElement.DOWNLOADING
                            || data.getStatus() == MKOLUpdateElement.WAITING) {
                        // 暂停
                        int id = data.getCityId();
                        if (id > 0) {
                            offline.pause(id);
                            data.setStatus(MKOLUpdateElement.SUSPENDED);
                            if (listener != null) {
                                listener.statusChanged(data, false);
                            }
                        }

                    } else {
                        if (data.isHavaUpdate()) {
                            // 先删除,直接start不行
                            offline.remove(data.getCityId());
                            data.setStatus(MKOLUpdateElement.UNDEFINED);
                            if (listener != null) {
                                listener.statusChanged(data, true);
                            }
                            // 再下载
                            offline.start(data.getCityId());

                        }
                        // 跳转下载界面
                        if (context instanceof OfflineMapActivity
                                && data.getStatus() == MKOLUpdateElement.FINISHED) {
                            ((OfflineMapActivity) context).toDownloadPage();
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    }
}
