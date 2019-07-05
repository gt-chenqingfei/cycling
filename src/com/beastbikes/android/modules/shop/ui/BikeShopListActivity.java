package com.beastbikes.android.modules.shop.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.res.annotation.MenuResource;
import com.beastbikes.framework.ui.android.lib.view.search.MySearchBar;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by chenqingfei on 16/6/16.
 */
@MenuResource(R.menu.bikeshop_map_switch)
@LayoutResource(R.layout.activity_bike_shop_list)
public class BikeShopListActivity extends SessionFragmentActivity implements View.OnClickListener {

    @IdResource(R.id.bike_shop_tab_mine)
    private TextView tabMine;

    @IdResource(R.id.bike_shop_tab_container)
    private View tabContainer;

    @IdResource(R.id.bike_shop_tab_all)
    private TextView tabAll;

    @IdResource(R.id.bike_shop_frag_search_container)
    private LinearLayout searchContainer;

    private FragmentManager fragmentManager;

    private int[] fragmentIds = {R.id.bike_shop_tab_all/*, R.id.bike_shop_tab_mine*/};
    private Map<Integer, BikeShopListFrag> fragments = new HashMap<Integer, BikeShopListFrag>();

    private MySearchBar searchBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null)
            bar.setDisplayHomeAsUpEnabled(true);

        searchBar = new MySearchBar(this, getString(R.string.club_search_search_btn),
                getString(R.string.label_search_bike_shop_hint));
        searchContainer.addView(searchBar);

        tabMine.setOnClickListener(this);
        tabAll.setOnClickListener(this);
        fragmentManager = getSupportFragmentManager();

        onTabChanged(tabAll);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.bike_shop_tab_all:
                searchContainer.setVisibility(View.VISIBLE);
                onTabChanged(v);
                break;
            case R.id.bike_shop_tab_mine:
                searchContainer.setVisibility(View.GONE);
                onTabChanged(v);
                break;
        }

    }

    private void onTabChanged(View v) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        Bundle bundle = new Bundle();

        if(fragmentIds.length<=1){
            tabContainer.setVisibility(View.GONE);
        }

        for (int i = 0; i < fragmentIds.length; i++) {
            int ids = fragmentIds[i];
            BikeShopListFrag fragment = fragments.get(ids);
            if (v.getId() == ids) {
                v.setSelected(true);
                bundle.putInt(BikeShopListFrag.EXTRA_TYPE, v.getId());
                if (fragment == null) {
                    fragment = (BikeShopListFrag) Fragment.instantiate(this, BikeShopListFrag.class.getName());
                    fragment.setArguments(bundle);
                    fragmentTransaction.add(R.id.bike_shop_list_frag, fragment);
                    fragments.put(v.getId(),fragment);
                } else {
                    fragmentTransaction.show(fragment);
                }
                searchBar.setSearchBarListener(fragment);
            } else {
                findViewById(ids).setSelected(false);
                if (fragment != null) {
                    fragmentTransaction.hide(fragment);
                }
            }


        }
        fragmentTransaction.commitAllowingStateLoss();

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.bike_shop_map_switch:
                this.startActivity(new Intent(this, BikeShopMapActivity.class));
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }

}
