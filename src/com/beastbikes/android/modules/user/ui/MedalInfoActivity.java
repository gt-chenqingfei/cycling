package com.beastbikes.android.modules.user.ui;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.embapi.BrowserActivity;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.user.dto.MedalDTO;
import com.beastbikes.android.modules.user.ui.adapter.MedalViewPagerAdapter;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.utils.Toasts;

import java.util.ArrayList;

import static io.fabric.sdk.android.services.network.HttpRequest.append;

@LayoutResource(R.layout.activity_medal_info)
public class MedalInfoActivity extends SessionFragmentActivity implements View.OnClickListener, MedalViewPagerAdapter.OnMedalButtonClickListener, ViewPager.OnPageChangeListener {

    public static final String EXTRA_MEDAL_ID = "medal_id";
    public static final String EXTRA_MEDAL_LIST = "medal_list";
    public static final String EXTRA_MEDAL_POSITION = "medal_position";
    public static final String EXTRA_AWARD_STATUS = "award_status";

    /**
     * 领奖url
     */
    static final String URL_RECEIVE_AWARD = "/app/activity/reward.html?id=";
    /**
     * 抽奖url
     */
    static final String URL_LOTTERY = "/app/activity/lottery.html?id=";

    @IdResource(R.id.activity_medal_info_close)
    private ImageView closeIv;
    @IdResource(R.id.viewPager_medal_info)
    private ViewPager mViewPager;
    @IdResource(R.id.activity_medal_info_dot_linear)
    private LinearLayout mLinearDot;
    private LinearLayout.LayoutParams ll;
    private Drawable mDotDrawable;
    private int preDotPosition = 0;

    private int mCurrentItem = 0;

    private MedalViewPagerAdapter mAdapter;

    private ArrayList<MedalDTO> medalDTOs;
    /**
     * 使用每个活动的id作为requestCode,便于返回时找到对应的活动
     */
    private int mRequestCode;
    private boolean isFromPush;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_bottom, R.anim.activity_none);

        this.mCurrentItem = getIntent().getIntExtra(EXTRA_MEDAL_POSITION, 0);
        this.medalDTOs = (ArrayList<MedalDTO>) getIntent().getSerializableExtra(EXTRA_MEDAL_LIST);
        isFromPush = getIntent().getBooleanExtra(MedalsActivity.EXTRA_FROM_PUSH, false);

        if (null == this.medalDTOs || this.medalDTOs.isEmpty()) {
            Toasts.show(this, "数据错误");
            this.finish();
            return;
        }

        this.initView();

    }

    private void initView() {
        int dotSize = getResources().getDimensionPixelSize(R.dimen.dimen_7);
        ll = new LinearLayout.LayoutParams(dotSize, dotSize);

        mAdapter = new MedalViewPagerAdapter(this, medalDTOs, isFromPush);
        if (isFromPush) {
            mAdapter.setOnMedalButtonClickListener(this);
            mCurrentItem = 0;
            this.setDot();
            if (medalDTOs.size() > 1) {
                mLinearDot.setVisibility(View.VISIBLE);
                mLinearDot.getChildAt(0).setEnabled(true);
            } else {
                mLinearDot.setVisibility(View.GONE);
            }
        }
        mViewPager.setAdapter(mAdapter);
        mViewPager.setCurrentItem(mCurrentItem);

        this.setListener();
    }

    private void setListener() {
        this.closeIv.setOnClickListener(this);
        if (isFromPush) {
            this.mViewPager.addOnPageChangeListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.activity_medal_info_close:
                finish();
                break;
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == this.mRequestCode) {
            if (null == data) {
                return;
            }

//            'giftType': 0, # 奖品类型 0: '实物',1: '虚拟',2: '流量',3: '抽奖'
//            'giftSituation': 0, # 领奖状态 0 不可领奖, 1 可领奖, 2 已抽奖(完成抽奖但未填写详细信息领取奖励) , 3 已领奖
//            status:web返回状态 1：已经抽奖，没有获奖。    2：已经抽奖，获奖了，但是没有领取。  3 ： 已经领奖（不管抽奖与否
            for (MedalDTO medalDTO : medalDTOs) {
                if (medalDTO.getActivityId() == requestCode) {
                    int status = data.getIntExtra(EXTRA_AWARD_STATUS, 0);
                    switch (status) {
                        case 1:
                        case 3:
                            medalDTO.setGiftSituation(3); // 已经抽奖未中奖
                            medalDTO.setStatus(3); //勋章已经领奖
                            break;

                        case 2:  //2：已经抽奖，获奖了，但是没有领取。
                            medalDTO.setGiftSituation(2); //已抽奖(完成抽奖但未填写详细信息领取奖励)
                            break;
                    }
                    //3代表已领奖
                    medalDTO.setStatus(3);
                    break;
                }
            }
            mAdapter.resetView();
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_bottom);
    }

    private void setDot() {
        int size = medalDTOs.size();
        for (int i = 0; i < size; i++) {
            mLinearDot.addView(getDot(i == 0));
        }
    }

    private View getDot(boolean isFirst) {
        View view = new View(this);
        mDotDrawable = getResources().getDrawable(R.drawable.bg_dot_white_and_gray);
        view.setBackgroundDrawable(mDotDrawable);
        view.setEnabled(false);
        if (!isFirst) {
            ll.leftMargin = getResources().getDimensionPixelSize(R.dimen.dimen_5);
        } else {
            ll.leftMargin = 0;
        }
        view.setLayoutParams(ll);
        return view;
    }

    @Override
    public void onMedalButtonClick(MedalDTO medalDTO) {
        if (medalDTO.getActivityId() <= 0 || medalDTO.getGiftId() <= 0) {
            this.finish();
        } else {
            Intent intent = new Intent(this, BrowserActivity.class);

            final StringBuilder sb = new StringBuilder(Constants.UrlConfig.DEV_SPEEDX_HOST);
            if (medalDTO.getGiftType() == 3 && medalDTO.getGiftSituation() != 2) {
                sb.append(URL_LOTTERY);
            } else {
                sb.append(URL_RECEIVE_AWARD);
            }
            sb.append(medalDTO.getActivityId());
            intent.setData(Uri.parse(sb.toString()));
            this.mRequestCode = medalDTO.getActivityId();
            startActivityForResult(intent, mRequestCode);
        }
    }

    @Override
    public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int position) {
        // 取余后的索引，得到新的page的索引
        int newPosition = position % medalDTOs.size();
        // 把上一个点设置为被选中
        mLinearDot.getChildAt(preDotPosition).setEnabled(false);
        // 根据索引设置那个点被选中
        mLinearDot.getChildAt(newPosition).setEnabled(true);
        // 新索引赋值给上一个索引的位置
        preDotPosition = newPosition;
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }
}
