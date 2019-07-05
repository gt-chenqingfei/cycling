package com.beastbikes.android.modules.user.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.ViewPager.LayoutParams;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.ImageView.ScaleType;
import android.widget.LinearLayout;

import com.beastbikes.android.R;
import com.beastbikes.android.dialog.LoadingDialog;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;
import com.beastbikes.framework.ui.android.BaseActivity;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

@Alias("查看头像大图")
@LayoutResource(R.layout.avatar_viewer)
public class AvatarViewer extends BaseActivity {

    public static final String EXTRA_USER_ID = "user_id";
    public static final String EXTRA_USER_AVATAR_URL = "user_avatar_url";

    @IdResource(R.id.profile_fragment_avatar_viewer)
    private ImageView avatarViewer;

    private float x1 = 0;
    private float x2 = 0;
    private float y1 = 0;
    private float y2 = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = getIntent();
        if (null == intent)
            return;


        DisplayMetrics dm = new DisplayMetrics();
        WindowManager vm = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        vm.getDefaultDisplay().getMetrics(dm);
        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, dm.widthPixels);
        avatarViewer.setLayoutParams(layoutParams);
        avatarViewer.setScaleType(ScaleType.CENTER_CROP);

        String avatarUrl = intent.getStringExtra(EXTRA_USER_AVATAR_URL);
        if (TextUtils.isEmpty(avatarUrl)) {
            avatarViewer.setImageResource(R.drawable.ic_avatar);
            return;
        }

        StringBuilder sb = new StringBuilder(avatarUrl);
        sb.append("?imageView/2/w/").append(dm.widthPixels).append("/h/");
        sb.append(dm.widthPixels).append("/q/100/format/png");
        final LoadingDialog dialog = new LoadingDialog(AvatarViewer.this, getString(R.string.avatar_image_loading), true);
        dialog.show();
        if (!TextUtils.isEmpty(sb.toString())) {
            Picasso.with(this).load(sb.toString()).fit().centerCrop().error(R.drawable.ic_avatar)
                    .placeholder(R.drawable.ic_avatar).into(avatarViewer, new Callback() {
                @Override
                public void onSuccess() {
                    if (null != dialog && null != AvatarViewer.this && !AvatarViewer.this.isFinishing()) {
                        dialog.dismiss();
                    }
                }

                @Override
                public void onError() {
                    if (null != dialog && null != AvatarViewer.this && !AvatarViewer.this.isFinishing()) {
                        dialog.dismiss();
                    }
                }
            });
        } else {
            this.avatarViewer.setImageResource(R.drawable.ic_avatar);
            if (null != dialog && null != AvatarViewer.this && !AvatarViewer.this.isFinishing()) {
                dialog.dismiss();
            }
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {

        if (event.getAction() == MotionEvent.ACTION_DOWN) {
            x1 = event.getX();
            y1 = event.getY();
            return true;
        }
        if (event.getAction() == MotionEvent.ACTION_UP) {
            x2 = event.getX();
            y2 = event.getY();
            double x = Math.pow(x1 - x2, 2);
            double y = Math.pow(y1 - y2, 2);
            if (Math.sqrt(x + y) < 20) {
                finish();
            }
        }
        return true;
    }

    
}
