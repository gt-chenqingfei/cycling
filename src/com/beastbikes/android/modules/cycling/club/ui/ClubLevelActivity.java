package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.framework.ui.android.WebActivity;
import com.beastbikes.android.embapi.BrowserActivity;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.modules.cycling.club.biz.ClubManager;
import com.beastbikes.android.modules.cycling.club.dto.ClubInfoCompact;
import com.beastbikes.android.modules.cycling.club.dto.ClubLevel;
import com.beastbikes.android.modules.cycling.club.dto.Privilege;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;
import com.beastbikes.framework.ui.android.lib.list.BaseListAdapter;
import com.beastbikes.framework.ui.android.utils.ViewHolder;
import com.beastbikes.framework.ui.android.widget.CircleImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;


@LayoutResource(R.layout.clubfeed_level)
public class ClubLevelActivity extends SessionFragmentActivity implements
        OnClickListener {
    public static final String EXTRA_CLUBINFO = "club_info";
    private ClubInfoCompact clubInfoCompact;
    @IdResource(R.id.lv_level)
    private ListView lvLevle;

    @IdResource(R.id.club_info_logo)
    CircleImageView clubLogo;

    @IdResource(R.id.tv_current_level)
    private TextView currentLevel;

    @IdResource(R.id.tv_next_level)
    private TextView nextLevel;

    @IdResource(R.id.tv_understanding)
    private TextView understanding;

    @IdResource(R.id.privileges_container)
    private LinearLayout privilegesContainer;

    private List<ClubLevel> levelList = new ArrayList<>();
    ClubFeedLevelAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        final android.support.v7.app.ActionBar bar = getSupportActionBar();
        if (null != bar) {
            bar.setDisplayHomeAsUpEnabled(true);
        }

        final Intent intent = getIntent();
        if (intent == null)
            return;
        Object obj = intent.getSerializableExtra(EXTRA_CLUBINFO);
        if (obj != null) {
            clubInfoCompact = (ClubInfoCompact) obj;
        }
        adapter = new ClubFeedLevelAdapter(null, lvLevle, levelList);
        lvLevle.setAdapter(adapter);

        fetchClubLevels();
        fetchPrivileges();
        refreshView();
    }


    @Override
    protected void onResume() {
        super.onResume();

    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    public void onClick(View v) {
        if (v == understanding) {
            final StringBuilder sb = new StringBuilder(Constants.UrlConfig.DEV_SPEEDX_HOST)
                    .append("/app/club/privilege.html");
            final Uri browsweUri = Uri.parse(sb.toString());
            final Intent browserIntent = new Intent(this,
                    BrowserActivity.class);
            browserIntent.setData(browsweUri);
            browserIntent.addCategory(Intent.CATEGORY_DEFAULT);
            browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
            browserIntent.setPackage(getPackageName());
            browserIntent.putExtra(WebActivity.EXTRA_TITLE,
                    getString(R.string.clubfeed_level_understand));
            browserIntent.putExtra(WebActivity.EXTRA_ENTER_ANIMATION,
                    R.anim.activity_in_from_right);
            browserIntent.putExtra(WebActivity.EXTRA_EXIT_ANIMATION,
                    R.anim.activity_out_to_right);
            browserIntent.putExtra(WebActivity.EXTRA_NONE_ANIMATION,
                    R.anim.activity_none);
            this.startActivity(browserIntent);
        }
    }

    private void refreshView() {
        understanding.setOnClickListener(this);
        if (clubInfoCompact != null) {
            if (!TextUtils.isEmpty(clubInfoCompact.getLogo())) {
                Picasso.with(this).load(clubInfoCompact.getLogo()).fit().centerCrop().error(R.drawable.ic_avatar_club)
                        .placeholder(R.drawable.ic_avatar_club).into(clubLogo);
            } else {
                this.clubLogo.setImageResource(R.drawable.ic_avatar_club);
            }
        }
    }

    private void refreshPrivileges(Privilege v) {
        if (null == v.getPrivileges()) return;
        privilegesContainer.removeAllViews();
        for (int i = 0; i < v.getPrivileges().size(); i++) {
            TextView tv = (TextView) LayoutInflater.from(this).inflate(R.layout.club_privileges_item, null);
            tv.setText(v.getPrivileges().get(i));
            privilegesContainer.addView(tv);
        }
        this.currentLevel.setText(" LV." + v.getCurLevel());
        int next = v.getCurLevel() + 1;
        this.nextLevel.setText(" LV." + next);
    }

    private void fetchClubLevels() {
        this.getAsyncTaskQueue().add(
                new AsyncTask<Void, Void, List<ClubLevel>>() {

                    @Override
                    protected List<ClubLevel> doInBackground(Void... params) {

                        try {
                            return new ClubManager(getApplicationContext()).getClubLevelInfo();
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(List<ClubLevel> result) {
                        if (null != result) {
                            adapter.add(result);
                        }
                    }

                });
    }

    private void fetchPrivileges() {
        this.getAsyncTaskQueue().add(
                new AsyncTask<Void, Void, Privilege>() {

                    @Override
                    protected Privilege doInBackground(Void... params) {

                        try {


                            return new ClubManager(getApplicationContext()).getClubPrivilegInfo();
                        } catch (BusinessException e) {
                            return null;
                        }
                    }

                    @Override
                    protected void onPostExecute(Privilege result) {
                        if (null != result) {
                            refreshPrivileges(result);
                        }
                    }
                });
    }

    class ClubFeedLevelAdapter extends BaseListAdapter<ClubLevel> {

        public ClubFeedLevelAdapter(Handler handler, AbsListView listView, final List<ClubLevel> data) {
            super(handler, listView, data);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {

            final LevelViewHolder vh;

            if (convertView == null) {
                convertView = LayoutInflater.from(parent.getContext()).inflate(
                        R.layout.clubfeed_level_item, null);
                vh = new LevelViewHolder(convertView);
            } else {
                vh = (LevelViewHolder) convertView.getTag();
            }
            ClubLevel clubLevel = getItem(position);
            if (clubLevel != null) {
                vh.bind(clubLevel);
            }
            return convertView;
        }

        @Override
        protected void recycleView(View view) {

        }

        class LevelViewHolder extends ViewHolder<ClubLevel> {
            TextView tvParam1;
            TextView tvParam2;
            ProgressBar progressBar;

            /**
             * Create an instance with the specified view
             *
             * @param v The root view to hold
             */
            protected LevelViewHolder(View v) {
                super(v);
                tvParam1 = (TextView) v.findViewById(R.id.tv_param1);
                tvParam2 = (TextView) v.findViewById(R.id.tv_param2);
                progressBar = (ProgressBar) v.findViewById(R.id.pb_level);
            }

            public void bind(ClubLevel o) {
                tvParam1.setText(o.getParam1());
                tvParam2.setText(o.getProgress() + "/" + o.getProgressMax());
                progressBar.setMax(o.getProgressMax());
                progressBar.setProgress(o.getProgress());
            }
        }
    }

}
