package com.beastbikes.android.main.tutorial;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationSet;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;

public abstract class TutorialPage extends FrameLayout {

    public TutorialPage(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public TutorialPage(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TutorialPage(Context context) {
        this(context, null);
    }

    public abstract int getTutorialDescriptionId();

    public void onEnterPage() {

        final View desc = findViewById(getTutorialDescriptionId());
        AnimationSet asDesc = new AnimationSet(getContext(), null);
        asDesc.addAnimation(new AlphaAnimation(0f, 1f));
        asDesc.addAnimation(new TranslateAnimation(
                Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f,
                Animation.RELATIVE_TO_SELF, 0.0f));
        asDesc.setAnimationListener(new AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                desc.setVisibility(View.VISIBLE);
            }

            @Override
            public void onAnimationRepeat(Animation animation) {
            }

            @Override
            public void onAnimationEnd(Animation animation) {
            }
        });
        asDesc.setDuration(300);
        asDesc.setStartOffset(300);
        desc.startAnimation(asDesc);
    }

    public void onLeavePage() {
        findViewById(getTutorialDescriptionId()).setVisibility(View.INVISIBLE);
    }

}
