package com.beastbikes.android.modules.cycling.route.ui;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.LayoutParams;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Html;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.ScrollView;
import android.widget.TextView;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.Volley;
import com.avos.avoscloud.AVAnalytics;
import com.beastbikes.android.R;
import com.beastbikes.android.locale.LocaleManager;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.route.biz.RouteManager;
import com.beastbikes.android.modules.cycling.route.dto.RouteCommentAdapter;
import com.beastbikes.android.modules.cycling.route.dto.RouteCommentDTO;
import com.beastbikes.android.modules.cycling.route.dto.RouteDTO;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dao.entity.LocalUser;
import com.beastbikes.android.modules.user.ui.ProfileActivity;
import com.beastbikes.android.utils.SpeedxAnalytics;
import com.beastbikes.android.widget.NonScrollListView;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.utils.Toasts;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

@Alias("路线详情")
@LayoutResource(R.layout.route_activity)
public class RouteActivity extends SessionFragmentActivity implements
        OnPageChangeListener, OnClickListener, OnItemClickListener {

    public static final Logger logger = LoggerFactory.getLogger(RouteActivity.class);

    public static final String EXTRA_ROUTE = "route";

    public static final String EXTRA_ROUTE_ID = "route_id";

    private static final float RESOLUTION = 640f / 380f;

    private static final String TAG = "RouteActivity";

    @IdResource(R.id.view_loading)
    private ViewGroup loading;

    @IdResource(R.id.map_loading)
    private ViewGroup mapLoading;

    @IdResource(R.id.route_activity_view_pager)
    private ViewPager pager;

    @IdResource(R.id.route_activity_view)
    private ViewGroup view;

    @IdResource(R.id.route_activity_indicator)
    private ViewGroup indicator;

    @IdResource(R.id.route_activity_line_map)
    private ImageView mapView;

    @IdResource(R.id.route_activity_difficulty_start)
    private RatingBar difficulty;

    //    @IdResource(R.id.route_activity_view_value)
//    private TextView viewTv;
    @IdResource(R.id.route_activity_score_start)
    private RatingBar score;

    @IdResource(R.id.route_activity_traffic_start)
    private RatingBar traffic;

    @IdResource(R.id.route_activity_distance_value)
    private TextView distance;

    @IdResource(R.id.route_activity_distance_unit)
    private TextView distanceUnit;

    @IdResource(R.id.route_activity_want)
    private LinearLayout mLinearWant;

    @IdResource(R.id.route_activity_want_go)
    private TextView wantTv;

    @IdResource(R.id.route_activity_want_go_count)
    private TextView countTv;

    @IdResource(R.id.route_activity_route_view_desc)
    private TextView viewDesc;

    @IdResource(R.id.route_activity_comment_all)
    private TextView commentMore;

    @IdResource(R.id.route_activity_comment_title)
    private TextView commentCountTv;

    @IdResource(R.id.route_activity_comment_list)
    private NonScrollListView listView;

    @IdResource(R.id.route_activity_send_comment_content)
    private EditText editText;

    @IdResource(R.id.route_activity_send_comment)
    private Button sendComment;

    @IdResource(R.id.route_activity_want)
    private ViewGroup want;

    @IdResource(R.id.route_activity_scroll)
    private ScrollView scrollView;

    private ImageView indicators[];

    private ImageView image;
    private ImageView indicImg;
    private TutorialPageAdapter adapter;
    private List<View> pagers = new ArrayList<View>();

    private RouteCommentAdapter commentAdapter;
    private List<RouteCommentDTO> commentList = new ArrayList<RouteCommentDTO>();

    private RouteManager routeManager;

    private RequestQueue queue;

    private RouteDTO route;

    private int commentCount = 0;

    private List<String> urls = new ArrayList<String>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);
        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (bar != null) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        this.routeManager = new RouteManager(this);
        this.queue = Volley.newRequestQueue(this);
        this.queue.start();

        final Intent intent = getIntent();
        this.route = (RouteDTO) intent.getSerializableExtra(EXTRA_ROUTE);

        String routeId = intent.getStringExtra(EXTRA_ROUTE_ID);
        this.mapLoading.setVisibility(View.VISIBLE);
        if (null != this.route) {
            this.setTitle(this.route.getName());
            this.getRoutePhotosByRouteId(route.getId());
            this.fetchRouteDetaiById(route.getId());

            if (!TextUtils.isEmpty(route.getMapURL())) {
                Picasso.with(this).load(route.getMapURL()).fit().centerCrop().error(R.drawable.transparent)
                        .placeholder(R.drawable.transparent).into(this.mapView, new Callback() {
                    @Override
                    public void onSuccess() {
                        mapLoading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        loading.setVisibility(View.GONE);
                    }
                });
            } else {
                loading.setVisibility(View.GONE);
            }
            this.difficulty.setRating((float) route.getDifficultyCoefficient());
//            this.viewTv.setText(String.valueOf(route.getViewCoefficient()));
            this.score.setRating((float) route.getViewCoefficient());
            this.traffic.setRating((float) route.getTrafficCoefficient());
            if (LocaleManager.isDisplayKM(this)) {
                this.distance.setText(String.format("%.0f",
                        route.getTotalDistance() / 1000));
            } else {
                this.distance.setText(String.format("%.0f",
                        LocaleManager.kilometreToMile(route.getTotalDistance()) / 1000));
                distanceUnit.setText(getResources().getString(R.string.mi));
            }
//            changeView();
            this.countTv.setText("(" + route.getNumberOfFollowers() + ")");
            this.viewDesc.setText("      "
                    + Html.fromHtml(route.getDescription()));
        }

        if (!TextUtils.isEmpty(routeId)) {
            this.getRoutePhotosByRouteId(routeId);
            this.fetchRouteDetaiById(routeId);
            this.fetchRouteComment(routeId);
        }

        this.pager.removeAllViews();
        this.indicator.removeAllViews();

        // 点击事件注册
        this.mapView.setOnClickListener(this);
        this.want.setOnClickListener(this);
        this.commentMore.setOnClickListener(this);
        this.sendComment.setOnClickListener(this);

        this.commentAdapter = new RouteCommentAdapter(commentList);
        this.listView.setAdapter(commentAdapter);
        this.listView.setOnItemClickListener(this);

        this.loading.setVisibility(View.VISIBLE);

        DisplayMetrics dm = new DisplayMetrics();
        WindowManager vm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        vm.getDefaultDisplay().getMetrics(dm);

        android.view.ViewGroup.LayoutParams params = view.getLayoutParams();
        params.width = dm.widthPixels;
        params.height = (int) (dm.widthPixels / RESOLUTION) + 1;
        view.setPadding(0, 0, 0, 0);
        view.setLayoutParams(params);

    }

    @Override
    protected void onResume() {
        super.onResume();
        if (null != this.route) {
            this.fetchRouteComment(this.route.getId());
        }
    }

    private void changeView() {
        if (route.isFollowed()) {
            this.want.setBackgroundResource(R.drawable.route_want_bg);
            this.wantTv.setTextColor(getResources().getColor(
                    R.color.route_activity_want_go));
            this.wantTv.setText(R.string.routes_activity_want_go);
            this.wantTv.setCompoundDrawablesWithIntrinsicBounds(getResources()
                            .getDrawable(R.drawable.ic_route_activity_want), null,
                    null, null);
            this.countTv.setTextColor(getResources().getColor(
                    R.color.route_activity_want_go));
        } else {
            this.want.setBackgroundResource(R.drawable.route_wanted_bg);
            this.wantTv.setText(R.string.routes_activity_wanted_go);
            this.wantTv.setTextColor(getResources().getColor(
                    R.color.route_activity_wanted_go));
            this.wantTv.setCompoundDrawablesWithIntrinsicBounds(getResources()
                            .getDrawable(R.drawable.ic_route_activity_wanted), null,
                    null, null);
            this.countTv.setTextColor(getResources().getColor(
                    R.color.route_activity_wanted_go));
        }

        mLinearWant.setVisibility(View.VISIBLE);
    }

    @Override
    protected void onDestroy() {
        this.queue.stop();
        super.onDestroy();
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public void onPageScrollStateChanged(int state) {

    }

    @Override
    public void onPageScrolled(int position, float positionOffset,
                               int positionOffsetPixels) {

    }

    @Override
    public void onPageSelected(int index) {
        for (int i = 0; i < indicators.length; i++) {
            indicators[index]
                    .setBackgroundResource(R.drawable.route_activity_indicator_1);
            if (index != i) {
                indicators[i]
                        .setBackgroundResource(R.drawable.route_activity_indicator_0);
            }
        }
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.route_activity_want:
                if (route.isFollowed()) {
                    Toasts.show(this, R.string.route_activity_comment_followed_msg);
                    break;
                }

//                want.setBackgroundResource(R.drawable.route_want_bg);
//                wantTv.setText(R.string.routes_activity_want_go);
//                wantTv.setTextColor(getResources().getColor(
//                        R.color.route_activity_want_go));
//                wantTv.setCompoundDrawablesWithIntrinsicBounds(getResources()
//                                .getDrawable(R.drawable.ic_route_activity_want), null,
//                        null, null);
//                countTv.setTextColor(getResources().getColor(
//                        R.color.route_activity_want_go));
//                countTv.setText("(" + route.getNumberOfFollowers() + ")");

                this.fetchPostFollower(this.route.getId());
                SpeedxAnalytics.onEvent(this, "路线想去总次数",null);
                break;

            case R.id.route_activity_comment_all:
                Intent intent = new Intent(this, RouteCommentActivity.class);
                intent.putExtra(RouteCommentActivity.EXTRA_ROUTE_ID,
                        this.route.getId());
                intent.putExtra(RouteCommentActivity.EXTRA_ROUTE_COMMENT_COUNT,
                        commentCount);
                startActivity(intent);
                break;

            case R.id.route_activity_send_comment:
                String content = this.editText.getText().toString().trim();
                if (TextUtils.isEmpty(content)) {
                    Toasts.show(this, R.string.route_activity_comment_empty_msg);
                    break;
                }

                ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE))
                        .hideSoftInputFromWindow(editText.getWindowToken(),
                                InputMethodManager.HIDE_NOT_ALWAYS);
                this.sendComment.setClickable(false);

                this.postRouteComment(content);
                SpeedxAnalytics.onEvent(this, "路线评论总次数",null);
                break;
            case R.id.route_activity_line_map:
                Intent mapIntent = new Intent(this, RouteMapActivity.class);
                mapIntent.putExtra(RouteMapActivity.EXTRA_ROUTE_ID,
                        this.route.getId());
                mapIntent.putExtra(RouteMapActivity.EXTRA_ROUTE_DISTANCE,
                        this.route.getTotalDistance());
                startActivity(mapIntent);
                SpeedxAnalytics.onEvent(this, "查看精品路线地图详情",null);
                break;
            default:
                break;
        }
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position,
                            long id) {
        RouteCommentDTO rct = (RouteCommentDTO) parent.getAdapter().getItem(position);
        if (null == rct)
            return;

        Intent intent = new Intent(this, ProfileActivity.class);
        intent.putExtra(ProfileActivity.EXTRA_USER_ID, rct.getUserId());
        intent.putExtra(ProfileActivity.EXTRA_AVATAR, rct.getAvatarUrl());
        intent.putExtra(ProfileActivity.EXTRA_NICK_NAME, rct.getNickName());
        intent.putExtra(ProfileActivity.EXTRA_REMARKS, rct.getRemarks());
        startActivity(intent);
    }

    private void initViewPager() {
        pagers = new ArrayList<View>();

        for (int i = 0; i < urls.size(); i++) {
            image = new ImageView(this);
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
            image.setLayoutParams(layoutParams);
            image.setScaleType(ScaleType.CENTER_CROP);

            pagers.add(image);
        }

        adapter = new TutorialPageAdapter(pagers);
        pager.setAdapter(adapter);
        pager.setOnPageChangeListener(this);
    }

    private void initDot() {
        indicators = new ImageView[urls.size()];

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        layoutParams.setMargins(4, 4, 4, 4);

        for (int i = 0; i < pagers.size(); i++) {
            indicImg = new ImageView(this);

            indicImg.setLayoutParams(layoutParams);
            indicators[i] = indicImg;
            indicators[i].setTag(i);
            indicators[i].setOnClickListener(this);

            if (i == 0) {
                indicators[i]
                        .setBackgroundResource(R.drawable.route_activity_indicator_1);
            } else {
                indicators[i]
                        .setBackgroundResource(R.drawable.route_activity_indicator_0);
            }

            indicator.addView(indicators[i]);
        }
    }

    /**
     * 获取路线的详细信息
     *
     * @param routeId
     */
    private void fetchRouteDetaiById(String routeId) {
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, RouteDTO>() {

            @Override
            protected RouteDTO doInBackground(String... params) {
                try {
                    return routeManager.getRouteInfoByRouteId(params[0]);
                } catch (BusinessException e) {
                    return null;
                }
            }

            @Override
            protected void onPostExecute(RouteDTO rd) {
                if (null == rd)
                    return;

                route = rd;
                difficulty.setRating((float) rd.getDifficultyCoefficient());

                score.setRating((float) rd.getViewCoefficient());
                traffic.setRating((float) rd.getTrafficCoefficient());
                if (LocaleManager.isDisplayKM(RouteActivity.this)) {
                    distance.setText(String.format("%.0f", rd.getTotalDistance() / 1000));
                } else {
                    distance.setText(String.format("%.0f", LocaleManager.kilometreToMile(rd.getTotalDistance()) / 1000));
                }
                changeView();
                countTv.setText("(" + rd.getNumberOfFollowers() + ")");
                viewDesc.setText("      " + Html.fromHtml(rd.getDescription()));
            }

        }, routeId);
    }

    /**
     * 添加关注
     *
     * @param routeId
     */
    private void fetchPostFollower(String routeId) {
        this.getAsyncTaskQueue().add(new AsyncTask<String, Void, Integer>() {

            @Override
            protected Integer doInBackground(String... params) {
                try {
                    return routeManager.postRouteFollowerById(params[0]);
                } catch (BusinessException e) {
                    return -1;
                }
            }

            @Override
            protected void onPostExecute(Integer result) {
                wantTv.setClickable(true);
                if (result == -1) {
                    want.setBackgroundResource(R.drawable.route_wanted_bg);
                    wantTv.setText(R.string.routes_activity_wanted_go);
                    wantTv.setTextColor(getResources().getColor(
                            R.color.route_activity_wanted_go));
                    wantTv.setCompoundDrawablesWithIntrinsicBounds(
                            getResources().getDrawable(
                                    R.drawable.ic_route_activity_wanted), null,
                            null, null);
                    countTv.setTextColor(getResources().getColor(
                            R.color.route_activity_wanted_go));
                    countTv.setText("(" + route.getNumberOfFollowers() + ")");
                    Toasts.show(RouteActivity.this,
                            R.string.route_activity_comment_followed_err);
                    return;
                }

                want.setBackgroundResource(R.drawable.route_want_bg);
                wantTv.setText(R.string.routes_activity_want_go);
                wantTv.setTextColor(getResources().getColor(
                        R.color.route_activity_want_go));
                wantTv.setCompoundDrawablesWithIntrinsicBounds(getResources()
                                .getDrawable(R.drawable.ic_route_activity_want), null,
                        null, null);
                countTv.setTextColor(getResources().getColor(
                        R.color.route_activity_want_go));
                route.setFollowed(true);
                if (result > 0) {
                    countTv.setText("(" + result + ")");
                }
            }

        }, routeId);
    }

    /**
     * 获取评论列表(默认显示三条)
     *
     * @param routeId
     */
    private void fetchRouteComment(String routeId) {
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<RouteCommentDTO>>() {

                    @Override
                    protected List<RouteCommentDTO> doInBackground(
                            String... params) {
                        try {
                            return routeManager.getRouteCommentByRouteId(
                                    params[0], 3, 1);
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<RouteCommentDTO> rcdList) {
                        if (null == rcdList || rcdList.isEmpty())
                            return;

                        commentList.clear();
                        commentList.addAll(rcdList);
                        commentAdapter.notifyDataSetChanged();

                        commentCount = rcdList.get(0).getCommentCount();
                        commentCountTv
                                .setText(String
                                        .format(getString(R.string.route_activity_comment_title),
                                                commentCount));
                    }

                }, routeId);
    }

    /**
     * 获取路线风景图
     *
     * @param routeId
     */
    private void getRoutePhotosByRouteId(String routeId) {
        this.getAsyncTaskQueue().add(
                new AsyncTask<String, Void, List<String>>() {

                    @Override
                    protected List<String> doInBackground(String... params) {
                        try {
                            return routeManager.getRoutePhotosByRouteId(params[0]);
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<String> rcdList) {
                        if (null == rcdList || rcdList.isEmpty())
                            return;

                        urls.clear();
                        urls.addAll(rcdList);
                        logger.trace("Route cover " + urls.toString());
                        initViewPager();
                        initDot();
                    }
                }, routeId);
    }

    /**
     * 提交评论
     *
     * @param content
     */
    private void postRouteComment(String content) {
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
                        sendComment.setClickable(true);
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
                        fetchRouteComment(route.getId());
                    }

                }, route.getId(), content);
    }

    private final class TutorialPageAdapter extends PagerAdapter {

        private List<View> pages = new ArrayList<View>();

        public TutorialPageAdapter(List<View> pages) {
            this.pages = pages;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            if (position >= pages.size()) {
                return null;
            }
            ImageView iamgeView = (ImageView) pages.get(position);

            if (!TextUtils.isEmpty(urls.get(position))) {
                Picasso.with(RouteActivity.this).load(urls.get(position)).fit().centerCrop().error(R.drawable.transparent)
                        .placeholder(R.drawable.transparent).into(image, new Callback() {
                    @Override
                    public void onSuccess() {
                        loading.setVisibility(View.GONE);
                    }

                    @Override
                    public void onError() {
                        loading.setVisibility(View.GONE);
                    }
                });
            } else {
                image.setImageResource(R.drawable.transparent);
            }

            ((ViewPager) container).addView(pages.get(position));
            return iamgeView;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView(pages.get(position));
        }

        @Override
        public int getCount() {
            return this.pages.size();
        }

        @Override
        public boolean isViewFromObject(View view, Object item) {
            return view == item;
        }

    }

}
