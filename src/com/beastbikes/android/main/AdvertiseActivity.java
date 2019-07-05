package com.beastbikes.android.main;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.embapi.BrowserActivity;
import com.beastbikes.framework.ui.android.WebActivity;
import com.beastbikes.android.modules.SessionFragmentActivity;
import com.beastbikes.android.utils.BitmapLoadManager;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.IdResource;
import com.beastbikes.framework.android.res.annotation.LayoutResource;


@Alias("广告页")
@LayoutResource(R.layout.activity_advertise)
public class AdvertiseActivity extends SessionFragmentActivity {
    @IdResource(R.id.activity_advertise_iv)
    private ImageView iv;

    @IdResource(R.id.activity_advertise_viewGroup)
    private LinearLayout linearLayout;

    @IdResource(R.id.activity_advertise_timecount)
    private TextView time_count;

    private TimeCount timeCount;

    private String url = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right,
                R.anim.activity_none);

        url = getIntent().getStringExtra(AdviertiseManager.PREF_AD_URL);
        String imgurl = getIntent().getStringExtra(AdviertiseManager.PREF_AD_IMGURL);
        final Bitmap bitmap = BitmapLoadManager.getBitmapbyFile(imgurl, this);
        if (bitmap == null) {
            finish();
            return;
        }

        iv.setImageBitmap(bitmap);
        timeCount = new TimeCount(4000, 1000);
        timeCount.start();
        iv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openWeb();
                iv.setImageBitmap(null);
                iv = null;
                if (bitmap != null && !bitmap.isRecycled()) {
                    bitmap.recycle();
                }
                finish();
            }
        });

        linearLayout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_BACK:
                return true;
        }

        return super.onKeyUp(keyCode, event);
    }

    class TimeCount extends CountDownTimer {
        public TimeCount(long millisInFuture, long countDownInterval) {
            super(millisInFuture, countDownInterval);//参数依次为总时长,和计时的时间间隔
        }

        @Override
        public void onFinish() {//计时完毕时触发
            finish();
        }

        @Override
        public void onTick(long millisUntilFinished) {//计时过程显示
            time_count.setText(millisUntilFinished / 1000 + "s");
        }
    }

    @Override
    public void finish() {
        super.finish();
        if (timeCount != null) {
            timeCount.cancel();
        }
        super.overridePendingTransition(0, R.anim.activity_out_to_right);
    }

    private void openWeb() {

        if (!TextUtils.isEmpty(url)) {

            Uri uri = Uri.parse(url);
            Intent intent = new Intent(this,
                    BrowserActivity.class);
            intent.setData(uri);
            intent.addCategory(Intent.CATEGORY_DEFAULT);
            intent.addCategory(Intent.CATEGORY_BROWSABLE);
            intent.setPackage(getPackageName());
            intent.putExtra(WebActivity.EXTRA_ENTER_ANIMATION,
                    R.anim.activity_in_from_right);
            intent.putExtra(WebActivity.EXTRA_EXIT_ANIMATION,
                    R.anim.activity_out_to_right);
            intent.putExtra(WebActivity.EXTRA_NONE_ANIMATION,
                    R.anim.activity_none);
            startActivity(intent);
        }
    }
}
