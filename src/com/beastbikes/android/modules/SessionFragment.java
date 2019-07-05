package com.beastbikes.android.modules;

import android.app.Activity;
import android.content.Intent;
import android.text.TextUtils;

import com.avos.avoscloud.AVAnalytics;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.ui.android.BaseFragment;

public class SessionFragment extends BaseFragment {

    protected String getUserId() {
        final Activity ctx = getActivity();
        if (null != ctx) {

            final Intent intent = ctx.getIntent();
            if (null != intent) {

                final String userId = intent
                        .getStringExtra(SessionFragmentActivity.EXTRA_USER_ID);
                if (!TextUtils.isEmpty(userId)) {
                    return userId;
                }
            }
        }
        final AVUser current = AVUser.getCurrentUser();
        if (null == current)
            return null;

        return current.getObjectId();
    }

    @Override
    public void onPause() {
        super.onPause();
        final Class<?> clazz = getClass();
        final Alias alias = clazz.getAnnotation(Alias.class);
        if (null != alias) {
            AVAnalytics.onFragmentEnd(alias.value());
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        final Class<?> clazz = getClass();
        final Alias alias = clazz.getAnnotation(Alias.class);
        if (null != alias) {
            AVAnalytics.onFragmentStart(alias.value());
        }
    }

}
