package com.beastbikes.android.modules.cycling.club.ui.widget;


import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragment;
import com.beastbikes.android.modules.cycling.club.ui.ClubActivitiesListActivity;
import com.beastbikes.android.modules.cycling.club.ui.ClubMemberRankActivity;
import com.beastbikes.android.modules.cycling.club.ui.ClubActivityReleaseActivity;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

/**
 * Created by zhangyao on 2016/1/13.
 */
@LayoutResource(R.layout.fragment_club_activity_not_list)
public class ClubActivityOntListFragment extends SessionFragment {
    @IdResource(R.id.fragment_club_activity_not_list_commit)
    private TextView commit;

    @IdResource(R.id.fragment_club_activity_not_list_tv)
    private TextView textView;

    @IdResource(R.id.fragment_club_activity_not_iv)
    private ImageView iv;
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        final Bundle bundle=getArguments();
        final boolean isMyClub = bundle.getBoolean(ClubActivitiesListActivity.IS_MYCLUB);
        if (isMyClub) {
            iv.setVisibility(View.GONE);
            commit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                        final Intent intent = new Intent(getActivity(),
                                ClubActivityReleaseActivity.class);
                        intent.putExtra(ClubActivitiesListActivity.IS_MYCLUB,isMyClub);
                        intent.putExtra(ClubMemberRankActivity.EXTRA_CLUB_ID,
                                bundle.getString(ClubMemberRankActivity.EXTRA_CLUB_ID));
                        getActivity().startActivity(intent);
                }
            });
        }else {
            iv.setVisibility(View.VISIBLE);
            commit.setVisibility(View.GONE);
            textView.setText(getResources().getString(R.string.fragment_club_activity_not_ismyclub));
        }
    }

}
