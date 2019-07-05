package com.beastbikes.framework.android.text.style;

import android.text.style.ClickableSpan;

public abstract class HyperLinkSpan extends ClickableSpan {

    private final String url;

    public HyperLinkSpan(final String url) {
        this.url = url;
    }

    public String getUrl() {
        return url;
    }

}
