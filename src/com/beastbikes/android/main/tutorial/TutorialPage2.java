package com.beastbikes.android.main.tutorial;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.beastbikes.android.R;

public class TutorialPage2 extends TutorialPage {

    public TutorialPage2(Context context) {
        this(context, null);
    }

    public TutorialPage2(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TutorialPage2(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final View page = inflate(context, R.layout.tutorial_page2, null);
        this.addView(page);
    }

    @Override
    public int getTutorialDescriptionId() {
        return R.id.tutorial_page_desc_2;
    }

}
