package com.beastbikes.android.main.tutorial;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import com.beastbikes.android.R;

public class TutorialPage4 extends TutorialPage {

    private final View page;

    public TutorialPage4(Context context) {
        this(context, null);
    }

    public TutorialPage4(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public TutorialPage4(final Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        this.page = inflate(context, R.layout.tutorial_page4, null);
        this.addView(this.page);
    }

    @Override
    public int getTutorialDescriptionId() {
        return R.id.tutorial_page_desc_4;
    }

}
