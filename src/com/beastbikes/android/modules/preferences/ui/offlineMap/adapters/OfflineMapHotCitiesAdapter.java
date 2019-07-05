package com.beastbikes.android.modules.preferences.ui.offlineMap.adapters;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.preferences.ui.offlineMap.OfflineMapActivity;
import com.beastbikes.android.modules.preferences.ui.offlineMap.interfaces.OnOfflineItemStatusChangeListener;
import com.beastbikes.android.modules.preferences.ui.offlineMap.models.OfflineMapItem;
import com.beastbikes.android.modules.preferences.ui.offlineMap.utils.OfflineMapUtil;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.ui.android.utils.ViewHolder;

import java.util.List;

public class OfflineMapHotCitiesAdapter extends
        ArrayListAdapter<OfflineMapItem> {

    private Context context;
    private MKOfflineMap offline;
    private OnOfflineItemStatusChangeListener listener;

    public OfflineMapHotCitiesAdapter(Context context, MKOfflineMap offline,
                                      OnOfflineItemStatusChangeListener listener) {
        super(context);
        this.context = context;
        this.offline = offline;
        this.listener = listener;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final HotViewHolder holder;

        if (convertView == null) {
            convertView = LayoutInflater.from(context).inflate(
                    R.layout.activity_offline_map_item_province_child, null);
            convertView.setBackgroundColor(Color.parseColor("#ffffff"));
            holder = new HotViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (HotViewHolder) convertView.getTag();
        }

        OfflineMapItem data = (OfflineMapItem) getItem(position);
        holder.bind(data);

        return convertView;
    }

    @Override
    public void setDatas(List<OfflineMapItem> ds) {
        super.setDatas(ds);
    }

    @Override
    public void setArrayDatas(OfflineMapItem[] array) {
        super.setArrayDatas(array);
    }

    class HotViewHolder extends ViewHolder<OfflineMapItem> implements
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

        public HotViewHolder(View convertView) {
            super(convertView);
            tvDownload.setOnClickListener(this);
        }

        @Override
        public void bind(OfflineMapItem data) {
            if (null == data)
                return;

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
                    //隐藏下载状态和进度条
                    tvStatus.setVisibility(View.INVISIBLE);
//				pbDownload.setVisibility(View.INVISIBLE);
                    tvDownload.setVisibility(View.VISIBLE);
                    tvDownload.setText(context.getResources().getString(R.string.offline_map_setting_activity_download));
                    break;
                case MKOLUpdateElement.DOWNLOADING:
                    //正在下载时下载按钮换为暂停
                    tvStatus.setVisibility(View.VISIBLE);
//				pbDownload.setVisibility(View.VISIBLE);
                    tvDownload.setVisibility(View.VISIBLE);
                    tvStatus.setText(data.getProgress() + "%");
//				pbDownload.setProgress(data.getProgress());
                    tvDownload.setText(context.getResources().getString(R.string.activity_state_label_pause));
                    break;
                case MKOLUpdateElement.FINISHED:
                    if (data.isHavaUpdate()) {
                        tvStatus.setVisibility(View.INVISIBLE);
//					pbDownload.setVisibility(View.INVISIBLE);
                        tvDownload.setVisibility(View.VISIBLE);
                        tvDownload.setText(context.getResources().getString(R.string.update_dialog_button_update_immediately));
                    } else {
                        tvStatus.setVisibility(View.INVISIBLE);
//					pbDownload.setVisibility(View.INVISIBLE);
                        tvDownload.setVisibility(View.VISIBLE);
                        tvDownload.setText(context.getResources().getString(R.string.offline_map_setting_activity_already_downloaded));
                    }
                    break;
                // 暂停、错误，都当作暂停，都是可以继续下载
                case MKOLUpdateElement.SUSPENDED:
                case MKOLUpdateElement.eOLDSMd5Error:
                case MKOLUpdateElement.eOLDSIOError:
                case MKOLUpdateElement.eOLDSNetError:
                case MKOLUpdateElement.eOLDSWifiError:
                    tvStatus.setVisibility(View.VISIBLE);
//				pbDownload.setVisibility(View.VISIBLE);
                    tvDownload.setVisibility(View.VISIBLE);
                    tvStatus.setText(context.getResources().getString(R.string.offline_map_setting_activity_download_pausing));
//				pbDownload.setProgress(data.getProgress());
                    tvDownload.setText(context.getResources().getString(R.string.offline_map_setting_activity_download_start));
                    break;
                case MKOLUpdateElement.WAITING:
                    tvStatus.setVisibility(View.VISIBLE);
//				pbDownload.setVisibility(View.VISIBLE);
                    tvDownload.setVisibility(View.VISIBLE);
                    tvStatus.setText(context.getResources().getString(R.string.offline_map_setting_activity_wait));
//				pbDownload.setProgress(data.getProgress());
                    tvDownload.setText(context.getResources().getString(R.string.activity_state_label_pause));
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
                        if (data.isHavaUpdate() || data.getStatus() >= MKOLUpdateElement.eOLDSMd5Error) {
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
