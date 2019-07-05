package com.beastbikes.android.modules.user.ui.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewStub;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.beastbikes.android.R;
import com.beastbikes.android.modules.user.dto.MedalDTO;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * 勋章详情PagerAdapter
 * Created by secret on 16/9/29.
 */
public class MedalViewPagerAdapter extends AbstractPagerAdapter<MedalDTO> implements View.OnClickListener {

    private Context mContext;

    private LayoutInflater mLayoutInflater;

    private boolean isFromPush;

    private OnMedalButtonClickListener onMedalButtonClickListener;

    public MedalViewPagerAdapter(Context context, ArrayList<MedalDTO> medalDTOs, boolean isFromPush) {
        super(medalDTOs);
        this.mContext = context;
        this.isFromPush = isFromPush;
    }

    @Override
    protected View onCreateView(int position) {
        Log.d("TAGGGG", "onCreateView is called!");
        if (null == mLayoutInflater) {
            mLayoutInflater = LayoutInflater.from(mContext);
        }
        View view = mLayoutInflater.inflate(R.layout.medal_detail_item, null);
        ImageView imageView = (ImageView) view.findViewById(R.id.activity_medal_info_icon);
        ImageView imageLight = (ImageView) view.findViewById(R.id.activity_medal_info_light_view);

        MedalDTO medalDTO = getItem(position);

        if (medalDTO.getStatus() < 2) {
            if (!TextUtils.isEmpty(medalDTO.getUnLightUrl()))
                Picasso.with(mContext).load(medalDTO.getUnLightUrl()).fit().centerCrop().into(imageView);
            imageLight.setVisibility(View.GONE);
        } else {
            if (!TextUtils.isEmpty(medalDTO.getLightUrl()))
                Picasso.with(mContext).load(medalDTO.getLightUrl()).fit().centerCrop().into(imageView);
            imageLight.setVisibility(View.VISIBLE);
        }

        if (isFromPush) {
            ViewStub viewStub = (ViewStub) view.findViewById(R.id.activity_medal_info_viewStub_push_item);
            viewStub.inflate();
            //推送展示点亮详情
            TextView textViewName = (TextView) view.findViewById(R.id.activity_medal_info_push_medal_name);
            TextView textViewAdditional = (TextView) view.findViewById(R.id.activity_medal_info_push_additional_get);
            Button button = (Button) view.findViewById(R.id.activity_medal_info_push_bottom_btn);

            textViewName.setText(String.format(medalDTO.getName(), ""));
            String str = mContext.getString(R.string.str_additional_get);
            textViewAdditional.setText(str + medalDTO.getGiftName());
            button.setTag(medalDTO);

            if (medalDTO.getGiftId() > 0 && medalDTO.getActivityId() > 0) {
                if (medalDTO.getStatus() == 3) {
                    button.setText(R.string.str_label_has_received_award);
                    button.setOnClickListener(null);
                } else {
                    button.setText(R.string.str_go_to_get_prize);
                    button.setOnClickListener(this);
                }
                textViewAdditional.setVisibility(View.VISIBLE);
            } else {
                button.setText(R.string.activity_alert_dialog_text_ok);
                textViewAdditional.setVisibility(View.INVISIBLE);
            }

        } else {
            ViewStub viewStub = (ViewStub) view.findViewById(R.id.activity_medal_info_viewStub_item);
            viewStub.inflate();
            //正常勋章详情
            TextView textViewCount = (TextView) view.findViewById(R.id.activity_medal_info_light_count);
            TextView textViewStatus = (TextView) view.findViewById(R.id.activity_medal_info_status);
            TextView textViewTitle = (TextView) view.findViewById(R.id.activity_medal_info_title);
            TextView textViewDetail = (TextView) view.findViewById(R.id.activity_medal_info_detail);

            if (medalDTO.getStatus() < 2) {
                textViewStatus.setVisibility(View.VISIBLE);
                textViewCount.setText(String.format(mContext.getString(R.string.label_medal_unlight_count_msg),
                        medalDTO.getTotalLight()));
            } else {
                textViewStatus.setVisibility(View.INVISIBLE);
                textViewCount.setText(String.format(mContext.getString(R.string.label_medal_light_count_msg),
                        medalDTO.getRank()));
            }
            textViewTitle.setText(medalDTO.getName());
            textViewDetail.setText(medalDTO.getDetail());
        }

        alphaAnim(imageView);
        anim(imageView);
        return view;
    }

    private void alphaAnim(View view) {
        AlphaAnimation alphaAnimation1 = new AlphaAnimation(0.3f, 1.0f);
        alphaAnimation1.setDuration(500);
        alphaAnimation1.setRepeatCount(10);
        alphaAnimation1.setRepeatMode(Animation.REVERSE);
        view.setAnimation(alphaAnimation1);
        alphaAnimation1.start();
    }

    private void anim(View view) {
        Animation ani = AnimationUtils.loadAnimation(mContext, R.anim.medal_info_anim);
        view.startAnimation(ani);
        ani.start();
    }

    @Override
    public void onClick(View v) {
        if (null != this.onMedalButtonClickListener) {
            this.onMedalButtonClickListener.onMedalButtonClick((MedalDTO) v.getTag());
        }
    }

    public void setOnMedalButtonClickListener(OnMedalButtonClickListener onMedalButtonClickListener) {
        this.onMedalButtonClickListener = onMedalButtonClickListener;
    }

    public interface OnMedalButtonClickListener {
        void onMedalButtonClick(MedalDTO medalDTO);
    }
}
