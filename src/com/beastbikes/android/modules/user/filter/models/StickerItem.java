package com.beastbikes.android.modules.user.filter.models;

import android.graphics.drawable.Drawable;

public class StickerItem {

    private String name;
    private Drawable drawable;
    private boolean locked;
    private boolean needDraw;
    private int[] margins;

    public StickerItem() {
    }

    public StickerItem(boolean locked, String name, Drawable drawable) {
        this.locked = locked;
        this.name = name;
        this.drawable = drawable;
    }

    public boolean isLocked() {
        return locked;
    }

    public void setLocked(boolean locked) {
        this.locked = locked;
    }

    public int[] getMargins() {
        return margins;
    }

    public void setMargins(int[] margins) {
        this.margins = margins;
    }

    public boolean isNeedDraw() {
        return needDraw;
    }

    public void setNeedDraw(boolean needDraw) {
        this.needDraw = needDraw;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Drawable getDrawable() {
        return drawable;
    }

    public void setDrawable(Drawable drawable) {
        this.drawable = drawable;
    }

}
