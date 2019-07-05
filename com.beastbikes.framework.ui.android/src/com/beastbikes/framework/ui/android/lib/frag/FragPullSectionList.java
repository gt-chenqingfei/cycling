package com.beastbikes.framework.ui.android.lib.frag;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;

import android.app.Activity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MotionEvent;
import android.view.View;
import android.view.View.OnTouchListener;
import android.view.ViewGroup.LayoutParams;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.ExpandableListView;
import android.widget.ExpandableListView.OnChildClickListener;
import android.widget.ExpandableListView.OnGroupCollapseListener;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.beastbikes.framework.ui.android.R;
import com.beastbikes.framework.ui.android.lib.list.BaseSectionListAdapter;
import com.beastbikes.framework.ui.android.lib.list.PageData;
import com.beastbikes.framework.ui.android.lib.list.PageRefreshData;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.ExpandableSectionList;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.Groupable;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.PullProxyFactory;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.PullRefeshListener;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.PullToRefreshSectionListProxy;

public abstract class FragPullSectionList<K, G extends Groupable<C>, C> extends
        FragBasePull<K, ExpandableSectionList> implements OnChildClickListener,
        PullRefeshListener<K> {

    protected PullToRefreshSectionListProxy<K, G, C> sectionProxy;
    protected BaseSectionListAdapter<G, C> sectionAdapter;
    protected Class<?> clsKey;

    protected LinearLayout headerContainer;
    protected LinearLayout footerContainer;

    // ===========abstract methods=========
    protected abstract BaseSectionListAdapter<G, C> adapterToDisplay(
            AbsListView view);

    /**
     * must same as your groupview's layout, absolutely same, so the best way is
     * put your group layout in a xml file, and then return xml's reousrce id.
     */
    protected int sectionResource() {
        return R.id.invalidResId;
    }

    /**
     * if you do not want to put section in xml file, you can return section
     * view diresctly
     *
     * @return
     */
    protected View sectionView() {
        return null;
    }

    @Override
    protected final PullToRefreshSectionListProxy<K, G, C> getPullProxy() {
        this.initHeaderAndFooter();
        internalView.addHeaderView(headerContainer);
        internalView.addFooterView(footerContainer);
        sectionProxy = getSectionPullProxy();
        sectionAdapter = sectionProxy.getAdapter();
        View sectionView = this.sectionView();
        int secViewId = this.sectionResource();
        if (sectionView == null && secViewId != R.id.invalidResId) {
            sectionView = getLayoutInflater(null).inflate(secViewId,
                    internalView, false);
        } else if (sectionView != null) {
            LayoutParams param = new AbsListView.LayoutParams(
                    AbsListView.LayoutParams.MATCH_PARENT,
                    AbsListView.LayoutParams.WRAP_CONTENT);
            sectionView.setLayoutParams(param);
        }

        if (sectionView != null) {
            sectionView.setOnTouchListener(new OnTouchListener() {

                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    return false;
                }
            });
        }

        internalView.setPinnedHeaderView(sectionView);
        internalView.setGroupIndicator(null);

        internalView.setOnChildClickListener(this);
        sectionProxy.setOnCreateContextMenuListener(this);
        internalView.setOnGroupCollapseListener(new OnGroupCollapseListener() {

            @Override
            public void onGroupCollapse(int groupPosition) {
                internalView.expandGroup(groupPosition);

            }
        });
        return sectionProxy;
    }

    @SuppressWarnings("unchecked")
    protected PullToRefreshSectionListProxy<K, G, C> getSectionPullProxy() {
        if (clsKey == Long.class) {
            PullToRefreshSectionListProxy<Long, G, C> proxy = new PullToRefreshSectionListProxy<Long, G, C>(
                    this.adapterToDisplay(internalView), this.pullView,
                    this.cacheKey(), (PullRefeshListener<Long>) this,
                    PullProxyFactory.getDefaultLongPageable());

            return (PullToRefreshSectionListProxy<K, G, C>) proxy;
        } else {
            PullToRefreshSectionListProxy<String, G, C> proxy = new PullToRefreshSectionListProxy<String, G, C>(
                    this.adapterToDisplay(internalView), this.pullView,
                    this.cacheKey(), (PullRefeshListener<String>) this,
                    PullProxyFactory.getDefaultStringPageable());

            return (PullToRefreshSectionListProxy<K, G, C>) proxy;
        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initClasses();
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        sectionProxy.onStart();
        sectionProxy.disablePull();
    }

    private void initClasses() {
        Class<?> cls = getClass();
        Type superCls = cls.getGenericSuperclass();
        while (!(superCls instanceof ParameterizedType)) {
            cls = cls.getSuperclass();
            superCls = cls.getGenericSuperclass();
        }
        ParameterizedType parameterizedType = (ParameterizedType) superCls;
        Type[] types = parameterizedType.getActualTypeArguments();
        clsKey = (Class<?>) types[0];

    }

    private void initHeaderAndFooter() {
        headerContainer = new LinearLayout(getActivity());
        footerContainer = new LinearLayout(getActivity());
        headerContainer.setOrientation(LinearLayout.VERTICAL);
        footerContainer.setOrientation(LinearLayout.VERTICAL);

        /**
         * here is fix cannot pull up refresh, which maybe listview's bug, when
         * listview's footer's height is 0, the last visible position will never
         * be footer, so pull refresh think listview has more content to
         * display, not calling pulling up to refresh
         */
        footerContainer.setPadding(0, 1, 0, 0); // maybe
    }

    @Override
    public void onDestroy() {
        if (sectionProxy != null) {
            sectionProxy.onStop();
        }
        super.onDestroy();
    }

    ;

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {

    }

    @Override
    public boolean onChildClick(ExpandableListView parent, View v,
                                int groupPosition, int childPosition, long id) {
        C item = this.sectionAdapter.getChild(groupPosition, childPosition);
        if (item != null) {
            this.onItemClick(item);
        }
        return true;
    }

    public void onItemClick(C item) {

    }

    /**
     * override this to use your custom layout
     *
     * @return
     */
    @Override
    protected int layoutResource() {
        return R.layout.pull_to_refresh_section_list;
    }

    @Override
    protected String cacheKey() {
        return getClass().getName();
    }

    protected void setListHeader(View header) {
        headerContainer.removeAllViews();
        if (header != null) {
            headerContainer.addView(header);
        }
    }

    protected void addHeaderView(View header) {
        headerContainer.addView(header);
    }

    protected void addHeaderView(View header, int pos) {
        headerContainer.addView(header, pos);
    }

    protected void addFooterView(View footer) {
        footerContainer.addView(footer);
    }

    protected void removeHeaderView(View header) {
        headerContainer.removeView(header);
    }

    protected void removeFooterView(View footer) {
        footerContainer.removeView(footer);
    }

    @Override
    public void loadMore(K maxId) {

    }

    @Override
    public void loadRefreshMore(K maxId, long lastModify) {

    }

    // /===========wrapped proxy method=================

//	public void onLoadFailed(Failture failture) {
//		sectionProxy.onLoadFailed(failture);
//	}

    protected void onLoadFailed(String info) {
        if (getActivity() != null && !TextUtils.isEmpty(info)) {
            Toast.makeText(getActivity(), info, Toast.LENGTH_LONG).show();
        }
        this.pullProxy.onRefreshFinished();
    }

    public void onLoadSucessfully(ArrayList<G> data) {
        sectionProxy.onLoadSucessfully(data);
    }

    public void onLoadSucessfully(PageRefreshData<K, G> dataList) {
        sectionProxy.onLoadSucessfully(dataList);
    }

    public void onLoadSucessfully(PageData<K, G> dataList) {
        sectionProxy.onLoadSucessfully(dataList);
    }

}