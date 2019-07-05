package com.beastbikes.android.ble.ui;


import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.baidu.mapapi.search.core.PoiInfo;
import com.baidu.mapapi.search.core.SearchResult;
import com.baidu.mapapi.search.poi.OnGetPoiSearchResultListener;
import com.baidu.mapapi.search.poi.PoiAddrInfo;
import com.baidu.mapapi.search.poi.PoiCitySearchOption;
import com.baidu.mapapi.search.poi.PoiDetailResult;
import com.baidu.mapapi.search.poi.PoiResult;
import com.baidu.mapapi.search.poi.PoiSearch;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.utils.Toasts;

import java.util.ArrayList;
import java.util.List;

@LayoutResource(R.layout.activity_search_location)
public class SearchLocationActivity extends SessionFragmentActivity implements TextWatcher{

    public static String EXTRA_CTIY = "ctiy";

    @IdResource(R.id.activity_seach_loction_rv)
    private RecyclerView rv;

    @IdResource(R.id.activity_seach_loction_et)
    private EditText et;

    @IdResource(R.id.activity_seach_loction_finsh)
    private TextView finsh;

    private List<PoiInfo> poiInfos = new ArrayList<PoiInfo>();
    private PoiSearch poiSearch;
    private String ctiy = "";
    private SearchAdpter adpter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        initPoi();
        initView();
    }

    public void finish() {
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
        super.finish();
    }
    private void initPoi(){
        Intent intent = getIntent();
        ctiy=intent.getStringExtra(EXTRA_CTIY);
        ctiy = "北京";
        adpter = new SearchAdpter(poiInfos ,this);
        OnGetPoiSearchResultListener onGetPoiSearchResultListener = new OnGetPoiSearchResultListener() {
            @Override
            public void onGetPoiResult(PoiResult poiResult) {
                if (poiResult == null
                        || poiResult.error == SearchResult.ERRORNO.RESULT_NOT_FOUND) {
                    clearRv();
                    return;
                } else if (poiResult.error == SearchResult.ERRORNO.NO_ERROR) {// 检索结果正常返回
                    poiInfos.clear();
                    poiInfos.addAll(poiResult.getAllPoi());
                    adpter.notifyDataSetChanged();
                }
            }
            @Override
            public void onGetPoiDetailResult(PoiDetailResult arg0) {

            }
        };
        poiSearch = PoiSearch.newInstance();
        poiSearch.setOnGetPoiSearchResultListener(onGetPoiSearchResultListener);
    }
    private void clearRv(){
        poiInfos.clear();
        adpter.notifyDataSetChanged();
    }
   private void initView(){
       et.addTextChangedListener(this);
       rv.setLayoutManager(new LinearLayoutManager(this));
       rv.setAdapter(adpter);
       et.setOnTouchListener(new View.OnTouchListener() {
           @Override
           public boolean onTouch(View v, MotionEvent event) {
               //getCompoundDrawables() 可以获取一个长度为4的数组，
               //存放drawableLeft，Right，Top，Bottom四个图片资源对象
               //index=2 表示的是 drawableRight 图片资源对象
               Drawable drawable = et.getCompoundDrawables()[2];
               if (drawable == null)
                   return false;

               if (event.getAction() != MotionEvent.ACTION_UP)
                   return false;

               //drawable.getIntrinsicWidth() 获取drawable资源图片呈现的宽度
               if (event.getX() > et.getWidth() - et.getPaddingRight()
                       - drawable.getIntrinsicWidth()) {
                   et.setText("");
               }

               return false;
           }
       });
       finsh.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               finish();
           }
       });
   }

    @Override
    public void beforeTextChanged(CharSequence s, int start, int count, int after) {
    }

    @Override
    public void onTextChanged(CharSequence s, int start, int before, int count) {
        if (TextUtils.isEmpty(ctiy)&& TextUtils.isEmpty(s))
            return;
        PoiCitySearchOption poiCitySearchOption = new PoiCitySearchOption().city(ctiy).
                keyword(s.toString());
        poiSearch.searchInCity(poiCitySearchOption);
    }

    @Override
    public void afterTextChanged(Editable s) {
        if (TextUtils.isEmpty(s)){
            clearRv();
        }
    }
}
