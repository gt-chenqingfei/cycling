package com.beastbikes.android.modules.user.widget;

import android.content.Context;
import android.graphics.Color;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.FrameLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.user.dto.HistogramDTO;
import com.beastbikes.android.utils.DateFormatUtil;
import com.beastbikes.android.utils.IntentUtils;
import com.beastbikes.android.widget.helper.ItemClickListener;
import com.beastbikes.framework.android.utils.DimensionUtils;

import java.util.ArrayList;
import java.util.List;

import static android.support.v7.widget.RecyclerView.OnScrollListener;
import static android.support.v7.widget.RecyclerView.ViewHolder;

/**
 * Created by icedan on 16/4/18.
 */
public class HistogramView extends FrameLayout implements View.OnClickListener, ItemClickListener {

    // 柱状图的最大高度
    private static final int MAX_HISTOGRAM_VIEW_HEIGHT = 100;

    private TextView titleTv;
    private TextView rankTv;
    private RecyclerView recyclerView;
    private ViewGroup distanceView;
    private View pointView;
    private TextView distanceTv;

    private HistogramDTO histogramDTO;
    private List<HistogramDTO.ItemDTO> items = new ArrayList<>();
    private HistogramAdapter adapter;
    private LinearLayoutManager layoutManager;
    private int width;
    private int modulus;
    private String clubId;

    private int lastVisiblePosition;

    public HistogramView(Context context) {
        super(context);
        LayoutInflater.from(context).inflate(R.layout.histogram_view, this);
        this.initView();
    }

