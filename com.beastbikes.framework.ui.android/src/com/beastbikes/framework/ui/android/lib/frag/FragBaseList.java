package com.beastbikes.framework.ui.android.lib.frag;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;

import com.beastbikes.framework.ui.android.lib.pulltorefresh.OnFlingListener;


public abstract class FragBaseList<K, D, V extends AbsListView> extends
        FragPullAbsList<K, D, V> implements OnFlingListener {

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        pullView.setOnFlingListener(this);

        /**
         * 动画效果暂时没有使用
         */
//        AnimationSet set = new AnimationSet(true);
//
//        Animation animation = new AlphaAnimation(0.0f, 1.0f);
//        animation.setDuration(100);
//        set.addAnimation(animation);
//
//        animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f,
//                Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF,
//                -1.0f, Animation.RELATIVE_TO_SELF, 0.0f);
//        animation.setDuration(150);
//        set.addAnimation(animation);
//
//        LayoutAnimationController controller = new LayoutAnimationController(
//                set, 0.5f);
//        internalView.setLayoutAnimation(controller);

        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public boolean onFlingToLeft(float fromX, float fromY, float toX, float toY) {
        return false;
    }

    @Override
    public boolean onFlingToRight(float fromX, float fromY, float toX, float toY) {
        if (isAdded()) {
            if(getActivity() != null) {
                getActivity().finish();
            }
        }
        return false;
    }

    @Override
    public boolean onTouchedAfterFlinged(float x, float y) {
        return false;
    }

}
