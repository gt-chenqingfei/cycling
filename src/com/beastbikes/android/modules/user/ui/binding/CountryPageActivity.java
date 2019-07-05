/*
 * 官网地站:http://www.mob.com
 * 技术支持QQ: 4006852216
 * 官方微信:ShareSDK   （如果发布新版本的话，我们将会第一时间通过微信将版本更新内容推送给您。如果使用过程中有任何问题，也可以通过微信与我们取得联系，我们将会在24小时内给予回复）
 *
 * Copyright (c) 2014年 mob.com. All rights reserved.
 */
package com.beastbikes.android.modules.user.ui.binding;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.user.ui.binding.widget.CountryListView;
import com.beastbikes.android.modules.user.ui.binding.widget.GroupListView;
import com.beastbikes.android.modules.user.ui.binding.widget.SearchEngine;

/**
 * 国家列表界面
 */
public class CountryPageActivity extends SessionFragmentActivity implements GroupListView.OnItemClickListener {
    private CountryListView listView;
    public static final String EXTTA_COUNTRY = "country";
    public static final String EXTTA_COUNTRY_CODE = "code";
    public static final int REQ_COUNTRY = 0x1234;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
            bar.setTitle(getString(R.string.activity_country_page_title));
        }
        // 初始化搜索引擎
        SearchEngine.prepare(CountryPageActivity.this, new Runnable() {

            @Override
            public void run() {
                runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        // 初始化搜索引擎
                        setContentView(R.layout.country_list_page);
                        listView = (CountryListView) findViewById(R.id.clCountry);
                        listView.setOnItemClickListener(CountryPageActivity.this);
                    }
                });
            }
        });
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }


    @Override
    public void onItemClick(GroupListView parent, View view, int group, int position) {
        if (position >= 0) {
            String[] country = listView.getCountry(group, position);
            Intent intent = new Intent();
            if (country != null) {
                intent.putExtra(EXTTA_COUNTRY_CODE, country[1]);
            }
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

}
