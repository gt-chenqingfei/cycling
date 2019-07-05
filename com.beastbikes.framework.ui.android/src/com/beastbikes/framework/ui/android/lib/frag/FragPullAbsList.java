package com.beastbikes.framework.ui.android.lib.frag;

import android.os.Bundle;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.beastbikes.framework.ui.android.R;
import com.beastbikes.framework.ui.android.lib.list.BaseListAdapter;
import com.beastbikes.framework.ui.android.lib.list.PageData;
import com.beastbikes.framework.ui.android.lib.list.PageRefreshData;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.AbsListProxable;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.DensityUtil;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.PullProxyFactory;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.PullRefeshListener;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.PullToRefrehGridStringProxy;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.PullToRefreshAbsListViewProxy;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.PullToRefreshAdapterViewBase;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.PullToRefreshGridView;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.PullToRefreshListViewProxy;
import com.beastbikes.framework.ui.android.lib.pulltorefresh.PullToRefreshProxy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.List;

public abstract class FragPullAbsList<K, D, V extends AbsListView> extends
        FragBasePull<K, V> implements OnItemClickListener,AdapterView.OnItemLongClickListener {

    public static final int COUNT = 20;
    private static final String KEY_CLS_VIEW = "cls_view";
    private static final String KEY_CLS_KEY = "cls_key";
    private static final String KEY_CLS_ITEM = "cls_item";
    public AbsListProxable<K, D> absProxy = null;
    protected Class<?> clsKey;
    protected Class<?> clsItem;
    protected Class<?> clsView;

    protected LinearLayout headerContainer;
    protected LinearLayout footerContainer;

    private CharSequence emptyText = "暂时还没有相关的数据";
    private int emptyTxtColor = 0x33000000;
    private TextView emptyView;

    // ==========abstract methods============
    protected abstract BaseListAdapter<D> adapterToDisplay(AbsListView view);

    // ========life cycle event==========

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (savedInstanceState != null) {
            clsKey = (Class<?>) savedInstanceState.getSerializable(KEY_CLS_KEY);
            clsItem = (Class<?>) savedInstanceState
                    .getSerializable(KEY_CLS_ITEM);
            clsView = (Class<?>) savedInstanceState
                    .getSerializable(KEY_CLS_VIEW);
        } else {
            initClasses();
        }
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putSerializable(KEY_CLS_KEY, clsKey);
        outState.putSerializable(KEY_CLS_ITEM, clsItem);
        outState.putSerializable(KEY_CLS_VIEW, clsView);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        emptyView = new TextView(getActivity());
        emptyView.setLineSpacing(0, 1.2f);
        emptyView.setGravity(Gravity.CENTER_HORIZONTAL);
        int padding = DensityUtil.dip2px(getActivity(), 30);
        emptyView.setPadding(padding, padding, padding, padding);
        emptyView.setTextSize(20);
        emptyView.setText(emptyText);
        emptyView.setTextColor(getResources().getColor(android.R.color.white));
        emptyView.setMovementMethod(LinkMovementMethod.getInstance());
        pullView.setEmptyView(emptyView);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        this.absProxy.onStart();
    }

    @Override
    public void onDestroy() {
        if (absProxy != null) {
            this.absProxy.onStop();
        }
        super.onDestroy();

    }

    ;

    @Override
    protected final PullToRefreshProxy<K, V> getPullProxy() {
        PullToRefreshAbsListViewProxy<K, D, V> absPullProxy = this
                .getAbsPullProxy();
        if (!(absPullProxy instanceof AbsListProxable)) {
            throw new UnsupportedClassVersionError(
                    "proxy must be implements AbsListProxable");
        }
        absProxy = absPullProxy;
        return absPullProxy;

    }

    @SuppressWarnings("unchecked")
    protected PullToRefreshAbsListViewProxy<K, D, V> getAbsPullProxy() {
        if (clsKey == Long.class && clsView == ListView.class) {
            initHeaderAndFooter();
            PullToRefreshListViewProxy<Long, D> proxy = new PullToRefreshListViewProxy<Long, D>(
                    this.adapterToDisplay(this.internalView),
                    (PullToRefreshAdapterViewBase<ListView>) pullView,
                    this.cacheKey(), (PullRefeshListener<Long>) this,
                    headerContainer, footerContainer,
                    PullProxyFactory.getDefaultLongPageable());

            proxy.setPullHeader(getPullHeader());
            proxy.setOnItemClickListener(this);
            proxy.setOnItemLongClickListener(this);
            proxy.getInternalView().setFooterDividersEnabled(false);

            return (PullToRefreshAbsListViewProxy<K, D, V>) proxy;
        } else if (clsKey == String.class && clsView == ListView.class) {
            initHeaderAndFooter();
            PullToRefreshListViewProxy<String, D> proxy = new PullToRefreshListViewProxy<String, D>(
                    this.adapterToDisplay(this.internalView),
                    (PullToRefreshAdapterViewBase<ListView>) pullView,
                    this.cacheKey(), (PullRefeshListener<String>) this,
                    headerContainer, footerContainer,
                    PullProxyFactory.getDefaultStringPageable());

            proxy.setPullHeader(getPullHeader());
            proxy.setOnItemClickListener(this);
            proxy.setOnItemLongClickListener(this);
            proxy.getInternalView().setFooterDividersEnabled(false);

            return (PullToRefreshAbsListViewProxy<K, D, V>) proxy;
        } else if (clsView == GridView.class) {
            PullToRefrehGridStringProxy<D> proxy = new PullToRefrehGridStringProxy<D>(
                    this.adapterToDisplay(internalView),
                    (PullToRefreshGridView) pullView, this.cacheKey(),
                    (PullRefeshListener<String>) this,
                    PullProxyFactory.getDefaultStringPageable());

            proxy.setPullHeader(getPullHeader());
            proxy.setOnItemClickListener(this);
            proxy.setOnItemLongClickListener(this);

            return (PullToRefreshAbsListViewProxy<K, D, V>) proxy;
        }
        return null;
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
        View header = getHeaderView();
        if (header != null) {
            headerContainer.addView(header);
        }
        View footer = getFooterView();
        if (footer != null) {
            footerContainer.addView(footer);
        }
    }

    @Override
    public void onItemClick(AdapterView<?> arg0, View arg1, int position,
                            long arg3) {

        int pos = position;
        if (internalView instanceof ListView) {
            ListView lv = (ListView) internalView;
            int headerCount = lv.getHeaderViewsCount();
            pos = position - headerCount;
        }

        if (pos >= 0 && pos < this.absProxy.getAdapter().getCount()) {
            D item = this.absProxy.getAdapter().getItem(pos);
            if (item != null) {
                this.onItemClick(item);
            }
        }
    }

    @Override
    public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
        int pos = position;
        if (internalView instanceof ListView) {
            ListView lv = (ListView) internalView;
            int headerCount = lv.getHeaderViewsCount();
            pos = position - headerCount;
        }

        if (pos >= 0 && pos < this.absProxy.getAdapter().getCount()) {
            D item = this.absProxy.getAdapter().getItem(pos);
            if (item != null) {
                this.onItemLongClick(item);
            }
        }
        return true;
    }

    public void onItemLongClick(D item) {

    }

    protected void onItemClick(D item) {

    }

    @Override
    public void loadRefreshMore(K maxId, long lastModify) {

    }

    @Override
    public void loadMore(K maxId) {

    }

    @Override
    protected void showListContextMenu() {
        this.pullProxy.showContextMenu();
    }

    public void refreshList() {
        this.absProxy.refreshList();
    }

    protected void onLoadFailed(String info) {
        if (getActivity() != null && !TextUtils.isEmpty(info)) {
            Toast.makeText(getActivity(), info, Toast.LENGTH_LONG).show();
        }

        this.pullProxy.onRefreshFinished();
    }

    protected void onLoadSucessfully(List<D> data) {
        this.absProxy.onLoadSucessfully(data);

    }

    protected void onLoadSucessAddfully(List<D> data) {
        if (data != null && data.size() != 0) {
            this.absProxy.onLoadSucessAddfully(data);
        } else {
            this.pullProxy.hidePullUp();
        }

    }

    protected void onLoadSucessfully(PageRefreshData<K, D> dataList) {
        this.absProxy.onLoadSucessfully(dataList);
    }

    protected void onLoadSucessfully(PageData<K, D> dataList) {
        this.absProxy.onLoadSucessfully(dataList);
    }

    protected void onPullDownRefresh() {
        this.pullProxy.onPullDownRefresh();
    }

    // =============header view and footer view=============
    @Deprecated
    protected View getHeaderView() {
        return null;
    }

    @Deprecated
    protected View getFooterView() {
        return null;
    }

    protected void addHeaderView(View view) {
        headerContainer.addView(view);
    }

    protected void addFooterView(View view) {
        footerContainer.addView(view);
    }

    protected void removeHeaderView(View view) {
        headerContainer.removeView(view);
    }

    protected void removeFooterView(View view) {
        footerContainer.removeView(view);
    }

    public void setEmptyText(CharSequence text) {
        this.emptyText = text;
        if (emptyView != null) {
            emptyView.setText(text);
        }
    }

    public void setEmptyText(CharSequence text, int color) {
        this.emptyText = text;
        this.emptyTxtColor = color;
        if (emptyView != null) {
            emptyView.setTextColor(color);
            emptyView.setText(text);
        }
    }

    @Override
    protected int layoutResource() {
        if (clsView == ListView.class) {
            return R.layout.pull_to_refresh_list;
        } else if (clsView == GridView.class) {
            return R.layout.pull_to_refresh_grid;
        }

        return R.id.invalidResId;
    }

    private void initClasses() {
        Class<?> cls = getClass();
        Type[] types = cls.getGenericInterfaces();

        Type superCls = cls.getGenericSuperclass();
        while (!(superCls instanceof ParameterizedType)) {
            cls = cls.getSuperclass();
            superCls = cls.getGenericSuperclass();
        }
        ParameterizedType parameterizedType = (ParameterizedType) superCls;
        types = parameterizedType.getActualTypeArguments();
        clsKey = (Class<?>) types[0];
        clsItem = (Class<?>) types[1];

        if (types.length < 3) {
            while (!(superCls instanceof ParameterizedType)
                    || ((ParameterizedType) superCls).getActualTypeArguments().length < 3) {
                cls = cls.getSuperclass();
                superCls = cls.getGenericSuperclass();
            }
            parameterizedType = (ParameterizedType) superCls;
            types = parameterizedType.getActualTypeArguments();
        }
        if (clsView == null) {
            clsView = (Class<?>) types[2];
        }
    }

    public void scrollToTop(boolean refresh) {
        if (clsView == ListView.class) {
            ListView lv = (ListView) internalView;
            lv.setSelection(0);
            if (refresh) {
                pullProxy.pullDownToRefresh();
            }
        } else if (clsView == GridView.class) {
            GridView gv = (GridView) internalView;
            gv.setSelection(0);
            if (refresh) {
                pullProxy.pullDownToRefresh();
            }
        }
    }
}
