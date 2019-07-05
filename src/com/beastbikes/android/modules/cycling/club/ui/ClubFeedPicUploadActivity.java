package com.beastbikes.android.modules.cycling.club.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.cycling.club.dto.ClubFeedPost;
import com.beastbikes.android.modules.cycling.club.ui.widget.EditPhotoView;
import com.beastbikes.framework.android.analytics.annotation.Alias;
import com.beastbikes.framework.android.res.annotation.LayoutResource;

import java.util.ArrayList;

import com.beastbikes.android.widget.multiimageselector.MultiImageSelectorActivity;

@Alias("俱乐部相册上传")
@LayoutResource(R.layout.activity_clubfeed_post)
public class ClubFeedPicUploadActivity extends ClubFeedPostActivity implements
        View.OnClickListener {

    public static final String EXTRA_DIFF_COUNT = "count";

    private ArrayList<String> datas;
    private int count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        super.overridePendingTransition(R.anim.activity_in_from_right, R.anim.activity_none);
        addCyclingRecord.setVisibility(View.GONE);
        cbSyncAblum.setVisibility(View.GONE);

        this.count = getIntent().getIntExtra(EXTRA_DIFF_COUNT, 0);
        maxSelected = count;
        editText.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before,
                                      int count) {
                if (menuItemPost == null)
                    return;
                if (null != datas && !datas.isEmpty()) {
                    menuItemPost.setEnabled(true);
                } else {
                    menuItemPost.setEnabled(false);
                }
            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count,
                                          int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }

    @Override
    protected int getClubPostType() {
        return ClubFeedPost.TYPE_ABLUM;
    }

    @Override
    public void finish() {
        super.finish();
        super.overridePendingTransition(R.anim.activity_none, R.anim.activity_out_to_right);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (resultCode) {
            case RESULT_OK:
                switch (requestCode) {
                    case EditPhotoView.REQ_SELECT_IMAGE:
                        if (editPhoto != null) {
                            datas = data.getStringArrayListExtra(
                                    MultiImageSelectorActivity.EXTRA_RESULT);
                            if (null != datas && !datas.isEmpty()) {
                                menuItemPost.setEnabled(true);
                            } else {
                                menuItemPost.setEnabled(false);
                            }
                            editPhoto.onActivityResult(requestCode, resultCode, data);
                        }
                        break;
                }
                break;
        }
        super.onActivityResult(requestCode, resultCode, data);
    }
}
