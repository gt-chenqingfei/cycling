package com.beastbikes.android.widget;

import android.content.Context;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beastbikes.android.R;


import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by caoxiao on 16/1/7.
 */
public class SwipeRefreshLoadRecyclerView extends FrameLayout {
    private Context context;
    private ViewGroup parentView;

    private LinearLayoutManager mLinearLayoutManager;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private RecyclerView mRecyclerView;

    private List<?> mDataList = new ArrayList<>();
    private Object headerDate;
    private SimpleStringRecyclerViewAdapter mAdapter;

    private boolean isRefresh = false;
    private boolean isLoading = false;

    private boolean canLoadMore = true;

    private int lastVisibleItem;

    private RecyclerCallBack recyclerCallBack;
    //    private RecyclerViewViewHolderCallBack recyclerViewViewHolderCallBack;
    private BaseRecyclerViewViewAdapter baseRecyclerViewViewAdapter;

    public static final int NORMAL = 0;//都没有
    public static final int HASHEADER = 1;//有header
    public static final int HASFOOTER = 2;//有footer
    public static final int HEADERANDHASFOOTER = 3;//都有

    public SwipeRefreshLoadRecyclerView(Context context, ViewGroup parentView, List<?> mDataList) {
        this(context, null, parentView, mDataList, NORMAL);
    }

    public SwipeRefreshLoadRecyclerView(Context context, ViewGroup parentView, List<?> mDataList, int type) {
        this(context, null, parentView, mDataList, type);
    }

    //需要传递parentView
    public SwipeRefreshLoadRecyclerView(Context context, AttributeSet attrs, ViewGroup parentView, List<?> mDataList, int type) {
        super(context, attrs);
        this.context = context;
        this.parentView = parentView;
        this.mDataList = mDataList;
        initView(type);
    }

    private void initType(int type) {
        switch (type) {
            case HASHEADER:
                mAdapter.setHasHeader(true);
                break;
            case HASFOOTER:
                mAdapter.setHasFooter(true);
                break;
            case HEADERANDHASFOOTER:
                mAdapter.setHasHeader(true);
                mAdapter.setHasFooter(true);
                break;
        }
    }

    public void setRecyclerCallBack(RecyclerCallBack recyclerCallBack) {
        this.recyclerCallBack = new WeakReference<>(recyclerCallBack).get();
    }

    public void setAdapter(BaseRecyclerViewViewAdapter baseRecyclerViewViewAdapter) {
        if (baseRecyclerViewViewAdapter != null)
            this.baseRecyclerViewViewAdapter = baseRecyclerViewViewAdapter;
    }