    public HistogramView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public HistogramView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.histogram_view_title_tv:
                if(!TextUtils.isEmpty(clubId)) {
                    IntentUtils.goClubFeedInfoActivity(getContext(), clubId);
                }
                break;
        }
    }

    @Override
    public void OnItemClick(ViewHolder viewHolder, int position) {
        for (int i = 0; i < items.size(); i++) {
            HistogramDTO.ItemDTO item = items.get(i);
            if (null == item) {
                continue;
            }

            if (position == i) {
                item.setSelected(true);
            } else {
                item.setSelected(false);
            }
        }

        this.adapter.notifyDataSetChanged();
    }

    @Override
    public void OnItemLongClick(ViewHolder viewHolder, int position) {

    }

    private void initView() {
        this.titleTv = (TextView) findViewById(R.id.histogram_view_title_tv);
        this.rankTv = (TextView) findViewById(R.id.histogram_view_rank_tv);
        this.distanceView = (ViewGroup) findViewById(R.id.histogram_view_distance_view);
        this.pointView = findViewById(R.id.histogram_view_point_view);
        this.distanceTv = (TextView) findViewById(R.id.histogram_view_distance_tv);
        this.recyclerView = (RecyclerView) findViewById(R.id.histogram_recycler_view);
        this.titleTv.setOnClickListener(this);

        this.recyclerView.setHasFixedSize(true);
        this.layoutManager = new LinearLayoutManager(getContext());
        this.layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        this.recyclerView.setLayoutManager(this.layoutManager);

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager vm = (WindowManager) getContext().getSystemService(Context.WINDOW_SERVICE);
        vm.getDefaultDisplay().getMetrics(dm);
        this.width = dm.widthPixels;

        this.recyclerView.setOnScrollListener(new OnScrollListener() {
            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {
                super.onScrollStateChanged(recyclerView, newState);
            }

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                int position = layoutManager.findLastVisibleItemPosition();
                if (null != adapter && lastVisiblePosition != position) {
                    lastVisiblePosition = position;
                    for (int i = 0; i < items.size(); i++) {
                        HistogramDTO.ItemDTO item = items.get(i);
                        if (i == position) {
                            item.setSelected(true);
                        } else {
                            item.setSelected(false);
                        }
                    }
                    adapter.notifyDataSetChanged();
                }
            }
        });
    }

    public void setTitle(int resId) {
        this.titleTv.setText(resId);
    }

    public void setTitle(String title) {
        if (TextUtils.isEmpty(title)) {
            this.titleTv.setText(R.string.profile_club_empty);
            return;
        }
        this.titleTv.setText(title);
    }

    public void setClubId(String clubId){
        this.clubId = clubId;
    }

    public void setHistogramDTO(HistogramDTO histogramDTO) {
        if (null == histogramDTO) {
            return;
        }

        this.histogramDTO = histogramDTO;
        this.items = this.histogramDTO.getItems();
        this.adapter = new HistogramAdapter(this.items, this);
        this.recyclerView.setAdapter(this.adapter);
        this.lastVisiblePosition = items.size() - 1;
        this.recyclerView.scrollToPosition(this.lastVisiblePosition);

        if (histogramDTO.getMax() <= 0) {
            this.distanceView.setVisibility(GONE);
            this.pointView.setVisibility(GONE);
        } else {
            this.distanceView.setVisibility(VISIBLE);
            this.pointView.setVisibility(VISIBLE);
        }

        if (histogramDTO.getMonthRank() <= 0) {
            this.rankTv.setText("－－");
        } else {
            this.rankTv.setText(String.valueOf(histogramDTO.getMonthRank()));
        }
        int maxHeight = DimensionUtils.dip2px(getContext(), MAX_HISTOGRAM_VIEW_HEIGHT);
        this.modulus = (int) (histogramDTO.getMax() / maxHeight);
    }

    public void setRank(int rank) {
        this.rankTv.setText(String.valueOf(rank));
    }

    private class HistogramViewHolder extends ViewHolder {

        private View view;
        private TextView dateTv;
        private TextView weekTv;
        private TextView histogramTv;
        private RelativeLayout histogramView;

        public HistogramViewHolder(View itemView) {
            super(itemView);
            this.view = itemView;
            this.dateTv = (TextView) itemView.findViewById(R.id.histogram_view_item_date_tv);
            this.weekTv = (TextView) itemView.findViewById(R.id.histogram_view_item_week_tv);
            this.histogramTv = (TextView) itemView.findViewById(R.id.histogram_view_item_histogram_view);
            this.histogramView = (RelativeLayout) itemView.findViewById(R.id.histogram_view_item_histogram);
        }
    }

    private class HistogramAdapter extends RecyclerView.Adapter<HistogramViewHolder> {

        private final ItemClickListener itemClickListener;
        private final List<HistogramDTO.ItemDTO> items;

        public HistogramAdapter(List<HistogramDTO.ItemDTO> items, ItemClickListener itemClickListener) {
            this.items = items;
            this.itemClickListener = itemClickListener;
        }

        @Override
        public HistogramViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.histogram_view_item, parent, false);
            return new HistogramViewHolder(view);
        }

        @Override
        public void onBindViewHolder(final HistogramViewHolder holder, final int position) {
            if (null == this.items || this.items.size() < 0) {
                return;
            }

            HistogramDTO.ItemDTO item = this.items.get(position);
            if (null == item) {
                return;
            }

            RecyclerView.LayoutParams viewParams = (RecyclerView.LayoutParams) holder.view.getLayoutParams();
            viewParams.width = width / 10;
            holder.view.setLayoutParams(viewParams);
            holder.dateTv.setText(DateFormatUtil.getDay(getContext(), item.getData()));
            holder.weekTv.setText(DateFormatUtil.getWeek(getContext(), item.getData()));
            RelativeLayout.LayoutParams params = (RelativeLayout.LayoutParams) holder.histogramTv.getLayoutParams();
            params.height = (int) (item.getValue() / modulus);
            holder.histogramTv.setLayoutParams(params);
            if (item.isSelected()) {
                holder.histogramTv.setBackgroundColor(Color.parseColor("#444444"));
                distanceTv.setText(String.format("%.1f",
                        item.getValue() / 1000));
            } else {
                holder.histogramTv.setBackgroundColor(Color.parseColor("#222222"));
            }

            holder.histogramView.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    itemClickListener.OnItemClick(holder, position);
                }
            });

        }

        @Override
        public int getItemCount() {
            return this.items.size();
        }

    }

}
