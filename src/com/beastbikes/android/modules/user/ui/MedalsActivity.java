package com.beastbikes.android.modules.user.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.widget.GridLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dto.MedalDTO;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.helper.ItemClickListener;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.utils.DimensionUtils;
import com.squareup.picasso.Picasso;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;

@LayoutResource(R.layout.activity_medals)
public class MedalsActivity extends SessionFragmentActivity implements View.OnClickListener, ItemClickListener {

    public static final String EXTRA_MEDAL_COUNT = "medal_count";
    private static final int SPAN_COUNT = 3;

    /**
     * 是否为push进来
     */
    public static final String EXTRA_FROM_PUSH = "from_push";
    /**
     * 已经点亮和进行中的
     */
    public static final int MEDAL_VALID = 0;
    /**
     * 过期的
     */
    public static final int MEDAL_INVALID = 1;
    /**
     * v2.5.0新增自动点亮功能
     * 服务器自动点亮并下发(通过push)
     */
    public static final int MEDAL_NEW_ACTIVE = 2;


    @IdResource(R.id.activity_medals_already_get)
    private RecyclerView medalView;
    @IdResource(R.id.activity_medals_already_expired)
    private RecyclerView expiredMedalView;
    @IdResource(R.id.activity_medals_no_medal)
    private LinearLayout noMedalView;
    @IdResource(R.id.activity_medals_medal_count)
    private TextView medalCountTv;
    @IdResource(R.id.activity_medal_get_view)
    private ViewGroup getView;
    @IdResource(R.id.activity_medals_already_expired_view)
    private ViewGroup expiredView;
    @IdResource(R.id.activity_medals_visible_view)
    private ViewGroup visibleView;

    private int height;

