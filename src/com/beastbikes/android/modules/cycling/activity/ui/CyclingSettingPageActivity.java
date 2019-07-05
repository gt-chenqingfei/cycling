package com.beastbikes.android.modules.cycling.activity.ui;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.v4.view.MotionEventCompat;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.activity.dto.PreviewDto;
import com.beastbikes.android.widget.MaterialCheckBox;
import com.beastbikes.android.widget.MaterialDialog;
import com.beastbikes.android.widget.helper.ItemTouchHelperAdapter;
import com.beastbikes.android.widget.helper.ItemTouchHelperViewHolder;
import com.beastbikes.android.widget.helper.OnStartDragListener;
import com.beastbikes.android.widget.helper.SimpleItemTouchHelperCallback;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.beastbikes.android.widget.convenientbanner.listener.OnItemClickListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@LayoutResource(R.layout.activity_cycling_page_setting)
@MenuResource(R.menu.menu_cycling_setting)
public class CyclingSettingPageActivity extends SessionFragmentActivity implements OnClickListener,
        OnStartDragListener, Constants, OnItemClickListener {

    public static final String EXTRA_SETTING_POSITION = "position";
    private static final Logger logger = LoggerFactory.getLogger(CyclingSettingPageActivity.class);

    @IdResource(R.id.activity_cycling_setting_add_data)
    private LinearLayout addDataView;

    @IdResource(R.id.activity_cycling_setting_data)
    private RecyclerView dataView;
    private RecyclerListAdapter adapter;
    private ListView selectView;
    private SettingAdapter selectAdapter;
    private MaterialDialog dialog;
    private MaterialDialog msgDislog;

    private ItemTouchHelper mItemTouchHelper;
    private List<PreviewDto> previewList = new ArrayList<>();
    private SharedPreferences userSp;
    private JSONArray array;
    private boolean isEdit = false;

    public static List<Integer> selectIndex = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.userSp = getSharedPreferences(getUserId(), 0);

        this.adapter = new RecyclerListAdapter(this, previewList);
        this.dataView.setHasFixedSize(true);
        this.dataView.setAdapter(adapter);
        this.dataView.setLayoutManager(new LinearLayoutManager(this));

        ItemTouchHelper.Callback callback = new SimpleItemTouchHelperCallback(adapter);
        mItemTouchHelper = new ItemTouchHelper(callback);
        mItemTouchHelper.attachToRecyclerView(this.dataView);
        this.addDataView.setOnClickListener(this);

        this.initDataSp();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_cycling_setting_edit:
                if (!isEdit) {
                    item.setTitle(R.string.club_release_activites_dialog_ok);
                } else {
                    item.setTitle(R.string.label_edit);
                }
                this.isEdit = !this.isEdit;
                for (PreviewDto dto : previewList) {
                    dto.setEdit(this.isEdit);
                }
                this.adapter.notifyDataSetChanged();
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_cycling_setting_add_data:
                if (this.isEdit) {
                    return;
                }

                if (null != previewList && previewList.size() >= 6) {
                    if (null == msgDislog) {
                        this.msgDislog = new MaterialDialog(this);
                        this.msgDislog.setMessage(R.string.cycling_setting_add_data_error);
                        this.msgDislog.setPositiveButton(R.string.activity_alert_dialog_text_ok,
                                new OnClickListener() {
                                    @Override
                                    public void onClick(View v) {
                                        msgDislog.dismiss();
                                    }
                                });
                    }
                    this.msgDislog.show();
                    return;
                }

                if (null == selectView) {
                    this.selectView = new ListView(this);
                    if (null == selectAdapter) {
                        this.selectAdapter = new SettingAdapter(this);
                    }
                    this.selectView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                            ViewGroup.LayoutParams.MATCH_PARENT));
                    this.selectView.setDividerHeight(0);
                    this.selectView.setAdapter(this.selectAdapter);
                }

                if (null == dialog) {
                    dialog = new MaterialDialog(this);
                    dialog.setTitle(R.string.cycling_target_add_data);
                    dialog.setContentView(this.selectView);
                    dialog.setPositiveButton(R.string.activity_alert_dialog_text_ok, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                            if (null == selectIndex || selectIndex.size() <= 0) {
                                return;
                            }

                            JSONObject json = new JSONObject();
                            for (int i = 0; i < selectIndex.size(); i++) {
                                try {
                                    json.put(String.valueOf(i), selectIndex.get(i));
                                } catch (Exception e) {
                                    logger.error("Cycling data add data error, " + e);
                                }
                            }

                            previewList.add(new PreviewDto(getApplicationContext(), json));
                            adapter.notifyDataSetChanged();
                        }
                    });
                    dialog.setNegativeButton(R.string.activity_alert_dialog_text_cancel, new OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            dialog.dismiss();
                        }
                    });
                }

                this.selectAdapter = new SettingAdapter(this);
                this.selectView.setAdapter(this.selectAdapter);
                dialog.show();
                selectIndex.clear();
                break;
        }
    }

    @Override
    public void onItemClick(int position) {

    }

    @Override
    public void finish() {
        if (null != previewList && null != userSp) {
            this.userSp.edit().remove(PREF_CYCLING_DATA_SETTING_KEY).commit();
            this.array = new JSONArray();
            for (int i = 0; i < this.previewList.size(); i++) {
                final PreviewDto dto = this.previewList.get(i);
                try {
                    array.put(i, dto.getJson());
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
            this.userSp.edit().putString(PREF_CYCLING_DATA_SETTING_KEY, array.toString()).commit();
        }
        super.finish();
    }

    private void initDataSp() {
        this.array = new JSONArray();
        if (userSp.contains(PREF_CYCLING_DATA_SETTING_KEY)) {
            String data = userSp.getString(PREF_CYCLING_DATA_SETTING_KEY, "");
            try {
                array = new JSONArray(data);
            } catch (Exception e) {
                logger.error("get cycling data setting error," + e);
            }
        } else {
            array = new JSONArray();
            JSONObject timeArray = new JSONObject();
            try {
                timeArray.put("0", CYCLING_DATA_TIME);
            } catch (Exception e) {
                logger.error("Cycling data put time error, " + e);
            }

            try {
                array.put(0, timeArray);
            } catch (Exception e) {
                logger.error("Cycling data set time error, " + e);
            }

            JSONObject speedArray = new JSONObject();
            try {
                speedArray.put("0", CYCLING_DATA_ALTITUDE);
                speedArray.put("1", CYCLING_DATA_SVG_SPEED);
            } catch (Exception e) {
                logger.error("Cycling data put altitude and svg speed error, " + e);
            }

            try {
                array.put(1, speedArray);
            } catch (Exception e) {
                logger.error("Cycling data set altitude and svg error, " + e);
            }

            JSONObject uphillArray = new JSONObject();
            try {
                uphillArray.put("0", CYCLING_DATA_UPHILL_DISTANCE);
            } catch (Exception e) {
                logger.error("Cycling data put altitude and svg error, " + e);
            }

            try {
                array.put(2, uphillArray);
            } catch (Exception e) {
                logger.error("Cycling data set uphill distance error, " + e);
            }

            userSp.edit().putString(PREF_CYCLING_DATA_SETTING_KEY, array.toString()).commit();
        }

        if (null == array || array.length() <= 0) {
            return;
        }

        previewList.clear();
        for (int i = 0; i < array.length(); i++) {
            JSONObject data = array.optJSONObject(i);
            previewList.add(new PreviewDto(this, data));
        }

        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void onStartDrag(RecyclerView.ViewHolder viewHolder) {
        mItemTouchHelper.startDrag(viewHolder);
    }

    /**
     * Simple RecyclerView.Adapter that implements {@link ItemTouchHelperAdapter} to respond to move and
     * dismiss events from a {@link android.support.v7.widget.helper.ItemTouchHelper}.
     *
     * @author Paul Burke (ipaulpro)
     */
    public class RecyclerListAdapter extends RecyclerView.Adapter<ItemViewHolder>
            implements ItemTouchHelperAdapter {

        private List<PreviewDto> mItems = new ArrayList<>();

        private final OnStartDragListener mDragStartListener;

        public RecyclerListAdapter(OnStartDragListener dragStartListener, List<PreviewDto> list) {
            mDragStartListener = dragStartListener;
            mItems = list;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.activity_cycling_setting_data_item,
                    parent, false);
            ItemViewHolder itemViewHolder = new ItemViewHolder(view);
            return itemViewHolder;
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder holder, final int position) {
            final PreviewDto preview = this.mItems.get(position);
            if (null == preview) {
                return;
            }

            String value1 = preview.getLabel1();
            String value2 = preview.getLabel2();
            if (TextUtils.isEmpty(value1) || TextUtils.isEmpty(value2)) {
                holder.labelTv.setText(R.string.cycling_data_count_1);
            }

            if (!TextUtils.isEmpty(value1) && !TextUtils.isEmpty(value2)) {
                holder.labelTv.setText(R.string.cycling_data_count_2);
            }

            StringBuilder sb = new StringBuilder();
            if (!TextUtils.isEmpty(value1)) {
                sb.append(value1);
            }
            if (!TextUtils.isEmpty(value2)) {
                sb.append(",").append(value2);
            }
            holder.valueTv.setText(sb.toString());

            if (preview.isEdit()) {
                holder.dragIv.setVisibility(View.VISIBLE);
                holder.deleteIv.setVisibility(View.VISIBLE);
            } else {
                holder.dragIv.setVisibility(View.GONE);
                holder.deleteIv.setVisibility(View.GONE);
            }

            // Start a drag whenever the handle view it touched
            holder.dragIv.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    if (MotionEventCompat.getActionMasked(event) == MotionEvent.ACTION_DOWN) {
                        mDragStartListener.onStartDrag(holder);
                    }
                    return false;
                }
            });

            holder.deleteIv.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    onItemDismiss(position);
                }
            });
        }

        @Override
        public void onItemDismiss(int position) {
            if (null == mItems || mItems.size() <= 0) {
                return;
            }

            if (mItems.size() == 1) {
                Toasts.show(getApplicationContext(), R.string.cycling_setting_data_already_one);
                return;
            }

            if (position >= mItems.size()) {
                return;
            }

            mItems.remove(position);
            notifyDataSetChanged();
        }

        @Override
        public boolean onItemMove(int fromPosition, int toPosition) {
            Collections.swap(mItems, fromPosition, toPosition);
            notifyItemMoved(fromPosition, toPosition);
            return true;
        }

        @Override
        public int getItemCount() {
            return mItems.size();
        }

    }

    /**
     * Simple example of a view holder that implements {@link ItemTouchHelperViewHolder} and has a
     * "handle" view that initiates a drag event when touched.
     */
    private static class ItemViewHolder extends RecyclerView.ViewHolder
            implements ItemTouchHelperViewHolder {

        public final ImageView deleteIv;
        public final TextView labelTv;
        public final TextView valueTv;
        public final ImageView dragIv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.labelTv = (TextView) itemView.findViewById(R.id.activity_cycling_setting_data_item_label);
            this.valueTv = (TextView) itemView.findViewById(R.id.activity_cycling_setting_data_item_value);
            this.deleteIv = (ImageView) itemView.findViewById(R.id.cycling_setting_delete_icon);
            this.dragIv = (ImageView) itemView.findViewById(R.id.activity_cycling_setting_drag_icon);
        }

        @Override
        public void onItemSelected() {
        }

        @Override
        public void onItemClear() {
        }
    }

    private static class SettingAdapter extends BaseAdapter {

        public final String[] list;

        public SettingAdapter(Context context) {
            this.list = context.getResources().getStringArray(R.array.cycling_setting_array);
        }

        @Override
        public int getCount() {
            return this.list.length;
        }

        @Override
        public Object getItem(int position) {
            return this.list[position];
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            final SelectItemViewHolder viewHolder;
            if (null == convertView) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.cycling_setting_dialog_layout, null);
                viewHolder = new SelectItemViewHolder(convertView, position);
            } else {
                viewHolder = (SelectItemViewHolder) convertView.getTag();
            }

            viewHolder.bind(this.list[position]);
            return convertView;
        }
    }

    private static class SelectItemViewHolder extends ViewHolder<String> {
        public final MaterialCheckBox checkBox;
        public final TextView labelTv;
        public final View view;
        private int index;

        public SelectItemViewHolder(View itemView, int index) {
            super(itemView);
            this.view = itemView;
            this.checkBox = (MaterialCheckBox) itemView.findViewById(R.id.cycling_setting_dialog_item_check);
            this.labelTv = (TextView) itemView.findViewById(R.id.cycling_setting_dialog_item_label);
            this.index = index;
        }

        @Override
        public void bind(String s) {
            this.labelTv.setText(s);

            this.view.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkBox.isChecked() && selectIndex.size() >= 2 && !selectIndex.contains(index)) {
                        Toasts.show(getContext(), R.string.cycling_setting_more_data_msg);
                        return;
                    }
                    checkBox.setChecked(!checkBox.isChecked());
                }
            });

            this.checkBox.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!checkBox.isChecked() && selectIndex.size() >= 2 && !selectIndex.contains(index)) {
                        Toasts.show(getContext(), R.string.cycling_setting_more_data_msg);
                        return;
                    }
                    checkBox.setChecked(!checkBox.isChecked());
                }
            });

            this.checkBox.setOnCheckedChangedListener(new MaterialCheckBox.OnCheckedChangeListener() {
                @Override
                public void onCheckedChanged(View view, boolean isChecked) {
                    if (isChecked && selectIndex.size() >= 2 && !selectIndex.contains(index)) {
                        checkBox.setChecked(!isChecked);
                        // TODO 是否需要提示
                        return;
                    }

                    if (isChecked) {
                        selectIndex.add(index);
                    } else {
                        if (selectIndex.contains(index)) {
                            selectIndex.remove((Object) index);
                        }
                    }
                }
            });
        }
    }
}
