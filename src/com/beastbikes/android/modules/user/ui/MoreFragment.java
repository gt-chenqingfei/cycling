package com.beastbikes.android.modules.user.ui;

import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.avos.avoscloud.AVAnalytics;
import com.beastbikes.android.BeastBikes;
import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragment;
import com.beastbikes.android.modules.cycling.activity.ui.CyclingTargetSettingActivity;
import com.beastbikes.android.modules.preferences.ui.SettingActivity;
import com.beastbikes.android.modules.user.biz.UserManager;
import com.beastbikes.android.modules.user.dto.ProfileDTO;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.business.BusinessException;

/**
 * Created by caoxiao on 16/1/11.
 */
@LayoutResource(R.layout.fragment_more)
public class MoreFragment extends SessionFragment implements View.OnClickListener {

    @IdResource(R.id.goal_settings)
    private ViewGroup goalSettingsVG;
    private ImageView goalIcon;
    private TextView goalTitle;

    @IdResource(R.id.profile_fragment_detail_item_grid)
    private ViewGroup gridVG;
    private ImageView gridIcon;
    private TextView gridTitle;

    @IdResource(R.id.settings)
    private ViewGroup settingsVG;
    private ImageView settingsIcon;
    private TextView settingsTitle;

    private ProfileDTO profile;
    private SharedPreferences userSp;

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        this.userSp = getActivity().getSharedPreferences(getUserId(), 0);
        getActivity().setTitle(R.string.club_discover_group_more);
        goalIcon = (ImageView) goalSettingsVG.findViewById(R.id.layout_morefragment_detail_icon);
        goalTitle = (TextView) goalSettingsVG.findViewById(R.id.layout_morefragment_detail_title);

        gridIcon = (ImageView) gridVG.findViewById(R.id.layout_morefragment_detail_icon);
        gridTitle = (TextView) gridVG.findViewById(R.id.layout_morefragment_detail_title);

        settingsIcon = (ImageView) settingsVG.findViewById(R.id.layout_morefragment_detail_icon);
        settingsTitle = (TextView) settingsVG.findViewById(R.id.layout_morefragment_detail_title);

        goalIcon.setImageResource(R.drawable.ic_goalsettings);
        gridIcon.setImageResource(R.drawable.ic_grid);
        settingsIcon.setImageResource(R.drawable.ic_settings);

        goalTitle.setText(R.string.label_goal_settings);
        gridTitle.setText(R.string.grid_explore_label);
        settingsTitle.setText(R.string.club_info_setting_menu_tips);

        gridVG.setOnClickListener(this);
        settingsVG.setOnClickListener(this);
        this.goalSettingsVG.setOnClickListener(this);

        this.fetchUserProfile();
    }

    @Override
    public void onHiddenChanged(boolean hidden) {
        super.onHiddenChanged(hidden);
        if (!hidden) {
            getActivity().setTitle(R.string.club_discover_group_more);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        BeastBikes beast = (BeastBikes) BeastBikes.getInstance();
        if (beast.isMapStyleEnabled()) {
            this.gridVG.setVisibility(View.VISIBLE);
        } else {
            this.gridVG.setVisibility(View.GONE);
        }
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.profile_fragment_detail_item_grid:
                Intent intent = new Intent(this.getActivity(), GridExploreActivity.class);
                intent.putExtra(GridExploreActivity.EXTRA_PROFILE, profile);
                startActivity(intent);
                break;
            case R.id.settings:
                startActivity(new Intent(this.getActivity(), SettingActivity.class));
                break;
            case R.id.goal_settings:
                startActivity(new Intent(getActivity(), CyclingTargetSettingActivity.class));
                break;
        }
    }

    private void fetchUserProfile() {
        getAsyncTaskQueue().add(new AsyncTask<Void, Void, Void>() {

            @Override
            protected Void doInBackground(Void... voids) {
                try {
                    profile = new UserManager(MoreFragment.this.getActivity()).getProfileByUserId(MoreFragment.this.getUserId());
                } catch (BusinessException e) {
                    e.printStackTrace();
                }
                return null;
            }
        });

    }
}