    private UserManager userManager;
    private MedalRecyclerAdapter medalAdapter;
    private MedalRecyclerAdapter expiredMedalAdapter;
    private List<MedalDTO> medals = new ArrayList<>();
    private List<MedalDTO> expiredMedals = new ArrayList<>();
    private List<MedalDTO> newActivedMedals = new ArrayList<>();
    private MedalDTO clickMedal;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        final ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        int medalCount = getIntent().getIntExtra(EXTRA_MEDAL_COUNT, 0);
        this.medalCountTv.setText(String.format(getString(R.string.label_medals_count), medalCount));
        this.visibleView.setOnClickListener(this);
        this.expiredView.setOnClickListener(this);
        this.userManager = new UserManager(this);
        this.medalAdapter = new MedalRecyclerAdapter(this, medals, true);
        this.medalView.setHasFixedSize(true);
        this.medalView.setAdapter(medalAdapter);
        this.medalView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));

        this.expiredMedalAdapter = new MedalRecyclerAdapter(this, expiredMedals, false);
        this.expiredMedalView.setHasFixedSize(true);
        this.expiredMedalView.setAdapter(expiredMedalAdapter);
        this.expiredMedalView.setLayoutManager(new GridLayoutManager(this, SPAN_COUNT));

        WindowManager wm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        Display dp = wm.getDefaultDisplay();
        this.height = dp.getHeight() - DimensionUtils.dip2px(this, 120 + 88) - getStatusBarHeight();

        // 获取已点亮的勋章
        this.getBadgeList(MEDAL_VALID);
        // 获取已过期的勋章
        this.getBadgeList(MEDAL_INVALID);

        //获取最新的已经点亮的
        if (getIntent().getBooleanExtra(EXTRA_FROM_PUSH, false)) {
            this.getBadgeList(MEDAL_NEW_ACTIVE);
        }
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_medals_already_expired_view:
                this.showMedalViewAnim();
                break;
            case R.id.activity_medals_visible_view:
                this.showExpiredViewAnim();
                break;
        }
    }

    @Override
    public void OnItemClick(RecyclerView.ViewHolder viewHolder, int position) {
        if (null == clickMedal) {
            return;
        }

        SpeedxAnalytics.onEvent(this, "", "click_meadl_details");
        final Intent intent = new Intent(this, MedalInfoActivity.class);
        intent.putExtra(MedalInfoActivity.EXTRA_USER_ID, getUserId());
        intent.putExtra(MedalInfoActivity.EXTRA_MEDAL_ID, clickMedal.getId());
        intent.putExtra(MedalInfoActivity.EXTRA_MEDAL_POSITION, position);
        if (clickMedal.getStatus() == 0 || clickMedal.getStatus() == 2) {
            intent.putExtra(MedalInfoActivity.EXTRA_MEDAL_LIST, (Serializable) medals);
        } else {
            intent.putExtra(MedalInfoActivity.EXTRA_MEDAL_LIST, (Serializable) expiredMedals);
        }
        startActivity(intent);
    }

    @Override
    public void OnItemLongClick(RecyclerView.ViewHolder viewHolder, int position) {

    }

    private int getStatusBarHeight() {
        try {
            Class<?> c = Class.forName("com.android.internal.R$dimen");
            Object obj = c.newInstance();
            Field field = c.getField("status_bar_height");
            int x = Integer.parseInt(field.get(obj).toString());
            return getResources().getDimensionPixelSize(x);
        } catch (Exception e1) {
            e1.printStackTrace();
        }

        return DimensionUtils.dip2px(this, 24);
    }

    /**
     * 获取图片
     */
    private void getBadgeList(final int isHistory) {
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, List<MedalDTO>>() {

            @Override
            protected List<MedalDTO> doInBackground(String... params) {
                return userManager.getBadgeList(isHistory, 1, 1000, getUserId());
            }

            @Override
            protected void onPostExecute(List<MedalDTO> medalDTOs) {
                if (null == medalDTOs || medalDTOs.isEmpty()) {
                    if (isHistory == MEDAL_VALID) {
                        noMedalView.setVisibility(View.VISIBLE);
                    }
                    return;
                }

                if (isHistory == MEDAL_VALID) {
                    noMedalView.setVisibility(View.INVISIBLE);
                    medals.addAll(medalDTOs);
                    //v2.5.0只显示未过期的个数
//                    medalCountTv.setText(medalDTOs.size());
                    medalAdapter.notifyDataSetChanged();
                } else if (isHistory == MEDAL_INVALID) {
                    expiredMedals.addAll(medalDTOs);
                    expiredMedalAdapter.notifyDataSetChanged();
                } else {
                    newActivedMedals.clear();
                    newActivedMedals.addAll(medalDTOs);
                    showMedalsDialog();
                }
            }
        });
    }

    /**
     * 调用MedalInfoActivity代替dialog
     */
    private void showMedalsDialog() {
        Intent intent = new Intent(this, MedalInfoActivity.class);
        intent.putExtra(MedalInfoActivity.EXTRA_MEDAL_LIST, (Serializable) newActivedMedals);
        intent.putExtra(EXTRA_FROM_PUSH, true);
        startActivity(intent);
    }

    /**
     * 显示已过期View的动画
     */
    private void showExpiredViewAnim() {
        this.expiredView.setVisibility(View.VISIBLE);
        this.getView.setVisibility(View.GONE);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, 0, -height);
        translateAnimation.setDuration(500);
        this.getView.setAnimation(translateAnimation);
        translateAnimation.start();
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                visibleView.setVisibility(View.GONE);
            }

            @Override
            public void onAnimationEnd(Animation animation) {

            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        TranslateAnimation translateAnimation1 = new TranslateAnimation(0, 0, height - DimensionUtils.dip2px(this, 57), 0);
        translateAnimation1.setDuration(500);
        this.expiredView.setAnimation(translateAnimation1);
        translateAnimation1.start();
    }

    private void showMedalViewAnim() {
        this.expiredView.setVisibility(View.GONE);
        this.getView.setVisibility(View.VISIBLE);
        TranslateAnimation translateAnimation = new TranslateAnimation(0, 0, -height, 0);
        translateAnimation.setDuration(500);
        this.getView.setAnimation(translateAnimation);
        translateAnimation.start();
        translateAnimation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                visibleView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });

        TranslateAnimation translateAnimation1 = new TranslateAnimation(0, 0, 0, height - DimensionUtils.dip2px(this, 45));
        translateAnimation1.setDuration(500);
        this.expiredView.setAnimation(translateAnimation1);
        translateAnimation1.start();
    }

    private final class MedalRecyclerAdapter extends RecyclerView.Adapter<ItemViewHolder> {

        private final int EMPTY_VIEW_TYPE = -1;

        private final List<MedalDTO> medals;
        private final ItemClickListener itemClickListener;
        private boolean isHistory;

        public MedalRecyclerAdapter(ItemClickListener listener, List<MedalDTO> medals, boolean isHistory) {
            this.medals = medals;
            this.itemClickListener = listener;
            this.isHistory = isHistory;
        }

        @Override
        public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            final ItemViewHolder itemViewHolder;
            if (viewType == EMPTY_VIEW_TYPE) {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medal_item_empty_view,
                        parent, false);
                itemViewHolder = new ItemViewHolder(view);
            } else {
                View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.medal_item_view,
                        parent, false);
                itemViewHolder = new ItemViewHolder(view);
            }
            return itemViewHolder;
        }

        @Override
        public void onBindViewHolder(final ItemViewHolder holder, final int position) {
            if (isHistory && position < SPAN_COUNT) {
                return;
            }

            final MedalDTO medal;
            if (isHistory && position >= SPAN_COUNT && position <= getItemCount()) {
                medal = this.medals.get(position - SPAN_COUNT);
            } else {
                medal = this.medals.get(position);
            }

            if (null == medal) {
                return;
            }

            holder.nameTv.setText(medal.getName());
            String imageUrl = medal.getUnLightUrl();
            if (this.isHistory) {
                imageUrl = medal.getLightUrl();
            }
            if (!TextUtils.isEmpty(imageUrl)) {
                Picasso.with(MedalsActivity.this)
                        .load(imageUrl).fit().centerCrop().into(holder.iconIv);
            }
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    clickMedal = medal;
                    itemClickListener.OnItemClick(holder, position);
                }
            });

            holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View v) {
                    clickMedal = medal;
                    itemClickListener.OnItemLongClick(holder, position);
                    return true;
                }
            });

        }

        @Override
        public int getItemCount() {
            if (isHistory) {
                return this.medals.size() + SPAN_COUNT;
            }
            return this.medals.size();
        }

        @Override
        public int getItemViewType(int position) {
            if (this.isHistory && position < SPAN_COUNT) {
                return EMPTY_VIEW_TYPE;
            }
            return super.getItemViewType(position);
        }
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder {

        public final View itemView;
        public final TextView nameTv;
        public final ImageView iconIv;

        public ItemViewHolder(View itemView) {
            super(itemView);
            this.itemView = itemView;
            this.nameTv = (TextView) itemView.findViewById(R.id.medal_item_medal_name);
            this.iconIv = (ImageView) itemView.findViewById(R.id.medal_item_medal_icon);
        }
    }
}
