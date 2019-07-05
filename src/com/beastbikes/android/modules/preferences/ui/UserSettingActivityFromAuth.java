package com.beastbikes.android.modules.preferences.ui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.KeyEvent;
import android.widget.ImageView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.widget.blureffect.BlurUtil;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

@Alias("个人设置页")
@LayoutResource(R.layout.user_setting_activity_from_auth)
public class UserSettingActivityFromAuth extends SessionFragmentActivity {

    @IdResource(R.id.user_setting_activity_from_auth_blur_image)
    ImageView blurImage;

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        Fragment fragment = Fragment.instantiate(this, UserSettingFragment.class.getName());
        getSupportFragmentManager().beginTransaction().
                add(R.id.user_setting_activity_fragment_container, fragment).
                commitAllowingStateLoss();

        BlurUtil.blurOn(this, blurImage, R.drawable.authentication_bg);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        BlurUtil.blurBitmapFree(blurImage);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return true;
    }

}
