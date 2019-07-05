package com.beastbikes.android.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

import com.beastbikes.android.R;

/**
 * Created by icedan on 16/1/5.
 */
public class NumberTextView extends TextView {

    private Context context;
    private String typeFaceName;

    public NumberTextView(Context context) {
        super(context);
        this.context = context;
    }

    public NumberTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.context = context;
        init(context, attrs);
    }

    public NumberTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        this.context = context;
        this.init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        Typeface defaultType = Typeface.createFromAsset(context.getAssets(), "fonts/BebasNeue.otf");
        this.setTypeface(defaultType);
        TypedArray attributes = context.obtainStyledAttributes(attrs, R.styleable.NumberTextView);
        this.typeFaceName = attributes.getString(R.styleable.NumberTextView_typefaceName);
        if (this.typeFaceName != null && !"".equals(this.typeFaceName)) {
            Typeface typeface = Typeface.createFromAsset(context.getAssets(),
                    "fonts/" + this.typeFaceName + ".otf");
            this.setTypeface(typeface);
        }
    }

    public void setTypefaceName(String typefaceName) {
        this.typeFaceName = typefaceName;
        Typeface typeface = Typeface.createFromAsset(context.getAssets(), "fonts/" + typefaceName + ".otf");
        this.setTypeface(typeface);
    }

    public String getTypeFaceName() {
        return this.typeFaceName;
    }

}
