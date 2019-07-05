package com.beastbikes.android.modules.cycling.club.ui.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.cycling.club.ui.adapter.AdapterClubFeedBanner;
import com.beastbikes.android.modules.cycling.club.ui.widget.MyViewPager;
import com.beastbikes.android.modules.shop.ui.BikeShopDetailActivity;
import com.beastbikes.android.widget.PageIndicator;
import com.beastbikes.android.widget.SwipeRefreshAndLoadLayout;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.beastbikes.android.widget.stickylistlibrary.stickylistheaders.StickyListHeadersListView;

/**
 * Created by chenqingfei on 15/12/3.
 */

public class FeedItemHeader extends LinearLayout implements View.OnClickListener {

    private MyViewPager pagerBanner;
    protected PageIndicator mPageIndicator;
    private TextView clubCityTv;
    private TextView clubTotalDistanceTv;
    private TextView clubMemberCount;
    private ClubInfoCompact clubInfo;
    private AdapterClubFeedBanner clubFeedBanner;
    private Context context;
    private ImageView clubTypeIV;
    private LinearLayout clubTypeLayout;

    private Logger logger = LoggerFactory.getLogger(FeedItemHeader.class);

    public FeedItemHeader(Context context) {
        super(context);
        this.context = context;
        initView();
    }

    @SuppressLint("WrongViewCast")
    private void initView() {
        LayoutInflater.from(this.getContext()).inflate(R.layout.clubfeed_header, this);
        this.pagerBanner = (MyViewPager) findViewById(R.id.pager_banner);
        this.mPageIndicator = (PageIndicator) findViewById(R.id.indicator);

        this.pagerBanner.setAdapter(clubFeedBanner = new AdapterClubFeedBanner(clubInfo, getContext()));
        this.pagerBanner.setCurrentItem(0);
        this.mPageIndicator.setViewPager(pagerBanner);
        this.clubTotalDistanceTv = (TextView) findViewById(R.id.club_info_total_distance);
        this.clubMemberCount = (TextView) findViewById(R.id.club_info_member_count);
        this.clubCityTv = (TextView) findViewById(R.id.club_info_location);
        this.clubTypeIV = (ImageView) findViewById(R.id.club_feed_info_type);
        this.clubTypeLayout = (LinearLayout) findViewById(R.id.club_feed_info_type_ll);
    }

    public void notify(ClubInfoCompact clubInfo) {
        this.clubInfo = clubInfo;
        this.clubCityTv.setText(clubInfo.getCity());
        if (LocaleManager.isDisplayKM(context)) {
            this.clubTotalDistanceTv.setText(Math.round(clubInfo.getMilestone() / 1000) + "" + getResources().getText(R.string.kilometre));
        } else {
            this.clubTotalDistanceTv.setText(Math.round(LocaleManager.kilometreToMile(clubInfo.getMilestone() / 1000)) + "" + getResources().getText(R.string.mi));
        }
        this.clubMemberCount.setText(clubInfo.getMembers() + "" + getResources().getText(R.string.person));
        this.clubFeedBanner.notifyDataSetChanged(clubInfo);
        if(LocaleManager.isChineseTimeZone()) {
            switch (clubInfo.getType()) {
                case 0:
                    clubTypeIV.setImageResource(0);
                    break;
                case 1:
                    clubTypeIV.setImageResource(R.drawable.ic_club_type_shop);
                    this.clubTypeLayout.setOnClickListener(this);
                    break;
                case 2:
                    clubTypeIV.setImageResource(R.drawable.ic_club_type_school);
                    this.clubTypeLayout.setOnClickListener(this);
                    break;
            }
        }
        else{
            clubTypeIV.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onClick(View view) {
        Intent intent = new Intent(context, BikeShopDetailActivity.class);
        intent.putExtra(BikeShopDetailActivity.INTENT_SHOP_ID, clubInfo.getLinkTo());
        context.startActivity(intent);
    }

    public void setViewPagerNestedpParent(SwipeRefreshAndLoadLayout swipeRefreshAndLoadLayout, StickyListHeadersListView stickyList) {
        if (pagerBanner != null)
            pagerBanner.setNestedpParent(swipeRefreshAndLoadLayout, stickyList);
    }
}