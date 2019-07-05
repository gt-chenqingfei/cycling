package com.beastbikes.android.modules.preferences.ui.offlineMap.adapters;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.baidu.mapapi.map.offline.MKOLUpdateElement;
import com.baidu.mapapi.map.offline.MKOfflineMap;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.preferences.ui.offlineMap.interfaces.OnOfflineItemStatusChangeListener;
import com.beastbikes.android.modules.preferences.ui.offlineMap.models.OfflineMapItem;
import com.beastbikes.android.modules.preferences.ui.offlineMap.utils.OfflineMapUtil;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.ui.android.utils.ViewHolder;

public class OfflineMapManagerAdapter extends ArrayListAdapter<OfflineMapItem> {

    private Context context;
    private MKOfflineMap offline;
    private OnOfflineItemStatusChangeListener listener;

    public OfflineMapManagerAdapter(Context context, MKOfflineMap offline,
                                    OnOfflineItemStatusChangeListener listener) {
        super(context);
        this.context = context;
        this.offline = offline;
        this.listener = listener;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        final DownLoadedViewHolder holder;

        if (convertView == null) {
            convertView = inflater.inflate(
                    R.layout.activity_offline_map_item_manager, null);
            holder = new DownLoadedViewHolder(convertView);
            convertView.setTag(holder);

        } else {
            holder = (DownLoadedViewHolder) convertView.getTag();
        }

        OfflineMapItem data = (OfflineMapItem) getItem(position);
        holder.bind(data);

        return convertView;
    }

    @Override
    public void setArrayDatas(OfflineMapItem[] array) {
        super.setArrayDatas(array);
    }

    class DownLoadedViewHolder extends ViewHolder<OfflineMapItem> implements
            OnClickListener {

        @IdResource(R.id.offlinemap_fragment_download_manager_tvCityname)
        private TextView tvCityname;

        @IdResource(R.id.offlinemap_fragment_download_manager_tvSize)
        private TextView tvSize;

        @IdResource(R.id.offlinemap_fragment_download_manager_btnRemove)
        private TextView btnRemove;

        private OfflineMapItem data;

        public DownLoadedViewHolder(View convertView) {
            super(convertView);
            btnRemove.setOnClickListener(this);
        }

        @Override
        public void bind(OfflineMapItem data) {
            this.data = data;

            tvCityname.setText(data.getCityName());

            tvSize.setText("(" + OfflineMapUtil.getSizeStr(data.getSize())
                    + ")");

            if (data.getStatus() == MKOLUpdateElement.DOWNLOADING) {
                // tvCityname.setTextColor(Color.BLUE);

                // tvStatus.setText("正在下载 " + data.getProgress() + "%");
                // btnDown.setText("暂停");
                // pbDownload.setProgress(data.getProgress());

                // btnDown.setVisibility(View.VISIBLE);
                // pbDownload.setVisibility(View.VISIBLE);
                // tvStatus.setVisibility(View.VISIBLE);

            } else if (data.getStatus() == MKOLUpdateElement.FINISHED) {
                if (data.isHavaUpdate()) {
                    // tvCityname.setTextColor(Color.BLUE);

                    // tvStatus.setText("有更新");
                    // btnDown.setText("更新");
                    // pbDownload.setProgress(data.getProgress());
                    // pbDownload.setVisibility(View.GONE);
                    // btnDown.setVisibility(View.VISIBLE);
                    // pbDownload.setVisibility(View.VISIBLE);
                    // tvStatus.setVisibility(View.VISIBLE);

                } else {
                    // btnDown.setVisibility(View.GONE);
                    // pbDownload.setProgress(data.getProgress());
                    // pbDownload.setVisibility(View.GONE);
                    // tvStatus.setVisibility(View.GONE);
                }

            } else if (data.getStatus() == MKOLUpdateElement.SUSPENDED
                    || data.getStatus() == MKOLUpdateElement.UNDEFINED
                    || data.getStatus() >= MKOLUpdateElement.eOLDSMd5Error) {
                // tvCityname.setTextColor(Color.BLUE);

                // 暂停、未知、错误，都是继续下载
                // tvStatus.setText("暂停");
                // btnDown.setText("继续");
                // pbDownload.setProgress(data.getProgress());

                // btnDown.setVisibility(View.VISIBLE);
                // pbDownload.setVisibility(View.VISIBLE);
                // tvStatus.setVisibility(View.VISIBLE);

            } else if (data.getStatus() == MKOLUpdateElement.WAITING) {
                // tvCityname.setTextColor(Color.BLUE);

                // tvStatus.setText("等待");
                // btnDown.setText("暂停");
                // pbDownload.setProgress(data.getProgress());

                // btnDown.setVisibility(View.VISIBLE);
                // pbDownload.setVisibility(View.VISIBLE);
                // tvStatus.setVisibility(View.VISIBLE);

            } else {
                // btnDown.setVisibility(View.GONE);
                // pbDownload.setVisibility(View.GONE);
                // tvStatus.setVisibility(View.GONE);
            }
        }

        @Override
        public void onClick(View v) {
            switch (v.getId()) {

                case R.id.offlinemap_fragment_download_manager_btnRemove:
                    final MaterialDialog dialog = new MaterialDialog(context);
                    dialog.setTitle(context.getResources().getText(R.string.club_create_tip_title));
                    dialog.setMessage(R.string.dialog_sure_or_delete);
                    dialog.setPositiveButton(R.string.delete, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            offline.remove(data.getCityId());
                            SpeedxAnalytics.onEvent(context, "删除离线地图",null);
                            data.setStatus(MKOLUpdateElement.UNDEFINED);
                            if (listener != null) {
                                listener.statusChanged(data, true);
                            }
                            dialog.dismiss();
                        }
                    });
                    dialog.setNegativeButton(R.string.cancel, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                    dialog.show();
                    break;
                default:
                    break;
            }
        }
    }
}
