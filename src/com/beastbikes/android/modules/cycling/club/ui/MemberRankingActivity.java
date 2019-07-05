package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

/**
 * Created by caoxiao on 15/12/1.
 */


@Alias("新成员排名")
@LayoutResource(R.layout.activity_memberranking)
public class MemberRankingActivity extends SessionFragmentActivity implements View.OnClickListener {

    @IdResource(R.id.monthrank)
    private TextView monthrank;

    @IdResource(R.id.totalrank)
    private TextView totalrank;

    @IdResource(R.id.btn_back)
    private RelativeLayout btn_back;

    private FragmentManager fm;
    private MonthMemberRankFrag monthMemberRankFrag;
    private TotalMemberRankFrag totalMemberRankFrag;
    public static String clubId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        Intent intent = getIntent();
        if (intent != null) {
            clubId = intent.getStringExtra(ClubFeedInfoFrag.EXTRA_CLUB_ID);
        }
        fm = getSupportFragmentManager();
        monthrank.setSelected(true);
        showFragment(1);
        monthrank.setOnClickListener(this);
        totalrank.setOnClickListener(this);
        btn_back.setOnClickListener(this);

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.monthrank:
                showFragment(1);
                break;
            case R.id.totalrank:
                showFragment(2);
                break;
            case R.id.btn_back:
                finish();
                break;
        }
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    public void showFragment(int index) {
        FragmentTransaction ft = fm.beginTransaction();
        hideFragments(ft);

        switch (index) {
            case 1:
                monthrank.setSelected(true);
                totalrank.setSelected(false);
                if (monthMemberRankFrag != null)
                    ft.show(monthMemberRankFrag);
                else {
                    monthMemberRankFrag = new MonthMemberRankFrag();
                    ft.add(R.id.frag_container, monthMemberRankFrag);
                }
                break;
            case 2:
                totalrank.setSelected(true);
                monthrank.setSelected(false);
                if (totalMemberRankFrag != null)
                    ft.show(totalMemberRankFrag);
                else {
                    totalMemberRankFrag = new TotalMemberRankFrag();
                    ft.add(R.id.frag_container, totalMemberRankFrag);
                }
                break;
        }
        ft.commit();
    }

    // 当fragment已被实例化，就隐藏起来
    public void hideFragments(FragmentTransaction ft) {
        if (monthMemberRankFrag != null)
            ft.hide(monthMemberRankFrag);
        if (totalMemberRankFrag != null)
            ft.hide(totalMemberRankFrag);
    }

}
