package com.beastbikes.android.modules.cycling.route.ui;

import android.content.Context;
import android.content.Intent;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;

import com.avos.avoscloud.AVAnalytics;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.route.biz.RouteManager;
import com.beastbikes.android.modules.cycling.route.dto.RouteCommentAdapter;
import com.beastbikes.android.modules.cycling.route.dto.RouteCommentDTO;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dao.entity.LocalUser;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.ListViewFooter;
import com.beastbikes.android.widget.PullRefreshListView;
import com.beastbikes.android.widget.PullRefreshListView.onListViewListener;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.android.utils.ConnectivityUtils;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;

import java.util.ArrayList;
import java.util.List;

@Alias("路线全部评论页")
@LayoutResource(R.layout.route_comment_activity)
public class RouteCommentActivity extends SessionFragmentActivity implements
        onListViewListener, OnClickListener, OnItemClickListener {

    public static final String EXTRA_ROUTE_ID = "route_id";

    public static final String EXTRA_ROUTE_COMMENT_COUNT = "route_comment_count";

    private static final String TAG = "RouteCommentActivity";

    @IdResource(R.id.route_comment_activity_list)
    private PullRefreshListView listView;

    @IdResource(R.id.route_comment_edit_text)
    private EditText editText;

    @IdResource(R.id.route_comment_send)
    private Button send;

    private RouteManager routeManager;
    private UserManager userManager;

    private RouteCommentAdapter commentAdapter;
    private List<RouteCommentDTO> commentList = new ArrayList<RouteCommentDTO>();

    private String routeId;
    private int count;
    private int pageIndex = 2;// 加载更多默认从第二页开始加载

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.routeId = getIntent().getStringExtra(EXTRA_ROUTE_ID);
        this.count = getIntent().getIntExtra(EXTRA_ROUTE_COMMENT_COUNT, 0);
        super.setTitle(String.format(
                getString(R.string.route_comment_activity_title), count));

        this.routeManager = new RouteManager(this);
        this.userManager = new UserManager(this);

        this.listView.setPullLoadEnable(true);
        this.listView.setPullRefreshEnable(true);
        this.listView.setListViewListener(this);
        this.commentAdapter = new RouteCommentAdapter(commentList);
        this.listView.setAdapter(this.commentAdapter);
        this.listView.setOnItemClickListener(this);

        this.send.setOnClickListener(this);
        this.refreshRouteCommentList();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public void onRefresh() {
        this.refreshRouteCommentList();
    }

    @Override
    public void onLoadMore() {
        this.loadMoreRouteCommentList();
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.route_comment_send:
                String content = this.editText.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    Toasts.show(this, R.string.route_activity_comment_empty_msg);
                    break;
                }

                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(editText.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);

                this.sendComment(content);
                SpeedxAnalytics.onEvent(this, "路线评论总次数",null);
                break;

            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        RouteCommentDTO rcd = (RouteCommentDTO) parent.getAdapter().getItem(
                position);
        if (null == rcd)
            return;

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER_ID, rcd.getUserId());
        intent.putExtra(ProfileActivity.EXTRA_AVATAR, rcd.getAvatarUrl());
        intent.putExtra(ProfileActivity.EXTRA_NICK_NAME, rcd.getNickName());
        intent.putExtra(ProfileActivity.EXTRA_REMARKS, rcd.getRemarks());
        startActivity(intent);
    }

    /**
     * 刷新Listview
     */
    private void refreshRouteCommentList() {
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<RouteCommentDTO>>() {

                    @Override
                    protected List<RouteCommentDTO> doInBackground(
                            String... params) {
                        try {
                            return routeManager.getRouteCommentByRouteId(
                                    params[0], 10, 1);
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<RouteCommentDTO> rcdList) {
                        listView.stopRefresh();
                        if (null == rcdList || rcdList.isEmpty()) {
                            NetworkInfo ni = ConnectivityUtils
                                    .getActiveNetwork(RouteCommentActivity.this);
                            if (ni == null || !ni.isConnected()) {
                                Toasts.show(RouteCommentActivity.this,
                                        R.string.activity_unnetwork_err);
                            } else {
                                Toasts.show(RouteCommentActivity.this,
                                        R.string.route_comment_no_more);
                            }
                            listView.setPullLoadEnable(false);
                            return;
                        }

                        // if (rcdList.size() < 10) {
                        // listView.stopLoadMore(ListViewFooter.STATE_NO_MORE);
                        // listView.setPullLoadEnable(false);
                        // } else {
                        // listView.stopLoadMore(ListViewFooter.STATE_NORMAL);
                        // listView.setPullLoadEnable(true);
                        // }
                        commentList.clear();
                        commentList.addAll(rcdList);

                        final RouteCommentDTO dto = rcdList.get(0);
                        if (!isFinishing()) {
                            setTitle(String.format(
                                    getString(R.string.route_comment_activity_title), dto.getCommentCount()));
                        }
                        commentAdapter.notifyDataSetChanged();

                    }

                }, this.routeId);

    }

    /**
     * 加载更多
     */
    private void loadMoreRouteCommentList() {
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<RouteCommentDTO>>() {

                    @Override
                    protected List<RouteCommentDTO> doInBackground(
                            String... params) {
                        try {
                            return routeManager.getRouteCommentByRouteId(
                                    params[0], 10, pageIndex);
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<RouteCommentDTO> rcdList) {
                        if (null == rcdList || rcdList.isEmpty()) {
                            listView.stopLoadMore(ListViewFooter.STATE_NO_MORE);
                            // listView.setPullLoadEnable(false);

                            NetworkInfo ni = ConnectivityUtils
                                    .getActiveNetwork(RouteCommentActivity.this);
                            if (ni == null || !ni.isConnected()) {
                                Toasts.show(RouteCommentActivity.this,
                                        R.string.activity_unnetwork_err);
                            } else {
                                Toasts.show(RouteCommentActivity.this,
                                        R.string.route_comment_no_more);
                            }

                            return;
                        }

                        // if (rcdList.size() < 10) {
                        // listView.stopLoadMore(ListViewFooter.STATE_NO_MORE);
                        // listView.setPullLoadEnable(false);
                        // } else {
                        listView.stopLoadMore(ListViewFooter.STATE_NORMAL);
                        listView.setPullLoadEnable(true);
                        // }
                        pageIndex = pageIndex + 1;
                        commentList.addAll(rcdList);
                        commentAdapter.notifyDataSetChanged();
                    }

                }, this.routeId);
    }

    /**
     * 发送评论
     *
     * @param content
     */
    private void sendComment(String content) {
        try {
            final LocalUser user = userManager.getLocalUser(getUserId());
            if (user != null) {
                String nickName = user.getNickname();
                if (TextUtils.isEmpty(nickName)) {
                    nickName = user.getUsername();
                }
            }
            this.getAsyncTaskQueue().add(
                    new AsyncTask<String, Void, Integer>() {

                        @Override
                        protected Integer doInBackground(String... params) {
                            try {
                                return routeManager.postRouteComment(params[0], params[1], null);
                            } catch (BusinessException e) {
                                return -1;
                            }
                        }

                        @Override
                        protected void onPostExecute(Integer msg) {
                            if (msg == -1) {
                                Toasts.show(
                                        getApplicationContext(),
                                        R.string.route_activity_comment_commit_err);
                                return;
                            }

                            editText.setText("");
                            Toasts.show(
                                    getApplicationContext(),
                                    R.string.route_activity_comment_commit_sucess);
                            // 刷新评论列表
                            pageIndex = 2;
                            refreshRouteCommentList();
                        }

                    }, this.routeId, content);

        } catch (BusinessException e) {
            Log.e(TAG, "Send comment for route " + this.routeId + " error", e);
        }
    }
}