    private void initView(final int type) {
        View view = LayoutInflater.from(context).inflate(R.layout.layout_swiperecyclerview, parentView);
//        this.setBackgroundColor(getResources().getColor(R.color.blackSix));
        mSwipeRefreshLayout = (SwipeRefreshLayout) view.findViewById(R.id.swipe_refresh_layout);
        mRecyclerView = (RecyclerView) view.findViewById(R.id.recycler_view);
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setColorSchemeResources(R.color.designcolor_c7);
        mRecyclerView.setLayoutManager(mLinearLayoutManager = new LinearLayoutManager(context));
        mAdapter = new SimpleStringRecyclerViewAdapter(mDataList);
        initType(type);
        mAdapter.setHasMoreData(true);
//        mAdapter.setHasFooter(true);

        mRecyclerView.setAdapter(mAdapter);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {

                if (isRefresh || isLoading)
                    return;
//                Log.e("刷新", "刷新");
                isRefresh = true;
                mSwipeRefreshLayout.setRefreshing(true);

                if (recyclerCallBack != null)
                    recyclerCallBack.refreshCallBack();
//                mRecyclerView.getAdapter().notifyDataSetChanged();

            }
        });

        mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {

            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);
                lastVisibleItem = mLinearLayoutManager.findLastVisibleItemPosition();
            }

            @Override
            public void onScrollStateChanged(RecyclerView recyclerView, int newState) {

                if (canLoadMore && !isRefresh && !isLoading && mAdapter.hasMoreData() && lastVisibleItem + 1 == mAdapter.getItemCount()) {
                    isLoading = true;
//                    initType(type);
                    mSwipeRefreshLayout.setEnabled(false);
//                    Log.e("加载", "加载");
                    int lastPos = mLinearLayoutManager.findLastVisibleItemPosition();
                    if (lastPos > mDataList.size() - 2) {//最后一个位置的时候加载更多
                        isRefresh = true;
                        isRefresh = false;
                        if (recyclerCallBack != null)
                            recyclerCallBack.loadMoreCallBack();
                    }
                }
            }
        });
    }

    public void setRefreshEnable(boolean enable) {
        if (mSwipeRefreshLayout != null)
            mSwipeRefreshLayout.setEnabled(enable);
    }

    //加载或者刷新完完成后调用
    public void finishLoad() {
        isLoading = false;
        isRefresh = false;
        isRefresh = false;
        mSwipeRefreshLayout.setEnabled(true);
        mSwipeRefreshLayout.setRefreshing(false);
    }

    public void notifyDataSetChanged() {
        if (null != mAdapter) {
            mAdapter.notifyDataSetChanged();
        }
    }

    public void notifyItemChanged(int position) {
        if (null != mAdapter) {
            mAdapter.notifyItemChanged(position);
            mRecyclerView.setItemAnimator(null);
        }
    }

    public void noMoreData(boolean noMoreData) {
        mAdapter.setHasMoreDataAndFooter(noMoreData, mAdapter.isHasFooter());
    }

    public void setHasFooter(boolean hasFooter) {
        mAdapter.setHasFooter(hasFooter);
    }

    public void hintFooter() {
        mAdapter.hintFooter();
    }

    public boolean isCanLoadMore() {
        return canLoadMore;
    }

    public void setCanLoadMore(boolean canLoadMore) {
        this.canLoadMore = canLoadMore;
    }

    public void setHasHeader(boolean hasHeader) {
        mAdapter.setHasHeader(hasHeader);
    }

    public Object getHeaderDate() {
        return headerDate;
    }

    public boolean isRefresh() {
        return isRefresh;
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setHeaderDate(Object headerDate) {
        this.headerDate = headerDate;
        mAdapter.notifyDataSetChanged();
    }

    public class SimpleStringRecyclerViewAdapter
            extends RecyclerView.Adapter {
        private static final int TYPE_HEADER = Integer.MIN_VALUE;
        private static final int TYPE_FOOTER = Integer.MIN_VALUE + 1;
        private static final int TYPE_CONTENT = Integer.MIN_VALUE + 2;
        //        private static final int TYPE_ADAPTEE_OFFSET = 2;
        private List<?> mValues;
        private boolean hasFooter;
        private boolean hasHeader;
        private boolean hasMoreData;

        private RecyclerView.ViewHolder itemViewHolder;
        private RecyclerView.ViewHolder headViewHolder;

        private int lastItemPositon = 0;

        public int getBasicItemCount() {
            if (mValues == null || mValues.size() == 0)
                return 0;
            return mValues.size();
        }


        public class FooterViewHolder extends RecyclerView.ViewHolder {
            public final ProgressBar mProgressView;
            public final TextView mTextView;

            public FooterViewHolder(View view) {
                super(view);
                mProgressView = (ProgressBar) view.findViewById(R.id.progress_view);
                mTextView = (TextView) view.findViewById(R.id.tv_content);
            }
        }

        public Object getValueAt(int position) {
            return mValues.get(position);
        }

        public SimpleStringRecyclerViewAdapter(List<?> items) {
            mValues = items;
        }

        @Override
        public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
            if (viewType == TYPE_FOOTER) {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_view_load_more, parent, false);
                return new FooterViewHolder(view);
            } else if (viewType == TYPE_HEADER) {
                if (baseRecyclerViewViewAdapter != null) {
                    headViewHolder = baseRecyclerViewViewAdapter.getHeadViewHolder();
                }
                return headViewHolder;
            } else {

                if (baseRecyclerViewViewAdapter != null) {
                    itemViewHolder = baseRecyclerViewViewAdapter.getItemViewHolder();
                }
                return itemViewHolder;
            }
        }

        @Override
        public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {

            if (holder instanceof FooterViewHolder) {
                //没有更多数据
                if (hasMoreData) {
                    ((FooterViewHolder) holder).mProgressView.setVisibility(View.VISIBLE);
                    ((FooterViewHolder) holder).mTextView.setText(getResources().getText(R.string.loadmore));
                } else {
                    ((FooterViewHolder) holder).mProgressView.setVisibility(View.GONE);
                    ((FooterViewHolder) holder).mTextView.setText(getResources().getString(R.string.nomoredate));
                }
            } else {
                Object value = null;

                if (hasHeader) {
                    if (position == 0) {//返回header的values
                        value = headerDate;
                    }
                    if (position >= 1 && position <= getItemCount())
                        value = mValues.get(position - 1);
                    if (position == lastItemPositon) {
                        baseRecyclerViewViewAdapter.onBindViewHolder(holder, value, position - 1, true);
                    } else {
                        baseRecyclerViewViewAdapter.onBindViewHolder(holder, value, position - 1, false);
                    }
                } else {
                    if (position >= 0 && position <= getItemCount() - 1)
                        value = mValues.get(position);
                    if (position == lastItemPositon) {
                        baseRecyclerViewViewAdapter.onBindViewHolder(holder, value, position, true);
                    } else {
                        baseRecyclerViewViewAdapter.onBindViewHolder(holder, value, position, false);
                    }
                }
            }
        }

        @Override
        public int getItemCount() {
            getLastItemPosition();
            if (getBasicItemCount() == 0) {
                return mValues.size() + (hasHeader ? 1 : 0);
            }
            return mValues.size() + (hasFooter ? 1 : 0) + (hasHeader ? 1 : 0);
        }

        private void getLastItemPosition() {
            if (hasFooter) {
                lastItemPositon = mValues.size() + (hasFooter ? 1 : 0) + (hasHeader ? 1 : 0) - 2;
            } else {
                lastItemPositon = mValues.size() + (hasFooter ? 1 : 0) + (hasHeader ? 1 : 0) - 1;
            }
        }

        @Override
        public int getItemViewType(int position) {
            if (position == 0) {
                if (hasHeader) {
                    return TYPE_HEADER;
                } else {
                    return TYPE_CONTENT;
                }
            } else if (position == getItemCount() - 1 && hasFooter && getBasicItemCount() > 0) {
                return TYPE_FOOTER;
            } else {
                return TYPE_CONTENT;
            }
        }

        public boolean hasFooter() {
            return hasFooter;
        }

        public boolean hasHeader() {
            return hasHeader;
        }

        public void setHasFooter(boolean hasFooter) {
            this.hasFooter = hasFooter;
            mAdapter.notifyDataSetChanged();
        }

        public void setHasHeader(boolean hasHeader) {
            this.hasHeader = hasHeader;
        }

        public void hintFooter() {
            mAdapter.setHasFooter(false);
            mAdapter.notifyDataSetChanged();
            mAdapter.setHasFooter(true);
        }

        public boolean hasMoreData() {
            return hasMoreData;
        }

        public void setHasMoreData(boolean isMoreData) {
            if (this.hasMoreData != isMoreData) {
                this.hasMoreData = isMoreData;
                notifyDataSetChanged();
            }
        }

        public void setHasMoreDataAndFooter(boolean hasMoreData, boolean hasFooter) {
            if (this.hasMoreData != hasMoreData || this.hasFooter != hasFooter) {
                this.hasMoreData = hasMoreData;
                this.hasFooter = hasFooter;
                notifyDataSetChanged();
            }
        }

        public boolean isHasFooter() {
            return hasFooter;
        }
    }


    public interface RecyclerViewViewHolderCallBack {

        RecyclerView.ViewHolder getItemViewHolder();

        void onBindViewHolder(RecyclerView.ViewHolder holder, Object value, int positon, boolean isLastItem);
    }

    public interface RecyclerCallBack {

        void refreshCallBack();

        void loadMoreCallBack();
    }

    public static abstract class BaseRecyclerViewViewAdapter implements SwipeRefreshLoadRecyclerView.RecyclerViewViewHolderCallBack {
        public RecyclerView.ViewHolder getHeadViewHolder() {
            return null;
        }
    }

}
