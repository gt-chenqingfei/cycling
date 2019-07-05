package com.beastbikes.framework.ui.android.lib.pulltorefresh;

import java.io.Serializable;
import java.util.ArrayList;

public interface Groupable<C> extends Serializable {

    String getTitle();

    ArrayList<C> getChildren();

    void addChild(C child);

    void addChildren(ArrayList<C> children);

}
