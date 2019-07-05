package com.beastbikes.framework.ui.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.widget.TextView;

import com.beastbikes.framework.ui.android.R;

public class PatternTextView extends TextView {

    public PatternTextView(Context context) {
        this(context, null);
    }

    public PatternTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public PatternTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        final TypedArray ta = context.obtainStyledAttributes(attrs,
                R.styleable.PatternTextView);
        final CharSequence pattern = ta
                .getText(R.styleable.PatternTextView_pattern);
        this.setText(String.format(String.valueOf(pattern), getText()));
        ta.recycle();
    }

}
