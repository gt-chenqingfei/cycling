package com.beastbikes.android.modules.cycling.task.ui;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.util.AttributeSet;
import android.view.View;
import android.view.WindowManager.LayoutParams;
import android.widget.ImageView;
import android.widget.PopupWindow;

import com.beastbikes.android.Constants;
import com.beastbikes.android.R;
import com.beastbikes.android.authentication.AVUser;
import com.beastbikes.framework.ui.android.WebActivity;
import com.beastbikes.android.embapi.BrowserActivity;
import com.beastbikes.android.modules.cycling.task.dto.Bazinga;
import com.squareup.picasso.Picasso;

public class BarzingaPopupWindow extends PopupWindow implements View.OnClickListener {

    private Bazinga bazinga;

    public BarzingaPopupWindow(Context context, AttributeSet attribute) {
        super(context, attribute);
    }

    private Activity context;

    public BarzingaPopupWindow(Activity context, Bazinga bazinga) {

        this.context = context;
        this.bazinga = bazinga;
        initProperty();
        View view = context.getLayoutInflater().inflate(R.layout.barzinga_popup_window, null);
        this.setContentView(view);

        ImageView ivContent = (ImageView) view.findViewById(R.id.iv_barzinga);
        view.findViewById(R.id.iv_close).setOnClickListener(this);


        if (bazinga != null) {
            Picasso.with(context).load(bazinga.getImageUrl()).into(ivContent);
            ivContent.setOnClickListener(this);
        }

        SharedPreferences defaultSp = context.getSharedPreferences(Constants.PREF_BAZINGA, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = defaultSp.edit();
        editor.putInt(Constants.PREF_BAZINGA_COUNTER, bazinga.getCounter() + 1);
        editor.commit();
    }

    private void initProperty() {

        this.setWidth(LayoutParams.FILL_PARENT);
        this.setHeight(LayoutParams.FILL_PARENT);

        this.setFocusable(true);
        this.setAnimationStyle(R.style.WindowAnim);
        this.setOutsideTouchable(false);

    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    private void setBackgroundAlpha(float alpha) {
        LayoutParams lp = this.context.getWindow().getAttributes();
        lp.alpha = alpha; //0.0-1.0
        this.context.getWindow().setAttributes(lp);
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.iv_barzinga) {
            final Uri uri = Uri.parse(bazinga.getLinkTo());
            final Intent browserIntent = new Intent(context, BrowserActivity.class);
            browserIntent.setData(uri);
            browserIntent.addCategory(Intent.CATEGORY_DEFAULT);
            browserIntent.addCategory(Intent.CATEGORY_BROWSABLE);
            browserIntent.setPackage(context.getPackageName());
            browserIntent.putExtra(WebActivity.EXTRA_ENTER_ANIMATION,
                    R.anim.activity_in_from_right);
            browserIntent.putExtra(WebActivity.EXTRA_EXIT_ANIMATION,
                    R.anim.activity_out_to_right);
            browserIntent.putExtra(WebActivity.EXTRA_NONE_ANIMATION,
                    R.anim.activity_none);
            Bundle bundle = new Bundle();
            if (AVUser.getCurrentUser() != null) {
                bundle.putString("X-User-Id", AVUser.getCurrentUser().getObjectId());
            }
            browserIntent.putExtra(WebActivity.EXTRA_HTTP_HEADERS, bundle);

            context.startActivity(browserIntent);
        }

        this.dismiss();
    }
}
